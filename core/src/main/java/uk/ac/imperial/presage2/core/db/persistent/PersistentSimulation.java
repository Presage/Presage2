package uk.ac.imperial.presage2.core.db.persistent;

import java.util.Map;

public interface PersistentSimulation {

	public void addParameter(String name, Object value);

	public Map<String, Object> getParameters();

	public int getFinishTime();

	public void setCurrentTime(int time);

	public int getCurrentTime();

	public void setState(String newState);

	public String getState();

	public void addChild(PersistentSimulation child);

	public void setParentSimulation(PersistentSimulation parent);

	public PersistentSimulation getParentSimulation();

	public void setFinishedAt(long time);

	public long getFinishedAt();

	public void setStartedAt(long time);

	public long getStartedAt();

	public long getCreatedAt();

	public String getClassName();

	public String getName();

}
