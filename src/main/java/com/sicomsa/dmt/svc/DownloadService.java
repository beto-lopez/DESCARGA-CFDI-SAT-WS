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


/**
 * Default implementation of {@link com.sicomsa.dmt.DMTService} to consume the WS.
 * <p>When you create a DownloadService you assign a {@link SvcMessageFactory} that
 * will be used as the context to instantiate the concrete services this service
 * will use.</p>
 * <p>You can set to this class a {@link com.sicomsa.dmt.DownloadRepository} that
 * will be used to save the CFDIs downloaded. If you do not set one a default
 * {@link LocalRepository} will be used.</p>
 *  
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.10
 * @since 1.0
 *
 *  
 * 
 */
public class DownloadService implements DMTService {

    /**
     * Factory used to build services
     */
    protected SvcMessageFactory factory;
    
    /**
     * Current DownloadRepository
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
     * Constructs a new DownloadService with a default <code>SvcMessageFactory</code>.
     */
    public DownloadService() {
        this(DefaultMessageFactory.newInstance());
    }
    
    /**
     * Constructs a new DownloadServide with the specified factory.
     * 
     * @param factory the <code>SvcMessageFactory</code> this service will use
     */
    public DownloadService(SvcMessageFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is required");
        }
        this.factory = factory;
        autenticaSvc = new AuthenticationSvc(factory);
        solicitaSvc  = new SolicitaSvc(factory);
        verificaSvc  = new VerificaSvc(factory);
        descargaSvc  = new DescargaSvc(factory);
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
        return factory.instant();
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
