package uk.ac.imperial.presage2.core.db;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

public interface GraphDB extends GraphDatabaseService {

	public enum BaseRelationships implements RelationshipType {
		SIMULATIONS,
		SIMULATION_STATES,
		SIMULATION_PARAMETERS,
		SIMULATION_TIMESTEPS,
		PLUGINS
	}
		
}
