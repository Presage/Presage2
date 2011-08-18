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
