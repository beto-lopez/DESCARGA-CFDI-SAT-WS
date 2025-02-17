/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;



import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.SatResponse;
import com.sicomsa.dmt.PackageIds;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.WebServiceException;

import java.time.Instant;
import java.lang.System.Logger.Level;
import javax.swing.event.EventListenerList;

/**
 * DownloadRequest (Solicitude) implementation.
 * 
 * Mention pause, when and how...
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.23
 * @since 1.0
 * 
 * 
 */
public class DefaultSolicitude implements Solicitude {

    /**
     * <code>DownloadState</code> of a new solicitude.
     */
    public static final DownloadState NEW_STATE = new DownloadState.New();
    
    
    /**
     * <code>DownloadState</code> of an accepted solicitude.
     */
    public static final DownloadState ACCEPTED_STATE = new DownloadState.Accepted();
    
    /**
     * <code>DownloadState</code> of a delayed solicitude.
     */
    public static final DownloadState DELAYED_STATE = new DownloadState.Delayed();
    
    /**
     * <code>DownloadState</code> of a verified solicitude.
     */
    public static final DownloadState VERIFIED_STATE = new DownloadState.Verified();
    
    /**
     * Empty download registry constant
     */
    public static final MutableRegistry UNVERIFIED_REGISTRY = new EmptyRegistry();
    
    /**
     * list of listeners
     */
    protected EventListenerList listenerList;
    
  
    private boolean _paused = false;
    
    /**
     * Client of this solicitude
     */
    protected DMTClient client;
    
    /**
     * Query of this solicitude
     */
    protected Query query;
    
    /**
     * Web service's response if response was not accepted
     */
    protected SatResponse reject;
    
    /**
     * Download state of this solicitude
     */
    protected DownloadState state;
    
    /**
     * Request identifier of this solicitude
     */
    protected String requestId;
    
    /**
     * Instant when this solicitude was las accepted
     */
    protected Instant lastAccepted;
    
    /**
     * Value of Delay if this solicitude is delayed
     */
    protected Delay delay;
    
    /**
     * Ammount of digital certificates this solicitude considers
     */
    protected int cfdis;
    
    /**
     * Context of this solicitude
     */
    protected Context context;
    
    /**
     * Mutable download registry property
     */
    protected MutableRegistry registry;

    private static final System.Logger LOG = System.getLogger(DefaultSolicitude.class.getName());

