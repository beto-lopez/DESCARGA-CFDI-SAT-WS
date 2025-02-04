/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.Query;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.12.27
 * 
 * BatchRequest is used to group data of a download request. That is, its id
 * within the batch, the RFC of the download requester and the query that
 * contains the requester's download selection.
 * 
 *  
 */
public class BatchRequest {
    
    protected long id;
    protected String rfc;
    protected Query query;
    
    public BatchRequest(long id, String rfc, Query query) {
        if (rfc == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.id = id;
        this.rfc = rfc.toUpperCase();
        this.query = query;
    }
    
    public long getId() {
        return id;
    }
    
    public String getRfc() {
        return rfc;
    }
    
    public Query getQuery() {
        return query;
    }
    
    @Override public String toString() {
        return new StringBuilder("BatchRequest{")
                .append("id=").append(id)
                .append(",rfc=").append(rfc)
                .append(",query=").append(query)
                .append("}").toString();
    }

}
