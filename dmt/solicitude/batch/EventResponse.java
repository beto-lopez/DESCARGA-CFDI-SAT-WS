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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.12.27
 * 
 * Represents a SAT response that occurred on a download event. We use it to be
 * able to reconstruct an event response from a file or stream.
 * 
 */
public class EventResponse {
    protected long processId;
    protected String type;
    protected SatResponse response;
    protected String status;
    
    public EventResponse(long processId, SolicitaResponse response, String status) {
        this(processId, "SolicitaResponse", response, status);
    }
    
    public EventResponse(long processId, VerificaResponse response, String status) {
        this(processId, "VerificaResponse", response, status);
    }
    
    public EventResponse(long processId, DescargaResponse response, String status) {
        this(processId, "DescargaResponse", response, status);
    }
    
    protected EventResponse(long processId, String type, SatResponse response, String status) {
        if (type == null || response == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.processId = processId;
        this.type = type;
        this.response = response;
        this.status = status;
    }
        
    public long getProcessId() {
        return processId;
    }
    
    public Instant getInstant() {
        return response.getInstant();
    }
    
    public String getType() {
        return type;
    }
    
    public SatResponse getResponse() {
        return response;
    }
    
    public String getStatus() {
        return status;
    }
    
    @Override public String toString() {
        return new StringBuilder("EventResponse{")
                .append("processId=").append(processId)
                .append(",type=").append(type)
                .append(",status=").append(status)
                .append(",response=").append(response)
                .append("}").toString();
    }
   
}
