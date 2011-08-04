package uk.ac.imperial.presage2.core.db.nodes;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import uk.ac.imperial.presage2.core.db.GraphDB.BaseRelationships;

public class SimulationParameterNode extends NodeDelegate {

	enum ParameterRelationships implements RelationshipType {
		PARAMETER_TYPE
	}
	
	static final String KEY_NAME = "name";
	
	public static SimulationParameterNode get(GraphDatabaseService db, String parameterName) {
		Node base = db
				.getReferenceNode()
				.getSingleRelationship(BaseRelationships.SIMULATION_PARAMETERS,
						Direction.OUTGOING).getEndNode();
		for (Relationship r : base.getRelationships(ParameterRelationships.PARAMETER_TYPE)) {
			if (r.getEndNode().getProperty(KEY_NAME).toString().equalsIgnoreCase(parameterName)) {
				return new SimulationParameterNode(r.getEndNode());
			}
		}
		Transaction tx = db.beginTx();
		SimulationParameterNode s = null;
		try {
			Node n = db.createNode();
			n.setProperty("name", parameterName);
			base.createRelationshipTo(n, ParameterRelationships.PARAMETER_TYPE);
			s = new SimulationParameterNode(n);
			tx.success();
		} finally {
			tx.finish();
		}
		return s;
	}
	
	protected SimulationParameterNode(Node delegate) {
		super(delegate);
	}
	
	public String getName() {
		return (String) this.getProperty(KEY_NAME);
	}

}
