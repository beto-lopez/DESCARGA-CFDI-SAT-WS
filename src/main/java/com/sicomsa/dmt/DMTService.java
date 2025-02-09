/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;



import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

/**
 * <code>DMTService</code> includes the methods that consume the SAT's cfdis
 * massive download web service.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.10
 * @since 1.0
 * 
 */
public interface DMTService extends java.time.InstantSource {

    /**
     * Calls the authentication web service and returns an <code>Authorization</code>
     * 
     * @param conn  <code>SOAPConnection</code> to use
     * @param creds <code>Credentials</code> to indentify with
     * @return <code>Authorization</code> received
     * @throws SOAPException if there is a<code>SOAP</code> error
     * @throws IllegalArgumentException if <code>conn</code> or <code>creds</code> are null
     */
    public Authorization autentica(SOAPConnection conn, Credentials creds) throws SOAPException;
    
    /**
     * Calls the download request web service to obtain a <code>SolicitaResponse</code>
     * 
     * @param conn <code>SOAPConnection</code> to use
     * @param creds <code>Credentials</code> to indentify with
     * @param query <code>Query</code> with request selection and filters
     * @param token a wrapped token with authorization
     * @return <code>SolicitaResponse</code> received
     * @throws SOAPException if there is a <code>SOAP</code> error
     * @throws IllegalArgumentException if connection or credentials are null
     */
    public SolicitaResponse solicita(SOAPConnection conn, Credentials creds, Query query, String token) throws SOAPException;
    
    /**
     * Calls the verify request web service in order to verify a particular
     * <code>requestId</code>, (IDSolicitud). Returning SAT's respone as a
     * <code>VerificaResponse</code>
     * 
     * @param conn <code>SOAPConnection</code> to use
     * @param creds <code>Credentials</code> to indentify with
     * @param requestId a request identification previously gotten in a
     *                  <code>SolicitaResponse</code>
     * @param token a wrapped token with authorization
     * @return SAT's response as a <code>VerificaResponse</code>
     * @throws SOAPException if there is a <code>SOAP</code> error
     * @throws IllegalArgumentException if connection, credentials or requestId are null
     */
    public VerificaResponse verifica(SOAPConnection conn, Credentials creds, String requestId, String token) throws SOAPException;
    
    /**
     * Calls the download service in order to download <code>packageId</code>'s 
     * specific package (CFDI or Metadata or other, as it was requested.
     * Returns SAT's response as a <code>DescargaResponse</code>.
     * 
     * @param conn <code>SOAPConnection</code> to use
     * @param creds <code>Credentials</code> to indentify with
     * @param packageId an id provided from SAT earlier in a <code>VerificaResponse</code>
     * @param token a wrapped token with authorization
     * @return SAT's response as a <code>DescargaResponse</code>
     * @throws SOAPException if there is a <code>SOAP</code> error
     * @throws IllegalArgumentException if connection, credentials or packageId are null
     */
    public DescargaResponse descarga(SOAPConnection conn, Credentials creds, String packageId, String token) throws SOAPException;
    
    /**
     * Returns a repository to store downloaded files.
     * 
     * @return <code>DownloadRepository</code> to store files.
     */
    public DownloadRepository getRepository();
    
    /**
     * Sets the <code>DownloadRepository</code> that will be used to store
     * downloaded files in this service.
     * 
     * @param repository <code>DownloadRepository</code> to use
     */
    public void setRepository(DownloadRepository repository);
    
}
