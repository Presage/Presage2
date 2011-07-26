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

import java.util.UUID;

/**
 *
 */
public class ParticipantSharedState<T> extends SharedState<T> {

	protected UUID participantID;

	public ParticipantSharedState(String type, T value, UUID participantID) {
		super(type, value);
		this.participantID = participantID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParticipantSharedState) {
			ParticipantSharedState<?> state = (ParticipantSharedState<?>) obj;
			return this.participantID == state.participantID
					&& this.type.equals(state.type);
		}
		return super.equals(obj);
	}

	private int cachedHashCode = 0;

	@Override
	public int hashCode() {
		int hc = cachedHashCode;
		if (hc == 0) {
			hc = (this.type + this.participantID).hashCode();
			cachedHashCode = hc;
		}
		return hc;
	}

}
