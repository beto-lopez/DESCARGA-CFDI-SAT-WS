/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import jakarta.xml.ws.WebServiceException;
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.04
 * 
 * Exception during a DownloadEvent.
 * This exception is thrown when an error is produced during a download event.
 * 
 */
public class DownloadEventException extends WebServiceException {
    
    private static final long serialVersionUID = 20250104L;
    
    public DownloadEventException(String message) {
        super(message);
    }
    
    public DownloadEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
