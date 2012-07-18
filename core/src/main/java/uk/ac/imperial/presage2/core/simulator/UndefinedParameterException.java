package uk.ac.imperial.presage2.core.simulator;

final class UndefinedParameterException extends Exception {

	private static final long serialVersionUID = 1L;

	UndefinedParameterException(String parameter) {
		super("Undefined simulation parameter: " + parameter);
	}

}
