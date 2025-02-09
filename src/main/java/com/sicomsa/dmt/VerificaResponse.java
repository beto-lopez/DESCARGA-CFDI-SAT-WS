/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;


import java.util.List;
import java.util.ArrayList;
import java.time.Instant;


/**
 * <code>VerificaResponse</code> is SAT's response from a "verify download request"
 * made to the massive download web service when requesting a verification for
 * a particular request mapped by a <code>requestId</code> or (IdSolicitud)
 * provided in a download request of a particular query.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version 2025.01.04
 * @since 1.0
 * 
 */
public class VerificaResponse extends SatResponse {
    
    private static final long serialVersionUID = 20250104L;
    
    /**
     * Status code value when there were no CFDI's found with given request.
     */
    public static final String NO_INFO_FOUND_STS_CODE = "5004";
    
    /**
     * Constant state value for an undefined state
     */
    public static final int UNDEFINED  = 0; //I have seen state 0
    
    /**
     * Constant state value for an accepted state
     */
    public static final int ACEPTADA   = 1;
    
    /**
     * Constant state value for an "in progress" state
     */
    public static final int EN_PROCESO = 2; //esta no es error, pero no está lista
    
    /**
     * Constant state value for "concluded" state
     */
    public static final int TERMINADA  = 3; //en teoría este es success
    
    /**
     * Constant state value for error
     */
    public static final int ERROR      = 4;
    
    /**
     * Constant state value for a rejection state
     */
    public static final int RECHAZADA  = 5; //puede ser que no haya cfdis en periodo seleccionado
    
    /**
     * Constant state value for an expired request
     */
    public static final int VENCIDA    = 6;

    /**
     * The requestId that was verified.
     */
    protected String requestId;
    
    /**
     * The request state of this response
     */
    protected int solicitudeState;
    
    /**
     * The status code of the download request.
     * Note: this response's inherited <code>statusCode</code> property is
     * the status code of the verification.
     */
    protected String solicitudeStsCode;
    
    /**
     * Ammount of CFDI's to download found given the request attributes.
     */
    protected int cfdis;
    
    /**
     * Wrapper class that holds a list of ids to download.
     */
    protected PackageIds packageIds;
    
    /////////////////////////////////////////////////////////
    
    /**
     * Returns a <code>VerificaResponse</code> with the given parameters.
     * It is recommended to use the <code>VerificaResponse.Builder</code> class
     * to instantiate this class.
     * 
     * @param satInstant <code>Instant</code> mesage was received from SAT
     * @param statusCode status code of the received message
     * @param message message received
     * @param state state of the request
     * @param solicitudeSts status code of the download request
     * @param requestId request if of the download request
     */
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
     * Returns a <code>VerificaResponse</code> with the given parameters.
     * It is recommended to use the <code>VerificaResponse.Builder</code> class
     * to instantiate this class.
     * 
     * @param satInstant <code>Instant</code> mesage was received from SAT
     * @param statusCode status code of the received message
     * @param message message received
     * @param solicitudeState state of the request
     * @param solicitudeStsCode status code of the download request
     * @param requestId request if of the download request
     * @param cfdis ammount of CFDI's found for download
     * @param packageIds wrapper of the list of ids to download
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
    
    /**
     * Returns true if this response corresponds to an accepted verification.
     * 
     * @return true if this response corresponds to an accepted verification
     */
    @Override public boolean isAccept() {
        return (isDelay() || isDownloadable());
    }
    
    /**
     * Returns true if this response corresponds to an accepted request whose
     * verification has been concluded, and contains package id's to download.
     * 
     * @return true if this response corresponds to an accepted request whose
     *         verification has been concluded, and contains package id's
     *         to download.
     */
    public boolean isDownloadable() {
        return (isAcceptedSolicitude()
                && isFinishedState()
                && hasPackagesInfo());
    }
    
    /**
     * Returns true if this response corresponds to an accepted request whose
     * verification has not been concluded.
     * 
     * @return true if this response corresponds to an accepted request whose
     *         verification has not been concluded.
     */
    public boolean isDelay() {
        return (isAcceptedSolicitude()
                && !hasPackagesInfo()
                && (isAcceptedState() || isInProgressState()));
    }
    
