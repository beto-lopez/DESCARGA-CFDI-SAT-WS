/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import jakarta.xml.ws.WebServiceException;

/**
 * Base class for Repository related exceptions
 * Note: this is a RuntimeException
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.13
 * @since 1.0
 * 
 */
public class RepositoryException extends WebServiceException {
    
    private static final long serialVersionUID = 20241113L;
    
    /**
     * Constructs a new RepositoryException with the specified detail message.
     * The cause is not initialized.
     * 
     * @param message The detail message which is later retrieved using the
     *                <code>getMessage</code> method
     */
    public RepositoryException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new RepositoryException with the specified cause and a detail
     * message of (cause==null ? null : cause.toString()) (which typically
     * contains the class and detail message of cause).
     * 
     * @param cause The cause which is saved for the later retrieval throw by the
     *              <code>getCause</code> method. (A null value is permitted, and
     *              indicates that the cause is nonexistent or unknown.)
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new RepositoryException with the specified detail message and cause
     * 
     * @param message The detail message which is later retrieved using the
     *                <code>getMessage</code> method
     * @param cause The cause which is saved for the later retrieval throw by
     *              the <code>getCause</code> method
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
