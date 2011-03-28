/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

/**
 * @author Sam Macbeth
 *
 */
public class InvalidAuthkeyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAuthkeyException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	public InvalidAuthkeyException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidAuthkeyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public InvalidAuthkeyException(Throwable cause) {
		super(cause);
	}
	
	
	
}
