package uk.ac.imperial.presage2.db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.TupleStorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SqlStorage extends TupleStorageService implements
		DatabaseService {

	protected final Logger logger = Logger.getLogger(SqlStorage.class);
	protected Properties jdbcInfo;
	protected Connection conn = null;
	QueryExecutor exec;
	Set<String> simKeys = new HashSet<String>();

	@Inject
	public SqlStorage(@Named(value = "sql.info") Properties jdbcInfo) {
		super();
		this.jdbcInfo = jdbcInfo;
		if (jdbcInfo.getProperty("driver", "").equalsIgnoreCase(
				"org.postgresql.Driver"))
			Sql.dialect = Dialect.POSTGRESQL;
		else
			Sql.dialect = Dialect.MYSQL;
		simKeys.addAll(Arrays.asList(reservedKeys));
	}

	@Override
	public void start() throws Exception {
		if (conn == null) {
			try {
				Class.forName(jdbcInfo.getProperty("driver",
						"com.mysql.jdbc.Driver"));
				this.conn = DriverManager.getConnection(
						jdbcInfo.getProperty("url"), jdbcInfo);
			} catch (SQLException e) {
				logger.fatal(
						"Exception while attempting to connect to jdbc db.", e);
				throw e;
			} catch (ClassNotFoundException e) {
				logger.fatal("JDBC driver not found.", e);
				throw e;
			}
			initTables();
			// start batch thread
			exec = new QueryExecutor(10);
			exec.start();
		}
	}

	@Override
	public boolean isStarted() {
		return conn != null;
	}

	@Override
	public void stop() {
		if (conn != null) {
			exec.stop();
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				logger.warn("Exception when closing db.", e);
			}
		}
	}

	protected void initTables() {
		Statement createSimulations = null;
		Statement createParameters = null;
		Statement createTuples = null;
		try {
			createSimulations = conn.createStatement();
			createSimulations.execute(Sql.createSimulationTable());
			createSimulations.close();
			createParameters = conn.createStatement();
			createParameters.execute(Sql.createParametersTable());
			createParameters.close();
			createTuples = conn.createStatement();
			createTuples.execute(Sql.formatQuery("CREATE TABLE IF NOT EXISTS `tuples` ("
					+ "`sid` bigint NOT NULL," + "`key` varchar(255) NOT NULL,"
					+ "`val_str` varchar(512) NULL," + "`val_int` int NULL,"
					+ "`val_dbl` float NULL," + "PRIMARY KEY (`sid`, `key`));"));
			createTuples.execute(Sql.formatQuery("CREATE TABLE IF NOT EXISTS `tuples_t` ("
					+ "`sid` bigint NOT NULL," + "`key` varchar(255) NOT NULL,"
					+ "	`t` int NOT NULL," + "`val_str` varchar(512) NULL,"
					+ "	`val_int` int NULL," + "`val_dbl` float NULL,"
					+ "	PRIMARY KEY (`sid`, `key`, `t`));"));
			createTuples.execute(Sql.formatQuery("CREATE TABLE IF NOT EXISTS `tuples_ag` ("
					+ "`sid` bigint NOT NULL," + "`key` varchar(255) NOT NULL,"
					+ "`aid` varchar(36) NOT NULL,"
					+ "`val_str` varchar(512) NULL," + "`val_int` int NULL,"
					+ "`val_dbl` float NULL,"
					+ "PRIMARY KEY (`sid`, `key`, `aid`));"));
			createTuples.execute(Sql.formatQuery("CREATE TABLE IF NOT EXISTS `tuples_ag_t` ("
					+ "`sid` bigint NOT NULL," + "`key` varchar(255) NOT NULL,"
					+ "`aid` varchar(36) NOT NULL," + "`t` int NOT NULL,"
					+ "`val_str` varchar(512) NULL," + "`val_int` int NULL,"
					+ "`val_dbl` float NULL,"
					+ "PRIMARY KEY (`sid`, `key`, `aid`, `t`));"));
			createTuples.close();
		} catch (SQLException e) {
			logger.fatal("Couldn't create tables", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public PersistentSimulation createSimulation(String name, String classname,
			String state, int finishTime) {
		PreparedStatement createSimulation = null;
		ResultSet generatedKeys = null;
		try {
			createSimulation = conn.prepareStatement(
					Sql.insertIntoSimulations(),
					Statement.RETURN_GENERATED_KEYS);
			createSimulation.setString(1, name);
			createSimulation.setString(2, state);
			createSimulation.setString(3, classname);
			createSimulation.setInt(4, finishTime);
			createSimulation.setLong(5, new Date().getTime());
			createSimulation.executeUpdate();
			generatedKeys = createSimulation.getGeneratedKeys();
			if (generatedKeys.next()) {
				long simId = generatedKeys.getLong(1);
				Simulation s = new Simulation(simId);
				storeParameter(simId, KEYS.finishTime,
						Integer.toString(finishTime));
				return s;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (generatedKeys != null)
				try {
					generatedKeys.close();
				} catch (SQLException e) {
				}
			if (createSimulation != null)
				try {
					createSimulation.close();
				} catch (SQLException e) {
				}
		}
		return null;
	}

	@Override
	public List<Long> getSimulations() {
		PreparedStatement getSimulations = null;
		ResultSet simRow = null;
		List<Long> simIds = new LinkedList<Long>();
		try {
			getSimulations = conn.prepareStatement(Sql.getSimulations());
			simRow = getSimulations.executeQuery();
			while (simRow.next()) {
				simIds.add(simRow.getLong(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (getSimulations != null) {
				try {
					getSimulations.close();
				} catch (SQLException e) {
				}
			}
			if (simRow != null) {
				try {
					simRow.close();
				} catch (SQLException e) {
				}
			}
		}
		return simIds;
	}

	@Override
	protected long getNextId() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void storeParameter(long id, String key, String value) {
		PreparedStatement setParameter = null;
		try {
			setParameter = conn.prepareStatement(Sql.insertIntoParameters());
			setParameter.setLong(1, id);
			setParameter.setString(2, key);
			setParameter.setString(3, value);
			exec.submit(setParameter);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, String value) {
		PreparedStatement stmt = null;
		try {
			if (simKeys.contains(key)) {
				stmt = conn.prepareStatement(Sql
						.formatQuery("UPDATE simulations " + "SET `" + key
								+ "` = ? WHERE `id` = ?"));
				stmt.setString(1, value);
				stmt.setLong(2, id);
			} else {
				stmt = conn
						.prepareStatement(Sql
								.formatQuery("INSERT INTO tuples (`sid`,`key`,`val_str`) VALUE (?, ?, ?) ON DUPLICATE KEY UPDATE `val_str` = VALUES(`val_str`)"));
				stmt.setLong(1, id);
				stmt.setString(2, key);
				stmt.setString(3, value);
			}
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, int value) {
		PreparedStatement stmt = null;
		try {
			if (simKeys.contains(key)) {
				stmt = conn.prepareStatement(Sql
						.formatQuery("UPDATE simulations " + "SET `" + key
								+ "` = ? WHERE `id` = ?"));
				stmt.setInt(1, value);
				stmt.setLong(2, id);
			} else {
				stmt = conn
						.prepareStatement(Sql
								.formatQuery("INSERT INTO tuples (`sid`,`key`,`val_int`) VALUE (?, ?, ?) ON DUPLICATE KEY UPDATE `val_int` = VALUES(`val_int`)"));
				stmt.setLong(1, id);
				stmt.setString(2, key);
				stmt.setInt(3, value);
			}
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, double value) {
		PreparedStatement stmt = null;
		try {
			if (simKeys.contains(key)) {
				stmt = conn.prepareStatement(Sql
						.formatQuery("UPDATE simulations " + "SET `" + key
								+ "` = ? WHERE `id` = ?"));
				stmt.setDouble(1, value);
				stmt.setLong(2, id);
			} else {
				stmt = conn
						.prepareStatement(Sql
								.formatQuery("INSERT INTO tuples (`sid`,`key`,`val_dbl`) VALUE (?, ?, ?) ON DUPLICATE KEY UPDATE `val_dbl` = VALUES(`val_dbl`)"));
				stmt.setLong(1, id);
				stmt.setString(2, key);
				stmt.setDouble(3, value);
			}
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, int t, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_t (`sid`,`key`,`t`,`val_str`) VALUE (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `val_str` = VALUES(`val_str`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setInt(3, t);
			stmt.setString(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, int t, int value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_t (`sid`,`key`,`t`,`val_int`) VALUE (?,?,?,?) ON DUPLICATE KEY UPDATE `val_int` = VALUES(`val_int`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setInt(3, t);
			stmt.setInt(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, int t, double value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_t (`sid`,`key`,`t`,`val_dbl`) VALUE (?,?,?,?) ON DUPLICATE KEY UPDATE `val_dbl` = VALUES(`val_dbl`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setInt(3, t);
			stmt.setDouble(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag (`sid`,`key`,`aid`,`val_str`) VALUE (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `val_str` = VALUES(`val_str`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setString(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag (`sid`,`key`,`aid`,`val_int`) VALUE (?,?,?,?) ON DUPLICATE KEY UPDATE `val_int` = VALUES(`val_int`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setInt(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, double value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag (`sid`,`key`,`aid`,`val_dbl`) VALUE (?,?,?,?) ON DUPLICATE KEY UPDATE `val_dbl` = VALUES(`val_dbl`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setDouble(4, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			String value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag_t (`sid`,`key`,`aid`,`t`,`val_str`) VALUE (?,?,?,?,?) ON DUPLICATE KEY UPDATE `val_str` = VALUES(`val_str`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setInt(4, t);
			stmt.setString(5, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t, int value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag_t (`sid`,`key`,`aid`,`t`,`val_int`) VALUE (?,?,?,?,?) ON DUPLICATE KEY UPDATE `val_int` = VALUES(`val_int`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setInt(4, t);
			stmt.setInt(5, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			double value) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("INSERT INTO tuples_ag (`sid`,`key`,`aid`,`t`,`val_dbl`) VALUE (?,?,?,?,?) ON DUPLICATE KEY UPDATE `val_dbl` = VALUES(`val_dbl`)"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setInt(4, t);
			stmt.setDouble(5, value);
			exec.submit(stmt);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Set<String> fetchParameterKeys(long id) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Set<String> keys = new HashSet<String>();
		try {
			stmt = conn.prepareStatement(Sql.getParametersById());
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			while (rs.next()) {
				keys.add(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return keys;
	}

	@Override
	protected String fetchParameter(long id, String key) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("SELECT value FROM parameters WHERE `simId` = ? AND `name` = ?"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private <T> T returnAsType(ResultSet rs, Class<T> type) throws SQLException {
		if (type == String.class)
			return rs.getObject(1, type);
		else if (type == Integer.class || type == Integer.TYPE)
			return rs.getObject(2, type);
		else if (type == Double.class || type == Double.TYPE)
			return rs.getObject(3, type);
		else if (type == Boolean.class || type == Boolean.TYPE)
			return rs.getObject(1, type);

		throw new RuntimeException("Unknown type cast request");
	}

	@Override
	protected <T> T fetchTuple(long id, String key, Class<T> type) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (simKeys.contains(key)) {
				stmt = conn.prepareStatement(Sql.formatQuery("SELECT `" + key
						+ "` FROM simulations WHERE id = ?"));
				stmt.setLong(1, id);
				rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getObject(1, type);
				}
			} else {
				stmt = conn
						.prepareStatement(Sql
								.formatQuery("SELECT `val_str`, `val_int`, `val_dbl` FROM `tuples` WHERE `sid` = ? AND `key` = ?"));
				stmt.setLong(1, id);
				stmt.setString(2, key);
				rs = stmt.executeQuery();
				if (rs.next()) {
					return returnAsType(rs, type);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, int t, Class<T> type) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("SELECT `val_str`, `val_int`, `val_dbl` FROM `tuples_t` WHERE `sid` = ? AND `key` = ? AND `t` = ?"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setInt(3, t);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return returnAsType(rs, type);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, Class<T> type) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("SELECT `val_str`, `val_int`, `val_dbl` FROM `tuples_ag` WHERE `sid` = ? AND `key` = ? AND `aid` = ?"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				return returnAsType(rs, type);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, int t,
			Class<T> type) {
		exec.waitForEmptyQueue();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn
					.prepareStatement(Sql
							.formatQuery("SELECT `val_str`, `val_int`, `val_dbl` FROM `tuples_ag_t` WHERE `sid` = ? AND `key` = ? AND `aid` = ? AND `t` = ?"));
			stmt.setLong(1, id);
			stmt.setString(2, key);
			stmt.setString(3, agent.toString());
			stmt.setInt(4, t);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return returnAsType(rs, type);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	class QueryExecutor implements Runnable {

		final protected BlockingQueue<PreparedStatement> queryQ;
		final Thread execThread;
		AtomicBoolean inTransaction = new AtomicBoolean();

		QueryExecutor(int capacity) {
			super();
			queryQ = new LinkedBlockingQueue<PreparedStatement>(capacity);
			execThread = new Thread(this, "Query executor");
		}

		public void start() {
			execThread.start();
		}

		public synchronized void stop() {
			try {
				PreparedStatement queuePoison = conn
						.prepareStatement("SELECT 1");
				queuePoison.close();
				submit(queuePoison);
				execThread.join();
			} catch (SQLException e2) {
				throw new RuntimeException(e2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public synchronized void submit(PreparedStatement stmt) {
			try {
				queryQ.put(stmt);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		public synchronized void waitForEmptyQueue() {
			synchronized (queryQ) {
				logger.info("Check exec...");
				while (inTransaction.get() || !queryQ.isEmpty()) {
					logger.info("Exec in transaction");
					try {
						queryQ.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				List<PreparedStatement> tx = new ArrayList<PreparedStatement>();
				boolean breakOnFinish = false;
				try {
					tx.add(queryQ.take());
					inTransaction.set(true);
					queryQ.drainTo(tx);
					conn.setAutoCommit(false);
					for (PreparedStatement stmt : tx) {
						if (stmt.isClosed())
							breakOnFinish = true;
						else {
							logger.debug("Execute: " + stmt);
							stmt.execute();
						}
					}
					conn.commit();
					conn.setAutoCommit(true);
					if (breakOnFinish)
						break;
				} catch (SQLException e) {
					logger.warn("Error executing batch query", e);
					logger.warn("Next exception was: ", e.getNextException());
				} catch (InterruptedException e1) {
				} finally {
					inTransaction.set(false);
					for (PreparedStatement stmt : tx) {
						try {
							stmt.close();
						} catch (SQLException e) {
						}
					}
					synchronized(queryQ) {
						queryQ.notify();
					}
				}
			}
		}

	}

}
