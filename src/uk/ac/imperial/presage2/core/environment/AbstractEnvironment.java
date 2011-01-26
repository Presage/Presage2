/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * Abstract implementation of an environment.
 * 
 * @author Sam Macbeth
 *
 */
public abstract class AbstractEnvironment implements EnvironmentConnector,
		EnvironmentSharedStateAccess {

	/**
	 * Map of Participants in the simulation
	 */
	protected Map<UUID, Participant> registeredParticipants;
	
	/**
	 * Map of participant IDs to authkeys.
	 */
	protected Map<UUID, UUID> authkeys;
	
	protected Map<String, SharedState<?>> globalSharedState;
	
	protected Map<UUID, Map<String, ParticipantSharedState<?>>> participantState;
	
	/**
	 * 
	 */
	public AbstractEnvironment() {
		super();
		// these data structures must be synchronised as synchronous access is probable.
		registeredParticipants = Collections.synchronizedMap(new HashMap<UUID, Participant>());
		globalSharedState = Collections.synchronizedMap(new HashMap<String, SharedState<?>>());
		participantState = Collections.synchronizedMap(new HashMap<UUID, Map<String, ParticipantSharedState<?>>>());
		// for authkeys we don't synchronize, but we must remember to do so manually for insert/delete operations
		authkeys = new HashMap<UUID, UUID>();
	}

	/**
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentConnector#register(uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest)
	 */
	@Override
	public EnvironmentRegistrationResponse register(
			EnvironmentRegistrationRequest request) {
		// check we've been passed a non null participant
		UUID participantUUID;
		try {
			participantUUID = request.getParticipant().getID();
		} catch(NullPointerException e) {
			throw e;
		}
		// register participant
		registeredParticipants.put(participantUUID, request.getParticipant()); 
		// generate authkey TODO Global RNG in here
		synchronized(authkeys) {
			authkeys.put(participantUUID, new UUID(0, 0));
		}
		// process shared state:
		// We create a new Map to put shared state type -> object pairs
		// We transfer shared state from the provided Set to this Map
		// We then add our Map to the participantState Map referenced by the registerer's UUID.
		HashMap<String, ParticipantSharedState<?>> stateMap = new HashMap<String, ParticipantSharedState<?>>();
		Set<ParticipantSharedState<?>> pStates = request.getSharedState();
		for(ParticipantSharedState<?> state : pStates) {
			stateMap.put(state.getType(), state);
		}
		participantState.put(participantUUID, stateMap);
		
		// Generate EnvironmentServices we are providing in the response.
		Set<EnvironmentService> services = generateServices(request.getParticipant());
		
		// Create response
		EnvironmentRegistrationResponse response = new EnvironmentRegistrationResponse(authkeys.get(participantUUID), services);
		
		return response;
	}

	/**
	 * <p>Generate a Set of {@link EnvironmentService}s for a participant to be sent.</p>
	 * @param participant
	 * @return
	 */
	abstract protected Set<EnvironmentService> generateServices(Participant participant);

	/**
	 * <p>Perform an {@link Action} on the environment.</p>
	 * 
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentConnector#act(uk.ac.imperial.presage2.core.Action, java.util.UUID, java.util.UUID)
	 */
	@Override
	public void act(Action action, UUID actor, UUID authkey) {
		// verify authkey
		if(authkeys.get(actor) != authkey) {
			throw new RuntimeException("Agent "+actor+" attempting to act with incorrect authkey!"); // TODO strongly typed exception
		}
		// TODO Action processing
	}

	/**
	 * <p>Deregister a participant with the environment.
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentConnector#deregister(java.util.UUID, java.util.UUID)
	 */
	@Override
	public void deregister(UUID participantID, UUID authkey) {
		if(authkeys.get(participantID) != authkey) {
			throw new RuntimeException("Agent "+participantID+" attempting to act with incorrect authkey!"); // TODO strongly typed exception
		}
		registeredParticipants.remove(participantID);
		synchronized(authkeys) {
			authkeys.remove(participantID);
		}
	}
	
	/**
	 * <p>Return a global state value referenced by name.</p>
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess#getGlobal(java.lang.String)
	 */
	@Override
	public SharedState<?> getGlobal(String name) {
		SharedState<?> global = globalSharedState.get(name);
		if(global == null) {
			throw new RuntimeException("Invalid global shared state access. State '"+name+"' does not exist!"); //TODO strongly type
		} else {
			return global;
		}
	}

	/**
	 * <p>Return a shared state value from an individual agent</p>
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess#get(java.lang.String, java.util.UUID)
	 */
	@Override
	public ParticipantSharedState<?> get(String name, UUID participantID) {
		ParticipantSharedState<?> state;
		try {
			state = participantState.get(participantID).get(name);
		} catch(NullPointerException e) {
			throw new RuntimeException("Invalid shared state access: '"+participantID+"."+name+"'. Participant does not exist", e); //TODO strongly type
		}
		if(state == null) {
			throw new RuntimeException("Invalid shared state access: '"+participantID+"."+name+"'. Participant does not have a state with this name");
		}
		return state;
	}

}
