/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

//import java.lang.ProcessBuilder;

/**
 * Repository that stores downloaded packages
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.12
 * @since 1.0
 *
 */
public interface DownloadRepository {
    
    /**
     * Saves the <code>encodedPackage</code>. This package is encoded as it
     * was received from SAT in a <code>SOAPMessage</code>.<p>
     * To get the package's byte[] you can use a Base64.decoder:
     *   byte[] decoded = java.util.Base64.getDecoder().decode(encodedPackage);
     * 
     * @param rfc RFC of the contributor that requested the download
     * @param packageId Id of the package downloaded
     * @param encodedPackage the package encoded as received from SAT WS.
     * @param params alternate parameters
     * @throws RepositoryException if there is a repository problem
     * @see com.sicomsa.dmt.svc.LocalRepository#decode(String)
     */
    public void save(String rfc, String packageId, String encodedPackage, Object params);

}
