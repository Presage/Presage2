/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

/**
 * @author Sam Macbeth
 *
 */
public class ActionHandlingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ActionHandlingException() {
		super();
	}

	public ActionHandlingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionHandlingException(String message) {
		super(message);
	}

	public ActionHandlingException(Throwable cause) {
		super(cause);
	}

}
