package com.qantas.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qantas.challenge.dto.AirportDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the AirportAdminController.
 * This class tests the placeholder CRUD endpoints to ensure they adhere to RESTful principles
 * regarding HTTP methods, status codes, and response structures.
 */
@WebMvcTest(AirportAdminController.class)
public class AirportAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests the POST /api/v1/admin/airports endpoint for creating an airport.
     * It should return a 201 Created status and a Location header.
     */
    @Test
    void createAirport_returnsCreated() throws Exception {
        AirportDto newAirport = AirportDto.builder().airportCode("TEST").airportName("Test Airport").build();

        mockMvc.perform(post("/api/v1/admin/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAirport)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.airportCode", is("TEST")));
    }

    /**
     * Tests the GET /api/v1/admin/airports/{airportCode} endpoint for a known code.
     * It should return a 200 OK status with the mock airport data.
     */
    @Test
    void getAirportByCode_whenFound_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/admin/airports/SYD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.airportCode", is("SYD")));
    }

    /**
     * Tests the GET /api/v1/admin/airports/{airportCode} endpoint for an unknown code.
     * It should return a 404 Not Found status.
     */
    @Test
    void getAirportByCode_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/airports/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the PUT /api/v1/admin/airports/{airportCode} endpoint for updating an airport.
     * It should return a 200 OK status with the updated data.
     */
    @Test
    void updateAirport_returnsOk() throws Exception {
        AirportDto updatedAirport = AirportDto.builder().airportCode("TEST").airportName("Updated Airport").build();

        mockMvc.perform(put("/api/v1/admin/airports/TEST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAirport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.airportName", is("Updated Airport")));
    }

    /**
     * Tests the DELETE /api/v1/admin/airports/{airportCode} endpoint for deleting an airport.
     * It should return a 204 No Content status.
     */
    @Test
    void deleteAirport_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/airports/TEST"))
                .andExpect(status().isNoContent());
    }
}