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
		// This section loads JDO support if the correct module is on the classpath.
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
	@SuppressWarnings("unused")
	private static Properties loadProperties() throws Exception {
		 Properties properties = new Properties();
		 ClassLoader loader = SimulatorModule.class.getClassLoader();
		 URL url = loader.getResource("config.properties");
		 properties.load(url.openStream());
		 return properties;
	}

}
