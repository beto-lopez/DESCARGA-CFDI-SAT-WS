/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;



import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.10
 * 
 *
 * DMTService has the methods that consume the SAT's cfdis massive download
 * web service.
 * 
 */
public interface DMTService extends java.time.InstantSource {

    /**
     * 
     * @param conn
     * @param creds
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or credentials are null
     */
    public Authorization autentica(SOAPConnection conn, Credentials creds) throws SOAPException;
    
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
    public SolicitaResponse solicita(SOAPConnection conn, Credentials creds, Query query, String token) throws SOAPException;
    
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
    public VerificaResponse verifica(SOAPConnection conn, Credentials creds, String requestId, String token) throws SOAPException;
    
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
    public DescargaResponse descarga(SOAPConnection conn, Credentials creds, String packageId, String token) throws SOAPException;
    
    public DownloadRepository getRepository();
    
    public void setRepository(DownloadRepository repository);
    
}
