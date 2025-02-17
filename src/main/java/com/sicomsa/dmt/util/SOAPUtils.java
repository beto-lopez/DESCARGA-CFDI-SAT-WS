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
 * Helper class that provides simple methods for parsing SOAPMessages; converting
 * messages to String or from String; and saving or reading them from file.
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.13
 * @since 1.0
 * 
 */
public final class SOAPUtils {

    private static final Logger LOG = Logger.getLogger(SOAPUtils.class.getName());
    
    ////////////////////////////////////////////////////////////////////////////
    /// PARSE SECTION
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the integer value of an attribute with a given QName of a given SOAPElement.
     * 
     * @param element that contains the attribute to find
     * @param qname the name of the attribute to get the value from
     * @return the integer value of the qname attribute in the given element.
     * @throws IllegalArgumentException if element or qname are null
     * @throws SvcParseException if unable to cast value of attribute to Integer
     */
    public static int parseIntAttributeValue(SOAPElement element, QName qname) {
        try {
            return Integer.parseInt(parseAttributeValue(element, qname));
        }
        catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new SvcParseException("invalid int value from :("+qname+")");
        }
    }

    /**
     * Returns the long value of an attribute with a given QName of a given SOAPElement.
     * 
     * @param element that contains the attribute to find
     * @param qname the name of the attribute to get the value from
     * @return the long value of the qname attribute in the given element.
     * @throws IllegalArgumentException if element or qname are null
     * @throws SvcParseException if unable to cast value of attribute to Long
     */
    public static long parseLongAttributeValue(SOAPElement element, QName qname) {
        try {
            return Long.parseLong(parseAttributeValue(element, qname));
        }
        catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new SvcParseException("invalid long value from :("+qname+")");
        }
    }
    
    /**
     * Returns the value of an attribute with a given QName of a given SOAPElement.
     * 
     * @param element that contains the attribute to find
     * @param qname the name of the attribute to get the value from
     * @return the value of the qname attribute in the given element.
     * @throws IllegalArgumentException if element or qname are null
     */
    public static String parseAttributeValue(SOAPElement element, QName qname) {
        if (element == null || qname == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return element.getAttributeValue(qname);
    }
    
    ///////////////////////////////
    
    /**
     * Gets the immediate child Node of <code>perent</code> element with the name
     * <code>son</code> and from that found element, gets the immediate child Node
     * with the name <code>grandchild</code> and returns de Node as a SOAPElement.
     * Or it will throw SvcParceException if <code>son</code> or <code>granchild</code>
     * were not found.
     * <p>It is important to consider that this method is mostly used when a
     * parent Node should only have one child Node with a particular name and
     * that that child element will only have a child Node with an also particular
     * name. Since this method only seeks the immediate child Node at each search.</p>
     *  
     * @param parent the element to seek for the son element
     * @param son the name of the element to find in parent
     * @param grandchild the name of the element to find from the son found element
     * @return the immediate child Node of the element with the name specified in
     *         the<code>granchild</code> parameter, from the immediate child Node
     *         of the element whit the name specified in the <code>son</code>
     *         parameter, from the element <code>parent</code>.
     * @throws IllegalArgumentException y element or qname are null
     * @throws SvcParseException if the <code>son</code> element could not be found
     *         or if the <code>granchild</code> element could not be found.
     */
    public static SOAPElement parseGrandchild(SOAPElement parent, QName son, QName grandchild) {
        return parseChild(parseChild(parent, son), grandchild);
    }
    
    /**
     * Returns the immediate child Node of the given element with the specified name,
     * as a SOAPElement. This method will throw a SvcParseException if the
     * Node was not found.
     *  
     * @param element the element to parse
     * @param qname the name of the element to find
     * @return the immediate child Node of the given element with the specified name,
     *         as a SOAPElement. This method will throw a SvcParseException if the
     *         Node was not found.
     * @throws IllegalArgumentException y element or qname are null
     * @throws SvcParseException if the element did not have a child with the given qname.
     * 
     */
    public static SOAPElement parseChild(SOAPElement element, QName qname) {
        return parseChild(element, qname, true);
    }
    
    /**
     * Returns the immediate child Node of the given element with the specified name,
     * as a SOAPElement. Or null if child is not found and not required. If required
     * and not found then it will throw and exception.
     *  
     * @param element the element to parse
     * @param qname the name of the element to find
     * @param required if true child must be found or throw exception
     * @return the immediate child Node of the given element with the specified name,
     *         as a SOAPElement. Or null if child is not found and not required.
     *         If required and not found then it will throw and exception.
     * @throws IllegalArgumentException y element or qname are null
     * @throws SvcParseException if it was required to find the element and
     *         the element did not have a child with the given qname.
     * 
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
     * Returns a SOAPMessage from the given string.
     * 
     * @param string the string that contains the message
     * @return a SOAPMessage created from the given string
     * @throws SOAPException if there was a SOAP related problem
     * @throws IOException  if there were IO problems while creating message
     * @throws NullPointerException - if string is null
     * 
     */
    public static SOAPMessage fromString(String string) throws SOAPException, IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))) {
            return MessageFactory.newInstance().createMessage(new MimeHeaders(), in);
        }
    }
    
    /**
     * Writes a SOAPMessage to a file with the name specified
     * 
     * @param message the message to save
     * @param fileName the name that the file will have
     * @throws SOAPException if there were SOAP related problems
     * @throws IOException  if there IO related problems
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
     * Reads a file with the specified name, reads it, and returns contents as string
     * 
     * @param fileName the name of the file to read
     * @return the contents of the file as a string
     * @throws IOException if there were IO related problems with the file
     * @throws NullPointerException if fileName is null
     */
    public static String loadString(String fileName) throws IOException {
        Path path = new File(fileName).toPath();
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);       
    }
    
    
    /**
     * Creates a SOAPMessage from the content of a file with the specified file name
     * 
     * @param fileName the name of the file to read the message from
     * @return a SOAPMessage built from the content of the specified file
     * @throws IOException if there was any IO related problem
     * @throws SOAPException if there was a SOAP related problem
     * @throws NullPointerException if fileName is null
     */
    public static SOAPMessage loadSoap(String fileName) throws IOException, SOAPException {
        return loadSoap(new File(fileName));
    }
    
    /**
     * Creates a SOAPMessage from the content of the specified file
     * 
     * @param file the file to read the message from
     * @return a SOAPMessage built from the content of the specified file
     * @throws IOException if there was any IO related problem
     * @throws SOAPException if there was a SOAP related problem
     * @throws IllegalArgumentException if file is null
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
     * Creates a string form a SOAPMessage.
     * 
     * @param message the message to create the string from
     * @return a string that contains the message specified
     * @throws IOException if there was an IO problem
     * @throws SOAPException if there was a SOAP related problem
     * @throws NullPointerException if message is null
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
