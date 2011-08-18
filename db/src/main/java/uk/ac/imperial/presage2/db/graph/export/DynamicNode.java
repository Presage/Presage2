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

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DynamicNode extends Node {

	final String start;
	
	class Attvalue {
		final String attr;
		final String value;
		final String start;
		String end;
		
		public Attvalue(String attr, String value, String start, String end) {
			super();
			this.attr = attr;
			this.value = value;
			this.start = start;
			this.end = end;
		}

		public Attvalue(String attr, String value, String start) {
			super();
			this.attr = attr;
			this.value = value;
			this.start = start;
		}
		
	}
	
	List<Attvalue> attValues = new LinkedList<Attvalue>();
	
	public DynamicNode(String id, String label, String start) {
		super(id, label);
		this.start = start;
	}
	
	public void addAttributeValue(String attribute, String value, String start) {
		attValues.add(new Attvalue(attribute, value, start));
	}
	
	public void addAttributeValue(String attribute, String value, String start, String end) {
		attValues.add(new Attvalue(attribute, value, start, end));
	}

	@Override
	public Element getElement(Document dom) {
		Element e = super.getElement(dom);
		e.setAttribute("start", start);
		Element av = dom.createElement("attvalues");
		for(Attvalue value : attValues) {
			Element v = dom.createElement("attvalue");
			v.setAttribute("for", value.attr);
			v.setAttribute("value", value.value);
			v.setAttribute("start", value.start);
			if(value.end != null)
				v.setAttribute("end", value.end);
			av.appendChild(v);
		}
		e.appendChild(av);
		return e;
	}

}
