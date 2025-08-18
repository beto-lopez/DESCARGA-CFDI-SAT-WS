/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.QueryType;
import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.util.QueryBuilder;
import com.sicomsa.dmt.util.FolioQueryBuilder;

import jakarta.xml.soap.SOAPConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.soap.SOAPException;

/**
 * <code>SolicitaSvcAdapter></code> test class
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.06.05
 * @since 1.5
 */
public class SolicitaSvcAdapterTest {
    
    protected QueryBuilder builder;
    protected FolioQueryBuilder folioBuilder;
    protected SvcMock emitidosSvc;
    protected SvcMock recibidosSvc;
    protected SvcMock folioSvc;
    protected SolicitaSvcAdapter adapter;
    
    public SolicitaSvcAdapterTest() {
        builder = new QueryBuilder();
        folioBuilder = new FolioQueryBuilder();
        
        emitidosSvc = new SvcMock(QueryType.EMITIDOS);
        recibidosSvc = new SvcMock(QueryType.RECIBIDOS);
        folioSvc = new SvcMock(QueryType.FOLIO);
        
        adapter = new SolicitaSvcAdapter(emitidosSvc, recibidosSvc, folioSvc);
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
     * Test of callTheService method, of class SolicitaSvcAdapter.
     */
    @Test
    public void testCallTheService() throws Exception {
        System.out.println("callTheService");
        SOAPConnection connection = null;
        Credentials credentials = null;
        
        String token = "";
        SolicitaSvcAdapter instance = adapter;
        Query[] queries = new Query[] {
            builder.buildEmitidosQuery(),
            builder.buildRecibidosQuery("rfc recetor"),
            folioBuilder.build()
        };
        for (Query request : queries) {
            instance.callTheService(connection, credentials, request, token);
        }
    }

    /**
     * Test of getService method, of class SolicitaSvcAdapter.
     */
    @Test
    public void testGetService() {
        System.out.println("getService");
        
        SolicitaSvcAdapter instance = adapter;
        
        assertEquals(instance.getService(QueryType.EMITIDOS), emitidosSvc);
        assertEquals(instance.getService(QueryType.RECIBIDOS), recibidosSvc);
        assertEquals(instance.getService(QueryType.FOLIO), folioSvc);
    }
    
    protected class SvcMock implements Service<SolicitaResponse,Query> {
        protected QueryType type;
        
        public SvcMock(QueryType type) {
            this.type = type;
        }
        
        @Override
        public SolicitaResponse callTheService(SOAPConnection connection,
            Credentials credentials, Query request, String token) throws SOAPException {
            System.out.println("callTheService for :"+type);
            assertEquals(type, ((ExtendedQuery)request).getType());
            return null;
        }
    }

    /**
     * Test of getEmitidosService method, of class SolicitaSvcAdapter.
     */
    @Test
    public void testGetEmitidosService() {
        System.out.println("getEmitidosService");
        Service<SolicitaResponse, Query> expResult = new SvcMock(QueryType.EMITIDOS);
        SolicitaSvcAdapter instance = new SolicitaSvcAdapter(expResult, recibidosSvc, folioSvc);
        Service<SolicitaResponse, Query> result = instance.getEmitidosService();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRecibidosService method, of class SolicitaSvcAdapter.
     */
    @Test
    public void testGetRecibidosService() {
        System.out.println("getRecibidosService");
        Service<SolicitaResponse, Query> expResult = new SvcMock(QueryType.RECIBIDOS);
        SolicitaSvcAdapter instance = new SolicitaSvcAdapter(emitidosSvc, expResult, folioSvc);
        Service<SolicitaResponse, Query> result = instance.getRecibidosService();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFolioService method, of class SolicitaSvcAdapter.
     */
    @Test
    public void testGetFolioService() {
        System.out.println("getFolioService");
        Service<SolicitaResponse, Query> expResult = new SvcMock(QueryType.FOLIO);
        SolicitaSvcAdapter instance = new SolicitaSvcAdapter(emitidosSvc, recibidosSvc, expResult);
        Service<SolicitaResponse, Query> result = instance.getFolioService();
        assertEquals(expResult, result);
    }
   

    
    
}
