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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.13
 * 
 * BatchSolicitude extends DefaultSolicitude to include an Id needed to bind
 * each requests with its responses. And to be able to mutate the solicitude
 * with the saved responses.
 * 
 */
public class BatchSolicitude extends DefaultSolicitude {
    
    protected long batchId;

    public BatchSolicitude(DMTClient client, Query query, long batchId) {
        super(client, query);
        this.batchId = batchId;
    }
    
    public long getBatchId() {
        return batchId;
    }
    
    public void batchUpdate(SolicitaResponse response) {
        context.update(response);
    }
    
    public void batchUpdate(VerificaResponse response) {
        context.update(response);
    }
    
    public void batchUpdate(DescargaResponse response) {
        context.update(response);
    }
    
}
