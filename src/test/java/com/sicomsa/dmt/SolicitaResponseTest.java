/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.10.27
 *
 *
 */
public class SolicitaResponseTest {

    static Instant instant;
    static String sts;
    static String message;
    static String requestId;
    static SolicitaResponse validResponse;
    static SolicitaResponse nullsResponse;
    static SolicitaResponse blanksResponse;
    static SolicitaResponse validBlankResponse;
    
    public SolicitaResponseTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        instant = Instant.now();
        sts = SatResponse.STATUS_CODE_ACCEPT;
        message = "message content";
        requestId = "requestId content";
        validResponse = new SolicitaResponse(instant, sts, message, requestId);
        nullsResponse = new SolicitaResponse(instant, null, null, null);
        blanksResponse = new SolicitaResponse(instant, "", "", "  ");
        validBlankResponse = new SolicitaResponse(instant, sts, "", "  ");
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
     * Test of getRequestId method, of class SolicitaResponse.
     */
    @Test
    public void testGetRequestId() {
        System.out.println("getRequestId");
        assertEquals(requestId, validResponse.getRequestId());     
        assertEquals(null, nullsResponse.getRequestId());
    }
    
    @Test
    public void testSuperGetters() {
        System.out.println("superGetters");
        assertEquals(instant, validResponse.getInstant());
        assertEquals(sts, validResponse.getStatusCode());
        assertEquals(message, validResponse.getMessage());
    }

    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsAccept() {
        System.out.println("isAccept");
        assertTrue(validResponse.isAccept());
        assertFalse(nullsResponse.isAccept());
        assertFalse(blanksResponse.isAccept());
        assertEquals(validResponse.isAccept(), SatResponse.STATUS_CODE_ACCEPT.equals(validResponse.getStatusCode()));
        assertEquals(SatResponse.STATUS_CODE_ACCEPT, validBlankResponse.getStatusCode());
        assertFalse(validBlankResponse.isAccept());
    }
    

    /**
     * Test of toString method, of class SolicitaResponse.
     */
    @Test
    public void testToString() {
        System.out.println("toString:"+validResponse.toString());
        System.out.println("toString, with nullId:"+nullsResponse.toString());
    }
    

}