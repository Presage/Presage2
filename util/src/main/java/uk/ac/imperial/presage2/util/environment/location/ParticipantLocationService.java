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
package uk.ac.imperial.presage2.util.environment.location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentMembersService;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * <p>This is an extension of the {@link LocationService} to provide tools
 * specifically for {@link Participant}s. This class can also handle the
 * {@link ParticipantSharedState} of {@link Location} for the agent using it.</p>
 * 
 * <p>Some functions depend on the environment having an {@link EnvironmentMembersService}
 * available</p>
 * 
 * @author Sam Macbeth
 *
 */
public class ParticipantLocationService extends LocationService {

	private final Logger logger = Logger.getLogger(ParticipantLocationService.class);

	/**
	 * {@link ParticipantSharedState} for this agent's Location.
	 */
	protected final ParticipantSharedState<Location> state;

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
		this.state = new ParticipantSharedState<Location>("util.location", this.locationProvider.getLocation(), p.getID());
		this.membersService = getMembersService(serviceProvider);
	}

	/**
	 * <p>Create a {@link ParticipantLocationService} for {@link Participant} p using existing {@link ParticipantSharedState} for it's 
	 * current location and perception range provided by hasRange. The {@link EnvironmentSharedStateAccess} is provided by sharedState.</p>
	 * @param p	{@link Participant} this service is for.
	 * @param locationState	{@link ParticipantSharedState} for this agent's location.
	 * @param hasRange	{@link HasPerceptionRange} which provides the range at which this agent can perceive other agents.
	 * @param sharedState	{@link EnvironmentSharedStateAccess} which this {@link EnvironmentService} should use.
	 * @param serviceProvider	{@link EnvironmentServiceProvider} for fetching dependencies
	 */
	public ParticipantLocationService(Participant p, 
			ParticipantSharedState<Location> locationState, 
			HasPerceptionRange hasRange, 
			EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.me = p;
		this.state = locationState;
		this.locationProvider = new HasLocation() {
			@Override
			public Location getLocation() {
				return state.getValue();
			}
		};
		this.rangeProvider = hasRange;
		this.membersService = getMembersService(serviceProvider);
	}

	/**
	 * <p>Create a {@link ParticipantLocationService} for {@link Participant} p. If p implements {@link HasPerceptionRange} then
	 * this will be used to find the agent's perception range, otherwise we will use no perception range and this agent will be 
	 * able to see the {@link Location} of all other agents in the environment.</p>
	 * @param p	p	{@link Participant} this service is for.
	 * @param locationState	{@link ParticipantSharedState} for this agent's location.
	 * @param sharedState	{@link EnvironmentSharedStateAccess} which this {@link EnvironmentService} should use.
	 * @param serviceProvider	{@link EnvironmentServiceProvider} for fetching dependencies
	 */
	public ParticipantLocationService(Participant p, 
			ParticipantSharedState<Location> locationState, 
			EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.me = p;
		this.state = locationState;
		this.locationProvider = new HasLocation() {
			@Override
			public Location getLocation() {
				return state.getValue();
			}
		};
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

	public ParticipantSharedState<Location> getLocationState() {
		return this.state;
	}

	@Override
	public Location getAgentLocation(UUID participantID) throws CannotSeeAgent {
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
	public static ParticipantSharedState<Location> createSharedState(UUID pid, HasLocation loc) {
		return new ParticipantSharedState<Location>("util.location", loc.getLocation(), pid);
	}

}
