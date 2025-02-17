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
 * Represents a download request to the massive download web service, with its
 * status and information that is collected as the steps are invoked to download
 * the digital certificates.
 * <p>A <code>Solicitude</code> initially contains the client making the request
 * and the query options and filters. As it executes the web services, it gathers
 * information. For example, the query identifier and the instant it was accepted
 * through the "request download service". Later it gathers the download information
 * through the "verify request service", and so on.</p>
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.09
 * @since 1.0
 * 
 * 
 * 
 */
public interface Solicitude extends DownloadRegistry {

    /**
     * Returns the <code>DMTClient</code> of this download request.
     * 
     * @return the <code>DMTClient</code> of this download request
     */
    public DMTClient getClient();
    
    /**
     * Returns the <code>Query</code> of this download request.
     * 
     * @return the <code>Query</code> of this download request
     */
    public Query getQuery();
    
    /**
     * Returns the <code>StateValue</code> of this download request.
     * 
     * @return the <code>StateValue</code> of this download request
     */
    public StateValue getValue();
    
    /**
     * Returns the request identifier of this download request; null if this
     * request is not yet accepted.
     * 
     * @return the request identifier of this download request; null if this
     *         request is not yet accepted
     */
    public String getRequestId();
    
    /**
     * Returns true if this download request is not yet ready to be verified.
     * 
     * @return true if this download request is not yet ready to be verified
     */
    public boolean isDelay();
    
    /**
     * Returns the value state of the Delay or null if this request is not Delayed.
     * 
     * @return the value state of the Delay or null if this request is not Delayed
     */
    public Delay getDelay();
    
    /**
     * Returns the last <code>Instant</code> this request was accepted by the WS.
     * 
     * @return the last <code>Instant</code> this request was accepted by the WS
     */
    public Instant getLastAccepted();
    
    /**
     * Returns the ammount of CFDIs this request has; or zero if the
     * verification has not finished.
     * 
     * @return the ammount of CFDIs this request has; or zero if the
     *         verification has not finished
     */
    public int getCfdis();
    
    /**
     * Returns a new <code>SolicitudeData</code> with information of this
     * <code>Solicitude</code> in its current state.
     * 
     * @return a new <code>SolicitudeData</code> with information of this
     *         <code>Solicitude</code> in its current state
     */
    public SolicitudeData generateSolicitudeData();
    
    /**
     * Restores this <code>Solicitude</code> to the state and with the information
     * of the specified data.
     * 
     * @param data the state and data to restore to
     */
    public void restore(SolicitudeData data);
    
    /**
     * Pauses this <code>Solicitude</code>.
     * <p>To make a download, you need to follow certain steps: request, verify,
     * download one package, then the next one, etc. Pausing a request means not
     * continuing with the next process until a download is requested again.</p>
     * 
     * @see DefaultSolicitude#download(jakarta.xml.soap.SOAPConnection) 
     */
    public void pause();
    
    /**
     * Returns true if this <code>Solicitude</code> is paused.
     * 
     * @return true if this <code>Solicitude</code> is paused
     */
    public boolean isPaused();
    
    /**
     * Returns true if this download request is pending.
     * <p>A download request is pending if it has not been rejected
     * and has not yet downloaded all the package identifiers defined by the
     * download request verification</p>
     * 
     * @return true if this download request is pending
     */
    public boolean isPending();
    
    /**
     * Returns true if this <code>Solicitude</code> has not been accepted by the
     * web service.
     * 
     * @return true if this <code>Solicitude</code> has not been accepted by the
     *         web service
     */
    public boolean isReject();
    
    /**
     * Returns the <code>SatResponse</code> indicating that the request was not
     * accepted by the web service and therefore caused this <code>Solicitude</code>
     * to be rejected.
     * <p>This response can be casted to {@link com.sicomsa.dmt.SolicitaResponse},
     * {@link com.sicomsa.dmt.VerificaResponse}, or {@link com.sicomsa.dmt.DescargaResponse}
     * depending on which service generated the response.
     * </p>
     * 
     * @return the <code>SatResponse</code> indicating that the request was not
     *         accepted by the web service and therefore caused this <code>Solicitude</code>
     *         to be rejected.
     */
    public SatResponse getReject();
    
    /**
     * Adds the specified <code>DownloadListener</code> to this <code>Solicitude</code>.
     * 
     * @param listener the listener to add
     */
    public void addDownloadListener(DownloadListener listener);
    
    /**
     * Removes the specified <code>DownloadListener</code> from this <code>Solicitude</code>.
     * 
     * @param listener the listener to remove
     */
    public void removeDownloadListener(DownloadListener listener);

}
