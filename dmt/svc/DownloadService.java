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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.10
 *
 * Default implementation of DMTService.
 * 
 * You can set the DownloadRepository that clients will use to save downloaded
 * data. If the repository is not set, a default LocalRepository instance will
 * be used.
 * 
 */
public class DownloadService implements DMTService {

    protected SvcMessageFactory factory;
    protected DownloadRepository repository;
    protected Service<Authorization,Object> autenticaSvc;
    protected Service<SolicitaResponse,Query> solicitaSvc;
    protected Service<VerificaResponse,String> verificaSvc;
    protected Service<DescargaResponse,String> descargaSvc;
    
    public DownloadService() {
        this(DefaultMessageFactory.newInstance());
    }
    
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
    
    public Service<Authorization,Object> getAutenticaSvc() {
        return autenticaSvc;
    }
    public Service<SolicitaResponse,Query> getSolicitsSvc() {
        return solicitaSvc;
    }
    public Service<VerificaResponse,String> getVerificaSvc() {
        return verificaSvc;
    }
    public Service<DescargaResponse,String> getDescargaSvc() {
        return descargaSvc;
    }
    
    
    @Override public Instant instant() {
        return factory.instant();
    }
    
    /**
     * 
     * @param conn
     * @param creds
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or credentials are null
     */
    @Override public Authorization autentica(SOAPConnection conn,
            Credentials creds) throws SOAPException {
        return autenticaSvc.callTheService(conn, creds, null, "");
    }
    
    /**
     * 
     * @param conn
     * @param creds
     * @param query
     * @param token
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or credentials are null
     */
    @Override public SolicitaResponse solicita(SOAPConnection conn,
            Credentials creds, Query query, String token) throws SOAPException {
        return solicitaSvc.callTheService(conn, creds, query, token);
    }
    
    /**
     * 
     * @param conn
     * @param creds
     * @param requestId
     * @param token
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection, credentials or requestId are null
     */
    @Override public VerificaResponse verifica(SOAPConnection conn,
            Credentials creds, String requestId, String token) throws SOAPException {
        return verificaSvc.callTheService(conn, creds, requestId, token);
    }
    
    /**
     * 
     * @param conn
     * @param creds
     * @param packageId
     * @param token
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection, credentials or packageId are null
     */
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
