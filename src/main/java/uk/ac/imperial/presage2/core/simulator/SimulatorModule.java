/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

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
		try {
			Module dbModule = (Module) Class.forName("uk.ac.imperial.presage2.db.jdo.JDOModule").newInstance();
			logger.info("Found JDO storage module, loading JDO support.");
			install(dbModule);
		} catch(ClassNotFoundException e) {
			logger.info("No storage module found, running without db support.");
		} catch (InstantiationException e) {
			logger.error("Error loading JDO storage module.", e);
		} catch (IllegalAccessException e) {
			logger.error("Error loading JDO storage module.", e);
		}
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

}
