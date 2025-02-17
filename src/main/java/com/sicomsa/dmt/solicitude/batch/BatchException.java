/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

/**
 * Base class for Batch process exceptions, which is usually an Exception wrapper.
 *  
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04
 * @since 1.0
 * 
 * 
 */
public class BatchException extends Exception {
    
    private static final long serialVersionUID = 20241113L;
    
    /**
     * Constructs a new <code>BatchException</code> with the specified detail message.
     * 
     * @param message the detail message
     */
    public BatchException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new <code>BatchException</code> with the specified detail
     * message and cause
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
