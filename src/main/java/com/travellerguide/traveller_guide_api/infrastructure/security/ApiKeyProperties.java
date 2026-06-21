package com.travellerguide.traveller_guide_api.infrastructure.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "traveller-guide.security.api-key")
public record ApiKeyProperties(
        @NotBlank
        String value,
        String headerName,
        boolean enforceHttps
) {

    public String headerName() {
        return headerName == null || headerName.isBlank()
                ? "X-API-Key"
                : headerName;
    }
}
