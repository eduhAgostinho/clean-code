package cleancode;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ArgsTest {

    @Test
    public void testCreateWithNoSchemaOrArguments() throws Exception {
        Args args = new Args("", new String[0]);
        assertEquals(0, args.cardinality());
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testWithNoSchemaButWithOneArgument() throws Exception {
        Args args = new Args("", new String[] { "-x" });
        assertFalse(args.isValid());
        assertEquals("Argument(s) -x unexpected.", args.errorMessage());
    }

    @Test
    public void testWithNoSchemaButWithMultipleArguments2() throws Exception {
        Args args = new Args("", new String[] { "-x", "-y" });
        assertFalse(args.isValid());
        assertEquals("Argument(s) -x unexpected.", args.errorMessage());
    }

    @Test
    public void testArgumentsSeparated() throws Exception {
        Args args = new Args("x,y", new String[] { "-x", "True", "-y", "False" });
        assertTrue(args.isValid());
        assertTrue(args.getBoolean('x'));
        assertFalse(args.getBoolean('y'));
    }

    @Test
    public void testNonLetterSchema() throws Exception {
        exception.expect(ParseException.class);
        exception.expectMessage("Bad character:*in Args format: *");

        new Args("*", new String[] {});
    }

    @Test
    public void testInvalidSchemaFormat() throws Exception {
        exception.expect(ParseException.class);
        exception.expectMessage("Argument: f has invalid format: ~.");

        new Args("f~", new String[] { "f", "teste" });
    }

    @Test
    public void testInvalidSchemaFormat2() throws Exception {
        exception.expect(ParseException.class);
        exception.expectMessage("Argument: f has invalid format: ~.");

        new Args("x#,f~", new String[] { "-xf", "teste", "2.5" });
    }

    @Test
    public void testSimpleBooleanTruePresent() throws Exception {
        Args args = new Args("x", new String[] { "-x", "true" });
        assertEquals(1, args.cardinality());
        assertTrue(args.getBoolean('x'));
    }

    @Test
    public void testSimpleBooleanFalsePresent() throws Exception {
        Args args = new Args("x", new String[] { "-x", "false" });
        assertEquals(1, args.cardinality());
        assertFalse(args.getBoolean('x'));
    }

    @Test
    public void testMissingBooleanArgument() throws Exception {
        Args args = new Args("x", new String[] { "-x" });
        assertFalse(args.isValid());
    }

    @Test
    public void testInvalidBoolean() throws Exception {
        Args args = new Args("x", new String[] { "-x", "Truthy" });
        assertEquals(1, args.cardinality());
        assertFalse(args.getBoolean('x'));
    }

    @Test
    public void testMultipleBooleans() throws Exception {
        Args args = new Args("x,y", new String[] { "-xy", "true", "true" });
        assertEquals(2, args.cardinality());
        assertTrue(args.getBoolean('x'));
        assertTrue(args.getBoolean('y'));
    }

    @Test
    public void testSpacesInFormat() throws Exception {
        Args args = new Args("x, y", new String[] { "-xy", "true", "true" });
        assertEquals(2, args.cardinality());
        assertTrue(args.has('x'));
        assertTrue(args.has('y'));
        assertTrue(args.getBoolean('x'));
        assertTrue(args.getBoolean('y'));
    }

    @Test
    public void testSimpleStringPresent() throws Exception {
        Args args = new Args("x*", new String[] { "-x", "param" });
        assertEquals(1, args.cardinality());
        assertTrue(args.has('x'));
        assertEquals("param", args.getString('x'));
    }

    @Test
    public void testMissingStringArgument() throws Exception {
        Args args = new Args("x*", new String[] { "-x" });
        assertFalse(args.isValid());
        assertEquals("Could not find string parameter for -x.",
                args.errorMessage());
        assertEquals("", args.getString('w'));
    }

    @Test
    public void testSimpleIntPresent() throws Exception {
        Args args = new Args("x#", new String[] { "-x", "42" });
        assertTrue(args.isValid());
        assertEquals(1, args.cardinality());
        assertTrue(args.has('x'));
        assertEquals((Integer) 42, args.getInt('x'));
    }

    @Test
    public void testInvalidInteger() throws Exception {
        Args args = new Args("x#", new String[] { "-x", "Forty two" });
        assertFalse(args.isValid());
        assertEquals("Argument -x expects an integer but was 'Forty two'.",
                args.errorMessage());
    }

    @Test
    public void testMissingInteger() throws Exception {
        Args args = new Args("x#", new String[] { "-x" });
        assertFalse(args.isValid());
        assertEquals("Could not find integer parameter for -x.",
                args.errorMessage());
    }

    @Test
    public void testWithTwoIntegers() throws ParseException {
        Args args = new Args("x#,y#", new String[] { "-xy", "16", "12" });
        assertTrue(args.isValid());
        assertEquals(2, args.cardinality());
        assertTrue(args.has('x'));
        assertTrue(args.has('y'));
        assertEquals((Integer) 16, args.getInt('x'));
        assertEquals((Integer) 12, args.getInt('y'));
    }

    @Test
    public void testWithTwoIntegersWithDifferentOrderThanSchema() throws ParseException {
        Args args = new Args("x#,y#", new String[] { "-yx", "12", "16" });
        assertTrue(args.isValid());
        assertEquals(2, args.cardinality());
        assertTrue(args.has('x'));
        assertTrue(args.has('y'));
        assertEquals((Integer) 16, args.getInt('x'));
        assertEquals((Integer) 12, args.getInt('y'));
    }

    @Test
    public void testWithNoSchemaButWithMultipleArguments() throws Exception {
        Args args = new Args("", new String[] { "-xy", "True", "True" });
        assertFalse(args.isValid());
        assertEquals("Argument(s) -xy unexpected.", args.errorMessage());
    }

    @Test
    public void testWithNoSchemaAndNoArguments() throws Exception {
        Args args = new Args("", new String[] { "" });
        assertTrue(args.isValid());
    }

    @Test
    public void testWithNoSchemaAndNoArguments2() throws Exception {
        Args args = new Args("", new String[] { "-xy" });
        assertFalse(args.isValid());
        assertEquals("Argument(s) -xy unexpected.", args.errorMessage());
    }

    @Test
    public void testWithExtraArgument() throws ParseException  {
        Args args = new Args("x#", new String[] { "-x", "12", "16" });
        assertTrue(args.isValid());
        assertEquals(1, args.cardinality());
        assertTrue(args.has('x'));
        assertEquals((Integer) 12, args.getInt('x'));
    }
}