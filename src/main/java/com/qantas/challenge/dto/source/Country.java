package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the nested 'country' object within the external Qantas API data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    private String countryCode;
    private String countryName;
}