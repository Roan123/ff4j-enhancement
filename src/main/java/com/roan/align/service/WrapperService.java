package com.roan.align.service;

import org.ff4j.core.Feature;

import java.util.List;

/**
 * Wrapper service for feature toggling with property-based filtering.
 *
 * @author Roan
 * @date 2026/4/7
 */
public interface WrapperService {

    /**
     * Get all features with property-based filtering.
     *
     * @param country user country from request header
     * @param channel user channel from request header
     * @return list of features after property check
     */
    List<Feature> getWrapperFeatures(String country, String channel);
}