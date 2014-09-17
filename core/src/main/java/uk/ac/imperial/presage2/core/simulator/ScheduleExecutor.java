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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Wrapper for an {@link ExecutorService}. Allows for the controller to wait for
 * completion of tasks according to specified conditions.
 * 
 * @author Sam Macbeth
 * 
 */
public interface ScheduleExecutor {

	/**
	 * Conditions specifying a simulation schedule over a timestep.
	 * 
	 * @author Sam Macbeth
	 */
	public enum WaitCondition {
		PRE_STEP, STEP, POST_STEP
	}

	/**
	 * Submit a task with no completion condition to wait for.
	 * 
	 * @param s
	 */
	void submit(Runnable s);

	/**
	 * Submit a task with specified {@link WaitCondition}.
	 * 
	 * @param s
	 *            {@link Runnable} task
	 * @param condition
	 */
	void submitScheduled(Runnable s, WaitCondition condition);

	/**
	 * Submit a conditional task and receive a {@link Future} representing its
	 * future return value.
	 * 
	 * @param s
	 *            {@link Callable} task, returning a boolean.
	 * @param condition
	 * @return {@link Future} for this task.
	 */
	Future<Boolean> submitScheduledConditional(Callable<Boolean> s,
			WaitCondition condition);

	/**
	 * Wait for all scheduled tasks for the specified {@link WaitCondition} to
	 * complete.
	 * 
	 * @param condition
	 */
	void waitFor(WaitCondition condition);

	/**
	 * Get number of threads in use by this executor.
	 * 
	 * @return
	 */
	int getThreadCount();

	/**
	 * Shutdown executor.
	 */
	void shutdown();
}
