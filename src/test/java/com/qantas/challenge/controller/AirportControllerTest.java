package com.qantas.challenge.controller;

import com.qantas.challenge.dto.AirportDto;
import com.qantas.challenge.service.AirportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
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

    /**
     * Tests that combining multiple filters works as expected (AND logic).
     */
    @Test
    void getAirports_whenMultipleFilters_returnsCorrectlyFilteredList() throws Exception {
        // Given
        List<AirportDto> lax = List.of(
                AirportDto.builder().airportCode("LAX").countryCode("US").stateCode("CA").build()
        );
        given(airportService.getAirports("US", null, "CA", null, null)).willReturn(lax);

        // When & Then
        mockMvc.perform(get("/api/v1/airports?countryCode=US&stateCode=CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].airportCode", is("LAX")));
    }

    /**
     * Tests the negative path where a valid filter yields no results.
     * The API should return a 200 OK status with an empty array, not a 404.
     */
    @Test
    void getAirports_whenFilterYieldsNoResults_returnsEmptyArray() throws Exception {
        // Given
        given(airportService.getAirports("XYZ", null, null, null, null)).willReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/v1/airports?countryCode=XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests that providing a blank value for a parameter is ignored and treated as if
     * the parameter was not provided at all.
     */
    @Test
    void getAirports_withBlankParameter_isIgnored() throws Exception {
        // Given: We expect the service to be called with null, as the blank param is ignored.
        given(airportService.getAirports(null, null, null, null, null))
                .willReturn(List.of(new AirportDto()));

        // When & Then
        mockMvc.perform(get("/api/v1/airports?countryCode="))
                .andExpect(status().isOk());
    }

    /**
     * Tests that providing an unknown query parameter that is not defined in the controller
     * is safely ignored by Spring MVC.
     */
    @Test
    void getAirports_withUnknownParameter_isIgnored() throws Exception {
        // Given: We expect the service to be called with all nulls, as 'foo' is not a valid param.
        given(airportService.getAirports(null, null, null, null, null))
                .willReturn(List.of(new AirportDto()));

        // When & Then
        mockMvc.perform(get("/api/v1/airports?foo=bar"))
                .andExpect(status().isOk());
    }
}