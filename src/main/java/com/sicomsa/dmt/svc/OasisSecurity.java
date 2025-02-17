/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import javax.xml.namespace.QName;

import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPHeaderElement;
import jakarta.xml.soap.SOAPException;

import java.util.Base64;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;

/**
 * Utility methods for signing envelopes according to the security requested
 * by the WS of CFDI downloads.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.28
 * @since 1.0
 * 
 * 
 */
public class OasisSecurity {
    /**
     * Security URI
     */
    public static final String WSSE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    /**
     * Security's utility URI
     */
    public static final String WSU =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    
    /**
     * Name of value type attribute
     */
    public static final QName VALUE_TYPE = new QName("ValueType");
    
    /**
     * Value of value type attribute
     */
    public static final String TOKEN_PROFILE_URI = 
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
    
    /**
     * WSSE prefix
     */
    protected final String wsse;
    
    /**
     * WSU prefix
     */
    protected final String wsu;
    
    /**
     * Name of security
     */
    protected QName qSecurity;
    
    /**
     * Name of timestamp
     */
    protected QName qTimestamp;
    
    /**
     * Name of timestamp created
     */
    protected QName qCreated;
    
    /**
     * Name of timestamp expires
     */
    protected QName qExpires;
    
    /**
     * Name of WSU id
     */
    protected QName qWsuId;
    
    /**
     * Name of binary security token
     */
    protected QName qBsToken;
    
    /**
     * Name of security token reference
     */
    protected QName qSecurityTokenRef;
    
    /**
     * Name of security token reference ref
     */
    protected QName qSecurityReferenceRef;
    
    /**
     * Timestamp URI
     */
    protected String timestampIdUri = "TS";
    
    
    private Base64.Encoder _encoder;
    
    private long _secondsToAdd = 60 * 5; //5 minutes
    
    private DateTimeFormatter _formatter;
    
    /**
     * Creates a new default implementation of OasisSecurity
     */
    public OasisSecurity() {
        this("wsse", "wsu");
    }
    
    /**
     * Creates a new OasisSecurity with specified prefixes
     * 
     * @param wsse WSSE prefix
     * @param wsu  WSU prefix
     */
    public OasisSecurity(String wsse, String wsu) {
        if (wsse == null || wsu == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.wsse = wsse;
        this.wsu  = wsu;
        _formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
        qSecurity = new QName(WSSE, "Security", wsse);
        qTimestamp = new QName(WSU, "Timestamp", wsu);
        qCreated = new QName(WSU, "Created", wsu);
        qExpires = new QName(WSU, "Expires", wsu);
        qWsuId = new QName(WSU, "Id", wsu);
        qBsToken = new QName(WSSE, "BinarySecurityToken", wsse);
        qSecurityTokenRef = new QName(WSSE, "SecurityTokenReference", wsse);
        qSecurityReferenceRef = new QName(WSSE, "Reference", wsse);   
    }
    
    /**
     * Returns Oasis Security's URI
     * 
     * @return Oasis Security's URI
     */
    public String getSecurityURI() {
        return WSSE;
    }
    
    /**
     * Returns Oasis Security's prefix
     * 
     * @return Oasis Security's prefix
     */
    public String getSecurityPrefix() {
        return wsse;
    }
    
    /**
     * Returns Oasis Security Utility's URI
     * 
     * @return Oasis Security Utility's URI
     */
    public String getUtilURI() {
        return WSU;
    }
    
    /**
     * Returns Oasis Security Utility's prefix
     * 
     * @return Oasis Security Utility's prefix
     */
    public String getUtilPrefix() {
        return wsu;
    }
    ////////////////////////////////////////////////////////////////////////////
    // synchronized mutable properties. Hence private
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the duration (in seconds) to be used when adding timestamps
     * to a Node. 
     * 
     * @return the duration (in seconds) to be used when adding timestamps
     *         to a Node
     * 
     * @see OasisSecurity#addTimestamp(jakarta.xml.soap.SOAPElement, java.time.Instant, java.lang.String) 
     */
    public synchronized long getTimestampDuration() {
        return _secondsToAdd;
    }
    
    /**
     * Sets the duration (in seconds) to be used when adding timestamps to
     * a Node.
     * 
     * @param seconds duration (in seconds) to be used when adding timestamps to
     *                a Node.
     * @throws IllegalArgumentException if seconds is zero or negative
     * 
     * @see OasisSecurity#addTimestamp(jakarta.xml.soap.SOAPElement, java.time.Instant, java.lang.String) 
     */
    public synchronized void setTimestampDuration(long seconds) {
        if (seconds < 1) {
            throw new IllegalArgumentException("invalid duration");
        }
        this._secondsToAdd = seconds;
    }
    
    /**
     * Returns the formatter that will be used to format timestamp instants.
     * 
     * @return the formatter that will be used to format timestamp instants
     */
    public synchronized DateTimeFormatter getFormatter() {
        return _formatter;
    }
    
    /**
     * Sets the formatter that will be used to format timestamp instants.
     * @param formatter the formatter to use
     * @throws IllegalArgumentException if formatter is null
     */
    public synchronized void setFormatter(DateTimeFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("invalid formatter");
        }
        this._formatter = formatter;
    }
    
