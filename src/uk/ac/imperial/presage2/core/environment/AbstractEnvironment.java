/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * Abstract implementation of an environment.
 * 
 * @author Sam Macbeth
 *
 */
public abstract class AbstractEnvironment implements EnvironmentConnector,
		EnvironmentSharedStateAccess {

	private final Logger logger = Logger.getLogger(AbstractEnvironment.class);
	
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
	
	protected Set<ActionHandler> actionHandlers;
	
	/**
	 * <p>Creates the Environment, initialising it ready for participants to register and act.</p>
	 * <p>The following is initialised:</p>
	 * <ul>
	 * <li>Registered participants map</li>
	 * <li>Global shared state</li>
	 * <li>Participant shared state</li>
	 * <li>Authkeys</li>
	 * <li>Action handlers</li>
	 * </ul>
	 */
	public AbstractEnvironment() {
		super();
		// these data structures must be synchronised as synchronous access is probable.
		registeredParticipants = Collections.synchronizedMap(new HashMap<UUID, Participant>());
		globalSharedState = Collections.synchronizedMap(new HashMap<String, SharedState<?>>());
		participantState = Collections.synchronizedMap(new HashMap<UUID, Map<String, ParticipantSharedState<?>>>());
		// for authkeys we don't synchronize, but we must remember to do so manually for insert/delete operations
		authkeys = new HashMap<UUID, UUID>();
		
		actionHandlers = initialiseActionHandlers();
	}
	
	/**
	 * Initialise a set of {@link ActionHandler}s which the environment will use
	 * to process {@link Action}s.
	 * @return
	 */
	abstract protected Set<ActionHandler> initialiseActionHandlers();

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
			this.logger.warn("Failed to register participant, invalid request.", e);
			throw e;
		}
		// register participant
		if(this.logger.isInfoEnabled()) {
			this.logger.info("Registering participant "+ participantUUID +"");
		}
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
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Responding to environment registration request from "+participantUUID+" with "+services.size()+" services.");
		}
		
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
	public void act(Action action, UUID actor, UUID authkey) throws ActionHandlingException {
		// verify authkey
		if(authkeys.get(actor) != authkey) {
			InvalidAuthkeyException e = new InvalidAuthkeyException("Agent "+actor+" attempting to act with incorrect authkey!");
			this.logger.warn(e);
			throw e;
		}
		
		// Action processing
		if(actionHandlers.size() == 0) {
			ActionHandlingException e = new ActionHandlingException(this.getClass().getCanonicalName() 
					+ " has no ActionHandlers cannot execute action request ");
			logger.warn(e);
			throw e;
		}
		
		List<ActionHandler> canHandle = new ArrayList<ActionHandler>();
		
		for(ActionHandler h : actionHandlers) {
			if(h.canHandle(action)) {
				canHandle.add(h);
			}
		}
		
		if(canHandle.size() == 0) {
			ActionHandlingException e = new ActionHandlingException(this.getClass().getCanonicalName() 
					+ " has no ActionHandlers which can handle " + action.getClass().getCanonicalName() 
					+ " - cannot execute action request");
			logger.warn(e);
			throw e;
		}
		
		// Handle the action and retrieve the resultant input (if there is one)
		Input i;
		if(canHandle.size() > 1) {
			logger.warn("More than one ActionHandler.canhandle() returned true for " 
					+ action.getClass().getCanonicalName() + " therefore I'm picking one at random.");
			i = canHandle.get(0).handle(action, actor); // TODO random
		} else {
			i = canHandle.get(0).handle(action, actor);
		}
		// Give the input we got to the actor.
		if(i != null) {
			registeredParticipants.get(actor).enqueueInput(i);
		}
	}

	/**
	 * <p>Deregister a participant with the environment.
	 * @see uk.ac.imperial.presage2.core.environment.EnvironmentConnector#deregister(java.util.UUID, java.util.UUID)
	 */
	@Override
	public void deregister(UUID participantID, UUID authkey) {
		if(authkeys.get(participantID) != authkey) {
			InvalidAuthkeyException e = new InvalidAuthkeyException("Agent "+participantID+" attempting to deregister with incorrect authkey!");
			this.logger.warn(e);
			throw e;
		}
		if(this.logger.isInfoEnabled()) {
			this.logger.info("Deregistering participant "+ participantID +"");
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
			SharedStateAccessException e = new SharedStateAccessException("Invalid global shared state access. State '"+name+"' does not exist!");
			this.logger.warn(e);
			throw e;
		} else {
			if(this.logger.isDebugEnabled()) {
				this.logger.debug("Returning global environment state '"+name+"'");
			}
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
			SharedStateAccessException sse = new SharedStateAccessException("Invalid shared state access: '"+participantID+"."+name+"'. Participant does not exist", e);
			this.logger.warn(sse);
			throw sse;
		}
		if(state == null) {
			SharedStateAccessException e = new SharedStateAccessException("Invalid shared state access: '"+participantID+"."+name+"'. Participant does not have a state with this name");
			this.logger.warn(e);
			throw e;
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Returning participant state '"+participantID+"."+name+"'");
		}
		return state;
	}

}
