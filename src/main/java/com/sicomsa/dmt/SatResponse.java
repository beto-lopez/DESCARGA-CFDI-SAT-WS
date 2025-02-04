/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * 
 * @since 2024.9.9
 * @version 2024.10.14
 * 
 * Abstract base class for SAT responses from the web service.
 * 
 */
public abstract class SatResponse implements java.io.Serializable {
    
    private static final long serialVersionUID = 20241014L;
    
    public static final String STATUS_CODE_ACCEPT = "5000";
      
    protected Instant satInstant;
    protected String statusCode;
    protected String message;
    
    /**
     * according to wsdl attributes are not required so we have to accept null
     * @param satInstant
     * @param statusCode
     * @param message 
     * @throws IllegalArgumentException if satInstant is null
     */
    public SatResponse(Instant satInstant, String statusCode, String message) {
        if (satInstant == null) {
            throw new IllegalArgumentException("sat instant required");
        }
        this.statusCode = statusCode;
        this.message    = message;
        this.satInstant = satInstant;
    }
    ////////////////////////////////////////////////////////////////////////////
           
    public boolean isAccept() {
        return STATUS_CODE_ACCEPT.equals(statusCode);
    }
    
    public String getStatusCode() {
        return statusCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Instant getInstant() {
        return satInstant;
    }
  
}
