/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.util;

import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.QueryType;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <code>QueryBuilder</code> test class
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.2
 * @since       1.5 
 */
public class QueryBuilderTest {
    
    public QueryBuilderTest() {
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
     * Test of reset method, of class QueryBuilder.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        QueryBuilder instance = new QueryBuilder();
        assertTrue(isEmpty(instance));
        setAll(instance);
        assertFalse(isEmpty(instance));
        instance.reset();
        assertTrue(isEmpty(instance));
    }
    
    
    /**
     * Test of setFechaInicial method, of class QueryBuilder.
     */
    @Test
    public void testSetFechaInicial() {
        System.out.println("setFechaInicial");
        LocalDateTime fecha = LocalDateTime.now().plusMinutes(100);
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, QueryMap.FECHA_INICIAL));
        instance.setFechaInicial(fecha);
        assertEquals(fecha, getValue(instance, QueryMap.FECHA_INICIAL));
    }
    
    protected Object getValue(QueryBuilder queryBuilder, String key) {
        return queryBuilder.builder.map.get(key);
    }

    /**
     * Test of setFechaFinal method, of class QueryBuilder.
     */
    @Test
    public void testSetFechaFinal() {
        System.out.println("setFechaFinal");
        LocalDateTime fecha = LocalDateTime.now().plusMinutes(10);
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, QueryMap.FECHA_FINAL));
        instance.setFechaFinal(fecha);
        assertEquals(fecha, getValue(instance, QueryMap.FECHA_FINAL));
    }

    
    /**
     * Test of setRfcEmisor method, of class QueryBuilder.
     */
    @Test
    public void testSetRfcEmisor() {
        System.out.println("setRfcEmisor");
        String rfc = "EMISOR";
        String key = QueryMap.RFC_EMISOR;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setRfcEmisor(rfc);
        assertEquals(rfc, getValue(instance, key));
    }

    /**
     * Test of setRfcSolicitante method, of class QueryBuilder.
     */
    @Test
    public void testSetRfcSolicitante() {
        System.out.println("setRfcSolicitante");
        String rfc = "SOLICITANTE";
        String key = QueryMap.RFC_SOLICITANTE;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setRfcSolicitante(rfc);
        assertEquals(rfc, getValue(instance, key));
    }

    /**
     * Test of setTipoSolicitud method, of class QueryBuilder.
     */
    @Test
    public void testSetTipoSolicitud() {
        System.out.println("setTipoSolicitud");
        String tipo = "TIPO DE SOLICITUD";
        String key = QueryMap.TIPO_SOLICITUD;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setTipoSolicitud(tipo);
        assertEquals(tipo, getValue(instance, key));
    }

    /**
     * Test of setTipoComprobante method, of class QueryBuilder.
     */
    @Test
    public void testSetTipoComprobante() {
        System.out.println("setTipoComprobante");
        String tipo = "TIPO DE COMPROBANTE";
        String key = QueryMap.TIPO_COMPROBANTE;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setTipoComprobante(tipo);
        assertEquals(tipo, getValue(instance, key));
    }

    /**
     * Test of setEstadoComprobante method, of class QueryBuilder.
     */
    @Test
    public void testSetEstadoComprobante() {
        System.out.println("setEstadoComprobante");
        String estado = "ESTATUS";
        String key = QueryMap.ESTADO_COMPROBANTE;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setEstadoComprobante(estado);
        assertEquals(estado, getValue(instance, key));
    }

    /**
     * Test of setRfcTerceros method, of class QueryBuilder.
     */
    @Test
    public void testSetRfcTerceros() {
        System.out.println("setRfcTerceros");
        String rfc = "TERCEROS";
        String key = QueryMap.RFC_TERCEROS;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setRfcTerceros(rfc);
        assertEquals(rfc, getValue(instance, key));
    }

    /**
     * Test of setComplemento method, of class QueryBuilder.
     */
    @Test
    public void testSetComplemento() {
        System.out.println("setComplemento");
        String complemento = "THE COMPLEMENT";
        String key = QueryMap.COMPLEMENTO;
        QueryBuilder instance = new QueryBuilder();
        assertNull(getValue(instance, key));
        instance.setComplemento(complemento);
        assertEquals(complemento, getValue(instance, key));
    }

    /**
     * Test of buildEmitidosQuery method, of class QueryBuilder.
     */
    @Test
    public void testBuildEmitidosQuery_0args() {
        System.out.println("buildEmitidosQuery");
        QueryBuilder instance = new QueryBuilder();
        setAll(instance);
        ExtendedQuery result = instance.buildEmitidosQuery();
        assertTrue(result.getRfcReceptores().isEmpty());
        assertEquals(QueryType.EMITIDOS, result.getType());
        assertNull(result.getRfcReceptor());
        assertAllSet(instance, result);       
    }
    
    protected void assertSame(ExtendedQuery q1, ExtendedQuery q2) {
        q1.getAttributes().forEachRemaining(key-> {
            System.out.println("key="+key+",value="+q1.getAttributeValue(key));
            assertEquals(q1.getAttributeValue(key), q2.getAttributeValue(key));
        });
        assertTrue(q1.getRfcReceptores().containsAll(q2.getRfcReceptores()));
        assertEquals(q1.getRfcReceptores().size(), q2.getRfcReceptores().size());
    }
    

    /**
     * Test of buildEmitidosQuery method, of class QueryBuilder.
     */
    @Test
    public void testBuildEmitidosQuery_List() {
        System.out.println("buildEmitidosQuery");
        List<String> receptorsList = List.of("A", "B", "C", "D");
        QueryBuilder instance = new QueryBuilder();
        setAll(instance);
        ExtendedQuery result = instance.buildEmitidosQuery(null);
        assertTrue(result.getRfcReceptores().isEmpty());
        assertEquals(QueryType.EMITIDOS, result.getType());
        assertNull(result.getRfcReceptor());
        assertAllSet(instance, result);
        
        result = instance.buildEmitidosQuery(receptorsList);
        assertFalse(result.getRfcReceptores().isEmpty());
        assertEquals(QueryType.EMITIDOS, result.getType());
        assertNull(result.getRfcReceptor());
        assertAllSet(instance, result);
        
        assertTrue(receptorsList.containsAll(result.getRfcReceptores()));
        assertTrue(result.getRfcReceptores().containsAll(receptorsList));
        
    }

    /**
     * Test of buildRecibidosQuery method, of class QueryBuilder.
     */
    @Test
    public void testBuildRecibidosQuery() {
        System.out.println("buildRecibidosQuery");
        String rfcReceptor = "ALFA BETA";
        QueryBuilder instance = new QueryBuilder();
        setAll(instance);
        Exception e = assertThrows(IllegalArgumentException.class, ()-> {
            buildRecibidosQuery(instance, null);
        });
        assertEquals("rfc is required", e.getMessage());
                
        ExtendedQuery result = instance.buildRecibidosQuery(rfcReceptor);
        assertEquals(rfcReceptor, result.getRfcReceptor());
        assertEquals(QueryType.RECIBIDOS, result.getType());
        assertTrue(result.getRfcReceptores().isEmpty());
        assertAllSet(instance, result);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected boolean isEmpty(QueryBuilder queryBuilder) {
        QueryMap.Builder builder = queryBuilder.builder;
        return builder.receptorsSet.isEmpty()
                && builder.map.isEmpty();
    }
    
    protected ExtendedQuery buildRecibidosQuery(QueryBuilder instance, String rfc) {
        return instance.buildRecibidosQuery(rfc);
    }
    
    protected void setAll(QueryBuilder builder) {
        builder.setComplemento("complemento")
               .setEstadoComprobante("edo")
               .setFechaFinal(LocalDateTime.MIN)
               .setFechaInicial(LocalDateTime.MAX)
               .setRfcEmisor("emisor")
               .setRfcSolicitante("solicitante")
               .setRfcTerceros("terceros")
               .setTipoComprobante("tipoC")
               .setTipoSolicitud("tipoS");
    }
    
    
    protected void assertAllSet(QueryBuilder builder, ExtendedQuery equery) {
        ExtendedQueryMap query = (ExtendedQueryMap)equery;
        builder.builder.map.forEach((k,v)-> {
            if (!k.equals(ExtendedQueryMap.RFC_RECEPTOR)) {
                assertEquals(query.format(v), query.getAttributeValue(k));
            }
        });
    }
    
}
