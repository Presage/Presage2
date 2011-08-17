package uk.ac.imperial.presage2.db.graph.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.IteratorUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class GEXFExport {

	final Document dom;
	Element graph;
	Element nodesElement;
	Element edgesElement;

	final Set<Node> nodes = new HashSet<Node>();
	final Set<Relationship> edges = new HashSet<Relationship>();
	boolean written = false;

	GEXFExport() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docb = dbf.newDocumentBuilder();
		dom = docb.newDocument();
	}

	public static GEXFExport createStaticGraph() {
		GEXFExport g = null;
		try {
			g = new GEXFExport();
		} catch (ParserConfigurationException e) {
			return null;
		}
		g.staticHeader();
		return g;
	}

	private void staticHeader() {
		Element gexfRoot = dom.createElement("gexf");
		dom.appendChild(gexfRoot);
		gexfRoot.setAttribute("xmlns", "http://www.gexf.net/1.2draft");
		gexfRoot.setAttribute("version", "1.2");

		graph = dom.createElement("graph");
		graph.setAttribute("mode", "static");
		graph.setAttribute("defaultedgetype", "directed");
		gexfRoot.appendChild(graph);
	}

	private Element getNodesElement() {
		if (nodesElement == null) {
			nodesElement = dom.createElement("nodes");
			graph.appendChild(nodesElement);
		}
		return nodesElement;
	}

	private Element getEdgesElement() {
		if (edgesElement == null) {
			edgesElement = dom.createElement("edges");
			graph.appendChild(edgesElement);
		}
		return edgesElement;
	}

	public void addNode(final Node n) {
		nodes.add(n);
	}

	public void addNodes(final Collection<Node> nodes) {
		this.nodes.addAll(nodes);
	}

	public void addNodes(final Iterable<Node> it) {
		IteratorUtil.addToCollection(it, this.nodes);
	}

	public void addEdge(final Relationship e) {
		this.edges.add(e);
	}

	public void addEdges(final Collection<Relationship> edges) {
		this.edges.addAll(edges);
	}

	public void addEdges(final Iterable<Relationship> edgeIt) {
		IteratorUtil.addToCollection(edgeIt, this.edges);
	}

	private void writeGraph() {
		for (Node n : nodes) {
			Element eNode = dom.createElement("node");
			eNode.setAttribute("label", n.getProperty("label", "").toString());
			for (String key : n.getPropertyKeys()) {
				eNode.setAttribute(key, n.getProperty(key).toString());
			}
			eNode.setAttribute("id", Long.toString(n.getId()));
			getNodesElement().appendChild(eNode);
		}
		nodes.clear();
		for (Relationship r : edges) {
			Element e = dom.createElement("edge");
			for (String key : r.getPropertyKeys()) {
				e.setAttribute(key, r.getProperty(key).toString());
			}
			e.setAttribute("id", Long.toString(r.getId()));
			e.setAttribute("source", Long.toString(r.getStartNode().getId()));
			e.setAttribute("target", Long.toString(r.getEndNode().getId()));
			e.setAttribute("label", r.getType().name());
			getEdgesElement().appendChild(e);
		}
		edges.clear();
	}

	public void writeTo(Result result)
			throws TransformerFactoryConfigurationError, TransformerException {
		writeGraph();
		DOMSource source = new DOMSource(dom);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}

	public void writeTo(File file) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(file);
		StreamResult result = new StreamResult(out);
		
		try {
			writeTo(result);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeTo(String filename) throws FileNotFoundException {
		File f = new File(filename);
		writeTo(f);
	}

}
