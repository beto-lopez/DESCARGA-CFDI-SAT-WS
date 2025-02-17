/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import jakarta.xml.ws.WebServiceException;
/**
 * Exception indicating problems or failures while signing a SOAP request.
 * Note: this is a RuntimeException.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.13
 * @since 1.0
 * 
 */
public class SvcSignatureException extends WebServiceException {
    
    private static final long serialVersionUID = 20241113L;
    
   /**
    * Constructs a new SvcSignatureException with the specified detail message
    * The cause is not initialized.
    * 
    * @param message The detail message which is later retrieved using the
    *                <code>getMessage</code> method
    */
    public SvcSignatureException(String message) {
        super(message);
    }
        
    /**
     * Constructs a new SvcSignatureException with the specified detail message and cause
     * 
     * @param message The detail message which is later retrieved using the
     *                <code>getMessage</code> method
     * @param cause The cause which is saved for the later retrieval throw by
     *              the <code>getCause</code> method
     */
    public SvcSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
