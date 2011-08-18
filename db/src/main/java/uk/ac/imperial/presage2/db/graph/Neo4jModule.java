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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.GraphDB;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;

import com.google.inject.Singleton;

public class Neo4jModule extends DatabaseModule {

	private final static String PROPERTY_MODE = "mode";

	private final static String MODE_EMBEDDED = "embedded";
	private final static String PROPERTY_EMBEDDED_PATH = "embedded.path";
	private final static String DEFAULT_EMBEDDED_PATH = "var/presage.db";

	private final static String MODE_REST = "rest";
	private final static String PROPERTY_REST_URI = "rest.uri";
	private final static String DEFAULT_REST_URI = "http://localhost:7474/db/data";

	private final GraphDatabaseFactory dbFactory;

	public Neo4jModule() {
		super();
		dbFactory = new EmbeddedGraphDatabaseFactory(DEFAULT_EMBEDDED_PATH);
	}

	public Neo4jModule(Properties props) {
		super();
		String mode = props.getProperty(PROPERTY_MODE, MODE_EMBEDDED);
		if (mode.equalsIgnoreCase(MODE_REST)) {
			dbFactory = new RestGraphDatabaseFactory(props.getProperty(
					PROPERTY_REST_URI, DEFAULT_REST_URI));
		} else {
			dbFactory = new EmbeddedGraphDatabaseFactory(props.getProperty(
					PROPERTY_EMBEDDED_PATH, DEFAULT_EMBEDDED_PATH));
		}
	}

	@Override
	protected void configure() {
		bind(Neo4jDatabase.class).in(Singleton.class);
		bind(DatabaseService.class).to(Neo4jDatabase.class);
		bind(GraphDB.class).to(Neo4jDatabase.class);
		bind(GraphDatabaseService.class).toProvider(Neo4jDatabase.class);

		bind(PersistentAgentFactory.class).to(AgentNode.Factory.class);

		bind(GraphDatabaseFactory.class).toInstance(dbFactory);
	}

	class EmbeddedGraphDatabaseFactory implements GraphDatabaseFactory {

		private final Logger logger = Logger.getLogger(EmbeddedGraphDatabaseFactory.class);
		private final String databasePath;

		EmbeddedGraphDatabaseFactory(String databasePath) {
			this.databasePath = databasePath;
		}

		@Override
		public GraphDatabaseService create() {
			logger.info("Creating EmbeddedGraphDatabase using "+ databasePath);
			return new EmbeddedGraphDatabase(databasePath);
		}

	}

	class RestGraphDatabaseFactory implements GraphDatabaseFactory {

		private final Logger logger = Logger.getLogger(RestGraphDatabaseFactory.class);
		private final String databaseURI;

		RestGraphDatabaseFactory(String databaseURI) {
			this.databaseURI = databaseURI;
		}

		@Override
		public GraphDatabaseService create() {
			try {
				logger.info("Creating RestGraphDatabase with URI "+ databaseURI);
				return new RestGraphDatabase(new URI(databaseURI));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
