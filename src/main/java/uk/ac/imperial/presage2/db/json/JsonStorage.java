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
package uk.ac.imperial.presage2.db.json;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class JsonStorage implements StorageService, DatabaseService {

	private final Logger logger = Logger.getLogger(JsonStorage.class);

	final String baseStoragePath;
	final String simulationsFileName = "simulations.json";

	boolean started = false;
	long maxSimId = 0;

	List<SimulationBean> simulations;
	SimulationBean currentSimulation = null;
	long agentsLoaded = 0;
	Map<UUID, PersistentAgent> agents = new HashMap<UUID, PersistentAgent>();

	ObjectMapper mapper;
	Set<String> writeQueue = new HashSet<String>();
	Map<String, Object> jsonObjects = new HashMap<String, Object>();

	public JsonStorage() {
		this("data/");
	}

	@Inject
	public JsonStorage(@Named(JsonModule.STORAGEPATH_KEY) String baseStoragePath) {
		super();
		this.baseStoragePath = baseStoragePath;
		if (this.baseStoragePath.length() > 0) {
			File basePath = new File(this.baseStoragePath);
			if (!basePath.exists()) {
				basePath.mkdirs();
			}
			if (!basePath.isDirectory()) {
				throw new RuntimeException("Base path is not a directory.");
			}
		}

		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Override
	public void start() throws Exception {
		// open simulations file if exists, create otherwise
		File simulationsFile = new File(baseStoragePath + simulationsFileName);

		if (!simulationsFile.exists()) {
			simulationsFile.createNewFile();
			ArrayNode n = mapper.createArrayNode();
			mapper.writeValue(simulationsFile, n);
		}

		// read simulations content in to JsonNode
		try {
			simulations = mapper.readValue(simulationsFile,
					new TypeReference<List<SimulationBean>>() {
					});
		} catch (JsonMappingException e) {
			simulations = new LinkedList<JsonStorage.SimulationBean>();
			logger.warn("Couldn't parse simulations file: " + baseStoragePath
					+ simulationsFileName, e);
		}
		// find max simID for new insertions
		for (SimulationBean s : simulations) {
			maxSimId = Math.max(maxSimId, s.id);
		}
		started = true;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public void stop() {
		if (isStarted()) {
			// write files
			File simulationsFile = new File(baseStoragePath
					+ simulationsFileName);
			try {
				mapper.writeValue(simulationsFile, simulations);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (String fileName : writeQueue) {
				if (!jsonObjects.containsKey(fileName)) {
					logger.warn("Missing data for " + fileName);
					continue;
				}
				try {
					File f = new File(fileName);
					if (!f.exists()) {
						f.getParentFile().mkdirs();
						f.createNewFile();
					}
					mapper.writeValue(f, jsonObjects.get(fileName));
				} catch (IOException e) {
					logger.warn("Error writing file " + fileName, e);
				}
			}
		}

		started = false;
	}

	@Override
	public PersistentSimulation createSimulation(String name, String classname,
			String state, int finishTime) {
		this.currentSimulation = new SimulationBean(++maxSimId, name,
				classname, state, finishTime);
		this.simulations.add(currentSimulation);
		return new Simulation(currentSimulation);
	}

	@Override
	public PersistentSimulation getSimulation() {
		if (currentSimulation != null)
			return new Simulation(currentSimulation);
		return null;
	}

	@Override
	public PersistentSimulation getSimulationById(long id) {
		SimulationBean s = findSimulationId(id);
		if (s != null)
			return new Simulation(s);
		else
			return null;
	}

	@Override
	public List<Long> getSimulations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSimulation(PersistentSimulation sim) {
		if (sim == null)
			this.currentSimulation = null;
		else
			this.currentSimulation = findSimulationId(sim.getID());
	}

	@Override
	public PersistentAgent createAgent(UUID agentID, String name) {
		if (this.currentSimulation != null) {
			loadAgents(this.currentSimulation.id);

			Agent agent = new Agent();
			agent.iD = agentID;
			agent.name = name;

			this.currentSimulation.agents.add(agent);
			this.agents.put(agentID, agent);

			return agent;
		}
		throw new RuntimeException("Cannot create agent with no simulation.");
	}

	@Override
	public PersistentAgent getAgent(UUID agentID) {
		if (this.currentSimulation != null) {
			loadAgents(this.currentSimulation.id);

			return this.agents.get(agentID);
		}
		return null;
	}

	@Override
	public TransientAgentState getAgentState(UUID agentID, int time) {
		try {
			return getAgent(agentID).getState(time);
		} catch (NullPointerException e) {
			return null;
		}
	}

	private SimulationBean findSimulationId(long id) {
		for (SimulationBean sim : simulations) {
			if (sim.id == id)
				return sim;
		}
		return null;
	}

	private void addJsonObject(String filename, Object object) {
		this.writeQueue.add(filename);
		this.jsonObjects.put(filename, object);
	}

	private PersistentEnvironment getEnvironment(long simId) {
		Environment env;
		String filename = baseStoragePath + "env_" + simId + ".json";
		File envFile = new File(filename);
		try {
			if (envFile.exists()) {
				env = mapper.readValue(envFile, Environment.class);
			} else {
				env = new Environment();
				env.simId = simId;
			}
			addJsonObject(filename, env);
			return env;
		} catch (IOException e) {
			logger.warn("Unable to read environment file", e);
			return null;
		}
	}

	private void loadAgents(long simId) {
		if (agentsLoaded != simId) {
			SimulationBean sim = findSimulationId(simId);
			if (sim == null)
				return;

			String filename = baseStoragePath + "agents_" + simId + ".json";
			File agentsFile = new File(filename);
			try {
				// load agent data from json file
				if (agentsFile.exists()) {
					sim.agents = mapper.readValue(agentsFile,
							new TypeReference<List<PersistentAgent>>() {
							});
				} else {
					sim.agents = new LinkedList<PersistentAgent>();
				}

				// store id -> agent index
				this.agents.clear();
				for (PersistentAgent ag : sim.agents) {
					this.agents.put(ag.getID(), ag);
				}
				// mark agents list to be written to file.
				addJsonObject(filename, sim.agents);
				agentsLoaded = simId;
			} catch (IOException e) {
				logger.warn("Unable to read agents file", e);
			}
		}
	}

	static class SimulationBean {

		public long id;
		public String name;
		public String state;
		public String className;
		public int currentTime;
		public int finishTime;
		public long createdAt;
		public long startedAt;
		public long finishedAt;
		public Map<String, String> parameters = new HashMap<String, String>();
		public long parent = 0;

		@JsonIgnore
		List<PersistentAgent> agents = null;

		public SimulationBean() {
			super();
		}

		public SimulationBean(long id, String name, String className,
				String state, int finishTime) {
			super();
			this.id = id;
			this.name = name;
			this.className = className;
			this.currentTime = 0;
			this.finishTime = finishTime;
			this.createdAt = System.currentTimeMillis();
			this.state = state;
		}

	}

	class Simulation implements PersistentSimulation {

		SimulationBean delegate;

		public Simulation(SimulationBean delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		public long getID() {
			return delegate.id;
		}

		@Override
		public void addParameter(String name, String value) {
			delegate.parameters.put(name, value);
		}

		@Override
		public Map<String, String> getParameters() {
			return delegate.parameters;
		}

		@Override
		public int getFinishTime() {
			return delegate.finishTime;
		}

		@Override
		public void setCurrentTime(int time) {
			delegate.currentTime = time;
		}

		@Override
		public int getCurrentTime() {
			return delegate.currentTime;
		}

		@Override
		public void setState(String newState) {
			delegate.state = newState;
		}

		@Override
		public String getState() {
			return delegate.state;
		}

		@Override
		public PersistentEnvironment getEnvironment() {
			return JsonStorage.this.getEnvironment(getID());
		}

		@Override
		public void setParentSimulation(PersistentSimulation parent) {
			if (parent != null)
				delegate.parent = parent.getID();
			else
				delegate.parent = 0;
		}

		@Override
		public PersistentSimulation getParentSimulation() {
			if (delegate.parent > 0) {
				SimulationBean s = findSimulationId(delegate.parent);
				if (s != null)
					return new Simulation(s);
			}
			return null;
		}

		@Override
		public List<Long> getChildren() {
			List<Long> children = new LinkedList<Long>();
			for (SimulationBean s : simulations) {
				if (s.parent == getID()) {
					children.add(s.id);
				}
			}
			return children;
		}

		@Override
		public void setFinishedAt(long time) {
			delegate.finishedAt = time;
		}

		@Override
		public long getFinishedAt() {
			return delegate.finishedAt;
		}

		@Override
		public void setStartedAt(long time) {
			delegate.startedAt = time;
		}

		@Override
		public long getStartedAt() {
			return delegate.startedAt;
		}

		@Override
		public long getCreatedAt() {
			return delegate.createdAt;
		}

		@Override
		public String getClassName() {
			return delegate.className;
		}

		@Override
		public String getName() {
			return delegate.name;
		}

		@Override
		public Set<PersistentAgent> getAgents() {
			loadAgents(getID());
			return new HashSet<PersistentAgent>(delegate.agents);
		}
	}

	class Environment implements PersistentEnvironment {

		public long simId = 0;
		public Map<String, String> properties = new HashMap<String, String>();
		public Map<Integer, Map<String, String>> states = new HashMap<Integer, Map<String, String>>();

		public Environment() {
			super();
		}

		@Override
		public Map<String, String> getProperties() {
			return properties;
		}

		@Override
		@JsonIgnore
		public String getProperty(String key) {
			return properties.get(key);
		}

		@Override
		@JsonIgnore
		public void setProperty(String key, String value) {
			properties.put(key, value);
		}

		@Override
		@JsonIgnore
		public Map<String, String> getProperties(int timestep) {
			if (!states.containsKey(timestep))
				states.put(timestep, new HashMap<String, String>());

			return states.get(timestep);
		}

		@Override
		@JsonIgnore
		public String getProperty(String key, int timestep) {
			return getProperties(timestep).get(key);
		}

		@Override
		@JsonIgnore
		public void setProperty(String key, int timestep, String value) {
			getProperties(timestep).put(key, value);
		}

	}

	class Agent implements PersistentAgent {

		public UUID iD;
		public String name;
		public Map<String, String> properties = new HashMap<String, String>();
		public Map<Integer, Map<String, String>> state = new HashMap<Integer, Map<String, String>>();

		@Override
		public UUID getID() {
			return iD;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		@JsonIgnore
		public void setRegisteredAt(int time) {
		}

		@Override
		@JsonIgnore
		public void setDeRegisteredAt(int time) {
		}

		@Override
		public Map<String, String> getProperties() {
			return properties;
		}

		@Override
		@JsonIgnore
		public String getProperty(String key) {
			return properties.get(key);
		}

		@Override
		@JsonIgnore
		public void setProperty(String key, String value) {
			properties.put(key, value);
		}

		@Override
		@JsonIgnore
		public TransientAgentState getState(int time) {
			return new State(time);
		}

		class State implements TransientAgentState {

			final int time;

			public State(int time) {
				super();
				this.time = time;
				if (!state.containsKey(time))
					state.put(time, new HashMap<String, String>());
			}

			@Override
			public int getTime() {
				return time;
			}

			@Override
			public PersistentAgent getAgent() {
				return Agent.this;
			}

			@Override
			public Map<String, String> getProperties() {
				return state.get(time);
			}

			@Override
			public String getProperty(String key) {
				return getProperties().get(key);
			}

			@Override
			public void setProperty(String key, String value) {
				getProperties().put(key, value);
			}

		}

	}

}
