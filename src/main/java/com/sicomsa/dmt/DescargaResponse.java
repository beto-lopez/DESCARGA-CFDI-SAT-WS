/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;

/**
 * Represents a response of SAT's massive download web service, when requesting
 * the download of a particular package
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 *
 * @version 2025.01.04
 * @since 1.0
 * 
 */
public class DescargaResponse extends SatResponse {
  
    private static final long serialVersionUID = 20250104L;
    
    /**
     * The id of the package to download.
     */
    protected String packageId;
    
    /**
     * An encoded package as it was received from SAT.
     */
    protected String encodedPackage;
    
    /**
     * True if the <code>encodedPackage</code> has been disposed.
     */
    protected boolean disposed;

    /**
     * Builds a <code>DescargaResponse</code> using the parameters received.
     * 
     * @param satInstant <code>Instant</code> the response was received.
     * @param statusCode the status code enclosed in this response
     * @param message the message received in this response
     * @param packageId the id of the package that was meant to be downloaded
     * @param encodedPackage the encoded package received
     */
    public DescargaResponse(Instant satInstant, String statusCode, String message, String packageId, String encodedPackage) {
        this(satInstant, statusCode, message, packageId, encodedPackage, false);
    }
    
    /**
     * Protected constructor to build a <code>DescargaResponse</code> that may
     * have been disposed.
     * 
     * @param satInstant <code>Instant</code> the response was received.
     * @param statusCode the status code enclosed in this response
     * @param message the message received in this response
     * @param packageId the id of the package that was meant to be downloaded
     * @param disposed true if the package was disposed
     */
    protected DescargaResponse(Instant satInstant, String statusCode, String message, String packageId, boolean disposed) {
        this(satInstant, statusCode, message, packageId, null, disposed);
    }
    
    /**
     * Protected constructor to build a <code>DescargaResponse</code>.
     * 
     * @param satInstant <code>Instant</code> the response was received.
     * @param statusCode the status code enclosed in this response
     * @param message the message received in this response
     * @param packageId the id of the package that was meant to be downloaded
     * @param encodedPackage the encoded package received
     * @param disposed true if the package was disposed
     */
    protected DescargaResponse(Instant satInstant, String statusCode, String message,
            String packageId, String encodedPackage, boolean disposed) {
        
        super(satInstant, statusCode, message);
        this.packageId = packageId;
        this.encodedPackage = nonBlank(encodedPackage);
        this.disposed = disposed;
    }
    
    /**
     * Returns the string received or null if it is null or blank.
     * 
     * @param string the string to evaluate
     * @return  the <code>string</code> or null if it is null or blank.
     */
    protected final String nonBlank(String string) {
        return (string == null ? null : (string.isBlank() ? null : string));
    }
    
    /**
     * Returns the id of the package in this response.
     * 
     * @return the id of the package in this respose.
     */
    public String getPackageId() {
        return packageId;
    }
    
    /**
     * Returns the encoded package in this response.
     * 
     * @return the encoded package in this response.
     */
    public String getEncodedPackage() {
        return encodedPackage;
    }
    
    /**
     * Returns true if this response has been disposed.<p>
     * A <code>DescargaResponse</code> is disposed when it contained an
     * accepted and downloaded package and then it was disposed.
     * 
     * @return true if this response has been disposed.
     */
    public boolean isDisposed() {
        return disposed;
    }
    
    /**
     * Clears <code>encodedPackage</code> property which contains the
     * downloaded encoded package and sets this response as disposed if it 
     * is an accepted response.
     * 
     */
    public void dispose() {
        if (isAccept()) {
            disposed = true;
        }
        encodedPackage = null;
    }
    
    /**
     * Returns true if this response has or had a non blank encoded package
     * downloaded from SAT.
     * 
     * @return true if this response has or had a non blank encoded package
     *         downloaded from SAT.
     */
    @Override public boolean isAccept() {
        return (super.isAccept()
                && (disposed
                    || (encodedPackage != null && !encodedPackage.isBlank())));
    }

    /**
     * Returns a String representation of this response.
     * It will not include the encodedPackage in this representation.
     * 
     * @return a String representation of this response.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("DescargaResponse{")
                .append("instant=").append(satInstant)
                .append(",statusCode=").append(statusCode)
                .append(",message=").append(message)
                .append(",packageId=").append(packageId)
                .append(",disposed=").append(disposed)
                .append(",encodedPackage:");
        if (encodedPackage==null) {
            sb.append("null");
        }
        else {
            sb.append("ln=").append(encodedPackage.length());
        }
        return sb.append("}").toString();
    }

}
