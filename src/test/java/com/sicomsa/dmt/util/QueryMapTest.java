/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;


import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.List;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.sicomsa.dmt.QueryTest;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.10.22
 *
 *
 */
public class QueryMapTest {
    static final LocalDateTime T_FEC_INI = LocalDateTime.now();
    static final LocalDateTime T_FEC_FIN = T_FEC_INI.plusDays(2);
    static final String T_EMISOR = "_EMISOR";
    static final String T_SOLICITANTE = "_SOLICITANTE";
    static final String T_TIPO_SOLICIT = "_tipoSolicitud";
    static final String T_TIPO_COMPROBANTE = "_tipoComprobante";
    static final String T_EDO_COMPROBANTE = "_edoComprobante";
    static final String T_RFC_TERCEROS = "_RFC_TERCEROS";
    static final String T_COMPLEMENTO = "_complemento";
    static final String T_FOLIO = "_folio";
    
    protected QueryMap emptyQuery;
    protected  QueryMap fullQuery;
    protected String[] receptoresArray;
    protected  Set<String> receptoresSet;

    public QueryMapTest() {
        receptoresArray = new String[] {
          "_RECEPTOR1","_RECEPTOR2","_RECEPTOR3","_RECEPTOR4","_RECEPTOR5","_RECEPTOR6" //i know
        };
        receptoresSet = Set.of(receptoresArray);
        emptyQuery = new QueryMap.Builder().build();
        fullQuery  = makeFullQuery();
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
    
    public QueryMap getFullQuery() {
        return fullQuery;
    }
    
    public QueryMap getEmptyQuery() {
        return emptyQuery;
    }
    
    
    private QueryMap makeFullQuery() {
        return new QueryMap.Builder()
                .addReceptor(receptoresArray[0])
                .addReceptor(receptoresArray[1])
                .addReceptor(receptoresArray[2])
                .addReceptores(List.of(receptoresArray[3], receptoresArray[4], receptoresArray[5])) //i know, i know
                .setFechaInicial(T_FEC_INI)
                .setFechaFinal(T_FEC_FIN)
                .setRfcEmisor(T_EMISOR)
                .setRfcSolicitante(T_SOLICITANTE)
                .setTipoSolicitud(T_TIPO_SOLICIT)
                .setTipoComprobante(T_TIPO_COMPROBANTE)
                .setEstadoComprobante(T_EDO_COMPROBANTE)
                .setRfcTerceros(T_RFC_TERCEROS)
                .setComplemento(T_COMPLEMENTO)
                .setFolio(T_FOLIO)
                .build();
    }

    /**
     * Test of getRfcReceptores method, of class QueryMap.
     */
    @Test
    public void testGetRfcReceptores() {
        System.out.println("getRfcReceptores");
        QueryMap instance = new QueryMap.Builder().addReceptores(receptoresSet).build();
        assertSameSet(receptoresSet, instance.getRfcReceptores());
        assertSameSet(java.util.Collections.emptySet(), emptyQuery.getRfcReceptores());
    }
    
    protected void assertSameSet(Set<String> s0, Set<String> s1) {
        assertEquals(s0.size(), s1.size());
        s0.forEach(item-> {
            assertTrue(s1.contains(item));
        });
    }

    /**
     * Test of getFechaInicial method, of class QueryMap.
     */
    @Test
    public void testGetFechaInicial() {
        System.out.println("getFechaInicial");
        assertEquals(null, emptyQuery.getFechaInicial());
        assertEquals(T_FEC_INI, fullQuery.getFechaInicial());
    }

    /**
     * Test of getFechaFinal method, of class QueryMap.
     */
    @Test
    public void testGetFechaFinal() {
        System.out.println("getFechaFinal");
        assertEquals(null, emptyQuery.getFechaFinal());
        assertEquals(T_FEC_FIN, fullQuery.getFechaFinal());
    }

    /**
     * Test of getRfcEmisor method, of class QueryMap.
     */
    @Test
    public void testGetRfcEmisor() {
        System.out.println("getRfcEmisor");
        assertEquals(null, emptyQuery.getRfcEmisor());
        assertEquals(T_EMISOR, fullQuery.getRfcEmisor());
    }

    /**
     * Test of getRfcSolicitante method, of class QueryMap.
     */
    @Test
    public void testGetRfcSolicitante() {
        System.out.println("getRfcSolicitante");
        assertEquals(null, emptyQuery.getRfcSolicitante());
        assertEquals(T_SOLICITANTE, fullQuery.getRfcSolicitante());
    }

    /**
     * Test of getTipoSolicitud method, of class QueryMap.
     */
    @Test
    public void testGetTipoSolicitud() {
        System.out.println("getTipoSolicitud");
        assertEquals(null, emptyQuery.getTipoSolicitud());
        assertEquals(T_TIPO_SOLICIT, fullQuery.getTipoSolicitud());
    }

    /**
     * Test of getTipoComprobante method, of class QueryMap.
     */
    @Test
    public void testGetTipoComprobante() {
        System.out.println("getTipoComprobante");
        assertEquals(null, emptyQuery.getTipoComprobante());
        assertEquals(T_TIPO_COMPROBANTE, fullQuery.getTipoComprobante());
    }

    /**
     * Test of getEstadoComprobante method, of class QueryMap.
     */
    @Test
    public void testGetEstadoComprobante() {
        System.out.println("getEstadoComprobante");
        assertEquals(null, emptyQuery.getEstadoComprobante());
        assertEquals(T_EDO_COMPROBANTE, fullQuery.getEstadoComprobante());
    }

    /**
     * Test of getRfcTerceros method, of class QueryMap.
     */
    @Test
    public void testGetRfcTerceros() {
        System.out.println("getRfcTerceros");
        assertEquals(null, emptyQuery.getRfcTerceros());
        assertEquals(T_RFC_TERCEROS, fullQuery.getRfcTerceros());
    }

    /**
     * Test of getComplemento method, of class QueryMap.
     */
    @Test
    public void testGetComplemento() {
        System.out.println("getComplemento");
        assertEquals(null, emptyQuery.getComplemento());
        assertEquals(T_COMPLEMENTO, fullQuery.getComplemento());
    }

    /**
     * Test of getFolio method, of class QueryMap.
     */
    @Test
    public void testGetFolio() {
        System.out.println("getFolio");
        assertEquals(null, emptyQuery.getFolio());
        assertEquals(T_FOLIO, fullQuery.getFolio());
    }

    /**
     * Test of getAttributes method, of class QueryMap.
     */
    @Test
    public void testGetAttributes() {
        System.out.println("getAttributes");
        String[] attributes = new String[] {
            QueryMap.FECHA_INICIAL, QueryMap.FECHA_FINAL, QueryMap.RFC_EMISOR,
            QueryMap.RFC_SOLICITANTE, QueryMap.TIPO_SOLICITUD,
            QueryMap.TIPO_COMPROBANTE, QueryMap.ESTADO_COMPROBANTE,
            QueryMap.RFC_TERCEROS, QueryMap.COMPLEMENTO, QueryMap.FOLIO
        };
        int[] count = new int[attributes.length];
        Iterator<String> iterator = fullQuery.getAttributes();
        while (iterator.hasNext()) {
            count[indexOf(iterator.next(), attributes)]++;
        }
        for (int c : count) {
            assertEquals(1, c);
        }
        assertFalse(emptyQuery.getAttributes().hasNext());
    }
    
    protected int indexOf(String value, String[] array) {
        for (int index = 0; index < array.length; index++) {
            if (value.equals(array[index])) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Test of getAttributeValue method, of class QueryMap.
     */
    @Test
    public void testGetAttributeValue() {
        System.out.println("getAttributeValue");
        String fecIni = fullQuery.format(T_FEC_INI);
        String fecFin = fullQuery.format(T_FEC_FIN);
        assertEquals(fecIni, fullQuery.getAttributeValue(QueryMap.FECHA_INICIAL));
        assertEquals(fecFin, fullQuery.getAttributeValue(QueryMap.FECHA_FINAL));
        assertEquals(T_EMISOR, fullQuery.getAttributeValue(QueryMap.RFC_EMISOR));
        assertEquals(T_SOLICITANTE, fullQuery.getAttributeValue(QueryMap.RFC_SOLICITANTE));
        assertEquals(T_TIPO_SOLICIT, fullQuery.getAttributeValue(QueryMap.TIPO_SOLICITUD));
        assertEquals(T_TIPO_COMPROBANTE, fullQuery.getAttributeValue(QueryMap.TIPO_COMPROBANTE));
        assertEquals(T_EDO_COMPROBANTE, fullQuery.getAttributeValue(QueryMap.ESTADO_COMPROBANTE));
        assertEquals(T_RFC_TERCEROS, fullQuery.getAttributeValue(QueryMap.RFC_TERCEROS));
        assertEquals(T_COMPLEMENTO, fullQuery.getAttributeValue(QueryMap.COMPLEMENTO));
        assertEquals(T_FOLIO, fullQuery.getAttributeValue(QueryMap.FOLIO));
        
        fecIni = fullQuery.format(fullQuery.getFechaInicial());
        fecFin = fullQuery.format(fullQuery.getFechaFinal());
        assertEquals(fecIni, fullQuery.getAttributeValue(QueryMap.FECHA_INICIAL));
        assertEquals(fecFin, fullQuery.getAttributeValue(QueryMap.FECHA_FINAL));
        assertEquals(fullQuery.getRfcEmisor(), fullQuery.getAttributeValue(QueryMap.RFC_EMISOR));
        assertEquals(fullQuery.getRfcSolicitante(), fullQuery.getAttributeValue(QueryMap.RFC_SOLICITANTE));
        assertEquals(fullQuery.getTipoSolicitud(), fullQuery.getAttributeValue(QueryMap.TIPO_SOLICITUD));
        assertEquals(fullQuery.getTipoComprobante(), fullQuery.getAttributeValue(QueryMap.TIPO_COMPROBANTE));
        assertEquals(fullQuery.getEstadoComprobante(), fullQuery.getAttributeValue(QueryMap.ESTADO_COMPROBANTE));
        assertEquals(fullQuery.getRfcTerceros(), fullQuery.getAttributeValue(QueryMap.RFC_TERCEROS));
        assertEquals(fullQuery.getComplemento(), fullQuery.getAttributeValue(QueryMap.COMPLEMENTO));
        assertEquals(fullQuery.getFolio(), fullQuery.getAttributeValue(QueryMap.FOLIO));
        
    }
    /**
     * Test of dayStart method, of clas QueryMap
     */
    @Test
    public void testDayStart() {
        int year = 2024;
        int month = 11;
        int day = 8;
        LocalDateTime start = QueryMap.dayStart(year, month, day);
        assertEquals(year, start.getYear());
        assertEquals(month, start.getMonthValue());
        assertEquals(day, start.getDayOfMonth());
        assertEquals(0, start.getHour());
        assertEquals(0, start.getMinute());
        assertEquals(0, start.getSecond());
    }

    /**
     * Test of dayEnd method, of clas QueryMap
     */
    @Test
    public void testDayEnd() {
        int year = 2024;
        int month = 11;
        int day = 8;
        LocalDateTime start = QueryMap.dayEnd(year, month, day);
        assertEquals(year, start.getYear());
        assertEquals(month, start.getMonthValue());
        assertEquals(day, start.getDayOfMonth());
        assertEquals(23, start.getHour());
        assertEquals(59, start.getMinute());
        assertEquals(59, start.getSecond());
    }
    
    /**
     * Test of format method, of class QueryMap.
     */
    @Test
    public void testFormat_Object() {
        System.out.println("format");
        System.out.println(emptyQuery.format("a string"));
        System.out.println(emptyQuery.format(LocalDateTime.now()));
    }

    /**
     * Test of format method, of class QueryMap.
     */
    @Test
    public void testFormat_LocalDateTime() {
        System.out.println("format");
        System.out.println(emptyQuery.format(T_FEC_INI));
    }

    /**
     * Test of getFormatter method, of class QueryMap.
     */
    @Test
    public void testGetFormatter() {
        System.out.println("getFormatter");
        System.out.println(emptyQuery.getFormatter().format(T_FEC_INI));
    }
    
    @Test
    public void testBuilder() {
        System.out.println("Builder");
        testBuilder(emptyQuery);
        testBuilder(fullQuery);
        QueryMap.Builder builder = new QueryMap.Builder();
        setBuilder(builder, fullQuery);
        QueryTest.assertSameContent(emptyQuery, builder.reset().build());
        
        QueryTest.assertSameContent(emptyQuery, builder.resetQuery(emptyQuery).build());
        QueryTest.assertSameContent(fullQuery, builder.resetQuery(fullQuery).build());
        
    }
    
    protected void testBuilder(QueryMap expectedQuery) {
        QueryMap builtQuery = setBuilder(new QueryMap.Builder(), expectedQuery);
        QueryTest.assertSameContent(expectedQuery, builtQuery);
    }
    
    protected QueryMap setBuilder(QueryMap.Builder builder, QueryMap query) {
        builder.setComplemento(query.getComplemento());
        builder.setEstadoComprobante(query.getEstadoComprobante());
        builder.setFechaFinal(query.getFechaFinal());
        builder.setFechaInicial(query.getFechaInicial());
        builder.setFolio(query.getFolio());
        builder.setRfcEmisor(query.getRfcEmisor());
        builder.setRfcSolicitante(query.getRfcSolicitante());
        builder.setRfcTerceros(query.getRfcTerceros());
        builder.setTipoComprobante(query.getTipoComprobante());
        builder.setTipoSolicitud(query.getTipoSolicitud());
        
        builder.addReceptores(query.getRfcReceptores());
        return builder.build();
    }

    /**
     * Test of toString method, of class QueryMap.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        System.out.println(emptyQuery.toString());
        System.out.println(fullQuery.toString());
    }


}