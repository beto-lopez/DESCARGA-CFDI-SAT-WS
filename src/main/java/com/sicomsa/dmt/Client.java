/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


import com.sicomsa.dmt.svc.DownloadService;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.WebServiceException;

import java.security.cert.X509Certificate;

/**
 * <code>Client</code> is an implementation of <code>DMTClient</code> that calls
 * <code>DMTService</code> in order to consume the massive cfdi download service.<p>
 * 
 * <code>Client</code> keeps the authorized token in order to make requests, and
 * renews it when needed. SAT tokens usually last 5 minutes.<p>
 * 
 * You can specify a <code>DMTService</code> to use. If not, a default implementation
 * will be used.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @since 1.0
 * @version 2025.01.03
 * 
 */
public class Client  implements DMTClient {
    
    /**
     * The service this <code>Client</code> will use to call SAT.
     */
    protected DMTService service;
    
    /**
     * This client's <code>Credentials</code>
     */
    private Credentials credentials;
    
    /**
     * The latest <code>Authorization</code> this client has available.
     */
    protected Authorization authorization;
    
    /**
     * Returns a <code>Client</code> with the given <code>Credentials</code>
     * which will use the default service to call SAT.
     * 
     * @param credentials the credentials to use make requests
     */
    public Client(Credentials credentials) {
        this(credentials, new DownloadService());
    }
    
    /**
     * Returns a <code>Client</code> with the given <code>Credentials</code> and
     * with the given <code>DMTService</code> to use to call SAT.
     * 
     * @param service the service to use to make requests to SAT
     * @param credentials the credentials to use to be identified
     */
    public Client(Credentials credentials, DMTService service) {
        if (credentials == null || service == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.service = service;
        this.credentials = credentials;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// DMTClient implementation
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public String getRfc() {
        return credentials.getRfc();
    }
    
    @Override public X509Certificate getCertificate() {
        return credentials.getCertificate();
    }

    @Override
    public SolicitaResponse requestDownload(SOAPConnection conn, Query query) throws SOAPException {
        return service.solicita(conn, credentials, query, getValidToken(conn));
    }
    
    @Override
    public VerificaResponse verifyRequest(SOAPConnection conn, String requestId) throws SOAPException{
        return service.verifica(conn, credentials, requestId, getValidToken(conn));
    }
    
    @Override
    public DescargaResponse download(SOAPConnection conn, String packageId) throws SOAPException {
        return service.descarga(conn, credentials, packageId, getValidToken(conn));
    }
    
    @Override public void save(String packageId, String encodedPackage) {
        getRepository().save(getRfc(), packageId, encodedPackage, null);
    }
   
    /////////////////////////////////////////////////////////////////////////////
    
    /**
     * Verifies the current <code>Authorization</code> to detect if the token
     * is still valid and requests a new one if it is expired or has not yet
     * gotten one. And returns a wrapped and ready to use valid token.
     * 
     * @param conn the connection to use
     * @return a wrapped valid token
     * @throws SOAPException if there were SOAP related problems
     * @throws WebServiceException if there were service related problems
     */
    protected synchronized String getValidToken(SOAPConnection conn) throws SOAPException {
        if (!isValid(authorization)) {
            authorization = autentica(conn);
        }
        return Authorization.wrapp(authorization.getToken());
    }
    
    /**
     * This method calls SAT's autentica service to authenticate and receive
     * a token to be used in other requests. It returns an <code>Authorization</code>
     * that contains the token and other instant related methods.
     * 
     * @param conn the SOAP connection to use
     * @return an Authorization that contains the token and other useful methods.
     * @throws SOAPException 
     * @throws WebServiceException if there were other service related problems
     */
    protected Authorization autentica(SOAPConnection conn) throws SOAPException {
        return service.autentica(conn, credentials);
    }

    /**
     * Protected method to access the repository to use to save the downloaded
     * packages
     * 
     * @return the download repository of this client
     */
    protected DownloadRepository getRepository() {
        return service.getRepository();
    }

    /**
     * Returns true if the <code>Authorization</code> has not expired according
     * to the instant source of the service used by this client.
     * 
     * @param authorization the authorization to test
     * @return true if the authorization has not expired
     */
    protected boolean isValid(Authorization authorization) {
        return (authorization != null
                && authorization.getExpires().isAfter(service.instant()));
    }
        
    
}
