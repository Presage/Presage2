/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.core.simulator;

import java.lang.reflect.Field;
import java.util.LinkedList;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

class DeclaredParameter {

	final String name;
	final Object source;
	final Field field;
	final Class<?> type;
	final boolean optional;
	String stringValue;
	TypeHandler handler;

	static LinkedList<TypeHandler> handlers = new LinkedList<TypeHandler>();

	DeclaredParameter(Parameter param, Object source, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		super();
		this.name = param.name();
		this.optional = param.optional();
		this.source = source;
		this.field = field;
		this.type = field.getType();
		Object defaultValue = field.get(source);
		stringValue = defaultValue != null ? defaultValue.toString() : "";
		handler = getHandler();
	}

	synchronized TypeHandler getHandler() {
		if (handlers.size() == 0) {
			// initialise
			handlers.add(new IntegerHandler());
			handlers.add(new DoubleHandler());
			handlers.add(new BooleanHandler());
			handlers.add(new EnumHandler());
			handlers.add(new StringHandler());
		}
		for (TypeHandler h : handlers) {
			if (h.canHandle(type)) {
				return h;
			}
		}
		throw new RuntimeException("No handlers for parameter type " + type
				+ ", for parameter " + name + " declared in " + source);
	}

	void setValue(String value) throws IllegalArgumentException,
			IllegalAccessException {
		this.stringValue = value;
		this.handler.setValue(source, field, value);
	}

	interface TypeHandler {
		boolean canHandle(Class<?> type);

		void setValue(Object obj, Field field, String value)
				throws IllegalArgumentException, IllegalAccessException;

		AbstractModule getBinding(DeclaredParameter p);
	}

	static class IntegerHandler implements TypeHandler {

		@Override
		public boolean canHandle(Class<?> type) {
			return type == Integer.class || type == Integer.TYPE;
		}

		@Override
		public void setValue(Object obj, Field field, String value)
				throws NumberFormatException, IllegalArgumentException,
				IllegalAccessException {
			field.setInt(obj, Integer.parseInt(value));
		}

		@Override
		public AbstractModule getBinding(final DeclaredParameter p) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					Named n = Names.named("params." + p.name);
					bind(Integer.TYPE).annotatedWith(n).toInstance(
							Integer.parseInt(p.stringValue));
					bind(Integer.class).annotatedWith(n).toInstance(
							Integer.parseInt(p.stringValue));
				}
			};
		}
	}

	static class DoubleHandler implements TypeHandler {
		@Override
		public boolean canHandle(Class<?> type) {
			return type == Double.class || type == Double.TYPE;
		}

		@Override
		public void setValue(Object obj, Field field, String value)
				throws NumberFormatException, IllegalArgumentException,
				IllegalAccessException {
			field.setDouble(obj, Double.parseDouble(value));
		}

		@Override
		public AbstractModule getBinding(final DeclaredParameter p) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					Named n = Names.named("params." + p.name);
					bind(Double.TYPE).annotatedWith(n).toInstance(
							Double.parseDouble(p.stringValue));
					bind(Double.class).annotatedWith(n).toInstance(
							Double.parseDouble(p.stringValue));
				}
			};
		}
	}

	static class BooleanHandler implements TypeHandler {
		@Override
		public boolean canHandle(Class<?> type) {
			return type == Boolean.class || type == Boolean.TYPE;
		}

		@Override
		public void setValue(Object obj, Field field, String value)
				throws NumberFormatException, IllegalArgumentException,
				IllegalAccessException {
			field.setBoolean(obj, Boolean.parseBoolean(value));
		}

		@Override
		public AbstractModule getBinding(final DeclaredParameter p) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					Named n = Names.named("params." + p.name);
					bind(Boolean.TYPE).annotatedWith(n).toInstance(
							Boolean.parseBoolean(p.stringValue));
					bind(Boolean.class).annotatedWith(n).toInstance(
							Boolean.parseBoolean(p.stringValue));
				}
			};
		}
	}

	static class EnumHandler implements TypeHandler {
		@Override
		public boolean canHandle(Class<?> type) {
			return type.isEnum();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void setValue(Object obj, Field field, String value)
				throws NumberFormatException, IllegalArgumentException,
				IllegalAccessException {
			field.set(obj, Enum.valueOf((Class<Enum>) field.getType(), value));
		}

		@Override
		public AbstractModule getBinding(final DeclaredParameter p) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					Named n = Names.named("params." + p.name);
					// enum binding not possible so we use a string binding
					bind(String.class).annotatedWith(n).toInstance(
							p.stringValue);
				}
			};
		}
	}

	static class StringHandler implements TypeHandler {
		@Override
		public boolean canHandle(Class<?> type) {
			return type == String.class;
		}

		@Override
		public void setValue(Object obj, Field field, String value)
				throws NumberFormatException, IllegalArgumentException,
				IllegalAccessException {
			field.set(obj, value);
		}

		@Override
		public AbstractModule getBinding(final DeclaredParameter p) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					Named n = Names.named("params." + p.name);
					bind(String.class).annotatedWith(n).toInstance(
							p.stringValue);
				}
			};
		}
	}

}
