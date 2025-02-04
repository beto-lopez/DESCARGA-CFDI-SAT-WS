/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.SatResponse;
import java.time.Instant;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.09
 * 
 * Represents a download request to the SAT's masive download web service
 *
 */
public interface Solicitude extends DownloadRegistry {

    public DMTClient getClient();
    
    public Query getQuery();
    
    public StateValue getValue();
    
    public String getRequestId();
    
    public boolean isDelay();
    
    public Delay getDelay();
    
    public Instant getLastAccepted();
    
    public int getCfdis();
    
    public SolicitudeData generateSolicitudeData();
    
    public void restore(SolicitudeData data);
    
    public void pause();
    
    public boolean isPaused();
    
    public boolean isPending();
    
    public boolean isReject();
    
    public SatResponse getReject();
    
    public void addDownloadListener(DownloadListener listener);
    
    public void removeDownloadListener(DownloadListener listener);

}
