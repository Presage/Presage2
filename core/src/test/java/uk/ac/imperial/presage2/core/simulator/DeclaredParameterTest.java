package uk.ac.imperial.presage2.core.simulator;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class DeclaredParameterTest {

	enum EnumTest {
		A, B, C
	};

	static class TestSource {
		@Parameter(name = "testInt")
		public int testInt;
		@Parameter(name = "testDouble")
		public double testDouble;
		@Parameter(name = "testBool")
		public boolean testBool;
		@Parameter(name = "testEnum")
		public EnumTest testEnum;
		@Parameter(name = "testString")
		public String testString;

		@Parameter("testValue")
		public int testValue;
		@Parameter
		public double testFieldName;
		@Parameter(optional = true)
		public boolean testOptional;
	}

	@Test
	public void testTypes() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Random rnd = new Random();
		TestSource source = new TestSource();
		String[] names = { "testInt", "testDouble", "testBool", "testEnum",
				"testString" };
		Object[] values = { rnd.nextInt(), rnd.nextDouble(), rnd.nextBoolean(),
				EnumTest.values()[rnd.nextInt(3)],
				RandomStringUtils.randomAlphanumeric(rnd.nextInt(500)) };
		for (int i = 0; i < names.length; i++) {
			Field f = TestSource.class.getField(names[i]);
			Parameter a1 = f.getAnnotation(Parameter.class);
			DeclaredParameter test = new DeclaredParameter(a1, source, f);

			test.setValue(values[i].toString());
			assertEquals(values[i], f.get(source));
			assertEquals(names[i], test.name);
			assertFalse(test.optional);
		}
	}

	@Test
	public void testShortened() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		TestSource source = new TestSource();

		String[] names = { "testValue", "testFieldName" };
		for (int i = 0; i < names.length; i++) {
			Field f = TestSource.class.getField(names[i]);
			Parameter a1 = f.getAnnotation(Parameter.class);
			DeclaredParameter test = new DeclaredParameter(a1, source, f);
			assertEquals(names[i], test.name);
			assertFalse(test.optional);
		}
	}

	@Test
	public void testOptional() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		TestSource source = new TestSource();
		final String name = "testOptional";
		Field f = TestSource.class.getField(name);
		Parameter a1 = f.getAnnotation(Parameter.class);
		DeclaredParameter test = new DeclaredParameter(a1, source, f);
		assertEquals(name, test.name);
		assertTrue(test.optional);
	}

	@Test
	public void testBadParameter() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		TestSource source = new TestSource();

		// int given double string
		Field f = TestSource.class.getField("testInt");
		Parameter a = f.getAnnotation(Parameter.class);
		DeclaredParameter test = new DeclaredParameter(a, source, f);
		try {
			test.setValue("0.5");
			fail();
		} catch (NumberFormatException e) {
		}

		// double given word string
		f = TestSource.class.getField("testDouble");
		a = f.getAnnotation(Parameter.class);
		test = new DeclaredParameter(a, source, f);
		try {
			test.setValue("hi");
			fail();
		} catch (NumberFormatException e) {
		}

		// boolean will assume anything but 'true' is false
		f = TestSource.class.getField("testBool");
		a = f.getAnnotation(Parameter.class);
		test = new DeclaredParameter(a, source, f);
		test.setValue("true");
		assertEquals(true, f.get(source));
		test.setValue("hi");
		assertEquals(false, f.get(source));

		// enum given value not in enum
		f = TestSource.class.getField("testEnum");
		a = f.getAnnotation(Parameter.class);
		test = new DeclaredParameter(a, source, f);
		try {
			test.setValue("1");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

}