    /**
     * Returns true if this response corresponds to an accepted download request
     * whose verification is also accepted.
     * 
     * @return true if this response corresponds to an accepted download request
     *         whose verification is also accepted.
     */
    public boolean isAcceptedSolicitude() {
        return (super.isAccept()
                && STATUS_CODE_ACCEPT.equals(solicitudeStsCode));
    }
    
    /**
     * Returns true if there were no CFDI's found that met the request attributes.
     * 
     * @return true if there were no CFDI's found that met the request attributes.
     */
    public boolean isNoInfoFound() {
        return (STATUS_CODE_ACCEPT.equals(statusCode) ///stsCode = "5000"
                && NO_INFO_FOUND_STS_CODE.equals(solicitudeStsCode) ///solicitudeStsCode = "5004"
                && solicitudeState == RECHAZADA); ///solicitudeState = 5
    }
    
    /**
     * Returns true if this response contains a list of ids to download.
     * 
     * @return true if this response contains a list of ids to download.
     */
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
    
    /**
     * Returns the request Id of the download request this response corresponds to.
     * 
     * @return the request Id of the download request this response corresponds to.
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Returns the state of this verification.
     * 
     * @return the state of this verification.
     */
    public int getSolicitudeState() {
        return solicitudeState;
    }

    /**
     * Returns the status code of the download request of this verification.
     * 
     * @return the status code of the download request of this verification.
     */
    public String getSolicitudeStsCode() {
        return solicitudeStsCode;
    }
    
    /**
     * Returns the ammount of CFDI's found with the request attributes.
     * 
     * @return the ammount of CFDI's found with the request attributes.
     */
    public int getCfdis() {
        return cfdis;
    }

    /**
     * Returns the list of ids available for download wrapped in <code>PackageIds</code>
     * 
     * @return the list of ids available for download wrapped in <code>PackageIds</code>
     */
    public PackageIds getPackageIds() {
        return packageIds;
    }

    /**
     * Returns true if this response has an accepted state
     * 
     * @return true if this response has an accepted state
     */
    public boolean isAcceptedState() {
        return (solicitudeState == ACEPTADA);
    }
    
    /**
     * Returns true if this response has an "in progress" state
     * 
     * @return true if this response has an "in progress" state
     */
    public boolean isInProgressState() {
        return (solicitudeState == EN_PROCESO);
    }
    
    /**
     * Returns true if this response has a concluded state
     * 
     * @return true if this response has a concluded state
     */
    public boolean isFinishedState() {
        return (solicitudeState == TERMINADA);
    }
    
    /**
     * Returns true if this response has an error state
     * 
     * @return true if this response has an error state
     */
    public boolean isErrorState() {
        return (solicitudeState == ERROR);
    }
    
    /**
     * Returns true if this response has a rejected state
     * 
     * @return true if this response has a rejected state
     */
    public boolean isRejectedState() {
        return (solicitudeState == RECHAZADA);
    }
    
    /**
     * Returns true if this response has an expired state
     * 
     * @return true if this response has an expired state
     */
    public boolean isExpiredState() {
        return (solicitudeState == VENCIDA);
    }
    
    /**
     * Returns true if this response has an undefined state
     * 
     * @return true if this response has an undefined state
     */
    public boolean isUndefinedState() {
        return (solicitudeState == UNDEFINED || solicitudeState < 0 || solicitudeState > 6);
    }
    
    /**
     * Returns a <code>VerificaResponse.Builder</code> instance.
     * 
     * @return a <code>VerificaResponse.Builder</code> instance
     */
    public static VerificaResponse.Builder builder() {
        return new VerificaResponse.Builder();
    }
    
    /**
     * Returns a string representation of this response.
     * 
     * @return a string representation of this response
     */
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
    
