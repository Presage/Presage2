package uk.ac.imperial.presage2.util.fsm;

/**
 * {@link TransitionCondition} which returns the opposite of the condition given
 * to it.
 * 
 * @author Sam Macbeth
 * 
 */
public class NotCondition implements TransitionCondition {

	private final TransitionCondition condition;

	public NotCondition(TransitionCondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean allow(Object event, Object entity, State state) {
		return !(condition.allow(event, entity, state));
	}

}
