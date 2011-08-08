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
package uk.ac.imperial.presage2.db.graph;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.GraphDB;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

@Singleton
class Neo4jDatabase implements DatabaseService, GraphDB,
		Provider<GraphDatabaseService> {

	enum SubRefs implements RelationshipType {
		SIMULATIONS, SIMULATION_STATES, SIMULATION_PARAMETERS, SIMULATION_TIMESTEPS, PLUGINS, AGENTS
	}

	protected final Logger logger = Logger.getLogger(Neo4jDatabase.class);

	GraphDatabaseService graphDB = null;

	SimulationFactory simFactory;

	PersistentAgentFactory agentFactory;

	PersistentSimulation simulation;

	private static String databasePath = "var/presagedb";
	static final String LABEL = "label";

	@Override
	public void start() throws Exception {
		if (graphDB == null) {
			logger.info("Starting embedded Neo4j database at " + databasePath);
			graphDB = new EmbeddedGraphDatabase(databasePath);

			simFactory = new SimulationNode.Factory(graphDB);
			agentFactory = new AgentNode.Factory(this, get());
		}
	}

	@Override
	public boolean isStarted() {
		return graphDB != null;
	}

	@Override
	public void stop() {
		if (graphDB != null) {
			logger.info("Shutting down Neo4j database...");
			graphDB.shutdown();
		}
	}

	@Override
	public SimulationFactory getSimulationFactory() {
		return simFactory;
	}

	@Override
	public PersistentSimulation getSimulation() {
		return simulation;
	}

	@Override
	public void setSimulation(PersistentSimulation sim) {
		simulation = sim;
	}

	static Node getSubRefNode(GraphDatabaseService db, SubRefs type) {
		Relationship r = db.getReferenceNode().getSingleRelationship(type,
				Direction.OUTGOING);
		if (r == null) {
			Transaction tx = db.beginTx();
			try {
				Node subRef = db.createNode();
				subRef.setProperty(LABEL, type.name());
				r = db.getReferenceNode().createRelationshipTo(subRef, type);
				tx.success();
			} finally {
				tx.finish();
			}
		}
		return r.getEndNode();
	}

	@Override
	public GraphDatabaseService get() {
		if (graphDB == null) {
			try {
				this.start();
			} catch (Exception e) {
				logger.fatal("Exception when starting database", e);
			}
		}
		return graphDB;
	}

	@Override
	public TransientAgentState getAgentState(UUID agentID, int time) {
		TransientAgentStateNode stateNode = TransientAgentStateNode.get(
				(AgentNode) getAgent(agentID), time);
		if (time > 0) {
			stateNode.setPrevious(TransientAgentStateNode.get(
					(AgentNode) agentFactory.get(getSimulation(), agentID),
					time - 1));
		}
		return stateNode;
	}

	@Override
	public uk.ac.imperial.presage2.core.db.Transaction startTransaction() {
		return new TransactionDelegate(this.graphDB.beginTx());
	}

	class TransactionDelegate implements
			uk.ac.imperial.presage2.core.db.Transaction {

		private final Transaction delegate;

		private TransactionDelegate(Transaction delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		public void failure() {
			delegate.failure();
		}

		@Override
		public void finish() {
			delegate.finish();
		}

		@Override
		public void success() {
			delegate.success();
		}
	}

	@Override
	public PersistentAgent getAgent(UUID agentID) {
		return agentFactory.get(getSimulation(), agentID);
	}

}
