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
package uk.ac.imperial.presage2.db.sql;

import java.sql.SQLException;
import java.util.Properties;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.db.StorageService;
import uk.ac.imperial.presage2.db.Table.TableBuilder;

public abstract class SQLStorage extends SQLService implements StorageService,
		SQL, TimeDriven {

	long simulationID;
	Time time;

	protected SQLStorage(String driver, String connectionurl,
			Properties connectionProps) throws ClassNotFoundException {
		super(driver, connectionurl, connectionProps);
	}

	@Override
	public void start() throws Exception {
		super.start();
		if (logger.isDebugEnabled())
			logger.debug("Checking for core tables");
		if (!tableExists("simulations")) {
			logger.info("Creating simulations table");
			this.createTable("simulations")
					.addAutoIncrementColumn("ID", Long.class)
					.addColumn("parentID", Long.class, true)
					.addColumn("name", String.class)
					.addColumn("classname", String.class)
					.addColumn("state", String.class)
					.addColumn("currentTime", int.class, 0)
					.addColumn("finishTime", int.class)
					.addColumn("parameters", String.class)
					.addColumn("comment", String.class).addConstraints()
					.addIndex("parentID").commit();
		}

	}

	@Override
	public long getSimulationId() {
		return simulationID;
	}

	@Override
	public void setSimulationId(long id) {
		this.simulationID = id;
	}

	@Override
	public int getTime() {
		if (time != null)
			return time.intValue();
		return 0;
	}

	@Override
	public void setTime(Time t) {
		time = t;
	}

	@Override
	public void incrementTime() {
		time.increment();
	}

	@Override
	public void insertSimulation(RunnableSimulation sim) {
		if (isStarted()) {
			try {
				this.simulationID = insertInto("simulations")
						.addColumn("name", sim.getClass().getSimpleName())
						.addColumn("classname",
								sim.getClass().getCanonicalName())
						.addColumn("state", sim.getState())
						.addColumn("currentTime",
								sim.getCurrentSimulationTime().intValue())
						.addColumn("finishTime",
								sim.getSimulationFinishTime().intValue())
						.addColumn("parameters", sim.getParameters().toString())
						.getInsertedId();
			} catch (SQLException e) {
				throw new RuntimeException("Could not insert simulation", e);
			}
		}
		throw new RuntimeException("Database not started yet.");
	}

	@Override
	public TableBuilder buildTable(String tableName) {
		return new SQLTable.SQLTableBuilder(tableName, this);
	}

}
