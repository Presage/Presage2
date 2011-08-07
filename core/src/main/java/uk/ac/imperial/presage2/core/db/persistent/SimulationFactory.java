package uk.ac.imperial.presage2.core.db.persistent;

public interface SimulationFactory {

	public PersistentSimulation create(String name, String classname, String state, int finishTime);

	public PersistentSimulation get(long simulationID);

}
