/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.util.SOAPUtils;


import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;


import com.sicomsa.dmt.util.SvcParseException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.23
 *
 *
 */
public class DescargaSvcTest {

    static DefaultMessageFactory svcFactory;
    static DescargaSvc svc;
    static Instant now;
    static String genericRfc;
    static final Logger LOG = Logger.getLogger("com.sicomsa");

    public DescargaSvcTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        now = Instant.now();
        genericRfc = "some rfc";
        svcFactory = DefaultMessageFactory.newInstance();
        svc = new DescargaSvc(svcFactory);
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
     * Test of addContent method, of class DescargaSvc.
     */
    @Test
    public void testAddContent() throws Exception {
        System.out.println("addContent");
        SOAPMessage message = svcFactory.getMessageFactory().createMessage();
        String packageId= "somePackageId";
        testAddContent(message, genericRfc, packageId);
        testInvalidParameters(()->{testAddContent(message, genericRfc, null);});
        testInvalidParameters(()->{testAddContent(message, null, packageId);});
        testInvalidParameters(()->{testAddContent(null, genericRfc, packageId);});
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
    
    protected void testAddContent(SOAPMessage message, String rfc, String packageId) throws SOAPException, IOException {
        System.out.println("testing add Content, rfc="+rfc+",packageId:"+packageId+",soapMsg==null?"+(message==null));
        if (message != null) {
            message.getSOAPBody().removeContents();
        }
        svc.addContent(message, rfc, packageId);
        String[] result = parse(message);
        assertEquals(genericRfc, result[0]);
        assertEquals(packageId, result[1]);
    }
    
    protected void testInvalidContent(SOAPMessage message, String rfc, String packageId) throws SOAPException {
        System.out.println("testing add invalid Content, rfc="+rfc+",packageId:"+packageId+",msg=null?"+(message==null));
        svc.addContent(message, rfc, packageId);
    }

    protected String[] parse(SOAPMessage message) throws SOAPException, IOException {
        System.out.println("parsing message:");
        message.writeTo(System.out);
        System.out.println();
        SOAPElement node = SOAPUtils.parseGrandchild(
                message.getSOAPBody(),
                DescargaSvc.DOWNLOAD_REQUEST, DescargaSvc.REQUEST
        );
        String[] result = new String[2];
        result[0] = node.getAttributeValue(DescargaSvc.RFC);
        result[1] = node.getAttributeValue(DescargaSvc.PACKAGE_ID);
        return result;
    }
    
    ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * Test of parseMessage method, of class DescargaSvc.
     */
    @Test
    public void testParseReceivedMessage() throws Exception {
        System.out.println("parseReceivedMessage");
        Iterator<DescargaResponse> iterator = responseIterator();
        while (iterator.hasNext()) {
            DescargaResponse expected = iterator.next();
            String message = getMessage(expected);
            SOAPMessage smsg = SOAPUtils.fromString(message);
            DescargaResponse result = svc.parseReceivedMessage(smsg, expected.getInstant(), expected.getPackageId());
            assertEquals(expected.toString(), result.toString());
            assertEquals(expected.isAccept(), result.isAccept());
            assertEquals(expected.getPackageId(), result.getPackageId());
            assertEquals(expected.getEncodedPackage(), result.getEncodedPackage());
        }
        testParseEx(getInvalidMessage(new DescargaResponse(now, "5000", "Solicitud Aceptada", "packageid-abc", "package data")));
        testInvalidParameters(()->{invalidParseParameters1();});
        testInvalidParameters(()->{invalidParseParameters2();});
    }
    
    protected void invalidParseParameters1() throws SOAPException {
        svc.parseReceivedMessage(svcFactory.getMessageFactory().createMessage(), null, null);
    }
    protected void invalidParseParameters2() throws SOAPException {
        svc.parseReceivedMessage(null, now, null);
    }
    
    protected void testParseEx(String message) throws SOAPException, IOException {
        System.out.println("testParseEx:"+message);
        SOAPMessage smsg = SOAPUtils.fromString(message);
        Exception e = assertThrows(SvcParseException.class, ()->{svc.parseReceivedMessage(smsg, now, null);});
        System.out.println(e.getMessage());
    }
    
    protected Iterator<DescargaResponse> responseIterator() {
        String[] stss = new String[] {null, "", "   ","9999", "5000"};
        String[] msgs = new String[] {null, "", "Solicitud Aceptada"};
        String[] pids = new String[] {null, "", "  ", "apackageid-abc"};
        String[] eps  = new String[] {null, "", "encoded package content"};
        java.util.ArrayList<DescargaResponse> list = new java.util.ArrayList<>();
        for (String sts : stss) {
            for (String msg : msgs) {
                for (String pid : pids) {
                    for (String ep : eps) {
                        list.add(new DescargaResponse(now, sts, msg, pid, ep));
                    }
                }
            }
        }
        return list.iterator();
    }
/*
       
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Header>
<h:respuesta xmlns:h="http://DescargaMasivaTerceros.sat.gob.mx"
    xmlns="http://DescargaMasivaTerceros.sat.gob.mx"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    CodEstatus="5000" Mensaje="Solicitud Aceptada"/>
</s:Header>
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<RespuestaDescargaMasivaTercerosSalida xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<Paquete>UEsDBBQAAAAIAE4hMFk7Vx+M8wAA...fMDEudHh0UEsFBgAAAAABAAEAWQAAADwBAAAAAA==</Paquete>
</RespuestaDescargaMasivaTercerosSalida>
</s:Body>
</s:Envelope>
    */
    protected String getMessage(DescargaResponse dr) {
        StringBuilder sb = new StringBuilder()
                .append("""
                    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                    <s:Header>
                    <h:respuesta xmlns:h="http://DescargaMasivaTerceros.sat.gob.mx"
                        xmlns="http://DescargaMasivaTerceros.sat.gob.mx"
                        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance\"""");
        appendAttr(sb, DescargaSvc.STS_CODE, dr.getStatusCode());
        appendAttr(sb, DescargaSvc.MESSAGE, dr.getMessage());
        sb.append("""
                />
                </s:Header>
                <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                <RespuestaDescargaMasivaTercerosSalida xmlns="http://DescargaMasivaTerceros.sat.gob.mx">""");
        if (dr.getEncodedPackage() == null) {
            sb.append("<Paquete/>");
        }
        else {
            sb.append("<Paquete>").append(dr.getEncodedPackage()).append("</Paquete>");      
        }
        sb.append("</RespuestaDescargaMasivaTercerosSalida></s:Body></s:Envelope>");
        return sb.toString();
    }
    protected String getInvalidMessage(DescargaResponse dr) {
        String message = getMessage(dr);
        return message.replace("DescargaMasivaTerceros", "DescargaMacivaCuartos");
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