package com.qantas.challenge.service;

import com.qantas.challenge.client.QantasApiClient;
import com.qantas.challenge.dto.AirportDto;
import com.qantas.challenge.dto.source.QantasApiAirport;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Core service class containing the business logic for managing airport data.
 * This includes caching data on startup, transforming it into a simplified format,
 * and providing filtering capabilities.
 */
@Slf4j
@Service
public class AirportService {

    private final QantasApiClient qantasApiClient;
    private final List<AirportDto> airportCache = new CopyOnWriteArrayList<>();

    public AirportService(QantasApiClient qantasApiClient) {
        this.qantasApiClient = qantasApiClient;
    }

    /**
     * Initializes the in-memory airport cache when the application starts.
     * This method is automatically called by Spring after the service has been constructed.
     * It fetches data from the external API, transforms it, and stores it for fast access.
     */
    @PostConstruct
    public void loadAirportsIntoCache() {
        log.info("Initializing airport cache on application startup...");
        List<QantasApiAirport> rawAirports = qantasApiClient.fetchAirports();
        List<AirportDto> transformedAirports = rawAirports.stream()
                .map(this::transformToDto)
                .filter(Objects::nonNull)
                .toList();

        airportCache.addAll(transformedAirports);
        log.info("Airport cache initialized with {} entries.", airportCache.size());
    }

    /**
     * Filters the cached list of airports based on the provided optional query parameters.
     * Supports filtering by country, region, state, city, and partial airport name.
     * @return A list of matching AirportDto objects.
     */
    public List<AirportDto> getAirports(String countryCode, String regionCode, String stateCode, String cityCode, String airportName) {
        Stream<AirportDto> stream = airportCache.stream();

        if (countryCode != null && !countryCode.isBlank()) {
            stream = stream.filter(a -> countryCode.equalsIgnoreCase(a.getCountryCode()));
        }
        if (regionCode != null && !regionCode.isBlank()) {
            stream = stream.filter(a -> regionCode.equalsIgnoreCase(a.getRegionCode()));
        }
        if (stateCode != null && !stateCode.isBlank()) {
            stream = stream.filter(a -> stateCode.equalsIgnoreCase(a.getStateCode()));
        }
        if (cityCode != null && !cityCode.isBlank()) {
            stream = stream.filter(a -> cityCode.equalsIgnoreCase(a.getCityCode()));
        }
        if (airportName != null && !airportName.isBlank()) {
            stream = stream.filter(a -> a.getAirportName().toLowerCase().contains(airportName.toLowerCase()));
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Transforms a raw QantasApiAirport object from the external source into our simplified AirportDto.
     * This method includes defensive checks to handle null or incomplete data gracefully,
     * preventing NullPointerExceptions and ensuring data integrity.
     * @param source The raw airport object from the external API.
     * @return A simplified AirportDto, or null if the source data is incomplete.
     */
    AirportDto transformToDto(QantasApiAirport source) {
        if (source == null || source.getCity() == null || source.getLocation() == null || source.getCountry() == null || source.getRegion() == null) {
            log.warn("Skipping transformation for incomplete airport data: {}", source != null ? source.getAirportCode() : "null");
            return null;
        }

        return AirportDto.builder()
                .airportCode(source.getAirportCode())
                .airportName(source.getAirportName())
                .latitude(source.getLocation().getLatitude())
                .longitude(source.getLocation().getLongitude())
                .cityCode(source.getCity().getCityCode())
                .cityName(source.getCity().getCityName())
                .timeZoneName(source.getCity().getTimeZoneName())
                .stateCode(source.getState() != null ? source.getState().getStateCode() : null)
                .stateName(source.getState() != null ? source.getState().getStateName() : null)
                .countryCode(source.getCountry().getCountryCode())
                .countryName(source.getCountry().getCountryName())
                .regionCode(source.getRegion().getRegionCode())
                .regionName(source.getRegion().getRegionName())
                .build();
    }
}