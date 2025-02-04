/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.10.27
 *
 *
 */
public class DescargaResponseTest {
    
    static Instant instant;
    static String sts;
    static String message;
    static String pid;
    static String thepackage;
    static DescargaResponse validResponse, validBlankResponse, rejectedResponse, disposedResponse;   

    public DescargaResponseTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        instant = Instant.now();
        sts = SatResponse.STATUS_CODE_ACCEPT;
        pid = "packageid-abc";
        message = "message content";
        thepackage = "package content";
        validResponse = new DescargaResponse(instant, sts, message, pid, thepackage);
        validBlankResponse = new DescargaResponse(instant, sts, message, "", "  ");
        rejectedResponse = new DescargaResponse(instant, "8888", message, pid, thepackage);
        disposedResponse = new DescargaResponse(instant, "5555", message, pid, true);
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

    @Test
    public void testSuperGetters() {
        System.out.println("superGetters");
        assertEquals(instant, validResponse.getInstant());
        assertEquals(sts, validResponse.getStatusCode());
        assertEquals(message, validResponse.getMessage());
    }
    /**
     * Test of getEncodedPackage method, of class DescargaResponse.
     */
    @Test
    public void testGetEncodedPackage() {
        System.out.println("getEncodedPackage");
        assertEquals(thepackage, validResponse.getEncodedPackage());
    }

    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsAccept() {
        System.out.println("isAccept");
        assertTrue(validResponse.isAccept());
        assertFalse(validBlankResponse.isAccept());
        assertFalse(rejectedResponse.isAccept());
        assertEquals(validResponse.isAccept(), SatResponse.STATUS_CODE_ACCEPT.equals(validResponse.getStatusCode()));
        assertEquals(SatResponse.STATUS_CODE_ACCEPT, validBlankResponse.getStatusCode());
        assertFalse(validBlankResponse.isAccept());
    }
    /**
     * Test of toString method, of class DescargaResponse.
     */
    @Test
    public void testToString() {
        System.out.println("toString:"+validResponse.toString());
        System.out.println("toString with blank package:"+validBlankResponse.toString());
        System.out.println("toString with rejected response:"+rejectedResponse.toString());
    }

    /**
     * Test of nonBlank method, of class DescargaResponse.
     */
    @Test
    public void testNonBlank() {
        System.out.println("nonBlank");
        String string = "  ";
        DescargaResponse instance = validResponse;
        String expResult = null;
        String result = instance.nonBlank(string);
        assertEquals(expResult, result);
        
        string = " x ";
        expResult = string;
        result = instance.nonBlank(string);
        assertEquals(expResult, result);
        
        string = null;
        result = instance.nonBlank(string);
        assertNull(result);
    }

    /**
     * Test of getPackageId method, of class DescargaResponse.
     */
    @Test
    public void testGetPackageId() {
        System.out.println("getPackageId");
        DescargaResponse instance = validResponse;
        String expResult = pid;
        String result = instance.getPackageId();
        assertEquals(expResult, result);
    }

    /**
     * Test of isDisposed method, of class DescargaResponse.
     */
    @Test
    public void testIsDisposed() {
        System.out.println("isDisposed");
        assertFalse(validResponse.isDisposed());
        assertTrue(disposedResponse.isDisposed());
        DescargaResponse instance = validResponse;
        boolean expResult = false;
        boolean result = instance.isDisposed();
        assertEquals(expResult, result);
        
        instance = new DescargaResponse(instant, sts, message, pid, thepackage);
        assertFalse(instance.isDisposed());
        instance.dispose();
        assertTrue(instance.isDisposed());
    }

    /**
     * Test of dispose method, of class DescargaResponse.
     */
    @Test
    public void testDispose() {
        System.out.println("dispose");
        DescargaResponse instance = new DescargaResponse(instant, sts, message, pid, thepackage);
        assertFalse(instance.isDisposed());
        assertEquals(thepackage, instance.getEncodedPackage());
        instance.dispose();
        assertTrue(instance.isDisposed());
        assertNull(instance.getEncodedPackage());
    }

}