package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the main, top-level airport object from the external Qantas API JSON structure.
 * Used for deserializing the raw data fetched from the external endpoint.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QantasApiAirport {
    private String airportCode;
    private String airportName;
    private Location location;
    private City city;
    private State state;
    private Country country;
    private Region region;
}