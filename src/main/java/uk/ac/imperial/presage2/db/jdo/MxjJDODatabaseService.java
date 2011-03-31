package uk.ac.imperial.presage2.db.jdo;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mysql.management.driverlaunched.ServerLauncherSocketFactory;

@Singleton
public class MxjJDODatabaseService extends JDODatabaseService {

	private final Logger logger = Logger.getLogger(MxjJDODatabaseService.class);
	
	protected File mxjDatadir;
	
	@Inject
	public MxjJDODatabaseService(@JDO Properties jdoProps, @MXJDataDir File mxjDatadir) {
		super(jdoProps);
		this.mxjDatadir = mxjDatadir;
	}

	@Override
	public void stop() {
		super.stop();
		// force mysql server shutdown
		if(logger.isDebugEnabled())
			logger.debug("Shutting down embedded mysql server...");
		ServerLauncherSocketFactory.shutdown(mxjDatadir, null);
	}

}
