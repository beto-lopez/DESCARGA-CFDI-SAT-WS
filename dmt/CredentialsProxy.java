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
 * @since 2025.01.03
 * 
 * Abstract implementation of Credentials. Does not load the certificates until needed.
 * 
 * To create a concrete CredentialsProxy you need to implement:
 *     protected Credentials doGetCredentials() throws RepositoryException;
 * 
 */
public abstract class CredentialsProxy implements Credentials {
    private Credentials _credentials;
    protected final String rfc;
    
    public CredentialsProxy(String rfc) {
        if (rfc == null) {
            throw new IllegalArgumentException("invalid rfc");
        }
        this.rfc = rfc.toUpperCase();
    }
    
    protected abstract Credentials doGetCredentials() throws RepositoryException;
    
    @Override public String getRfc() {
        return rfc;
    }
    
    @Override public X509Certificate getCertificate() {
        return getCredentials().getCertificate();
    }
   
    @Override public void sign(XMLSignature signature, SOAPElement element) throws SvcSignatureException {
        getCredentials().sign(signature, element);
    }
    
    public synchronized Credentials getCredentials() throws RepositoryException {
        if (_credentials == null) {
            _credentials = doGetCredentials();
            if (_credentials == null) {
                throw new RepositoryException("Credentials not found for:"+rfc);
            }
        }
        return _credentials;
    }
    
    public synchronized void unload() {
        _credentials = null;
    }

}