    /**
     * Utility class to facilitate creation of <code>VerificaResponse</code> instances.<p>
     * 
     * Contains methods to set parameters and the build <code>VerificaResponse</code>s
     * instances with the <code>build</code> method.<p>
     * 
     * The <code>build</code> method does not clear any parameters and lets you
     * reuse the builder setting or resetting parameters. You can reset all the
     * parameters with the <code>clear</code> method.
     * 
     */
    public static class Builder {
        /**
         * The request Id of the download request
         */
        protected String requestId;
        
        /**
         * <code>Instant</code> when the response was received.
         */
        protected Instant satInstant;
        
        /**
         * Status code of the verification request
         */
        protected String statusCode;
        
        /**
         * Message received in the response
         */
        protected String message;
        
        /**
         * State value of the request state
         */
        protected int solicitudeState;
        
        /**
         * Status code of the download request
         */
        protected String solicitudeStsCode;
        
        /**
         * Ammount of CFDI's to download
         */
        protected int cfdis;
        
        /**
         * List of ids available for download
         */
        protected List<String> idList;
        
        
        /**
         * Constructs a <code>Builder</code> instance with no parameters set.
         */
        public Builder() {
        }
        
        /**
         * Clears all the parameters of this <code>Builder</code> instance.
         */
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
        
        /**
         * Sets the request id of the <code>VerificaResponse</code> this builder
         * will build.
         * 
         * @param requestId the request id
         * @return this <code>Builder</code>
         */
        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }
        
        /**
         * Sets the <code>Instant</code> of the <code>VerificaResponse</code>
         * this builder will build.
         * 
         * @param satInstant the <code>Instant</code> of the response
         * @return this <code>Builder</code>
         */
        public Builder setSatInstant(Instant satInstant) {
            this.satInstant = satInstant;
            return this;
        }
        
        /**
         * Sets the verification status code of the <code>VerificaResponse</code>
         * to be built.
         * 
         * @param statusCode the verification status code
         * @return this <code>Builder</code>
         */
        public Builder setStatusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        /**
         * Sets the message of the <code>VerificaResponse</code> to be built.
         * 
         * @param message the verification response message
         * @return this <code>Builder</code>
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
        
        
        /**
         * Sets the verification state of the <code>VerificaResponse</code> to
         * be built.
         * 
         * @param state the state of the verification to build
         * @return this <code>Builder</code>
         */
        public Builder setSolicitudeState(int state) {
            this.solicitudeState = state;
            return this;
        }
        
        /**
         * Sets the download request status code of the <code>VerificaResponse</code>
         * to be built.
         * 
         * @param code the download request status code
         * @return this <code>Builder</code>
         */
        public Builder setSolicitudeStsCode(String code) {
            this.solicitudeStsCode = code;
            return this;
        }
        
        /**
         * Sets the ammount of CFDI's available for download of the
         * <code>VerificaResponse</code> to be built.
         * 
         * @param ammount ammount of CFDI's to download
         * @return this <code>Builder</code>
         */
        public Builder setCfdisAmmount(int ammount) {
            this.cfdis = ammount;
            return this;
        }
        
        /**
         * Adds an id to the list of package Id's to download of the
         * <code>VerificaResponse</code> to be built.
         * 
         * @param packageId the id of the package available for download
         * @return this <code>Builder</code>
         */
        public Builder addPackageId(String packageId) {
            if (idList == null) {
                idList = new ArrayList<>();
            }
            idList.add(packageId);
            return this;
        }
        
        /**
         * Returns a new <code>VerificaResponse</code> with the parameters previously
         * set. This method does not clear any parameters.
         * 
         * @return a new <code>VerificaResponse</code> instance with the parameters set.
         */
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
        
        /**
         * Protected method that returns an instance of <code>PackageIds</code>
         * if there were id's added to this <code>Builder</code> or null if not.
         * 
         * @return an instance of <code>PackageIds</code> if there were id's
         *         added to this <code>Builder</code> or null if not.
         */
        protected PackageIds getPackageIds() {
            if (idList == null || idList.isEmpty()) {
                return null;
            }
            return new PackageIds(idList);
        }
        
    }
    
    /////////////////////////////////////////////////////////

}
