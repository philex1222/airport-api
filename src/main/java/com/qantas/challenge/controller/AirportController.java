package com.qantas.challenge.controller;

import com.qantas.challenge.dto.AirportDto;
import com.qantas.challenge.service.AirportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the public, read-only REST endpoints for querying airport information.
 * This controller handles incoming HTTP requests and delegates the business logic to the AirportService.
 */
@RestController
@RequestMapping("/api/v1/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    /**
     * Handles GET requests to /api/v1/airports to fetch a list of airports.
     * It accepts several optional query parameters for filtering the results.
     * @param countryCode Optional filter for country code (e.g., "AU").
     * @param regionCode Optional filter for region code.
     * @param stateCode Optional filter for state code (e.g., "NSW").
     * @param cityCode Optional filter for city code.
     * @param airportName Optional filter for partial, case-insensitive airport name (e.g., "Sydney").
     * @return A ResponseEntity containing a list of matching airports and a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<AirportDto>> getAirports(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) String airportName
    ) {
        List<AirportDto> airports = airportService.getAirports(countryCode, regionCode, stateCode, cityCode, airportName);
        return ResponseEntity.ok(airports);
    }
}