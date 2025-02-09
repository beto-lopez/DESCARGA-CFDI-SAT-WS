/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.ws.WebServiceException;
import javax.xml.crypto.dsig.XMLSignature;
import java.security.cert.X509Certificate;


/**
 * <code>Credentials</code> contains the methods needed in order to sign SOAP
 * requests for the massive cfdi download service, for a singular contributor.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024-9-28
 * @since 1.0
 *
 */
public interface Credentials {

    /**
     * Returns the RFC of the contributor owner of this credentials.
     * 
     * @return the RFC of the contributor owner of this credentials
     */
    public String getRfc();
    
    /**
     * Returns the <code>X509Certificate</code> of the contributor owner of this
     * credentials.
     * 
     * @return the <code>X509Certificate</code> of the contributor owner of this
     * credentials.
     * @throws WebServiceException if there were service related problems
     */
    public X509Certificate getCertificate();
   
    /**
     * Uses <code>signature</code> to sign the <code>element</code> received using
     * this credentials.
     * 
     * @param signature the signature to use 
     * @param element the element in which to append the signature
     * @throws IllegalArgumentException if signature or element are null
     * @throws SvcSignatureException if there were signature related problems
     * @throws WebServiceException if there were other service related problems
     */
    public void sign(XMLSignature signature, SOAPElement element);
    
}
