/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.QueryType;
import java.util.TreeSet;

/**
 * Subclass of {@link QueryMap} to implement {@link com.sicomsa.dmt.ExtendedQuery},
 * adding the attribute "RfcReceptor" into the query, and provides the type
 * of query used.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.9
 * @since       1.5 
 */
public class ExtendedQueryMap extends QueryMap implements ExtendedQuery {

    private static final long serialVersionUID = 20250809L;
    
    /**
     * Value = "RfcReceptor"
     */
    public static final String RFC_RECEPTOR = "RfcReceptor";
        
    /**
     * <code>QueryType</code> of this query
     */
    protected QueryType type;

    /**
     * Creates an <code>ExtendedQueryMap</code> from the specified builder.
     * It will make its own map and receptor's set in order to be inmutable.
     * 
     * @param builder the builder that contains the query information
     * @param type the type of query of this map.
     * @throws IllegalArgumentException if builder or type are null
     */
    public ExtendedQueryMap(Builder builder, QueryType type) {
        super(builder);
        if (type == null) {
            throw new IllegalArgumentException("invalid type");
        }
        this.type = type;
    }
        
    /**
     * Returns the type of query this {@link com.sicomsa.dmt.Query} represents
     * 
     * @return type of query this {@link com.sicomsa.dmt.Query} represents
     */
    @Override public QueryType getType() {
        
        return type;
    }
        
    /**
     * Returns the rfc of the cdfdi receptor.
     * 
     * @return rfc of the cdfdi receptor
     */
    @Override public String getRfcReceptor() {
        return (String)map.get(RFC_RECEPTOR);
    }
    
    /**
     * Sets the value of the <code>"RfcReceptor"</code> attribute with
     * the specified value.
     * <p><code>RfcReceptor</code> should only be set when requesting
     * <b>received</b> certificates.</p>
     * 
     * @param rfc the RFC of the requestor to set.
     */
    public void setRfcReceptor(String rfc) {
        map.put(RFC_RECEPTOR, (rfc == null ? null : rfc.toUpperCase()));
    }
        
    /**
     * Adds all rfc receptors specified in the collection to the map.
     * <p><code>RfcRecpetors</code> should only be set when requesting
     * <b>emitted</b> certificates.</p>
     * 
     * @param collection collection of rfc receptors to add to the map
     * @throws NullPointerException if the collection is null or if any items
     *         in the collection is null.
     */
    public void addAllRfcReceptors(java.util.Collection<String> collection) {
        if (receptorsSet == null) {
            receptorsSet = new TreeSet<>();
        }
        collection.forEach(rfc-> {
            receptorsSet.add(rfc.toUpperCase());
        });
    }
    
    /**
     * Removes all rfc receptors from this map.
     */
    public void clearRfcReceptors() {
        if (receptorsSet != null) {
            receptorsSet.clear();
        }
    }
}
