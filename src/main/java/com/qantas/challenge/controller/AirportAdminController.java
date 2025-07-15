package com.qantas.challenge.controller;

import com.qantas.challenge.dto.AirportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Exposes non-functional, placeholder REST endpoints for administrative CRUD operations on airports.
 * This class demonstrates RESTful API design principles (HTTP verbs, status codes, URL structure)
 * as requested in the optional part of the challenge. It is architecturally separate from the
 * main query controller for clarity and security purposes.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/airports")
public class AirportAdminController {

    /**
     * Placeholder endpoint to handle POST requests for creating a new airport.
     * In a real application, this would persist the new airport to a database.
     * @param airportDto The airport data from the request body.
     * @return A ResponseEntity with a 201 Created status and a Location header.
     */
    @PostMapping
    public ResponseEntity<AirportDto> createAirport(@RequestBody AirportDto airportDto) {
        log.info("Placeholder: Received request to CREATE airport: {}", airportDto);
        airportDto.setAirportCode(airportDto.getAirportCode().toUpperCase());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{airportCode}")
                .buildAndExpand(airportDto.getAirportCode())
                .toUri();
        return ResponseEntity.created(location).body(airportDto);
    }

    /**
     * Placeholder endpoint to handle GET requests for retrieving a single airport by its code.
     * @param airportCode The IATA code of the airport to retrieve.
     * @return A ResponseEntity with the airport data and 200 OK, or 404 Not Found.
     */
    @GetMapping("/{airportCode}")
    public ResponseEntity<AirportDto> getAirportByCode(@PathVariable String airportCode) {
        log.info("Placeholder: Received request to READ airport with code: {}", airportCode);
        if ("SYD".equalsIgnoreCase(airportCode)) {
            AirportDto mockAirport = AirportDto.builder().airportCode("SYD").airportName("Sydney").countryCode("AU").build();
            return ResponseEntity.ok(mockAirport);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Placeholder endpoint to handle PUT requests for updating an existing airport.
     * @param airportCode The code of the airport to update.
     * @param airportDto The updated airport data from the request body.
     * @return A ResponseEntity with the updated airport data and 200 OK.
     */
    @PutMapping("/{airportCode}")
    public ResponseEntity<AirportDto> updateAirport(@PathVariable String airportCode, @RequestBody AirportDto airportDto) {
        log.info("Placeholder: Received request to UPDATE airport with code {}: {}", airportCode, airportDto);
        return ResponseEntity.ok(airportDto);
    }

    /**
     * Placeholder endpoint to handle DELETE requests for removing an airport.
     * @param airportCode The code of the airport to delete.
     */
    @DeleteMapping("/{airportCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAirport(@PathVariable String airportCode) {
        log.info("Placeholder: Received request to DELETE airport with code: {}", airportCode);
    }
}