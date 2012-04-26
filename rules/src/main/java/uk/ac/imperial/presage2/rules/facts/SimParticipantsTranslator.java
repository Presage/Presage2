/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.rules.facts;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.google.inject.Inject;

public class SimParticipantsTranslator extends GenericGlobalStateTranslator {

	StatefulKnowledgeSession session;

	@Inject
	public SimParticipantsTranslator(StatefulKnowledgeSession session) {
		super();
		this.session = session;
	}

	@Override
	public boolean canTranslate(String name) {
		return name.equalsIgnoreCase("participants");
	}

	@Override
	public Object getFactObject(String name, Serializable value) {
		@SuppressWarnings("unchecked")
		Set<UUID> aids = (Set<UUID>) value;
		Collection<FactHandle> agentFacts = session.getFactHandles(new ObjectFilter() {
			@Override
			public boolean accept(Object object) {
				return object instanceof Agent;
			}
		});
		for (FactHandle factHandle : agentFacts) {
			aids.remove(((Agent) session.getObject(factHandle)).getAid());
		}
		for (UUID uuid : aids) {
			Object a = getAgentFact(uuid);
			if (session.getFactHandle(a) == null) {
				session.insert(a);
			}
		}
		return super.getFactObject(name, value);
	}

	protected Object getAgentFact(UUID uuid) {
		return new Agent(uuid);
	}

	@Override
	public Serializable getStateFromFact(Object fact) {
		return super.getStateFromFact(fact);
	}

}
