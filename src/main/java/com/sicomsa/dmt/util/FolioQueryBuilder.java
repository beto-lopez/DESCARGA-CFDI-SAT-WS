/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.QueryType;

/**
 * Builds queries to use when requesting a download by id (folio).
 * <p>You can reuse this builder, but from the same thread. The properties
 * you set will remain in the builder for the next build, unless you set other
 * property values.</p>
 * <p>Example:</p>
 * <pre>
 * {@code
 * FolioQueryBuilder builder = new FolioQueryBuilder();
 * ExtendedQuery firstQuery =
 *      builder.setRfcSolicitante("XAXX010101000").setFolio("abc-def-ghi").build();
 * ExtendedQuery secondQuery = builder.setFolio("hic-uvw-xyz").build();
 *  //secondQuery will have the same rfcSolicitante as firstQuery.
 * }
 * </pre>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.9
 * @since       1.5 
 * 
 */
public class FolioQueryBuilder {

    /**
     * Builder to build queries with.
     */
    protected QueryMap.Builder builder;
        
    /**
     * Creates a new <code>FolioQueryBuilder</code>
     */
    public FolioQueryBuilder() {
        builder = new QueryMap.Builder();
    }
        
    /**
     * Sets the value of the "Folio" attribute with the specified value.
     * 
     * @param folio the folio to set
     * @return this builder
     * @see com.sicomsa.dmt.Query#getFolio() 
    */
    public FolioQueryBuilder setFolio(String folio) {
        builder.setFolio(folio);
        return this;
    }
          
    /**
     * Sets the value of the "RfcSolicitante" attribute with
     * the specified value.
     * 
     * @param rfc the RFC of the requestor to set
     * @return this builder
     */
    public FolioQueryBuilder setRfcSolicitante(String rfc) {
        builder.setRfcSolicitante(rfc);
        return this;
    }
        
    /**
     * Returns a new <code>ExtendedQuery</code> with the parameters set in this builder.
     * 
     * @return a new <code>ExtendedQuery</code>with the parameters set in this builder.
    */
    public ExtendedQuery build() {
        return new ExtendedQueryMap(builder, QueryType.FOLIO);
    }
    
}
