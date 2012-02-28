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

import java.io.IOException;

import uk.ac.imperial.presage2.core.cli.Presage2CLI;

/**
 * <p>
 * Runs a simulation on a remote machine via ssh. Note this implementation may
 * not work in all situations. We assume the following:
 * </p>
 * <ul>
 * <li><code>ssh</code> and <code>rsync</code> commands available on the path of
 * the local machine (and usable from a {@link Process})</li>
 * <li>Passwordless login from the above commands to the remote machine.</li>
 * <li>Write access to the specified working directory in order to transfer
 * classpath dependencies.</li>
 * <li><code>java</code> command available on the path of the remote machine
 * (and usable from <code>ssh</code>).</li>
 * <li>The remote machine has access to the database via the settings specified
 * in <code>db.properties</code></li>
 * </ul>
 * 
 * @author Sam Macbeth
 * 
 */
public class RemoteSubProcessExecutor extends SubProcessExecutor implements
		SimulationExecutor {

	final String remoteHost;
	final String remoteWorkingDir;
	final String remoteUser;
	String remoteClasspath = "";
	boolean initialised = false;

	public RemoteSubProcessExecutor(int mAX_PROCESSES, String remoteUser,
			String remoteHost, String remoteWorkingDir) {
		super(mAX_PROCESSES);
		this.remoteUser = remoteUser;
		this.remoteHost = remoteHost;
		this.remoteWorkingDir = remoteWorkingDir;
	}

	protected void initialise() throws IOException {
		if (initialised)
			return;

		String classpath = this.getClasspath();
		String[] paths = classpath.split(System.getProperty("path.separator",
				":"));

		logger.debug("Initialising remote server: Transferring classpath deps.");
		// mkdir
		try {
			ProcessBuilder builder = new ProcessBuilder("ssh", remoteUser + "@"
					+ remoteHost, "mkdir -p " + remoteWorkingDir);
			logger.debug(builder.command());
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamGobbler gobbler = new StreamGobbler(process.getInputStream(),
					System.out);
			gobbler.start();
			process.waitFor();
			if (process.exitValue() != 0) {
				throw new IOException(
						"Could create working directory on remote server.");
			}
		} catch (InterruptedException e) {
		}

		// transfer dependencies
		// reverse iteration so that classes folders at the beginning of the
		// classpath get precedence.
		for (int i = paths.length - 1; i >= 0; i--) {
			ProcessBuilder builder = new ProcessBuilder("rsync", "-avz", "-e",
					"ssh", paths[i], remoteUser + "@" + remoteHost + ":"
							+ remoteWorkingDir + "/deps/");
			logger.debug(builder.command());
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamGobbler gobbler = new StreamGobbler(process.getInputStream());
			gobbler.start();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
			}
			if (process.exitValue() != 0) {
				throw new IOException(
						"Could not copy all classpath components to remote server.");
			}
		}
		remoteClasspath = remoteWorkingDir + "/deps/classes/:"
				+ remoteWorkingDir + "/deps/*";
		initialised = true;
	}

	@Override
	public synchronized void run(long simId)
			throws InsufficientResourcesException {
		if (!initialised) {
			try {
				initialise();
			} catch (Exception e) {
				logger.warn("Initialise threw", e);
				throw new InsufficientResourcesException(e);
			}
		}
		super.run(simId);
	}

	@Override
	protected ProcessBuilder createProcess(long simId)
			throws InsufficientResourcesException {
		if (!initialised || remoteClasspath.length() == 0) {
			throw new InsufficientResourcesException(
					"Executor not properly initialised.");
		}
		// we assume java is on PATH
		String className = Presage2CLI.class.getCanonicalName();
		String command = "java -cp \"" + remoteClasspath + "\" " + className
				+ " run " + simId;
		ProcessBuilder builder = new ProcessBuilder("ssh", remoteUser + "@"
				+ remoteHost, "" + command + "");
		logger.debug(builder.command());
		return builder;
	}

	@Override
	public String toString() {
		return "RemoteSubProcessExecutor @ " + this.remoteHost;
	}
}
