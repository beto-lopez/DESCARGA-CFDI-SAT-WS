/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.util;

import com.sicomsa.dmt.QueryType;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * <code>ExtendedQueryMap</code> test.
 * 
 * @version     2025.6.9
 * @since       1.5 
 */
public class ExtendedQueryMapTest {
    
    public ExtendedQueryMapTest() {
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
     * Test of getType method, of class ExtendedQueryMap.
     */
    @Test
    public void testGetType() {
        QueryMap.Builder builder = new QueryMap.Builder();
        System.out.println("getType");
        for (QueryType type : QueryType.values()) {
            ExtendedQueryMap instance = new ExtendedQueryMap(builder, type);
            QueryType expResult = type;
            QueryType result = instance.getType();
            assertEquals(expResult, result);
        }
        Exception e = assertThrows(IllegalArgumentException.class, ()->{nullType();});
        assertEquals(e.getMessage(), "invalid type");
    }
    
    
    protected ExtendedQueryMap nullType() {
        return new ExtendedQueryMap(new QueryMap.Builder(), null);
    }
    
    /**
     * Test of getRfcReceptor method, of class ExtendedQueryMap.
     */
    @Test
    public void testGetRfcReceptor() {
        System.out.println("getRfcReceptor");
        QueryMap.Builder builder = new QueryMap.Builder();
        ExtendedQueryMap instance = new ExtendedQueryMap(builder, QueryType.RECIBIDOS);
        assertNull(instance.getRfcReceptor());
        String expResult = "alsdkjaekwekm";
        instance = new ExtendedQueryMap(builder, QueryType.RECIBIDOS);
        instance.setRfcReceptor(expResult);
        String result = instance.getRfcReceptor();
        assertEquals(expResult.toUpperCase(), result);
    }
    

    /**
     * Test of setRfcReceptor method, of class ExtendedQueryMap.
     */
    @Test
    public void testSetRfcReceptor() {
        System.out.println("setRfcReceptor");
        String rfc = "XAXX01010100W";
        
        QueryMap.Builder builder = new QueryMap.Builder();
        ExtendedQueryMap instance = new ExtendedQueryMap(builder, QueryType.RECIBIDOS);
        assertNull(instance.getRfcReceptor());
        
        instance.setRfcReceptor(rfc);
        assertEquals(rfc, instance.getRfcReceptor());
    }

    /**
     * Test of addAllRfcReceptors method, of class ExtendedQueryMap.
     */
    @Test
    public void testAddAllRfcReceptors() {
        System.out.println("addAllRfcReceptors");
        
        QueryMap.Builder builder = new QueryMap.Builder();
        ExtendedQueryMap instance = new ExtendedQueryMap(builder, QueryType.EMITIDOS);
        Set<String> rfcs = instance.getRfcReceptores();
        assertTrue(rfcs.isEmpty());
          
        Exception e = assertThrows(NullPointerException.class, ()-> {
            addReceptores(instance, null);
        });
        
        System.out.println(e.getMessage());
        
        rfcs = new HashSet<>(List.of("A", "B", "C", "D"));
        addReceptores(instance, rfcs);
                
        assertContainsAll(rfcs, instance.getRfcReceptores());
    }
    
    /**
     * Test of clearRfcReceptors method, of class ExtendedQueryMap.
     */
    @Test
    public void testClearRfcReceptors() {
        System.out.println("clearRfcReceptors");
        QueryMap.Builder builder = new QueryMap.Builder();
        ExtendedQueryMap instance = new ExtendedQueryMap(builder, QueryType.EMITIDOS);
        HashSet<String> newRfcs = new HashSet<>(List.of("a", "b", "c", "d"));
        System.out.println("newRfcs="+newRfcs);
        addReceptores(instance, newRfcs);
        assertFalse(instance.getRfcReceptores().isEmpty());
        
        instance.clearRfcReceptors();
        assertTrue(instance.getRfcReceptores().isEmpty());
    }
    
    protected void addReceptores(ExtendedQueryMap map, Collection<String> collection) {
        System.out.println("addReceptores:"+collection);
        map.addAllRfcReceptors(collection);
    }
    
    protected void assertContainsAll(Collection<String> c1, Collection<String> c2) {
        System.out.println("assertContainsAll");
        System.out.println(c1);
        System.out.println(c2);
        assertTrue(c1.containsAll(c2));
        assertTrue(c2.containsAll(c1));
    }
    
    
}
