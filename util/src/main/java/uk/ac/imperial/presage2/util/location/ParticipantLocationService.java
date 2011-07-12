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
package uk.ac.imperial.presage2.util.location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationResponse;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;
import uk.ac.imperial.presage2.util.participant.HasPerceptionRange;

/**
 * <p>This is an extension of the {@link LocationService} to provide tools
 * specifically for {@link Participant}s. This class can also handle the
 * {@link ParticipantSharedState} of {@link Location} for the agent using it.</p>
 * 
 * <p>Some functions depend on the environment having an {@link EnvironmentMembersService}
 * available</p>
 * 
 * <h3>Usage</h3>
 * 
 * <p>Agents who have {@link Location} to share (implementing {@link HasLocation}) can create
 * shared state which registering with the environment with {@link #createSharedState(UUID, HasLocation)}:
 * <pre class="prettyprint">
 * ParticipantSharedState&lt;Location&gt; ss = ParticipantLocationService.createSharedState(myID, myLoc);
 * </pre></p>
 * 
 * <p>The environment should provide this service in the {@link EnvironmentRegistrationResponse}:
 * <pre class="prettyprint">
 * ParticipantLocationService p = new ParticipantLocationService(participant, sharedState, serviceProvider);
 * </pre>
 * </p>
 * 
 * @author Sam Macbeth
 *
 */
@ServiceDependencies({EnvironmentMembersService.class})
public class ParticipantLocationService extends LocationService {

	private final Logger logger = Logger.getLogger(ParticipantLocationService.class);

	/**
	 * {@link ParticipantSharedState} for this agent's Location.
	 */
	protected final ParticipantSharedState<HasLocation> state;

	protected final HasLocation locationProvider;

	protected final HasPerceptionRange rangeProvider;

	protected final Participant me;

	protected final EnvironmentMembersService membersService;

	/**
	 * <p>Create a {@link ParticipantLocationService} for {@link Participant} p which has a location provided by hasLoc,
	 * perception range provided by hasRange. The {@link EnvironmentSharedStateAccess} is provided by sharedState. This
	 * constructor will create a new {@link ParticipantSharedState} for {@link Location} from the given {@link HasLocation}</p>
	 * 
	 * @param p	{@link Participant} this service is for.
	 * @param hasLoc	{@link HasLocation} which provides the {@link Participant}'s location.
	 * @param hasRange	{@link HasPerceptionRange} which provides the range at which this agent can perceive other agents.
	 * @param sharedState	{@link EnvironmentSharedStateAccess} which this {@link EnvironmentService} should use.
	 * @param serviceProvider	{@link EnvironmentServiceProvider} for fetching dependencies
	 */
	public ParticipantLocationService(Participant p, 
			HasLocation hasLoc, 
			HasPerceptionRange hasRange, 
			EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.me = p;
		this.locationProvider = hasLoc;
		this.rangeProvider = hasRange;
		this.state = new ParticipantSharedState<HasLocation>("util.location", this.locationProvider, p.getID());
		this.membersService = getMembersService(serviceProvider);
	}

	@SuppressWarnings("unchecked")
	public ParticipantLocationService(Participant p, 
			EnvironmentSharedStateAccess sharedState, 
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.me = p;
		this.state = (ParticipantSharedState<HasLocation>) sharedState.get("util.location", p.getID());
		this.locationProvider = this.state.getValue();
		if(p instanceof HasPerceptionRange) {
			this.rangeProvider = (HasPerceptionRange) p;
		} else {
			this.rangeProvider = null;
			if(this.logger.isDebugEnabled()) {
				this.logger.debug("ParticipantLocationService created with no perception range. This agent is all seeing!");
			}
		}
		this.membersService = getMembersService(serviceProvider);
	}

	/**
	 * @param serviceProvider
	 * @return
	 */
	private EnvironmentMembersService getMembersService(
			EnvironmentServiceProvider serviceProvider) {
		try {
			return serviceProvider.getEnvironmentService(EnvironmentMembersService.class);
		} catch (UnavailableServiceException e) {
			logger.warn("Could not retrieve EnvironmentMembersService; functionality limited.");
			return null;
		}
	}

	public ParticipantSharedState<HasLocation> getLocationState() {
		return this.state;
	}

	@Override
	public Location getAgentLocation(UUID participantID) {
		if(this.rangeProvider == null) {
			return super.getAgentLocation(participantID);
		} else {
			final Location theirLoc = super.getAgentLocation(participantID);
			final Location myLoc = this.locationProvider.getLocation();

			if(myLoc.distanceTo(theirLoc) <= this.rangeProvider.getPerceptionRange()) {
				return theirLoc;
			} else {
				throw new CannotSeeAgent(this.me.getID(), participantID);
			}
		}
	}

	/**
	 * Not available for Participant use!
	 */
	@Override
	public void setAgentLocation(UUID participantID, Location l) {
		throw new SharedStateAccessException("A participant may not modify other participant's locations!");
	}

	/**
	 * Get the agents who are visible to me at this time and their {@link Location}s.
	 * @return	{@link HashMap} of agent's {@link UUID} to {@link Location}
	 */
	public Map<UUID, Location> getNearbyAgents() {
		if(this.membersService == null) {
			throw new UnsupportedOperationException();
		} else {
			final Map<UUID, Location> agents = new HashMap<UUID, Location>();
			for(UUID pid : this.membersService.getParticipants()) {
				// skip myself
				if(pid == this.me.getID()) 
					continue;
				// get location if I can see them, continue otherwise.
				Location l;
				try {
					l = this.getAgentLocation(pid);
				} catch (CannotSeeAgent e) {
					continue;
				}
				agents.put(pid, l);
			}
			return agents;
		}
	}

	/**
	 * Create the {@link ParticipantSharedState} required for this service.
	 * 
	 * @param pid	{@link UUID} of the participant to create sharedstate object for.
	 * @param loc	{@link HasLocation} provider for this participant.
	 * @return 	{@link ParticipantSharedState} on the type that this service uses.
	 */
	public static ParticipantSharedState<HasLocation> createSharedState(UUID pid, HasLocation loc) {
		return new ParticipantSharedState<HasLocation>("util.location", loc, pid);
	}

}
