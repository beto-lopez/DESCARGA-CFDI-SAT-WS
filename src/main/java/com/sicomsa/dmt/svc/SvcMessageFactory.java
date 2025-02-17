/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;

import java.time.InstantSource;

/**
 * Defines methods needed to build and sign messages.
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.22
 * @since 1.0
 * 
 * 
 */
public interface SvcMessageFactory extends InstantSource {
    
    /**
     * Returns a <code>MessageFactory</code>
     * 
     * @return <code>MessageFactory</code>
     * @throws SOAPException if there were SOAP related problems
     */
    public MessageFactory getMessageFactory() throws SOAPException;
    
    /**
     * Returns the <code>SvcSignatureFactory</code>
     * 
     * @return the <code>SvcSignatureFactory</code>
     */
    public SvcSignatureFactory getSignatureFactory();
        
}
