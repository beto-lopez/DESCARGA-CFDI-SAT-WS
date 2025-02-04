/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.SvcSignatureException;
import com.sicomsa.dmt.Credentials;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;

import java.security.GeneralSecurityException;
import javax.xml.crypto.dsig.XMLSignature;

import javax.xml.namespace.QName;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * @param<P> response - the response from SAT to the request made.
 * @param<Q> request - the request to SAT, depending of the service invoked.
 * 
 * Partial implementation of AbstractSvc. 
 * 
 * Implements basically the addSignedContent method defining a signature scheme
 * for subclasses. But they must provide implementations to a new method addContent
 * that should add the request content to the message and return the element to
 * be signed.
 * 
 *
 */
public abstract class AbstractGenericSvc<P,Q> extends AbstractSvc<P,Q> {
    public static final QName STS_CODE = new QName("CodEstatus"); 
    public static final QName MESSAGE  = new QName("Mensaje"); 
    
    public AbstractGenericSvc(SvcMessageFactory context) {
        super(context);
    }
    
    protected abstract SOAPElement addContent(SOAPMessage message, String rfc, Q request) throws SOAPException;
    
    /**
     * 
     * @param envelope
     * @throws SOAPException - if there is an error in creating the namespaces
     */
    @Override protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DMT_PREFIX, DMT_URI);
        super.addNamespaces(envelope);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    @Override  
    protected void addSignedContent(SOAPMessage message, Credentials creds, Q request)
            throws SOAPException, GeneralSecurityException, SvcSignatureException {
        SOAPElement toSign = addContent(message, creds.getRfc(), request);
        XMLSignature signature =
                getContext().getSignatureFactory()
                        .newGenericSignature("", creds.getCertificate());
        creds.sign(signature, toSign);
    }
    
}
