package com.qantas.challenge.controller;

import com.qantas.challenge.dto.AirportDto;
import com.qantas.challenge.service.AirportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AirportController.
 * This class uses @WebMvcTest to test the controller layer in isolation, without starting a full
 * application context. The service layer is mocked to provide predictable behavior.
 */
@WebMvcTest(AirportController.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AirportService airportService;

    /**
     * Tests the GET /api/v1/airports endpoint with no filter parameters.
     * It should return all airports provided by the service.
     */
    @Test
    void getAirports_whenNoFilter_returnsAllAirports() throws Exception {
        // Given: Define the mock data the service will return.
        List<AirportDto> allAirports = List.of(
                AirportDto.builder().airportCode("SYD").airportName("Sydney").build(),
                AirportDto.builder().airportCode("MEL").airportName("Melbourne").build()
        );
        given(airportService.getAirports(null, null, null, null, null)).willReturn(allAirports);

        // When & Then: Perform the request and assert the response is correct.
        mockMvc.perform(get("/api/v1/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].airportCode", is("SYD")));
    }

    /**
     * Tests the GET /api/v1/airports endpoint with the countryCode filter.
     * It should return only the airports matching the specified country.
     */
    @Test
    void getAirports_whenFilteredByCountryCode_returnsMatchingAirports() throws Exception {
        // Given: Define the mock data for a specific country.
        List<AirportDto> australianAirports = List.of(
                AirportDto.builder().airportCode("SYD").airportName("Sydney").countryCode("AU").build()
        );
        given(airportService.getAirports("AU", null, null, null, null)).willReturn(australianAirports);

        // When & Then: Perform the request with a query parameter and assert the response.
        mockMvc.perform(get("/api/v1/airports?countryCode=AU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].airportCode", is("SYD")))
                .andExpect(jsonPath("$[0].countryCode", is("AU")));
    }

    /**
     * Tests the GET /api/v1/airports endpoint with the airportName partial match filter.
     * It should return airports whose names contain the specified string.
     */
    @Test
    void getAirports_whenFilteredByPartialAirportName_returnsMatchingAirports() throws Exception {
        // Given: Define mock data for a partial name search.
        List<AirportDto> sydneyAirports = List.of(
                AirportDto.builder().airportCode("SYD").airportName("Sydney Kingsford").build()
        );
        given(airportService.getAirports(null, null, null, null, "Sydney")).willReturn(sydneyAirports);

        // When & Then: Perform the request and assert the response.
        mockMvc.perform(get("/api/v1/airports?airportName=Sydney"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].airportName", is("Sydney Kingsford")));
    }
}