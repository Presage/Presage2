package uk.ac.imperial.presage2.rules;

import org.drools.runtime.StatefulKnowledgeSession;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkConstraint;

public class MessagesToRuleEngine implements NetworkConstraint {

	final StatefulKnowledgeSession session;

	@Inject
	public MessagesToRuleEngine(StatefulKnowledgeSession session) {
		super();
		this.session = session;
	}

	@Override
	public Message<?> constrainMessage(Message<?> m) {
		session.insert(m);
		return m;
	}

	@Override
	public boolean blockMessageDelivery(NetworkAddress to, Message<?> m) {
		return false;
	}

}
