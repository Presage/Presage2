package uk.ac.imperial.presage2.db.graph;

import org.neo4j.graphdb.GraphDatabaseService;

public interface GraphDatabaseFactory {

	GraphDatabaseService create();

}