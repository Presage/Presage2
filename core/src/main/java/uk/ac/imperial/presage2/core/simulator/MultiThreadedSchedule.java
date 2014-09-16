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

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class MultiThreadedSchedule implements ScheduleExecutor {

	private final Logger logger = Logger.getLogger(MultiThreadedSchedule.class);

	private final ExecutorService threadPool;

	private final Map<WaitCondition, Queue<Future<?>>> futures = new HashMap<WaitCondition, Queue<Future<?>>>();

	private final int threads;

	MultiThreadedSchedule(final int threads) {
		super();
		this.threads = threads;
		this.threadPool = Executors.newFixedThreadPool(this.threads);
		for (WaitCondition c : WaitCondition.values()) {
			futures.put(c, new ConcurrentLinkedQueue<Future<?>>());
		}
	}

	@Override
	public void submitScheduled(Runnable s, WaitCondition condition) {
		futures.get(condition).add(threadPool.submit(s));
	}

	@Override
	public Future<Boolean> submitScheduledConditional(Callable<Boolean> s,
			WaitCondition condition) {
		Future<Boolean> f = threadPool.submit(s);
		futures.get(condition).add(f);
		return f;
	}

	@Override
	public void submit(Runnable s) {
		threadPool.submit(s);
	}

	@Override
	public void waitFor(WaitCondition condition) {
		while (!futures.get(condition).isEmpty()) {
			try {
				futures.get(condition).poll().get();
			} catch (InterruptedException e) {
				logger.warn("Unexpected InterruptedException", e);
			} catch (ExecutionException e) {
				logger.warn("Unexpected ExecutionException.", e);
			}
		}
	}

	@Override
	public int getThreadCount() {
		return this.threads;
	}

	@Override
	public void shutdown() {
		threadPool.shutdown();
	}

}
