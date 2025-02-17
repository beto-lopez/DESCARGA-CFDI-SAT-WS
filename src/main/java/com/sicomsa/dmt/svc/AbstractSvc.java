/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.SvcSignatureException;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

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
 * Abstract implementation of {@link Service}.
 * <p>This class implements method <code>callTheService</code> and leaves
 * several methods unimplemented. To create a concrete <code>Service</code> as a
 * subclass of <code>AbstractSvc</code> you need to provide implementations for
 * the following methods:
 * <ul>
 * <li>public String getServiceName();</li>
 * <li>public String getLocation();</li>
 * <li>public String getSoapAction();</li>
 * <li>public P parseReceivedMessage(SOAPMessage message, Instant instant, Q request) throws SOAPException;</li>
 * <li>protected void addSignedContent(SOAPMessage message, Credentials credentials, Q request)<br>
 *      throws SOAPException, GeneralSecurityException, SvcSignatureException;</li>
 * </ul>
 *
 * 
 * @param <P> resPonse, the type of response this service will return
 * @param <Q> reQuest, the type of request this service will receive as a parameter.
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04
 * @since 1.0
 * 
 *   
 *     
 */
public abstract class AbstractSvc<P,Q> implements Service<P,Q> {
    
    private static final System.Logger LOG = System.getLogger(AbstractSvc.class.getName());
    
    /**
     * Web Service message uri = "http://DescargaMasivaTerceros.sat.gob.mx"
     */
    public static final String DMT_URI = "http://DescargaMasivaTerceros.sat.gob.mx";
    
    /**
     * Web Service message prefix = "dmt"
     */
    public static final String DMT_PREFIX = "dmt";
    
    /**
     * Web Service authorization uri = "http://DescargaMasivaTerceros.gob.mx"
     */
    public static final String DMTA_URI = "http://DescargaMasivaTerceros.gob.mx";
    
    /**
     * Web Service authorization prefix = "dmta"
     */
    public static final String DMTA_PREFIX = "dmta";
    
    /**
     * Digital signature uri = "http://www.w3.org/2000/09/xmldsig#"
     */
    public static final String DIGSN_URI = "http://www.w3.org/2000/09/xmldsig#";
    
    /**
     * Digital signature prefix = "ds"
     */
    public static final String DIGSN_PREFIX = "ds";
    
    
    /**
     * Context to use to create messages
     */
    private SvcMessageFactory context;
       
    /**
     * Creates an <code>AbstractSvc</code> with the specified context.
     * 
     * @param context the message factory to use
     * @throws IllegalArgumentException if context is null
     */
    public AbstractSvc(SvcMessageFactory context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context;
    }
    
