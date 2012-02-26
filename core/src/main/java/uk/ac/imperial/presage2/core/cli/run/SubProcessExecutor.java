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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.cli.Presage2CLI;

import com.google.inject.Singleton;

/**
 * A {@link SimulationExecutor} which runs each simulation in separate JVM as a
 * sub process.
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class SubProcessExecutor implements SimulationExecutor {

	private final Logger logger = Logger.getLogger(SubProcessExecutor.class);
	final int MAX_PROCESSES;
	List<Process> running;
	Timer processMonitor;

	public SubProcessExecutor() {
		this(1);
	}

	SubProcessExecutor(int mAX_PROCESSES) {
		super();
		MAX_PROCESSES = mAX_PROCESSES;
		this.running = Collections.synchronizedList(new ArrayList<Process>(
				MAX_PROCESSES));
		this.processMonitor = new Timer(true);
		this.processMonitor.schedule(new TimerTask() {
			@Override
			public void run() {
				List<Process> completed = new LinkedList<Process>();
				for (Process p : running) {
					try {
						// check exit value to see if process has exited.
						int val = p.exitValue();
						logger.info("Simulation completed, returned " + val);
						completed.add(p);

					} catch (IllegalThreadStateException e) {
						// process is still running.
					}
				}
				running.removeAll(completed);
			}
		}, 1000, 1000);
	}

	@Override
	public synchronized void run(long simId)
			throws InsufficientResourcesException {
		// don't launch more than maxConcurrent processes.
		if (this.running() >= maxConcurrent())
			throw new InsufficientResourcesException(
					"Max number of concurrent processes, " + maxConcurrent()
							+ " has been reached");

		// set up processbuilder
		// see
		// http://stackoverflow.com/questions/636367/java-executing-a-java-application-in-a-separate-process/723914#723914
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator
				+ "java";
		String classpath = System.getProperty("java.class.path");
		String className = Presage2CLI.class.getCanonicalName();

		// if the system classpath only contains classworlds.jar we must rebuild
		// classpath from this class's classloader.
		if (classpath.split(":").length == 1
				&& classpath.matches(".*classworlds.jar.*")) {
			ClassLoader sysClassLoader = this.getClass().getClassLoader();
			URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
			String separator = System.getProperty("path.separator", ":");
			classpath = "";
			for (int i = 0; i < urls.length; i++) {
				classpath += urls[i].getFile();
				if (i >= urls.length - 1)
					break;
				classpath += separator;
			}
		}

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
				className, "run", Long.toString(simId));
		builder.redirectErrorStream(true);

		// start process and gobble streams
		try {
			logger.info("Starting simulation ID: " + simId
					+ " in a new process.");
			Process process = builder.start();
			StreamGobbler gobbler = new StreamGobbler(process.getInputStream());
			gobbler.start();
			running.add(process);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int running() {
		return this.running.size();
	}

	@Override
	public int maxConcurrent() {
		return MAX_PROCESSES;
	}

	@Override
	public String toString() {
		return "SubProcessExecutor @ localhost";
	}
}
