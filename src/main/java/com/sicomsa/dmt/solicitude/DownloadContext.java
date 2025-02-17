/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;


import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.DescargaResponse;

import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.DMTClient;


/**
 * Methods that class DownloadState requires to make requests to SAT and process
 * the responses received.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.09
 * @since 1.0
 * 
 *
 */
public interface DownloadContext {
    
    /**
     * Returns the <code>DMTClient</code> this context uses.
     * 
     * @return the <code>DMTClient</code> this context uses
     */
    public DMTClient getClient();
    
    /**
     * Returns the <code>Query</code> of this context.
     * 
     * @return the <code>Query</code> of this context
     */
    public Query getQuery();
    
    /**
     * Returns the <code>requestId</code> of this context.
     * 
     * @return the <code>requestId</code> of this context
     */
    public String getRequestId();
    
    /**
     * Updates this context with the specified <code>SolicitaResponse</code>
     * 
     * @param response the <code>SolicitaResponse</code> to update context with
     */
    public void update(SolicitaResponse response);
    
    /**
     * Updates this context with the specified <code>VerificaResponse</code>
     * 
     * @param response the <code>VerificaResponse</code> to update context with
     */
    public void update(VerificaResponse response);
    
    /**
     * Updates this context with the specified <code>DescargaResponse</code>
     * 
     * @param response the <code>DescargaResponse</code> to update context with
     */
    public void update(DescargaResponse response);
    
    /**
     * Returns a package identifier that has not been downloaded yet or null if
     * all identifiers have been downloaded or there isn't any one.
     * 
     * @return a package identifier that has not been downloaded yet or null if
     *         all identifiers have been downloaded or there isn't any one
     */
    public String getNextDownloadablePackageId();
}
