/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.18
 *
 * Methods to visualize download status of package id's to download.
 */
public interface DownloadRegistry {

    /**
     * 
     * @return the package id's to download. Could be null if info is not
     *          yet provided by SAT.
     */
    public PackageIds getPackageIds();
    public boolean isDownloaded(int index);
    public boolean isDownloadDone();
    public String getNextDownloadablePackageId();
}
