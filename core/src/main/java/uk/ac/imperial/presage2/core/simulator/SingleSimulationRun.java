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
package uk.ac.imperial.presage2.core.simulator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.event.EventBusModule;

import com.google.inject.AbstractModule;

/**
 * <p>
 * This is the main entry point to run presage simulations. The main function
 * will run the desired simulation from commandline arguments, or alternatively
 * the class can be used from within another program.
 * </p>
 * 
 * <p>
 * The {@link #main(String[])} function takes the following parameters:
 * </p>
 * <code>simulation_class_name simulation_parameter=parameter_value...</code>
 * <p>
 * Where <code>simulation_class_name</code> is the fully qualified name of a
 * class which implements {@link RunnableSimulation} and is visible to this
 * class (i.e. public), and <code>simulation_parameter=parameter_value</code>
 * are key/value pairs for simulation parameters. This pairs should correspond
 * to {@link Parameter} annotations on fields or methods within the
 * {@link RunnableSimulation} we are running. The key is the name assigned to
 * each {@link Parameter} inside the annotations. These fields and methods must
 * be public in order for use to insert the provided values in.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class SingleSimulationRun {

	private final Logger logger = Logger.getLogger(SingleSimulationRun.class);

	/**
	 * The {@link RunnableSimulation} we are running.
	 */
	public final RunnableSimulation sim;

	private Map<String, Field> fieldParameters = new HashMap<String, Field>();

	private Map<String, Method> methodParameters = new HashMap<String, Method>();

	public SingleSimulationRun(RunnableSimulation s) {
		sim = s;
	}

	/**
	 * <p>
	 * Create a new {@link SingleSimulationRun} from a provided string
	 * representing the fully qualified name of a {@link RunnableSimulation} and
	 * an array of parameters to it's constructor.
	 * </p>
	 * <p>
	 * The method will search for an appropriate constructor for the given
	 * parameters.
	 * </p>
	 * 
	 * @param className
	 *            string representing the fully qualified name of a
	 *            {@link RunnableSimulation}
	 * @param ctorParams
	 *            array of parameters to the constructor
	 * @return {@link SingleSimulationRun}
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	final public static SingleSimulationRun newFromClassName(String className,
			Object... ctorParams) throws ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		final Logger logger = Logger.getLogger(SingleSimulationRun.class);

		// Find Class and assert it is a RunnableSimulation
		Class<? extends RunnableSimulation> clazz = null;
		try {
			clazz = Class.forName(className).asSubclass(
					RunnableSimulation.class);
		} catch (ClassNotFoundException e) {
			logger.fatal(className + " is not on the classpath!", e);
			throw e;
		} catch (ClassCastException e) {
			logger.fatal(className + " is not a RunnableSimulation!");
			throw e;
		}

		// find ctor which matches params given.
		Constructor<? extends RunnableSimulation> ctor = null;
		Class<?>[] paramTypes = new Class<?>[ctorParams.length];
		for (int i = 0; i < ctorParams.length; i++) {
			paramTypes[i] = ctorParams[i].getClass();
		}
		try {
			ctor = ObjectFactory.getConstructor(clazz, paramTypes);
		} catch (SecurityException e) {
			logger.fatal("Could not get constructor for " + clazz, e);
			throw (e);
		} catch (NoSuchMethodException e) {
			logger.fatal("Could not find constructor for " + clazz, e);
			throw (e);
		}

		// create RunnableSimulation object
		RunnableSimulation simObj = null;
		try {
			simObj = ctor.newInstance(ctorParams);
		} catch (IllegalArgumentException e) {
			logger.fatal("Failed to create the RunnableSimulation", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.fatal("Failed to create the RunnableSimulation", e);
			throw e;
		} catch (InstantiationException e) {
			logger.fatal("Failed to create the RunnableSimulation", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.fatal("Failed to create the RunnableSimulation", e);
			throw e;
		}

		return new SingleSimulationRun(simObj);
	}

	public Map<String, Class<?>> getParameters() {
		Map<String, Class<?>> parameters = new HashMap<String, Class<?>>();
		parameters.putAll(getParametersFromFields());
		parameters.putAll(getParametersFromMethods());
		if (logger.isDebugEnabled()) {
			logger.debug("Got " + parameters.size()
					+ " parameters in simulation "
					+ this.sim.getClass().getSimpleName());
		}
		return parameters;
	}

	private Map<String, Class<?>> getParametersFromFields() {
		Map<String, Class<?>> parameters = new HashMap<String, Class<?>>();
		for (Field f : this.sim.getClass().getFields()) {
			Parameter param = f.getAnnotation(Parameter.class);

			if (param != null) {
				Class<?> paramType = f.getType();
				parameters.put(param.name(), paramType);
				fieldParameters.put(param.name(), f);
			}
		}
		return parameters;
	}

	private Map<String, Class<?>> getParametersFromMethods() {
		Map<String, Class<?>> parameters = new HashMap<String, Class<?>>();
		for (Method m : this.sim.getClass().getMethods()) {
			Parameter param = m.getAnnotation(Parameter.class);

			if (param != null && m.getParameterTypes().length == 1) {
				Class<?> paramType = m.getParameterTypes()[0];
				parameters.put(param.name(), paramType);
				methodParameters.put(param.name(), m);
			}
		}
		return parameters;
	}

	public void setParameter(String name, String value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (fieldParameters.containsKey(name)) {
			Class<?> type = fieldParameters.get(name).getType();
			if (type == String.class) {
				fieldParameters.get(name).set(this.sim, value);
			} else if (type == Integer.class || type == Integer.TYPE) {
				fieldParameters.get(name).setInt(this.sim,
						Integer.parseInt(value));
			} else if (type == Double.class || type == Double.TYPE) {
				fieldParameters.get(name).setDouble(this.sim,
						Double.parseDouble(value));
			}
		} else if (methodParameters.containsKey(name)) {
			Class<?> type = methodParameters.get(name).getParameterTypes()[0];
			if (type == String.class) {
				methodParameters.get(name).invoke(this.sim, value);
			} else if (type == Integer.class || type == Integer.TYPE) {
				methodParameters.get(name).invoke(this.sim,
						Integer.parseInt(value));
			} else if (type == Double.class || type == Double.TYPE) {
				methodParameters.get(name).invoke(this.sim,
						Double.parseDouble(value));
			}
		}
	}

	final public static void main(String[] args) throws ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {

		if (args.length < 1) {
			System.err.println("No args provided, expected 1.");
			return;
		}

		// Additional modules we want for this simulation run
		Set<AbstractModule> additionalModules = new HashSet<AbstractModule>();
		additionalModules.add(SimulatorModule.singleThreadedSimulator());
		additionalModules.add(new EventBusModule());

		// Create the runnable simulation assuming it's an InjectedSimulation
		SingleSimulationRun sim = newFromClassName(args[0], additionalModules);

		Map<String, Class<?>> parameters = sim.getParameters();

		// check for parameters in args
		Map<String, String> providedParams = new HashMap<String, String>();
		for (int i = 1; i < args.length; i++) {
			if (Pattern.matches("([a-zA-Z0-9_]+)=([a-zA-Z0-9_.])+$", args[i])) {
				String[] pieces = args[i].split("=", 2);
				providedParams.put(pieces[0], pieces[1]);
			}
		}

		// set parameters
		for (Map.Entry<String, Class<?>> entry : parameters.entrySet()) {
			if (!providedParams.containsKey(entry.getKey())) {
				System.err.println("No value provied for " + entry.getKey()
						+ " parameter.");
				return;
			}
			sim.setParameter(entry.getKey(), providedParams.get(entry.getKey()));
		}

		// go!
		sim.sim.load();
		sim.sim.run();

	}

	private static class ObjectFactory {

		@SuppressWarnings("unchecked")
		static <T> Constructor<? extends T> getConstructor(
				final Class<T> clazz, Class<?>... paramTypes)
				throws NoSuchMethodException {

			for (Constructor<?> ctor : clazz.getConstructors()) {
				Class<?>[] ctorParams = ctor.getParameterTypes();

				if (ctorParams.length == paramTypes.length) {
					boolean match = true;
					for (int i = 0; i < ctorParams.length; i++) {
						try {
							paramTypes[i].asSubclass(ctorParams[i]);
						} catch (ClassCastException e) {
							match = false;
							break;
						}
					}
					if (match)
						return (Constructor<? extends T>) ctor;
				}
			}
			throw new NoSuchMethodException(
					"Could not find constructor to match parameters for "
							+ clazz.getSimpleName());
		}

	}

}
