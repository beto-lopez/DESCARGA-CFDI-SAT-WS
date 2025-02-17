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
 * This class is an implementation of <code>Credentials</code> that uses
 * a private key in order to implement the <code>sign</code> method.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024-9-28
 * @since 1.0
 * 
 * 
 */
public class RealCredentials implements Credentials {
    
    /**
     * The RFC of the contributor owner of this credentials.
     */
    private String rfc;
    
    /**
     * The <code>X509Certificate</code> of the contributor owner of this credentials.
     */
    private X509Certificate certificate;
    /**
     * The <code>PrivateKey</code> of the contributor owner of this credentials.
     */
    private PrivateKey privateKey;
    
    private static final System.Logger LOG = System.getLogger(RealCredentials.class.getName());
    
    /**
     * Protected constructor without parameters
     */
    protected RealCredentials() {
    }
    
    /**
     * Returns an instance of <code>RealCredentials</code> that will use the
     * received <code>rfc</code>, <code>certificate</code>, and <code>privateKey</code>
     * to implement <code>Credentials</code>.
     * This constructor will upper cast the parameter <code>rfc</code>.
     * 
     * @param rfc the RFC of the contributor owner of this credentials
     * @param certificate the X509Certificate of the contributor owner of this credentials
     * @param privateKey the PrivateKey of the contributor owner of this credentials
     * @throws IllegalArgumentException if rfc, certificate or privateKey are null.
     */
    public RealCredentials(String rfc, X509Certificate certificate, PrivateKey privateKey) {
        if (certificate == null || privateKey == null || rfc == null || rfc.isBlank()) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.rfc = rfc.toUpperCase();
        this.certificate = certificate;
        this.privateKey = privateKey;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Credentials implementation
    ////////////////////////////////////////////////////////////////////////////

    @Override public String getRfc() {
        return rfc;
    }
    
    @Override public X509Certificate getCertificate() {
        return certificate;
    }
    
    @Override public void sign(XMLSignature signature, SOAPElement element) {
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
    
    ////////////////////////////////////////////////////////////////////////////
  
}
