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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.10
 * 
 * Default implementation of SvcMessageFactory.
 * 
 * Uses a Clock.systemUTC() to implement the InstantSource; jakarta's MessageFactory
 * to create messages; and an instance of SvcSignatureFactory that implements
 * several SOAP signature related methods needed to sign messages.
 * 
 */
public class DefaultMessageFactory implements SvcMessageFactory {
    
    protected InstantSource clock;
    protected MessageFactory messageFactory;
    protected SvcSignatureFactory signatureFactory;
    
    public DefaultMessageFactory() {
        this(SvcSignatureFactory.getInstance());
    }
    public DefaultMessageFactory(SvcSignatureFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory required");
        }
        this.clock = Clock.systemUTC();
        this.signatureFactory = factory;
    }
    
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
    
    
    public synchronized InstantSource getInstantSource() {
        return clock;
    }
    
    public synchronized void setInstantSource(InstantSource source) {
        if (source == null) {
            throw new IllegalArgumentException("invalid source");
        }
        this.clock = source;
    }
}
