/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PostgreSQLStorage extends SqlStorage {

	PreparedStatement createAgent = null;

	@Inject
	public PostgreSQLStorage(@Named(value = "sql.info") Properties jdbcInfo) {
		super(jdbcInfo);
		if (Sql.dialect != Dialect.POSTGRESQL) {
			throw new RuntimeException(
					"Cannot use Postgresql storage with non-pgsql driver.");
		}
		Sql.dialect = Dialect.POSTGRESQL_HSTORE;
	}

	@Override
	protected void initTables() {
		super.initTables();
		Statement createTables = null;
		try {
			createTables = conn.createStatement();
			createTables.execute("CREATE TABLE IF NOT EXISTS environment"
					+ "(\"simId\" bigint NOT NULL REFERENCES simulations,"
					+ "state hstore default hstore(array[]::varchar[]),"
					+ "PRIMARY KEY (\"simId\"));");
			createTables
					.execute("CREATE TABLE IF NOT EXISTS environmentTransient"
							+ "(\"simId\" bigint NOT NULL REFERENCES simulations,"
							+ "time int NOT NULL,"
							+ "state hstore default hstore(array[]::varchar[]),"
							+ "PRIMARY KEY (\"simId\", time));");
			createTables.execute("CREATE TABLE IF NOT EXISTS agents"
					+ "(\"simId\" bigint NOT NULL REFERENCES simulations,"
					+ "\"aid\" uuid NOT NULL," + "name varchar(100),"
					+ "state hstore default hstore(array[]::varchar[]),"
					+ "PRIMARY KEY (\"simId\", \"aid\"));");
			createTables
					.execute("CREATE TABLE IF NOT EXISTS agentTransient"
							+ "(\"simId\" bigint NOT NULL REFERENCES simulations,"
							+ "\"aid\" uuid NOT NULL,"
							+ "time int NOT NULL,"
							+ "state hstore default hstore(array[]::varchar[]),"
							+ "PRIMARY KEY (\"simId\", \"aid\", time),"
							+ "FOREIGN KEY (\"simId\", \"aid\") REFERENCES agents (\"simId\", \"aid\"));");
			createTables.close();
		} catch (SQLException e) {
			logger.fatal("Couldn't create tables", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public PersistentAgent createAgent(UUID agentID, String name) {
		PersistentAgent a = super.createAgent(agentID, name);
		try {
			if (createAgent == null)
				createAgent = conn
						.prepareStatement("INSERT INTO agents (\"simId\", \"aid\", name) VALUES (?, ?, ?)");
			synchronized (createAgent) {
				createAgent.setLong(1, this.simId);
				createAgent.setObject(2, a.getID());
				createAgent.setString(3, a.getName());
				createAgent.addBatch();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return a;
	}

	@Override
	protected void updateSimulations() {
		PreparedStatement updateSimulation = null;
		PreparedStatement updateParameters = null;
		try {
			updateSimulation = conn
					.prepareStatement("UPDATE simulations SET state = ?, "
							+ "\"currentTime\" = ?, " + "\"startedAt\" = ?, "
							+ "\"finishedAt\" = ?," + "parent = ? "
							+ "WHERE id = ? ;");
			updateParameters = conn
					.prepareStatement("UPDATE simulations SET parameters = parameters || hstore(?, ?) WHERE id = ?");
			synchronized (simulationQ) {
				for (Simulation s : simulationQ) {

					updateSimulation.setString(1, s.getState());
					updateSimulation.setInt(2, s.getCurrentTime());
					updateSimulation.setLong(3, s.getStartedAt());
					updateSimulation.setLong(4, s.getFinishedAt());
					updateSimulation.setLong(5, s.parent);
					updateSimulation.setLong(6, s.getID());

					updateSimulation.addBatch();

					// update parameters if they are dirty
					if (s.dirty) {
						for (Map.Entry<String, String> p : s.getParameters()
								.entrySet()) {
							updateParameters.setString(1, p.getKey());
							updateParameters.setString(2, p.getValue());
							updateParameters.setLong(3, s.getID());
							updateParameters.addBatch();
						}
						batchQueryQ.put(updateParameters);
						s.dirty = false;
					}
				}
				if (simulationQ.size() > 0) {
					batchQueryQ.put(updateSimulation);
				}
				simulationQ.clear();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateEnvironment() {
		try {
			PreparedStatement insertEnvironment = conn
					.prepareStatement("INSERT INTO environment (\"simId\")"
							+ "	SELECT ?"
							+ "	WHERE NOT EXISTS (SELECT 1 FROM environment WHERE \"simId\"=?);");
			PreparedStatement updateEnvironment = conn
					.prepareStatement("UPDATE environment SET state = state || hstore(?, ?) "
							+ "WHERE \"simId\" = ?");

			synchronized (environmentQ) {
				for (Environment env : environmentQ) {
					for (Map.Entry<String, String> prop : env.getProperties()
							.entrySet()) {
						insertEnvironment.setLong(1, env.simId);
						insertEnvironment.setLong(2, env.simId);
						insertEnvironment.addBatch();
						updateEnvironment.setString(1, prop.getKey());
						updateEnvironment.setString(2, prop.getValue());
						updateEnvironment.setLong(3, env.simId);
						updateEnvironment.addBatch();
					}
				}
				if (environmentQ.size() > 0) {
					batchQueryQ.put(insertEnvironment);
					batchQueryQ.put(updateEnvironment);
				}
				environmentQ.clear();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateTransientEnvironment() {
		try {
			PreparedStatement insertEnvironment = conn
					.prepareStatement("INSERT INTO environmentTransient (\"simId\", \"time\")"
							+ "	SELECT ?, ?"
							+ "	WHERE NOT EXISTS (SELECT 1 FROM environmentTransient WHERE \"simId\"=? AND \"time\" = ?);");
			PreparedStatement updateEnvironment = conn
					.prepareStatement("UPDATE environmentTransient SET state = state || hstore(?, ?) "
							+ "WHERE \"simId\" = ? AND \"time\" = ?");
			synchronized (environmentTransientQ) {
				for (Environment e : environmentTransientQ) {
					for (Integer t : e.transientProperties.keySet()) {
						for (Map.Entry<String, String> p : e.transientProperties
								.get(t).entrySet()) {
							updateEnvironment.setString(1, p.getKey());
							updateEnvironment.setString(2, p.getValue());
							updateEnvironment.setLong(3, e.simId);
							updateEnvironment.setInt(4, t);
							updateEnvironment.addBatch();
						}
						insertEnvironment.setLong(1, e.simId);
						insertEnvironment.setLong(3, e.simId);
						insertEnvironment.setInt(2, t);
						insertEnvironment.setInt(4, t);
						insertEnvironment.addBatch();
					}
					e.transientProperties.clear();
				}
				if (environmentTransientQ.size() > 0) {
					batchQueryQ.put(insertEnvironment);
					batchQueryQ.put(updateEnvironment);
				}
				environmentTransientQ.clear();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected void updateAgents() {
		// insert any new agents
		if (createAgent != null) {
			synchronized (createAgent) {
				try {
					batchQueryQ.put(createAgent);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				createAgent = null;
			}
		}
		// update agent states
		try {
			PreparedStatement updateAgents = conn
					.prepareStatement("UPDATE agents "
							+ "SET state = state || hstore(?, ?) "
							+ "WHERE \"simId\" = ? AND \"aid\" = ?");
			synchronized (agentQ) {
				for (Agent a : agentQ) {
					for (Map.Entry<String, String> p : a.getProperties()
							.entrySet()) {
						updateAgents.setString(1, p.getKey());
						updateAgents.setString(2, p.getValue());
						updateAgents.setLong(3, a.simId);
						updateAgents.setObject(4, a.getID());
						updateAgents.addBatch();
					}
				}
				if (agentQ.size() > 0)
					batchQueryQ.put(updateAgents);
				agentQ.clear();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateTransientAgents() {
		try {
			PreparedStatement insertAgentState = conn
					.prepareStatement("INSERT INTO agentTransient (\"simId\", \"aid\", \"time\")"
							+ "	SELECT ?, ?, ?"
							+ "	WHERE NOT EXISTS (SELECT 1 FROM agentTransient WHERE \"simId\"=? AND \"aid\" = ? AND \"time\" = ?);");
			PreparedStatement updateAgentState = conn
					.prepareStatement("UPDATE agentTransient SET state = state || hstore(?, ?) "
							+ "WHERE \"simId\" = ? AND \"aid\" = ? AND \"time\" = ?");
			synchronized (agentTransientQ) {
				for (Agent a : agentTransientQ) {
					for (Integer t : a.transientProperties.keySet()) {
						// create agent state if it doesn't exist
						insertAgentState.setLong(1, a.simId);
						insertAgentState.setObject(2, a.getID());
						insertAgentState.setLong(3, t);
						insertAgentState.setLong(4, a.simId);
						insertAgentState.setObject(5, a.getID());
						insertAgentState.setLong(6, t);
						insertAgentState.addBatch();

						// insert/update states for this time
						for (Map.Entry<String, String> p : a.transientProperties
								.get(t).entrySet()) {
							updateAgentState.setString(1, p.getKey());
							updateAgentState.setString(2, p.getValue());
							updateAgentState.setLong(3, a.simId);
							updateAgentState.setObject(4, a.getID());
							updateAgentState.setInt(5, t);
							updateAgentState.addBatch();
						}
					}
					a.transientProperties.clear();
				}
				if (agentTransientQ.size() > 0) {
					batchQueryQ.put(insertAgentState);
					batchQueryQ.put(updateAgentState);
				}
				agentTransientQ.clear();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
