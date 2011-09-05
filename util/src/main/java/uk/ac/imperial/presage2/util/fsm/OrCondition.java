package uk.ac.imperial.presage2.util.fsm;

/**
 * {@link TransitionCondition} which does a boolean OR of all the conditions
 * given to it and returns that result.
 * 
 * @author Sam Macbeth
 * 
 */
public class OrCondition implements TransitionCondition {

	private final TransitionCondition[] conditions;

	/**
	 * @param conditions
	 *            {@link TransitionCondition} to OR together.
	 */
	public OrCondition(TransitionCondition... conditions) {
		super();
		this.conditions = conditions;
	}

	@Override
	public boolean allow(Object event, Object entity, State state) {
		for (TransitionCondition condition : conditions) {
			if (condition.allow(event, entity, state))
				return true;
		}
		return false;
	}

}
