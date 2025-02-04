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

import java.time.Instant;
import java.lang.System.Logger.Level;
import javax.swing.event.EventListenerList;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.23
 * 
 * DownloadRequest (Solicitude) implementation.
 * 
 * 
 * 
 */
public class DefaultSolicitude implements Solicitude {

    public static final DownloadState NEW_STATE = new DownloadState.New();
    public static final DownloadState ACCEPTED_STATE = new DownloadState.Accepted();
    public static final DownloadState DELAYED_STATE = new DownloadState.Delayed();
    public static final DownloadState VERIFIED_STATE = new DownloadState.Verified();
    
    public static final MutableRegistry UNVERIFIED_REGISTRY = new EmptyRegistry();
    
    protected EventListenerList listenerList;
    
    private boolean _paused = false;
    
    protected DMTClient client;
    protected Query query;
    
    protected SatResponse reject;
    protected DownloadState state;
    protected String requestId;
    protected Instant lastAccepted;
    protected Delay delay;
    protected int cfdis;
    protected Context context;
    protected MutableRegistry registry;

    private static final System.Logger LOG = System.getLogger(DefaultSolicitude.class.getName());

    
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
    
    protected boolean isRequestable() {
        return (isAble() && state.isRequestable());
    }
    
    protected boolean isVerifiable() {
        return (isAble() && state.isVerifiable());
    }
    
    protected boolean isDownloadable() {
        return (isAble() && state.isDownloadable() && !isDownloadDone());
    }

    protected synchronized void setPaused(boolean paused) {
        this._paused = paused;
    }
    
    protected boolean isAble() {
        return (!isPaused() && !isReject());
    }
    
    
    ////////////////////////////////////////////////////////////////////////////   
  
    protected void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    protected void setLastAccepted(Instant instant) {
        this.lastAccepted = instant;
    }
    
    protected void setDelay(Delay delay) {
        this.delay = delay;
    }
    
    protected void setCfdis(int cfdis) {
        this.cfdis = cfdis;
    }
    
    protected void setUnverified() {
        registry = UNVERIFIED_REGISTRY;
    }
    
    protected void setVerified(PackageIds ids) {
        if (ids == null) {
            setUnverified();
        }
        else {
            registry = new VerifiedRegistry(ids);
        }
    }
    
    protected void setVerified(DownloadRegistry data) {
        if (data == null || data.getPackageIds() == null) {
            setUnverified();
        }
        else {
            this.registry = new VerifiedRegistry(data);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////

    protected void setState(DownloadState state) {
        if (state == null) {
            throw new NullPointerException("invalid state");
        }
        this.state = state;
    }
    ////////////////////////////////////////////////////////////////////////////
    
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
    
    protected void processReject(SatResponse reject) {
        if (reject == null) {
            throw new IllegalArgumentException("cannot process reject with null reject");
        }
        setReject(reject);
        LOG.log(Level.DEBUG, "Process with state({0}) was rejected ({1})", getValue(), reject);
        fireDownloadEvent(DownloadEvent.Result.REJECTED, reject);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected boolean isAccept(SatResponse response) {
        return (response.isAccept());
    }
    
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

    protected void setReject(SatResponse reject) {
        this.reject = reject;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    protected DownloadState getNewRequestState() {
        return NEW_STATE;
    }
    protected DownloadState getAcceptedState(){
        return ACCEPTED_STATE;
    }
    protected DownloadState getDelayState() {
        return DELAYED_STATE;
    }
    protected DownloadState getVerifiedState() {
        return VERIFIED_STATE;
    }
    
        
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
    
        @Override public void update(SolicitaResponse response) {
            doUpdate(response);
        }
        @Override public void update(VerificaResponse response) {
            doUpdate(response);
        }
        @Override public void update(DescargaResponse response) {
            doUpdate(response);
        }
    
        @Override public String getNextDownloadablePackageId() {
            return DefaultSolicitude.this.getNextDownloadablePackageId();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    protected static interface MutableRegistry extends DownloadRegistry {
        public boolean updateDownloaded(String packageId);
    }
    
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
    
    protected static class VerifiedRegistry extends DownloadRegistryImpl implements MutableRegistry {
        private static final long serialVersionUID = 20241027L;
        
        public VerifiedRegistry(DownloadRegistry data) {
            this(data.getPackageIds());
            for (int idx = 0; idx < flags.length; idx++) {
                flags[idx] = data.isDownloaded(idx);
            }
        }
        
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
