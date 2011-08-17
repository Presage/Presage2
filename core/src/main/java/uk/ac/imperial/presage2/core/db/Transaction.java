package uk.ac.imperial.presage2.core.db;

/**
 * A Transaction on a graph database. Follows the same convention as Neo4j's
 * transaction interface.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Transaction {

	/**
	 * Mark this transaction as failed.
	 */
	void failure();

	/**
	 * Finish the transaction.
	 */
	void finish();

	/**
	 * Mark this transaction as successful.
	 */
	void success();

}
