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
import org.junit.jupiter.api.function.Executable;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.10.27
 *
 *
 */
public class SatResponseTest {

    static Instant instant;
    static String sts;
    static String message;
    static SatResponse response;
    static SatResponse unaccepted;
    
    public static class SatResultImpl extends SatResponse {
        
        private static final long serialVersionUID = 1000L;
        public SatResultImpl(Instant instant, String sts, String message) {
            super(instant, sts, message);
        }
      
    }
    
    public SatResponseTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        instant = Instant.now();
        sts = SatResponse.STATUS_CODE_ACCEPT;
        message = "message content";
        response = new SatResultImpl(instant, sts, message);
        unaccepted = new SatResultImpl(instant, "888", message);
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
     * Test of getStatusCode method, of class SatResponse.
     */
    @Test
    public void testGetStatusCode() {
        System.out.println("getStatusCode");
        assertEquals(sts, response.getStatusCode());
    }

    /**
     * Test of getMessage method, of class SatResponse.
     */
    @Test
    public void testGetMessage() {
        System.out.println("getMessage");
        assertEquals(message, response.getMessage());
    }

    /**
     * Test of getSatInstant method, of class SatResponse.
     */
    @Test
    public void testGetInstant() {
        System.out.println("getInstant");
        assertEquals(instant, response.getInstant());
    }


    @Test
    public void testConstructors() throws Exception {
        System.out.println("constructors");
        testIllegalArgument(()->{noInstant();});
        assertDoesNotThrow(()-> {valid();});       
        assertDoesNotThrow(()-> {valid2();});       
    }
    
    public SatResponse noInstant() {
        return new SatResultImpl(null, "", "");
    }
    
    public SatResponse valid() {
        return new SatResultImpl(Instant.now(), "", "");
    }
    public SatResponse valid2() {
        return new SatResultImpl(Instant.now(), null, null);
    }

    public void testIllegalArgument(Executable ex) throws Exception {
        System.out.println("testIllegalArgument");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        assertEquals(e.getMessage(), "sat instant required");
    }

    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsAccept() {
        System.out.println("isAccept");
        assertTrue(response.isAccept());
        assertFalse(unaccepted.isAccept());
        assertEquals(response.isAccept(), SatResponse.STATUS_CODE_ACCEPT.equals(response.getStatusCode()));
        assertEquals(unaccepted.isAccept(), SatResponse.STATUS_CODE_ACCEPT.equals(unaccepted.getStatusCode()));
    }


}