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
package uk.ac.imperial.presage2.db.graph;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

class NodeDelegate implements Node {

	private final Node delegate;

	protected NodeDelegate(Node delegate) {
		super();
		this.delegate = delegate;
	}

	public Node getUnderlyingNode() {
		return delegate;
	}

	public Relationship createRelationshipTo(Node arg0, RelationshipType arg1) {
		return delegate.createRelationshipTo(arg0, arg1);
	}

	public void delete() {
		delegate.delete();
	}

	public GraphDatabaseService getGraphDatabase() {
		return delegate.getGraphDatabase();
	}

	public long getId() {
		return delegate.getId();
	}

	public Object getProperty(String arg0, Object arg1) {
		return delegate.getProperty(arg0, arg1);
	}

	public Object getProperty(String arg0) {
		return delegate.getProperty(arg0);
	}

	public Iterable<String> getPropertyKeys() {
		return delegate.getPropertyKeys();
	}

	@SuppressWarnings("deprecation")
	public Iterable<Object> getPropertyValues() {
		return delegate.getPropertyValues();
	}

	public Iterable<Relationship> getRelationships() {
		return delegate.getRelationships();
	}

	public Iterable<Relationship> getRelationships(Direction arg0,
			RelationshipType... arg1) {
		return delegate.getRelationships(arg0, arg1);
	}

	public Iterable<Relationship> getRelationships(Direction arg0) {
		return delegate.getRelationships(arg0);
	}

	public Iterable<Relationship> getRelationships(RelationshipType arg0,
			Direction arg1) {
		return delegate.getRelationships(arg0, arg1);
	}

	public Iterable<Relationship> getRelationships(RelationshipType... arg0) {
		return delegate.getRelationships(arg0);
	}

	public Relationship getSingleRelationship(RelationshipType arg0,
			Direction arg1) {
		return delegate.getSingleRelationship(arg0, arg1);
	}

	public boolean hasProperty(String arg0) {
		return delegate.hasProperty(arg0);
	}

	public boolean hasRelationship() {
		return delegate.hasRelationship();
	}

	public boolean hasRelationship(Direction arg0, RelationshipType... arg1) {
		return delegate.hasRelationship(arg0, arg1);
	}

	public boolean hasRelationship(Direction arg0) {
		return delegate.hasRelationship(arg0);
	}

	public boolean hasRelationship(RelationshipType arg0, Direction arg1) {
		return delegate.hasRelationship(arg0, arg1);
	}

	public boolean hasRelationship(RelationshipType... arg0) {
		return delegate.hasRelationship(arg0);
	}

	public Object removeProperty(String arg0) {
		return delegate.removeProperty(arg0);
	}

	public void setProperty(String arg0, Object arg1) {
		delegate.setProperty(arg0, arg1);
	}

	public Traverser traverse(Order arg0, StopEvaluator arg1,
			ReturnableEvaluator arg2, Object... arg3) {
		return delegate.traverse(arg0, arg1, arg2, arg3);
	}

	public Traverser traverse(Order arg0, StopEvaluator arg1,
			ReturnableEvaluator arg2, RelationshipType arg3, Direction arg4,
			RelationshipType arg5, Direction arg6) {
		return delegate.traverse(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public Traverser traverse(Order arg0, StopEvaluator arg1,
			ReturnableEvaluator arg2, RelationshipType arg3, Direction arg4) {
		return delegate.traverse(arg0, arg1, arg2, arg3, arg4);
	}

}
