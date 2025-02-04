/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.Credentials;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.27
 * @param <P>
 * @param <Q>
 *
 * Interface that defines the method and parameters needed to implement a
 * service that consumes a massive download SAT web service.
 * 
 */
public interface Service<P extends Object,Q extends Object> {
    
    /**
     * 
     * @param connection
     * @param credentials
     * @param request
     * @param token
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or credentials are null
     */
    public P callTheService(SOAPConnection connection, Credentials credentials, Q request, String token) throws SOAPException;
    
}
