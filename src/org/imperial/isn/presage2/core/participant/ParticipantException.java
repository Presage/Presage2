/**
 * 
 */
package org.imperial.isn.presage2.core.participant;

import java.util.UUID;

import org.imperial.isn.presage2.core.Time;

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
	
	/**
	 * Time the participant threw the exception.
	 */
	private Time participantTime;
	
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
		this.participantTime = p.getTime();
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
		this.participantTime = p.getTime();
	}
	
	/**
	 * 
	 * @param p
	 * @param message
	 * @param cause
	 */
	protected ParticipantException(Participant p, String message, Throwable cause) {
		super(message, cause);
		this.participantUUID = p.getID();
		this.participantName = p.getName();
		this.participantTime = p.getTime();
	}

	/**
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return "Participant "+ this.participantName +" (UUID: "+ this.participantUUID +") " +
		"at sim time: "+ this.participantTime.toString() +": "+ super.getLocalizedMessage();
	}
	
	

}
