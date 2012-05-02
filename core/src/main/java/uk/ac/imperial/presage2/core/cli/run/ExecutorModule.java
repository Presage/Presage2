/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.core.cli.run;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * This {@link AbstractModule} binds a set of {@link SimulationExecutor}s which
 * can be used by an {@link ExecutorManager}. This module will usually be loaded
 * via {@link #load()} which will create the module from configuration files if
 * present.
 * 
 * @author Sam Macbeth
 * 
 */
public class ExecutorModule extends AbstractModule {

	Set<SimulationExecutor> executorInstances = new HashSet<SimulationExecutor>();
	Set<Class<? extends SimulationExecutor>> executorClasses = new HashSet<Class<? extends SimulationExecutor>>();

	public ExecutorModule() {
		super();
	}

	public ExecutorModule(int localProcesses) {
		super();
		this.addExecutorInstance(new LocalSubProcessExecutor(localProcesses));
	}

	@Override
	protected void configure() {
		Multibinder<SimulationExecutor> executorBinder = Multibinder
				.newSetBinder(binder(), SimulationExecutor.class);
		for (SimulationExecutor exe : executorInstances) {
			executorBinder.addBinding().toInstance(exe);
		}
		for (Class<? extends SimulationExecutor> clazz : executorClasses) {
			executorBinder.addBinding().to(clazz);
		}
	}

	public void addExecutorInstance(SimulationExecutor exec) {
		executorInstances.add(exec);
	}

	public void addExecutor(Class<? extends SimulationExecutor> exeClass) {
		executorClasses.add(exeClass);
	}

	/**
	 * <p>
	 * Load an {@link AbstractModule} which can inject an
	 * {@link ExecutorManager} with the appropriate {@link SimulationExecutor}s
	 * as per provided configuration.
	 * </p>
	 * 
	 * <p>
	 * The executor config can be provided in two ways (in order of precedence):
	 * </p>
	 * <ul>
	 * <li><code>executors.properties</code> file on the classpath. This file
	 * should contain a <code>module</code> key who's value is the fully
	 * qualified name of a class which extends {@link AbstractModule} and has a
	 * public constructor which takes a single {@link Properties} object as an
	 * argument or public no-args constructor. An instance of this class will be
	 * returned.</li>
	 * <li><code>executors.json</code> file on the classpath. This file contains
	 * a specification of the executors to load in JSON format. If this file is
	 * valid we will instantiate each executor defined in the spec and add it to
	 * an {@link ExecutorModule} which will provide the bindings for them.</li>
	 * </ul>
	 * 
	 * <h3>executors.json format</h3>
	 * 
	 * <p>
	 * The <code>executors.json</code> file should contain a JSON object with
	 * the following:
	 * <ul>
	 * <li><code>executors</code> key whose value is an array. Each element of
	 * the array is a JSON object with the following keys:
	 * <ul>
	 * <li><code>class</code>: the fully qualified name of the executor class.</li>
	 * <li><code>args</code>: an array of arguments to pass to a public
	 * constructor of the class.</li>
	 * <li><code>enableLogs</code> (optional): boolean value whether this
	 * executor should save logs to file. Defaults to global value.</li>
	 * <li><code>logDir</code> (optional): string path to save logs to. Defaults
	 * to global value</li>
	 * </ul>
	 * </li>
	 * <li><code>enableLogs</code> (optional): Global value for enableLogs for
	 * each executor. Defaults to false.</li>
	 * <li><code>logDir</code> (optional): Global value for logDir for each
	 * executor. Default values depend on the executor.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * e.g.:
	 * </p>
	 * 
	 * <pre class="prettyprint">
	 * {
	 * 	"executors": [{
	 * 		"class": "my.fully.qualified.Executor",
	 * 		"args": [1, "some string", true]
	 * 	},{
	 * 		...
	 * 	}],
	 * "enableLogs": true
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	public static AbstractModule load() {
		Logger logger = Logger.getLogger(ExecutorModule.class);
		// look for executors.properties
		// This defines an AbstractModule to use instead of this one.
		// We try and load the module class given and return it.
		try {
			Properties execProps = new Properties();
			execProps.load(ExecutorModule.class.getClassLoader()
					.getResourceAsStream("executors.properties"));
			String moduleName = execProps.getProperty("module", "");

			Class<? extends AbstractModule> module = Class.forName(moduleName)
					.asSubclass(AbstractModule.class);
			// look for suitable ctor, either Properties parameter or default
			Constructor<? extends AbstractModule> ctor;
			try {
				ctor = module.getConstructor(Properties.class);
				return ctor.newInstance(execProps);
			} catch (NoSuchMethodException e) {
				ctor = module.getConstructor();
				return ctor.newInstance();
			}
		} catch (Exception e) {
			logger.debug("Could not create module from executors.properties");
		}
		// executors.properties fail, look for executors.json
		// This file defines a set of classes to load with parameters for the
		// constructor.
		// We try to create each defined executor and add it to this
		// ExecutorModule.
		try {
			// get executors.json file and parse to JSON.
			// throws NullPointerException if file doesn't exist, or
			// JSONException if we can't parse the JSON.
			InputStream is = ExecutorModule.class.getClassLoader()
					.getResourceAsStream("executors.json");
			logger.debug("Processing executors from executors.json");
			JSONObject execConf = new JSONObject(new JSONTokener(
					new InputStreamReader(is)));
			// Create our module and look for executor specs under the executors
			// array in the JSON.
			ExecutorModule module = new ExecutorModule();
			JSONArray executors = execConf.getJSONArray("executors");

			// optional global settings
			boolean enableLogs = execConf.optBoolean("enableLogs", false);
			String logDir = execConf.optString("logDir");

			logger.info("Building Executors from executors.json");

			// Try and instantiate an instance of each executor in the spec.
			for (int i = 0; i < executors.length(); i++) {
				try {
					JSONObject executorSpec = executors.getJSONObject(i);
					String executorClass = executorSpec.getString("class");
					JSONArray args = executorSpec.getJSONArray("args");
					Class<? extends SimulationExecutor> clazz = Class.forName(
							executorClass).asSubclass(SimulationExecutor.class);
					// build constructor args.
					// We assume all types are in primitive form where
					// applicable.
					// The only available types are boolean, int, double and
					// String.
					Class<?>[] argTypes = new Class<?>[args.length()];
					Object[] argValues = new Object[args.length()];
					for (int j = 0; j < args.length(); j++) {
						argValues[j] = args.get(j);
						Class<?> type = argValues[j].getClass();
						if (type == Boolean.class)
							type = Boolean.TYPE;
						else if (type == Integer.class)
							type = Integer.TYPE;
						else if (type == Double.class)
							type = Double.TYPE;

						argTypes[j] = type;
					}
					SimulationExecutor exe = clazz.getConstructor(argTypes)
							.newInstance(argValues);
					logger.debug("Adding executor to pool: " + exe.toString());
					module.addExecutorInstance(exe);
					// logging config
					boolean exeEnableLog = executorSpec.optBoolean(
							"enableLogs", enableLogs);
					String exeLogDir = executorSpec.optString("logDir", logDir);
					exe.enableLogs(exeEnableLog);
					if (exeLogDir.length() > 0) {
						exe.setLogsDirectory(exeLogDir);
					}
				} catch (JSONException e) {
					logger.warn("Error parsing executor config", e);
				} catch (ClassNotFoundException e) {
					logger.warn("Unknown executor class in config", e);
				} catch (IllegalArgumentException e) {
					logger.warn("Illegal arguments for executor ctor", e);
				} catch (NoSuchMethodException e) {
					logger.warn(
							"No matching public ctor for args in executor config",
							e);
				} catch (Exception e) {
					logger.warn("Could not create executor from specification",
							e);
				}
			}
			return module;
		} catch (JSONException e) {
			logger.debug("Could not create module from executors.json");
		} catch (NullPointerException e) {
			logger.debug("Could not open executors.json");
		}

		// no executor config, use a default config: 1 local sub process
		// executor.
		logger.info("Using default ExecutorModule.");
		return new ExecutorModule(1);
	}
}
