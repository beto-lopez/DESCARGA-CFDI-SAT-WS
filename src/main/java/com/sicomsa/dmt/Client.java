/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


import com.sicomsa.dmt.svc.DownloadService;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

import java.security.cert.X509Certificate;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.09
 * @version 2025.01.03
 * 
 * Implementation of DMTClient that calls DMTService in order to consume
 * the massive cfdi download service.
 * 
 * Client keeps the authorized token in order to make requests, and renews it
 * when needed. SAT tokens usually last 5 minutes.
 * 
 * You can specify a DMTService to use. If not, the default implementation
 * will be used.
 * 
 */
public class Client  implements DMTClient {
    
    protected DMTService service;
    private Credentials credentials;
    protected Authorization authorization;
    
    public Client(Credentials credentials) {
        this(credentials, new DownloadService());
    }
    
    /**
     * 
     * @param service
     * @param credentials 
     */
    public Client(Credentials credentials, DMTService service) {
        if (credentials == null || service == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.service = service;
        this.credentials = credentials;
    }
    
    @Override public String getRfc() {
        return credentials.getRfc();
    }
    
    /**
     * this will also load the certificate if under lazy load
     * @return 
     */
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
    
    @Override public void save(String packageId, String encodedPackage) throws RepositoryException {
        getRepository().save(getRfc(), packageId, encodedPackage, null);
    }
   
    /////////////////////////////////////////////////////////////////////////////
    
    protected synchronized String getValidToken(SOAPConnection conn) throws SOAPException {
        if (!isValid(authorization)) {
            authorization = autentica(conn);
        }
        return Authorization.wrapp(authorization.getToken());
    }
    
    protected Authorization autentica(SOAPConnection conn) throws SOAPException {
        return service.autentica(conn, credentials);
    }

    protected DownloadRepository getRepository() {
        return service.getRepository();
    }

    protected boolean isValid(Authorization authorization) {
        return (authorization != null
                && authorization.getExpires().isAfter(service.instant()));
    }
        
    
}
