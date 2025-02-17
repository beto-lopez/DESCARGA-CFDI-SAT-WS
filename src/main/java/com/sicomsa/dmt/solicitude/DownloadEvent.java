/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.SatResponse;

/**
 * Represents and event generated after receiving a response from the web service.
 * Note: source property is transient
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.12.25
 * @since 1.0
 * 
 */
public class DownloadEvent extends java.util.EventObject {
    private static final long serialVersionUID = 20241225L;
   
    /**
     * Result values of a download event from the WS
     */
    public enum Result {
        /**
         * Download request was rejected
         */
        REJECTED,
        
        /**
         * Download request was accepted
         */
        ACCEPTED,
        
        /**
         * Download request verification is not yet ready
         */
        DELAYED,
        
        /**
         * Download request has been verified
         */
        VERIFIED,
        
        /**
         * A package has been downloaded
         */
        DOWNLOADED;
    }
    
    /**
     * Result value of this event
     */
    protected Result result;
    
    /**
     * The response from the WS
     */
    protected SatResponse response;
    
    /**
     * Constructs a new <code>DownloadEvent</code> with the specified parameters.
     * 
     * @param source <code>Solicitude</code> that requested the download
     * @param result result of the download request
     * @param response the response from the WS
     * @throws IllegalArgumentException if source, result or response are null
     */
    public DownloadEvent(Solicitude source, Result result, SatResponse response) {
        super(source);
        if (result == null || response == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.result = result;
        this.response = response;
    }
    
    /**
     * Returns the <code>Solicitude</code> that generated this event.
     * 
     * @return the <code>Solicitude</code> that generated this event
     */
    public Solicitude getSolicitude() {
        return (Solicitude)source;
    }
    
    /**
     * Returns the <code>Result</code> value of this event.
     * 
     * @return the <code>Result</code> value of this event
     */
    public Result getResult() {
        return result;
    }
    
    /**
     * Returns WS's response of this event.
     * 
     * @return WS's response of this event
     */
    public SatResponse getResponse() {
        return response;
    }

}
