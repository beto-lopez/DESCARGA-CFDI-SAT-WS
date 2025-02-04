/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import jakarta.xml.soap.SOAPConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.17
 *
 *
 */
public class DownloadStateTest {

    public static final DownloadState NEW_STATE = new DownloadState.New();
    public static final DownloadState ACCEPTED_STATE = new DownloadState.Accepted();
    public static final DownloadState DELAYED_STATE = new DownloadState.Delayed();
    public static final DownloadState VERIFIED_STATE = new DownloadState.Verified();
    
    public DownloadStateTest() {
    }

    @BeforeAll
    public static void setUpClass() {
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
     * Test of getValue method, of class DownloadState.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        assertEquals(StateValue.NEW, NEW_STATE.getValue());
        assertEquals(StateValue.ACCEPTED, ACCEPTED_STATE.getValue());
        assertEquals(StateValue.DELAYED, DELAYED_STATE.getValue());
        assertEquals(StateValue.VERIFIED, VERIFIED_STATE.getValue());
    }

    /**
     * Test of isRequestable method, of class DownloadState.
     */
    @Test
    public void testIsRequestable() {
        System.out.println("isRequestable");
        assertTrue(NEW_STATE.isRequestable());
        assertFalse(ACCEPTED_STATE.isRequestable());
        assertFalse(DELAYED_STATE.isRequestable());
        assertFalse(VERIFIED_STATE.isRequestable());
    }

    /**
     * Test of isVerifiable method, of class DownloadState.
     */
    @Test
    public void testIsVerifiable() {
        System.out.println("isVerifiable");
        assertFalse(NEW_STATE.isVerifiable());
        assertTrue (ACCEPTED_STATE.isVerifiable());
        assertTrue (DELAYED_STATE.isVerifiable());
        assertFalse(VERIFIED_STATE.isVerifiable());
    }

    /**
     * Test of isDownloadable method, of class DownloadState.
     */
    @Test
    public void testIsDownloadable() {
        System.out.println("isDownloadable");
        assertFalse(NEW_STATE.isDownloadable());
        assertFalse(ACCEPTED_STATE.isDownloadable());
        assertFalse(DELAYED_STATE.isDownloadable());
        assertTrue (VERIFIED_STATE.isDownloadable());
    }

    /**
     * Test of requestDownload method, of class DownloadState.
     */
    @Disabled("we won´t test since it only forwards to ctx. Will test in ctxImpl")
    @Test
    public void testRequestDownload() throws Exception {
        System.out.println("requestDownload");
        SOAPConnection conn = null;
        DownloadContext ctx = null;
        DownloadState instance = new DownloadStateImpl();
        instance.requestDownload(conn, ctx);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of verifyRequest method, of class DownloadState.
     */
    @Disabled("we won´t test since it only forwards to ctx. Will test in ctxImpl")
    @Test
    public void testVerifyRequest() throws Exception {
        System.out.println("verifyRequest");
        SOAPConnection conn = null;
        DownloadContext ctx = null;
        DownloadState instance = new DownloadStateImpl();
        instance.verifyRequest(conn, ctx);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of downloadOnlyOne method, of class DownloadState.
     */
    @Disabled("we won´t test since it only forwards to ctx. Will test in ctxImpl")
    @Test
    public void testDownloadOnlyOne() throws Exception {
        System.out.println("downloadOnlyOne");
        SOAPConnection conn = null;
        DownloadContext ctx = null;
        DownloadState instance = new DownloadStateImpl();
        instance.downloadOnlyOne(conn, ctx);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class DownloadStateImpl extends DownloadState {
        @Override public StateValue getValue() {
            return null;
        }
    }
}