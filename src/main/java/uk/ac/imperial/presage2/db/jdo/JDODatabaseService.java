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
