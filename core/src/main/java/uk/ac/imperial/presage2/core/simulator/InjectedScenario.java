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

import java.util.Set;

import uk.ac.imperial.presage2.core.FinishTime;
import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.Inject;

/**
 * 
 * <p>Instance of a {@link Scenario} where elements are injected via
 * Guice using a {@link ScenarioModule}.</p>
 * 
 * 
 * 
 * @author Sam Macbeth
 *
 */
public class InjectedScenario implements Scenario {
	
	final private Set<Participant> participants;
	
	final private Set<Plugin> plugins;
	
	final private Set<TimeDriven> timedriven;
	
	final private Time finishTime;
	
	/**
	 * @param participants
	 * @param plugins
	 * @param timedriven
	 */
	@Inject
	protected InjectedScenario(Set<Participant> participants, Set<Plugin> plugins,
			Set<TimeDriven> timedriven, @FinishTime Time finish) {
		this.participants = participants;
		this.plugins = plugins;
		this.timedriven = timedriven;
		this.finishTime = finish;
	}

	@Override
	public Set<Participant> getParticipants() {
		return participants;
	}

	@Override
	public Set<TimeDriven> getTimeDriven() {
		return timedriven;
	}

	@Override
	public Set<Plugin> getPlugins() {
		return plugins;
	}

	@Override
	public Time getFinishTime() {
		return finishTime;
	}
	

}
