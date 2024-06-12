package cleancode.enums;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ArgumentFormatsTest {

    @Test
    public void testIntegerArgumentFormat() {
        assertTrue(ArgumentFormats.isValidValue("#"));
        assertEquals(ArgumentFormats.INTEGER.getValue(), "#");
    }

    @Test
    public void testStringArgumentFormat() {
        assertTrue(ArgumentFormats.isValidValue("*"));
        assertEquals(ArgumentFormats.STRING.getValue(), "*");
    }

    @Test
    public void testBooleanArgumentFormat() {
        assertTrue(ArgumentFormats.isValidValue(""));
        assertEquals(ArgumentFormats.BOOLEAN.getValue(), "");
    }

    @Test
    public void testInvalidArgumentFormat() {
        assertFalse(ArgumentFormats.isValidValue("%"));
    }
}
