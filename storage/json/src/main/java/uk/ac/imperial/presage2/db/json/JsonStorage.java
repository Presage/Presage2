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
package uk.ac.imperial.presage2.db.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.TupleStorageService;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class JsonStorage extends TupleStorageService implements
		DatabaseService {

	private final Logger logger = Logger.getLogger(JsonStorage.class);

	final String baseStoragePath;
	final String simulationsFileName = "simulations.json";

	boolean started = false;
	long maxSimId = 0;

	ObjectMapper mapper;
	ArrayNode root;

	Map<Long, ObjectNode> simulations;

	public JsonStorage() {
		this("data/");
	}

	@Inject
	public JsonStorage(
			@Named(JsonModule.STORAGEPATH_KEY) String baseStoragePath) {
		super();
		this.baseStoragePath = baseStoragePath;
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Override
	public void start() throws Exception {
		// check base path
		if (this.baseStoragePath.length() > 0) {
			File basePath = new File(this.baseStoragePath);
			if (!basePath.exists()) {
				basePath.mkdirs();
			}
			if (!basePath.isDirectory()) {
				throw new RuntimeException("Base path is not a directory.");
			}
		}
		// open simulations file if exists, create otherwise
		File simulationsFile = new File(baseStoragePath + simulationsFileName);

		if (!simulationsFile.exists()) {
			simulationsFile.createNewFile();
			root = mapper.createArrayNode();
			mapper.writeValue(simulationsFile, root);
		}

		// read simulations content in to JsonNode
		simulations = new HashMap<Long, ObjectNode>();
		try {
			root = mapper.readValue(simulationsFile, ArrayNode.class);

			for (JsonNode s : root) {
				simulations.put(s.get("id").asLong(), (ObjectNode) s);
			}
		} catch (JsonMappingException e) {
			logger.warn("Couldn't parse simulations file: " + baseStoragePath
					+ simulationsFileName, e);
		}
		// find max simID for new insertions
		try {
			maxSimId = Collections.max(simulations.keySet());
		} catch (NoSuchElementException e) {
			// empty set
			maxSimId = 0;
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
				mapper.writeValue(simulationsFile, root);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		started = false;
	}

	@Override
	public List<Long> getSimulations() {
		return Collections.unmodifiableList(new ArrayList<Long>(simulations
				.keySet()));
	}

	@Override
	protected long getNextId() {
		return ++maxSimId;
	}

	private ObjectNode getSim(long id) {
		ObjectNode s = simulations.get(id);
		if (s == null) {
			s = mapper.createObjectNode();
			s.put("id", id);
			simulations.put(id, s);
			root.add(s);
		}
		return s;
	}

	private <T> T returnAsType(JsonNode n, Class<T> type) {
		if (type == String.class)
			return type.cast(n.textValue());
		else if (type == Integer.class || type == Integer.TYPE)
			return type.cast(n.asInt());
		else if (type == Double.class || type == Double.TYPE)
			return type.cast(n.asDouble());
		else if (type == Boolean.class || type == Boolean.TYPE)
			return type.cast(n.asBoolean());
		else if (type == Long.class || type == Long.TYPE)
			return type.cast(n.asLong());

		throw new RuntimeException("Unknown type cast request");
	}

	@Override
	protected void storeParameter(long id, String key, String value) {
		getSim(id).with("parameters").put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, String value) {
		getSim(id).with("properties").put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, int value) {
		getSim(id).with("properties").put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, double value) {
		getSim(id).with("properties").put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, int t, String value) {
		getSim(id).with("transproperties").with(Integer.toString(t))
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, int t, int value) {
		getSim(id).with("transproperties").with(Integer.toString(t))
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, int t, double value) {
		getSim(id).with("transproperties").with(Integer.toString(t))
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, String value) {
		getSim(id).with("agentproperties").with(agent.toString())
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int value) {
		getSim(id).with("agentproperties").with(agent.toString())
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, double value) {
		getSim(id).with("agentproperties").with(agent.toString())
				.put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			String value) {
		getSim(id).with("agenttransproperties").with(agent.toString())
				.with(Integer.toString(t)).put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t, int value) {
		getSim(id).with("agenttransproperties").with(agent.toString())
				.with(Integer.toString(t)).put(key, value);
	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			double value) {
		getSim(id).with("agenttransproperties").with(agent.toString())
				.with(Integer.toString(t)).put(key, value);
	}

	@Override
	protected Set<String> fetchParameterKeys(long id) {
		Set<String> keys = new HashSet<String>();
		for (Iterator<String> it = getSim(id).with("parameters").fieldNames(); it
				.hasNext();) {
			keys.add(it.next());
		}
		return keys;
	}

	@Override
	protected String fetchParameter(long id, String key) {
		return getSim(id).with("parameters").path(key).textValue();
	}

	@Override
	protected <T> T fetchTuple(long id, String key, Class<T> type) {
		JsonNode n = getSim(id).with("properties").path(key);
		return returnAsType(n, type);
	}

	@Override
	protected <T> T fetchTuple(long id, String key, int t, Class<T> type) {
		JsonNode n = getSim(id).with("transproperties")
				.with(Integer.toString(t)).path(key);
		return returnAsType(n, type);
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, Class<T> type) {
		JsonNode n = getSim(id).with("agentproperties").with(agent.toString())
				.path(key);
		return returnAsType(n, type);
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, int t,
			Class<T> type) {
		JsonNode n = getSim(id).with("agenttransproperties")
				.with(agent.toString()).with(Integer.toString(t)).path(key);
		return returnAsType(n, type);
	}

}