    /**
     * Returns this service's message factory context
     * 
     * @return this service's message factory context
     */
    protected SvcMessageFactory getContext() {
        return context;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the name of this service. Should be unique.
     * 
     * @return the name of this service.
     */
    public abstract String getServiceName();
    
    /**
     * Returns the location this service should connect to
     * <p>This location will be used to connect to the web service</p>
     * 
     * @return the location this service should connect to
     */
    public abstract String getLocation();
    
    /**
     * Returns the SOAP action this service invokes.
     * <p>This action will be set in the headers of the <code>SOAPMessage</code>s
     * sent by this service.</p> 
     * 
     * @return the SOAP action this service invokes.
     */
    public abstract String getSoapAction();
    
    /**
     * Returns an instance of type P with the information of the <code>SOAPMessage</code>
     * received from the web service. Instant and request can be used to build on
     * the response to return, it is up to the implementation.
     *  
     * @param message received from the web service.
     * @param instant instant when the message was received
     * @param request request used to send the request that originated the message
     *                we are parsing here.
     * @return a response of type P 
     * @throws SOAPException if there were SOAP related problems
     */
    public abstract P parseReceivedMessage(SOAPMessage message, Instant instant, Q request) throws SOAPException;
        
    /**
     * Adds content to the specified <code>SOAPMessage</code>, using the specified
     * parameters and signs the message.
     * 
     * @param message to be signed after adding some content
     * @param credentials to be used to sign the message
     * @param request to be used to append data to the message
     * @throws SOAPException if there were SOAP related problems
     * @throws GeneralSecurityException if there were security problems while
     *         signing message
     * @throws SvcSignatureException if there were other signature related problems
     */
    protected abstract void addSignedContent(SOAPMessage message, Credentials credentials, Q request)
            throws SOAPException, GeneralSecurityException, SvcSignatureException;
      
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a <code>SOAPMessage</code> using the specified parameters and sends
     * it to the endpoint this service implements; <b>blocks until it receives the
     * response</b> as a <code>SOAPMessage</code> that then parses in order to return
     * a response of type P.
     * <p>If the <code>SOAPMessage</code> that this service receives is a
     * <code>SOAPFault</code> a {@link jakarta.xml.ws.soap.SOAPFaultException SOAPFaultException}
     * should be thrown unless stated otherwise by subclass.</p>
     * 
     * @param conn connection to use to connect to WS
     * @param creds credentials to use to sign <code>SOAPMessage</code>
     * @param request a request of type Q
     * @param token a token which could be null or blank in some services.
     *              For example, to authenticate we do not need to send a token.
     * @return a response of type P
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if connection or credentials are null
     * @throws SOAPFaultException if message received was a <code>SOAPFault</code>
     * @throws SvcParseException if there were problems while parsing message received from WS
     * @throws SvcSignatureException if there were signature related problems
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

    /**
     * Returns the specified message as string
     * 
     * @param message message to convert
     * @return the specified message as string
     */
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

    /**
     * Creates and returns the <code>SOAPMessage</code> to send to the WS.
     * <p>This method creates a new <code>SOAPMessage</code>, adds the namespaces
     * required; fills ands signs the message with the parameters received; sets
     * the headers and returns the message ready to be sent.</p>
     * 
     * @param credentials credentials to be used to sign message
     * @param request with the content to be added to the request
     * @param token the token to use to identify with SAT. In some cases it
     *              could be null or blank.
     * @return the message to send
     * @throws SOAPException if there were SOAP related problems
     * @throws NullPointerException if credentials are null
     * @throws SvcSignatureException if there were signature related problems
     */
    public SOAPMessage createMessageToSend(Credentials credentials, Q request, String token) throws SOAPException {
        SOAPMessage message = newMessage();
        addNamespaces(message.getSOAPPart().getEnvelope());
        fillAndSign(message, credentials, request);
        setHeaders(message.getMimeHeaders(), token);
        message.saveChanges();
        return message;
    } 
    
    /**
     * Uses specified connection to call the web service sending the specified
     * request. Returns the <code>SOAPMessage</code> received from the connection.
     * <p>The location to send the message to will be obtained from this service's
     * <code>getLocation()</code> method.</p>
     * 
     * @param connection the connection to use
     * @param request the request to send
     * @return the message received from the connection's call to the WS
     * @throws SOAPException if there was a SOAP error
     * @throws NullPointerException if connection is null
     */
    public SOAPMessage callService(SOAPConnection connection, SOAPMessage request) throws SOAPException {
        return connection.call(request, getLocation());
    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds request content to the message and signs it.
     * 
     * @param message the message to sign after adding content
     * @param credentials credentials to use to fill and sign the message
     * @param request request containing data used to fill the message
     * @throws SOAPException if SOAP related problems arose
     * @throws NullPointerException if message or credentials are null
     * @throws SvcSignatureException if there were signature related problems
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
     * Returns a new <code>SOAPMessage</code> using this service's message context.
     * 
     * @return the new SOAPMessage
     * @throws SOAPException it there were SOAP related 
     */
    protected SOAPMessage newMessage() throws SOAPException {
        return getContext().getMessageFactory().createMessage();
    }
    
    /**
     * Adds the namespaces this service requires to create message to sign and
     * send to web service.
     * <p>This implementation adds the digital signature namespace. You should
     * call super.addNamespaces(envelope) is you are overriding this method and
     * need the digital signature namespace.</p>
     *  
     * @param envelope envelope to add the namespace to.
     * @throws SOAPException - if there is an error in creating the namespaces
     * @throws NullPointerException if envelope is null
     */
    protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DIGSN_PREFIX, DIGSN_URI);
    }
    
    /**
     * Sets "SOAPAction" and "Authorization" attributes to headers specified.
     * <p>Uses method <code>getSoapAction()</code> of this service to set the
     * SOAP action and the token received to set the authorization value.</p>
     * 
     * @param headers to update
     * @param token to set to headers
     * @throws NullPointerException if headers is null
     */
    protected void setHeaders(MimeHeaders headers, String token) {
        headers.setHeader("SOAPAction", getSoapAction());
        headers.setHeader("Authorization", token);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Throws a <code>SOAPFaultException</code> if the message specified is
     * a <code>SOAPFault</code>.
     * 
     * @param message the message to validate
     * @throws SOAPException if there were SOAP related problems
     * @throws NullPointerException if message is null
     * @throws SOAPFaultException if message received is a SOAPFault
     */
    protected void checkFault(SOAPMessage message) throws SOAPException {
        final SOAPFault fault = message.getSOAPBody().getFault();
        if (fault != null) {
            LOG.log(Level.WARNING, ()->{ return faultMessage(fault);});
            throw new SOAPFaultException(fault);
        }
    }
    
    /**
     * Returns a string representation of the <code>SOAPFault</code> specified.
     * 
     * @param fault the fault specified
     * @return a string representation of the fault specified.
     * @throws NullPointerException if fault is null
     */
    protected String faultMessage(SOAPFault fault) {
        return MessageFormat.format("SOAPFault response: code ({0}), string ({1})",
                fault.getFaultCodeAsQName(), fault.getFaultString());
    }

    ///////////////////////////////////////////////////////////////////////////

}
