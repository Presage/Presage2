package uk.ac.imperial.presage2.core.db;

import com.google.inject.Singleton;

public class Neo4jModule extends DatabaseModule {

	public Neo4jModule() {
		super();
	}
	
	@Override
	protected void configure() {
		bind(Neo4jDatabase.class).in(Singleton.class);
		bind(DatabaseService.class).to(Neo4jDatabase.class);
		bind(GraphDB.class).to(Neo4jDatabase.class);
	}

}
