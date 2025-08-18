/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.QueryBuilder;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;


/**
 * <code>SolicitaService</code> test class.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.10
 * @since       1.5 
 */
public class SolicitaServiceTest {
    
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    
    protected RequestContext defaultCtx; //emitidos
    protected SolicitaService testSvc;
    
    public SolicitaServiceTest() {
        
        LOG.setLevel(Level.ALL);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
        DefaultRequestContextTest test = new DefaultRequestContextTest();
        defaultCtx = test.getDefaultTestContext();
        testSvc = new SolicitaService(DefaultMessageFactory.newInstance(), defaultCtx); 
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
     * Test of getRequestContext method, of class SolicitaService.
     */
    @Test
    public void testGetRequestContext() {
        System.out.println("getRequestContext");
        RequestContext expResult = new DefaultRequestContext();
        SolicitaService instance = new SolicitaService(DefaultMessageFactory.newInstance(), expResult); 
        RequestContext result = instance.getRequestContext();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getServiceName method, of class SolicitaService.
     */
    @Test
    public void testGetServiceName() {
        System.out.println("getServiceName");
        SolicitaService instance = testSvc;
        String expResult = defaultCtx.getServiceName();
        String result = instance.getServiceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLocation method, of class SolicitaService.
     */
    @Test
    public void testGetLocation() {
        System.out.println("getLocation");
        SolicitaService instance = testSvc;
        String expResult = defaultCtx.getLocation();
        String result = instance.getLocation();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSoapAction method, of class SolicitaService.
     */
    @Test
    public void testGetSoapAction() {
        System.out.println("getSoapAction");
        SolicitaService instance = testSvc;
        String expResult = defaultCtx.getSoapAction();
        String result = instance.getSoapAction();
        assertEquals(expResult, result);
    }

    /**
     * Test of addContent method, of class SolicitaService.
     */
    @Test
    public void testAddContent() throws Exception {
        System.out.println("addContent");
        String rfc = "";
        List<String> receptores = List.of("abc", "rec2", "rec3", "rect4");
        Query query = getEmitidosQuery(rfc, "Metadata", receptores);
        testAddContent(query);
    }
    
    public void testAddContent(Query query) throws Exception {
        System.out.println("addContent:"+query);
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        String rfc = "";
        SolicitaService instance = testSvc;
        instance.addContent(message, rfc, query);
        System.out.println("result after addContent:");
        message.writeTo(System.out);
        System.out.println();
        SOAPElement result = getResultElement(message.getSOAPBody());
        assertAllSet(query, result);
        assertSameReceptors(query.getRfcReceptores(), result);        
    }
    
    protected void assertAllSet(Query query, SOAPElement result) {

        query.getAttributes().forEachRemaining(attr->{
            assertEquals(query.getAttributeValue(attr), result.getAttribute(attr));
        });
     
    }
    
    protected SOAPElement getResultElement(SOAPElement body) {
        
        SOAPElement result = SOAPUtils.parseGrandchild(
                body,
                defaultCtx.getParentRequestName(),
                defaultCtx.getChildRequestName());
        return result;
    }
    
    protected void assertSameReceptors(Set<String> receptorsSet, SOAPElement element) {
        System.out.println("assertSameReceptors:"+receptorsSet);
        SOAPElement receptorsElement = SOAPUtils.parseChild(element, SolicitaService.RFC_RECEPTORES, false);
        if (receptorsSet == null || receptorsSet.isEmpty()) {
            assertNull(receptorsElement);
        }
        else {
            assertNotNull(receptorsElement);
            List<String> receptorsList = parseReceptores(receptorsElement);
            assertTrue(receptorsSet.containsAll(receptorsList));
            assertEquals(receptorsSet.size(), receptorsList.size());
        }
    }
    
    protected List<String> parseReceptores(SOAPElement element) {
        ArrayList<String> list = new ArrayList<>(5);
        element.getChildElements(SolicitaService.RFC_RECEPTOR).forEachRemaining(node-> {
            list.add(node.getTextContent());
        });
        return list;
    }
    
    protected Query getEmitidosQuery(String rfc, String tipo, List<String> receptores) {
        return new QueryBuilder()
                .setRfcEmisor(rfc).setRfcSolicitante(rfc)
                .setFechaInicial(LocalDateTime.now())
                .setFechaFinal(LocalDateTime.now().plusMinutes(100))
                .setTipoSolicitud(tipo) //"CFDI")
                .buildEmitidosQuery(receptores);            
    }
    
    protected Query getRecibidosQuery(String emisor, String rfc, String tipoSolicitud) {
        
        return new QueryBuilder()
                .setRfcEmisor(emisor).setRfcSolicitante(rfc)
                .setFechaInicial(LocalDateTime.now())
                .setFechaFinal(LocalDateTime.now().plusMinutes(100))
                .setTipoSolicitud(tipoSolicitud) //"Metadata") //"CFDI")
                .buildRecibidosQuery(rfc);          
    }

    /**
     * Test of addSvcContent method, of class SolicitaService.
     */
    //@Test
    public void testAddSvcContent() throws Exception {
        System.out.println("addSvcContent");
        List<String> receptores = List.of("abc", "rec2", "rec3", "rect4");
        Query query = getEmitidosQuery("", "CFDI", receptores);
        testAddSvcContent(query);
    }
    
    public void testAddSvcContent(Query query) throws Exception {
        System.out.println("addSvcContent:"+query);
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SolicitaService instance = testSvc;
        SOAPElement result = instance.addSvcContent(message.getSOAPBody(), query);
        assertAllSet(query, result);
        assertSameReceptors(query.getRfcReceptores(), result);        
    }

    /**
     * Test of parseReceivedMessage method, of class SolicitaService.
     */
    //@Test
    public void testParseReceivedMessage() throws Exception {
        System.out.println("parseReceivedMessage");
        SOAPMessage message = SOAPUtils.fromString(validResponseContent1);
        Instant instant = Instant.now();
        Query request = null;
        SolicitaService instance = testSvc;
        SolicitaService.SolicitaResponseExtended expResult = getExpectedResult1(instant);
        SolicitaResponse result = instance.parseReceivedMessage(message, instant, request);
        assertSameResponse(expResult, result);
    }

    /**
     * Test of parseSvcResponse method, of class SolicitaService.
     */
    //@Test
    public void testParseSvcResponse() throws Exception {
        System.out.println("parseSvcResponse");
        Instant instant = Instant.now();
        SOAPElement element =
                SOAPUtils.fromString(validResponseContent1).getSOAPBody();
        SolicitaService instance = testSvc;
        SolicitaService.SolicitaResponseExtended expResult = getExpectedResult1(instant);
        SolicitaResponse result = instance.parseSvcResponse(element, instant);
        assertSameResponse(expResult, result);
    }

    /**
     * Test of getResponse method, of class SolicitaService.
     * @throws SOAPException
     * @throws java.io.IOException 
     */
    //@Test
    public void testGetResponse() throws SOAPException, java.io.IOException {
        System.out.println("getResponse");
        Instant instant = Instant.now();
        SOAPMessage message = SOAPUtils.fromString(validResponseContent1);
        SolicitaService instance = this.testSvc;
        
        SOAPElement resultElement = SOAPUtils.parseGrandchild(
                message.getSOAPBody(),
                this.defaultCtx.getResponseName(),
                this.defaultCtx.getResultName());
        
        SolicitaService.SolicitaResponseExtended expResult = getExpectedResult1(instant);
        SolicitaResponse result = instance.getResponse(instant, resultElement);
        assertSameResponse(expResult, result);
    }
    
    protected  SolicitaService.SolicitaResponseExtended getExpectedResult1(Instant instant) {
         SolicitaService.SolicitaResponseExtended response = 
                new  SolicitaService.SolicitaResponseExtended(
                instant, "5000", "Solicitud Aceptada", "e5d08laksjdfjxcd-asdf47f-ca-dgfhd48e8-a60d-354643567a52c6");
        response.setRfcSolicitante("RFC SOLICITANTE");
        return response;
    }
    
    protected void assertSameResponse( SolicitaService.SolicitaResponseExtended r1, SolicitaResponse r22) {
        assertTrue(r22 instanceof  SolicitaService.SolicitaResponseExtended);
         SolicitaService.SolicitaResponseExtended r2 = ( SolicitaService.SolicitaResponseExtended)r22;
        assertEquals(r1.getRequestId(), r2.getRequestId());
        assertEquals(r1.getInstant(), r2.getInstant());
        assertEquals(r1.getMessage(), r2.getMessage());
        assertEquals(r1.getStatusCode(), r2.getStatusCode());
        assertEquals(r1.getRfcSolicitante(), r2.getRfcSolicitante());
    }
    
    
    String validResponseContent1 = """ 
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<SolicitaDescargaEmitidosResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<SolicitaDescargaEmitidosResult RfcSolicitante="abcd" IdSolicitud="e5d08laksjdfjxcd-asdf47f-ca-dgfhd48e8-a60d-354643567a52c6" CodEstatus="5000" Mensaje="Solicitud Aceptada"/>
</SolicitaDescargaResponse>
</s:Body>
</s:Envelope>""";

    
}
