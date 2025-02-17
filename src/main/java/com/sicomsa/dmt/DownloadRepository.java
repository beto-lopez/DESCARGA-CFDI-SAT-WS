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
     * Saves the <code>encodedPackage</code>.
     * 
     * @param rfc RFC of the contributor that requested the download
     * @param packageId Id of the package downloaded
     * @param encodedPackage the package encoded as received from SAT WS.
     * @param params alternative parameters
     * @throws RepositoryException if there is a repository problem
     * @see DescargaResponse#getEncodedPackage()
     */
    public void save(String rfc, String packageId, String encodedPackage, Object params);

}
