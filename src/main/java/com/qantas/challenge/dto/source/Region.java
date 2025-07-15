package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the nested 'region' object within the external Qantas API data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Region {
    private String regionCode;
    private String regionName;
}