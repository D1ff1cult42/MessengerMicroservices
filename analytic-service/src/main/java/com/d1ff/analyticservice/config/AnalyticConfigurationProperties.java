package com.d1ff.analyticservice.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "events")
@Component
@Getter
@Setter
public class AnalyticConfigurationProperties {
    private Set<String> authEvents = new HashSet<>();
}
