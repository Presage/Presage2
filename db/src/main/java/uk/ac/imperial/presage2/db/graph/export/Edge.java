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
