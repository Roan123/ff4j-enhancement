package com.roan.align.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.property.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of WrapperService that checks feature availability based on user properties.
 *
 * @author Roan
 * @date 2026/4/7
 */
@Slf4j
@Service
public class WrapperServiceImpl implements WrapperService {

    private static final String COUNTRY_KEY = "country";
    private static final String CHANNEL_KEY = "channel";

    @Autowired
    private FF4j ff4j;

    @Override
    public List<Feature> getWrapperFeatures(final String country, final String channel) {
        log.info("Getting wrapper features with country: {}, channel: {}", country, channel);

        Map<String, Feature> featuresMap = ff4j.getFeatures();
        log.info("Total features found: {}", featuresMap.size());

        List<Feature> result = featuresMap.values().stream()
                .map(feature -> processFeature(feature, country, channel))
                .collect(Collectors.toList());

        log.info("Processed features count: {}", result.size());
        return result;
    }

    /**
     * Process a single feature - check property if enabled.
     *
     * @param feature the feature to process
     * @param country user country from request header
     * @param channel user channel from request header
     * @return processed feature
     */
    private Feature processFeature(final Feature feature, final String country, final String channel) {
        boolean originalEnabled = feature.isEnable();

        if (originalEnabled) {
            // Check country property
            boolean countryMatch = propertyCheck(country, COUNTRY_KEY, feature.getCustomProperties());
            // Check channel property
            boolean channelMatch = propertyCheck(channel, CHANNEL_KEY, feature.getCustomProperties());

            // Feature is enabled only if both property checks pass
            // If property doesn't exist in customProperties, it's considered a match (return true)
            feature.setEnable(countryMatch && channelMatch);
        }

        // Clean up sensitive data from the feature
        cleanFeature(feature);

        return feature;
    }

    /**
     * Check if user property matches feature property.
     *
     * @param userProperty       user property from request header
     * @param propertyKey       property key (country or channel)
     * @param customProperties   feature's custom properties
     * @return true if property matches or doesn't exist, false if doesn't match
     */
    private boolean propertyCheck(final String userProperty, final String propertyKey,
                                   final Map<String, Property<?>> customProperties) {
        // If customProperties is null or userProperty is blank, return true (pass check)
        if (customProperties == null || StringUtils.isBlank(userProperty)) {
            return true;
        }

        // Get the property from customProperties
        Property<?> property = customProperties.get(propertyKey);
        if (property == null) {
            // Property doesn't exist in feature, consider as match
            return true;
        }

        // Get feature property value
        Object featurePropertyValue = property.getValue();
        if (featurePropertyValue == null) {
            return true;
        }

        String featureProperty = featurePropertyValue.toString();

        // Case-insensitive comparison
        boolean matches = StringUtils.equalsIgnoreCase(featureProperty, userProperty);

        log.debug("Property check - userProperty: {}, propertyKey: {}, featureProperty: {}, matches: {}",
                userProperty, propertyKey, featureProperty, matches);

        return matches;
    }

    /**
     * Clean sensitive data from feature before returning.
     *
     * @param feature the feature to clean
     */
    private void cleanFeature(final Feature feature) {
        feature.setDescription(null);
        feature.setGroup(null);
        feature.setFlippingStrategy(null);
        feature.setPermissions(null);
        feature.setCustomProperties(null);
    }
}