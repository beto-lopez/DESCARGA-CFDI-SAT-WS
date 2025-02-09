/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.WebServiceException;
import java.security.cert.X509Certificate;


/**
 * <code>DMTClient</code> contains methods needed for a contributor to download
 * cfdis, and store them, at particular stages of the download process.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2020-10-12
 * @since   1.0
 * 
 */
public interface DMTClient {
    
    /**
     * Returns the RFC of this client.
     * 
     * @return the RFC of this client
     */
    public String getRfc();

    /**
     * Calls SAT's request download service from SAT with the provided <code>connection</code>
     * and <code>query</code>, returning a decoded response.
     * 
     * @param connection the soap connection to use
     * @param query the request with its filters to send
     * @return SAT's response encapsulated in a <code>SolicitaResponse</code>
     * @throws SOAPException if there was a SOAP related problem
     * @throws IllegalArgumentException if connection or query are null
     * @throws WebServiceException if there were other service related problems
     * @see SolicitaResponse
     */
    public SolicitaResponse requestDownload(SOAPConnection connection, Query query) throws SOAPException;
    
    /**
     * Calls SAT's verify request service with the given <code>connection</code>
     * on the given <code>requestId</code>.
     * 
     * @param connection the connection to use to connect
     * @param requestId the requestId "IdSolicitud" to be verified
     * @return SAT's response to the verify request encapsulated in a <code>VerificaResponse</code>
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or requestId are null
     * @throws WebServiceException if there were other service related problems
     * @see VerificaResponse
     */
    public VerificaResponse verifyRequest(SOAPConnection connection, String requestId) throws SOAPException;
    
    /**
     * Calls SAT's download service for the given <code>packageId</code> using
     * the <code>connection</code> received
     * 
     * @param connection the connection to be used
     * @param packageId the packageId to download
     * @return SAT's reponse encapsulated in a <code>DescargaResponse</code>
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or packageId are null
     * @throws WebServiceException if there were other service related problems
     * @see DescargaResponse
     */
    public DescargaResponse download(SOAPConnection connection, String packageId) throws SOAPException;
    
    /**
     * Saves the <code>encodedPackage</code> in an user defined repository.
     * 
     * @param packageId the packageId of the package to be saved
     * @param encodedPackage the package to be saved
     * @throws RepositoryException if there was a problem while saving the package
     */
    public void save(String packageId, String encodedPackage);
    
    /**
     * Returns the <code>X509Certificate</code> of this client.
     * This method will also load the certificate if under lazy load.
     * 
     * @return the X509Certificate of this client.
     * @throws WebServiceException if there were other service related problems
     */
    public X509Certificate getCertificate();

}
