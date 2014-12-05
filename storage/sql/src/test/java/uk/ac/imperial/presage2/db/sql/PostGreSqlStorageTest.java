/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.db.sql;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.imperial.presage2.core.db.GenericStorageServiceTest;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;

@Ignore
public class PostGreSqlStorageTest extends GenericStorageServiceTest {

	SqlStorage sqlSto;

	@Override
	public void getDatabase() {
		Properties jdbcInfo = new Properties();
		jdbcInfo.put("driver", "org.postgresql.Driver");
		jdbcInfo.put("url", "jdbc:postgresql://localhost/presage_test");
		jdbcInfo.put("user", "presage_test");
		jdbcInfo.put("password", "test_user");
		this.sqlSto = new SqlStorage(jdbcInfo);
		try {
			this.sqlSto.start();
			//dropTestTables();
			this.sqlSto.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.db = sqlSto;
		this.sto = sqlSto;
	}

	void dropTestTables() throws SQLException {
		Statement stmt = sqlSto.conn.createStatement();
		stmt.execute("DROP TABLE IF EXISTS agentTransient;");
		stmt.execute("DROP TABLE IF EXISTS agents;");
		stmt.execute("DROP TABLE IF EXISTS environmentTransient;");
		stmt.execute("DROP TABLE IF EXISTS environment;");
		stmt.execute("DROP TABLE IF EXISTS simulations;");
	}

	public void testSimExecution() throws Exception {
		final Mockery context = new Mockery();
		final Scenario s = context.mock(Scenario.class);
		

		long benchmarkStart = System.currentTimeMillis();

		final String simName = RandomStringUtils.randomAlphanumeric(Random
				.randomInt(20));
		final String simClass = RandomStringUtils.randomAlphanumeric(Random
				.randomInt(100));
		final int simFinish = 100;
		final PersistentSimulation sim = sto.createSimulation(simName,
				simClass, "LOADING", simFinish);
		final long simId = sim.getID();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("finishTime", Integer.toString(simFinish));
		parameters.put("a", RandomStringUtils.randomAlphabetic(10));
		parameters.put("b", Integer.toString(Random.randomInt(100000)));
		parameters.put("c", Double.toString(Random.randomDouble()));
		for (Map.Entry<String, String> p : parameters.entrySet()) {
			sim.addParameter(p.getKey(), p.getValue());
		}

		sim.setStartedAt(System.currentTimeMillis());

		Map<String, String> environmentData = new HashMap<String, String>();
		Map<Integer, Map<String, String>> transEnvData = new HashMap<Integer, Map<String, String>>();
		Map<UUID, Map<String, String>> agentData = new HashMap<UUID, Map<String, String>>();
		Map<UUID, Map<Integer, Map<String, String>>> transAgentData = new HashMap<UUID, Map<Integer, Map<String, String>>>();

		// generate some agents
		final int agentCount = 20;
		for (int i = 0; i < agentCount; i++) {
			UUID aid = Random.randomUUID();
			String name = RandomStringUtils.randomAlphabetic(5);
			PersistentAgent ag = sto.createAgent(aid, name);
			Map<String, String> agentProperties = new HashMap<String, String>();
			agentData.put(aid, agentProperties);
			agentProperties.put("name", name);
			agentProperties.put("a", RandomStringUtils.randomAlphabetic(10));
			agentProperties
					.put("b", Integer.toString(Random.randomInt(100000)));
			agentProperties.put("c", Double.toString(Random.randomDouble()));
			ag.setProperty("a", agentProperties.get("a"));
			ag.setProperty("b", agentProperties.get("b"));
			ag.setProperty("c", agentProperties.get("c"));
			transAgentData
					.put(aid, new HashMap<Integer, Map<String, String>>());
		}

		sim.setState("RUNNING");

		for (int t = 0; t <= simFinish; t++) {
			sim.setCurrentTime(t);
			if (Random.randomDouble() >= 0.90) {
				String key = RandomStringUtils.randomAlphabetic(5);
				String value = Integer.toString(Random.randomInt(10000));
				environmentData.put(key, value);
				sto.getSimulation().getEnvironment().setProperty(key, value);
			}
			if (true) {
				String key = RandomStringUtils.randomAlphabetic(5);
				String value = Double.toString(Random.randomDouble());
				Map<String, String> dataPoint = new HashMap<String, String>();
				transEnvData.put(t, dataPoint);
				dataPoint.put(key, value);
				sto.getSimulation().getEnvironment().setProperty(key, t, value);
			}
			for (UUID aid : agentData.keySet()) {
				Map<String, String> state = new HashMap<String, String>();
				transAgentData.get(aid).put(t, state);
				if (Random.randomDouble() > 0.2) {
					String key = RandomStringUtils.randomAlphabetic(5);
					String value = Double.toString(Random.randomDouble());
					state.put(key, value);
					sto.getAgentState(aid, t).setProperty(key, value);
				}
				if (Random.randomDouble() > 0.5) {
					String key = RandomStringUtils.randomAlphabetic(5);
					String value = Double.toString(Random.randomInt());
					state.put(key, value);
					sto.getAgent(aid).getState(t).setProperty(key, value);
				}
			}
			//sqlSto.incrementTime();
		}

		sim.setState("COMPLETE");
		sim.setFinishedAt(System.currentTimeMillis());

		db.stop();

		System.out.println("Benchmark: " + agentCount + " agents; " + simFinish
				+ " timesteps. "
				+ (System.currentTimeMillis() - benchmarkStart) + "ms.");

		// check expectations
		db.start();
		Connection conn = sqlSto.conn;

		// check simulations table
		PreparedStatement getSimulation = conn
				.prepareStatement("SELECT name, state, classname, \"currentTime\", \"finishTime\" FROM simulations WHERE id = ?");
		getSimulation.setLong(1, simId);
		ResultSet rs = getSimulation.executeQuery();
		assertTrue(rs.next());
		assertEquals(simName, rs.getString(1));
		assertEquals("COMPLETE", rs.getString(2));
		assertEquals(simClass, rs.getString(3));
		assertEquals(simFinish, rs.getInt(4));
		assertEquals(simFinish, rs.getInt(5));

		// check parameters
		PreparedStatement getParameters = conn.prepareStatement(Sql
				.getParametersById());
		getParameters.setLong(1, simId);
		rs = getParameters.executeQuery();
		int paramCount = 0;
		while (rs.next()) {
			String key = rs.getString(1);
			String value = rs.getString(2);
			if (parameters.containsKey(key))
				assertEquals(parameters.get(key), value);
			else
				fail("Unexpected parameter: " + key);
			paramCount++;
		}
		assertEquals(parameters.size(), paramCount);

		// check agents
		PreparedStatement getAgents = conn
				.prepareStatement("SELECT aid, name FROM agents WHERE \"simId\" = ? ");
		getAgents.setLong(1, simId);
		rs = getAgents.executeQuery();
		PreparedStatement getAgentState = conn
				.prepareStatement("SELECT (a.s).key, (a.s).value FROM "
						+ "(SELECT each(state) AS s FROM agents WHERE \"simId\" = ? AND aid = ?) AS a");
		int observedAgents = 0;
		while (rs.next()) {
			UUID aid = UUID.fromString(rs.getString(1));
			String name = rs.getString(2);
			assertTrue(agentData.containsKey(aid));
			assertEquals(agentData.get(aid).get("name"), name);

			getAgentState.setLong(1, simId);
			getAgentState.setObject(2, aid);
			ResultSet state = getAgentState.executeQuery();
			int observedState = 0;
			while (state.next()) {
				String key = state.getString(1);
				String value = state.getString(2);
				assertTrue(agentData.get(aid).containsKey(key));
				assertEquals(agentData.get(aid).get(key), value);
				observedState++;
			}
			assertEquals(3, observedState);
			observedAgents++;
		}
		assertEquals(agentCount, observedAgents);

		// check environment state
		PreparedStatement getEnvironment = conn
				.prepareStatement("SELECT (e.s).key, (e.s).value FROM "
						+ "(SELECT each(state) AS s FROM environment WHERE \"simId\" = ? ) AS e");
		getEnvironment.setLong(1, simId);
		rs = getEnvironment.executeQuery();
		int observedEnvState = 0;
		while (rs.next()) {
			String key = rs.getString(1);
			String value = rs.getString(2);
			if (environmentData.containsKey(key))
				assertEquals(environmentData.get(key), value);
			else
				fail("Unexpected env state: " + key);
			observedEnvState++;
		}
		assertEquals(environmentData.size(), observedEnvState);

		// environment transient state
		PreparedStatement getTransEnv = conn
				.prepareStatement("SELECT (e.s).key, (e.s).value FROM "
						+ "(SELECT each(state) AS s FROM environmentTransient WHERE \"simId\" = ? AND time = ? ) AS e");
		for (Integer t : transEnvData.keySet()) {
			getTransEnv.setLong(1, simId);
			getTransEnv.setInt(2, t);
			rs = getTransEnv.executeQuery();
			Map<String, String> state = transEnvData.get(t);
			int observed = 0;
			while (rs.next()) {
				String key = rs.getString(1);
				String value = rs.getString(2);
				if (state.containsKey(key))
					assertEquals(state.get(key), value);
				else
					fail("Unexpected env state: " + key);
				observed++;
			}
			assertEquals(state.size(), observed);
		}
		
		// agent transient state
		PreparedStatement getTransAgent = conn.prepareStatement("SELECT (a.s).key, (a.s).value FROM "
				+ "(SELECT each(state) AS s FROM agentTransient WHERE \"simId\" = ? AND aid = ? AND time = ? ) AS a");
		for(UUID aid : transAgentData.keySet()) {
			for(Integer t : transAgentData.get(aid).keySet()) {
				getTransAgent.setLong(1, simId);
				getTransAgent.setObject(2, aid);
				getTransAgent.setInt(3, t);
				rs = getTransAgent.executeQuery();
				Map<String, String> state = transAgentData.get(aid).get(t);
				int observed = 0;
				while (rs.next()) {
					String key = rs.getString(1);
					String value = rs.getString(2);
					if (state.containsKey(key))
						assertEquals(state.get(key), value);
					else
						fail("Unexpected agent state: " + key);
					observed++;
				}
				assertEquals(state.size(), observed);
			}
		}

	}

}
