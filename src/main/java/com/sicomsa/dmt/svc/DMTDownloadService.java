/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.DMTService;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.Authorization;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.DownloadRepository;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

import java.time.Instant;
import java.time.InstantSource;

/**
 * New implementation of {@link com.sicomsa.dmt.DMTService} to consume the WS
 * as of SAT's version 1.5 (may 30, 2025).
 * <p>It is recommended to instantiate it using class {@link ServiceFactory}.</p>
 * <p>In this implementation the instant source and the services are received
 * in the constructor, and are used to implement the interface.</p>
 * <p>You can set to this class a {@link com.sicomsa.dmt.DownloadRepository} that
 * will be used to save the CFDIs downloaded. If you do not set one a default
 * {@link LocalRepository} will be used.</p>
 *  
 * @see com.sicomsa.dmt.svc.ServiceFactory for more information on building a service.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.4
 * @since       1.5 
 *  
 * 
 */
public class DMTDownloadService implements DMTService {

    /**
     * <code>InstantSource</code> this service uses
     */
    protected InstantSource instantSource;
    
    /**
     * Current <code>DownloadRepository</code> of this service
     */
    protected DownloadRepository repository;
    
    /**
     * Service that performs authentication service
     */
    protected Service<Authorization,Object> autenticaSvc;
    
    /**
     * Service that performs request download service
     */
    protected Service<SolicitaResponse,Query> solicitaSvc;
    
    /**
     * Service that performs verify download request service
     */
    protected Service<VerificaResponse,String> verificaSvc;
    
    /**
     * Service that performs download package service
     */
    protected Service<DescargaResponse,String> descargaSvc;
    
    /**
     * Constructs a <code>DownloadService</code> with the specified parameters.
     * 
     * @param instantSource to use in this service
     * @param autenticaSvc authentication service to use
     * @param solicitaSvc request download service to use
     * @param verificaSvc verify request service to use
     * @param descargaSvc download packages service to use
     */
    public DMTDownloadService(
            InstantSource instantSource,
            Service<Authorization,Object> autenticaSvc,
            Service<SolicitaResponse,Query> solicitaSvc,
            Service<VerificaResponse,String> verificaSvc,
            Service<DescargaResponse,String> descargaSvc) {
        if (instantSource == null || autenticaSvc == null || solicitaSvc == null
                || verificaSvc == null || descargaSvc == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.instantSource = instantSource;
        this.autenticaSvc = autenticaSvc;
        this.solicitaSvc = solicitaSvc;
        this.verificaSvc = verificaSvc;
        this.descargaSvc = descargaSvc;
    }
    
    
    /**
     * Returns the {@link Service} this DownloadService uses to implement
     * the authentication service of the WS.
     * 
     * @return the {@link Service} this DownloadService uses to implement
     * the authentication service of the WS.
     */
    public Service<Authorization,Object> getAutenticaSvc() {
        return autenticaSvc;
    }
    
    /**
     * Returns the {@link Service} this DownloadService uses to implement
     * the request download service of the WS.
     * 
     * @return the {@link Service} this DownloadService uses to implement
     * the request download service of the WS.
     */
    public Service<SolicitaResponse,Query> getSolicitsSvc() {
        return solicitaSvc;
    }
    
    /**
     * Returns the {@link Service} this DownloadService uses to implement
     * the verify download request service of the WS.
     * 
     * @return the {@link Service} this DownloadService uses to implement
     * the verify download request service of the WS.
     */
    public Service<VerificaResponse,String> getVerificaSvc() {
        return verificaSvc;
    }
    
    
    /**
     * Returns the {@link Service} this DownloadService uses to implement
     * the download package service of the WS.
     * 
     * @return the {@link Service} this DownloadService uses to implement
     * the download package service of the WS.
     */
    public Service<DescargaResponse,String> getDescargaSvc() {
        return descargaSvc;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// DMTService implementation
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public Instant instant() {
        return instantSource.instant();
    }
    
    @Override public Authorization autentica(SOAPConnection conn,
            Credentials creds) throws SOAPException {
        return autenticaSvc.callTheService(conn, creds, null, "");
    }
    
    @Override public SolicitaResponse solicita(SOAPConnection conn,
            Credentials creds, Query query, String token) throws SOAPException {
        return solicitaSvc.callTheService(conn, creds, query, token);
    }
    
    @Override public VerificaResponse verifica(SOAPConnection conn,
            Credentials creds, String requestId, String token) throws SOAPException {
        return verificaSvc.callTheService(conn, creds, requestId, token);
    }
    
    @Override public DescargaResponse descarga(SOAPConnection conn,
            Credentials creds, String packageId, String token) throws SOAPException {
        return descargaSvc.callTheService(conn, creds, packageId, token);
    }
    
    @Override public synchronized void setRepository(DownloadRepository repository) {
        this.repository = repository;
    }
    
    @Override public synchronized DownloadRepository getRepository() {
        if (repository == null) {
            repository = new LocalRepository();
        }
        return repository;
    }
    
}
