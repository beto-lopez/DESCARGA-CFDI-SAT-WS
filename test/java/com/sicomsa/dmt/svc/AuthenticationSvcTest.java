/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.sicomsa.dmt.Authorization;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import java.util.logging.*;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.18
 *
 * 
 */
public class AuthenticationSvcTest {
    static AuthenticationSvc svc;
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    static DefaultMessageFactory factory;

    public AuthenticationSvcTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        factory = DefaultMessageFactory.newInstance();
        svc = new AuthenticationSvc(factory);
        LOG.setLevel(Level.ALL);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }
    
    /**
     * Test of parseResponse method, of class AuthenticationSvc.
     */
    @Test
    public void testParseReceivedMessage() throws Exception {
        System.out.println("parseReceivedMessage");
        Instant now = Instant.now();
        Authorization auth = svc.parseReceivedMessage(SOAPUtils.fromString(getValidResponse1()), now, null);
        System.out.println("valid1Auth="+auth);
        assertTrue(sameContent(auth, getExpectedValidAuth1(now)));
        Exception e = assertThrows(SvcParseException.class, ()-> {
            svc.parseReceivedMessage(SOAPUtils.fromString(this.getUnparseableResponse1()), now, null);
        });
        System.out.println("parse ex msg ="+e.getMessage());
        
        System.out.println("EXPECTING A WARNING...");
        e = assertThrows(SvcParseException.class, ()-> {
            svc.parseReceivedMessage(SOAPUtils.fromString(this.getInconsistentResponse1()), now, null);
        });
        assertEquals("unable to get consistent token", e.getMessage());
    }
    
    protected Authorization getExpectedValidAuth1(Instant instant) {
        Instant created = Instant.parse("2024-11-19T05:08:52.794Z");
        Instant expires = Instant.parse("2024-11-19T05:13:52.794Z");
        String token = "eyJhbGci...13830";
        return new Authorization(instant, created, expires, token);
    }
    
    protected boolean sameContent(Authorization a0, Authorization a1) {
        return (a0.getInstant().equals(a1.getInstant())
                && a0.getCreated().equals(a1.getCreated())
                && a0.getExpires().equals(a1.getExpires())
                && a0.getToken().equals(a1.getToken()));
    }
    
    protected String getValidResponse1() {
        return """
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" xmlns:u="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
            <s:Header>
            <o:Security xmlns:o="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" s:mustUnderstand="1">
            <u:Timestamp u:Id="_0">
            <u:Created>2024-11-19T05:08:52.794Z</u:Created>
            <u:Expires>2024-11-19T05:13:52.794Z</u:Expires>
            </u:Timestamp>
            </o:Security>
            </s:Header>
            <s:Body>
            <AutenticaResponse xmlns="http://DescargaMasivaTerceros.gob.mx">
            <AutenticaResult>eyJhbGci...13830</AutenticaResult>
            </AutenticaResponse>
            </s:Body>
            </s:Envelope>""";
    }

    protected String getInconsistentResponse1() {
        return """
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" xmlns:u="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
            <s:Header>
            <o:Security xmlns:o="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" s:mustUnderstand="1">
            <u:Timestamp u:Id="_0">
            <u:Created>2024-11-19T05:08:52.794Z</u:Created>
            <u:Expires>2024-11-19T05:13:52.794Z</u:Expires>
            </u:Timestamp>
            </o:Security>
            </s:Header>
            <s:Body>
            <AutenticaResponse xmlns="http://DescargaMasivaTerceros.gob.mx">
            <AutenticaResult></AutenticaResult>
            </AutenticaResponse>
            </s:Body>
            </s:Envelope>""";
    }
    protected String getUnparseableResponse1() {
        return getValidResponse1().replace("http://DescargaMasivaTerceros.gob.mx", "http://DescargaCuartos.gob.mx");
    }
    
}