    /**
     * Constructs a new <code>DefaultSolicitude</code> with the specified parameters.
     * 
     * @param client client making this download request
     * @param query  query of this download request
     * @throws IllegalArgumentException if client or query are null
     */
    public DefaultSolicitude(DMTClient client, Query query) {
        if (client == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.client = client;
        this.query = query;
        this.state = NEW_STATE;
        this.registry = UNVERIFIED_REGISTRY;
        this.context = new Context();
        this.listenerList = new EventListenerList();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ////Solicitude implementation
    ///////////////////////////////////////////////////////////////////////////
    
    @Override public DMTClient getClient() {
        return client;
    }
    
    @Override public Query getQuery() {
        return query;
    }
    
    @Override public StateValue getValue() {
        return state.getValue();
    }
    
    @Override public String getRequestId(){
        return requestId;
    }
    
    @Override public boolean isDelay() {
        return (getDelay() != null);
    }
    
    @Override public Delay getDelay() {
        return delay;
    }
    
    @Override public Instant getLastAccepted() {
        return lastAccepted;
    }
    
    @Override public int getCfdis() {
        return cfdis;
    }

    @Override public SolicitudeData generateSolicitudeData() {
        return 
            switch (getValue()) {
                case NEW-> null;
                case ACCEPTED->
                    new SolicitudeData(requestId, lastAccepted);
                case DELAYED->
                    new SolicitudeData.Delayed(requestId, delay, lastAccepted);
                case VERIFIED->
                    new SolicitudeData.Verified(requestId, lastAccepted, cfdis,
                            registry);
            };
    }

    @Override public void restore(SolicitudeData data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null in order to restore");
        }
        setRequestId(data.getRequestId());
        setLastAccepted(data.getLastAccepted());
        setDelay(data.getDelay());
        setCfdis(data.getCfdis());
        setVerified(data);
        setState(getState(data.getStateValue()));
    }
    
    /**
     * Pauses this <code>Solicitude</code> making it unable to continue its
     * download processes until a new call to the {@link DefaultSolicitude#download(jakarta.xml.soap.SOAPConnection) }
     * is made.
     */
    @Override public void pause() {
        setPaused(true);
    }
    
    @Override public synchronized boolean isPaused() {
        return _paused;
    }
    
    @Override public boolean isPending() {
        return (!isReject() && !isDownloadDone());
    }
    
    @Override public boolean isReject() {
        return (reject != null);
    }
    
    @Override public SatResponse getReject() {
        return reject;
    }
        
    @Override public void addDownloadListener(DownloadListener listener) {
        listenerList.add(DownloadListener.class, listener);
    }
    
    @Override public void removeDownloadListener(DownloadListener listener) {
        listenerList.remove(DownloadListener.class, listener);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    ///DownloadRegistry implementation
    //////////////////////////////////////////////////////////////////////////
    
    @Override public PackageIds getPackageIds() {
        return registry.getPackageIds();
    }
    
    @Override public boolean isDownloaded(int index) {
        return registry.isDownloaded(index);
    }
    
    @Override public boolean isDownloadDone() {
        return registry.isDownloadDone();
    }
    
    @Override public String getNextDownloadablePackageId() {
        return registry.getNextDownloadablePackageId();
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a string representation of this solicitude.
     * 
     * @return  a string representation of this solicitude
     */
    @Override public String toString() {
        return new StringBuilder("DefaultSolicitude{")
                .append("client=").append(getClient())
                .append(",query=").append(getQuery())
                .append(",value=").append(getValue())
                .append(",requestId=").append(getRequestId())
                .append(",isDelay=").append(isDelay())
                .append(",Delay=").append(getDelay())
                .append(",lastAccepted=").append(getLastAccepted())
                .append(",cfdis=").append(getCfdis())
                .append(",packageIds=").append(getPackageIds())
                .append(",isDownloadDone=").append(isDownloadDone())
                .append(",isPaused=").append(isPaused())
                .append(",isReject=").append(isReject())
                .append(",Reject=").append(getReject())
                .append("}").toString();
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Depending of the state of this <code>Solicitude</code> this method will
     * call the request download service; then the verify request service; and
     * all the download package requests needed in order to do the downloads
     * of this <code>Solicitude</code>.
     * <p>Since a <code>Solicitude</code> has a state and updates it as it
     * performs service requests, this method can use that state to continue
     * the download process were it left.</p>
     * 
     * <p>Note: If this <code>Solicitude</code> is paused, this method unpauses it.</p>
     * @param conn the connection to use
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if conn is null
     * @throws WebServiceException if there were other service related problems
     */
    public void download(SOAPConnection conn) throws SOAPException {
        setPaused(false);
        if (isRequestable()) {
            state.requestDownload(conn, context);
        }
        if (isVerifiable()) {
            state.verifyRequest(conn, context);
        }
        while (isDownloadable()) {
            state.downloadOnlyOne(conn, context);
        }
    }
    
    ///////////////////////////
    
    /**
     * Returns true if this solicitude's state is requestable and this solicitude
     * is not paused and/or rejected.
     * 
     * @return true if this solicitude's state is requestable and this solicitude
     *         is not paused and/or rejected
     */
    protected boolean isRequestable() {
        return (isAble() && state.isRequestable());
    }
    
    /**
     * Returns true if this solicitude's state is verifiable and this solicitude
     * is not paused and/or rejected.
     * 
     * @return true if this solicitude's state is requestable and this solicitude
     *         is not paused and/or rejected
     */
    protected boolean isVerifiable() {
        return (isAble() && state.isVerifiable());
    }
    
    /**
     * Returns true if this solicitude's state is downloadable and this solicitude
     * is not paused and/or rejected.
     * 
     * @return true if this solicitude's state is requestable and this solicitude
     *         is not paused and/or rejected
     */
    protected boolean isDownloadable() {
        return (isAble() && state.isDownloadable() && !isDownloadDone());
    }

    /**
     * Sets paused property to the specified value.
     * 
     * @param paused true if paused, false if not paused
     */
    protected synchronized void setPaused(boolean paused) {
        this._paused = paused;
    }
    
    /**
     * Returs true if this solicitude is not paused and not rejected
     * 
     * @return true if this solicitude is not paused and not rejected
     */
    protected boolean isAble() {
        return (!isPaused() && !isReject());
    }
    
    
    ////////////////////////////////////////////////////////////////////////////   
  
    /**
     * Sets the request identifier of this solicitude to the specified value.
     * 
     * @param requestId the request identifier to set
     */
    protected void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * Sets the instant when this solicitude was last accepted by the WS
     * 
     * @param instant instant when this solicitude was las accepted
     */
    protected void setLastAccepted(Instant instant) {
        this.lastAccepted = instant;
    }
    
    /**
     * Sets the value of the delay of this solicitude.
     * 
     * @param delay the delay value
     */
    protected void setDelay(Delay delay) {
        this.delay = delay;
    }
    
    /**
     * Sets the ammount of digital certificates this solicitude considers.
     * 
     * @param cfdis ammount of digital certificates (CFDIs)
     */
    protected void setCfdis(int cfdis) {
        this.cfdis = cfdis;
    }
    
    /**
     * Clears this solicitude's download registry
     */
    protected void setUnverified() {
        registry = UNVERIFIED_REGISTRY;
    }
    
    /**
     * Updates this solicitude's download registry to contain the specified
     * package identifiers.
     * 
     * @param ids wrapper of package identifiers to set, may be null if no
     *            packages are avaliable
     */
    protected void setVerified(PackageIds ids) {
        if (ids == null) {
            setUnverified();
        }
        else {
            registry = new VerifiedRegistry(ids);
        }
    }
    
    /**
     * Updates this solicitude's download registry with the specified one.
     * 
     * @param data the registry data to set, may be null if no data is available yet
     */
    protected void setVerified(DownloadRegistry data) {
        if (data == null || data.getPackageIds() == null) {
            setUnverified();
        }
        else {
            this.registry = new VerifiedRegistry(data);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the download state of this solicitude.
     * 
     * @param state the state to set
     * @throws NullPointerException if state is null
     */
    protected void setState(DownloadState state) {
        if (state == null) {
            throw new NullPointerException("invalid state");
        }
        this.state = state;
    }
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Updates this solicitude with the specified download request response.
     * 
     * @param response the download request response received from the web service
     * @throws IllegalStateException if this solicitude is not new
     * @throws NullPointerException if response is null
     */
    protected void doUpdate(SolicitaResponse response) {
        LOG.log(Level.TRACE, "doUpdate ({0})", response);
        if (state.getValue() != StateValue.NEW) {
            throw new IllegalStateException("Invalid state for a SolicitaResponse:"+state.getValue());
        }
        if (!isAccept(response)) {
            processReject(response);
            return;
        }
        setReject(null);
        setRequestId(response.getRequestId());
        setLastAccepted(response.getInstant());
        setState(this.getAcceptedState());
        fireDownloadEvent(DownloadEvent.Result.ACCEPTED, response);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Updates this solicitude with the specified verify request response.
     * 
     * @param response the verify request response received from the web service
     * @throws IllegalStateException if this solicitude is not accepted or delayed
     * @throws NullPointerException if response is null
     */
    protected void doUpdate(VerificaResponse response) {
        LOG.log(Level.TRACE, "doUpdate ({0})", response);
        if (state.getValue() != StateValue.ACCEPTED
                && state.getValue() != StateValue.DELAYED) {
            throw new IllegalStateException("Invalid state for a VerificaResponse:"+state.getValue());
        }
        if (!isAccept(response)) {
            processReject(response);
            return;
        }
        setReject(null);
        setDelay(null);
        setRequestId(response.getRequestId());       
        setLastAccepted(response.getInstant());
        
        Delay responseDelay = getDelay(response);
        if (responseDelay != null) {
            setDelay(responseDelay);
            setState(this.getDelayState());
            fireDownloadEvent(DownloadEvent.Result.DELAYED, response);
        }
        else {
            setCfdis(response.getCfdis());
            setVerified(response.getPackageIds());
            setState(this.getVerifiedState());
            fireDownloadEvent(DownloadEvent.Result.VERIFIED, response);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Updates this solicitude with the specified download package response.
     * 
     * @param response the download package response received from the web service
     * @throws IllegalStateException if this solicitude is not verified
     * @throws NullPointerException if response is null
     */
    protected void doUpdate(DescargaResponse response) {
        LOG.log(Level.TRACE, "doUpdate ({0})", response);
        if (state.getValue() != StateValue.VERIFIED) {
            throw new IllegalStateException("Invalid state for a DescargaResponse:"+state.getValue());
        }
        String packageId = response.getPackageId();
        if (!isAccept(response)) {
            processReject(response);
            return;
        }
        setReject(null);
        if (!response.isDisposed()) {
            getClient().save(packageId, response.getEncodedPackage());
        }
                
        boolean marked = registry.updateDownloaded(packageId);
        if (!marked) {
            LOG.log(System.Logger.Level.ERROR, "packagesInfo returning not found package ({0})", packageId);
        }
        fireDownloadEvent(DownloadEvent.Result.DOWNLOADED, response);
    }
    
    /**
     * Updates the reject property of this solicitude with the specified response
     * and fires a download event to notify of this rejection.
     * 
     * @param reject the response that was not accepted
     * @throws IllegalArgumentException if reject is null
     */
    protected void processReject(SatResponse reject) {
        if (reject == null) {
            throw new IllegalArgumentException("cannot process reject with null reject");
        }
        setReject(reject);
        LOG.log(Level.DEBUG, "Process with state({0}) was rejected ({1})", getValue(), reject);
        fireDownloadEvent(DownloadEvent.Result.REJECTED, reject);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns true if the specified response is an acceptance
     * 
     * @param response the response to query
     * @return true if the specified response is an acceptance
     * @throws NullPointerException if response is null
     */
    protected boolean isAccept(SatResponse response) {
        return (response.isAccept());
    }
    
    /**
     * Returns the value of the delay of the specified response, or null
     * if the response is not a delayed verification.
     * 
     * @param response the verified response to get delay value from
     * @return the value of the delay of the specified response, or null
     *         if the response is not a delayed verification
     * @throws NullPointerException if response is null
     */
    protected Delay getDelay(VerificaResponse response) {
        if (!response.isDelay()) {
            return null;
        }
        if (response.isAcceptedState()) {
            return Delay.ACCEPTED;
        }
        if (response.isInProgressState()) {
            return Delay.IN_PROGRESS;
        }
        return Delay.OTHER;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Notifies all registered <code>DownloadListener</code>'s of a new
     * <code>DownloadEvent</code> that will be created with the specified parameters.
     * 
     * @param result the result of the download event
     * @param response the response from the web service
     * @throws IllegalArgumentException if result or response are null
     */
    protected void fireDownloadEvent(DownloadEvent.Result result, SatResponse response) {
        Object[] listeners = listenerList.getListenerList();
        DownloadEvent downloadEvent = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == DownloadListener.class) {
                if (downloadEvent == null) {
                    downloadEvent = new DownloadEvent(this, result, response);
                }
                ((DownloadListener)listeners[i+1]).stateChanged(downloadEvent);
            }
        }
    }
   
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the reject property of this solicitude to the specified response.
     * 
     * @param reject the response that was not accepted. May be null if no
     *               rejection was received.
     */
    protected void setReject(SatResponse reject) {
        this.reject = reject;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the <code>DownloadState</code> of a new <code>Solicitude</code>.
     * 
     * @return the <code>DownloadState</code> of a new <code>Solicitude</code>
     */
    protected DownloadState getNewRequestState() {
        return NEW_STATE;
    }
    
    /**
     * Returns the <code>DownloadState</code> of an accepted <code>Solicitude</code>.
     * 
     * @return the <code>DownloadState</code> of an accepted <code>Solicitude</code>
     */
    protected DownloadState getAcceptedState(){
        return ACCEPTED_STATE;
    }
    
    /**
     * Returns the <code>DownloadState</code> of a delayed <code>Solicitude</code>.
     * 
     * @return the <code>DownloadState</code> of a delayed <code>Solicitude</code>
     */
    protected DownloadState getDelayState() {
        return DELAYED_STATE;
    }
    
    /**
     * Returns the <code>DownloadState</code> of a verified <code>Solicitude</code>.
     * 
     * @return the <code>DownloadState</code> of a verified <code>Solicitude</code>
     */
    protected DownloadState getVerifiedState() {
        return VERIFIED_STATE;
    }
    
    /**
     * Returns the download state that corresponds to the specified state value.
     * 
     * @param value state value to query
     * 
     * @return the <code>DownloadState</code> that corresponds to the specified
     *         state value
     * @throws IllegalArgumentException if value is null
     */
    protected DownloadState getState(StateValue value) {
        if (value == null) {
            throw new IllegalArgumentException("invalid value");
        }
        return 
            switch (value) {
                case NEW-> NEW_STATE;
                case ACCEPTED-> ACCEPTED_STATE;
                case DELAYED-> DELAYED_STATE;
                case VERIFIED-> VERIFIED_STATE;
            };
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Concrete implementation of <code>DownloadContext</code>.
     * <p>Basically forwards its methods to solicitude's instance methods.</p>
     * <p>Download context was decided to be implemented through this subclass
     * instead of in <code>DefaultSolicitude</code> for protecting to a certain
     * point solicitude's consistency</p>
     */
    protected class Context implements DownloadContext {
        
        @Override public DMTClient getClient() {
            return DefaultSolicitude.this.getClient();
        }
        @Override public Query getQuery() {
            return DefaultSolicitude.this.getQuery();
        }
        @Override public String getRequestId() {
            return DefaultSolicitude.this.getRequestId();
        }
    
        /**
         * Updates this solicitude with the specified download request response.
         * 
         * @param response the download request response received from the web service
         * @throws IllegalStateException if this solicitude is not new
         * @throws NullPointerException if response is null
         */
        @Override public void update(SolicitaResponse response) {
            doUpdate(response);
        }
        
        /**
         * Updates this solicitude with the specified verify request response.
         * 
         * @param response the verify request response received from the web service
         * @throws IllegalStateException if this solicitude is not accepted or delayed
         * @throws NullPointerException if response is null
         */
        @Override public void update(VerificaResponse response) {
            doUpdate(response);
        }
         
        /**
         * Updates this solicitude with the specified download package response.
         * 
         * @param response the download package response received from the web service
         * @throws IllegalStateException if this solicitude is not verified
         * @throws NullPointerException if response is null
         */
        @Override public void update(DescargaResponse response) {
            doUpdate(response);
        }
    
        @Override public String getNextDownloadablePackageId() {
            return DefaultSolicitude.this.getNextDownloadablePackageId();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * <code>MutableRegistry</code> extends interface <code>DownloadRegistry</code>
     * adding a method that allows changing the download state of a package
     * identifier.
     */
    protected static interface MutableRegistry extends DownloadRegistry {
        
        /**
         * Updates the specified package identifier's state to downloaded.
         * <p>This method is case-sensitive.</p>
         * 
         * @param packageId the package identifier to update
         * @return false if the specified package identifier was not found
         */
        public boolean updateDownloaded(String packageId);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Utility class that provides a concrete implementation of <code>MutableRegistry</code>
     * representing an empty download registry.
     */
    protected static class EmptyRegistry implements MutableRegistry {       
        @Override public PackageIds getPackageIds() {
            return null;
        }
        @Override public boolean isDownloaded(int index) {
            return false;
        }
        @Override public boolean isDownloadDone() {
            return false;
        }
        @Override public boolean updateDownloaded(String packageId) {
            return false;
        }
        @Override public String getNextDownloadablePackageId() {
            return null;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Concrete implementation of <code>MutableRegistry</code> that represents
     * a download registry that can be updated.
     */
    protected static class VerifiedRegistry extends DownloadRegistryImpl implements MutableRegistry {
        private static final long serialVersionUID = 20241027L;
        
        /**
         * Constructs a new <code>VerifiedRegistry</code> with the specified data.
         * 
         * @param data the download registry to clone
         */
        public VerifiedRegistry(DownloadRegistry data) {
            this(data.getPackageIds());
            for (int idx = 0; idx < flags.length; idx++) {
                flags[idx] = data.isDownloaded(idx);
            }
        }
        
        /**
         * Constructs a new <code>VerifiedRegistry</code> with the specified
         * <code>PackageIds</code> whose enclosed packages have not been downloaded.
         * 
         * @param ids the wrapper that contains the package identifiers to download.
         */
        public VerifiedRegistry(PackageIds ids) {
            super(ids);
        }
        
        @Override public boolean updateDownloaded(String packageId) {
            int index = ids.indexOf(packageId);
            if (index >= 0) {
                flags[index] = true;
                return true;
            }
            return false;
        }

    }
    
}
