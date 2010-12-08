/**
 * 
 */
package org.imperial.isn.presage2.core.network;

/**
 * Top level network exception. All network exceptions
 * are children of this.
 * 
 * @author Sam Macbeth
 *
 */
abstract class NetworkException extends RuntimeException {

	/**
	 * 
	 */
	public NetworkException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NetworkException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NetworkException(Throwable cause) {
		super(cause);
	}
	
}
