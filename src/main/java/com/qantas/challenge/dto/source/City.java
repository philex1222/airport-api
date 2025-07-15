package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the nested 'city' object within the external Qantas API data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {
    private String cityCode;
    private String cityName;
    private String timeZoneName;
    private State state;
}