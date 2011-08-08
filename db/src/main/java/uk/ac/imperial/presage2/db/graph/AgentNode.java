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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.RelationshipIndex;

import uk.ac.imperial.presage2.core.participant.Participant;

class AgentNode extends NodeDelegate {

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

	public static AgentNode create(GraphDatabaseService db, Participant p) {
		Transaction tx = db.beginTx();
		Index<Node> agentIndex = db.index().forNodes(INDEX_ID);
		AgentNode a = null;
		try {
			Node n = db.createNode();
			n.setProperty(KEY_ID, p.getID().toString());
			n.setProperty(KEY_ID_RAW, rawUUID(p.getID()));
			agentIndex.add(n, KEY_ID_RAW, rawUUID(p.getID()));
			n.setProperty(KEY_NAME, p.getName());
			a = new AgentNode(n);
			tx.success();
		} finally {
			tx.finish();
		}
		return a;
	}

	public static AgentNode get(SimulationNode simulation, UUID agentID) {
		RelationshipIndex agentIndex = simulation.getGraphDatabase().index()
				.forRelationships(INDEX_RELATIONSHIP);
		Relationship r = agentIndex.get(KEY_ID_RAW, rawUUID(agentID), null,
				simulation.getUnderlyingNode()).getSingle();
		if (r == null)
			return null;
		else
			return new AgentNode(r.getStartNode());
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

	public UUID getID() {
		int[] rawID = (int[]) this.getProperty(KEY_ID_RAW);
		return new UUID(rawID[0], rawID[1]);
	}

	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

	public void setRegisteredAt(int time) {
		setPropertyOnParticipantInRelationship(KEY_REGISTERED_AT, time);
	}

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

}
