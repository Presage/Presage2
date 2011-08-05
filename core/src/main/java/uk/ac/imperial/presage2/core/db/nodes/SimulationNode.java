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
package uk.ac.imperial.presage2.core.db.nodes;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import uk.ac.imperial.presage2.core.db.GraphDB.BaseRelationships;

public class SimulationNode extends NodeDelegate {

	enum SimulationRelationships implements RelationshipType {
		SIMULATION, CONSISTS_OF, CURRENT_STATE, PARAMETER, CURRENT_TIME, FINISH_TIME
	}

	private static final String KEY_NAME = "name";
	private static final String KEY_CLASSNAME = "classname";
	private static final String KEY_CREATED_AT = "createdAt";
	private static final String KEY_STARTED_AT = "startedAt";
	private static final String KEY_FINISHED_AT = "finishedAt";

	private static final String KEY_PARAMETER_VALUE = "value";

	public static SimulationNode create(GraphDatabaseService db, String name,
			String classname, String state, int currentTime, int finishTime) {
		Transaction tx = db.beginTx();
		SimulationNode s = null;
		try {
			Node n = db.createNode();
			n.setProperty(KEY_NAME, name);
			n.setProperty(KEY_CLASSNAME, classname);
			n.setProperty(KEY_CREATED_AT, new Date().getTime());
			db.getReferenceNode()
					.getSingleRelationship(BaseRelationships.SIMULATIONS,
							Direction.OUTGOING)
					.getEndNode()
					.createRelationshipTo(n, SimulationRelationships.SIMULATION);
			n.createRelationshipTo(SimulationStateNode.get(db, state)
					.getUnderlyingNode(), SimulationRelationships.CURRENT_STATE);
			n.createRelationshipTo(SimulationTimeNode.get(db, currentTime)
					.getUnderlyingNode(), SimulationRelationships.CURRENT_TIME);
			n.createRelationshipTo(SimulationTimeNode.get(db, finishTime)
					.getUnderlyingNode(), SimulationRelationships.FINISH_TIME);
			s = new SimulationNode(n);
			tx.success();
		} finally {
			tx.finish();
		}
		return s;
	}

	SimulationNode(Node underlyingNode) {
		super(underlyingNode);
	}

	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

	public String getClassName() {
		return (String) this.getProperty(KEY_CLASSNAME);
	}

	public long getCreatedAt() {
		return (Long) this.getProperty(KEY_CREATED_AT);
	}

	public long getStartedAt() {
		return (Long) this.getProperty(KEY_STARTED_AT, 0);
	}

	public void setStartedAt(long time) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.setProperty(KEY_STARTED_AT, time);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public long getFinishedAt() {
		return (Long) this.getProperty(KEY_FINISHED_AT, 0);
	}

	public void setFinishedAt(long time) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.setProperty(KEY_FINISHED_AT, time);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public SimulationNode getParentSimulation() {
		Relationship parentRel = this.getSingleRelationship(
				SimulationRelationships.CONSISTS_OF, Direction.INCOMING);
		if (parentRel == null)
			return null;
		else
			return new SimulationNode(parentRel.getStartNode());
	}

	public void setParentSimulation(SimulationNode parent) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			parent.createRelationshipTo(this,
					SimulationRelationships.CONSISTS_OF);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public void addChild(SimulationNode child) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.createRelationshipTo(child,
					SimulationRelationships.CONSISTS_OF);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public String getState() {
		return (String) this
				.getSingleRelationship(SimulationRelationships.CURRENT_STATE,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationStateNode.KEY_NAME);
	}

	public void setState(String newState) {
		Transaction tx = this.getGraphDatabase().beginTx();
		try {
			this.getSingleRelationship(SimulationRelationships.CURRENT_STATE,
					Direction.OUTGOING).delete();
			this.createRelationshipTo(
					SimulationStateNode.get(this.getGraphDatabase(), newState)
							.getUnderlyingNode(),
					SimulationRelationships.CURRENT_STATE);
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public int getCurrentTime() {
		return (Integer) this
				.getSingleRelationship(SimulationRelationships.CURRENT_TIME,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationTimeNode.KEY_VALUE);
	}

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

	public int getFinishTime() {
		return (Integer) this
				.getSingleRelationship(SimulationRelationships.FINISH_TIME,
						Direction.OUTGOING).getEndNode()
				.getProperty(SimulationTimeNode.KEY_VALUE);
	}

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

}
