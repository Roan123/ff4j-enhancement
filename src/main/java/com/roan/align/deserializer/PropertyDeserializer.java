package com.roan.align.deserializer;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JsonNode;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyBigDecimal;
import org.ff4j.property.PropertyBigInteger;
import org.ff4j.property.PropertyBoolean;
import org.ff4j.property.PropertyDouble;
import org.ff4j.property.PropertyFloat;
import org.ff4j.property.PropertyInt;
import org.ff4j.property.PropertyLong;
import org.ff4j.property.PropertyString;

import java.lang.invoke.VarHandle;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Jackson 3 ValueDeserializer for ff4j Property class.
 * Deserializes JSON to appropriate Property subclass based on "type" field.
 *
 * @author Roan
 * @date 2026/3/18
 */
@Slf4j
public class PropertyDeserializer extends ValueDeserializer<Property<?>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Property<?> deserialize(JsonParser parser, DeserializationContext context) {
        TreeNode entry = parser.readValueAsTree();
        log.info("deserializing property value: {}", entry);

        String type = ((JsonNode) entry.get("type")).asText();
        Property<?> property;
        try {
            Class<?> clazz = Class.forName(type);
            property = (Property<?>) OBJECT_MAPPER.treeToValue(entry, clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to instantiate Property class for type: " + type);
        }
        return property;
    }
}
