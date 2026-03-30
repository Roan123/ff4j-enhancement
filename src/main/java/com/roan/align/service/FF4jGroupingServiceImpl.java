package com.roan.align.service;

import com.roan.align.exception.WealthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.property.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Roan
 * @date 2026/3/18 14:11
 */
@Slf4j
@Service
public class FF4jGroupingServiceImpl implements FF4jGroupingService {

    private static final String COUNTRY_PROPERTY = "Country";
    private static final String CHANNEL_PROPERTY = "Channel";

    // @Autowired
    // private FF4jClient ff4jClient;
    @Autowired
    private FF4j ff4j;

    @Override
    public List<Feature> customFeatureToggleModifier(final String country, final String channelId) {
        try {

            List<Feature> featureList = ff4j.getFeatures().values().stream().toList();
            log.info("All ff4j feature list: {}", featureList);

            for (Feature feature : featureList) {
                if (feature.isEnable()){
                    feature.setEnable(propertyCheck(feature, COUNTRY_PROPERTY, country) && propertyCheck(feature, CHANNEL_PROPERTY, channelId));
                }

                feature.setDescription(null);
                feature.setGroup(null);
                feature.setFlippingStrategy(null);
                feature.setPermissions(null);
                feature.setCustomProperties(null);
            }

            log.info("return feature list: {}", featureList);
            return featureList;
        } catch (Exception e){
            log.error("Error occurred while checking user group: {}", e.getMessage(), e);
            throw new WealthException(500, HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    @Override
    public Feature singleFeatureToggleModifier(final String country, final String channelId, final String uid) {
        try {
            Feature feature = ff4j.getFeature(uid);
            log.info("ff4JClient received feature: {}", feature);

            if (feature.isEnable()){
                feature.setEnable(propertyCheck(feature, COUNTRY_PROPERTY, country) && propertyCheck(feature, CHANNEL_PROPERTY, channelId));
            }

            feature.setDescription(null);
            feature.setGroup(null);
            feature.setFlippingStrategy(null);
            feature.setPermissions(null);
            feature.setCustomProperties(null);
            return feature;
        } catch (Exception e){
            log.error("Error occurred while checking user group: {}", e.getMessage(), e);
            throw new WealthException(500, HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    private boolean propertyCheck(Feature feature, String propertyKey, String userProperty) {
        if (feature.getCustomProperties() == null || userProperty == null) {
            return true;
        }
        Property<?> property = feature.getCustomProperties().get(propertyKey);
        if (property == null) {
            return true;
        }
        String featureProperty = (String) property.getValue();
        if (Strings.CI.equals(featureProperty, userProperty)){
            return true;
        }
        log.info("feature enabled but user {}: {} does not match with feature {}: {}",
                propertyKey, userProperty, propertyKey, featureProperty);
        return false;
    }
}
