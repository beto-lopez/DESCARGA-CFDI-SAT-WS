/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


import jakarta.xml.soap.SOAPElement;
import java.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignature;

/**
 * Abstract implementation of Credentials. Does not load the certificate until needed.<p>
 * 
 * To create a concrete CredentialsProxy you need to implement:
 * <ul>
 * <li>{@link CredentialsProxy#doGetCredentials() }</li>
 * </ul>
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.03
 * @since 1.0
 * 
 */
public abstract class CredentialsProxy implements Credentials {
    /**
     * Credentials instance, null if not loaded
     */
    private Credentials _credentials;
    
    /**
     * RFC of this <code>Credentials</code>, upper casted.
     */
    protected final String rfc;
    
    /**
     * Creates a <code>CredentialsProxy</code> with the given RFC
     * 
     * @param rfc the rfc of this credentials
     */
    public CredentialsProxy(String rfc) {
        if (rfc == null) {
            throw new IllegalArgumentException("invalid rfc");
        }
        this.rfc = rfc.toUpperCase();
    }
    
    /**
     * Returns an implementation of <code>Credentials</code> with loaded
     * certificates and ready to sign.
     * 
     * @return and implementation of <code>Credentials</code> with loaded
     * certificates and ready to sign.
     * 
     * @throws RepositoryException if there were problems to retrieve credentials
     */
    protected abstract Credentials doGetCredentials();
    
    ////////////////////////////////////////////////////////////////////////////
    // Credentials implementation
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public String getRfc() {
        return rfc;
    }
    
    @Override public X509Certificate getCertificate() {
        return getCredentials().getCertificate();
    }
   
    @Override public void sign(XMLSignature signature, SOAPElement element) {
        getCredentials().sign(signature, element);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Loads <code>Credentials</code> if they have not been loaded, and returns
     * them.
     * 
     * @return the Credentials
     * @throws RepositoryException if there were problems while loading
     */
    public synchronized Credentials getCredentials() {
        if (_credentials == null) {
            _credentials = doGetCredentials();
            if (_credentials == null) {
                throw new RepositoryException("Credentials not found for:"+rfc);
            }
        }
        return _credentials;
    }
    
    /**
     * Clears the property that references the loaded <code>Credentials</code>.
     */
    public synchronized void unload() {
        _credentials = null;
    }

}
