/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.svc.SolicitaSvc;
import com.sicomsa.dmt.svc.DefaultMessageFactory;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.Node;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.logging.Logger;

import java.util.logging.Level;

import java.io.IOException;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

import com.sicomsa.dmt.QueryTest;
import com.sicomsa.dmt.util.QueryMap;
import com.sicomsa.dmt.util.QueryMapTest;

import com.sicomsa.dmt.util.SvcParseException;
import com.sicomsa.dmt.util.SOAPUtils;
import java.util.logging.Handler;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.20
 *
 */
public class SolicitaSvcTest {
    
    static DefaultMessageFactory svcFactory;
    static SolicitaSvc svc;
    static Instant now;
    
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    
    static QueryMapTest queryTest;

    public SolicitaSvcTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        now = Instant.now();
        svcFactory = DefaultMessageFactory.newInstance();
        svc = new SolicitaSvc(svcFactory);
        queryTest = new QueryMapTest();
        
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
     * Test of addContent method, of class SolicitaSvc.
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    public void testAddContent() throws SOAPException, IOException {
        System.out.println("addContent");
        String rfc = null; //not needed in this method in this svc
        testAddContent(rfc, queryTest.getFullQuery());
        testAddContent(rfc, queryTest.getEmptyQuery());
        testInvalidParameters(()->{testAddContent(rfc, null);});
        testInvalidParameters(()->{svc.addContent(null, "", queryTest.getFullQuery());});
    }
    
    
    /**
     * Test of setAttributes method, of class SolicitaSvc.
     * @throws SOAPException
     */
    @Test
    public void testSetAttributes() throws SOAPException {
        System.out.println("setAttributes");
        testSetAttributes(queryTest.getEmptyQuery());
        testSetAttributes(queryTest.getFullQuery());       
    }
    
    protected void testSetAttributes(Query query) throws SOAPException {
        SolicitaSvc asvc = new SolicitaSvc(svcFactory);
        SOAPElement element = asvc.getContext().getMessageFactory().createMessage().getSOAPBody();
        SolicitaSvc.setAttributes(element, query);
        assertSameAttributes(query, element);
    }
    
    protected void assertSameAttributes(Query query, SOAPElement element) {
        query.getAttributes().forEachRemaining(name->{
            assertEquals(query.getAttributeValue(name), element.getAttribute(name));
        });
        element.getAllAttributes().forEachRemaining(name-> {
            assertEquals(element.getAttributeValue(name), query.getAttributeValue(name.getLocalName()));
        });
        
    }
    
    protected void testInvalidParameters(Executable ex) {
        System.out.println("testInvalidpParameters");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        System.out.println(e.getMessage());
        assertEquals(e.getMessage(), "invalid parameters");
    }
    
    protected void testAddContent(String rfc, Query query) throws SOAPException, IOException {
        System.out.println("testing query:"+query);
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        svc.addContent(message, rfc, query);
        message.writeTo(System.out);
        System.out.println();
        QueryMap parsedQuery = parse(message);
        QueryTest.assertSameContent(query, parsedQuery);
    }
    
    
    
    protected QueryMap parse(SOAPMessage message) throws SOAPException, IOException {
        System.out.println("parsing:");
        message.writeTo(System.out);
        System.out.println();
        SOAPElement node = SOAPUtils.parseGrandchild(message.getSOAPBody(), SolicitaSvc.SOLICITA, SolicitaSvc.SOLICITUD);
        QueryMap.Builder parsed = new QueryMap.Builder();
        node.getAllAttributes().forEachRemaining(name-> {
            String lname = name.getLocalName();
            System.out.println("attrName="+lname);
            switch (lname) {
                case QueryMap.COMPLEMENTO-> 
                    parsed.setComplemento(node.getAttribute(lname)); 
                case QueryMap.ESTADO_COMPROBANTE->
                    parsed.setEstadoComprobante(node.getAttribute(lname));
                case QueryMap.FECHA_INICIAL->
                    parsed.setFechaInicial(LocalDateTime.parse(node.getAttribute(lname)));
                case QueryMap.FECHA_FINAL->
                    parsed.setFechaFinal(LocalDateTime.parse(node.getAttribute(lname)));
                case QueryMap.FOLIO->
                    parsed.setFolio(node.getAttribute(lname));
                case QueryMap.RFC_TERCEROS->
                    parsed.setRfcTerceros(node.getAttribute(lname));
                case QueryMap.RFC_EMISOR->
                    parsed.setRfcEmisor(node.getAttribute(lname));
                case QueryMap.RFC_SOLICITANTE->
                    parsed.setRfcSolicitante(node.getAttribute(lname));
                case QueryMap.TIPO_COMPROBANTE->
                    parsed.setTipoComprobante(node.getAttribute(lname));
                case QueryMap.TIPO_SOLICITUD->
                    parsed.setTipoSolicitud(node.getAttribute(lname));
                default->
                    throw new IllegalArgumentException("attribute name not found:"+lname);
            }
        });
        node.getChildElements(SolicitaSvc.RFC_RECEPTORES);
        SOAPElement rNode = SOAPUtils.parseChild(node, SolicitaSvc.RFC_RECEPTORES, false);
        if (rNode != null) {
            Iterator<Node> i = rNode.getChildElements(SolicitaSvc.RFC_RECEPTOR);
            while (i.hasNext()) {
                parsed.addReceptor(i.next().getTextContent());
            }
        }
        QueryMap qm = parsed.build();
        System.out.println("parsed:"+qm);
        return qm;
    }
    
    
    String validHeader = """
                         <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                         <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                         <SolicitaDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
                         <SolicitaDescargaResult""";
    
