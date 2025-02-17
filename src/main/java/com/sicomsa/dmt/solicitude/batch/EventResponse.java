/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.SatResponse;

import java.time.Instant;

/**
 * <code>EventResponse</code> is a web service response derived from a download
 * event with additional properties that facilitate its manual serialization.
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.12.27
 * @since 1.0
 * 
 */
public class EventResponse {
    /**
     * Process identifier, usually the identifier of the <code>BatchRequest</code>.
     */
    protected long processId;
    
    /**
     * The type of this event response.
     */
    protected String type;
    
    /**
     * The concrete <code>SatResponse</code> received from the web service.
     */
    protected SatResponse response;
    
    /**
     * Status of this event response.
     */
    protected String status;
    
    /**
     * Constructs a new <code>EventResponse</code> whose response was received
     * from the request download web service.
     * 
     * @param processId process identifier, usually the <code>BatchRequest</code> id.
     * @param response response received from the request download web service
     * @param status status of this event response
     * @throws IllegalArgumentException if response is null
     */
    public EventResponse(long processId, SolicitaResponse response, String status) {
        this(processId, "SolicitaResponse", response, status);
    }
    
    /**
     * Constructs a new <code>EventResponse</code> whose response was received
     * from the verify request web service.
     * 
     * @param processId process identifier, usually the <code>BatchRequest</code> id.
     * @param response response received from the verify request web service
     * @param status status of this event response
     * @throws IllegalArgumentException if response is null
     */
    public EventResponse(long processId, VerificaResponse response, String status) {
        this(processId, "VerificaResponse", response, status);
    }
    
    /**
     * Constructs a new <code>EventResponse</code> whose response was received
     * from the download package web service.
     * 
     * @param processId process identifier, usually the <code>BatchRequest</code> id.
     * @param response response received from the download package web service
     * @param status status of this event response
     * @throws IllegalArgumentException if response is null
     */
    public EventResponse(long processId, DescargaResponse response, String status) {
        this(processId, "DescargaResponse", response, status);
    }
    
    /**
     * Constructs a new <code>EventResponse</code> with the specified parameters.
     * 
     * @param processId process identifier, usually the <code>BatchRequest</code> id.
     * @param type type of the event response to create
     * @param response response received from the web service
     * @param status status of this event response
     * @throws IllegalArgumentException if type or response are null
     */
    protected EventResponse(long processId, String type, SatResponse response, String status) {
        if (type == null || response == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.processId = processId;
        this.type = type;
        this.response = response;
        this.status = status;
    }
        
    /**
     * Returns the process identifier of this event response.
     * @return the process identifier of this event response
     */
    public long getProcessId() {
        return processId;
    }
    
    /**
     * Returns the instant when the response was received from the web service.
     * @return the instant when the response was received from the web service
     */
    public Instant getInstant() {
        return response.getInstant();
    }
    
    /**
     * Returns the type of this event response.
     * 
     * @return the type of this event response
     */
    public String getType() {
        return type;
    }
    
    /**
     * Returns the response received from the web service.
     * 
     * @return the response received from the web service
     */
    public SatResponse getResponse() {
        return response;
    }
    
    /**
     * Returns the status of this event response.
     * 
     * @return the status of this event resposne
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Returns a string representation of this event response.
     * 
     * @return a string representation of this event response
     */
    @Override public String toString() {
        return new StringBuilder("EventResponse{")
                .append("processId=").append(processId)
                .append(",type=").append(type)
                .append(",status=").append(status)
                .append(",response=").append(response)
                .append("}").toString();
    }
   
}
