package uk.ac.imperial.presage2.core.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class ExecutorServiceThreadPool implements ThreadPool {

	private final Logger logger = Logger
			.getLogger(ExecutorServiceThreadPool.class);

	private final ExecutorService threadPool;

	private final Map<WaitCondition, Queue<Future<?>>> futures = new HashMap<ThreadPool.WaitCondition, Queue<Future<?>>>();

	private final int threads;

	ExecutorServiceThreadPool(final int threads) {
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
