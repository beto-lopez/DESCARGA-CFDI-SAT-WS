/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.04
 * 
 * Base class for Batch process exceptions.
 * It is usually used as an exception wrapper.
 * 
 */
public class BatchException extends Exception {
    
    private static final long serialVersionUID = 20241113L;
    
    public BatchException(String message) {
        super(message);
    }
    
    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
