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

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * Manages a set of {@link SimulationExecutor}s and a queue of Simulations to be
 * run on them.
 * </p>
 * 
 * <p>
 * Simulations can be added to the queue with {@link #addSimulation(long)} and
 * these will be executed as soon as the executors will allow. Tasks are split
 * equally among the {@link SimulationExecutor}s up to each one's resource limit
 * (as given my {@link SimulationExecutor#maxConcurrent()}.
 * </p>
 * 
 * <p>
 * The {@link ExecutorManager} thread acts as a Consumer and will continue to
 * run until a simulation with ID 0 is passed to {@link #addSimulation(long)}.
 * Once this is detected it will wait for all executors to finish then shutdown
 * the thread.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class ExecutorManager extends Thread {

	private final Logger logger = Logger.getLogger(ExecutorManager.class);
	final BlockingQueue<Long> queue;
	Set<SimulationExecutor> executors = new HashSet<SimulationExecutor>();

	public ExecutorManager() {
		super("ExecutorManager");
		executors.add(new LocalSubProcessExecutor());
		queue = new LinkedBlockingQueue<Long>();
	}

	@Inject
	ExecutorManager(Set<SimulationExecutor> executors) {
		super("ExecutorManager");
		this.executors.addAll(executors);
		queue = new LinkedBlockingQueue<Long>();
	}

	/**
	 * Add a simulation to be run by an executor in the pool.
	 * 
	 * @param simId
	 */
	public void addSimulation(long simId) {
		if (!queue.contains(simId))
			queue.offer(simId);
	}

	@Override
	public void run() {
		int poolSize = 0;
		for (SimulationExecutor exe : executors) {
			poolSize += exe.maxConcurrent();
		}
		int queueSize = queue.size();
		if (queue.contains(0L)) {
			queueSize--;
		}
		logger.info("Starting with " + queueSize
				+ " simulations in the queue and " + executors.size()
				+ " executors. Pool size is " + poolSize);

		// Checks every second and notifies if there are executors available.
		Timer executorChecker = new Timer(true);
		executorChecker.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (ExecutorManager.this) {
					if (getNextExecutor() != null)
						ExecutorManager.this.notifyAll();
				}
			}
		}, 1000, 1000);

		while (true) {
			try {
				// take head of queue / wait for a queue head
				long simId = queue.take();

				// simId of 0 means end of queue, break from thread if we see it
				// and the queue is empty. Otherwise push it to the end of the
				// queue and continue.
				if (simId == 0) {
					if (queue.size() == 0)
						break;
					else {
						if (queue.peek() != 0)
							queue.offer(0L);
						continue;
					}
				}

				// get next executor / wait for executor
				synchronized (this) {
					SimulationExecutor exe;
					while ((exe = getNextExecutor()) == null) {
						logger.info("No Executors available, waiting.");
						wait();
					}

					try {
						logger.info("Submitting simulation " + simId
								+ " to executor: " + exe.toString());
						exe.run(simId);
					} catch (InsufficientResourcesException e) {
						queue.offer(simId);
					}
				}
			} catch (InterruptedException e1) {
			}
		}

		logger.debug("No more simulations to submit, waiting for executors to finish.");
		// wait for executors to finish
		for (SimulationExecutor exe : executors) {
			synchronized (this) {
				while (exe.running() > 0) {
					try {
						logger.debug("Waiting for executor to finish: "
								+ exe.toString());
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		logger.info("ExecutorManager shutting down.");
	}

	/**
	 * Gets the next available {@link SimulationExecutor}. We will select the
	 * least utilised executor with spare capacity.
	 * 
	 * @return {@link SimulationExecutor} which has available capacity.
	 */
	private SimulationExecutor getNextExecutor() {
		SimulationExecutor leastUsed = null;
		for (SimulationExecutor exe : executors) {
			if (exe.running() == 0 && exe.maxConcurrent() > 0) {
				return exe;
			} else if (exe.running() < exe.maxConcurrent()
					&& (leastUsed == null || exe.running() < leastUsed
							.running())) {
				leastUsed = exe;
			}
		}
		return leastUsed;
	}

}
