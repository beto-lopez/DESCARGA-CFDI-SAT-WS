/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.Client;
import com.sicomsa.dmt.CredentialsProxy;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.util.QueryMap;
import com.sicomsa.dmt.solicitude.DownloadEvent;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
 
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.sicomsa.dmt.QueryTest;
import org.junit.jupiter.api.function.Executable;
import com.sicomsa.dmt.solicitude.Solicitude;
import java.time.Instant;
import com.sicomsa.dmt.PackageIds;
import com.sicomsa.dmt.SatResponse;
import com.sicomsa.dmt.ClientTest;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.RepositoryException;
import java.nio.file.Path;
import java.nio.file.Files;
import org.junit.jupiter.api.io.TempDir;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.19
 *
 *
 */
public class BatchWriterTest {
    
    @TempDir
    static Path tempDir;
    static File file1;
    static File file2;
    
    static Instant auxInstant;

    public BatchWriterTest() {
    }

    @BeforeAll
    public static void setUpClass() throws java.io.IOException  {
        file1 = Files.createFile(tempDir.resolve("BatchDemo2cw.xml")).toFile();
        file2 = Files.createFile(tempDir.resolve("BatchDemo2cX.xml")).toFile();

        auxInstant = Instant.now();
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
     * Test of close method, of class BatchWriter.
     */
    @Test
    public void testClose() throws IOException {
        System.out.println("close");
        BatchWriter instance = new BatchWriter(file1);
        assertFalse(instance.isClosed());
        instance.close();
        assertTrue(instance.isClosed());
    }

    /**
     * Test of isClosed method, of class BatchWriter.
     */
    @Test
    public void testIsClosed() throws IOException {
        System.out.println("isClosed");
        testClose();
    }
    
    /**
     * Test of formatFile method, of class BatchWriter.
     */
    @Test
    public void testFormatFile() throws Exception {
        System.out.println("formatFile");
        testFormatFile(getList1());
        testFormatFile(Collections.emptyList());
        assertNullPointer(()-> {testFormatFile(null);}, null);
    }

    
    public void assertNullPointer(Executable ex, String message) {
        Exception e = assertThrows(NullPointerException.class, ex);
        if (message == null) {
            System.out.println(e.getMessage());
        }
        else {
            assertEquals(e.getMessage(), message);
        }
    }
    
    protected void testFormatFile(List<BatchSolicitude> list) throws IOException, SOAPException {
        System.out.println("testFormatFile:"+list);
        try (BatchWriter instance = new BatchWriter(file1)) {
            instance.formatFile(list.iterator());
        }
        BatchReader reader = BatchReader.read(file1);
        Iterator<BatchRequest> iterator = reader.getRequests();
        int idx = 0;
        while (iterator.hasNext()) {
            assertMatch(list.get(idx), iterator.next());
            idx++;           
        }
        assertEquals(list.size(), idx);
    }
    
    protected void assertMatch(BatchSolicitude solicitude, BatchRequest request) {
        assertEquals(solicitude.getBatchId(), request.getId());
        assertEquals(solicitude.getClient().getRfc(), request.getRfc());
        QueryTest.assertSameContent(solicitude.getQuery(), request.getQuery());
    }
    
    protected List<BatchSolicitude> getList1() {
        String rfc1= "MY RFC 86";
        String rfc2= "MY RFC 91";
        
        QueryMap.Builder b = new QueryMap.Builder();
        QueryMap query =  b.setFechaInicial(LocalDateTime.now())
                .setFechaFinal(LocalDateTime.now().plusDays(1))
                .setRfcEmisor(rfc1)
                .setTipoSolicitud("CFDI").build();
        QueryMap query2 = b.setRfcEmisor("").addReceptor(rfc2).build();
        ClientTest.ServiceMock service = new ClientTest.ServiceMock();
        BatchSolicitude bs1 = new BatchSolicitude(new Client(new DemoCredentials(rfc1), service), query, 1000);
        BatchSolicitude bs2 = new BatchSolicitude(new Client(new DemoCredentials(rfc2), service), query2, 2000);
        return List.of(bs1, bs2);
    }
    
    protected class DemoCredentials extends CredentialsProxy {
        public DemoCredentials(String rfc) {
            super(rfc);
        }
    
        @Override protected Credentials doGetCredentials() throws RepositoryException {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Test of writeResponse method, of class BatchWriter.
     */
    @Test
    public void testWriteResponse() throws Exception {
        System.out.println("writeResponse");
        List<BatchSolicitude> list = getList1();
        BatchSolicitude bs = list.get(0);
        List<DownloadEvent> eventList = getEventList1(bs);
        try (BatchWriter instance = new BatchWriter(file2)) {
            instance.formatFile(list.iterator());
            instance.writeComment("Batch begin");
            for (DownloadEvent e : eventList) {
                instance.writeResponse(e);
            }
            instance.writeComment("Batch end");
        }
        BatchReader reader = BatchReader.read(file2);
        Iterator<EventResponse> iterator = reader.getResponses();
        int idx = 0;
        while (iterator.hasNext()) {
            assertMatch(bs, eventList.get(idx), iterator.next());
            idx++;           
        }
        assertEquals(eventList.size(), idx);
    }
    
    protected void assertMatch(BatchSolicitude solicitude, DownloadEvent event, EventResponse response) {
        assertEquals(solicitude.getBatchId(), response.getProcessId());
        SatResponse satResponse = event.getResponse();
        assertEquals(satResponse.getInstant(), response.getInstant());
        assertEquals(event.getResult().toString(), /*getEventName(),*/ response.getStatus());
        assertEquals(satResponse.toString(), response.getResponse().toString());
    }
       
    protected List<DownloadEvent> getEventList1(Solicitude solicitude) {
        ArrayList<DownloadEvent> list = new ArrayList<>();
        String requestId = "request abc-def-ghi";
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.ACCEPTED, 
                new SolicitaResponse(nextUniqueInstant(), "5000", "Solicitud Aceptada", requestId)));
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.DELAYED, 
                new VerificaResponse(nextUniqueInstant(), "5000", "Solicitud Aceptada", 1, "5000", requestId)));
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.DELAYED, 
                new VerificaResponse(nextUniqueInstant(), "5000", "Solicitud Aceptada", 1, "5000", requestId)));
        PackageIds ids = new PackageIds(List.of("abc", "def"));
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.VERIFIED,
                new VerificaResponse(nextUniqueInstant(), "5000", "Aceptada", 1, "5000", requestId, 888, ids)));
        DescargaResponse disposed = new DescargaResponse(nextUniqueInstant(), "5000", "Descargada ok", "abc", "apckage");
        disposed.dispose();
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.DOWNLOADED, disposed));
        DescargaResponse disposed2 = new DescargaResponse(nextUniqueInstant(), "5000", "Descargada ok", "def", "apckage");
        disposed2.dispose();
        list.add(new DownloadEvent(solicitude, DownloadEvent.Result.DOWNLOADED, disposed2));
        return list;
    }
    
    protected Instant nextUniqueInstant() {
        return auxInstant = auxInstant.plusSeconds(55);
    }
    
    /**
     * Test of newMessagePrototype method, of class BatchWriter.
     */
    @Test
    public void testNewMessagePrototype() throws Exception {
        System.out.println("newMessagePrototype");
        try (BatchWriter instance = new BatchWriter(file1)) {
            assertNotNull(instance.newMessagePrototype());
        }
    }

    /**
     * Test of getWriter method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testGetWriter() throws Exception {
        System.out.println("getWriter");
        try (BatchWriter instance = new BatchWriter(file1)) {
            assertNotNull(instance.getWriter());
        }
    }

    /**
     * Test of addComment method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testAddComment() {
        System.out.println("addComment");
        SOAPElement element = null;
        String text = "";
        BatchWriter instance = null;
        instance.addComment(element, text);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkClosed method, of class BatchWriter.
     */
    @Test
    public void testCheckNotClosed() throws Exception {
        System.out.println("checkNotClosed");
        BatchWriter instance = new BatchWriter(file1);
        assertDoesNotThrow(()->{instance.checkNotClosed();});
        instance.close();
        Exception e = assertThrows(IOException.class,
                ()->{instance.checkNotClosed();});
        assertEquals("closed writer", e.getMessage());
    }

    /**
     * Test of addRequests method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testAddRequests() throws Exception {
        System.out.println("addRequests");
        SOAPElement element = null;
        Iterator<BatchSolicitude> iterator = null;
        BatchWriter instance = null;
        instance.addRequests(element, iterator);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addRequest method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testAddRequest() throws Exception {
        System.out.println("addRequest");
        SOAPElement parent = null;
        BatchSolicitude solicitude = null;
        BatchWriter instance = null;
        instance.addRequest(parent, solicitude);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of appendElement method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testAppendElement() throws Exception {
        System.out.println("appendElement");
        DownloadEvent event = null;
        BatchWriter instance = null;
        instance.appendElement(event);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addResponse method, of class BatchWriter.
     */
    @Test
    public void testAddResponse_SOAPElement_SolicitaResponse() throws Exception {
        System.out.println("addResponse");
        try (BatchWriter instance = new BatchWriter(file1)) {
            SOAPMessage message = instance.newMessagePrototype();
            SOAPElement element = message.getSOAPBody().addChildElement(BatchWriter.SOLICITA_RESPONSE);
            SolicitaResponse response = new SolicitaResponse(Instant.now(), "5555", "xx", "");
            instance.addResponse(element, response);
            BatchReader reader = new BatchReader(message);
            SolicitaResponse result = reader.parseSolicitaResponse(element);
            assertEquals(response.toString(), result.toString());
        }
    }

    /**
     * Test of addResponse method, of class BatchWriter.
     */
    @Test
    public void testAddResponse_SOAPElement_VerificaResponse() throws Exception {
        System.out.println("addResponse");
        try (BatchWriter instance = new BatchWriter(file1)) {
            SOAPMessage message = instance.newMessagePrototype();
            SOAPElement element = message.getSOAPBody().addChildElement(BatchWriter.VERIFICA_RESPONSE);
            VerificaResponse response = new VerificaResponse(Instant.now(), "5555", "xx", 1, "abc", "reqst");
            instance.addResponse(element, response);
            BatchReader reader = new BatchReader(message);
            VerificaResponse result = reader.parseVerificaResponse(element);
            assertEquals(response.toString(), result.toString());
        }
    }

    /**
     * Test of addResponse method, of class BatchWriter.
     */
    @Test
    public void testAddResponse_SOAPElement_DescargaResponse() throws Exception {
        System.out.println("addResponse");
        try (BatchWriter instance = new BatchWriter(file1)) {
            SOAPMessage message = instance.newMessagePrototype();
            SOAPElement element = message.getSOAPBody().addChildElement(BatchWriter.DESCARGA_RESPONSE);
            DescargaResponse response = new DescargaResponse(Instant.now(), "5555", "xx", "pid", "ep");
            response.dispose();
            instance.addResponse(element, response);
            BatchReader reader = new BatchReader(message);
            DescargaResponse result = reader.parseDescargaResponse(element);
            assertEquals(response.toString(), result.toString());
        }
    }

    /**
     * Test of addAttribute method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testAddAttribute() throws Exception {
        System.out.println("addAttribute");
        SOAPElement e = null;
        QName name = null;
        String value = "";
        BatchWriter instance = null;
        instance.addAttribute(e, name, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeComment method, of class BatchWriter.
     */
    @Disabled()
    @Test
    public void testWriteComment() throws Exception {
        System.out.println("writeComment");
        String text = "";
        BatchWriter instance = null;
        instance.writeComment(text);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


}