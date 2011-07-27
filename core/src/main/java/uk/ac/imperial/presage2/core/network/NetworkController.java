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

package uk.ac.imperial.presage2.core.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.EndOfTimeCycle;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.simulator.ThreadPool;
import uk.ac.imperial.presage2.core.simulator.ThreadPool.WaitCondition;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This is a central controller through which all messages go.
 * </p>
 * 
 * <p>
 * Like the environment it ensures agents obey the rules of the system. In this
 * case these are to do with the network, such as transmission ranges etc.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class NetworkController implements NetworkChannel, TimeDriven,
		RequiresRegistration {

	private final Logger logger = Logger.getLogger(NetworkController.class);

	protected Time time;

	protected Queue<Message> toDeliver;
	protected Queue<Delivery> awaitingDelivery = new LinkedList<NetworkController.Delivery>();

	static class Delivery {
		NetworkAddress to;
		Message msg;

		Delivery(NetworkAddress to, Message msg) {
			super();
			this.to = to;
			this.msg = msg;
		}
	}

	/**
	 * Map of devices registered to this controller.
	 */
	protected Map<NetworkAddress, NetworkChannel> devices;

	/**
	 * Access to environment shared state.
	 */
	protected EnvironmentSharedStateAccess environment;

	protected EventBus eventBus = null;

	protected ThreadPool threadPool = null;

	volatile boolean deliver = false;

	protected List<MessageHandler> threads = Collections
			.synchronizedList(new LinkedList<MessageHandler>());
	protected int MAX_THREADS = 1;

	/**
	 * @param time
	 */
	@Inject
	public NetworkController(Time time,
			EnvironmentSharedStateAccess environment, Scenario s) {
		super();
		this.time = time;
		this.environment = environment;
		this.devices = new HashMap<NetworkAddress, NetworkChannel>();
		this.toDeliver = new LinkedList<Message>();
		s.addTimeDriven(this);
	}

	@Inject
	public void setEventBus(EventBus e) {
		this.eventBus = e;
		this.eventBus.subscribe(this);
	}

	@Inject
	public void setThreadPool(ThreadPool pool) {
		threadPool = pool;
		this.MAX_THREADS = threadPool.getThreadCount();
	}

	/**
	 * @see uk.ac.imperial.presage2.core.TimeDriven#incrementTime()
	 */
	@Override
	public void incrementTime() {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Delivering messages for time "
					+ this.time.toString());
		}
		this.deliver = false;

		if (threadPool == null || MAX_THREADS == 1) {
			deliver = true;
			new MessageHandler().run();
		} else {
			spawnMessageHandler();
		}
		synchronized (time) {
			time.increment();
			time.notify();
		}
	}

	private synchronized void spawnMessageHandler() {
		if (this.threads.size() < MAX_THREADS) {
			MessageHandler m = new MessageHandler();
			this.threads.add(m);
			threadPool.submitScheduled(m, WaitCondition.END_OF_TIME_CYCLE);
		}
	}

	class MessageHandler implements Runnable {

		@Override
		public void run() {
			while (true) {
				// if we've been told to shutdown deliver messages too.
				if (deliver) {
					while (true) {
						List<Delivery> deliveries = new LinkedList<Delivery>();
						synchronized (awaitingDelivery) {
							for (int i = 0; i < 20; i++) {
								Delivery d = awaitingDelivery.poll();
								if (d == null)
									break;
								deliveries.add(d);
							}
							if (deliveries.size() == 0) {
								break;
							}
						}
						for (Delivery d : deliveries) {
							if (logger.isTraceEnabled()) {
								logger.trace("Delivering " + d.msg + " to "
										+ d.to);
							}
							if (devices.containsKey(d.to))
								devices.get(d.to).deliverMessage(d.msg);
						}
					}
				}

				// process toDeliver queue
				List<Message> messages = new LinkedList<Message>();
				boolean spawn = false;
				synchronized (toDeliver) {
					for (int i = 0; i < 20; i++) {
						Message m = toDeliver.poll();
						if (m == null)
							break;
						messages.add(m);
					}
					if (messages.size() == 0) {
						if (deliver) {
							break;
						}
						try {
							if (logger.isTraceEnabled()) {
								logger.trace("No messages to deliver, waiting for more.");
							}
							toDeliver.wait();
						} catch (InterruptedException e) {
						}
						continue;
					} else if (threads.size() < MAX_THREADS
							&& toDeliver.size() > 20) {
						spawn = true;
					}
				}
				if (spawn)
					spawnMessageHandler();

				for (Message m : messages) {
					try {
						if (logger.isTraceEnabled()) {
							logger.trace("Handing message " + m);
						}
						handleMessage(m);
					} catch (NetworkException e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
			// remove self before shutdown
			if (logger.isDebugEnabled()) {
				logger.debug("Thread shutting down.");
			}
			threads.remove(this);
		}

	}

	/**
	 * <p>
	 * Invoked by a NetworkConnector when it wishes to send a message.
	 * </p>
	 * <p>
	 * In this implementation we deliver at the end of time cycle, therefore
	 * this function just adds the message to the delivery queue
	 * </p>
	 * 
	 * @see uk.ac.imperial.presage2.core.network.NetworkChannel#deliverMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public void deliverMessage(Message m) {
		synchronized (this.toDeliver) {
			this.toDeliver.add(m);
			this.toDeliver.notifyAll();
		}
	}

	protected void handleMessage(Message m) {
		// check message type
		if (m instanceof UnicastMessage) {
			doUnicast((UnicastMessage) m);
		} else if (m instanceof MulticastMessage) {
			doMulticast((MulticastMessage) m);
		} else if (m instanceof BroadcastMessage) {
			doBroadcast((BroadcastMessage) m);
		} else if (m instanceof Ping) {
			// we do not constrain messages, so give them all registered network
			// addresses
			this.devices.get(m.getFrom()).deliverMessage(getPong((Ping) m));
		} else {
			throw new UnknownMessageTypeException(m);
		}
	}

	/**
	 * Send a unicast message
	 * 
	 * @param m
	 * @throws NetworkException
	 */
	protected void doUnicast(UnicastMessage m) {
		try {
			this.deliverMessageTo(m.getTo(), m);
			if (this.logger.isDebugEnabled()) {
				this.logger
						.debug("Dispatched unicast message: " + m.toString());
			}
		} catch (NullPointerException e) {
			throw new UnreachableRecipientException(m, m.getTo(), e);
		}
	}

	/**
	 * Send a multicast message
	 * 
	 * @param m
	 */
	protected void doMulticast(MulticastMessage m) {
		final List<NetworkAddress> recipients = m.getTo();
		final List<NetworkAddress> unreachable = new LinkedList<NetworkAddress>();
		for (NetworkAddress to : recipients) {
			try {
				this.deliverMessageTo(to, m);
			} catch (NullPointerException e) {
				unreachable.add(to);
			}
		}
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Sent multicast message: " + m.toString());
		}
		if (unreachable.size() > 0) {
			throw new UnreachableRecipientException(m, unreachable);
		}
	}

	/**
	 * Send a broadcast message
	 * 
	 * @param m
	 */
	protected void doBroadcast(BroadcastMessage m) {
		for (NetworkAddress to : this.devices.keySet()) {
			// deliver to all but sender
			if (m.getFrom() != to)
				this.deliverMessageTo(to, m);
		}
		this.logger.debug("Sent broadcast message: " + m.toString());
	}

	/**
	 * Return a {@link Pong} for the given {@link Ping}. This is a message
	 * containing the set of {@link NetworkAddress}es of devices we're connected
	 * with.
	 * 
	 * @param p
	 * @return {@link Pong}
	 */
	protected Pong getPong(Ping p) {
		return new Pong(time.clone(), new HashSet<NetworkAddress>(
				this.devices.keySet()));
	}

	/**
	 * Register a network device with this NetworkController
	 * 
	 * @param req
	 * @throws NetworkException
	 *             If the request is null, or one of the request's parameters is
	 *             null (Address or link).
	 */
	synchronized public void register(NetworkRegistrationRequest req) {
		// defensive programming
		if (req == null || req.getAddress() == null || req.getLink() == null) {
			throw new NullPointerException(
					"NetworkRegistrationRequest null or containing null parameters");
		}

		this.devices.put(req.getAddress(), req.getLink());
	}

	/**
	 * Deliver a message m to recipient to.
	 * 
	 * @param to
	 * @param m
	 */
	protected void deliverMessageTo(NetworkAddress to, Message m) {
		if (this.eventBus != null) {
			this.eventBus
					.publish(new MessageDeliveryEvent(time.clone(), m, to));
		}
		synchronized (this.awaitingDelivery) {
			if (logger.isTraceEnabled()) {
				logger.trace("Adding message for delivery" + m + " to " + to);
			}
			this.awaitingDelivery.add(new Delivery(to, m));
			this.awaitingDelivery.notifyAll();
		}
		// this.devices.get(to).deliverMessage(m);
	}

	@EventListener
	public void onEndOfTimeCycle(EndOfTimeCycle e) {
		// make sure incrementTime is executed first
		synchronized (time) {
			while (e.getTime().equals(time)) {
				try {
					time.wait();
				} catch (InterruptedException e1) {
				}
			}
		}

		// tell MessageHandler threads to shutdown
		if (logger.isDebugEnabled()) {
			logger.debug("Received end of time cycle event, telling threads to deliver messages");
		}
		synchronized (this.toDeliver) {
			deliver = true;
			this.toDeliver.notifyAll();
		}
	}

}
