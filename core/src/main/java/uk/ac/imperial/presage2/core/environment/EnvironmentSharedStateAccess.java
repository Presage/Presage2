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

package uk.ac.imperial.presage2.core.environment;

import java.io.Serializable;
import java.util.UUID;

/**
 * This is the access layer to the shared state of the environment.
 */
public interface EnvironmentSharedStateAccess {

	/**
	 * Get a global shared state value.
	 * 
	 * @param name
	 *            String key name of the value
	 * @return {@link Serializable} value if it exists in the state, null
	 *         otherwise.
	 */
	public Serializable getGlobal(String name);

	/**
	 * <p>
	 * Modify a global shared state value.
	 * </p>
	 * <p>
	 * Value will not be changed until the next time slice.
	 * </p>
	 * 
	 * @param name
	 *            String key name of the value to be changed.
	 * @param change
	 *            {@link StateTransformer} which will perform the change.
	 */
	public void changeGlobal(String name, StateTransformer change);

	/**
	 * <p>
	 * Change a global shared state value to a given new value,
	 * </p>
	 * <p>
	 * Value will not be changed until the next time slice.
	 * </p>
	 * 
	 * @param name
	 *            String key name of the value to be changed.
	 * @param value
	 *            New value to be set.
	 */
	public void changeGlobal(String name, Serializable value);

	/**
	 * <p>
	 * Create a value in the global shared state.
	 * </p>
	 * <p>
	 * Value will be created in the current time cycle.
	 * </p>
	 * 
	 * @param state
	 *            {@link SharedState} describing the state to create.
	 * @throws SharedStateAccessException
	 *             if state with the same key already exists.
	 */
	public void createGlobal(SharedState state);

	/**
	 * <p>
	 * Create a value in the global shared state.
	 * </p>
	 * <p>
	 * Value will be created in the current time cycle.
	 * </p>
	 * 
	 * @param name
	 *            String key name of the value to create.
	 * @param value
	 *            Initial value to set state to.
	 * @throws SharedStateAccessException
	 *             if state with the same key already exists.
	 */
	public void createGlobal(String name, Serializable value);

	/**
	 * Get a shared state value for an individual agent.
	 * 
	 * @param name
	 *            String key of the value to get
	 * @param participantID
	 *            {@link UUID} of the agent
	 * @return {@link Serializable} value if it exists in the state for this
	 *         agent, null otherwise.
	 */
	public Serializable get(String name, UUID participantID);

	/**
	 * <p>
	 * Modify a shared state value for a given agent.
	 * </p>
	 * <p>
	 * Value will not be changed until the next time slice.
	 * </p>
	 * 
	 * @param name
	 *            String key of the value to change
	 * @param participantID
	 *            {@link UUID} of the agent
	 * @param change
	 *            {@link StateTransformer} which will perform the change.
	 */
	public void change(String name, UUID participantID, StateTransformer change);

	/**
	 * <p>
	 * Change a shared state value for a given agent to a given value.
	 * </p>
	 * <p>
	 * Value will not be changed until the next time slice.
	 * </p>
	 * 
	 * @param name
	 *            String key of the value to change
	 * @param participantID
	 *            {@link UUID} of the agent
	 * @param value
	 *            {@link StateTransformer} which will perform the change.
	 */
	public void change(String name, UUID participantID, Serializable value);

	/**
	 * <p>
	 * Create a shared state value for a given agent.
	 * </p>
	 * <p>
	 * Value will be created in the current time cycle.
	 * </p>
	 * 
	 * @param state
	 *            {@link ParticipantSharedState} describing the state to create.
	 * @throws SharedStateAccessException
	 *             if state with the same key already exists.
	 */
	public void create(ParticipantSharedState state);

	/**
	 * <p>
	 * Create a shared state value for a given agent.
	 * </p>
	 * <p>
	 * Value will be created in the current time cycle.
	 * </p>
	 * 
	 * @param name
	 *            String key of the value to change
	 * @param participantID
	 *            {@link UUID} of the agent
	 * @param value
	 *            Initial value to set state to.
	 * @throws SharedStateAccessException
	 *             if state with the same key already exists.
	 */
	public void create(String name, UUID participantID, Serializable value);

}
