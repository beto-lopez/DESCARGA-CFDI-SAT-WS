/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.solicitude.DefaultSolicitude;

/**
 * <code>BatchSolicitude</code> extends {@link com.sicomsa.dmt.solicitude.DefaultSolicitude}
 * to implement a {@link com.sicomsa.dmt.solicitude.Solicitude} that can be
 * downloaded; has an additional identifier, and adds new public methods that
 * update this solicitude with responses received from the web service.
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.13
 * @since 1.0
 * 
 */
public class BatchSolicitude extends DefaultSolicitude {
    
    /**
     * The identifier of this solicitude.
     */
    protected long batchId;

    /**
     * Creates a new <code>BatchSolicitude</code> with the specified parameters.
     * 
     * @param client client making this request
     * @param query query to be used in this request
     * @param batchId identifier of this request within a <code>Batch</code>
     * @throws IllegalArgumentException if client or query are null
     */
    public BatchSolicitude(DMTClient client, Query query, long batchId) {
        super(client, query);
        this.batchId = batchId;
    }
    
    /**
     * Returns the id of this solicitude.
     * 
     * @return the id of this solicitude
     */
    public long getBatchId() {
        return batchId;
    }
    
    /**
     * Updates this solicitude with the specified download request response.
     * 
     * @param response the download request response received from the web service
     * @throws IllegalStateException if this solicitude is not new
     * @throws NullPointerException if response is null
     */
    public void batchUpdate(SolicitaResponse response) {
        context.update(response);
    }
    
    /**
     * Updates this solicitude with the specified verify request response.
     * 
     * @param response the verify request response received from the web service
     * @throws IllegalStateException if this solicitude is not accepted or delayed
     * @throws NullPointerException if response is null
     */
    public void batchUpdate(VerificaResponse response) {
        context.update(response);
    }
    
    /**
     * Updates this solicitude with the specified download package response.
     * 
     * @param response the download package response received from the web service
     * @throws IllegalStateException if this solicitude is not verified
     * @throws NullPointerException if response is null
     */
    public void batchUpdate(DescargaResponse response) {
        context.update(response);
    }
    
}
