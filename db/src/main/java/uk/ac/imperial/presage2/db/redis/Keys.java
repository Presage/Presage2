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
package uk.ac.imperial.presage2.db.redis;

import java.util.UUID;

final class Keys {

	static final class Simulation {

		final static String ID_COUNTER = "simulations.id";
		final static String ID_SET = "simulations";
		final static String STATE_NAMES = "simulations.states";
		final static String PARAMETER_NAMES = "simulations.parameters";

		private final static String prefix(long id) {
			return "simulation:" + id + ":";
		}

		final static String name(long id) {
			return prefix(id) + "name";
		}

		final static String className(long id) {
			return prefix(id) + "classname";
		}

		final static String state(long id) {
			return prefix(id) + "state";
		}

		final static String createdAt(long id) {
			return prefix(id) + "createdAt";
		}

		final static String startedAt(long id) {
			return prefix(id) + "startedAt";
		}

		final static String finishedAt(long id) {
			return prefix(id) + "finishedAt";
		}

		final static String currentTime(long id) {
			return prefix(id) + "currentTime";
		}

		final static String finishTime(long id) {
			return prefix(id) + "finishTime";
		}

		final static String stateMembershipSet(String state) {
			return "simulations." + state;
		}

		final static String parametersSet(long id) {
			return prefix(id) + "parameters";
		}

		final static String parameterValue(long id, String parameterName) {
			return prefix(id) + "parameters:" + parameterName;
		}

		final static String agentsSet(long id) {
			return prefix(id) + "agents";
		}

	}

	static class Agent {

		final long simID;
		final UUID aID;

		Agent(long simID, UUID aID) {
			super();
			this.simID = simID;
			this.aID = aID;
		}

		String prefix() {
			return Keys.Simulation.prefix(simID) + "agent:" + aID.toString() + ":";
		}

		public final String name() {
			return prefix() + "name";
		}

		public final String registeredAt() {
			return prefix() + "registeredAt";
		}

		public final String deregisteredAt() {
			return prefix() + "deregisteredAt";
		}

		public String property(String name) {
			return prefix() + name;
		}

	}

	static class AgentState extends Agent {

		final int time;

		AgentState(long simID, UUID aID, int time) {
			super(simID, aID);
			this.time = time;
		}

		@Override
		String prefix() {
			return super.prefix() + "state:" + time + ":";
		}

		@Override
		public String property(String name) {
			return prefix() + name;
		}

	}

}
