/**
 * 
 */
package org.imperial.isn.presage2.core.messaging;

/**
 * Enumerated type of all FIPA Performatives as given here:
 * http://jmvidal.cse.sc.edu/talks/agentcommunication/performatives.xml
 * 
 * @author Sam Macbeth
 *
 */
public enum Performative {
	ACCEPT_PROPOSAL, 
	AGREE, 
	CANCEL, 
	CFP, 
	CONFIRM, 
	DISCONFIRM, 
	FAILURE, 
	INFORM, 
	INFORM_IF,
	INFORM_REF,
	NOT_UNDERSTOOD,
	PROPAGATE,
	PROPOSE,
	PROXY,
	QUERY_IF,
	QUERY_REF,
	REFUSE,
	REJECT_PROPOSAL,
	REQUEST,
	REQUEST_WHEN,
	REQUEST_WHENEVER,
	SUBSCRIBE;
	
}
