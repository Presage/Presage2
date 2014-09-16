/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.simulator.ScheduleExecutor.WaitCondition;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Implements the Presage2 simulation loop. Loads a simulation either from
 * command-line invocation or database id.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class RunnableSimulation implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RunnableSimulation.class);
	private RuntimeScenario scenario;
	private Injector injector;
	Set<AbstractModule> modules = new HashSet<AbstractModule>();
	Set<DeclaredParameter> parameters = new HashSet<DeclaredParameter>();

	SortedSet<Pair<Method, Object>> initialisors = Collections
			.synchronizedSortedSet(new TreeSet<Pair<Method, Object>>(
					new NiceComparator()));
	SortedSet<Pair<Method, Object>> steppers = Collections
			.synchronizedSortedSet(new TreeSet<Pair<Method, Object>>(
					new NiceComparator()));
	Set<Pair<Method, Object>> finishConditions = Collections
			.synchronizedSet(new HashSet<Pair<Method, Object>>());
	Set<Pair<Method, Object>> finalisors = Collections
			.synchronizedSet(new HashSet<Pair<Method, Object>>());

	int threads = 8;
	ScheduleExecutor executor;

	@Inject
	SharedStateStorage stateEngine;
	int t = 0;

	@Parameter(name = "finishTime")
	public int finishTime;

	final public static void main(String[] args)
			throws IllegalArgumentException, IllegalAccessException,
			UndefinedParameterException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException,
			InstantiationException {
		// check args
		if (args.length < 1) {
			logger.error("No args provided, expected 1 or more.");
			return;
		}

		// check for parameters in args
		Map<String, String> providedParams = new HashMap<String, String>();
		for (int i = 1; i < args.length; i++) {
			if (Pattern.matches("([a-zA-Z0-9_]+)=([a-zA-Z0-9_.,])+$", args[i])) {
				String[] pieces = args[i].split("=", 2);
				providedParams.put(pieces[0], pieces[1]);
			}
		}
		RunnableSimulation run = newFromClassname(args[0]);
		run.loadParameters(providedParams);
		run.run();
	}

	final public static void runSimulationID(long simID, int threads)
			throws Exception {
		DatabaseModule db = DatabaseModule.load();
		Injector injector = Guice.createInjector(db);

		DatabaseService database = injector.getInstance(DatabaseService.class);
		StorageService storage = injector.getInstance(StorageService.class);
		database.start();
		// get PersistentSimulation
		PersistentSimulation sim = storage.getSimulationById(simID);
		if (sim == null) {
			database.stop();
			throw new RuntimeException("Simulation with ID " + simID
					+ " not found in storage. Aborting.");
		}
		if (!sim.getState().equalsIgnoreCase("NOT STARTED")
				&& !sim.getState().equalsIgnoreCase("AUTO START")) {
			database.stop();
			throw new RuntimeException("Simulation " + simID
					+ " has already been started. Aborting.");
		}

		RunnableSimulation run = newFromClassname(sim.getClassName());
		run.loadParameters(sim.getParameters());
		database.stop();
		run.run();
	}

	public RunnableSimulation() {
	}

	public abstract void initialiseScenario(Scenario scenario,
			Set<AbstractModule> modules);

	public void addModule(AbstractModule module) {
		modules.add(module);
	}

	@Override
	public void run() {
		initialise();
		step();
		finish();
	}

	protected void initialise() {
		logger.info("Generating scenario...");
		scenario = new RuntimeScenario();
		scenario.addObject(this);
		Set<AbstractModule> modules = new HashSet<AbstractModule>();
		initialiseScenario(scenario, modules);
		logger.info("Loading modules...");
		modules.add(new ScenarioModule(scenario, parameters));
		modules.add(DatabaseModule.load()); // database
		modules.remove(null);
		injector = Guice.createInjector(modules);

		logger.info("Wiring components...");
		injector.injectMembers(this);
		scenario.inject = true;

		logger.info("Scanning for schedule functions...");
		Set<Object> functions = new HashSet<Object>(scenario.objects);
		functions.addAll(scenario.agents);
		for (Object o : functions) {
			injector.injectMembers(o);
			findScheduleFunctions(o, initialisors, steppers, finalisors,
					finishConditions);
		}

		logger.info("Starting schedule executor...");
		executor = new MultiThreadedSchedule(threads);

		logger.info("Initialising agents and environment...");
		scenario.initialise = true;
		synchronized (initialisors) {
			for (Pair<Method, Object> init : initialisors) {
				executor.submitScheduled(new TaskRunner(init),
						WaitCondition.PRE_STEP);
			}
			initialisors.clear();
		}
		executor.waitFor(WaitCondition.PRE_STEP);

		stateEngine.incrementTime();
		executor.waitFor(WaitCondition.POST_STEP);
	}

	protected void step() {
		boolean step = true;
		do {
			logger.info("Timestep = " + t);
			// initialise anything new
			synchronized (initialisors) {
				for (Pair<Method, Object> init : initialisors) {
					executor.submitScheduled(new TaskRunner(init),
							WaitCondition.PRE_STEP);
				}
				initialisors.clear();
			}
			executor.waitFor(WaitCondition.PRE_STEP);

			// main step component
			for (Pair<Method, Object> task : steppers) {
				executor.submitScheduled(new TaskRunner(task, t),
						WaitCondition.STEP);
			}
			executor.waitFor(WaitCondition.STEP);

			// state update
			stateEngine.incrementTime();

			// loop conditions
			List<Future<Boolean>> conditions = new LinkedList<Future<Boolean>>();
			for (Pair<Method, Object> task : finishConditions) {
				conditions.add(executor.submitScheduledConditional(
						new ConditionalTask(task, t), WaitCondition.POST_STEP));
			}
			executor.waitFor(WaitCondition.POST_STEP);
			for (Future<Boolean> f : conditions) {
				try {
					if (f.get() == true) {
						step = false;
					}
				} catch (Exception e) {
					logger.warn("Error executing wait condition", e);
				}
			}
			t++;
		} while (step);
	}

	protected void finish() {
		logger.info("Running post-simulation tasks");
		for (Pair<Method, Object> task : finalisors) {
			executor.submitScheduled(new TaskRunner(task),
					WaitCondition.POST_STEP);
		}
		executor.waitFor(WaitCondition.POST_STEP);
	}

	@FinishCondition
	public boolean finishTimeCondition(int t) {
		return t >= finishTime;
	}

	final private void loadParametersFromFields()
			throws IllegalArgumentException, IllegalAccessException {
		for (Field f : this.getClass().getFields()) {
			Parameter param = f.getAnnotation(Parameter.class);
			if (param != null) {
				parameters.add(new DeclaredParameter(param, this, f));
			}
		}
	}

	private void loadParameters(Map<String, String> provided)
			throws IllegalArgumentException, IllegalAccessException,
			UndefinedParameterException {
		// collect parameters from field annotations
		loadParametersFromFields();
		// fill out given parameters
		Set<String> missing = new HashSet<String>();
		Set<String> unknown = new HashSet<String>();
		for (DeclaredParameter p : parameters) {
			if (provided.containsKey(p.name)) {
				p.setValue(provided.get(p.name));
				provided.remove(p.name);
			} else if (!p.optional) {
				missing.add(p.name);
			}
			logger.debug("Parameter: " + p.name + "=" + p.stringValue);
		}
		// check for unknown or missing parameters
		unknown.addAll(provided.keySet());
		String error = "";
		if (missing.size() > 0) {
			error += "Required parameters not specified: " + missing.toString()
					+ "; ";
		}
		if (unknown.size() > 0) {
			error += "Undefined parameters specified: " + unknown.toString()
					+ ";";
		}
		if (error.length() > 0) {
			throw new UndefinedParameterException(error);
		}
	}

	private void findScheduleFunctions(Object o,
			Set<Pair<Method, Object>> initialisors,
			Set<Pair<Method, Object>> steppers,
			Set<Pair<Method, Object>> finalisors,
			Set<Pair<Method, Object>> finishConditions) {
		boolean foundFunction = false;
		for (Method m : o.getClass().getMethods()) {
			for (Annotation a : m.getAnnotations()) {
				if (a.annotationType() == Initialisor.class) {
					if (m.getParameterTypes().length != 0) {
						throw new RuntimeException(
								"Initialisor function cannot take arguments. @Initialisor annotated function "
										+ m.getName() + " takes "
										+ m.getParameterTypes().length);
					}
					initialisors.add(Pair.of(m, o));
					foundFunction = true;
				} else if (a.annotationType() == Step.class) {
					Class<?>[] paramTypes = m.getParameterTypes();
					boolean valid = paramTypes.length == 0;
					valid |= (paramTypes.length == 1 && paramTypes[0] == Integer.TYPE);
					if (!valid) {
						throw new RuntimeException(
								"Step function may only take one integer arugment. @Step annotated function "
										+ m.getName() + " takes "
										+ m.getParameterTypes().length
										+ " of types: "
										+ Arrays.toString(paramTypes));
					}
					steppers.add(Pair.of(m, o));
					foundFunction = true;
				} else if (a.annotationType() == Finalisor.class) {
					if (m.getParameterTypes().length != 0) {
						throw new RuntimeException(
								"Finalisor function cannot take arguments. @Finalisor annotated function "
										+ m.getName() + " takes "
										+ m.getParameterTypes().length);
					}
					finalisors.add(Pair.of(m, o));
					foundFunction = true;
				} else if (a.annotationType() == FinishCondition.class) {
					Class<?>[] paramTypes = m.getParameterTypes();
					boolean valid = paramTypes.length == 0;
					valid |= (paramTypes.length == 1 && paramTypes[0] == Integer.TYPE);
					valid &= m.getReturnType() == Boolean.TYPE;
					if (!valid) {
						throw new RuntimeException(
								"FinishCondition function may only take one integer argument and must return a boolean. "
										+ "@Step annotated function "
										+ m.getName()
										+ " takes "
										+ m.getParameterTypes().length
										+ " of types: "
										+ Arrays.toString(paramTypes)
										+ " and returns " + m.getReturnType());
					}
					finishConditions.add(Pair.of(m, o));
					foundFunction = true;
				}
			}
		}
		if (!foundFunction) {
			logger.warn("No candidate function found in object " + o);
			throw new RuntimeException("No candidate function found in object "
					+ o);
		}
	}

	final private static RunnableSimulation newFromClassname(String className)
			throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, InstantiationException,
			IllegalAccessException {
		// Find Class and assert it is a RunnableSimulation
		Class<? extends RunnableSimulation> clazz = null;
		try {
			clazz = Class.forName(className).asSubclass(
					RunnableSimulation.class);
		} catch (ClassNotFoundException e) {
			logger.fatal(className + " is not on the classpath!", e);
			throw e;
		} catch (ClassCastException e) {
			logger.fatal(className + " is not a Simulation!");
			throw e;
		}
		// Find default (no-args) ctor
		Constructor<? extends RunnableSimulation> ctor;
		try {
			ctor = clazz.getConstructor();
		} catch (SecurityException e) {
			logger.fatal("Could not get constructor for " + clazz, e);
			throw (e);
		} catch (NoSuchMethodException e) {
			logger.fatal("Could not find constructor for " + clazz, e);
			throw (e);
		}
		// Create simulation object
		RunnableSimulation sim = null;
		try {
			sim = ctor.newInstance();
		} catch (IllegalArgumentException e) {
			logger.fatal("Failed to create the Simulation", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.fatal("Failed to create the Simulation", e);
			throw e;
		} catch (InstantiationException e) {
			logger.fatal("Failed to create the Simulation", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.fatal("Failed to create the Simulation", e);
			throw e;
		}
		return sim;
	}

	class RuntimeScenario implements Scenario {

		Set<Object> agents = new HashSet<Object>();
		Set<Object> objects = new HashSet<Object>();
		boolean inject = false;
		boolean initialise = false;

		RuntimeScenario() {
			super();
		}

		public void addTimeDriven(TimeDriven object) {
			objects.add(object);
		}

		public void addEnvironment(TimeDriven object) {
			// Environment handled in simulator.
		}

		public void addAgent(Object o) {
			agents.add(o);
			processObject(o);
		}

		public void addObject(Object o) {
			objects.add(o);
			processObject(o);
		}

		void processObject(Object o) {
			if (inject) {
				injector.injectMembers(o);
				findScheduleFunctions(o, initialisors, steppers, finalisors,
						finishConditions);
			}
		}

	}

	static class TaskRunner implements Runnable {

		final Pair<Method, Object> task;
		final Object[] args;

		TaskRunner(Pair<Method, Object> task, Object... args) {
			super();
			this.task = task;
			if (task.getLeft().getParameterTypes().length == args.length)
				this.args = args;
			else
				this.args = new Object[] {};
		}

		@Override
		public void run() {
			try {
				task.getLeft().invoke(task.getRight(), args);
			} catch (Exception e) {
				throw new RuntimeException("Cannot invoke task method", e);
			}
		}
	}

	static class ConditionalTask implements Callable<Boolean> {

		final Pair<Method, Object> task;
		final Object[] args;

		ConditionalTask(Pair<Method, Object> task, Object... args) {
			super();
			this.task = task;
			if (task.getLeft().getParameterTypes().length == args.length)
				this.args = args;
			else
				this.args = new Object[] {};
		}

		@Override
		public Boolean call() throws Exception {
			return (Boolean) task.getLeft().invoke(task.getRight(), args);
		}

	}

	static class NiceComparator implements Comparator<Pair<Method, Object>> {

		@Override
		public int compare(Pair<Method, Object> o1, Pair<Method, Object> o2) {
			return getNice(o1.getLeft()) - getNice(o2.getLeft());
		}

		int getNice(Method m) {
			Step s = m.getAnnotation(Step.class);
			if (s != null)
				return s.nice();
			Initialisor i = m.getAnnotation(Initialisor.class);
			if (i != null)
				return i.nice();
			return 0;
		}

	}

}
