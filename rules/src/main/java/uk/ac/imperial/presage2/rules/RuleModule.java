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
package uk.ac.imperial.presage2.rules;

import java.util.HashSet;
import java.util.Set;

import org.drools.runtime.StatefulKnowledgeSession;

import uk.ac.imperial.presage2.rules.facts.AgentStateTranslator;
import uk.ac.imperial.presage2.rules.facts.StateTranslator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * Binds {@link RuleStorage} with a set of DRL files to initially load into the
 * engine as well as {@link StateTranslator}s and {@link AgentStateTranslator}s
 * to use for shared state.
 * 
 * @author Sam Macbeth
 * 
 */
public class RuleModule extends AbstractModule {

	private Set<String> ruleFiles = new HashSet<String>();
	private Set<Class<? extends StateTranslator>> stateTranslators = new HashSet<Class<? extends StateTranslator>>();
	private Set<Class<? extends AgentStateTranslator>> agentStateTranslators = new HashSet<Class<? extends AgentStateTranslator>>();

	public RuleModule() {
		super();
	}

	public RuleModule addClasspathDrlFile(String fileName) {
		ruleFiles.add(fileName);
		return this;
	}

	public RuleModule addStateTranslator(Class<? extends StateTranslator> clazz) {
		stateTranslators.add(clazz);
		return this;
	}

	public RuleModule addAgentStateTranslator(
			Class<? extends AgentStateTranslator> clazz) {
		agentStateTranslators.add(clazz);
		return this;
	}

	@Override
	protected void configure() {
		bind(RuleStorage.class).in(Singleton.class);
		bind(StatefulKnowledgeSession.class).toProvider(RuleStorage.class);

		Multibinder<String> rulesBinder = Multibinder.newSetBinder(binder(),
				String.class, Rules.class);
		for (String file : ruleFiles) {
			rulesBinder.addBinding().toInstance(file);
		}
		Multibinder<StateTranslator> translatorBinder = Multibinder
				.newSetBinder(binder(), StateTranslator.class);
		for (Class<? extends StateTranslator> clazz : stateTranslators) {
			translatorBinder.addBinding().to(clazz);
		}
		Multibinder<AgentStateTranslator> agentTranslatorBinder = Multibinder
				.newSetBinder(binder(), AgentStateTranslator.class);
		for (Class<? extends AgentStateTranslator> clazz : agentStateTranslators) {
			agentTranslatorBinder.addBinding().to(clazz);
		}
	}

}
