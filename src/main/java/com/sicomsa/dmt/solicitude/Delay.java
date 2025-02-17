/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

/**
 * Values for the state of the Delay.
 * <p>A <code>Delay</code> means that the request application was accepted but its
 * verification is not yet ready.</p>
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.06
 * @since 1.0
 * 
 */
public enum Delay {
    /**
     * The request was accepted, its verification is pending
     */
    ACCEPTED,
    
    /**
     * The request was accepted, its verification is in progress
     */
    IN_PROGRESS,
    
    /**
     * The request was accepted, its verification is not yet ready for other reasons.
     */
    OTHER;
}
