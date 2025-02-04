/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import jakarta.xml.ws.WebServiceException;
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.13
 * 
 * 
 * Exception representing a problem or error while parsing a soap message
 * 
 * Note: this is a RuntimeException
 * 
 * 
 */
public class SvcParseException extends WebServiceException {
    
    private static final long serialVersionUID = 20241113L;
    
    public SvcParseException(String message) {
        super(message);
    }
    
    public SvcParseException(Throwable cause) {
        super(cause);
    }
    
    public SvcParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
