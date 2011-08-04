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
			if (r.getEndNode().getProperty(KEY_NAME).toString() == state) {
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
