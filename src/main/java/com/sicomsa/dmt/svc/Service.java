/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.SvcSignatureException;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

import jakarta.xml.ws.soap.SOAPFaultException;

/**
 * Interface that defines the method and parameters needed to implement a
 * service that consumes a massive download SAT web service.
 *
 * @param <P> resPonse, the type of response this service will return
 * @param <Q> reQuest, the type of request this service will receive as a parameter.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.27
 * @since 1.0
 * 
 */
public interface Service<P extends Object,Q extends Object> {
    
    /**
     * Creates a <code>SOAPMessage</code> using the specified parameters and sends
     * it to the endpoint this service implements; <b>blocks until it receives the
     * response</b> as a <code>SOAPMessage</code> that then parses in order to return
     * a response of type P.
     * <p>If the <code>SOAPMessage</code> that this service receives is a
     * <code>SOAPFault</code> a {@link jakarta.xml.ws.soap.SOAPFaultException SOAPFaultException}
     * should be thrown unless stated otherwise by subclass.</p>
     * 
     * @param connection connection to use to connect to WS
     * @param credentials credentials to use to sign <code>SOAPMessage</code>
     * @param request a request of type Q
     * @param token a token which could be null or blank in some services.
     *              For example, to authenticate we do not need to send a token.
     * @return a response of type P
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if connection or credentials are null
     * @throws SOAPFaultException if message received was a <code>SOAPFault</code>
     * @throws SvcSignatureException if there were signature related problems
     */
    public P callTheService(SOAPConnection connection, Credentials credentials, Q request, String token) throws SOAPException;
    
}
