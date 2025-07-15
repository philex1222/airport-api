package com.qantas.challenge.client;

import com.qantas.challenge.dto.source.QantasApiAirport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A client component responsible for all communication with the external Qantas API.
 * This class encapsulates the logic for fetching data from the remote source.
 */
@Slf4j
@Component
public class QantasApiClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public QantasApiClient(RestTemplate restTemplate, @Value("${qantas.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    /**
     * Fetches the complete list of airports from the Qantas API endpoint.
     * It handles potential network errors gracefully and deserializes the JSON response
     * into a list of QantasApiAirport objects.
     * @return A list of raw airport data objects, or an empty list if an error occurs.
     */
    public List<QantasApiAirport> fetchAirports() {
        try {
            log.info("Fetching airport data from external API: {}", apiUrl);
            QantasApiAirport[] airports = restTemplate.getForObject(apiUrl, QantasApiAirport[].class);
            if (airports == null) {
                log.warn("Received null response from external API.");
                return Collections.emptyList();
            }
            log.info("Successfully fetched {} airports.", airports.length);
            return Arrays.asList(airports);
        } catch (RestClientException e) {
            log.error("Error fetching data from Qantas API", e);
            return Collections.emptyList();
        }
    }
}