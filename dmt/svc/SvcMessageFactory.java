/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 
 */

package com.sicomsa.dmt.svc;


import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;

import java.time.InstantSource;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * 
 * Defines methods needed to build and sign messages.
 * 
 */
public interface SvcMessageFactory extends InstantSource {
    
    public MessageFactory getMessageFactory() throws SOAPException;
    
    public SvcSignatureFactory getSignatureFactory();
        
}
