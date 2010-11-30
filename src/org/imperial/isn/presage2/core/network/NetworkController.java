/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * <p>This is a central controller through which all messages go.</p>
 * 
 * <p>Like the environment it ensures agents obey the rules of the system. In
 * this case these are to do with the network, such as transmission ranges etc.</p>
 * 
 * @author Sam Macbeth
 *
 */
public abstract class NetworkController implements NetworkChannel {

	protected final Logger logger;
	
	/**
	 * Map of devices registered to this controller.
	 */
	protected Map<UUID, NetworkChannel> devices;
	
	// TODO when environment connectors are done.
	//protected EnvironmentConnector environment;
	
	/**
	 * @param logger
	 * @param devices
	 */
	public NetworkController(Logger logger) {
		super();
		this.logger = logger;
		this.devices = new HashMap<UUID, NetworkChannel>();
	}
	
	/**
	 * Invoked by a NetworkConnector when it wishes to send a message.
	 * @see org.imperial.isn.presage2.core.network.NetworkChannel#deliverMessage(org.imperial.isn.presage2.core.network.Message)
	 */
	@Override
	public void deliverMessage(Message m) throws NetworkException {
		// check message type
		if(m instanceof UnicastMessage) {
			doUnicast((UnicastMessage) m);
		} else if(m instanceof MulticastMessage) {
			doMulticast((MulticastMessage) m);
		} else if(m instanceof BroadcastMessage) {
			doBroadcast((BroadcastMessage) m);
		} else {
			// TODO handling of other message types
		}
	}

	/**
	 * Send a unicast message
	 * @param m
	 * @throws NetworkException
	 */
	protected void doUnicast(UnicastMessage m) throws NetworkException {
		try {
			this.devices.get(m.getTo()).deliverMessage(m);
			this.logger.debug("Dispatched unicast message: "+ m.toString());
		} catch(NullPointerException e) {
			this.logger.debug("Unicast message sent to unknown recipient: "+ m.toString());
		}
	}
	
	/**
	 * Send a multicast message
	 * @param m
	 */
	protected void doMulticast(MulticastMessage m) throws NetworkException {
		final List<UUID> recipients = m.getTo();
		for(UUID to : recipients) {
			try {		
				this.devices.get(to).deliverMessage(m);
			} catch(NullPointerException e) {
				this.logger.debug("Multicast message containing unknown recipient: "+ m.toString());
			}
		}
		this.logger.debug("Sent multicast message: "+ m.toString());
	}
	
	/**
	 * Send a broadcast message
	 * @param m
	 */
	protected void doBroadcast(BroadcastMessage m) throws NetworkException {
		for(UUID to : this.devices.keySet()) {
			this.devices.get(to).deliverMessage(m);
		}
		this.logger.debug("Sent broadcast message: "+ m.toString());
	}

	public void registerConnector() throws NetworkException {
		// TODO this function needs some registration object passed to it.
	}

}
