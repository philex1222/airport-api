package com.qantas.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the simplified, flat data structure for an airport that is exposed by our API.
 * This serves as the public data contract for our service, decoupling our clients from the
 * complex structure of the external data source.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportDto {
    private String airportCode;
    private Double latitude;
    private Double longitude;
    private String airportName;
    private String cityCode;
    private String cityName;
    private String timeZoneName;
    private String stateCode;
    private String stateName;
    private String countryCode;
    private String countryName;
    private String regionCode;
    private String regionName;
}