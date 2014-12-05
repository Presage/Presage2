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
package uk.ac.imperial.presage2.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class Simulation implements PersistentSimulation {

	boolean dirty = false;

	final SqlStorage sto;

	long id = 0;
	String name = "";
	String state = "NOT STARTED";
	String classname = "";
	int currentTime = 0;
	int finishTime = 0;
	long createdAt = 0;
	long startedAt = 0;
	long finishedAt = 0;
	long parent = 0;
	Map<String, String> parameters = new HashMap<String, String>();
	Environment env;
	Set<Agent> agents = new HashSet<Agent>();

	Simulation(long id, String name, String classname, String state,
			int finishTime, SqlStorage sto) {
		super();
		this.id = id;
		this.name = name;
		this.classname = classname;
		this.state = state;
		this.finishTime = finishTime;
		this.createdAt = new Date().getTime();
		this.sto = sto;
		this.env = new Environment(this.id, this.sto);
	}

	public Simulation(ResultSet simRow, SqlStorage sto) throws SQLException {
		super();
		this.sto = sto;
		this.id = simRow.getLong(1);
		this.name = simRow.getString(2);
		this.state = simRow.getString(3);
		this.classname = simRow.getString(4);
		this.currentTime = simRow.getInt(5);
		this.finishTime = simRow.getInt(6);
		this.createdAt = simRow.getLong(7);
		this.startedAt = simRow.getLong(8);
		this.finishedAt = simRow.getLong(9);
		this.parent = simRow.getLong(10);
		this.env = new Environment(this.id, this.sto);
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public int getFinishTime() {
		return this.finishTime;
	}

	@Override
	public void setCurrentTime(int time) {
		this.currentTime = time;
		this.sto.simulationQ.add(this);
	}

	@Override
	public int getCurrentTime() {
		return this.currentTime;
	}

	@Override
	public void setState(String newState) {
		this.state = newState;
		this.sto.simulationQ.add(this);
	}

	@Override
	public String getState() {
		return this.state;
	}

	@Override
	public void setParentSimulation(PersistentSimulation parent) {
		if (parent == null)
			this.parent = 0;
		else
			this.parent = parent.getID();
		this.sto.simulationQ.add(this);
	}

	@Override
	public List<Long> getChildren() {
		return this.sto.getChildren(this.getID());
	}

	@Override
	public void setFinishedAt(long time) {
		this.finishedAt = time;
		this.sto.simulationQ.add(this);
	}

	@Override
	public long getFinishedAt() {
		return this.finishedAt;
	}

	@Override
	public void setStartedAt(long time) {
		this.startedAt = time;
		this.sto.simulationQ.add(this);
	}

	@Override
	public long getStartedAt() {
		return this.startedAt;
	}

	@Override
	public long getCreatedAt() {
		return this.createdAt;
	}

	@Override
	public String getClassName() {
		return this.classname;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addParameter(String name, String value) {
		this.sto.setParameter(id, name, value);
		this.parameters.put(name, value);
	}

	@Override
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	@Override
	public PersistentEnvironment getEnvironment() {
		sto.fetchData(this.id);
		return this.env;
	}

	@Override
	public PersistentSimulation getParentSimulation() {
		return this.sto.getSimulationById(parent);
	}

	@Override
	public Set<PersistentAgent> getAgents() {
		sto.fetchData(this.id);
		Set<PersistentAgent> agentsCopy = new HashSet<PersistentAgent>(agents);
		return agentsCopy;
	}

	void setParameters(ResultSet paramsRow) throws SQLException {
		this.parameters.clear();
		while (paramsRow.next()) {
			this.parameters.put(paramsRow.getString(1), paramsRow.getString(2));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Simulation other = (Simulation) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
