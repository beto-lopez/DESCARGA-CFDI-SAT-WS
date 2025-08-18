/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.util;

import com.sicomsa.dmt.ExtendedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *<code>FolioQueryBuilder</code> test class.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.9
 * @since       1.5 
 */
public class FolioQueryBuilderTest {
    
    public FolioQueryBuilderTest() {
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
     * Test of setFolio method, of class FolioQueryBuilder.
     */
    @Test
    public void testSetFolio() {
        System.out.println("setFolio");
        String folio = "alkjsdhflaksdjh";
        FolioQueryBuilder instance = new FolioQueryBuilder();
        ExtendedQuery query = instance.build();
        assertNull(query.getFolio());
        query = instance.setFolio(folio).build();
        assertEquals(folio, query.getFolio());
    }

    /**
     * Test of setRfcSolicitante method, of class FolioQueryBuilder.
     */
    @Test
    public void testSetRfcSolicitante() {
        System.out.println("setRfcSolicitante");
        String rfc = "XEXX010101000";
        FolioQueryBuilder instance = new FolioQueryBuilder();
        ExtendedQuery query = instance.build();
        assertNull(query.getRfcSolicitante());
        query = instance.setRfcSolicitante(rfc).build();
        assertEquals(rfc, query.getRfcSolicitante());
    }

    /**
     * Test of build method, of class FolioQueryBuilder.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        String folio = "alkjsdhflaksdjh";
        String rfc = "XEXX010101000";
        FolioQueryBuilder instance = new FolioQueryBuilder();
        ExtendedQuery query = instance.build();
        assertNull(query.getFolio());
        assertNull(query.getRfcSolicitante());
        query = instance.setFolio(folio).setRfcSolicitante(rfc).build();
        assertEquals(folio, query.getFolio());
        assertEquals(rfc, query.getRfcSolicitante());
        
        String newFolio = "alskdjfxxxxx";
        query = instance.setFolio(newFolio).build();
        assertEquals(newFolio, query.getFolio());
        assertEquals(rfc, query.getRfcSolicitante());
        
        String newRfc = "XAXX010101000";
        query = instance.setRfcSolicitante(newRfc).build();
        assertEquals(newFolio, query.getFolio());
        assertEquals(newRfc, query.getRfcSolicitante());
    }
    
}
