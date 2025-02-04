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
 * @since 2024.10.26
 *
 *
 */
public class AuthorizationTest {

    static Instant satInstant;
    static Instant created;
    static Instant expires;
    static String token;
    static Authorization response;
    static Instant now = Instant.now();
    
    public AuthorizationTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        created = Instant.now();
        satInstant = created.plusMillis(100);
        expires = created.plusSeconds(60 * 5);
        token = "accepted";
        response = new Authorization(satInstant, created, expires, token);
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
     * Test of getReceived method, of class Authorization.
     */
    @Test
    public void testGetSatInstant() {
        System.out.println("getSatInstant");
        assertEquals(satInstant, response.getInstant());
    }

    /**
     * Test of getCreated method, of class Authorization.
     */
    @Test
    public void testGetCreated() {
        System.out.println("getCreated");
        assertEquals(created, response.getCreated());
    }

    /**
     * Test of getExpires method, of class Authorization.
     */
    @Test
    public void testGetExpires() {
        System.out.println("getExpires");
        assertEquals(expires, response.getExpires());
    }

    /**
     * Test of getResult method, of class Authorization.
     */
    @Test
    public void testGetResult() {
        System.out.println("getResult");
        assertEquals(token, response.getToken());
    }

    /**
     * Test of toString method, of class Authorization.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        System.out.println(response.toString());
    }
   
    @Test
    public void testConstructors() throws Exception {
        System.out.println("constructors");
        testInvalidParameters(()->{noReceived();}); 
        testInvalidRange(()->{noCreated();});
        testInvalidRange(()->{noExpired();});
        testInconsistency(()->{noToken();}); //INCONSISTENCY
        testInconsistency(()->{emptyToken();}); //INCONSISTENCY
        testInvalidRange(()->{invalidRange();});
        assertDoesNotThrow(()-> {validRange();});
                
       
    }
    public Authorization noReceived() {
        return new Authorization(null, now, now, "123");
    }
    public Authorization noCreated() {
        return new Authorization(now, null, now, "123");
    }
    public Authorization noExpired() {
        return new Authorization(now, now, null, "123");
}
    public Authorization noToken() {
        return new Authorization(now, now, now, null);
    }
    public Authorization emptyToken() {
        return new Authorization(now, now, now, "  ");
    }
    public Authorization validRange() {
        return new Authorization(now, now, now.plusMillis(1), "123");
    }
    public Authorization invalidRange() {
        return new Authorization(now, now, now.minusMillis(1), "123");
    }

    public void testInconsistency(Executable ex) throws Exception {
        System.out.println("testInconsistency");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        System.out.println(e.getMessage());
        assertEquals(e.getMessage(), "can not have authorization without token");    
    }
    public void testInvalidParameters(Executable ex) throws Exception {
        System.out.println("testInvalidParameters");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        assertEquals(e.getMessage(), "satinstant is required");
    }
    public void testInvalidRange(Executable ex) throws Exception {
        System.out.println("testInvalidRange");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        System.out.println(e.getMessage());
        assertEquals(e.getMessage(), "invalid token instant range");
    }
    
    /**
     * Test of isConsistent method, of class Authorization.
     */
    @Test
    public void testIsConsistent_String() {
        System.out.println("isConsistent Token");
        assertTrue(Authorization.isConsistent("abc"));
        assertFalse(Authorization.isConsistent(null));
        assertFalse(Authorization.isConsistent(" "));
    }

    /**
     * Test of isConsistent method, of class Authorization.
     */
    @Test
    public void testIsConsistent_Instant_Instant() {
        System.out.println("isConsistent Period");
        assertTrue(Authorization.isConsistent(created, created.plusMillis(1)));
        assertFalse(Authorization.isConsistent(created, created));
        assertFalse(Authorization.isConsistent(created, created.minusMillis(1)));
        assertFalse(Authorization.isConsistent(created, null));
        assertFalse(Authorization.isConsistent(null, created));
    }

    /**
     * Test of getInstant method, of class Authorization.
     */
    @Test
    public void testGetInstant() {
        System.out.println("getInstant");
        assertEquals(satInstant, response.getInstant());
    }

    /**
     * Test of getToken method, of class Authorization.
     */
    @Test
    public void testGetToken() {
        System.out.println("getToken");
        assertEquals(token, response.getToken());
    }

    /**
     * Test of wrapp method, of class Authorization.
     */
    @Test
    public void testWrapp() {
        System.out.println("wrapp");
        String wrapped = "WRAP access_token=\""+token+"\"";
        assertEquals(wrapped, Authorization.wrapp(token));
    }
   

}