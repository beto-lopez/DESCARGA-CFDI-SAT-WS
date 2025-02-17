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
 * Implementation of {@link AbstractSvc} for generic messages. Those are
 * the ones that need a token and all sign the same way.
 * <p>This class implements the method <code>addSignedContent</code> that defines
 * and implements a signature scheme, but needs the concrete implementation to
 * fill the message with the specific request content and return the <code>SOAPElement</code>
 * to be signed. That should be done with the implementation of the method
 * {@link AbstractGenericSvc#addContent(jakarta.xml.soap.SOAPMessage, java.lang.String, java.lang.Object) addContent() addContent()}.</p>
 * <p>
 * To create a concrete implementation of <code>AbstractGenericSvc</code> you
 * need to provide implementations for the following methods:</p>
 * <ul>
 * <li>public String getServiceName();</li>
 * <li>public String getLocation();</li>
 * <li>public String getSoapAction();</li>
 * <li>public P parseReceivedMessage(SOAPMessage message, Instant instant,
 *      Q request) throws SOAPException;</li>
 * <li>protected abstract SOAPElement addContent(SOAPMessage message, String rfc,
 *      Q request) throws SOAPException;</li>
 * </ul>
 * <br>
 * 
 * @param <P> resPonse, the type of response this service will return
 * @param <Q> reQuest, the type of request this service will receive as a parameter.
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.22
 * @since 1.0
 * 
 *
 */
public abstract class AbstractGenericSvc<P,Q> extends AbstractSvc<P,Q> {
    /**
     * StatusCode QName according to WSDL = "CodEstatus"
     */
    public static final QName STS_CODE = new QName("CodEstatus"); 
    
    /**
     * Message QName according to WSDL = "Mensaje"
     */
    public static final QName MESSAGE  = new QName("Mensaje"); 
    
    /**
     * Creates an AbsstractGenericSvc with the specified context.
     * 
     * @param context the context to use to create SOAPMessages
     * @throws IllegalArgumentException if context is null
     */
    public AbstractGenericSvc(SvcMessageFactory context) {
        super(context);
    }
    
    /**
     * Adds specific content, depending on the service implemented, to the
     * <code>SOAPMessage</code> specified and returns the <code>SOAPElement</code>
     * to be signed.
     * 
     * @param message message to add content to
     * @param rfc RFC of the contributor that is making the WS request
     * @param request request to be added to the message
     * @return the <code>SOAPElement</code> to be signed
     * @throws SOAPException if there were SOAP related problems
     */
    protected abstract SOAPElement addContent(SOAPMessage message, String rfc, Q request) throws SOAPException;
    
    ////////////////////////////////////////////////////////////////////////////
  
    /**
     * This implementation overrides this method to add namespaces needed for
     * generic messages.
     * 
     * @param envelope the envelope to 
     * @throws SOAPException - if there is an error in creating the namespaces
     */
    @Override protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DMT_PREFIX, DMT_URI);
        super.addNamespaces(envelope);
    }
      
    /**
     * Adds content to the specified <code>SOAPMessage</code>, using the specified
     * parameters and signs the message.
     * 
     * @param message to be signed after adding some content
     * @param creds to be used to sign the message
     * @param request to be used to append data to the message
     * @throws SOAPException if there were SOAP related problems
     * @throws GeneralSecurityException if there were security problems while
     *         signing message
     * @throws SvcSignatureException if there were other signature related problems
     */
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
