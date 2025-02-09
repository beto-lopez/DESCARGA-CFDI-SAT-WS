/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;
/**
 * Abstract base for responses received from the SAT web service.<p>
 * 
 * SAT responds with <code>SOAPMessage</code>s, we use <code>SatResponse</code>s
 * to provide specific data parsed from those messages to be easily used.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.14
 * @since 1.0
 * 
 */
public abstract class SatResponse implements java.io.Serializable {
    
    private static final long serialVersionUID = 20241014L;
    
    /**
     * Status value that means request was accepted.
     */
    public static final String STATUS_CODE_ACCEPT = "5000";
    
    /**
     * <code>Instant</code> the <code>SOAPMessage</code> was received from SAT.
     */
    protected Instant satInstant;
    
    /**
     * Value of the status coded received
     */
    protected String statusCode;
    
    /**
     * String message received
     */
    protected String message;
    
    /**
     * Builds a <code>SatResponse</code> from the received parameters.
     * 
     * @param satInstant <code>Instant</code> mesage was received from SAT
     * @param statusCode status code of the received message
     * @param message message received
     * @throws IllegalArgumentException if satInstant is null
     */
    public SatResponse(Instant satInstant, String statusCode, String message) {
        //according to wsdl attributes are not required so we have to accept null
        if (satInstant == null) {
            throw new IllegalArgumentException("sat instant required");
        }
        this.statusCode = statusCode;
        this.message    = message;
        this.satInstant = satInstant;
    }
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns true if this response was accepted.<p>
     * Subclasses could override this method to consider other parameters and/or
     * situations particular to their response type.
     * 
     * @return true if this response is accepted by SAT
     */
    public boolean isAccept() {
        return STATUS_CODE_ACCEPT.equals(statusCode);
    }
    
    /**
     * Returns the status of the request that generated this response.
     * 
     * @return the status code of the request that generated this response.
     *         Could be null.
     */
    public String getStatusCode() {
        return statusCode;
    }
    
    /**
     * Returns the message that came in the <code>SOAPMessage</code> received.
     * @return the message received in this response. Could be null.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns the <code>Instant</code> when this response was received.
     * 
     * @return the <code>Instant</code> when this response was received.
     */
    public Instant getInstant() {
        return satInstant;
    }
  
}
