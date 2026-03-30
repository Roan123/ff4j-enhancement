package com.roan.align.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyBigDecimal;
import org.ff4j.property.PropertyBigInteger;
import org.ff4j.property.PropertyBoolean;
import org.ff4j.property.PropertyDouble;
import org.ff4j.property.PropertyFloat;
import org.ff4j.property.PropertyInt;
import org.ff4j.property.PropertyLong;
import org.ff4j.property.PropertyString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PropertyDeserializer
 *
 * @author Roan
 * @date 2026/3/18
 */
class PropertyDeserializerTest {

    private PropertyDeserializer deserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deserializer = new PropertyDeserializer();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("PropertyString Tests")
    class PropertyStringTests {

        @Test
        @DisplayName("Should deserialize PropertyString correctly")
        void shouldDeserializePropertyString() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyString\",\"name\":\"testString\",\"value\":\"hello world\"}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyString.class, property);
            assertEquals("testString", property.getName());
            assertEquals("hello world", property.getValue());
        }

        @Test
        @DisplayName("Should deserialize PropertyString with empty value")
        void shouldDeserializePropertyStringWithEmptyValue() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyString\",\"name\":\"emptyString\",\"value\":\"\"}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyString.class, property);
            assertEquals("emptyString", property.getName());
            assertEquals("", property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyInt Tests")
    class PropertyIntTests {

        @Test
        @DisplayName("Should deserialize PropertyInt correctly")
        void shouldDeserializePropertyInt() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyInt\",\"name\":\"testInt\",\"value\":42}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyInt.class, property);
            assertEquals("testInt", property.getName());
            assertEquals(42, property.getValue());
        }

        @Test
        @DisplayName("Should deserialize PropertyInt with negative value")
        void shouldDeserializePropertyIntWithNegativeValue() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyInt\",\"name\":\"negativeInt\",\"value\":-100}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyInt.class, property);
            assertEquals("negativeInt", property.getName());
            assertEquals(-100, property.getValue());
        }

        @Test
        @DisplayName("Should deserialize PropertyInt with zero value")
        void shouldDeserializePropertyIntWithZeroValue() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyInt\",\"name\":\"zeroInt\",\"value\":0}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyInt.class, property);
            assertEquals("zeroInt", property.getName());
            assertEquals(0, property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyLong Tests")
    class PropertyLongTests {

        @Test
        @DisplayName("Should deserialize PropertyLong correctly")
        void shouldDeserializePropertyLong() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyLong\",\"name\":\"testLong\",\"value\":9223372036854775807}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyLong.class, property);
            assertEquals("testLong", property.getName());
            assertEquals(9223372036854775807L, property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyDouble Tests")
    class PropertyDoubleTests {

        @Test
        @DisplayName("Should deserialize PropertyDouble correctly")
        void shouldDeserializePropertyDouble() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyDouble\",\"name\":\"testDouble\",\"value\":3.14159}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyDouble.class, property);
            assertEquals("testDouble", property.getName());
            assertEquals(3.14159, property.getValue());
        }

        @Test
        @DisplayName("Should deserialize PropertyDouble with negative value")
        void shouldDeserializePropertyDoubleWithNegativeValue() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyDouble\",\"name\":\"negativeDouble\",\"value\":-2.5}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyDouble.class, property);
            assertEquals("negativeDouble", property.getName());
            assertEquals(-2.5, property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyFloat Tests")
    class PropertyFloatTests {

        @Test
        @DisplayName("Should deserialize PropertyFloat correctly")
        void shouldDeserializePropertyFloat() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyFloat\",\"name\":\"testFloat\",\"value\":1.5}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyFloat.class, property);
            assertEquals("testFloat", property.getName());
            assertEquals(1.5f, property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyBoolean Tests")
    class PropertyBooleanTests {

        @Test
        @DisplayName("Should deserialize PropertyBoolean with true value")
        void shouldDeserializePropertyBooleanTrue() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyBoolean\",\"name\":\"testBoolean\",\"value\":true}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyBoolean.class, property);
            assertEquals("testBoolean", property.getName());
            assertEquals(true, property.getValue());
        }

        @Test
        @DisplayName("Should deserialize PropertyBoolean with false value")
        void shouldDeserializePropertyBooleanFalse() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyBoolean\",\"name\":\"testBoolean\",\"value\":false}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyBoolean.class, property);
            assertEquals("testBoolean", property.getName());
            assertEquals(false, property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyBigDecimal Tests")
    class PropertyBigDecimalTests {

        @Test
        @DisplayName("Should deserialize PropertyBigDecimal correctly")
        void shouldDeserializePropertyBigDecimal() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyBigDecimal\",\"name\":\"testBigDecimal\",\"value\":123456.789123}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyBigDecimal.class, property);
            assertEquals("testBigDecimal", property.getName());
            assertEquals(new BigDecimal("123456.789123"), property.getValue());
        }
    }

    @Nested
    @DisplayName("PropertyBigInteger Tests")
    class PropertyBigIntegerTests {

        @Test
        @DisplayName("Should deserialize PropertyBigInteger correctly")
        void shouldDeserializePropertyBigInteger() throws Exception {
            // Given
            String json = "{\"type\":\"org.ff4j.property.PropertyBigInteger\",\"name\":\"testBigInteger\",\"value\":12345678901234567890}";
            JsonParser parser = objectMapper.createParser(json);

            // When
            Property<?> property = deserializer.deserialize(parser, null);

            // Then
            assertNotNull(property);
            assertInstanceOf(PropertyBigInteger.class, property);
            assertEquals("testBigInteger", property.getName());
            assertEquals(new BigInteger("12345678901234567890"), property.getValue());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw RuntimeException for invalid type")
        void shouldThrowRuntimeExceptionForInvalidType() {
            // Given
            String json = "{\"type\":\"com.nonexistent.InvalidClass\",\"name\":\"test\",\"value\":\"test\"}";
            JsonParser parser = objectMapper.createParser(json);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> deserializer.deserialize(parser, null));
            assertTrue(exception.getMessage().contains("Failed to instantiate Property class"));
        }
    }
}
