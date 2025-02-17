/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;

/**
 * <code>DownloadRegistry</code> contains methods to view the download statuses
 * of the package identifiers to be downloaded to complete the download request.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.18
 * @since 1.0
 *
 */
public interface DownloadRegistry {

    /**
     * Returns the package identifiers to download. Could be null if no identifiers
     * have been provided from the WS yet.
     * 
     * @return the package identifiers to download. Could be null if no identifiers
     *         have been provided from the WS yet
     */
    public PackageIds getPackageIds();
    
    /**
     * Returns true if the package with the identifier at index has been downloaded
     * 
     * @param index the specified index
     * @return true if the package with the identifier at index has been downloaded
     */
    public boolean isDownloaded(int index);
    
    /**
     * Returns true if the registry contains package identifiers and all have 
     * been downloaded.
     * 
     * @return true if the registry contains package identifiers and all have
     *         been downloaded
     */
    public boolean isDownloadDone();
    
    /**
     * Returns a package identifier that has not been downloaded yet or null if
     * all identifiers have been downloaded or there isn't any one.
     * 
     * @return a package identifier that has not been downloaded yet or null if
     *         all identifiers have been downloaded or there isn't any one
     */
    public String getNextDownloadablePackageId();
}
