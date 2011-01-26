/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

/**
 * @author Sam Macbeth
 *
 */
public class SharedStateAccessException extends RuntimeException {

	/**
	 * 
	 */
	public SharedStateAccessException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SharedStateAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SharedStateAccessException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SharedStateAccessException(Throwable cause) {
		super(cause);
	}
	
}
