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
package uk.ac.imperial.presage2.db.graph.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Edge {

	final String id;
	final String source;
	final String target;
	final String label;
	
	/**
	 * @param id
	 * @param source
	 * @param target
	 * @param label
	 */
	public Edge(String id, String source, String target, String label) {
		super();
		this.id = id;
		this.source = source;
		this.target = target;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getLabel() {
		return label;
	}
	
	public Element getElement(Document dom) {
		Element e = dom.createElement("edge");
		e.setAttribute("id", getId());
		e.setAttribute("source", getSource());
		e.setAttribute("target", getTarget());
		e.setAttribute("label", getLabel());
		return e;
	}
	
}
