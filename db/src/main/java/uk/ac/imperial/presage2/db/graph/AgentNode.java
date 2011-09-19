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

import java.util.Map;
import java.util.UUID;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.db.graph.Neo4jDatabase.SubRefs;

import com.google.inject.Inject;
import com.google.inject.Singleton;

public class AgentNode extends NodeDelegate implements PersistentAgent {

	public enum AgentRelationships implements RelationshipType {
		AGENT, PARTICIPANT_IN, TRANSIENT_STATE
	}

	private static final String KEY_ID = "id";
	private static final String KEY_ID_RAW = "id_raw";
	private static final String KEY_NAME = "name";
	private static final String KEY_REGISTERED_AT = "registeredAt";
	private static final String KEY_DEREGISTERED_AT = "deregisteredAt";

	private static final String INDEX_ID = "agentIDs";
	private static final String INDEX_SIMS = "sim_to_agent";

	protected AgentNode(Node delegate) {
		super(delegate);
	}

	@Singleton
	static class Factory implements PersistentAgentFactory {

		StorageService graph;
		GraphDatabaseService db;

		@Inject
		Factory(StorageService graph, GraphDatabaseService db) {
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
				// properties
				n.setProperty(Neo4jDatabase.LABEL, name);
				n.setProperty(KEY_ID, id.toString());
				n.setProperty(KEY_ID_RAW, rawUUID(id));
				n.setProperty(KEY_NAME, name);
				// UUID index
				agentIndex.add(n, KEY_ID, id.toString());

				// relationship to agent subref node
				Neo4jDatabase.getSubRefNode(db, SubRefs.AGENTS).createRelationshipTo(n,
						AgentRelationships.AGENT);

				// relationship to simulation
				a = new AgentNode(n);
				a.addToSimulation((SimulationNode) this.graph.getSimulation(), id);
				tx.success();
			} finally {
				tx.finish();
			}
			return a;
		}

		@Override
		public PersistentAgent get(PersistentSimulation sim, UUID id) {
			try {
				Relationship agentToSim = db
						.index()
						.forRelationships(INDEX_SIMS)
						.get(KEY_ID, id.toString(), null,
								((SimulationNode) sim).getUnderlyingNode()).getSingle();
				if (agentToSim == null)
					return null;
				else
					return new AgentNode(agentToSim.getStartNode());
			} catch (UnsupportedOperationException e) {
				// fallback for REST
				for (Relationship agentToSim : ((SimulationNode) sim).getUnderlyingNode()
						.getRelationships(AgentRelationships.PARTICIPANT_IN, Direction.INCOMING)) {
					if (agentToSim.getStartNode().getProperty(KEY_ID).equals(id.toString())) {
						return new AgentNode(agentToSim.getStartNode());
					}
				}
				return null;
			}
		}

	}

	private static long[] rawUUID(UUID id) {
		long[] rawID = { id.getMostSignificantBits(), id.getLeastSignificantBits() };
		return rawID;
	}

	public void addToSimulation(SimulationNode sim, UUID id) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			Relationship agentToSim = this.createRelationshipTo(sim.getUnderlyingNode(),
					AgentRelationships.PARTICIPANT_IN);
			this.getGraphDatabase().index().forRelationships(INDEX_SIMS)
					.add(agentToSim, KEY_ID, id.toString());
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
			this.getSingleRelationship(AgentRelationships.PARTICIPANT_IN, Direction.OUTGOING)
					.setProperty(key, value);
			tx.success();
		} catch (NullPointerException e) {
			tx.failure();
			throw new RuntimeException("Agent does not have a simulation relationship", e);
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

	@Override
	public void createRelationshipTo(PersistentAgent p, String type, Map<String, Object> parameters) {
		if (p instanceof AgentNode) {
			AgentNode n = (AgentNode) p;
			Transaction tx = this.getGraphDatabase().beginTx();
			try {
				Relationship rel = this.createRelationshipTo(n.getUnderlyingNode(),
						DynamicRelationshipType.withName(type));
				for (Map.Entry<String, Object> entry : parameters.entrySet()) {
					rel.setProperty(entry.getKey(), entry.getValue());
				}
				tx.success();
			} finally {
				tx.finish();
			}
		} else {
			throw new UnsupportedOperationException(
					"Cannot create relationship to non-AgentNode PersistentAgent.");
		}
	}

	@Override
	public TransientAgentState getState(int time) {
		return TransientAgentStateNode.get(this, time);
	}

}
