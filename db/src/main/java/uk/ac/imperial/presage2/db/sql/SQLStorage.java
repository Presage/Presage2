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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.Table.TableBuilder;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.EndOfTimeCycle;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation.SimulationState;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.simulator.ThreadPool;

import com.google.inject.Inject;

/**
 * Implementation of a {@link StorageService} backed by a {@link SQLService}.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class SQLStorage extends SQLService implements StorageService,
		SQL, TimeDriven, Runnable {

	long simulationID;
	Time time;
	protected RunnableSimulation sim;

	/**
	 * {@link Queue} of deferred {@link PreparedStatement}s to execute.
	 */
	protected final Queue<PreparedStatement> queryQueue = new ConcurrentLinkedQueue<PreparedStatement>();

	private ThreadPool threadPool;
	private boolean finishUp = false;

	protected SQLStorage(String driver, String connectionurl,
			Properties connectionProps) throws ClassNotFoundException {
		super(driver, connectionurl, connectionProps);
	}

	@Inject
	public void registerTimeDriven(Scenario s, Time t) {
		s.addTimeDriven(this);
		time = t;
	}

	@Inject
	public void subscribeToEvents(EventBus eventBus) {
		eventBus.subscribe(this);
	}

	@Inject
	public void getThreadPool(ThreadPool tp) {
		this.threadPool = tp;
	}

	/**
	 * Starts a SQL connection and also creates a 'simulations' table if it
	 * doesn't already exists.
	 */
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
					.addColumn("createdAt", java.sql.Time.class)
					.addColumn("startedAt", java.sql.Time.class, true)
					.addColumn("finishedAt", java.sql.Time.class, true)
					.addColumn("parameters", String.class)
					.addColumn("comment", String.class, "").addConstraints()
					.addIndex("parentID").commit();
		} else {
			logger.info("Found simulations table");
		}
		threadPool.submit(this);
	}

	@Override
	public synchronized void stop() {
		this.finishUp = true;
		notifyAll();
		super.stop();
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

	/**
	 * Inserts the {@link RunnableSimulation} into the 'simulations' table.
	 */
	@Override
	public void insertSimulation(RunnableSimulation sim) {
		this.sim = sim;
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
						.addColumn("createdAt", new Date())
						.addColumn("startedAt", new Date())
						.addColumn("parameters", parametersToString(sim))
						.getInsertedId();
				return;
			} catch (SQLException e) {
				throw new RuntimeException("Could not insert simulation", e);
			}
		} else
			throw new RuntimeException("Database not started yet.");
	}

	private String parametersToString(RunnableSimulation sim) {
		StringBuilder b = new StringBuilder("{");
		for (String s : sim.getParameters().keySet()) {
			b.append(s);
			b.append("=");
			b.append(sim.getParameter(s));
			b.append(",");
		}
		b.append("}");
		return b.toString();
	}

	@Override
	public void updateSimulation() {
		if (this.sim != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Updating simulation data in db.");
			}
			try {
				UpdateQueryBuilder up = update("simulations").set("state",
						sim.getState().name()).set("currentTime",
						sim.getCurrentSimulationTime().intValue());
				if (sim.getState() == SimulationState.COMPLETE)
					up.set("finishedAt", new Date());
				up.whereEquals("ID", this.simulationID).commit();
			} catch (SQLException t) {
				throw new RuntimeException("Could not update simulation", t);
			}
		}
	}

	/**
	 * Event listener to update simulation information at the end of each time
	 * cycle.
	 * 
	 * @param e
	 */
	@EventListener
	public void onNewTimeCycle(EndOfTimeCycle e) {
		updateSimulation();
	}

	@Override
	public TableBuilder buildTable(String tableName) {
		return new SQLTable.SQLTableBuilder(tableName, this);
	}

	/**
	 * Execute a query immediately.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	protected void executeQuery(String query) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("Executing Query: " + query);
		Statement s = this.conn.createStatement();
		s.execute(query);
		s.close();
	}

	/**
	 * Perform an insert query with a string to create a
	 * {@link PreparedStatement} and data values to put into it. Returns an
	 * inserted ID from the result set.
	 * 
	 * @param preparedStatement
	 * @param values
	 * @return ID at which the data was inserted (if applicable).
	 * @throws SQLException
	 */
	protected long insert(String preparedStatement, Object... values)
			throws SQLException {
		PreparedStatement s = this.conn.prepareStatement(preparedStatement,
				Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < values.length; i++) {
			s.setObject(i + 1, values[i]);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Executing Query: " + preparedStatement
					+ " parameters: (" + commaSeparatedObjectArray(values)
					+ ")");
		}
		s.execute();
		ResultSet rs = s.getGeneratedKeys();
		rs.next();
		long id = rs.getLong(1);
		s.close();
		return id;
	}

	/**
	 * Submit a prepared statement to be executed at some time in the future.
	 * 
	 * @param preparedStatement
	 * @param values
	 * @throws SQLException
	 */
	protected synchronized void deferredQuery(String preparedStatement,
			Object... values) throws SQLException {
		this.queryQueue.add(prepareStatement(preparedStatement, values));
		notify();
	}

	private PreparedStatement prepareStatement(String preparedStatement,
			Object... values) throws SQLException {
		PreparedStatement s = this.conn.prepareStatement(preparedStatement);
		for (int i = 0; i < values.length; i++) {
			s.setObject(i + 1, values[i]);
		}
		return s;
	}

	protected String commaSeparatedObjectArray(Object... array) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			s.append(array[i]);
			if (i + 1 < array.length)
				s.append(" , ");
		}
		return s.toString();
	}

	/**
	 * Process queries queued in {@link #queryQueue} until {@link #finishUp} is
	 * set to true.
	 */
	@Override
	public void run() {

		while (true) {
			synchronized (this) {
				while (queryQueue.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException e) {
						logger.warn("QueryExecutor was interrupted", e);
					}
				}
			}

			while (!queryQueue.isEmpty()) {
				try {
					PreparedStatement s = queryQueue.poll();
					s.execute();
					if (logger.isDebugEnabled()) {
						logger.debug("Executing Query: " + s.toString());
					}
				} catch (SQLException e) {
					logger.warn("Error processing query.", e);
				}
			}

			if (finishUp && queryQueue.isEmpty())
				break;
		}

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

}
