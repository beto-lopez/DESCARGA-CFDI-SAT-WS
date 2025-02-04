/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;


import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

import java.util.*;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.12.14
 *
 *
 */
public class QueryTest {
    
    static final Query EMPTY_QUERY = new EmptyQuery();

    public QueryTest() {
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
    
    public static Query getEmptyQuery() {
        return EMPTY_QUERY;
    }
    
    public static class EmptyQuery implements Query {
        private static final long serialVersionUID = 20241214L;
        
        @Override public Set<String> getRfcReceptores() {
            return Collections.emptySet();
        }
        @Override public LocalDateTime getFechaInicial() {
            return null;
        }
        @Override public LocalDateTime getFechaFinal(){
            return null;
        }
        @Override public String getRfcEmisor(){
            return null;
        }
        @Override public String getRfcSolicitante(){
            return null;
        }
        @Override public String getTipoSolicitud(){
            return null;
        }
        @Override public String getTipoComprobante(){
            return null;
        }
        @Override public String getEstadoComprobante(){
            return null;
        }
        @Override public String getRfcTerceros(){
            return null;
        }
        @Override public String getComplemento(){
            return null;
        }
        @Override public String getFolio(){
            return null;
        }
        @Override public Iterator<String> getAttributes(){
            return Collections.emptyIterator();
        }
        @Override public String getAttributeValue(String name){
            return null;
        }
    }
    
    public static void assertSameContent(Query q0, Query q1) {
        if (q0 == null) {
            assertNull(q1);
            return;
        }
        assertSameQueryDate(q0.getFechaInicial(), q1.getFechaInicial());
        assertSameQueryDate(q0.getFechaFinal(), q1.getFechaFinal());
        assertEquals(q0.getRfcEmisor(), q1.getRfcEmisor());
        assertEquals(q0.getRfcSolicitante(), q1.getRfcSolicitante());
        assertEquals(q0.getTipoSolicitud(), q1.getTipoSolicitud());
        assertEquals(q0.getTipoComprobante(), q1.getTipoComprobante());
        assertEquals(q0.getEstadoComprobante(), q1.getEstadoComprobante());
        assertEquals(q0.getRfcTerceros(), q1.getRfcTerceros());
        assertEquals(q0.getComplemento(), q1.getComplemento());
        assertEquals(q0.getFolio(), q1.getFolio());
        Set<String> set0 = new TreeSet<>();
        Set<String> set1 = new TreeSet<>();
        q0.getAttributes().forEachRemaining(name-> {
            set0.add(name);
            assertEquals(q0.getAttributeValue(name), q1.getAttributeValue(name));
        });
        q1.getAttributes().forEachRemaining(name-> {
            set1.add(name);
        });
        assertEquals(set0.toString(), set1.toString());
    }
    
    public static void assertSameQueryDate(LocalDateTime d0, LocalDateTime d1) {
        if (d0 == null) {
            assertNull(d1);
        }
        else {
            assertEquals(d0.getYear(), d1.getYear());
            assertEquals(d0.getMonth(), d1.getMonth());
            assertEquals(d0.getDayOfMonth(), d1.getDayOfMonth());
            assertEquals(d0.getHour(), d1.getHour());
            assertEquals(d0.getMinute(), d1.getMinute());
            assertEquals(d0.getSecond(), d1.getSecond());
        }
    }
   
}