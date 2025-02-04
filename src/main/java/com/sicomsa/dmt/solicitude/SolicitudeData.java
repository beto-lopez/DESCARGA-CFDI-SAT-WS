/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;



import java.time.Instant;

import com.sicomsa.dmt.PackageIds;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.17
 * 
 * Contains the information collected from a request at different stages.
 * It is used to restore a Solicitude to a given state.
 * 
 * 
 */
public class SolicitudeData implements java.io.Serializable, DownloadRegistry {
    
    private static final long serialVersionUID = 20241117L;
    
    protected String requestId;
    protected Instant instant;
    
    public SolicitudeData(String requestId, Instant instant) {
        if (requestId == null || requestId.isBlank() || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.requestId = requestId;
        this.instant = instant;
    }
    
    public StateValue getStateValue() {
        return StateValue.ACCEPTED;
    }

    public String getRequestId() {
        return requestId;
    }
    
    public Instant getLastAccepted() {
        return instant;
    }
    
    public Delay getDelay() {
        return null;
    }
    
    public int getCfdis() {
        return 0;
    }
    
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
    
    @Override public String toString() {
        return new StringBuilder("SolicitudeData{")
                .append("instant=").append(instant)
                .append(",requestId=").append(requestId)
                .append(",stateValue=").append(getStateValue())
                .append("}").toString();
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public static class Delayed extends SolicitudeData {
        
        private static final long serialVersionUID = 20241118L;
        
        protected Delay delay;
        
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
    
    public static class Verified extends SolicitudeData {
        private static final long serialVersionUID = 20241118L;
        
        protected final PackageIds ids;
        protected final int cfdis;
        protected final boolean[] flags;
        
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
