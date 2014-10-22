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

import java.util.Map;
import java.util.UUID;

public interface PersistentSimulation {

	long getID();

	void addParameter(String name, String value);

	Map<String, String> getParameters();

	void setCurrentTime(int time);

	int getCurrentTime();

	void setState(String newState);

	String getState();

	void setFinishedAt(long time);

	long getFinishedAt();

	void setStartedAt(long time);

	long getStartedAt();

	String getClassName();

	String getName();

	int getFinishTime();

	PersistentEnvironment getEnvironment();

	void storeTuple(String key, String value);

	void storeTuple(String key, int value);

	void storeTuple(String key, double value);

	void storeTuple(String key, int t, String value);

	void storeTuple(String key, int t, int value);

	void storeTuple(String key, int t, double value);

	void storeTuple(String key, UUID agent, String value);

	void storeTuple(String key, UUID agent, int value);

	void storeTuple(String key, UUID agent, double value);

	void storeTuple(String key, UUID agent, int t, String value);

	void storeTuple(String key, UUID agent, int t, int value);

	void storeTuple(String key, UUID agent, int t, double value);

	String fetchTuple(String key);

	<T> T fetchTuple(String key, Class<T> type);

	String fetchTuple(String key, int t);

	<T> T fetchTuple(String key, int t, Class<T> type);

	String fetchTuple(String key, UUID agent);

	<T> T fetchTuple(String key, UUID agent, Class<T> type);

	String fetchTuple(String key, UUID agent, int t);

	<T> T fetchTuple(String key, UUID agent, int t, Class<T> type);

}
