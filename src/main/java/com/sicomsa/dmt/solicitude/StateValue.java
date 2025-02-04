/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.solicitude;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 18-10-24
 * 
 * States of a download request.
 */
public enum StateValue {
    NEW,
    ACCEPTED,
    DELAYED,
    VERIFIED;
    
    public boolean isNew() {
        return (this == NEW);
    }
    public boolean isAccepted() {
        return (this == ACCEPTED);
    }
    public boolean isDelay() {
        return (this == DELAYED);
    }
    public boolean isVerified() {
        return (this == VERIFIED);
    }
}
