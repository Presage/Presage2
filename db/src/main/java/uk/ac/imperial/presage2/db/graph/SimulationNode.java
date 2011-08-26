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

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;
import uk.ac.imperial.presage2.db.graph.Neo4jDatabase.SubRefs;

import com.google.inject.Inject;

class SimulationNode extends NodeDelegate implements PersistentSimulation {

	enum SimulationRelationships implements RelationshipType {
		SIMULATION, CONSISTS_OF, CURRENT_STATE, PREVIOUS_STATE, PARAMETER, CURRENT_TIME, FINISH_TIME
	}

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_CLASSNAME = "classname";
	private static final String KEY_CREATED_AT = "createdAt";
	private static final String KEY_STARTED_AT = "startedAt";
	private static final String KEY_FINISHED_AT = "finishedAt";

	private static final String KEY_PREV_STATE_TIME = "timestamp";
	private static final String KEY_PARAMETER_VALUE = "value";

	static class Factory implements SimulationFactory {

		final GraphDatabaseService db;

		private static final String KEY_COUNTER = "id_counter";
		private static final String SIMULATION_INDEX = "simulations";

		@Inject
		Factory(GraphDatabaseService db) {
			this.db = db;
		}

		@Override
		public PersistentSimulation create(String name, String classname,
				String state, int finishTime) {
			Transaction tx = db.beginTx();
			PersistentSimulation s = null;
			try {
				// create node and properties
				Node n = db.createNode();
				long simID = getNextId();
				n.setProperty(Neo4jDatabase.LABEL, "Simulation " + simID);
				n.setProperty(KEY_ID, simID);
				n.setProperty(KEY_NAME, name);
				n.setProperty(KEY_CLASSNAME, classname);
				n.setProperty(KEY_CREATED_AT, new Date().getTime());
				// relationships to subref, state, times
				getSubRefNode().createRelationshipTo(n,
						SimulationRelationships.SIMULATION);
				n.createRelationshipTo(SimulationStateNode.get(db, state)
						.getUnderlyingNode(),
						SimulationRelationships.CURRENT_STATE);
				n.createRelationshipTo(SimulationTimeNode.get(db, 0)
						.getUnderlyingNode(),
						SimulationRelationships.CURRENT_TIME);
				n.createRelationshipTo(SimulationTimeNode.get(db, finishTime)
						.getUnderlyingNode(),
						SimulationRelationships.FINISH_TIME);
				// index on id
				Index<Node> simIndex = db.index().forNodes(SIMULATION_INDEX);
				simIndex.add(n, KEY_ID, simID);
				s = new SimulationNode(n);
				tx.success();
			} finally {
				tx.finish();
			}
			return s;
		}

		@Override
		public PersistentSimulation get(long simulationID) {
			Index<Node> simIndex = db.index().forNodes(SIMULATION_INDEX);
			Node result = simIndex.get(KEY_ID, simulationID).getSingle();
			if (result == null) {
				// try non-index lookup
				for (Relationship r : Neo4jDatabase.getSubRefNode(db,
						SubRefs.SIMULATIONS).getRelationships(
						SimulationRelationships.SIMULATION, Direction.OUTGOING)) {
					if (((Long) r.getEndNode().getProperty(KEY_ID, -1))
							.longValue() == simulationID) {
						return new SimulationNode(r.getEndNode());
					}
				}
				return null;
			} else
				return new SimulationNode(result);
		}

		private Node getSubRefNode() {
			Relationship r = db.getReferenceNode().getSingleRelationship(
					SubRefs.SIMULATIONS, Direction.OUTGOING);
			if (r == null) {
				Transaction tx = db.beginTx();
				try {
					Node subRef = db.createNode();
					subRef.setProperty(Neo4jDatabase.LABEL, "Sim Subref");
					subRef.setProperty(KEY_COUNTER, 0L);
					r = db.getReferenceNode().createRelationshipTo(subRef,
							SubRefs.SIMULATIONS);
					tx.success();
				} finally {
					tx.finish();
				}
			}
			return r.getEndNode();
		}

		private synchronized long getNextId() {
			Long counter = Long.parseLong(getSubRefNode().getProperty(
					KEY_COUNTER, 0L).toString());
			getSubRefNode().setProperty(KEY_COUNTER, new Long(counter + 1));
			return counter;
		}
	}

	SimulationNode(Node underlyingNode) {
		super(underlyingNode);
	}

	@Override
	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

