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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import uk.ac.imperial.presage2.core.db.GraphDB.BaseRelationships;

public class SimulationStateNode extends NodeDelegate {

	enum StateRelationships implements RelationshipType {
		STATE
	}

	static final String KEY_NAME = "name";

	public static SimulationStateNode get(GraphDatabaseService db,
			final String state) {
		Node base = db
				.getReferenceNode()
				.getSingleRelationship(BaseRelationships.SIMULATION_STATES,
						Direction.OUTGOING).getEndNode();
		for (Relationship r : base.getRelationships(StateRelationships.STATE)) {
			if (r.getEndNode().getProperty(KEY_NAME).toString().equalsIgnoreCase(state)) {
				return new SimulationStateNode(r.getEndNode());
			}
		}
		// create new node if it doesn't exist yet
		Transaction tx = db.beginTx();
		SimulationStateNode s = null;
		try {
			Node n = db.createNode();
			n.setProperty(KEY_NAME, state);
			base.createRelationshipTo(n, StateRelationships.STATE);
			s = new SimulationStateNode(n);
			tx.success();
		} finally {
			tx.finish();
		}
		return s;
	}

	SimulationStateNode(Node underlyingNode) {
		super(underlyingNode);
	}

	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

}
