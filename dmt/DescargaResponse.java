/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * 
 * @since 2024.9.9
 * @version 2025.01.04
 * 
 * Represents the response of sat massive download web service, when requesting
 * the download of a particular package
 * 
 */
public class DescargaResponse extends SatResponse {
  
    private static final long serialVersionUID = 20250104L;
    
    protected String packageId;
    protected String encodedPackage;
    protected boolean disposed;

    public DescargaResponse(Instant satInstant, String statusCode, String message, String packageId, String encodedPackage) {
        this(satInstant, statusCode, message, packageId, encodedPackage, false);
    }
    
    protected DescargaResponse(Instant satInstant, String statusCode, String message, String packageId, boolean disposed) {
        this(satInstant, statusCode, message, packageId, null, disposed);
    }
    
    protected DescargaResponse(Instant satInstant, String statusCode, String message,
            String packageId, String encodedPackage, boolean disposed) {
        
        super(satInstant, statusCode, message);
        this.packageId = packageId;
        this.encodedPackage = nonBlank(encodedPackage);
        this.disposed = disposed;
    }
    
    protected final String nonBlank(String string) {
        return (string == null ? null : (string.isBlank() ? null : string));
    }
    
    public String getPackageId() {
        return packageId;
    }
    
    public String getEncodedPackage() {
        return encodedPackage;
    }
    
    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        if (isAccept()) {
            disposed = true;
        }
        encodedPackage = null;
    }
    
    
    @Override public boolean isAccept() {
        return (super.isAccept()
                && (disposed
                    || (encodedPackage != null && !encodedPackage.isBlank())));
    }

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
