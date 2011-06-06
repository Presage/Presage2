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
