/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

/**
 * Abstract class representing the state of a download request solicitude.
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.09
 * @since 1.0
 * 
 */
public abstract class DownloadState {
            
    /**
     * Returns the value of this state.
     * 
     * @return the value of this state
     */
    public abstract StateValue getValue();

    /**
     * Returns true if this state is able to call the web service to
     * request a download.
     * 
     * @return true if this state is able to call the web service to request
     *         a download
     */
    public boolean isRequestable() {
        return false;
    }
    
    /**
     * Returns true if this state is able to call the verify request service.
     * 
     * @return true if this state is able to call the verify request service
     */
    public boolean isVerifiable() {
        return false;
    }
    
    /**
     * Returns true if this state is able to call the download package service.
     * 
     * @return true if this state is able to call the download package service
     */
    public boolean isDownloadable() {
        return false;
    }
    
    /**
     * Calls the web service to request a download using and updating the
     * specified download conext.
     * 
     * @param conn connection to use
     * @param ctx the download context of the download request
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if this state is not able to request downloads
     */
    public void requestDownload(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
        throw new IllegalStateException("invalid state for a download request");
    }
    
    /**
     * Calls the web service to verify a download request using and updating the
     * specified download conext.
     * 
     * @param conn connection to use
     * @param ctx the download context of the download request
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if this state is not able to verify download
     *         requests
     */
    public void verifyRequest(SOAPConnection conn, DownloadContext ctx) throws SOAPException{
        throw new IllegalStateException("invalid state to verify request");
    }
    
    /**
     * Calls the web service to download a package using and updating the
     * specified download conext.
     * 
     * @param conn connection to use
     * @param ctx the download context of the download request
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if this state is not able to download packages
     */
    public void downloadOnlyOne(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
        throw new IllegalStateException("invalid state to download a package");
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Concrete implementation of <code>DownloadState</code> that represents the
     * initial state of a request that has not been sent to the service. This
     * state is able to request a download.
     */
    public static class New extends DownloadState {
        
        /**
         * Returns <code>StateValue.NEW</code>
         * 
         * @return <code>StateValue.NEW</code>
         */
        @Override public StateValue getValue() {
            return StateValue.NEW;
        }
        
        /**
         * Returns true
         * 
         * @return true
         */
        @Override public boolean isRequestable() {
            return true;
        }
        
        @Override
        public void requestDownload(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
            ctx.update(ctx.getClient().requestDownload(conn, ctx.getQuery()));
        }
  
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Concrete implementation of <code>DownloadState</code> that represents the
     * accepted state of a download request. In this state, the request has been
     * accepted but has not been yet verified.
     * <p>This accepted state is able to verify a request.</p>
     */
    public static class Accepted extends DownloadState {
        
        /**
         * Returns <code>StateValue.ACCEPTED</code>
         * 
         * @return <code>StateValue.ACCEPTED</code>
         */
        @Override public StateValue getValue() {
            return StateValue.ACCEPTED;
        }
        
        /**
         * Returns true
         * 
         * @return true
         */
        @Override public boolean isVerifiable() {
            return true;
        }
        
        @Override
        public void verifyRequest(SOAPConnection conn, DownloadContext ctx) throws SOAPException{
            ctx.update(ctx.getClient().verifyRequest(conn, ctx.getRequestId()));
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Implementation that overrides <code>DownloadState.Accepted</code> to
     * represent the <b>delayed state</b>, which is when the verify request service
     * has been called and the web service has responded that the verification
     * is not yet ready.
     * <p>This state, as the accepted state it overrides, is able to verify a request.</p>
     */
    
    public static class Delayed extends Accepted {
        
        /**
         * Returns <code>StateValue.DELAYED</code>
         * 
         * @return <code>StateValue.DELAYED</code>
         */
        @Override public StateValue getValue() {
            return StateValue.DELAYED;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Concrete implementation of <code>DownloadState</code> that represents the
     * verified state of a download request. In this state, the request has been
     * verified and contains the package identifiers to download packages from
     * the web service.
     * <p>This accepted state is able to download packages.</p>
     */
    public static class Verified extends DownloadState {
          
        /**
         * Returns <code>StateValue.VERIFIED</code>
         * 
         * @return <code>StateValue.VERIFIED</code>
         */
        @Override public StateValue getValue() {
            return StateValue.VERIFIED;
        }
        
        /**
         * Returns true
         * 
         * @return true
         */
        @Override public boolean isDownloadable() {
            return true;
        }
        
        @Override
        public void downloadOnlyOne(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
            ctx.update(ctx.getClient().download(conn, ctx.getNextDownloadablePackageId()));
        }
    }

}
