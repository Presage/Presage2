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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.FinalizeEvent;
import uk.ac.imperial.presage2.core.simulator.ParticipantsComplete;
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

	/**
	 * Queue of messages sent by agents which require processing.
	 */
	protected BlockingQueue<Message<?>> toDeliver;
	protected BlockingQueue<Delivery> awaitingDelivery = new LinkedBlockingQueue<Delivery>();

	/**
	 * Whether or not to fire {@link MessageDeliveryEvent} for every message
	 * that is delivered.
	 */
	protected boolean DELIVER_MESSAGE_EVENTS_ENABLED = false;

	static class Delivery {
		NetworkAddress to;
		Message<?> msg;

		Delivery(NetworkAddress to, Message<?> msg) {
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

	protected List<MessageHandler> threads = Collections
			.synchronizedList(new LinkedList<MessageHandler>());

	/**
	 * Maximum number of extra threads we will spawn to process messages with.
	 */
	protected int MAX_THREADS = 3;
	/**
	 * Maximum number of {@link Delivery}s the {@link MessageDeliverer} will
	 * take from the queue at once.
	 */
	protected int DELIVERER_DRAIN_LIMIT = 2000;
	/**
	 * Maximum number of Messages a {@link MessageHandler} will take from the
	 * queue at once.
	 */
	protected int HANDLER_DRAIN_LIMIT = 200;
	/**
	 * Queue size required to cause a {@link MessageHandler} to try and fork a
	 * new thread.
	 */
	protected int HANDLER_FORK_THRESHOLD = 500;
	/**
	 * Threshold at which a {@link MessageHandler} will shut itself down if
	 * there is at least one other handler running.
	 */
	protected int HANDLER_SHUTDOWN_THRESHOLD = 0;

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
		this.toDeliver = new LinkedBlockingQueue<Message<?>>(10000);
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
		spawnMessageHandler();
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
		if (threads.size() == 0) {
			spawnMessageHandler();
		}
		// if there is no threadpool we must join the messagedeliverer to ensure
		// the timestep waits for us.
		if (threadPool == null) {
			Thread t = new Thread(new MessageDeliverer(), "MessageDeliverer");
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
		time.increment();
	}

	private synchronized void spawnMessageHandler() {
		if (this.threads.size() < MAX_THREADS) {
			MessageHandler m = new MessageHandler();
			this.threads.add(m);
			new Thread(m, "MessageHandler-" + this.threads.indexOf(m)).start();
			logger.info("Spawning message handler. Total Handlers: "
					+ threads.size());
		}
	}

	/**
	 * Delivers messages in the awaiting delivery queue. Exits when the queue is
	 * empty.
	 * 
	 * @author Sam Macbeth
	 * 
	 */
	class MessageDeliverer implements Runnable {

		@Override
		public void run() {
			logger.info("MessageDeliverer started");
			while (true) {
				// retrieve a set of up to 100 deliveries.
				List<Delivery> deliveries = new LinkedList<Delivery>();
				awaitingDelivery.drainTo(deliveries, DELIVERER_DRAIN_LIMIT);
				if (deliveries.size() == 0) {
					if (toDeliver.isEmpty())
						break;
					else
						continue;
				}
				logger.debug("MessageDeliverer processing " + deliveries.size()
						+ " message batch");
				// process deliveries.
				for (Delivery d : deliveries) {
					if (devices.containsKey(d.to))
						devices.get(d.to).deliverMessage(d.msg);
				}
			}
			logger.info("MessageDeliverer done: " + awaitingDelivery.size()
					+ " awaiting delivery, " + toDeliver.size()
					+ " to deliver.");
		}

	}

	/**
	 * <p>
	 * Processes messages delivered by agents. Runs until it takes a
	 * {@link ShutdownMessage} message off the queue. We also dynamically
	 * allocate threads as the queue expands, up to a maximum of
	 * <code>MAX_THREADS</code>.
	 * </p>
	 * 
	 * @author Sam Macbeth
	 * 
	 */
	class MessageHandler implements Runnable {

		@Override
		public void run() {
			logger.info("Message Handler spawned");
			while (true) {
				List<Message<?>> messages = new LinkedList<Message<?>>();
				// Get an element from the queue, or block 'till one is there
				Message<?> message;
				try {
					message = toDeliver.take();
				} catch (InterruptedException e1) {
					continue;
				}
				// check for shutdown signal
				if (message instanceof ShutdownMessage)
					break;

				messages.add(message);
				// grab a few extra for good measure.
				toDeliver.drainTo(messages, HANDLER_DRAIN_LIMIT);

				int queueSize = toDeliver.size();
				logger.debug("MessageHandler processing " + messages.size()
						+ " message batch, " + queueSize + " in the queue.");

				// dynamic spawning
				if (queueSize > HANDLER_FORK_THRESHOLD) {
					spawnMessageHandler();
				}

				// process messages
				for (Message<?> m : messages) {
					try {
						handleMessage(m);
					} catch (NetworkException e) {
						logger.warn(e.getMessage(), e);
					}
				}

				// shutdown thread if there is no more demand.
				if (queueSize <= HANDLER_SHUTDOWN_THRESHOLD
						&& threads.size() >= 2) {
					break;
				}
			}
			logger.info("Message Handler shutting down.");
			threads.remove(this);
		}

	}

	class ShutdownMessage extends Message<Object> {

		public ShutdownMessage() {
			super(null, null, time);
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
	public void deliverMessage(Message<?> m) {
		this.toDeliver.offer(m);
	}

	protected void handleMessage(Message<?> m) {
		// check message type
		if (m instanceof UnicastMessage) {
			doUnicast((UnicastMessage<?>) m);
		} else if (m instanceof MulticastMessage) {
			doMulticast((MulticastMessage<?>) m);
		} else if (m instanceof BroadcastMessage) {
			doBroadcast((BroadcastMessage<?>) m);
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
	protected void doUnicast(UnicastMessage<?> m) {
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
	protected void doMulticast(MulticastMessage<?> m) {
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
	protected void doBroadcast(BroadcastMessage<?> m) {
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
	protected void deliverMessageTo(NetworkAddress to, Message<?> m) {
		if (this.eventBus != null && DELIVER_MESSAGE_EVENTS_ENABLED) {
			this.eventBus
					.publish(new MessageDeliveryEvent(time.clone(), m, to));
		}
		this.awaitingDelivery.offer(new Delivery(to, m));
		// this.devices.get(to).deliverMessage(m);
	}

	@EventListener
	public void onParticipantsComplete(ParticipantsComplete e) {
		// tell MessageHandler threads to shutdown
		if (logger.isDebugEnabled()) {
			logger.debug("Received end of time cycle event, telling threads to deliver messages");
		}
		if (threadPool != null) {
			this.threadPool.submitScheduled(new MessageDeliverer(),
					WaitCondition.END_OF_TIME_CYCLE);
		}
	}

	@EventListener
	public void onFinalize(FinalizeEvent e) {
		// send shutdown to all MessageHandlers
		for (int i = 0; i < threads.size(); i++) {
			toDeliver.offer(new ShutdownMessage());
		}
	}

}
