/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.13
 * 
 * Base class for Repository related exceptions
 * 
 * Note: this is a RuntimeException
 * 
 */
public class RepositoryException extends WebServiceException {
    
    private static final long serialVersionUID = 20241113L;
    
    public RepositoryException(String message) {
        super(message);
    }
    
    public RepositoryException(Throwable cause) {
        super(cause);
    }
    
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
