/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.Client;
import com.sicomsa.dmt.ClientTest;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.CredentialsProxy;
import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.DMTService;
import com.sicomsa.dmt.Query;

import com.sicomsa.dmt.RepositoryException;
import com.sicomsa.dmt.util.QueryMap;
import com.sicomsa.dmt.solicitude.Solicitude;
import jakarta.xml.soap.SOAPConnection;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import java.util.LinkedHashMap;
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
 * @since 2025.01.20
 *
 *
 */
public class BatchTest {
    
    static Batch batch;
    static LinkedHashMap<Long,BatchSolicitude> batchMap;
    static ClientTest.ServiceMock service;

    public BatchTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        service = new ClientTest.ServiceMock();
        batchMap = new LinkedHashMap<>();
        List<BatchSolicitude> list = getList1(service);
        for (BatchSolicitude solicitude : list) {
            batchMap.put(solicitude.getBatchId(), solicitude);
        }
        batch = new Batch(new File(""), batchMap);
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

    protected static List<BatchSolicitude> getList1(DMTService service) {
        String rfc1= "MY RFC 86";
        String rfc2= "MY RFC 91";
        
        QueryMap.Builder b = new QueryMap.Builder();
        QueryMap query =  b.setFechaInicial(LocalDateTime.now())
                .setFechaFinal(LocalDateTime.now().plusDays(1))
                .setRfcEmisor(rfc1)
                .setTipoSolicitud("CFDI").build();
        QueryMap query2 = b.setRfcEmisor("").addReceptor(rfc2).build();
        QueryMap query3 = b.setTipoSolicitud("Metadata").build();
        BatchSolicitude bs1 = new BatchSolicitude(new Client(new UselessCredentials(rfc1), service), query, 1000);
        BatchSolicitude bs2 = new BatchSolicitude(new Client(new UselessCredentials(rfc2), service), query2, 2000);
        BatchSolicitude bs3 = new BatchSolicitude(new Client(new UselessCredentials(rfc2), service), query3, 3000);
        return List.of(bs1, bs2, bs3);
    }
    
    protected class MutablePendingSolicitude extends BatchSolicitude {
        private boolean pending = false;
        public MutablePendingSolicitude(DMTClient client, Query query, long batchId) {
            super(client, query, batchId);
        }
        public void setPending(boolean pending) {
            this.pending = pending;
        }
        @Override
        public boolean isPending() {
            return pending;
        }
        
    }
    /**
     * Test of download method, of class Batch.
     */
    @Disabled("tested with real data")
    @Test
    public void testDownload() throws Exception {
        System.out.println("download");
        SOAPConnection conn = null;
        Batch instance = null;
        instance.download(conn);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPending method, of class Batch.
     */
    @Test
    public void testIsPending() {
        System.out.println("isPending");
        LinkedHashMap<Long,BatchSolicitude> map2 = new LinkedHashMap<>();
        Iterator<Solicitude> iterator = batch.solicitudes();
        while (iterator.hasNext()) {
            BatchSolicitude bs = (BatchSolicitude)iterator.next();
            map2.put(bs.getBatchId(), bs);
            map2.put(bs.getBatchId(),
                    new MutablePendingSolicitude(
                            new Client(new UselessCredentials(bs.getClient().getRfc()), service),
                            bs.getQuery(),
                            bs.getBatchId()));
        }
        Batch xbatch = new Batch(new File(""), map2);
        assertFalse(xbatch.isPending());
        MutablePendingSolicitude mps = (MutablePendingSolicitude)xbatch.solicitudes().next();
        mps.setPending(true);
        assertTrue(xbatch.isPending());
    }


    /**
     * Test of verifyCredentials method, of class Batch.
     */
    @Disabled("tested manually")
    @Test
    public void testVerifyCredentials() throws Exception {
        System.out.println("verifyCredentials");
        Batch instance = batch;
        instance.verifyCredentials();
    }

    /**
     * Test of checkCertificate method, of class Batch.
     */
    @Disabled("tested manually")
    @Test
    public void testCheckCertificate() throws Exception {
        System.out.println("checkCertificate");
        DMTClient client = null;
        Batch instance = null;
        instance.checkCertificate(client);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSolicitude method, of class Batch.
     */
    @Test
    public void testGetSolicitude() {
        System.out.println("getSolicitude");
        Batch instance = batch;
        Iterator<Solicitude> iterator = instance.solicitudes();
        while (iterator.hasNext()) {
            BatchSolicitude bs = (BatchSolicitude)iterator.next();
            assertEquals(bs, instance.getSolicitude(bs.getBatchId()));
        }
    }

    /**
     * Test of batchIds method, of class Batch.
     */
    @Test
    public void testBatchIds() {
        System.out.println("batchIds");
        Batch instance = batch;
        Iterator<Long> expResult = batchMap.keySet().iterator();
        Iterator<Long> result = instance.batchIds();
        System.out.print("batch ids:");
        while (expResult.hasNext()) {
            Long id = expResult.next();
            System.out.print(id+" ");
            assertEquals(id, result.next());
        }
        System.out.println();
    }

    /**
     * Test of solicitudes method, of class Batch.
     */
    @Test
    public void testSolicitudes() {
        System.out.println("solicitudes");
        Batch instance = batch;
        Iterator<BatchSolicitude> expResult = batchMap.values().iterator();
        Iterator<Solicitude> result = instance.solicitudes();
        System.out.print("batch Solicitudes:");
        while (expResult.hasNext()) {
            BatchSolicitude bs = expResult.next();
            System.out.print(bs.getBatchId()+" ");
            assertEquals(bs, result.next());
        }
        System.out.println();
        
    }
    ///////////////////////////////////////////////////////////////////////////
    
    public static class UselessCredentials extends CredentialsProxy {
        public UselessCredentials(String rfc) {
            super(rfc);
        }
        
        @Override protected Credentials doGetCredentials() throws RepositoryException {
            throw new UnsupportedOperationException();
        }
    }
    

}