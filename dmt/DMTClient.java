/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import java.security.cert.X509Certificate;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 12-10-24
 * 
 * Contains methods needed for a contributor to download cfdis, and store them,
 * at particular stages of the download process.
 * 
 */
public interface DMTClient {
    
    public String getRfc();

    /**
     * 
     * @param connection
     * @param query
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or query are null
     */
    public SolicitaResponse requestDownload(SOAPConnection connection, Query query) throws SOAPException;
    /**
     * 
     * @param connection
     * @param requestId
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or requestId are null
     */
    public VerificaResponse verifyRequest(SOAPConnection connection, String requestId) throws SOAPException;
    /**
     * 
     * @param connection
     * @param packageId
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if connection or packageId are null
     */
    public DescargaResponse download(SOAPConnection connection, String packageId) throws SOAPException;
    
    public void save(String packageId, String encodedPackage) throws RepositoryException;
    
    /**
     * this will also load the certificate if under lazy load
     * @return 
     */
    public X509Certificate getCertificate();

}
