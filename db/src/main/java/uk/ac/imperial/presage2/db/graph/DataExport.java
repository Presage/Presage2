package uk.ac.imperial.presage2.db.graph;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import uk.ac.imperial.presage2.db.graph.AgentNode.AgentRelationships;
import uk.ac.imperial.presage2.db.graph.TransientAgentStateNode.TransientAgentStateRel;
import uk.ac.imperial.presage2.db.graph.export.GEXFExport;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DataExport {

	final Neo4jDatabase db;

	@Inject
	protected DataExport(Neo4jDatabase db) {
		this.db = db;
	}

	public Traverser getSimulationSubGraph(long simulationID) {
		SimulationNode n = (SimulationNode) this.db.getSimulationFactory().get(
				simulationID);
		TraversalDescription trav = Traversal
				.description()
				.breadthFirst()
				.uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
				.relationships(AgentRelationships.PARTICIPANT_IN,
						Direction.INCOMING)
				.relationships(AgentRelationships.TRANSIENT_STATE)
				.relationships(TransientAgentStateRel.NEXT_STATE)
				.relationships(DynamicRelationshipType.withName("SENT_MESSAGE"))
				.relationships(TransientAgentStateRel.AT_TIME,
						Direction.OUTGOING);
		return trav.traverse(n);
	}
	
	public Node getSimulationNode() {
		return ((SimulationNode) db.getSimulation()).getUnderlyingNode();
	}
	
	public RelationshipType getParticipantInRelationship() {
		return AgentRelationships.PARTICIPANT_IN;
	}

}
