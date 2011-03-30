/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * <p>Global simulator level guice bindings</p>
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
		/*try {
			Properties props = loadProperties();
			Names.bindProperties(binder(), props);
		} catch (Exception e) {
			 logger.fatal("Unable to retrieve simulation config properties from file config.properties", e);
		}*/
		
		
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
	
	@Provides @JDO
	protected Properties bindJDOProperties() {
		Properties p = new Properties();
		p.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		p.setProperty("javax.jdo.option.ConnectionURL",  "");
		p.setProperty("javax.jdo.option.ConnectionDriverName", "");
		p.setProperty("javax.jdo.option.ConnectionUserName", "");
		p.setProperty("javax.jdo.option.ConnectionPassword", "");
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
