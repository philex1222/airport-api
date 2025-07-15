package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the nested 'location' object within the external Qantas API data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    private Double latitude;
    private Double longitude;
}