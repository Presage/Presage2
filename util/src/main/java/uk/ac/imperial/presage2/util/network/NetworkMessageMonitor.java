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

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.Table;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.network.BroadcastMessage;
import uk.ac.imperial.presage2.core.network.MessageBlockedEvent;
import uk.ac.imperial.presage2.core.network.MessageDeliveryEvent;
import uk.ac.imperial.presage2.core.network.MulticastMessage;
import uk.ac.imperial.presage2.core.plugin.Plugin;
import uk.ac.imperial.presage2.core.simulator.EndOfTimeCycle;

import com.google.inject.Inject;

public class NetworkMessageMonitor implements Plugin {

	private final Logger logger = Logger.getLogger(NetworkMessageMonitor.class);

	private final StorageService db;
	private final Time time;
	private Table t = null;

	private int broadcasts = 0;
	private int multicasts = 0;
	private int unicasts = 0;

	@Inject
	public NetworkMessageMonitor(StorageService db, EventBus eb, Time t) {
		super();
		this.db = db;
		this.time = t;
		eb.subscribe(this);
	}

	@EventListener
	public synchronized void onMessageDelivery(MessageDeliveryEvent e) {
		if (e.getMessage() instanceof BroadcastMessage) {
			broadcasts++;
		} else if (e.getMessage() instanceof MulticastMessage) {
			multicasts++;
		} else {
			unicasts++;
		}
	}

	@EventListener
	public void onMessageBlocked(MessageBlockedEvent e) {

	}

	@EventListener
	public synchronized void onEndOfTimeCycle(EndOfTimeCycle e) {
		if (t != null) {
			try {
				t.insert().atTimeStep(time.intValue())
						.set("Broadcasts", broadcasts)
						.set("Multicasts", multicasts)
						.set("Unicasts", unicasts).commit();
			} catch (Exception e1) {
				logger.warn("Error inserting to db", e1);
			}
			time.increment();
			broadcasts = 0;
			multicasts = 0;
			unicasts = 0;
		}
	}

	@Override
	public void incrementTime() {
	}

	@Override
	public void initialise() {
		// create db table
		try {
			t = db.buildTable("message_counts").forClass(getClass())
					.withFields("Broadcasts", "Multicasts", "Unicasts")
					.withTypes(Long.class, Long.class, Long.class)
					.withOneRowPerTimeCycle().create();
		} catch (Exception e) {
			logger.warn("Could not create table.", e);
		}
	}

	@Override
	public void execute() {
	}

	@Override
	public void onSimulationComplete() {

	}

}
