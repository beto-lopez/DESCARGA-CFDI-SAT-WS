/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import jakarta.xml.ws.WebServiceException;

/**
 * RuntimeException that denotes an error produced during a download event.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04
 * @since 1.0
 *  
 */
public class DownloadEventException extends WebServiceException {
    
    private static final long serialVersionUID = 20250104L;
    
    /**
     * Constructs a new <code>DownloadEventException</code> with the specified
     * detail message.
     * The cause is not initialized.
     * 
     * @param message The detail message which is later retrieved using the
     *                <code>getMessage</code> method
     */
    public DownloadEventException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new <code>DownloadEventException</code> with the specified
     * detail message and cause.
     * 
     * @param message The detail message which is later retrieved using the
     *                <code>getMessage</code> method
     * @param cause The cause which is saved for the later retrieval throw by
     *              the <code>getCause</code> method
     */
    public DownloadEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
