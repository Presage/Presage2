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
package uk.ac.imperial.presage2.util.location;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.db.GraphDB;
import uk.ac.imperial.presage2.core.db.Transaction;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.plugin.Plugin;
import uk.ac.imperial.presage2.db.graph.AgentNode.AgentRelationships;
import uk.ac.imperial.presage2.db.graph.DataExport;
import uk.ac.imperial.presage2.db.graph.export.DynamicNode;
import uk.ac.imperial.presage2.db.graph.export.GEXFExport;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

import com.google.inject.Inject;

public class LocationStoragePlugin implements Plugin {

	private final Logger logger = Logger.getLogger(LocationStoragePlugin.class);

	private GraphDB storage;
	private DataExport exporter = null;

	private final EnvironmentMembersService membersService;
	private final LocationService locService;

	private final Time time;

	public LocationStoragePlugin() {
		super();
		storage = null;
		locService = null;
		membersService = null;
		time = null;
		logger.info("I wasn't given a storage service, I won't do anything!");
	}

	@Inject
	public LocationStoragePlugin(EnvironmentServiceProvider serviceProvider,
			Time t) throws UnavailableServiceException {
		this.storage = null;
		this.membersService = serviceProvider
				.getEnvironmentService(EnvironmentMembersService.class);
		this.locService = serviceProvider
				.getEnvironmentService(LocationService.class);
		this.time = t;
	}

	@Inject(optional = true)
	public void setStorage(GraphDB storage) {
		this.storage = storage;
	}

	@Inject(optional = true)
	public void setDataExporter(DataExport exp) {
		this.exporter = exp;
	}

	@Override
	public void incrementTime() {
		if (this.storage != null) {
			Transaction tx = this.storage.startTransaction();
			try {
				for (UUID pid : this.membersService.getParticipants()) {
					Location l;
					try {
						l = this.locService.getAgentLocation(pid);
					} catch (Exception e) {
						logger.debug("Exception getting agent location.", e);
						continue;
					}
					TransientAgentState state = this.storage.getAgentState(pid,
							time.intValue());
					state.setProperty("x", l.getX());
					state.setProperty("y", l.getY());
					state.setProperty("z", l.getZ());
				}
				tx.success();
			} finally {
				tx.finish();
			}
		}
		time.increment();
	}

	@Override
	public void initialise() {
	}

	@Override
	public void execute() {
	}

	@Override
	public void onSimulationComplete() {
		if (exporter != null) {
			Node n = exporter.getSimulationNode();
			GEXFExport gexf = GEXFExport.createDynamicGraph();
			gexf.addAttribute("x", "x", "float");
			gexf.addAttribute("y", "y", "float");
			gexf.addAttribute("z", "z", "float");
			for (Relationship agentRel : n
					.getRelationships(exporter.getParticipantInRelationship(),
							Direction.INCOMING)) {
				Node agent = agentRel.getStartNode();
				DynamicNode exNode = new DynamicNode(Long.toString(agent
						.getId()), agent.getProperty("label", "").toString(),
						agentRel.getProperty("registeredAt", "0").toString());
				for (Relationship r : agent.getRelationships(
						AgentRelationships.TRANSIENT_STATE, Direction.OUTGOING)) {
					Node state = r.getEndNode();
					String time = r.getProperty("time").toString();
					exNode.addAttributeValue("x", state.getProperty("x")
							.toString(), time);
					exNode.addAttributeValue("y", state.getProperty("y")
							.toString(), time);
					exNode.addAttributeValue("z", state.getProperty("z")
							.toString(), time);
				}
				gexf.addNode(exNode);
			}
			try {
				gexf.writeTo("locations.gexf");
			} catch (FileNotFoundException e) {
			}
		}
	}
}
