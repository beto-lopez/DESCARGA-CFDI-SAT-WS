/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import java.util.List;
import java.util.ArrayList;
import java.time.Instant;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * 
 * @since 2024.9.9
 * @version 2024.10.27
 * @version 2025.01.04
 * 
 * Represents and contains SAT's response to a download request verification
 * 
 */
public class VerificaResponse extends SatResponse {
    
    private static final long serialVersionUID = 20250104L;
    
    public static final String NO_INFO_FOUND_STS_CODE = "5004";
    
    public static final int UNDEFINED  = 0; //I have seen state 0
    public static final int ACEPTADA   = 1;
    public static final int EN_PROCESO = 2; //esta no es error, pero no está lista
    public static final int TERMINADA  = 3; //en teoría este es success
    public static final int ERROR      = 4;
    public static final int RECHAZADA  = 5; //puede ser que no haya cfdis en periodo seleccionado
    public static final int VENCIDA    = 6;

    protected String requestId;
    protected int solicitudeState;
    protected String solicitudeStsCode;
    protected int cfdis;
    protected PackageIds packageIds;
    
    /////////////////////////////////////////////////////////
    
    public VerificaResponse(
            Instant satInstant,
            String statusCode,
            String message,
            int state,
            String solicitudeSts,
            String requestId) {
        
        this(satInstant, statusCode, message, state, solicitudeSts, requestId, 0, null);
    }

    /**
     * better use the builder
     * @param satInstant
     * @param statusCode
     * @param message
     * @param solicitudeState
     * @param solicitudeStsCode
     * @param requestId
     * @param cfdis 
     * @param packageIds
     */
    public VerificaResponse(
            Instant satInstant,
            String statusCode,
            String message,
            int solicitudeState,
            String solicitudeStsCode,
            String requestId,
            int cfdis,
            PackageIds packageIds) {
        super(satInstant, statusCode, message);
        this.solicitudeState = solicitudeState;
        this.solicitudeStsCode = solicitudeStsCode;
        this.requestId = requestId;
        this.cfdis = cfdis;
        this.packageIds = packageIds;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    @Override public boolean isAccept() {
        return (isDelay() || isDownloadable());
    }
    
    public boolean isDownloadable() {
        return (isAcceptedSolicitude()
                && isFinishedState()
                && hasPackagesInfo());
    }
    
    public boolean isDelay() {
        return (isAcceptedSolicitude()
                && !hasPackagesInfo()
                && (isAcceptedState() || isInProgressState()));
    }
    
    public boolean isAcceptedSolicitude() {
        return (super.isAccept()
                && STATUS_CODE_ACCEPT.equals(solicitudeStsCode));
    }
    
    public boolean isNoInfoFound() {
        return (STATUS_CODE_ACCEPT.equals(statusCode) ///stsCode = "5000"
                && NO_INFO_FOUND_STS_CODE.equals(solicitudeStsCode) ///solicitudeStsCode = "5004"
                && solicitudeState == RECHAZADA); ///solicitudeState = 5
    }
    
    public boolean hasPackagesInfo() {
        return (packageIds != null);
    }
    
    /*
    VerificaResponse{
instantUTC=2024-11-28T07:31:06.707583900Z,
statusCode=5000,
message=Solicitud Aceptada,
solicitudeState=5,
solicitudeStsCode=5004,
cfdisAmmount=0[]}
}
    */
    
    public String getRequestId() {
        return requestId;
    }
    
    public int getSolicitudeState() {
        return solicitudeState;
    }

    public String getSolicitudeStsCode() {
        return solicitudeStsCode;
    }
    
    public int getCfdis() {
        return cfdis;
    }

    public PackageIds getPackageIds() {
        return packageIds;
    }

    public boolean isAcceptedState() {
        return (solicitudeState == ACEPTADA);
    }
    public boolean isInProgressState() {
        return (solicitudeState == EN_PROCESO);
    }
    public boolean isFinishedState() {
        return (solicitudeState == TERMINADA);
    }
    public boolean isErrorState() {
        return (solicitudeState == ERROR);
    }
    public boolean isRejectedState() {
        return (solicitudeState == RECHAZADA);
    }
    public boolean isExpiredState() {
        return (solicitudeState == VENCIDA);
    }
    public boolean isUndefinedState() {
        return (solicitudeState == UNDEFINED || solicitudeState < 0 || solicitudeState > 6);
    }
    
    public static VerificaResponse.Builder builder() {
        return new VerificaResponse.Builder();
    }
    
    @Override public String toString() {
        return new StringBuilder("VerificaResponse{")
                .append("instant=").append(satInstant)
                .append(",statusCode=").append(statusCode)
                .append(",message=").append(message)
                .append(",solicitudeState=").append(solicitudeState)
                .append(",solicitudeStsCode=").append(solicitudeStsCode)
                .append(",requestId=").append(requestId)
                .append(",cfdiso=").append(cfdis)
                .append(",packageIds=").append(packageIds)
                .append("}").toString();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public static class Builder {
        protected String requestId;
        protected Instant satInstant;
        protected String statusCode;
        protected String message;
        protected int solicitudeState;
        protected String solicitudeStsCode;
        protected int cfdis;
        protected List<String> idList;
        
        public Builder() {
        }
        
        public void clear() {
            requestId = null;
            satInstant = null;
            statusCode = null;
            message = null;
            solicitudeState = 0;
            solicitudeStsCode = null;
            cfdis = 0;
            idList = null;
        }
        
        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }
        public Builder setSatInstant(Instant satInstant) {
            this.satInstant = satInstant;
            return this;
        }
        public Builder setStatusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
        public Builder setSolicitudeState(int state) {
            this.solicitudeState = state;
            return this;
        }
        public Builder setSolicitudeStsCode(String code) {
            this.solicitudeStsCode = code;
            return this;
        }
        public Builder setCfdisAmmount(int ammount) {
            this.cfdis = ammount;
            return this;
        }
        public Builder addPackageId(String packageId) {
            if (idList == null) {
                idList = new ArrayList<>();
            }
            idList.add(packageId);
            return this;
        }
        public VerificaResponse build() {
            return new VerificaResponse(
                    satInstant,
                    statusCode,
                    message,
                    solicitudeState,
                    solicitudeStsCode,
                    requestId,
                    cfdis,
                    getPackageIds());
        }
        protected PackageIds getPackageIds() {
            if (idList == null || idList.isEmpty()) {
                return null;
            }
            return new PackageIds(idList);
        }
        
    }
    
    /////////////////////////////////////////////////////////

}
