/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.messaging.Input;

/**
 * 
 * <p>An ActionHandler processes an {@link Action} in the environment.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface ActionHandler {

	/**
	 * Check if this Handler can handle the given action.
	 * @param action Action to handle
	 * @return true if it can, false otherwise
	 */
	public  boolean canHandle(Action action);

	/**
	 * Handle the given action.
	 * @param action action to handle
	 * @param actorID actor performing this action
	 * @return May return a further Input to be returned to the actor, or null if not.
	 */
	public  Input handle(Action action, String actorID);
	
}
