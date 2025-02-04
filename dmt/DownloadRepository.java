/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.12
 *
 * Repository that stores downloaded packages
 */
public interface DownloadRepository {
    
    public void save(String rfc, String packageId, String encodedPackage, Object params) throws RepositoryException;

}
