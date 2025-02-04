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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.09
 * 
 * Methods that class DownloadState requires to make requests to SAT and process
 * the responses received.
 *
 */
public interface DownloadContext {
    
    public DMTClient getClient();
    public Query getQuery();
    public String getRequestId();
    
    public void update(SolicitaResponse response);
    public void update(VerificaResponse response);
    public void update(DescargaResponse response);
    
    public String getNextDownloadablePackageId();
}
