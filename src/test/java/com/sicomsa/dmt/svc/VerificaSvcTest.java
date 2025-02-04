/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.PackageIds;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;

import javax.xml.namespace.QName;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import org.junit.jupiter.api.function.Executable;

import com.sicomsa.dmt.util.SvcParseException;
import com.sicomsa.dmt.util.SOAPUtils;

import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.22
 *
 *
 */
public class VerificaSvcTest {
    

    static DefaultMessageFactory svcFactory;
    static VerificaSvc svc;
    static Instant now;
    static String genericRfc;
    
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    ////////////////////////////////////////////////////////////////////////////    
    
    public VerificaSvcTest() throws SOAPException {
    }

    @BeforeAll
    public static void setUpClass() {
        genericRfc = "some rfc";
        svcFactory = DefaultMessageFactory.newInstance();
        svc = new VerificaSvc(svcFactory);
        now = Instant.now();
        LOG.setLevel(Level.OFF);
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
     * Test of addContent method, of class VerificaSvc.
     * 
    protected SOAPElement addContent(SOAPMessage message, Credentials credentials, String requestId) throws SOAPException {
     */
    @Test
    public void testAddContent() throws Exception {
        System.out.println("addContent");
        SOAPMessage message = svcFactory.getMessageFactory().createMessage();
        String requestId= "someRequestId";
        testAddContent(message, genericRfc, requestId);
        testInvalidParameters(()->{testAddContent(message, genericRfc, null);});
        testInvalidParameters(()->{testAddContent(message, null, requestId);});
        testInvalidParameters(()->{testAddContent(null, genericRfc, requestId);});
    }
    
    /**
     * pasar este a testutils
     * @param ex 
     */
    protected void testInvalidParameters(Executable ex) {
        System.out.println("testInvalidParameters");
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        System.out.println(e.getMessage());
        assertEquals(e.getMessage(), "invalid parameters");
    }
    
    protected void testAddContent(SOAPMessage message, String rfc, String requestId) throws SOAPException, IOException {
        System.out.println("testing add Content, rfc="+rfc+",requestId:"+requestId+",soapMsg==null?"+(message==null));
        if (message != null) {
            message.getSOAPBody().removeContents();
        }
        svc.addContent(message, rfc, requestId);
        String[] result = parse(message);
        assertEquals(genericRfc, result[0]);
        assertEquals(requestId, result[1]);
    }
    
    protected void testInvalidContent(SOAPMessage message, String rfc, String requestId) throws SOAPException {
        System.out.println("testing add invalid Content, rfc="+rfc+",requestId:"+requestId+",msg=null?"+(message==null));
        svc.addContent(message, rfc, requestId);
    }

    protected String[] parse(SOAPMessage message) throws SOAPException, IOException {
        System.out.println("parsing message:");
        message.writeTo(System.out);
        System.out.println();
        SOAPElement node = SOAPUtils.parseGrandchild(
                message.getSOAPBody(),
                VerificaSvc.VERIFICA_NAME, VerificaSvc.SOLICITUD_NAME
        );
        String[] result = new String[2];
        result[0] = node.getAttributeValue(VerificaSvc.RFC_NAME);
        result[1] = node.getAttributeValue(VerificaSvc.ID_NAME);
        return result;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Test of parseMessage method, of class VerificaSvc.
     */
    @Disabled("Already tested, takes too long. Enable to retest")
    @Test
    public void testParseReceivedMessage() throws Exception {
        System.out.println("parseMessage");
        Iterator<VerificaResponse> iterator = validResponseIterator();
        while (iterator.hasNext()) {
            VerificaResponse expected = iterator.next();
            String message = getMessage(expected);
            SOAPMessage smsg = SOAPUtils.fromString(message);
            VerificaResponse result = svc.parseReceivedMessage(smsg, expected.getInstant(), expected.getRequestId());
            assertEquals(expected.toString(), result.toString());
            assertEquals(expected.isAccept(), result.isAccept());
            assertEquals(expected.getRequestId(), result.getRequestId());
            assertEquals(expected.isDelay(), result.isDelay());
        }
        testParseEx(invalidStateMessage1());
        testParseEx(invalidStateMessage2());
        testParseEx(invalidCfdisMessage1());
        testParseEx(invalidCfdisMessage2());
        testParseEx(invalidMsg());
        testInvalidParameters(()->{invalidParseParameters1();});
        testInvalidParameters(()->{invalidParseParameters2();});
    }
    
    protected void invalidParseParameters1() throws SOAPException {
        svc.parseReceivedMessage(svcFactory.getMessageFactory().createMessage(), null, null);
    }
    protected void invalidParseParameters2() throws SOAPException {
        svc.parseReceivedMessage(null, now, "any");
    }
    
    
    protected void testParseEx(String message) throws SOAPException, IOException {
        System.out.println("testParseEx:"+message);
        SOAPMessage smsg = SOAPUtils.fromString(message);
        Exception e = assertThrows(SvcParseException.class, ()->{svc.parseReceivedMessage(smsg, now,"any");});
        System.out.println(e.getMessage());  
    }
    
    protected Iterator<VerificaResponse> validResponseIterator() {
        Instant i = Instant.now();
        String[] stss = new String[] {null, "", "   ", "800", "5000"};
        String[] msgs = new String[] {null, "", "   ", "Solicitud Aceptada"};
        String[] sscs = new String[] {null, "", "   ", "444", "5000"};
        String[] rids = new String[] {null, "", "  ", "package-abc"};
        int[] statess = new int[]{-1,0,1,2,6,7,800};
        int[] cfdiss = new int[]{0,10,500};
        List<List<String>> pss = new java.util.ArrayList<>(2);
        pss.add(List.of("package1"));
        pss.add(List.of("package1", "package2", "package3"));
        List<VerificaResponse> list = new java.util.LinkedList<>();
        for (String sts: stss) {
            for (String msg: msgs) {
                for (String ssc : sscs) {
                    for (String rid : rids) {
                        for (int state : statess) {
                            for (int cfdis: cfdiss) {
                                pss.forEach(item-> {
                                    list.add(new VerificaResponse(i,sts, msg, state, ssc, rid, cfdis, new PackageIds(item)));
                                });
                            }
                        }
                    }
                }
                    
            }
        }
        return list.iterator();
    }
/*
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<VerificaSolicitudDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<VerificaSolicitudDescargaResult CodEstatus="5000" EstadoSolicitud="3" CodigoEstadoSolicitud="5000" NumeroCFDIs="163" Mensaje="Solicitud Aceptada">
<IdsPaquetes>CE03F-FBD8-4616-AE06063_01</IdsPaquetes> //reducted
</VerificaSolicitudDescargaResult>
</VerificaSolicitudDescargaResponse>
</s:Body>
</s:Envelope>
    */
    protected String invalidStateMessage1() {
        String attributes = " CodEstatus=\"5000\" EstadoSolicitud=\"abc\" CodigoEstadoSolicitud=\"5000\" NumeroCFDIs=\"163\"";
        return getValidMessage(attributes, "");
    }
    protected String invalidStateMessage2() {
        String attributes = " CodEstatus=\"5000\" CodigoEstadoSolicitud=\"5000\" NumeroCFDIs=\"163\"";
        return getValidMessage(attributes, "");
    }
    protected String invalidCfdisMessage1() {
        String attributes = " CodEstatus=\"5000\" EstadoSolicitud=\"5\" CodigoEstadoSolicitud=\"5000\" NumeroCFDIs=\"abc\"";
        return getValidMessage(attributes, "");
    }
    protected String invalidCfdisMessage2() {
        String attributes = " CodEstatus=\"5000\" EstadoSolicitud=\"5\" CodigoEstadoSolicitud=\"5000\"";
        return getValidMessage(attributes, "");
    }
    protected String invalidMsg() {
        String attributes = " CodEstatus=\"5000\" EstadoSolicitud=\"3\" CodigoEstadoSolicitud=\"5000\" NumeroCFDIs=\"163\"";
        return getInvalidMessage(attributes, "");
    }
    
    protected String getValidHeader() {
        return 
            """
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
            <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
            <VerificaSolicitudDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
            <VerificaSolicitudDescargaResult""";
    }
    protected String getInvalidHeader() {
        return 
            """
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
            <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
            <VerificaSolicitudDescargaResponse xmlns="http://DescaXXXrgaMasivaTerceros.sat.gob.mx">
            <VerificaSolicitudDescargaResult""";
    }
    protected String getValidTail() {
        return "</VerificaSolicitudDescargaResult></VerificaSolicitudDescargaResponse></s:Body></s:Envelope>";
    }
    protected String getMessage(VerificaResponse vr) {
        return getValidMessage(getAttributes(vr), getIdsPaquetes(vr.getPackageIds()));
    }
    protected String getValidMessage(String attributes, String packages) {
        return new StringBuilder(getValidHeader())
                .append(attributes)
                .append(">")
                .append(packages)
                .append(getValidTail())
                .toString();
    }
    protected String getInvalidMessage(String attributes, String packages) {
        return new StringBuilder(getInvalidHeader())
                .append(attributes)
                .append(">")
                .append(packages)
                .append(getValidTail())
                .toString();
    }

    protected String getAttributes(VerificaResponse vr) {
        StringBuilder sb = new StringBuilder("");
        appendAttr(sb, VerificaSvc.STS_CODE, vr.getStatusCode());
        appendAttr(sb, VerificaSvc.MESSAGE, vr.getMessage());
        appendAttr(sb, VerificaSvc.STATE_NAME, vr.getSolicitudeState());
        appendAttr(sb, VerificaSvc.SOLICITUDE_STS_CODE_NAME, vr.getSolicitudeStsCode());
        appendAttr(sb, VerificaSvc.CFDIS_NAME, vr.getCfdis());
        return sb.toString();
    }
   
    protected String getIdsPaquetes(PackageIds info) {
        if (info == null || info.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int idx = 0; idx < info.size(); idx++) {
            sb.append("<IdsPaquetes>").append(info.getPackageId(idx)).append("</IdsPaquetes>");
        }
        return sb.toString();
    }
    protected void appendAttr(StringBuilder sb, QName name, int value) {
        appendAttr(sb, name, Integer.toString(value));
    }
    protected void appendAttr(StringBuilder sb, QName name, String value) {
        if (value != null) {
            sb.append(" ")
                    .append(name.getLocalPart())
                    .append("=\"").append(value).append("\"")
                    .toString();
        }
    }
    
}