	@Override
	public String getClassName() {
		return (String) this.getProperty(KEY_CLASSNAME);
	}

	@Override
	public long getCreatedAt() {
		return (Long) this.getProperty(KEY_CREATED_AT);
	}

	@Override
	public long getStartedAt() {
		return (Long) this.getProperty(KEY_STARTED_AT, 0);
	}

	@Override
	public void setStartedAt(long time) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.setProperty(KEY_STARTED_AT, time);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public long getFinishedAt() {
		return (Long) this.getProperty(KEY_FINISHED_AT, 0);
	}

	@Override
	public void setFinishedAt(long time) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.setProperty(KEY_FINISHED_AT, time);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public PersistentSimulation getParentSimulation() {
		Relationship parentRel = this.getSingleRelationship(
				SimulationRelationships.CONSISTS_OF, Direction.INCOMING);
		if (parentRel == null)
			return null;
		else
			return new SimulationNode(parentRel.getStartNode());
	}

	@Override
	public void setParentSimulation(PersistentSimulation parent) {
		if (!(parent instanceof SimulationNode)) {
			throw new IllegalArgumentException("Parent type of "
					+ parent.getClass().getCanonicalName()
					+ " is not compatible with SimulationNode");
		}
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			((SimulationNode) parent).createRelationshipTo(this,
					SimulationRelationships.CONSISTS_OF);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public void addChild(PersistentSimulation child) {
		if (!(child instanceof SimulationNode)) {
			throw new IllegalArgumentException("Parent type of "
					+ child.getClass().getCanonicalName()
					+ " is not compatible with SimulationNode");
		}
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.createRelationshipTo((SimulationNode) child,
					SimulationRelationships.CONSISTS_OF);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public String getState() {
		return (String) this
				.getSingleRelationship(SimulationRelationships.CURRENT_STATE,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationStateNode.KEY_NAME);
	}

	@Override
	public void setState(String newState) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			Relationship oldStateRel = null;
			try {
				oldStateRel = this.getSingleRelationship(
						SimulationRelationships.CURRENT_STATE,
						Direction.OUTGOING);
			} catch (RuntimeException e) {
				// > 1 current_state relationship
				for (Relationship r : this.getRelationships(
						SimulationRelationships.CURRENT_STATE,
						Direction.OUTGOING)) {
					if (oldStateRel == null)
						oldStateRel = r;
					else
						r.delete();
				}
			}
			Relationship prevState = this.createRelationshipTo(
					oldStateRel.getEndNode(),
					SimulationRelationships.PREVIOUS_STATE);
			prevState.setProperty(KEY_PREV_STATE_TIME, new Date().getTime());
			oldStateRel.delete();
			this.createRelationshipTo(
					SimulationStateNode.get(this.getGraphDatabase(), newState)
							.getUnderlyingNode(),
					SimulationRelationships.CURRENT_STATE);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public int getCurrentTime() {
		return (Integer) this
				.getSingleRelationship(SimulationRelationships.CURRENT_TIME,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationTimeNode.KEY_VALUE);
	}

	@Override
	public void setCurrentTime(int time) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			Relationship previousTime = this.getSingleRelationship(
					SimulationRelationships.CURRENT_TIME, Direction.OUTGOING);
			previousTime.delete();
			this.createRelationshipTo(
					SimulationTimeNode.get(getGraphDatabase(), time)
							.getUnderlyingNode(),
					SimulationRelationships.CURRENT_TIME);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public int getFinishTime() {
		return (Integer) this
				.getSingleRelationship(SimulationRelationships.FINISH_TIME,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationTimeNode.KEY_VALUE);
	}

	@Override
	public Map<String, Object> getParameters() {
		final Map<String, Object> params = new Hashtable<String, Object>();
		for (Relationship r : this
				.getRelationships(SimulationRelationships.PARAMETER)) {
			params.put(
					(String) r.getEndNode().getProperty(
							SimulationParameterNode.KEY_NAME),
					r.getProperty(KEY_PARAMETER_VALUE));
		}
		return params;
	}

	@Override
	public void addParameter(String name, Object value) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			Relationship paramRel = this.createRelationshipTo(
					SimulationParameterNode.get(getGraphDatabase(), name)
							.getUnderlyingNode(),
					SimulationRelationships.PARAMETER);
			paramRel.setProperty(KEY_PARAMETER_VALUE, value);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public long getID() {
		return (Long) this.getProperty(KEY_ID, Long.valueOf(0));
	}

}
