/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.ClientTest;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.CredentialsStore;
import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.DMTService;
import com.sicomsa.dmt.Query;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import com.sicomsa.dmt.QueryTest;
import com.sicomsa.dmt.RepositoryException;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.QueryMap;
import com.sicomsa.dmt.util.QueryMapTest;
import com.sicomsa.dmt.solicitude.Solicitude;
import com.sicomsa.dmt.solicitude.StateValue;
import java.io.StreamCorruptedException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPMessage;


import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.20
 *
 *
 */
public class BatchFactoryTest {
    @TempDir
    static Path tempDir;
    static Path tempFile;
    
    static DMTService service;
    static Query fullQuery;

    
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    
    public BatchFactoryTest() {
    }

    @BeforeAll
    public static void setUpClass() throws java.io.IOException {
        tempFile = java.nio.file.Files.createFile(tempDir.resolve("test.xml"));
        System.out.println(tempFile);
        service = new ClientTest.ServiceMock();
        fullQuery = new QueryMapTest().getFullQuery();
        
        LOG.setLevel(Level.ALL);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
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
     * Test of builder method, of class BatchFactory.
     */
    @Test
    public void testBuilder() throws Exception {
        System.out.println("builder");
        BatchFactory instance = newBatchFactory(service);
        BatchFactory.Builder builder = instance.builder();
        Exception e = assertThrows(IllegalStateException.class, ()->{builder.build(new File(""));});
        assertEquals("can not build batch without requests", e.getMessage());
        HashMap<Long,BatchRequest> requestMap = new HashMap<>();
        makeRequestList().forEach(request-> {
            requestMap.put(request.getId(), request);
            builder.addRequest(request.getId(), request.getRfc(), request.getQuery());
        });
        assertFalse(requestMap.isEmpty());
        BatchRequest duplicateRequest = requestMap.values().iterator().next();
        e = assertThrows(IllegalArgumentException.class,
                ()->{builder.addRequest(duplicateRequest.getId(),
                        duplicateRequest.getRfc(), duplicateRequest.getQuery());});
        assertTrue(e.getMessage().contains("batchId must be unique among batch requests"));
        File file = tempFile.toFile();
        Batch batch = builder.build(file);
        requestMap.values().forEach(request-> {
            System.out.println("mapRequest:"+request);
            assertMatches(request, (BatchSolicitude)batch.getSolicitude(request.getId()));
        });
        
        BatchReader reader = BatchReader.read(file);
        reader.getRequests().forEachRemaining(requestRead-> {
            System.out.println("requestRead="+requestRead);
            BatchSolicitude bs = (BatchSolicitude)batch.getSolicitude(requestRead.getId());
            assertMatches(requestRead, bs);
            BatchRequest originalRequest = requestMap.remove(requestRead.getId());
            assertMatches(originalRequest, bs);
        });
        assertTrue(requestMap.isEmpty()); //verify all were read and matched
    }

    /**
     * Test of load2 method, of class BatchFactory.
     */
    @Test
    public void testLoad_File() throws Exception {
        System.out.println("load");
        testLoad(batchLoadTest );

        Exception e = assertThrows(StreamCorruptedException.class,
                ()->{testLoad(batchLoadTest2);});
        assertEquals("no requests found in batch file", e.getMessage());
        
        e = assertThrows(StreamCorruptedException.class,
                ()->{testLoad(batchLoadTest3);});
        assertTrue(e.getMessage().contains("downloadProcess not found"));
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected void testLoad(String string) throws Exception {
        SOAPMessage message = SOAPUtils.fromString(string);
        BatchReader reader = new BatchReader(message);
        BatchFactory instance = newBatchFactory(service);
        Map<Long,BatchSolicitude> map = instance.buildSolicitudeMap(reader);
        doTestLoaded(new Batch(tempFile.toFile(), map));
    }

    protected void testLoad(File file) throws Exception {
        System.out.println("testLoad:"+file.getAbsolutePath());
        BatchFactory instance = newBatchFactory(service);
        doTestLoaded(instance.load(file));
    }
    
    protected void doTestLoaded(Batch expResult) throws Exception {
        expResult.solicitudes().forEachRemaining(solicitud-> {
            BatchSolicitude bs = (BatchSolicitude)solicitud;
            System.out.println("batchId="+bs.getBatchId()+"="+solicitud);
            System.out.println("data="+bs.generateSolicitudeData());
        });
        assertMatches(expResult.getSolicitude(1000L), false, false, StateValue.NEW, false);
        assertMatches(expResult.getSolicitude(2000L), false, true, StateValue.NEW, false);
        assertMatches(expResult.getSolicitude(3000L), false, false, StateValue.ACCEPTED, false);
        assertMatches(expResult.getSolicitude(4000L), false, true, StateValue.ACCEPTED, false);
        assertMatches(expResult.getSolicitude(5000L), true , false, StateValue.DELAYED, false);
        assertMatches(expResult.getSolicitude(6000L), false, false, StateValue.VERIFIED, false);
        assertMatches(expResult.getSolicitude(7000L), false, true, StateValue.VERIFIED, false);
        assertMatches(expResult.getSolicitude(8000L), false, false, StateValue.VERIFIED, true);
    }
    
    protected void assertMatches(Solicitude solicitude, boolean isDelay, boolean isReject, StateValue value, boolean done) {
        assertEquals(isDelay, solicitude.isDelay());
        assertEquals(isReject, solicitude.isReject());
        assertEquals(value, solicitude.getValue());
        assertEquals(done, solicitude.isDownloadDone());
    }
    
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Test of buildSolicitudeMap method, of class BatchFactory.
     */
    @Test
    public void testBuildSolicitudeMap() {
        System.out.println("buildSolicitudeMap");
        List<BatchRequest> requestList = makeRequestList();
               
        Iterator<BatchRequest> iterator = requestList.iterator();
        BatchFactory instance = newBatchFactory(service);

        Map<Long, BatchSolicitude> result = instance.buildSolicitudeMap(iterator);
        
        assertEquals(requestList.size(), result.size());
        
        requestList.forEach(request-> {
           assertMatches(request, result.get(request.getId()));
        });
    }
    
    protected final static List<BatchRequest> makeRequestList() {
        ArrayList<BatchRequest> list = new ArrayList<>();
        QueryMap.Builder builder = QueryMap.builder();
        Query q0 = builder.setFolio("abc").build();
        Query q1 = builder.reset().setComplemento("alddkdkdk").build();
        Query q2 = builder.reset().setRfcEmisor("rfc1").build();
        
        list.add(new BatchRequest(0L, "rfc0", q0));
        list.add(new BatchRequest(88L, "rfc777", q2));
        list.add(new BatchRequest(999L, "rfc444", q1));
        return list;
    }
    
    protected void assertMatches(BatchRequest request, BatchSolicitude bs) {
        assertEquals(request.getId(), bs.getBatchId());
        assertEquals(request.getRfc(), bs.getClient().getRfc());
        QueryTest.assertSameContent(request.getQuery(), bs.getQuery());
    }
    

    /**
     * Test of newSolicitude method, of class BatchFactory.
     */
    @Test
    public void testNewSolicitude() {
        System.out.println("newSolicitude");
        String rfc = "abcdefg";
        Query query = fullQuery;
        long id = 99L;
        BatchFactory instance = newBatchFactory(service);
//        BatchSolicitude expResult = null;
        BatchSolicitude result = instance.newSolicitude(rfc, query, id);
        assertEquals(rfc.toUpperCase(), result.getClient().getRfc());
        assertEquals(query, result.getQuery());
        assertEquals(id, result.getBatchId());
        assertTrue(result.getValue().isNew());
    }

    /**
     * Test of getClient method, of class BatchFactory.
     */
    @Test
    public void testGetClient() {
        System.out.println("getClient");
        String rfc = "abcd";
        BatchFactory instance = newBatchFactory(service);
        DMTClient expResult = instance.getClient(rfc);
        assertEquals(rfc.toUpperCase(), expResult.getRfc());
        DMTClient result = instance.getClient(rfc);
        assertEquals(expResult, result);
    }

    
    protected static BatchFactory newBatchFactory(DMTService service) {
        return new BatchFactory(service, STORE);
    }
    
    static final CredentialsStore STORE = new TestingStore();
    protected static class TestingStore implements CredentialsStore {
    
        @Override
        public Credentials getCredentials(String rfc) throws RepositoryException {
            throw new UnsupportedOperationException();
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    static final String batchLoadTest = """
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:BATCH="com.sicomsa.dmt/Batch/2024/12/27" xmlns:dmt="http://DescargaMasivaTerceros.sat.gob.mx">
    <SOAP-ENV:Header>
    <BATCH:Requests>
    <BATCH:Request BatchId="1000" RFC="MY RFC 86">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="MY RFC 86" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="2000" RFC="MY RFC 91">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="3000" RFC="MY RFC 92">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="4000" RFC="MY RFC 93">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="5000" RFC="MY RFC 94">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="6000" RFC="MY RFC 95">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="7000" RFC="MY RFC 96">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    <BATCH:Request BatchId="8000" RFC="MY RFC 97">
    <dmt:SolicitaDescarga>
    <dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga>
    </BATCH:Request>
    </BATCH:Requests>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
    <BATCH:Version>1.0</BATCH:Version>
    <!-- Batch begin -->
    <BATCH:Response BatchId="2000" Status="rejected" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5002" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Rechazada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="3000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="4000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="4000" Status="rejected" Type="VerificaResponse">
    <VerificaResponse CodEstatus="5002" CodigoEstadoSolicitud="5003" EstadoSolicitud="3" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:17:39.533677Z" Mensaje="Solicitud Rechazada" NumeroCFDIs="0"/>
    </BATCH:Response>
    <BATCH:Response BatchId="5000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="5000" Status="delayed" Type="VerificaResponse">
    <VerificaResponse CodEstatus="5000" CodigoEstadoSolicitud="5000" EstadoSolicitud="1" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:18:34.533677Z" Mensaje="Solicitud Aceptada" NumeroCFDIs="0"/>
    </BATCH:Response>
    <BATCH:Response BatchId="6000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="6000" Status="verified" Type="VerificaResponse">
    <VerificaResponse CodEstatus="5000" CodigoEstadoSolicitud="5000" EstadoSolicitud="3" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:19:29.533677Z" Mensaje="Aceptada" NumeroCFDIs="888">
    <dmt:IdsPaquetes>abc</dmt:IdsPaquetes>
    <dmt:IdsPaquetes>def</dmt:IdsPaquetes>
    </VerificaResponse>
    </BATCH:Response>
    <BATCH:Response BatchId="7000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="7000" Status="verified" Type="VerificaResponse">
    <VerificaResponse CodEstatus="5000" CodigoEstadoSolicitud="5000" EstadoSolicitud="3" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:19:29.533677Z" Mensaje="Aceptada" NumeroCFDIs="888">
    <dmt:IdsPaquetes>abc</dmt:IdsPaquetes>
    <dmt:IdsPaquetes>def</dmt:IdsPaquetes>
    </VerificaResponse>
    </BATCH:Response>
    <BATCH:Response BatchId="7000" Status="rejected" Type="DescargaResponse">
    <DescargaResponse CodEstatus="5002" Disposed="true" IdPaquete="abc" Instant="2025-01-19T18:20:24.533677Z" Mensaje="error al descargar"/>
    </BATCH:Response>
    <BATCH:Response BatchId="8000" Status="accepted" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5000" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Aceptada"/>
    </BATCH:Response>
    <BATCH:Response BatchId="8000" Status="verified" Type="VerificaResponse">
    <VerificaResponse CodEstatus="5000" CodigoEstadoSolicitud="5000" EstadoSolicitud="3" IdSolicitud="request abc-def-ghi" Instant="2025-01-19T18:19:29.533677Z" Mensaje="Aceptada" NumeroCFDIs="888">
    <dmt:IdsPaquetes>abc</dmt:IdsPaquetes>
    <dmt:IdsPaquetes>def</dmt:IdsPaquetes>
    </VerificaResponse>
    </BATCH:Response>
    <BATCH:Response BatchId="8000" Status="downloaded1" Type="DescargaResponse">
    <DescargaResponse CodEstatus="5000" Disposed="true" IdPaquete="abc" Instant="2025-01-19T18:20:24.533677Z" Mensaje="Descargada ok"/>
    </BATCH:Response>
    <BATCH:Response BatchId="8000" Status="downloaded2" Type="DescargaResponse">
    <DescargaResponse CodEstatus="5000" Disposed="true" IdPaquete="def" Instant="2025-01-19T18:21:19.533677Z" Mensaje="Aceptada ok2"/>
    </BATCH:Response>
    <!-- Batch end -->
    </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>""";
    
    static final String batchLoadTest2 = """
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:BATCH="com.sicomsa.dmt/Batch/2024/12/27" xmlns:dmt="http://DescargaMasivaTerceros.sat.gob.mx">
    <SOAP-ENV:Header>
    <BATCH:Requests/>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
    <BATCH:Version>1.0</BATCH:Version>
    <!-- Batch begin -->
    </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>""";
    
    static final String batchLoadTest3 = """
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:BATCH="com.sicomsa.dmt/Batch/2024/12/27" xmlns:dmt="http://DescargaMasivaTerceros.sat.gob.mx"><SOAP-ENV:Header>
    <BATCH:Requests>
    <BATCH:Request BatchId="1000" RFC="MY RFC 86">
    <dmt:SolicitaDescarga><dmt:solicitud FechaFinal="2025-01-20T12:15:49" FechaInicial="2025-01-19T12:15:49" RfcEmisor="MY RFC 86" TipoSolicitud="CFDI"/>
    </dmt:SolicitaDescarga></BATCH:Request>
    </BATCH:Requests>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body><BATCH:Version>1.0</BATCH:Version><!--Batch begin-->
    <BATCH:Response BatchId="2000" Status="rejected" Type="SolicitaResponse">
    <SolicitaResponse CodEstatus="5002" Instant="2025-01-19T18:16:44.533677Z" Mensaje="Solicitud Rechazada"/>
    </BATCH:Response>
    </SOAP-ENV:Body></SOAP-ENV:Envelope>""";
}