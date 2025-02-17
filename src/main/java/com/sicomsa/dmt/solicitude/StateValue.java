/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.solicitude;


/**
 * Values of a <code>Solicitude</code> state.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 18-10-24
 * @since 1.0
 * 
 */
public enum StateValue {
    /**
     * <code>Solicitude</code> has not yet been sent to the web service for acceptance.
     */
    NEW,
    /**
     * <code>Solicitude</code> has been accepted.
     */
    ACCEPTED,
    /**
     * <code>Solicitude</code>'s verification is not ready yet.
     */
    DELAYED,
    /**
     * <code>Solicitude</code> is verified.
     */
    VERIFIED;
    
    /**
     * Returs true if this <code>StateValue</code> is new.
     * 
     * @return true if this <code>StateValue</code> is new
     */
    public boolean isNew() {
        return (this == NEW);
    }
    
    /**
     * Returs true if this <code>StateValue</code> is accepted.
     * 
     * @return true if this <code>StateValue</code> is accepted
     */
    public boolean isAccepted() {
        return (this == ACCEPTED);
    }
    
    /**
     * Returs true if this <code>StateValue</code> is delayed.
     * 
     * @return true if this <code>StateValue</code> is delayed
     */
    public boolean isDelay() {
        return (this == DELAYED);
    }
    
    /**
     * Returs true if this <code>StateValue</code> has been verified.
     * 
     * @return true if this <code>StateValue</code> has been verified
     */
    public boolean isVerified() {
        return (this == VERIFIED);
    }
}
