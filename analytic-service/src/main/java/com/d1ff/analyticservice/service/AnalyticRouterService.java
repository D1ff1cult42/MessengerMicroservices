package com.d1ff.analyticservice.service;

import com.d1ff.analyticservice.config.AnalyticConfigurationProperties;
import com.d1ff.analyticservice.model.AuthAnalyticEvent;
import com.d1ff.analyticservice.repository.AuthAnalyticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticRouterService {

    private final AuthAnalyticRepository authAnalyticRepository;
    private final GeoIpService geoIpService;
    private final ObjectMapper objectMapper;
    private final AnalyticConfigurationProperties analyticConfigurationProperties;


    public void route(byte[] jsonPayload) {
        try {
            String eventType = objectMapper.readTree(jsonPayload).get("eventType").asText();

            if (analyticConfigurationProperties.getAuthEvents().contains(eventType)) {
                AuthAnalyticEvent event = objectMapper.readValue(jsonPayload, AuthAnalyticEvent.class);
                String country = geoIpService.resolveCountry(event.ipAddress());
                authAnalyticRepository.add(event.withCountry(country));
            } else {
                log.warn("Unknown analytic eventType: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to route analytic payload", e);
            throw new RuntimeException(e);
        }
    }
}