    String validTail = "/></SolicitaDescargaResponse></s:Body></s:Envelope>";
    
    String invalidHeader = """
                         <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                         <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                         <SolicitaDescargaResponse xmlns="http://DescargaXXXMasivaTerceros.sat.gob.mx">
                         <SolicitaDescargaResult""";
    
  
    /**
     * Test of parseReceivedMessage method, of class SolicitaSvc.
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    public void testParseReceivedMessage() throws SOAPException, IOException {
        System.out.println("parseReceivedMessage");
        String[] messages = new String[] {null, "", "   ", "a message"};
        String[] ids = new String[] {null, "", "  ", "an id"};
        String[] codes    = new String[] {null, "", "  ", "12345", "5000"};
        for (String message : messages) {
            for (String id : ids) {
                for (String code : codes) {
                    test(code, message, id);
                }
            }
        }
        String message10 = " IdSolicitud=\"e5d08laksjd\" CodEstatus=\"5000\" Mensaje=\"Solicitud xds\"";
        SolicitaResponse response10 = new SolicitaResponse(now, "5000", "Solicitud xds", "e5d08laksjd" );
        boolean accept10 = true;
        testInvalidResponse(message10, response10, accept10);
        testInvalidParameters(()->{invalidParseParameters1();});
        testInvalidParameters(()->{invalidParseParameters2();});
    }
    
    protected void invalidParseParameters1() throws SOAPException {
        svc.parseReceivedMessage(svcFactory.getMessageFactory().createMessage(), null, null);
    }
    protected void invalidParseParameters2() throws SOAPException {
        svc.parseReceivedMessage(null, now, null);
    }
    
    protected boolean shouldAccept(String statusCode, String requestId) {
        return "5000".equals(statusCode) && requestId != null && !requestId.isBlank();
    }
    protected String getMessage(String statusCode, String msg, String requestId) {
        StringBuilder sb = new StringBuilder("");
        appendAttr(sb, "CodEstatus", statusCode);
        appendAttr(sb, "Mensaje", msg);
        appendAttr(sb, "IdSolicitud", requestId);
        return sb.toString();
    }
    protected void appendAttr(StringBuilder sb, String name, String value) {
        if (value != null) {
            sb.append(" ").append(name).append("=\"").append(value).append("\"").toString();
        }
    }
    
    protected void test(String statusCode, String msg, String requestId) throws SOAPException, IOException {
        String attrs = getMessage(statusCode, msg, requestId);
        boolean shouldAccept=shouldAccept(statusCode, requestId);
      //  System.out.println("testing attrs:"+attrs+",shouldAccept="+shouldAccept);
        String message = validHeader.concat(attrs).concat(validTail);
     //   System.out.println("testing response:"+message);
        SOAPMessage soapMsg = SOAPUtils.fromString(message);
        Instant instant = now;
        SolicitaResponse expected = new SolicitaResponse(now, statusCode, msg, requestId);
        SolicitaSvc instance = svc;
        SolicitaResponse result = instance.parseReceivedMessage(soapMsg, instant, null);
       // System.out.println("result="+result+",isAccept="+result.isAccept());
        assertEquals(expected.toString(), result.toString());
        assertEquals(shouldAccept, result.isAccept()); 
    }
    
    protected void testInvalidResponse(String message, SolicitaResponse expected, boolean accept)
            throws SOAPException,IOException {
        message = invalidHeader.concat(message).concat(validTail);
     //   System.out.println("testing response:"+message);
        SOAPMessage msg = SOAPUtils.fromString(message);
        Instant instant = now;
        Exception e = assertThrows(SvcParseException.class, ()->{getResponse(msg,instant);});
        System.out.println(e.getMessage()); 
    }
    
    protected SolicitaResponse getResponse(SOAPMessage message, Instant instant) throws SOAPException {
        return svc.parseReceivedMessage(message, instant, null);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    
    /*
    
    String validResponseContent1 =
""" 
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<SolicitaDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<SolicitaDescargaResult IdSolicitud="e5d08laksjdfjxcd-asdf47f-ca-dgfhd48e8-a60d-354643567a52c6" CodEstatus="5000" Mensaje="Solicitud Aceptada"/>
</SolicitaDescargaResponse>
</s:Body>
</s:Envelope>
""";
    String rejectResponseContent1 =
""" 
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<SolicitaDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<SolicitaDescargaResult IdSolicitud="e5d08laksjdfjxcd-asd" CodEstatus="5444" Mensaje="Solicitud no Aceptada"/>
</SolicitaDescargaResponse>
</s:Body>
</s:Envelope>
""";
 
    */
   
}