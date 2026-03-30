package com.roan.align.service;

import org.ff4j.core.Feature;

import java.util.List;

/**
 * @author Roan
 * @date 2026/3/18 14:11
 */
public interface FF4jGroupingService {
    List<Feature> customFeatureToggleModifier(String country, String channelId);

    Feature singleFeatureToggleModifier(String country, String channelId, String uid);
}
