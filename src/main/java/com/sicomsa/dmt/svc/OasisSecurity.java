/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.

 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.util.SOAPUtils;

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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.28
 * 
 * Utility methods for signing envelopes according to the security requested
 * by SAT.
 * 
 */
public class OasisSecurity {
    public static final String WSSE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    public static final String WSU =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    
    public static final QName VALUE_TYPE = new QName("ValueType");
    
    public static final String TOKEN_PROFILE_URI = 
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
    
    protected final String wsse;
    protected final String wsu;
    
    protected QName qSecurity;
    protected QName qTimestamp;
    protected QName qCreated;
    protected QName qExpires;
    protected QName qWsuId;
    protected QName qBsToken;
    protected QName qSecurityTokenRef;
    protected QName qSecurityReferenceRef;
    
    protected String timestampIdUri = "TS";
    
    private Base64.Encoder _encoder;
    
    private long _secondsToAdd = 60 * 5; //5 minutes
    
    private DateTimeFormatter _formatter;
    
    public OasisSecurity() {
        this("wsse", "wsu");
    }
    
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
    
    public String getSecurityURI() {
        return WSSE;
    }
    public String getSecurityPrefix() {
        return wsse;
    }
    public String getUtilURI() {
        return WSU;
    }
    public String getUtilPrefix() {
        return wsu;
    }
    ////////////////////////////////////////////////////////////////////////////
    // synchronized mutable properties. Hence private
    ////////////////////////////////////////////////////////////////////////////
    
    public synchronized long getTimestampDuration() {
        return _secondsToAdd;
    }
    
    public synchronized void setTimestampDuration(long seconds) {
        if (seconds < 1) {
            throw new IllegalArgumentException("invalid duration");
        }
        this._secondsToAdd = seconds;
    }
    
    public synchronized DateTimeFormatter getFormatter() {
        return _formatter;
    }
    
    public synchronized void setFormatter(DateTimeFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("invalid formatter");
        }
        this._formatter = formatter;
    }
    
    protected String format(Instant instant) {
        return getFormatter().format(instant);
    }
    
    protected synchronized Base64.Encoder getEncoder() {
        if (_encoder == null) {
            _encoder = Base64.getEncoder();
        }
        return _encoder;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * @param envelope
     * @throws SOAPException - if there is an error in creating the namespaces
     */
    public void addNamespaces(SOAPEnvelope envelope) throws SOAPException { //vse
        envelope.addNamespaceDeclaration(wsse, WSSE)
                .addNamespaceDeclaration(wsu, WSU);
    }
    
    /**
     * 
     * @param header
     * @return 
     * @throws SOAPException - if a SOAP error occurs
     * @throws IllegalArgumentException - if there is a problem in setting the mustUnderstand attribute
     */
    public SOAPHeaderElement addSecurity(SOAPHeader header) throws SOAPException {
        SOAPHeaderElement security = header.addHeaderElement(qSecurity);
        security.setMustUnderstand(true);
        return security;
    }
    
    public SOAPElement addTimestamp(SOAPElement security, Instant instant, String uri) throws SOAPException {
        return addTimestamp(security, instant, instant.plusSeconds(getTimestampDuration()), uri);
    }
    
    public SOAPElement addBinarySecurityToken(SOAPElement security, String uuid, X509Certificate certificate) 
            throws SOAPException, CertificateEncodingException {
        
        SOAPElement bsToken = 
                security.addChildElement(qBsToken)
                        .addAttribute(qWsuId, uuid)
                        .addAttribute(VALUE_TYPE, TOKEN_PROFILE_URI);
        bsToken.setTextContent(encode(certificate));
        
        return bsToken;
    }
    
    public SOAPElement addSecurityTokenReference(SOAPElement element, String uri) throws SOAPException {
        SOAPElement tokenReference = element.addChildElement(qSecurityTokenRef);
        
        tokenReference.addChildElement(qSecurityReferenceRef)
                .setAttribute("URI", uri);
        
        return tokenReference;
    } 
    
    public Instant[] getTimestamp(SOAPElement parent) {
        SOAPElement ts = SOAPUtils.parseGrandchild(parent, qSecurity, qTimestamp);
        Instant[] array = new Instant[2];
        array[0] = getInstant(ts, qCreated);
        array[1] = getInstant(ts, qExpires);
        return array;
    }
    
    /**
     * 
     * @param security
     * @param created
     * @param expires
     * @param uri
     * @return
     * @throws SOAPException
     *  - if there is an error in creating the SOAPElement object
     *  - if there is an error in creating the Attribute, or it is invalid to set an attribute with QName qname on this SOAPElement.
     */
    public SOAPElement addTimestamp(SOAPElement security, Instant created, Instant expires, String uri)  throws SOAPException { //vse
        SOAPElement timestamp =
                security.addChildElement(qTimestamp)
                        .addAttribute(qWsuId, uri);
        timestamp.addChildElement(qCreated).setTextContent(format(created));
        timestamp.addChildElement(qExpires).setTextContent(format(expires));
        return timestamp;
    }
    
    public String encode(X509Certificate certificate) throws CertificateEncodingException {
        return getEncoder().encodeToString(certificate.getEncoded());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
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
