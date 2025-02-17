/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;


/**
 * Defines an object which listens for <code>DownloadEvent</code>s.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.12.25
 * @since 1.0
 * 
 *
 */
public interface DownloadListener extends java.util.EventListener {
    
    /**
     * Invoked when the target of the listener has changed its state
     * 
     * @param evt the event
     */
    public void stateChanged(DownloadEvent evt);
}
