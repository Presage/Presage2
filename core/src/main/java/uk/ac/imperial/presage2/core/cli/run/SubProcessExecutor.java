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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * <p>
 * A {@link SimulationExecutor} which has a set of sub processes which each runs
 * a simulation. This abstract class automates the management of these
 * processes. It calls the {@link #createProcess(long)} function to get a
 * {@link ProcessBuilder} with which to spawn the process to manage. This class
 * can also log output from these processes to files.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class SubProcessExecutor implements SimulationExecutor {

	protected final Logger logger = Logger.getLogger(this.getClass());
	protected final int MAX_PROCESSES;
	protected List<Process> running;
	protected Timer processMonitor;
	boolean saveLogs = false;
	String logsDir = System.getProperty("user.dir", "") + "/logs/";

	protected SubProcessExecutor(int mAX_PROCESSES) {
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
	public boolean enableLogs() {
		return saveLogs;
	}

	@Override
	public void enableLogs(boolean saveLogs) {
		this.saveLogs = saveLogs;
	}

	@Override
	public String getLogsDirectory() {
		return logsDir;
	}

	@Override
	public void setLogsDirectory(String logsDir) {
		this.logsDir = logsDir;
	}

	@Override
	public synchronized void run(long simId)
			throws InsufficientResourcesException {
		// don't launch more than maxConcurrent processes.
		if (this.running() >= maxConcurrent())
			throw new InsufficientResourcesException(
					"Max number of concurrent processes, " + maxConcurrent()
							+ " has been reached");

		ProcessBuilder builder = createProcess(simId);
		builder.redirectErrorStream(true);

		// start process and gobble streams
		try {
			logger.info("Starting simulation ID: " + simId + "");
			Process process = builder.start();

			InputStream is = process.getInputStream();
			OutputStream os = null;
			// optionally pipe process output to a log.
			if (saveLogs) {
				String logPath = logsDir + simId + ".log";
				logger.debug("Logging to: " + logPath);
				try {
					// ensure log dir exists
					File logDir = new File(logsDir);
					if (!logDir.exists()) {
						logDir.mkdirs();
					}

					File logFile = new File(logPath);
					if (!logFile.exists())
						logFile.createNewFile();
					os = new FileOutputStream(logFile, true);
				} catch (IOException e) {
					logger.warn("Unable to create log file: " + logPath, e);
					os = null;
				}
			}
			StreamGobbler gobbler = os == null ? new StreamGobbler(is)
					: new StreamGobbler(is, os);
			gobbler.start();
			running.add(process);
		} catch (IOException e) {
			logger.warn("Error launching process", e);
		}
	}

	/**
	 * Create a {@link ProcessBuilder} which will spawn a {@link Process} to run the given simulation.
	 * @param simId
	 * @return
	 * @throws InsufficientResourcesException
	 */
	protected abstract ProcessBuilder createProcess(long simId)
			throws InsufficientResourcesException;

	@Override
	public int running() {
		return this.running.size();
	}

	@Override
	public int maxConcurrent() {
		return MAX_PROCESSES;
	}

	protected String getClasspath() {
		String classpath = System.getProperty("java.class.path");
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
		return classpath;
	}

}
