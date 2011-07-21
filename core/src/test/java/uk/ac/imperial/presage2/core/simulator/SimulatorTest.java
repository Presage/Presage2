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

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * @author Sam Macbeth
 * 
 */
abstract public class SimulatorTest {

	protected Simulator simulatorUnderTest;

	// Create mock scenario
	final Mockery context = new Mockery();

	final Scenario scenario = context.mock(Scenario.class);

	final Time time = context.mock(Time.class);

	final EventBus eventBus = context.mock(EventBus.class);

	@Before
	public void mockExpectations() {
		context.checking(new Expectations() {
			{
				allowing(time).clone();
				will(returnValue(time));
			}
		});
	}

	/**
	 * Initialise Simulator under test in the member variable
	 * {@link #simulatorUnderTest}.
	 * 
	 * @throws Exception
	 */
	@Before
	abstract public void setUp() throws Exception;

	@Test
	public void testSimulator() {
		// create some mock scenario entities
		final Participant part1 = context.mock(Participant.class,
				"participant1");
		final Participant part2 = context.mock(Participant.class,
				"participant2");
		final Set<Participant> partSet = new HashSet<Participant>();
		partSet.add(part1);
		partSet.add(part2);

		final TimeDriven td1 = context.mock(TimeDriven.class, "timedriven1");
		final TimeDriven td2 = context.mock(TimeDriven.class, "timedriven2");
		final Set<TimeDriven> tdSet = new HashSet<TimeDriven>();
		tdSet.add(td1);
		tdSet.add(td2);

		final Plugin plug1 = context.mock(Plugin.class, "plugin1");
		final Plugin plug2 = context.mock(Plugin.class, "plugin2");
		final Set<Plugin> plugSet = new HashSet<Plugin>();
		plugSet.add(plug1);
		plugSet.add(plug2);

		final Time finishTime = context.mock(Time.class, "finish");

		// initialisation expectations
		context.checking(new Expectations() {
			{
				allowing(scenario).getParticipants();
				will(returnValue(partSet));
				allowing(scenario).getPlugins();
				will(returnValue(plugSet));
				allowing(scenario).getTimeDriven();
				will(returnValue(tdSet));
				allowing(time).increment();
				allowing(finishTime).clone();
				will(returnValue(finishTime));
				oneOf(part1).initialise();
				oneOf(part2).initialise();
				oneOf(plug1).initialise();
				oneOf(plug2).initialise();
			}
		});

		simulatorUnderTest.initialise();

		context.assertIsSatisfied();

		// execution expectations
		final Sequence loopLimit = context.sequence("loopLimit");
		context.checking(new Expectations() {
			{
				allowing(scenario).getFinishTime();
				will(returnValue(finishTime));
				exactly(2).of(finishTime).greaterThan(time);
				will(returnValue(true));
				inSequence(loopLimit);
				oneOf(finishTime).greaterThan(time);
				will(returnValue(false));
				inSequence(loopLimit);
				exactly(2).of(part1).incrementTime();
				exactly(2).of(part2).incrementTime();
				exactly(2).of(plug1).incrementTime();
				exactly(2).of(plug2).incrementTime();
				exactly(2).of(td1).incrementTime();
				exactly(2).of(td2).incrementTime();
			}
		});

		simulatorUnderTest.run();

		context.assertIsSatisfied();

		// completion expectations
		context.checking(new Expectations() {
			{
				oneOf(plug1).onSimulationComplete();
				oneOf(plug2).onSimulationComplete();
				oneOf(eventBus).publish(with(any(FinalizeEvent.class)));
			}
		});

		simulatorUnderTest.complete();

		context.assertIsSatisfied();

	}

}
