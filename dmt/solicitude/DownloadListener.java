/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.12.25
 * 
 * Listener of DownloadEvents
 *
 */
public interface DownloadListener extends java.util.EventListener {
    public void stateChanged(DownloadEvent evt);
}
