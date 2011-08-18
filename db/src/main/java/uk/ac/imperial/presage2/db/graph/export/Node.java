package uk.ac.imperial.presage2.db.graph.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Node {

	final String id;
	final String label;

	/**
	 * @param id
	 * @param label
	 */
	public Node(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
	
	public Element getElement(Document dom) {
		Element n = dom.createElement("node");
		n.setAttribute("label", getLabel());
		n.setAttribute("id", getId());
		return n;
	}

}
