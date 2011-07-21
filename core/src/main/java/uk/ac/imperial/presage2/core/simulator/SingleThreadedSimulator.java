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

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.Inject;

/**
 * <p>
 * Simulator which executes agents consecutively in a single thread
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class SingleThreadedSimulator extends Simulator {

	private final Logger logger = Logger
			.getLogger(SingleThreadedSimulator.class);

	/**
	 * @param scenario
	 */
	@Inject
	public SingleThreadedSimulator(Scenario scenario, Time t, EventBus eventBus) {
		super(scenario, t, eventBus);
	}

	@Override
	public void initialise() {
		// init Participants
		logger.info("Initialising Participants..");
		for (Participant p : this.scenario.getParticipants()) {
			try {
				p.initialise();
			} catch (Exception e) {
				logger.warn("Exception thrown by participant " + p.getName()
						+ " on initialisation.", e);
			}
		}
		// init Plugins
		logger.info("Initialising Plugins..");
		for (Plugin pl : this.scenario.getPlugins()) {
			try {
				pl.initialise();
			} catch (Exception e) {
				logger.warn("Exception thrown by plugin " + pl
						+ " on execution.", e);
			}
		}
	}

	@Override
	public void run() {

		while (this.scenario.getFinishTime().greaterThan(time)) {

			logger.info("Time: " + time.toString());

			logger.debug("Executing Participants...");
			for (Participant p : this.scenario.getParticipants()) {
				try {
					p.incrementTime();
				} catch (Exception e) {
					logger.warn(
							"Exception thrown by participant " + p.getName()
									+ " on execution.", e);
				}
			}

			logger.debug("Executing TimeDriven...");
			for (TimeDriven t : this.scenario.getTimeDriven()) {
				try {
					t.incrementTime();
				} catch (Exception e) {
					logger.warn("Exception thrown by TimeDriven " + t
							+ " on execution.", e);
				}
			}

			logger.debug("Executing Plugins...");
			for (Plugin pl : this.scenario.getPlugins()) {
				try {
					pl.incrementTime();
				} catch (Exception e) {
					logger.warn("Exception thrown by Plugin " + pl
							+ " on execution.", e);
				}
			}

			time.increment();

		}

		logger.info("Simulation cycle complete.");
	}

	@Override
	public void complete() {
		logger.info("Running completion tasks and tidying up.");
		for (Plugin pl : this.scenario.getPlugins()) {
			try {
				pl.onSimulationComplete();
			} catch (Exception e) {
				logger.warn("Exception thrown by Plugin " + pl
						+ " on simulation completion.", e);
			}
		}
		eventBus.publish(new FinalizeEvent(time));
	}

}
