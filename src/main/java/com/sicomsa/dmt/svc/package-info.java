/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
/**
 * This package contains classes that implement SAT's massive download web service.
 * For CFDIs downloading.
 * <p>
 * Class {@link DownloadService} implements {@link com.sicomsa.dmt.DMTService}
 * providing concrete implementations for the following methods that consume
 * the web service:</p>
 * <ul>
 * <li>Method {@link DownloadService#autentica(jakarta.xml.soap.SOAPConnection, com.sicomsa.dmt.Credentials) }
 * calls the authentication service in order to get an {@link com.sicomsa.dmt.Authorization authorization}
 * with a token to be used in other services.
 * </li>
 * <li>Method {@link DownloadService#solicita(jakarta.xml.soap.SOAPConnection, com.sicomsa.dmt.Credentials, com.sicomsa.dmt.Query, java.lang.String)}
 * that calls the request download service, providing a request Id that will be used
 * in the verification process of the download request.</li>
 * <li>Method {@link DownloadService#verifica(jakarta.xml.soap.SOAPConnection, com.sicomsa.dmt.Credentials, java.lang.String, java.lang.String)}
 * will call the verify request service in order to verift if the request was
 * approved, is pending or if done, provide the package Id's needed to download.
 * </li>
 * <li>With method {@link DownloadService#descarga(jakarta.xml.soap.SOAPConnection, com.sicomsa.dmt.Credentials, java.lang.String, java.lang.String) }
 * user will be able to download a package using a package Id provided by the
 * verification.
 * </li>
 * </ul>
 * 
 *
 * 
 * 
 */
package com.sicomsa.dmt.svc;
