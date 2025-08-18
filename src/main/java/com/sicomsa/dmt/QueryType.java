/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

/**
 * Types of queries available to request a download from the web service.
 * Emmited cfdis, Received cfdis, and cfdis by id.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.1
 * @since       1.5 
 */
public enum QueryType {
    /**
     * Emmited cfdis
     */
    EMITIDOS,
    /**
     * Received cfdis
     */
    RECIBIDOS,
    /**
     * cfdi by id
     */
    FOLIO
}
