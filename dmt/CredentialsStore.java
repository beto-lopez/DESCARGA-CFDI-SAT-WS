/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
  */

package com.sicomsa.dmt;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.22
 * 
 * Provides credentials of contributor(s).
 *
 */
public interface CredentialsStore {
    /**
     * 
     * @param rfc will be upper casted before the search
     * @return credentials for the rfc provided or null if not found
     * @throws RepositoryException 
     */
    public Credentials getCredentials(String rfc) throws RepositoryException;
}
