package in.taxgenie.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JWT Service token type handling
 * Tests the fix for Integer to String conversion issues
 * This test focuses on the convertToLong helper method to verify type conversion works
 */
class JwtServiceTokenTypeTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "serverSecret", "defaultSecretKeyForDevelopmentOnlyNotForProduction");
        ReflectionTestUtils.setField(jwtService, "maxVendorsPerOem", 1000);
    }

    @Test
    void testConvertToLongWithInteger() throws Exception {
        // Test the convertToLong method with Integer input
        Method convertToLongMethod = JwtService.class.getDeclaredMethod("convertToLong", Object.class);
        convertToLongMethod.setAccessible(true);

        // Test with Integer (this was the original problem)
        Integer intValue = 4469;
        Long result = (Long) convertToLongMethod.invoke(jwtService, intValue);
        assertEquals(4469L, result, "Should convert Integer to Long correctly");
    }

    @Test
    void testConvertToLongWithLong() throws Exception {
        // Test the convertToLong method with Long input
        Method convertToLongMethod = JwtService.class.getDeclaredMethod("convertToLong", Object.class);
        convertToLongMethod.setAccessible(true);

        Long longValue = 26047L;
        Long result = (Long) convertToLongMethod.invoke(jwtService, longValue);
        assertEquals(26047L, result, "Should handle Long input correctly");
    }

    @Test
    void testConvertToLongWithString() throws Exception {
        // Test the convertToLong method with String input
        Method convertToLongMethod = JwtService.class.getDeclaredMethod("convertToLong", Object.class);
        convertToLongMethod.setAccessible(true);

        String stringValue = "31";
        Long result = (Long) convertToLongMethod.invoke(jwtService, stringValue);
        assertEquals(31L, result, "Should parse String to Long correctly");
    }

    @Test
    void testConvertToLongWithNull() throws Exception {
        // Test the convertToLong method with null input
        Method convertToLongMethod = JwtService.class.getDeclaredMethod("convertToLong", Object.class);
        convertToLongMethod.setAccessible(true);

        Long result = (Long) convertToLongMethod.invoke(jwtService, (Object) null);
        assertNull(result, "Should return null for null input");
    }

    @Test
    void testConvertToLongWithInvalidType() throws Exception {
        // Test the convertToLong method with invalid type
        Method convertToLongMethod = JwtService.class.getDeclaredMethod("convertToLong", Object.class);
        convertToLongMethod.setAccessible(true);

        Double doubleValue = 123.45;

        Exception exception = assertThrows(Exception.class, () -> {
            convertToLongMethod.invoke(jwtService, doubleValue);
        });

        // The exception will be wrapped in InvocationTargetException
        assertTrue(exception.getCause() instanceof IllegalArgumentException,
                  "Should throw IllegalArgumentException for invalid type");
        assertTrue(exception.getCause().getMessage().contains("Cannot convert"),
                  "Error message should mention conversion failure");
    }
}
