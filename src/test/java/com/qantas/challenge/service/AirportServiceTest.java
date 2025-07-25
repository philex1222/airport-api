package com.qantas.challenge.service;

import com.qantas.challenge.client.QantasApiClient;
import com.qantas.challenge.dto.AirportDto;
import com.qantas.challenge.dto.source.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the AirportService.
 * This class tests the business logic of the service in isolation, particularly the
 * data transformation logic, without involving the Spring context or real network calls.
 */
@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private QantasApiClient qantasApiClient;

    private AirportService airportService;

    @BeforeEach
    void setUp() {
        // Create a new service instance for each test to ensure isolation.
        airportService = new AirportService(qantasApiClient);
    }

    /**
     * Tests that the transformToDto method correctly maps all fields from a valid,
     * complete source object to the simplified DTO.
     */
    @Test
    void transformToDto_whenSourceIsValid_mapsAllFieldsCorrectly() {
        // Given: Create a complete, well-formed source object.
        QantasApiAirport source = new QantasApiAirport();
        source.setAirportCode("SYD");
        source.setAirportName("Sydney Airport");

        Location location = new Location();
        location.setLatitude(-33.946);
        location.setLongitude(151.177);
        source.setLocation(location);

        City city = new City();
        city.setCityCode("SYD");
        city.setCityName("Sydney");
        city.setTimeZoneName("Australia/Sydney");
        source.setCity(city);

        State state = new State();
        state.setStateCode("NSW");
        state.setStateName("New South Wales");
        source.setState(state);

        Country country = new Country();
        country.setCountryCode("AU");
        country.setCountryName("Australia");
        source.setCountry(country);

        Region region = new Region();
        region.setRegionCode("AU");
        region.setRegionName("Australia");
        source.setRegion(region);

        // When: Call the transformation method.
        AirportDto result = airportService.transformToDto(source);

        // Then: Assert that every field in the result is correct.
        assertThat(result).isNotNull();
        assertThat(result.getAirportCode()).isEqualTo("SYD");
        assertThat(result.getAirportName()).isEqualTo("Sydney Airport");
        assertThat(result.getLatitude()).isEqualTo(-33.946);
        assertThat(result.getLongitude()).isEqualTo(151.177);
        assertThat(result.getCityName()).isEqualTo("Sydney");
        assertThat(result.getStateCode()).isEqualTo("NSW");
        assertThat(result.getStateName()).isEqualTo("New South Wales");
        assertThat(result.getCountryCode()).isEqualTo("AU");
        assertThat(result.getRegionName()).isEqualTo("Australia");
    }

    /**
     * Tests the defensive nature of the transformation logic.
     * When the source data contains an empty 'state' object instead of a populated one,
     * the resulting DTO should have null values for state fields, not throw an error.
     */
    @Test
    void transformToDto_whenStateIsEmptyObject_mapsStateFieldsToNull() {
        // Given: Create a source object with an empty but non-null state object.
        QantasApiAirport source = createBaseAirport("AAA", "Anaa");

        // When: Call the transformation method.
        AirportDto result = airportService.transformToDto(source);

        // Then: Assert that the state fields are null.
        assertThat(result).isNotNull();
        assertThat(result.getStateCode()).isNull();
        assertThat(result.getStateName()).isNull();
    }

    /**
     * Tests that if the 'state' field is completely missing from the JSON (resulting in a null
     * State object), the transformation still proceeds correctly with null state fields.
     */
    @Test
    void transformToDto_whenStateObjectIsNull_mapsStateFieldsToNull() {
        // Given: Create a source object and explicitly set its state object to null.
        QantasApiAirport source = createBaseAirport("BBB", "Baltra");
        source.setState(null);

        // When: Call the transformation method.
        AirportDto result = airportService.transformToDto(source);

        // Then: Assert that the state fields are correctly set to null.
        assertThat(result).isNotNull();
        assertThat(result.getStateCode()).isNull();
        assertThat(result.getStateName()).isNull();
    }

    /**
     * Tests that the transformation logic correctly handles cases where a required
     * nested object (like 'city') is completely null in the source data.
     * The method should return null to indicate the record could not be processed.
     */
    @Test
    void transformToDto_whenRequiredObjectIsNull_returnsNull() {
        // Given: Create an incomplete source object missing a required nested object.
        QantasApiAirport source = new QantasApiAirport();
        source.setAirportCode("INCOMPLETE");
        source.setCity(null);

        // When: Call the transformation method.
        AirportDto result = airportService.transformToDto(source);

        // Then: Assert that the entire result is null.
        assertThat(result).isNull();
    }

    /**
     * Tests the ultimate edge case where the entire source object passed to the method is null.
     * The method should handle this gracefully by returning null.
     */
    @Test
    void transformToDto_whenSourceIsNull_returnsNull() {
        // When: Call the transformation method with a null input.
        AirportDto result = airportService.transformToDto(null);

        // Then: Assert that the result is null.
        assertThat(result).isNull();
    }

    /**
     * Helper method to create a base, valid airport object for testing.
     */
    private QantasApiAirport createBaseAirport(String code, String name) {
        QantasApiAirport source = new QantasApiAirport();
        source.setAirportCode(code);
        source.setAirportName(name);
        source.setLocation(new Location());
        source.setCountry(new Country());
        source.setRegion(new Region());
        source.setCity(new City());
        source.setState(new State());
        return source;
    }
}