    /**
     * Formatts the specified instant
     * 
     * @param instant the instant to format
     * @return the formatted instant
     */
    protected String format(Instant instant) {
        return getFormatter().format(instant);
    }
    
    /**
     * Returns a base64 encoder
     * 
     * @return a base64 encoder
     */
    protected synchronized Base64.Encoder getEncoder() {
        if (_encoder == null) {
            _encoder = Base64.getEncoder();
        }
        return _encoder;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adds to the specified envelope the namespaces needed for this security.
     * 
     * @param envelope the envelope to add namespaces to
     * @throws SOAPException - if there is an error in creating the namespaces
     */
    public void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(wsse, WSSE)
                .addNamespaceDeclaration(wsu, WSU);
    }
    
    /**
     * Adds the security element to the specified header.
     * 
     * @param header the header to add the security to
     * @return the <code>SOAPHeaderElement</code> that was added to the security element
     * @throws SOAPException - if a SOAP error occurs
     * @throws IllegalArgumentException - if there is a problem in setting the mustUnderstand attribute
     */
    public SOAPHeaderElement addSecurity(SOAPHeader header) throws SOAPException {
        SOAPHeaderElement security = header.addHeaderElement(qSecurity);
        security.setMustUnderstand(true);
        return security;
    }
    
    /**
     * Adds a timestamp <code>SOAPElement</code> to the specified security element
     * using the specified and returns the <code>SOAPElement</code> that was added
     * to the security element.
     * 
     * @param security the <code>SOAPElement</code> to add the timestamp to
     * @param instant the initial instant of the timestamp to add
     * @param uri the id of the timestamp element
     * @return the <code>SOAPElement</code> that was added to the security element
     * @throws SOAPException if there were SOAP related problems
     */
    public SOAPElement addTimestamp(SOAPElement security, Instant instant, String uri) throws SOAPException {
        return addTimestamp(security, instant, instant.plusSeconds(getTimestampDuration()), uri);
    }
    
    /**
     * Adds a binary security token element to the specified security element.
     * 
     * @param security <code>SOAPElement</code> to add token to
     * @param uuid <code>UUID</code> of binary token
     * @param certificate certificate to use
     * @return the binary security token <code>SOAPElement</code> added
     * @throws SOAPException if there were SOAP related problems
     * @throws CertificateEncodingException  if there were certificate encoding problems
     */
    public SOAPElement addBinarySecurityToken(SOAPElement security, String uuid, X509Certificate certificate) 
            throws SOAPException, CertificateEncodingException {
        
        SOAPElement bsToken = 
                security.addChildElement(qBsToken)
                        .addAttribute(qWsuId, uuid)
                        .addAttribute(VALUE_TYPE, TOKEN_PROFILE_URI);
        bsToken.setTextContent(encode(certificate));
        
        return bsToken;
    }
    
