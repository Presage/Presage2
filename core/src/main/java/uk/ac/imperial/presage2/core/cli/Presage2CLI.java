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
package uk.ac.imperial.presage2.core.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.cli.FormattedSimulation.Column;
import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;

import com.google.inject.Guice;
import com.google.inject.Injector;

public final class Presage2CLI {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	private @interface Command {
		String name();

		String description();
	}

	private static Map<String, Method> commands = null;

	private static OptionGroup getCommands() {
		OptionGroup cmdGroup = new OptionGroup();
		for (String cmd : commands.keySet()) {
			cmdGroup.addOption(new Option(cmd, commands.get(cmd).getAnnotation(Command.class)
					.description()));
		}
		return cmdGroup;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// inspect available commands
		commands = new HashMap<String, Method>();
		for (Method m : Presage2CLI.class.getDeclaredMethods()) {
			Command c = m.getAnnotation(Command.class);
			if (c != null) {
				commands.put(c.name(), m);
			}
		}

		// first arg must be a command
		if (args.length == 0 || !commands.containsKey(args[0])) {
			HelpFormatter formatter = new HelpFormatter();
			Options cmdOpts = new Options();

			cmdOpts.addOptionGroup(Presage2CLI.getCommands());

			formatter.setOptPrefix("");
			formatter.printHelp("presage2-cli <command> [OPTIONS]", cmdOpts);
			System.exit(1);
		}

		// attempt to invoke command
		try {
			commands.get(args[0]).invoke(null, (Object) args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private static Injector injector;
	private static DatabaseService database;
	private static StorageService storage;

	private static StorageService getDatabase() {
		if (storage != null)
			return storage;

		DatabaseModule db = DatabaseModule.load();
		if (db != null) {
			injector = Guice.createInjector(db);
			database = injector.getInstance(DatabaseService.class);
			storage = injector.getInstance(StorageService.class);
			try {
				database.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return storage;
		}
		throw new RuntimeException("Couldn't get a database connection.");
	}

	private static void stopDatabase() {
		database.stop();
	}

	/**
	 * List all simulations in the datastore.
	 * 
	 * @param args
	 */
	@Command(name = "list", description = "Lists all simulations")
	static void list(String[] args) {

		Options options = new Options();
		options.addOption("h", "help", false, "Show help");
		options.addOption("enablelog", false, "Enable logging.");
		// options.addOption("p", "showparams", false,
		// "Show simulation parameters.");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			new HelpFormatter().printHelp("presage2cli list", options, true);
			return;
		}
		if (cmd.hasOption("h")) {
			new HelpFormatter().printHelp("presage2cli list", options, true);
			return;
		}
		if (!cmd.hasOption("enablelog")) {
			Logger.getRootLogger().setLevel(Level.OFF);
		}

		// get database
		StorageService storage = getDatabase();

		// field formatting
		Column[] fields = { Column.ID, Column.Name, Column.ClassName, Column.State, Column.SimCycle };

		int[] colSizes = new int[fields.length];
		for (int i = 0; i < fields.length; i++) {
			colSizes[i] = fields[i].name().length();
		}

		List<FormattedSimulation> sims = new LinkedList<FormattedSimulation>();
		for (Long id : storage.getSimulations()) {
			FormattedSimulation sim = new FormattedSimulation(storage.getSimulationById(id));
			sims.add(sim);
			for (int i = 0; i < fields.length; i++) {
				colSizes[i] = Math.max(colSizes[i], sim.getField(fields[i]).length());
			}
		}

		for (int i = 0; i < fields.length; i++) {
			System.out.printf("%-" + colSizes[i] + "s	", fields[i].name());
		}
		System.out.println();
		for (int i = 0; i < fields.length; i++) {
			String tHead = "";
			for (int j = 0; j < colSizes[i]; j++) {
				tHead += "-";
			}
			System.out.print(tHead + "	");
		}
		System.out.println();

		for (FormattedSimulation sim : sims) {
			for (int i = 0; i < fields.length; i++) {
				System.out.printf("%-" + colSizes[i] + "s	", sim.getField(fields[i]));
			}
			System.out.println();
		}

		stopDatabase();
	}

	@SuppressWarnings("static-access")
	@Command(description = "Add a new simulation.", name = "add")
	static void add(String[] args) {

		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("name").hasArg()
				.withDescription("Name of the simulation to add.").isRequired().create("name"));
		options.addOption(OptionBuilder
				.withArgName("classname")
				.hasArg()
				.withDescription(
						"Name of the RunnableSimulation class to execute for this simulation.")
				.isRequired().create("classname"));
		options.addOption(OptionBuilder.withArgName("finishTime").hasArg()
				.withDescription("Simulation cycle to stop execution at.").isRequired()
				.create("finish"));
		options.addOption(OptionBuilder.withArgName("parameter=value").hasArgs()
				.withValueSeparator().withDescription("Parameters to supply to the simulation.")
				.create("P"));
		options.addOption("h", "help", false, "Show help");
		options.addOption("enablelog", false, "Enable logging.");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("presage2cli add", options, true);
			return;
		}
		if (cmd.hasOption("h")) {
			new HelpFormatter().printHelp("presage2cli add", options, true);
			return;
		}
		if (!cmd.hasOption("enablelog")) {
			Logger.getRootLogger().setLevel(Level.OFF);
		}

		try {
			Integer.parseInt(cmd.getOptionValue("finish"));
		} catch (NumberFormatException e) {
			System.err.println("finishTime must be an integer.");
			return;
		}

		StorageService storage = getDatabase();

		PersistentSimulation sim = storage.createSimulation(cmd.getOptionValue("name"),
				cmd.getOptionValue("classname"), "NOT STARTED",
				Integer.parseInt(cmd.getOptionValue("finish")));

		sim.addParameter("finishTime", cmd.getOptionValue("finish"));
		Properties params = cmd.getOptionProperties("P");
		for (Object param : params.keySet()) {
			sim.addParameter(param.toString(), params.get(param).toString());
		}

		System.out.println("Added simulation ID: " + sim.getID());

		stopDatabase();
	}

	@Command(description = "Run a simulation.", name = "run")
	static void run(String[] args) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, InstantiationException, IllegalAccessException {
		int threads = 4;

		Options options = new Options();
		options.addOption("t", "threads", true, "Number of threads for the simulatior (default "
				+ threads + ").");
		options.addOption("h", "help", false, "Show help");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp("presage2cli run <ID>", options, true);
			return;
		}
		if (cmd.hasOption("h") || args.length < 2) {
			new HelpFormatter().printHelp("presage2cli run <ID>", options, true);
			return;
		}
		if (cmd.hasOption("t")) {
			try {
				threads = Integer.parseInt(cmd.getOptionValue("t"));
			} catch (NumberFormatException e) {
				System.err.println("Thread no. should be in integer.");
				return;
			}
		}

		long simulationID;
		try {
			simulationID = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Simulation ID should be an integer.");
			return;
		}

		StorageService storage = getDatabase();
		DatabaseService db = database;

		RunnableSimulation.runSimulationID(db, storage, simulationID, threads);

		stopDatabase();
	}
}
