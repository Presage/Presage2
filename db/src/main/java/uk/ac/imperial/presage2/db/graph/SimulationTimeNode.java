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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import uk.ac.imperial.presage2.db.graph.Neo4jDatabase.SubRefs;

public class SimulationTimeNode extends NodeDelegate {

	enum TimeRelationships implements RelationshipType {
		TIME
	}

	static final String KEY_VALUE = "time";

	static SimulationTimeNode get(GraphDatabaseService db, final int time) {
		Index<Node> timeIndex = db.index().forNodes("timesteps");
		Node timeNode = timeIndex.get(KEY_VALUE, time).getSingle();
		if (timeNode != null) {
			// found in index
			return new SimulationTimeNode(timeNode);
		} else {
			// not found, create new
			Transaction tx = db.beginTx();
			SimulationTimeNode t = null;
			try {
				Node base = Neo4jDatabase.getSubRefNode(db, SubRefs.SIMULATION_TIMESTEPS);
				Node n = db.createNode();
				n.setProperty(Neo4jDatabase.LABEL, "Time: "+time);
				n.setProperty(KEY_VALUE, time);
				base.createRelationshipTo(n, TimeRelationships.TIME);
				t = new SimulationTimeNode(n);
				timeIndex.add(n, KEY_VALUE, time);
				tx.success();
			} finally {
				tx.finish();
			}
			return t;
		}
	}

	SimulationTimeNode(Node underlyingNode) {
		super(underlyingNode);
	}

	public int getValue() {
		return (Integer) this.getProperty(KEY_VALUE);
	}

}
