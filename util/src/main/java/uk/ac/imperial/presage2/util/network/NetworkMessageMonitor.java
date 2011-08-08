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
package uk.ac.imperial.presage2.util.network;

import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.db.GraphDB;
import uk.ac.imperial.presage2.core.db.Transaction;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.network.MessageBlockedEvent;
import uk.ac.imperial.presage2.core.network.MessageDeliveryEvent;
import uk.ac.imperial.presage2.core.plugin.Plugin;
import uk.ac.imperial.presage2.core.simulator.EndOfTimeCycle;

import com.google.inject.Inject;

public class NetworkMessageMonitor implements Plugin {

	private final Logger logger = Logger.getLogger(NetworkMessageMonitor.class);

	private GraphDB db = null;
	private final Time time;

	Queue<MessageDeliveryEvent> deliveryQueue = new LinkedBlockingQueue<MessageDeliveryEvent>();

	@Inject
	public NetworkMessageMonitor(EventBus eb, Time t) {
		super();
		this.time = t;
		eb.subscribe(this);
	}

	@Inject(optional = true)
	public void setStorageService(GraphDB db) {
		this.db = db;
	}

	@EventListener
	public void onMessageDelivery(MessageDeliveryEvent e) {
		deliveryQueue.offer(e);

	}

	@EventListener
	public void onMessageBlocked(MessageBlockedEvent e) {

	}

	@Override
	public void incrementTime() {
		if (db != null) {
			Transaction tx = db.startTransaction();
			try {
				while (deliveryQueue.peek() != null) {
					MessageDeliveryEvent e = deliveryQueue.poll();
					Map<String, Object> parameters = new Hashtable<String, Object>();
					parameters.put("time", time.intValue());
					parameters.put("type", e.getMessage().getType());
					parameters.put("performative", e.getMessage()
							.getPerformative().name());
					parameters.put("class", e.getMessage().getClass()
							.getSimpleName());
					db.getAgent(e.getMessage().getFrom().getId())
							.createRelationshipTo(
									db.getAgent(e.getRecipient().getId()),
									"SENT_MESSAGE", parameters);
				}
				tx.success();
			} finally {
				tx.finish();
			}
		}
		time.increment();
	}

	@Override
	public void initialise() {
	}

	@Override
	public void execute() {
	}

	@Override
	public void onSimulationComplete() {

	}

}
