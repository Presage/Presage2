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
package uk.ac.imperial.presage2.core.db.persistent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PersistentSimulation {

	public long getID();

	public void addParameter(String name, Object value);

	public Map<String, Object> getParameters();

	public int getFinishTime();

	public void setCurrentTime(int time);

	public int getCurrentTime();

	public void setState(String newState);

	public String getState();

	public PersistentEnvironment getEnvironment();

	public void setParentSimulation(PersistentSimulation parent);

	public PersistentSimulation getParentSimulation();

	public List<Long> getChildren();

	public void setFinishedAt(long time);

	public long getFinishedAt();

	public void setStartedAt(long time);

	public long getStartedAt();

	public long getCreatedAt();

	public String getClassName();

	public String getName();

	public Set<PersistentAgent> getAgents();

}
