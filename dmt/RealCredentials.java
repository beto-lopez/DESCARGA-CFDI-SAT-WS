/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 
 */

package com.sicomsa.dmt;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import jakarta.xml.soap.SOAPElement;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.dom.DOMSignContext;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 28-9-24
 * 
 * Credentials implmentation with loaded certificate and private key.
 * 
 */
public class RealCredentials implements Credentials {
    
    private String rfc;
    private X509Certificate certificate;
    private PrivateKey privateKey;
    
    private static final System.Logger LOG = System.getLogger(RealCredentials.class.getName());
    
    protected RealCredentials() {
    }
    
    public RealCredentials(String rfc, X509Certificate certificate, PrivateKey privateKey) {
        if (certificate == null || privateKey == null || rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.rfc = rfc.toUpperCase();
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    @Override public String getRfc() {
        return rfc;
    }
    
    @Override public X509Certificate getCertificate() {
        return certificate;
    }
    
    @Override public void sign(XMLSignature signature, SOAPElement element)
            throws SvcSignatureException {
        
        if (signature == null || element == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        try {
            signature.sign( new DOMSignContext(privateKey, element) );
        }
        catch (MarshalException | XMLSignatureException e) {
            LOG.log(System.Logger.Level.ERROR, e.getMessage(), e);
            throw new SvcSignatureException(e.getMessage(), e);
        }
    }
  
}
