/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;


import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import javax.xml.namespace.QName;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.11.23
 * @version 2025.01.13
 *      added parselong y parseString. untested.
 */
public final class SOAPUtils {

    private static final Logger LOG = Logger.getLogger(SOAPUtils.class.getName());
    
    ////////////////////////////////////////////////////////////////////////////
    /// PARSE SECTION
    ////////////////////////////////////////////////////////////////////////////
    
    public static int parseIntAttributeValue(SOAPElement element, QName qname) {
        try {
            return Integer.parseInt(parseAttributeValue(element, qname));
        }
        catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new SvcParseException("invalid int value from :("+qname+")");
        }
    }

    public static long parseLongAttributeValue(SOAPElement element, QName qname) {
        try {
            return Long.parseLong(parseAttributeValue(element, qname));
        }
        catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new SvcParseException("invalid long value from :("+qname+")");
        }
    }
    
    public static String parseAttributeValue(SOAPElement element, QName qname) {
        if (element == null || qname == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return element.getAttributeValue(qname);
    }
    
    public static SOAPElement parseGrandchild(SOAPElement parent, QName son, QName grandchild) {
        return parseChild(parseChild(parent, son), grandchild);
    }
    
    /**
     * @param element
     * @param qname
     * @return 
     */
    public static SOAPElement parseChild(SOAPElement element, QName qname) {
        return parseChild(element, qname, true);
    }
    
    /**
     * @param element
     * @param qname
     * @param required
     * @return 
     */
    public static SOAPElement parseChild(SOAPElement element, QName qname, boolean required) {
        if (element == null || qname == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        java.util.Iterator<Node> iterator = element.getChildElements(qname);
        if (iterator.hasNext()) {
            return (SOAPElement)iterator.next();
        }
        if (required) {
            LOG.log(Level.SEVERE, "Required child node not found ({0})", qname);
            throw new SvcParseException("Child node not found:("+qname+")");
        }
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * @param string
     * @return
     * @throws SOAPException
     * @throws java.io.IOException 
     * @throws NullPointerException - if string is null
     * 
     */
    public static SOAPMessage fromString(String string) throws SOAPException, IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))) {
            return MessageFactory.newInstance().createMessage(new MimeHeaders(), in);
        }
    }
    
    /**
     * 
     * @param message
     * @param fileName
     * @throws SOAPException
     * @throws IOException 
     * @throws NullPointerException y message or fileName are null
     */
    public static void write(SOAPMessage message, String fileName) throws SOAPException, IOException {
        if (message == null || fileName == null) {
            throw new NullPointerException("invalid parameters");
        }
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            try (BufferedOutputStream bos = new BufferedOutputStream(out)) {
                message.writeTo(bos);
                bos.flush();
            }
        }
    }
    
    /**
     * 
     * @param fileName
     * @return
     * @throws IOException 
     * @throws NullPointerException if fileName is null
     * ver notas de fromString arriba
     * 
     */
    public static String loadString(String fileName) throws IOException {
        Path path = new File(fileName).toPath();
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);       
    }
    
    
    /**
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @throws SOAPException 
     */
    public static SOAPMessage loadSoap(String fileName) throws IOException, SOAPException {
        return loadSoap(new File(fileName));
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     * @throws SOAPException 
     */
    public static SOAPMessage loadSoap(File file) throws IOException, SOAPException {
        if (file == null) {
            throw new IllegalArgumentException("invalid file");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                return MessageFactory.newInstance()
                        .createMessage(new MimeHeaders(), bis);
            }
        }
    }
    
    /**
     * 
     * @param message
     * @return
     * @throws IOException
     * @throws SOAPException 
     */
    public static String toString(SOAPMessage message) throws IOException, SOAPException {
        if (message == null) {
            throw new NullPointerException("null message");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            message.writeTo(out);
            out.flush();
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
