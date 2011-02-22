/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * <p>Global simulator level guice bindings</p>
 * 
 * <p>Currently these are:
 * 	<ul><li>Database connection (via config.properties)</li></ul>
 * </p>
 * 
 * 
 * @author Sam Macbeth
 *
 */
public class SimulatorModule extends AbstractModule {

	private static final Logger logger = Logger.getLogger(SimulatorModule.class);
	
	@Override
	protected void configure() {
		// get properties for database binding
		try {
			Properties props = loadProperties();
			Names.bindProperties(binder(), props);
		} catch (Exception e) {
			 logger.fatal("Unable to retrieve simulation config properties from file config.properties", e);
		}
		
		bind(Connection.class).toProvider(ConnectionProvider.class);
	}
	
	/**
	 * <p>Pulls simulator config from a properties file and returns them.</p>
	 * @return
	 * @throws Exception
	 */
	private static Properties loadProperties() throws Exception {
		 Properties properties = new Properties();
		 ClassLoader loader = SimulatorModule.class.getClassLoader();
		 URL url = loader.getResource("config.properties");
		 properties.load(url.openStream());
		 return properties;
	}
	
	private class ConnectionProvider implements Provider<Connection> {

		private String connector;
		private String url;
		private String user;
		private String password;
		
		@SuppressWarnings("unused")
		@Inject
		public ConnectionProvider(@Named("database.jdbcconnector") String connector,
				@Named("database.url") String url,
				@Named("database.username") String user,
				@Named("database.password") String password) {
			this.connector = connector;
			this.url = url;
			this.user = user;
			this.password = password;
		}
		
		@Override
		public Connection get() {
			try {
				// init jdbc connector.
				Class.forName(this.connector);
				// create Properties for connection
				Properties dbinfo = new Properties();
				dbinfo.put("user", this.user);
				dbinfo.put("password", this.password);
				return DriverManager.getConnection(this.url, dbinfo);
			} catch(ClassNotFoundException e) {
				logger.fatal("Could not load JDBC connector specified by: "+this.connector, e);
				return null;
			} catch (SQLException e) {
				logger.fatal("Could not connect to database.", e);
				return null;
			}
		}

	}

}
