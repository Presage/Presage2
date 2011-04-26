package uk.ac.imperial.presage2.db;

/**
 * This is a generic description of a Database service in presage. Anything we want
 * to use as a database should implement this so the platform can start and stop it.
 * 
 * @author sm1106
 *
 */
public interface DatabaseService {

	void start() throws Exception;
	
	void stop();
	
}
