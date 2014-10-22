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

package uk.ac.imperial.presage2.core.participant;

import java.util.UUID;

/**
 * 
 * Parent of all exceptions thrown by a participant.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class ParticipantException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The UUID of the throwing participant.
	 */
	private UUID participantUUID;

	/**
	 * Name of the throwing participant.
	 */
	private String participantName;

	protected ParticipantException() {

	}

	/**
	 * 
	 * @param p
	 * @param message
	 */
	protected ParticipantException(Participant p, String message) {
		super(message);
		this.participantUUID = p.getID();
		this.participantName = p.getName();
	}

	/**
	 * 
	 * @param p
	 * @param cause
	 */
	protected ParticipantException(Participant p, Throwable cause) {
		super(cause);
		this.participantUUID = p.getID();
		this.participantName = p.getName();
	}

	/**
	 * 
	 * @param p
	 * @param message
	 * @param cause
	 */
	protected ParticipantException(Participant p, String message,
			Throwable cause) {
		super(message, cause);
		this.participantUUID = p.getID();
		this.participantName = p.getName();
	}

	/**
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return "Participant " + this.participantName + " (UUID: "
				+ this.participantUUID + ") " + ": "
				+ super.getLocalizedMessage();
	}

}
