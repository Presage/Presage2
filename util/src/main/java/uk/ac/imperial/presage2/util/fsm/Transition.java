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
package uk.ac.imperial.presage2.util.fsm;

public class Transition {

	private final String name;
	private final State start;
	private final State end;
	private TransitionCondition condition;
	private Action action;

	/**
	 * @param name
	 * @param start
	 * @param end
	 * @param condition
	 * @param action
	 */
	Transition(String name, State start, State end, TransitionCondition condition, Action action) {
		super();
		this.name = name;
		this.start = start;
		this.end = end;
		this.condition = condition;
		this.action = action;
	}

	public String getName() {
		return name;
	}

	State getStart() {
		return start;
	}

	State getEnd() {
		return end;
	}

	TransitionCondition getCondition() {
		return condition;
	}

	Action getAction() {
		return action;
	}

	void setCondition(TransitionCondition condition) {
		this.condition = condition;
	}

	void setAction(Action action) {
		this.action = action;
	}

}
