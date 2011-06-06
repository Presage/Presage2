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

package uk.ac.imperial.presage2.db.jdo;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import uk.ac.imperial.presage2.db.DatabaseService;

@Singleton
public class JDODatabaseService implements DatabaseService, Provider<PersistenceManager> {

	private final Logger logger = Logger.getLogger(JDODatabaseService.class);
	
	protected PersistenceManagerFactory pmf = null;
	
	protected Properties jdoProps;
	
	@Inject
	public JDODatabaseService(@JDO Properties jdoProps) {
		super();
		this.jdoProps = jdoProps;
	}

	@Override
	public void start() {
		if(pmf == null) {
			logger.info("Starting JDO database service...");
			try {
				pmf = JDOHelper.getPersistenceManagerFactory(jdoProps);
			} catch(Exception e) {
				logger.error("Exception thrown when getting PersistenceManger", e);
			}
		}
	}

	@Override
	public void stop() {
		logger.info("Shutting down JDO database service...");
		pmf.close();
	}

	@Override
	public PersistenceManager get() {
		if(pmf == null) {
			this.start();
		}
		return pmf.getPersistenceManager();
	}

}
