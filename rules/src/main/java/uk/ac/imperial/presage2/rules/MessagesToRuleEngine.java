/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.rules;

import org.drools.runtime.StatefulKnowledgeSession;

import uk.ac.imperial.presage2.util.network.Message;
import uk.ac.imperial.presage2.util.network.NetworkAddress;
import uk.ac.imperial.presage2.util.network.NetworkConstraint;

import com.google.inject.Inject;

public class MessagesToRuleEngine implements NetworkConstraint {

	final StatefulKnowledgeSession session;

	@Inject
	public MessagesToRuleEngine(StatefulKnowledgeSession session) {
		super();
		this.session = session;
	}

	@Override
	public Message constrainMessage(Message m) {
		session.insert(m);
		return m;
	}

	@Override
	public boolean blockMessageDelivery(NetworkAddress from, NetworkAddress to) {
		return false;
	}

}
