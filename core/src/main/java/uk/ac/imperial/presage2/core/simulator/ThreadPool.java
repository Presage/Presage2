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
