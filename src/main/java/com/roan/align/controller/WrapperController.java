package com.roan.align.controller;

import com.roan.align.service.WrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ff4j.core.Feature;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Wrapper controller that checks feature availability based on user properties.
 *
 * @author Roan
 * @date 2026/4/7
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wrapper")
public class WrapperController {

    private final WrapperService wrapperService;

    /**
     * Check all features and return enabled features based on user properties.
     *
     * @param country  user country from request header
     * @param channel  user channel from request header
     * @return list of enabled features after property check
     */
    @GetMapping("/features")
    public ResponseEntity<List<Feature>> getWrapperFeatures(
            @RequestHeader(value = "country", required = false) String country,
            @RequestHeader(value = "channel", required = false) String channel) {
        log.info("Received country: {}, channel: {}", country, channel);
        List<Feature> features = wrapperService.getWrapperFeatures(country, channel);
        return ResponseEntity.ok(features);
    }
}