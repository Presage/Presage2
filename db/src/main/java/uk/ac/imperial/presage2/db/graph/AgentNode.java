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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import uk.ac.imperial.presage2.core.db.GraphDB;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

class AgentNode extends NodeDelegate implements PersistentAgent {

	enum AgentRelationships implements RelationshipType {
		PARTICIPANT_IN
	}

	private static final String KEY_ID = "id";
	private static final String KEY_ID_RAW = "id_raw";
	private static final String KEY_NAME = "name";
	private static final String KEY_REGISTERED_AT = "registeredAt";
	private static final String KEY_DEREGISTERED_AT = "deregisteredAt";

	private static final String INDEX_ID = "agentIDs";
	private static final String INDEX_RELATIONSHIP = "agentParticipation";

	protected AgentNode(Node delegate) {
		super(delegate);
	}

	@Singleton
	static class Factory implements PersistentAgentFactory {

		GraphDB graph;
		GraphDatabaseService db;

		@Inject
		Factory(GraphDB graph, GraphDatabaseService db) {
			this.graph = graph;
			this.db = db;
		}

		@Override
		public PersistentAgent create(UUID id, String name) {
			Transaction tx = db.beginTx();
			Index<Node> agentIndex = db.index().forNodes(INDEX_ID);
			AgentNode a = null;
			try {
				Node n = db.createNode();
				n.setProperty(Neo4jDatabase.LABEL, name);
				n.setProperty(KEY_ID, id.toString());
				n.setProperty(KEY_ID_RAW, rawUUID(id));
				agentIndex.add(n, KEY_ID_RAW, rawUUID(id));
				n.setProperty(KEY_NAME, name);
				a = new AgentNode(n);
				a.addToSimulation((SimulationNode) this.graph.getSimulation());
				tx.success();
			} finally {
				tx.finish();
			}
			return a;
		}

		@Override
		public PersistentAgent get(UUID id) {
			Index<Node> agentIndex = db.index().forNodes(INDEX_ID);
			Node n = agentIndex.get(KEY_ID_RAW, rawUUID(id)).getSingle();
			if (n == null)
				return null;
			else
				return new AgentNode(n);
		}

	}

	private static long[] rawUUID(UUID id) {
		long[] rawID = { id.getMostSignificantBits(),
				id.getLeastSignificantBits() };
		return rawID;
	}

	public void addToSimulation(SimulationNode sim) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.createRelationshipTo(sim.getUnderlyingNode(),
					AgentRelationships.PARTICIPANT_IN);
			tx.success();
		} finally {
			tx.finish();
		}

	}

	@Override
	public UUID getID() {
		int[] rawID = (int[]) this.getProperty(KEY_ID_RAW);
		return new UUID(rawID[0], rawID[1]);
	}

	@Override
	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

	@Override
	public void setRegisteredAt(int time) {
		setPropertyOnParticipantInRelationship(KEY_REGISTERED_AT, time);
	}

	@Override
	public void setDeRegisteredAt(int time) {
		setPropertyOnParticipantInRelationship(KEY_DEREGISTERED_AT, time);
	}

	private void setPropertyOnParticipantInRelationship(String key, Object value) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.getSingleRelationship(AgentRelationships.PARTICIPANT_IN,
					Direction.OUTGOING).setProperty(key, value);
			tx.success();
		} catch (NullPointerException e) {
			tx.failure();
			throw new RuntimeException(
					"Agent does not have a simulation relationship", e);
		} finally {
			tx.finish();
		}
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			super.setProperty(arg0, arg1);
			tx.success();
		} finally {
			tx.finish();
		}
	}

}
