package uk.ac.imperial.presage2.core.db.persistent;

public interface PersistentEnvironment {

	public Object getProperty(String key);

	public void setProperty(String key, Object value);

	public Object getProperty(String key, int timestep);

	public void setProperty(String key, int timestep, Object value);

}
