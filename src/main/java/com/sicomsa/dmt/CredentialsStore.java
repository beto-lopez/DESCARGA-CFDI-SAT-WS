/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
  */

package com.sicomsa.dmt;

/**
 * A storage for <code>Credentials</code>. Maps the <code>Credentials</code> to
 * contributor(s) RFCs.
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.22
 * @since 1.0
 * 
 *
 */
public interface CredentialsStore {
    /**
     * Returns <code>Credentials</code> mapped to the RFC received.
     * 
     * @param rfc RFC of the contributor, will be upper casted before the search
     * @return credentials Credentials of the contributor with the given RFC
     * or null if not found
     * @throws RepositoryException if there were repository problems
     */
    public Credentials getCredentials(String rfc);
}
