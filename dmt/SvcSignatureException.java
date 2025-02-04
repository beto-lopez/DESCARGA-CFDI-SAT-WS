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
 * Exception indicating problems or failures while signing a SOAP request.
 * 
 */
public class SvcSignatureException extends WebServiceException {
    
    private static final long serialVersionUID = 20241113L;
    
    public SvcSignatureException(String message) {
        super(message);
    }
    
    public SvcSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
