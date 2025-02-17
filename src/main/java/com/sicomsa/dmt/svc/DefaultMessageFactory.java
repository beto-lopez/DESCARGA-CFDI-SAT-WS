/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import java.time.InstantSource;
import java.time.Clock;

/**
 * Default implementation of SvcMessageFactory to provide methods needed to build
 * and sign messages.
 * <p>Uses a <code>java.time.Clock.systemUTC()</code> to implement the InstantSource;
 * jakarta's MessageFactory to create messages; and an instance of SvcSignatureFactory
 * that implements several SOAP signature related methods needed to sign messages.</p>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.11.10
 * @since 1.0
 * 
 */
public class DefaultMessageFactory implements SvcMessageFactory {
    
    /**
     * InstantSource to use
     */
    protected InstantSource clock;
    
    /**
     * MessageFactory to use
     */
    protected MessageFactory messageFactory;
    
    /**
     * Signature factory
     */
    protected SvcSignatureFactory signatureFactory;
    
    /**
     * Returns a new DefaultMessageFactory with a default signature factory
     */
    public DefaultMessageFactory() {
        this(SvcSignatureFactory.getInstance());
    }
    
    /**
     * Returns a new DefaultMessageFactory with the specified signature factory.
     * 
     * @param factory the signature factory to use
     * @throws IllegalArgumentException if factory is null
     */
    public DefaultMessageFactory(SvcSignatureFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory required");
        }
        this.clock = Clock.systemUTC();
        this.signatureFactory = factory;
    }
    
    /**
     * Returns a default implementation of DefaultMessageFactory
     * 
     * @return a default implementation of DefaultMessageFactory
     */
    public static DefaultMessageFactory newInstance() {
        return new DefaultMessageFactory();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    //// SvcMessageFactory implementation
    
    @Override public java.time.Instant instant() {
        return getInstantSource().instant();
    }   
    
    @Override public synchronized MessageFactory getMessageFactory() throws SOAPException {
        if (messageFactory == null) {
            messageFactory = MessageFactory.newInstance();
        }
        return messageFactory;
    }
    
    @Override public SvcSignatureFactory getSignatureFactory() {
        return signatureFactory;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Returns an <code>InstantSource</code>
     * 
     * @return an <code>InstantSource</code>
     */
    public synchronized InstantSource getInstantSource() {
        return clock;
    }
    
    /**
     * Sets the <code>InstantSource</code> this factory will use.
     * 
     * @param source the <code>InstantSource</code>
     * @throws IllegalArgumentException if source is null
     */
    public synchronized void setInstantSource(InstantSource source) {
        if (source == null) {
            throw new IllegalArgumentException("invalid source");
        }
        this.clock = source;
    }
}
