/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.SatResponse;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.12.25
 * 
 * Represents and event generated after receiving a response from the web service.
 *  * 
 * Note: source property is transient
 */
public class DownloadEvent extends java.util.EventObject {
    private static final long serialVersionUID = 20241225L;
    
    public enum Result {
        REJECTED, ACCEPTED, DELAYED, VERIFIED, DOWNLOADED;
    }
    
    protected Result result;
    protected SatResponse response;
    
    /**
     * @param source
     * @param result
     * @param response 
     */
    public DownloadEvent(Solicitude source, Result result, SatResponse response) {
        super(source);
        if (result == null || response == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.result = result;
        this.response = response;
    }
    
    public Solicitude getSolicitude() {
        return (Solicitude)source;
    }
    
    public Result getResult() {
        return result;
    }
    
    public SatResponse getResponse() {
        return response;
    }

}
