/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;



import java.time.Instant;

import com.sicomsa.dmt.PackageIds;

/**
 * Contains the information collected from a request at different stages.
 * It can be used to restore a <code>Solicitude</code> to a given state.
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.17
 * @since 1.0
 * 
 * 
 */
public class SolicitudeData implements java.io.Serializable, DownloadRegistry {
    
    private static final long serialVersionUID = 20241117L;
    
    /**
     * The request identifier. (IdSolicitud)
     */
    protected String requestId;
    
    /**
     * Instant when this data was received from the WS.
     */
    protected Instant instant;
    
    /**
     * Creates a <code>SolicitudeData</code> whith the specified parameters.
     * 
     * @param requestId the request identifier
     * @param instant instant when received from the WS
     */
    public SolicitudeData(String requestId, Instant instant) {
        if (requestId == null || requestId.isBlank() || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.requestId = requestId;
        this.instant = instant;
    }
    
    /**
     * Returns the <code>StateValue</code> of this data.
     * 
     * @return the <code>StateValue</code> of this data
     */
    public StateValue getStateValue() {
        return StateValue.ACCEPTED;
    }

    /**
     * Returns the request identifier of this data.
     * 
     * @return the request identifier of this data
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Returns the instant this data was received from the WS.
     * 
     * @return the instant this data was received from the WS
     */
    public Instant getLastAccepted() {
        return instant;
    }
    
    /**
     * Returns the delay value of this data or null if this
     * data is not delayed.
     * 
     * @return the delay value of this data or null if this data is not delayed
     */
    public Delay getDelay() {
        return null;
    }
    
    /**
     * Returns the ammount of digital certificates this request considers.
     * 
     * @return the ammount of digital certificates this request considers
     */
    public int getCfdis() {
        return 0;
    }
    
    /**
     * Returns a string representation of this data.
     * 
     * @return a string representation of this data
     */
    @Override public String toString() {
        return new StringBuilder("SolicitudeData{")
                .append("instant=").append(instant)
                .append(",requestId=").append(requestId)
                .append(",stateValue=").append(getStateValue())
                .append("}").toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// DownloadRegitry implementation
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public PackageIds getPackageIds() {
        return null;
    }
    
    @Override public boolean isDownloaded(int index) {
        return false;
    }
    
    @Override public boolean isDownloadDone() {
        return false;
    }
    
    @Override public String getNextDownloadablePackageId() {
        return null;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Contains the information collected from a <code>Solicitude</code> whose
     * request has not yet been verified.
     *
     * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
     * @version 2024.11.17
     * @since 1.0
     * 
     */
    public static class Delayed extends SolicitudeData {
        
        private static final long serialVersionUID = 20241118L;
        
        /**
         * Value of delay
         */
        protected Delay delay;
        
        /**
         * Constructs a new <code>SolicitudeData.Delayed</code> with the
         * specified parameters.
         * 
         * @param requestId the request identifier
         * @param delay the value of hte delay
         * @param instant the instant this data was received from the WS
         */
        public Delayed(String requestId, Delay delay, Instant instant) {
            super(requestId, instant);
            if (delay == null) {
                throw new IllegalArgumentException("delay required in this constructor");
            }
            this.delay = delay;
        }
        
        @Override public StateValue getStateValue() {
            return StateValue.DELAYED;
        }
        
        @Override public Delay getDelay() {
            return delay;
        }
        
        @Override public String toString() {
            return new StringBuilder("SolicitudeData.Delayed{")
                .append("instant=").append(instant)
                .append(",requestId=").append(requestId)
                .append(",stateValue=").append(getStateValue())
                .append(",delay=").append(delay)
                .append("}").toString();
        }

    }
    
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Contains the information collected from a <code>Solicitude</code> whose
     * request has been verified.
     *
     * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
     * @version 2024.11.17
     * @since 1.0
     * 
     */
    public static class Verified extends SolicitudeData {
        private static final long serialVersionUID = 20241118L;
        
        /**
         * package identifiers of this verification
         */
        protected final PackageIds ids;
        
        /**
         * ammount of digital certificates it considers
         */
        protected final int cfdis;
        
        /**
         * array of flags. True when package identifier at index has been downloaded
         */
        protected final boolean[] flags;
        
        /**
         * Creates a new <code>SolicitudeData.Verified</code> with the specified
         * parameters.}
         * 
         * @param requestId the request identifier of the <code>Solicitude</code>
         * @param instant instant when the verification was received from the WS
         * @param cfdis ammount of digital certificates involved in this verification
         * @param registry registry with the package identifiers of this verification
         */
        public Verified(String requestId, Instant instant, int cfdis, DownloadRegistry registry) { 
            super(requestId, instant);
            if (registry == null) {
                throw new IllegalArgumentException("download registry is required");
            }
            if (cfdis < 0) {
                throw new IllegalArgumentException("invalid cfdis ammount");
            }
            this.ids = registry.getPackageIds();
            if (ids == null) {
                throw new IllegalArgumentException("packageIds cannot be null in verified solicitude");
            }
            this.cfdis = cfdis;
            flags = new boolean[ids.size()];
            for (int idx = 0; idx < flags.length; idx++) {
                flags[idx] = registry.isDownloaded(idx);
            }
        }
        
        @Override public StateValue getStateValue() {
            return StateValue.VERIFIED;
        }
        
        @Override public int getCfdis() {
            return cfdis;
        }
        
        @Override public PackageIds getPackageIds() {
            return ids;
        }
        
        @Override public boolean isDownloaded(int index) {
            return (index >= 0
                    && index < flags.length
                    && flags[index]);
        }
        
        @Override public boolean isDownloadDone() {
            for (boolean done : flags) {
                if (!done) {
                    return false;
                }
            }
            return true;
        }
        
        @Override public String getNextDownloadablePackageId() {
            for (int idx = 0; idx < flags.length; idx++) {
                if (!flags[idx]) {
                    return ids.getPackageId(idx);
                }
            }
            return null;
        }
        
        @Override public String toString() {
            return new StringBuilder("SolicitudeData.Verified{")
                .append("instant=").append(instant)
                .append(",requestId=").append(requestId)
                .append(",stateValue=").append(getStateValue())
                .append(",cfdis=").append(cfdis)
                .append(",ids=").append(ids)
                .append(",downloads=").append(java.util.Arrays.toString(flags))
                .append("}").toString();
        }
    }
    
}
