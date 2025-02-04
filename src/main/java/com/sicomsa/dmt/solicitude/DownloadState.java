/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.09
 * 
 * Posible states of a download request solicitude.
 * 
 */
public abstract class DownloadState {
            
    public abstract StateValue getValue();

    public boolean isRequestable() {
        return false;
    }
    
    public boolean isVerifiable() {
        return false;
    }
    
    public boolean isDownloadable() {
        return false;
    }
    /**
     * 
     * @param conn
     * @param ctx
     * @throws SOAPException 
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if invalid state to invoke this method
     */
    public void requestDownload(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
        throw new IllegalStateException("invalid state for a download request");
    }
    
    /**
     * 
     * @param conn
     * @param ctx
     * @throws SOAPException 
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if invalid state to invoke this method
     */
    public void verifyRequest(SOAPConnection conn, DownloadContext ctx) throws SOAPException{
        throw new IllegalStateException("invalid state to verify request");
    }
    
    /**
     * 
     * @param conn
     * @param ctx
     * @throws SOAPException 
     * @throws IllegalArgumentException if conn is null
     * @throws NullPointerException if ctx is null
     * @throws IllegalStateException if invalid state to invoke this method
     */
    public void downloadOnlyOne(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
        throw new IllegalStateException("invalid state to download a package");
    }

    ///////////////////////////////////////////////////////////////////////////
    
    public static class New extends DownloadState {
            
        @Override public StateValue getValue() {
            return StateValue.NEW;
        }
        
        @Override public boolean isRequestable() {
            return true;
        }
        
        /**
         * 
         * @param conn
         * @param ctx
         * @throws SOAPException 
         * @throws IllegalArgumentException if conn is null
         * @throws NullPointerException if ctx is null
         */
        @Override
        public void requestDownload(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
            ctx.update(ctx.getClient().requestDownload(conn, ctx.getQuery()));
        }
  
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static class Accepted extends DownloadState {
          
        @Override public StateValue getValue() {
            return StateValue.ACCEPTED;
        }
        
        @Override public boolean isVerifiable() {
            return true;
        }
        /**
         * 
         * @param conn
         * @param ctx
         * @throws SOAPException 
         * @throws IllegalArgumentException if conn is null
         * @throws NullPointerException if ctx is null
         */
        @Override
        public void verifyRequest(SOAPConnection conn, DownloadContext ctx) throws SOAPException{
            ctx.update(ctx.getClient().verifyRequest(conn, ctx.getRequestId()));
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static class Delayed extends Accepted {
        
        @Override public StateValue getValue() {
            return StateValue.DELAYED;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static class Verified extends DownloadState {
                
        @Override public StateValue getValue() {
            return StateValue.VERIFIED;
        }
        
        @Override public boolean isDownloadable() {
            return true;
        }
        /**
         * 
         * @param conn
         * @param ctx
         * @throws SOAPException 
         * @throws IllegalArgumentException if conn is null
         * @throws NullPointerException if ctx is null
         */
        @Override
        public void downloadOnlyOne(SOAPConnection conn, DownloadContext ctx) throws SOAPException {
            ctx.update(ctx.getClient().download(conn, ctx.getNextDownloadablePackageId()));
        }
    }

}
