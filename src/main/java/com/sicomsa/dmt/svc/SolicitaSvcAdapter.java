/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.QueryType;
import com.sicomsa.dmt.SolicitaResponse;

import com.sicomsa.dmt.SvcSignatureException;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

import jakarta.xml.ws.soap.SOAPFaultException;
/**
 * Concrete implementation of {@literal Service<SolicitaResponse,Query>} to
 * send download requests to SAT's web service version 1.5
 * <p>This service will use the corresponding service depending on the
 * {@link com.sicomsa.dmt.QueryType} received in the <code>callTheService</code>
 * method.</p>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.06.04
 * @since 1.5
 */
public class SolicitaSvcAdapter implements Service<SolicitaResponse,Query> {
    /**
     * Service to request downloads of emitted certificates
     */
    protected Service<SolicitaResponse,Query> emitidosSvc;
    
    /**
     * Service to request downloads of received certificates
     */
    protected Service<SolicitaResponse,Query> recibidosSvc;
    
    /**
     * Service to request downloads of certificates by Id
     */
    protected Service<SolicitaResponse,Query> folioSvc;
        
    /**
     * Creates a new <code>SolicitaSvcAdapter</code> with the provided services.
     * 
     * @param emitidosSvc service to request downloads of emitted certificates
     * @param recibidosSvc service to request downloads of received certificates
     * @param folioSvc service to request downloads of certificates by Id
     * @throws IllegalArgumentException if any of the services provided are null
     */
    public SolicitaSvcAdapter(
            Service<SolicitaResponse,Query> emitidosSvc,
            Service<SolicitaResponse,Query> recibidosSvc,
            Service<SolicitaResponse,Query> folioSvc) {
            
        if (emitidosSvc == null || recibidosSvc == null || folioSvc == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.emitidosSvc = emitidosSvc;
        this.recibidosSvc = recibidosSvc;
        this.folioSvc = folioSvc;
    }
    
    /**
     * Returns the <code>Service</code> used to download emitted certificates
     * 
     * @return the <code>Service</code> used to download emitted certificates
     */
    protected Service<SolicitaResponse,Query> getEmitidosService() {
        return emitidosSvc;
    }
    
    /**
     * Returns the <code>Service</code> used to download received certificates
     * 
     * @return the <code>Service</code> used to download received certificates
     */
    protected Service<SolicitaResponse,Query> getRecibidosService() {
        return recibidosSvc;
    }
    
    /**
     * Returns the <code>Service</code> used to download certificates by Id
     * 
     * @return the <code>Service</code> used to download certificates by Id
     */
    protected Service<SolicitaResponse,Query> getFolioService() {
        return folioSvc;
    }
        
    /**
     * Calls the web service to request download for emitted; received, or by
     * Id requests, depending on the {@link com.sicomsa.dmt.QueryType} of the
     * {@link com.sicomsa.dmt.Query} received, and returns web service's response.
     * <p><code>Query</code> received must be an instance of {@link com.sicomsa.dmt.ExtendedQuery}
     * </p>
     * 
     * @param connection connection to use
     * @param credentials credentials to use
     * @param request request to send
     * @param token to use
     * @return web service response as a {@link com.sicomsa.dmt.SolicitaResponse}
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if connection or credentials are null
     * @throws SOAPFaultException if message received was a <code>SOAPFault</code>
     * @throws SvcSignatureException if there were signature related problems
     */
    @Override
    public SolicitaResponse callTheService(SOAPConnection connection,
            Credentials credentials, Query request, String token) throws SOAPException {

        if (!(request instanceof ExtendedQuery query)) {
            throw new IllegalArgumentException("query must be instance of MultipleQuery as of version 1.5");
        }
        return getService(query.getType())
                .callTheService(connection, credentials, request, token);
    }
    
    /**
     * Returns the {@literal Service<SolicitaResponse,Query>} for the type
     * of query provided.
     * 
     * @param type type of query
     * @return {@literal Service<SolicitaResponse,Query>} for the type
     *         of query provided
     * @throws NullPointerException if type is null
     */
    protected Service<SolicitaResponse,Query> getService(QueryType type) {
        if (type == null) {
            throw new NullPointerException("null type");
        }
        return
            switch(type) {
                case EMITIDOS -> emitidosSvc;
                case RECIBIDOS -> recibidosSvc;
                case FOLIO -> folioSvc;
            };
    }
    
}
