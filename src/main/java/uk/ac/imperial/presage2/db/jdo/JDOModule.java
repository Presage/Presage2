/**
 * 
 */
package uk.ac.imperial.presage2.db.jdo;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author sm1106
 *
 */
public class JDOModule extends AbstractModule {

	private static final Logger logger = Logger.getLogger(JDOModule.class);
	
	protected static class MXJ_DEFAULTS {
		public static int PORT = 3336;
		public static String DBNAME = "presage2";
		public static String DIR = System.getProperty("user.dir") + "/db";
		public static String USERNAME = "root";
		public static String PASSWORD = "";
	}
	
	@Override
	protected void configure() {
		// Our bindings are done through providers so we don't need anything here.
	}
	
	@SuppressWarnings("unused")
	@Provides @JDO
	private static Properties loadProperties() {
		 Properties properties = new Properties();
		 ClassLoader loader = JDOModule.class.getClassLoader();
		 try {
			 // look for defined jdo properties
			 URL url = loader.getResource("jdo.properties");
			 properties.load(url.openStream());
			 logger.info("Using custom JDO properties from jdo.properties.");
			 return properties;
		 } catch(NullPointerException e) {
			 // if no properties found, use defaults
			 logger.info("Using default JDO properties (mysql Connector/MXJ)");
			 return defaultJDOProperties();
		 } catch(IOException e) {
			 logger.info("Error loading custom JDO properties from jdo.properties, using defaults.");
			 return defaultJDOProperties();
		 }
	}
	
	protected static Properties defaultJDOProperties() {
		Properties p = new Properties();
		p.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		p.setProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql:mxj://localhost"
			    +"/"+ MXJ_DEFAULTS.DBNAME 
				+"?"+ "server.basedir=" + MXJ_DEFAULTS.DIR
				+ "&" + "createDatabaseIfNotExist=true"
				);
		p.setProperty("javax.jdo.option.ConnectionDriverName", "com.mysql.jdbc.Driver");
		p.setProperty("javax.jdo.option.ConnectionUserName", MXJ_DEFAULTS.USERNAME);
		p.setProperty("javax.jdo.option.ConnectionPassword", MXJ_DEFAULTS.PASSWORD);
		return p;
	}
	
	@Provides @Singleton
	PersistenceManagerFactory providePersistenceManagerFactory(@JDO Properties jdoProps) {
		return JDOHelper.getPersistenceManagerFactory(jdoProps);
	}
	
	@Provides
	PersistenceManager providePersistence(PersistenceManagerFactory pmf) {
		return pmf.getPersistenceManager();
		
	}

}
