/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


/**
 * Represents a query to be submitted to the web service within the request
 * download service; it contains methods needed to implement the web service
 * version 1.5, releases May 30, 2025 by SAT.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.2
 * @since       1.5
 */
public interface ExtendedQuery extends Query {
    
    /**
     * Returns the rfc of the cdfdi receptor.
     * 
     * @return rfc of the cdfdi receptor
     */
    public String getRfcReceptor();
    
    /**
     * Returns the type of query this {@link com.sicomsa.dmt.Query} represents
     * 
     * @return type of query this {@link com.sicomsa.dmt.Query} represents
     */
    public QueryType getType();

}
