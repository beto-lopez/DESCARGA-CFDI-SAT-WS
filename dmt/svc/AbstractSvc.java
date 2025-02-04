/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;



import com.sicomsa.dmt.SvcSignatureException;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.util.SOAPUtils;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPEnvelope;

import jakarta.xml.ws.soap.SOAPFaultException;

import java.time.Instant;

import java.lang.System.Logger.Level;
import java.text.MessageFormat;

import java.security.GeneralSecurityException;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * @param<P> response - the response from SAT to the request made.
 * @param<Q> request - the request to SAT, depending of the service invoked.
 * 
 * @version 2025.01.04  
 *     
 * Abstract implementation of a SAT Service.
 * 
 * To create a concrete Service as a subclass of AbstractSvc you need to provide
 * implementations for the following methods:
 * 
 *  public String getServiceName();
 *  public String getLocation();
 *  public String getSoapAction();
 * 
 *  public P parseReceivedMessage(SOAPMessage message, Instant instant, Q request) throws SOAPException;
 * 
 *  protected void addSignedContent(SOAPMessage message, Credentials credentials, Q request)
 *      throws SOAPException, GeneralSecurityException, SvcSignatureException;
 */
public abstract class AbstractSvc<P,Q> implements Service<P,Q> {
    
    private static final System.Logger LOG = System.getLogger(AbstractSvc.class.getName());
    
    public static final String DMT_URI = "http://DescargaMasivaTerceros.sat.gob.mx";
    public static final String DMT_PREFIX = "dmt";
    
    public static final String DMTA_URI = "http://DescargaMasivaTerceros.gob.mx";
    public static final String DMTA_PREFIX = "dmta";
    
    public static final String DIGSN_URI = "http://www.w3.org/2000/09/xmldsig#";
    public static final String DIGSN_PREFIX = "ds";
    
    private SvcMessageFactory context;
       
    
    public AbstractSvc(SvcMessageFactory context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }
    
    protected SvcMessageFactory getContext() {
        return context;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public abstract String getServiceName();
    public abstract String getLocation();
    public abstract String getSoapAction();
    
    public abstract P parseReceivedMessage(SOAPMessage message, Instant instant, Q request) throws SOAPException;
        
    protected abstract void addSignedContent(SOAPMessage message, Credentials credentials, Q request)
            throws SOAPException, GeneralSecurityException, SvcSignatureException;
      
    ////////////////////////////////////////////////////////////////////////////
    /**
     * @param conn
     * @param creds
     * @param request
     * @param token
     * @return
     * @throws SOAPException 
     * @throws SOAPFaultException
     * @throws IllegalArgumentException
     */
    @Override
    public P callTheService(SOAPConnection conn, Credentials creds, Q request, String token) throws SOAPException {
        if (conn == null || creds == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        if (LOG.isLoggable(Level.DEBUG)) {
            LOG.log(Level.DEBUG, "Calling DMT service ({0}) for ({1}), request ({2})",
                    getServiceName(), creds.getRfc(), request);
        }
       
        SOAPMessage received = callService(
            conn, createMessageToSend(creds, request, token)
        );
                
        LOG.log(Level.TRACE, ()->{return logMessage(received);});
        
        Instant instant = getContext().instant();
                
        checkFault(received);
        
        P result = parseReceivedMessage(received, instant, request);
        LOG.log(Level.DEBUG, "DMT service response ({0})", result);
        
        return result;
    }

    protected String logMessage(SOAPMessage message) {
        try {
            return MessageFormat.format( "SAT SOAP response ({0})",
                SOAPUtils.toString(message));
        }
        catch (SOAPException | java.io.IOException e) {
            LOG.log(System.Logger.Level.ERROR, e.getMessage(), e);
        }
        return "";
    }

    public SOAPMessage createMessageToSend(Credentials credentials, Q request, String token) throws SOAPException {
        SOAPMessage message = newMessage();
        addNamespaces(message.getSOAPPart().getEnvelope());
        fillAndSign(message, credentials, request);
        setHeaders(message.getMimeHeaders(), token);
        message.saveChanges();
        return message;
    } 
    
    public SOAPMessage callService(SOAPConnection connection, SOAPMessage request) throws SOAPException {
        return connection.call(request, getLocation());
    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param message
     * @param credentials
     * @param request
     * @throws SOAPException 
     * @throws SvcSignatureException 
     * @throws NullPointerException if message or credentials are null
     */
    protected void fillAndSign(SOAPMessage message, Credentials credentials, Q request) throws SOAPException{
        try {
            addSignedContent(message, credentials, request);
        }
        catch (GeneralSecurityException e) {
            LOG.log(Level.ERROR, e.getMessage(), e);
            throw new SvcSignatureException(e.getMessage(), e);
        }
    }
     
    
    /**
     * @return
     * @throws SOAPException 
     */
    protected SOAPMessage newMessage() throws SOAPException {
        return getContext().getMessageFactory().createMessage();
    }
    /**
     * 
     * @param envelope
     * @throws SOAPException - if there is an error in creating the namespaces
     * @throws NullPointerException if envelope is null
     */
    protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DIGSN_PREFIX, DIGSN_URI);
    }
    
    /**
     * 
     * @param headers
     * @param token
     * @throws NullPointerException if headers is null
     */
    protected void setHeaders(MimeHeaders headers, String token) {
        headers.setHeader("SOAPAction", getSoapAction());
        headers.setHeader("Authorization", token);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * @param message
     * @throws SOAPException
     * @throws NullPointerException if message is null
     */
    protected void checkFault(SOAPMessage message) throws SOAPException {
        final SOAPFault fault = message.getSOAPBody().getFault();
        if (fault != null) {
            LOG.log(Level.WARNING, ()->{ return faultMessage(fault);});
            throw new SOAPFaultException(fault);
        }
    }
    
    protected String faultMessage(SOAPFault fault) {
        return MessageFormat.format("SOAPFault response: code ({0}), string ({1})",
                fault.getFaultCodeAsQName(), fault.getFaultString());
    }

    ///////////////////////////////////////////////////////////////////////////

}
