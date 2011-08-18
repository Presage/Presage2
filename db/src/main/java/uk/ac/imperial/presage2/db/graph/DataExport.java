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
