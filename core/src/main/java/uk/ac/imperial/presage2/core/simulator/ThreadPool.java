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

import java.util.concurrent.ExecutorService;

/**
 * Wrapper for a {@link ExecutorService}. The main reason for this being that in
 * the simulation we have to enforce a certain set of tasks' completion before
 * the next set can be added. We provide simple access to a service which
 * provides this guarantees this. However binding of this interface is only an
 * optional requirement of a {@link Simulator}.
 * 
 * @author Sam Macbeth
 * 
 */
public interface ThreadPool {

	/**
	 * Submit a task to be run in the thread pool. We guarantee that this task
	 * will be run within the simulation time slice relevant to it's calling
	 * thread.
	 * 
	 * @param s
	 */
	void submit(Runnable s);

}