    /**
     * Adds a security token reference <code>SOAPElement</code> to the specified
     * element using the specified uri, and returns the <code>SOAPElement</code>
     * that was added.
     * 
     * @param element element to add the security token reference to
     * @param uri the URI of the security token reference
     * @return the <code>SOAPElement</code> that was added
     * @throws SOAPException if there were SOAP related problems
     */
    public SOAPElement addSecurityTokenReference(SOAPElement element, String uri) throws SOAPException {
        SOAPElement tokenReference = element.addChildElement(qSecurityTokenRef);
        
        tokenReference.addChildElement(qSecurityReferenceRef)
                .setAttribute("URI", uri);
        
        return tokenReference;
    } 
    
    /**
     * Parses the specified <code>SOAPElement</code> and returns an array of two
     * instants. The first instant (0 index) will be the created instant and
     * the second (index 1 of array) will be the expiration of the timestamp.
     * 
     * @param parent the <code>SOAPElement</code> to parse
     * @return an array of two instants. instant[0] = created instant;
     *         instant[1] = expires instant.
     * @throws SvcParseException if there were problems while parsing
     */
    public Instant[] getTimestamp(SOAPElement parent) {
        SOAPElement ts = SOAPUtils.parseGrandchild(parent, qSecurity, qTimestamp);
        Instant[] array = new Instant[2];
        array[0] = getInstant(ts, qCreated);
        array[1] = getInstant(ts, qExpires);
        return array;
    }
    
    /**
     * Adds a timestamp element to the specified security element using the
     * specified parameters, and returns the <code>SOAPElement</code> that
     * was added to the security element.
     * 
     * @param security the <code>SOAPElement</code> to add timestamp element to
     * @param created timestamp's created instant
     * @param expires timestamp's expires instant
     * @param uri id of timestamp
     * @return the <code>SOAPElement</code> that was added to the security element
     * @throws SOAPException if there were SOAP releated problems
     * 
     */
    public SOAPElement addTimestamp(SOAPElement security, Instant created, Instant expires, String uri)  throws SOAPException {
        SOAPElement timestamp =
                security.addChildElement(qTimestamp)
                        .addAttribute(qWsuId, uri);
        timestamp.addChildElement(qCreated).setTextContent(format(created));
        timestamp.addChildElement(qExpires).setTextContent(format(expires));
        return timestamp;
    }
    
    /**
     * Returns the certificate base64 encoded
     * 
     * @param certificate the certificate to encode
     * @return the certificate base64 encoded
     * @throws CertificateEncodingException  if there were certificate encoding problems
     */
    public String encode(X509Certificate certificate) throws CertificateEncodingException {
        return getEncoder().encodeToString(certificate.getEncoded());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Parses an <code>Instant</code> from the specified <code>SOAPElement</code>
     * seeking a specific Node with the specified <code>QName</code>.
     * 
     * @param parent the element to parse
     * @param qname the name of the Node containing an instant
     * @return the instant extracted
     * @throws SvcParseException if the Node was not found
     */
    protected Instant getInstant(SOAPElement parent, QName qname) {
        return Instant.parse(SOAPUtils.parseChild(parent, qname).getTextContent());
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

/*    
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:u="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
<s:Header>
<o:Security xmlns:o="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" s:mustUnderstand="1">
<u:Timestamp u:Id="_0">
<u:Created>2024-09-19T13:40:44.876Z</u:Created>
<u:Expires>2024-09-19T13:45:44.876Z</u:Expires>
</u:Timestamp>
</o:Security>
</s:Header>
<s:Body>
<AutenticaResponse xmlns="http://DescargaMasivaTerceros.gob.mx">
<AutenticaResult>eyJhbGciOiJodHRwOi8vd...03031303030303030373036383232353032</AutenticaResult>
</AutenticaResponse>
</s:Body>
</s:Envelope>
    */
   
}
