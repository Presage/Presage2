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
package uk.ac.imperial.presage2.core.db;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;

public abstract class DatabaseModule extends AbstractModule {

	private static final Logger logger = Logger.getLogger(DatabaseModule.class);

	/**
	 * Load a database module from settings in the db.properties file. The file
	 * should specify the <code>module</code> property with the class name of
	 * the module to load. Other properties will be passed the module's
	 * constructor.
	 * 
	 * @return {@link DatabaseModule} defined in db.properties. null if the file
	 *         does not exist or there is an exception raise in the process.
	 */
	public static DatabaseModule load() {
		Properties dbProp = new Properties();
		try {
			dbProp.load(DatabaseModule.class.getClassLoader()
					.getResource("db.properties").openStream());
		} catch (IOException e) {
			logger.info(
					"Could not find db.properties, no database will be loaded.");
			return null;
		} catch (NullPointerException e) {
			logger.info(
					"Could not find db.properties, no database will be loaded.");
			return null;
		}

		String dbModule = null;
		try {
			logger.info("Using database settings from db.properties.");
			dbModule = dbProp.getProperty("module").trim();
			Class<? extends DatabaseModule> module = Class.forName(dbModule)
					.asSubclass(DatabaseModule.class);
			// look for suitable ctor, either Properties parameter or default
			Constructor<? extends DatabaseModule> ctor;
			try {
				ctor = module.getConstructor(Properties.class);
				return ctor.newInstance(dbProp);
			} catch (NoSuchMethodException e) {
				ctor = module.getConstructor();
				return ctor.newInstance();
			}
		} catch (NullPointerException e) {
			logger.warn(
					"db.properties missing 'module' property, cannot load database.",
					e);
		} catch (ClassNotFoundException e) {
			logger.warn(
					"Module specified in db.properties could not be found on classpath ("
							+ dbModule + ")", e);
		} catch (ClassCastException e) {
			logger.warn("Module class specified is not a DatabaseModule!", e);
		} catch (NoSuchMethodException e) {
			logger.warn("Could not find suitable constructor for the module "
					+ dbModule, e);
		} catch (IllegalArgumentException e) {
			logger.warn("Could not load database module", e);
		} catch (InstantiationException e) {
			logger.warn("Could not load database module", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not load database module", e);
		} catch (InvocationTargetException e) {
			logger.warn("Could not load database module", e);
		}
		return null;
	}

}
