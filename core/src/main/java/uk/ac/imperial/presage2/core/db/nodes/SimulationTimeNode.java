package uk.ac.imperial.presage2.core.db.nodes;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import uk.ac.imperial.presage2.core.db.GraphDB.BaseRelationships;

public class SimulationTimeNode extends NodeDelegate {

	enum TimeRelationships implements RelationshipType {
		TIME
	}

	static final String KEY_VALUE = "time";

	public static SimulationTimeNode get(GraphDatabaseService db, final int time) {
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
				Node base = db
						.getReferenceNode()
						.getSingleRelationship(
								BaseRelationships.SIMULATION_TIMESTEPS,
								Direction.OUTGOING).getEndNode();
				Node n = db.createNode();
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
