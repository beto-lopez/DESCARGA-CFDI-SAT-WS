/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import jakarta.xml.soap.SOAPElement;
import java.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignature;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 28-9-24
 *
 * Contains the methods needed in order to sign SOAP requests for the massive
 * cfdi download service, for a singular contributor.
 * 
 */
public interface Credentials {

    public String getRfc();
    
    public X509Certificate getCertificate();
   
    public void sign(XMLSignature signature, SOAPElement element) throws SvcSignatureException;
    
}
