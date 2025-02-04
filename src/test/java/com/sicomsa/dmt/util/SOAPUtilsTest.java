/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;


import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import javax.xml.namespace.QName;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.12.14
 *
 *
 */
public class SOAPUtilsTest {
    
    @TempDir
    static Path tempDir;
    static File file1;
    static File file2;
    
    static SOAPFactory factory;

    public SOAPUtilsTest() {
    }

    @BeforeAll
    public static void setUpClass() throws IOException, SOAPException {
        file1 = Files.createFile(tempDir.resolve("test1.xml")).toFile();
        file2 = Files.createFile(tempDir.resolve("test2.xml")).toFile();
        factory = SOAPFactory.newInstance();
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
     * Test of parseIntAttributeValue method, of class SOAPUtils.
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    public void testParseIntAttributeValue() throws SOAPException, IOException {
        System.out.println("parseIntAttributeValue");
        QName qname = new QName("http://DescargaMasivaTerceros.sat.gob.mx", "VerificaSolicitudDescargaResponse", "s");
        SOAPElement element = factory.createElement(qname);
        QName alphaName = new QName("alphaNumber");
        element.addAttribute(alphaName, "alpha");
        Exception e = assertThrows(SvcParseException.class, ()-> {
            SOAPUtils.parseIntAttributeValue(element, alphaName);
        });
        System.out.println(e.getMessage());
        QName number99Name = new QName("number99");
        element.addAttribute(number99Name, "99");
        int expResult = 99;
        int result = SOAPUtils.parseIntAttributeValue(element, number99Name);
        assertEquals(expResult, result);
    }

    String msg = """
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
            <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                <VerificaSolicitudDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
                    <VerificaSolicitudDescargaResult CodEstatus="FOUND U" EstadoSolicitud="5" CodigoEstadoSolicitud="5001" NumeroCFDIs="0" Mensaje="Solicitud Aceptada"/>
                </VerificaSolicitudDescargaResponse>
            </s:Body>
        </s:Envelope>""";
    
    /**
     * Test of parseGrandchild method, of class SOAPUtils.
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    public void testParseGrandchild() throws SOAPException, IOException {
        System.out.println("parseGrandchild");
        QName son   = new QName("http://DescargaMasivaTerceros.sat.gob.mx","VerificaSolicitudDescargaResponse");
        QName child = new QName("http://DescargaMasivaTerceros.sat.gob.mx", "VerificaSolicitudDescargaResult");
        QName sonX  = new QName("http://DescarWWWgaMasivaTerceros.sat.gob.mx","VerificaSolicitudDescargaResponse");
        QName childX= new QName("http://DescargaMasivaTerceros.sat.gob.mx", "VerificaSolWWWicitudDescargaResult");
        
        SOAPMessage message = SOAPUtils.fromString(msg);
        SOAPElement result = SOAPUtils.parseGrandchild(message.getSOAPBody(), son, child);
        assertEquals("FOUND U", result.getAttribute("CodEstatus"));
        
        Exception e = assertThrows(SvcParseException.class, ()-> {
            SOAPUtils.parseGrandchild(message.getSOAPBody(), son, childX);
        });
        System.out.println(e.getMessage());
        
        e = assertThrows(SvcParseException.class, ()-> {
            SOAPUtils.parseGrandchild(message.getSOAPBody(), sonX, child);
        });
        System.out.println(e.getMessage());
    }

    /**
     * Test of parseChild method, of class SOAPUtils.
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    public void testParseChild_3args() throws SOAPException, IOException {
        System.out.println("parseChild");
        SOAPBody body = SOAPUtils.fromString(msg).getSOAPBody();
        QName qname = new QName("http://DescargaMasivaTerceros.sat.gob.mx","VerificaSolicitudDescargaResponse");

        assertDoesNotThrow(()-> {
            final SOAPElement result = SOAPUtils.parseChild(body, qname);
            QName rname = result.getElementQName();
            System.out.println("qnameFound="+rname);
            assertEquals(qname.getLocalPart(), rname.getLocalPart());
            assertEquals(qname.getNamespaceURI(), rname.getNamespaceURI());
        });
        
        QName xname = new QName("alskdjffsdñf", "alskdjfdñ");
        Exception e = assertThrows(SvcParseException.class, ()-> {
            SOAPUtils.parseChild(body, xname);
        });
        System.out.println(e.getMessage());
        
        assertNull(SOAPUtils.parseChild(body, xname, false));
    }


    /**
     * Test of fromString method, of class SOAPUtils.
     */
    @Test
    public void testFromString() throws Exception {
        System.out.println("fromString");
        SOAPMessage message = SOAPUtils.fromString(msg);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            message.writeTo(out);
            out.flush();
            String result = new String(out.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("result ="+result);
            assertEquals(msg, result);
        }
        System.out.println("fromString(null)");
        Exception e = assertThrows(NullPointerException.class, ()->{
            SOAPUtils.fromString(null);
        });
        System.out.print(e.getMessage());
        
        System.out.println("fromString(blank)");
        SOAPUtils.fromString(" ");
        
        System.out.println("fromString(grabage)");
        SOAPUtils.fromString("asñdlkfjañlsdfkjjasldk");
    }

    /**
     * Test of write method, of class SOAPUtils.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        SOAPMessage message = SOAPUtils.fromString(msg);
        String fileName = file1.getAbsolutePath();
        SOAPUtils.write(message, fileName);
        String readMessage = SOAPUtils.loadString(fileName);
        System.out.println("read message="+readMessage);
        assertEquals(msg, readMessage);
        
        System.out.println("write (null, file)");
        Exception e = assertThrows(NullPointerException.class, ()->{
            SOAPUtils.write(null, fileName);
        });
        System.out.println(e.getMessage());
        
        System.out.println("write (msg, null)");
        e = assertThrows(NullPointerException.class, ()-> {
            SOAPUtils.write(message, null);
        });
        System.out.println(e.getMessage());
    }

    /**
     * Test of loadString method, of class SOAPUtils.
     */
    @Test
    public void testLoadString() throws Exception {
        System.out.println("loadString");
        SOAPMessage message = SOAPUtils.fromString(msg);
        String fileName = file2.getAbsolutePath();
        SOAPUtils.write(message, fileName);
        String readMessage = SOAPUtils.loadString(fileName);
        System.out.println("read message="+readMessage);
        assertEquals(msg, readMessage);
        
        System.out.println("loadString (null)");
        Exception e = assertThrows(NullPointerException.class, ()-> {
            SOAPUtils.loadString(null);
        });
        System.out.println(e.getMessage());
        
        System.out.println("loadString (notfound)");
        e = assertThrows(IOException.class, ()-> {
            SOAPUtils.loadString("invalid path file name");
        });
        System.out.println(e.getMessage());
        
    }

}