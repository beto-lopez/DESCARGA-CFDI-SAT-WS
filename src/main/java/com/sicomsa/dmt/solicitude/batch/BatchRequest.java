/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.Query;

/**
 * <code>BatchRequest</code> is used to identify and locate a specific request
 * and its data, within a <code>Batch</code> that holds several requests.
 * <p>Each request has a unique identifier within a <code>Batch</code>.</p>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.12.27
 * @since 1.0
 *   
 */
public class BatchRequest {
   
    /**
     * Request identifier within this Batch
     */
    protected long id;
    
    /**
     * RFC of the contributor making this request
     */
    protected String rfc;
    
    /**
     * Query of this download request
     */
    protected Query query;
    
    /**
     * Constructs a new <code>BatchRequest</code> with the specified parameters.
     * 
     * @param id identifier of this request within a Batch
     * @param rfc RFC of contributor making this request
     * @param query query of this request
     * @throws IllegalArgumentException if rfc or query are null
     */
    public BatchRequest(long id, String rfc, Query query) {
        if (rfc == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.id = id;
        this.rfc = rfc.toUpperCase();
        this.query = query;
    }
    
    /**
     * Returns the id of this request within the Batch.
     * 
     * @return the id of this request within the Batch
     */
    public long getId() {
        return id;
    }
    
    /**
     * Returns the RFC of the contributor making this request.
     * 
     * @return the RFC of the contributor making this request
     */
    public String getRfc() {
        return rfc;
    }
    
    /**
     * Returns the <code>Query</code> selection of this request.
     * 
     * @return the <code>Query</code> selection of this request
     */
    public Query getQuery() {
        return query;
    }
    
    /**
     * Returns a string representation of this request.
     * 
     * @return a string representation of this request
     */
    @Override public String toString() {
        return new StringBuilder("BatchRequest{")
                .append("id=").append(id)
                .append(",rfc=").append(rfc)
                .append(",query=").append(query)
                .append("}").toString();
    }

}
