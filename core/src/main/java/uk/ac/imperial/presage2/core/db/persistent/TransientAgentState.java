package uk.ac.imperial.presage2.core.db.persistent;

public interface TransientAgentState {

	int getTime();

	PersistentAgent getAgent();

	Object getProperty(String key);

	void setProperty(String key, Object value);

}
