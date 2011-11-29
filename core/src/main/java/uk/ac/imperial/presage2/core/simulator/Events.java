package uk.ac.imperial.presage2.core.simulator;

import uk.ac.imperial.presage2.core.event.Event;


public class Events {

	public static class Initialised implements Event {
	}
	
	public static final Event INITIALISED = new Initialised();
	
}
