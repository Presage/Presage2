package uk.ac.imperial.presage2.db.jdo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.db.DatabaseService;

import com.google.inject.AbstractModule;

/**
 * <p>A Guice module which provides guice bindings for JDO entities.</p>
 * 
 * <p>By default we look for the file <code>jdo.properties</code> in the current
 * classpath to provide the {@link Properties} to be passed to {@link JDOHelper#getPersistenceManagerFactory(java.util.Map)}
 * (in {@link JDODatabaseService}). Otherwise we provide bindings to use an embedded
 * MYSQL server using the Connector/MXJ driver which is creating in a 'db' folder in the
 * current working directory.</p>
 * 
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
	
	protected Properties jdoProps = new Properties();
	
	@Override
	protected void configure() {
		ClassLoader loader = JDOModule.class.getClassLoader();
		 try {
			 // look for defined jdo properties
			 URL url = loader.getResource("jdo.properties");
			 jdoProps.load(url.openStream());
			 logger.info("Using custom JDO properties from jdo.properties.");
			 bind(DatabaseService.class).to(JDODatabaseService.class);
			 bind(PersistenceManager.class).toProvider(JDODatabaseService.class);
		 } catch(NullPointerException e) {
			 // if no properties found, use defaults
			 logger.info("Using default JDO properties (mysql Connector/MXJ)");
			 useMXJ();
		 } catch(IOException e) {
			 logger.info("Error loading custom JDO properties from jdo.properties, using defaults.");
			 useMXJ();
		 }
		 bind(Properties.class).annotatedWith(JDO.class).toInstance(jdoProps);
	}
	
	protected void useMXJ() {
		bind(DatabaseService.class).to(MxjJDODatabaseService.class);
		bind(PersistenceManager.class).toProvider(MxjJDODatabaseService.class);
		bind(File.class).annotatedWith(MXJDataDir.class).toInstance(new File(MXJ_DEFAULTS.DIR));
		
		jdoProps.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		jdoProps.setProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql:mxj://localhost"
			    +"/"+ MXJ_DEFAULTS.DBNAME 
				+"?"+ "server.basedir=" + MXJ_DEFAULTS.DIR
				+ "&" + "createDatabaseIfNotExist=true"
				);
		jdoProps.setProperty("javax.jdo.option.ConnectionDriverName", "com.mysql.jdbc.Driver");
		jdoProps.setProperty("javax.jdo.option.ConnectionUserName", MXJ_DEFAULTS.USERNAME);
		jdoProps.setProperty("javax.jdo.option.ConnectionPassword", MXJ_DEFAULTS.PASSWORD);
		
	}

}
