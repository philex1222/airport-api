package com.qantas.challenge.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the nested 'state' object within the external Qantas API data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
    private String stateCode;
    private String stateName;
}