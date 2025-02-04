/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.Authorization;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.SvcSignatureException;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import java.security.GeneralSecurityException;
import java.time.Instant;

import java.util.UUID;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;

import java.lang.System.Logger.Level;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * 
 * @version 2025.01.04  
 * 
 * Implementation of the autentica service from SAT.
 * Generates the signed SOAPMessage, sends it and parses the response returing
 * an Authorization with a token, if credentials were accepted.
 * 
 */
public class AuthenticationSvc extends AbstractSvc<Authorization,Object> {

    protected QName qResponse = new QName(DMTA_URI, "AutenticaResponse"); 
    protected QName qResult =  new QName(DMTA_URI, "AutenticaResult");
    
    protected QName qAutentica = new QName(DMTA_URI, "Autentica", DMTA_PREFIX);
   
    protected OasisSecurity security;

    protected String timestampUri = "TS"; 
    
    
    private static final System.Logger LOG = System.getLogger(AuthenticationSvc.class.getName());
    
    public AuthenticationSvc(SvcMessageFactory context) {
        this(context, new OasisSecurity());
    }
    
    public AuthenticationSvc(SvcMessageFactory context, OasisSecurity security) {
        super(context);
        if (security == null) {
            throw new IllegalArgumentException("security is required");
        }
        this.security = security;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public String getServiceName() {
        return "AutenticaSvc";
    }
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/Autenticacion/Autenticacion.svc";
    }
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.gob.mx/IAutenticacion/Autentica";
    }
    
    
    /**
     * 
     * @param message
     * @param creds
     * @param request
     * @throws SOAPException
     * @throws GeneralSecurityException
     * @throws SvcSignatureException 
     * @throws NullPointerException if message or creds are null
     */
    @Override
    protected void addSignedContent(SOAPMessage message, Credentials creds, Object request)
            throws SOAPException, GeneralSecurityException {
        
        message.getSOAPBody().addChildElement(qAutentica);
            
        SOAPElement securityElement = security.addSecurity(message.getSOAPHeader());
        security.addTimestamp(securityElement, getContext().instant(), timestampUri);
        String uuid = createSecurityTokenUUID();
        security.addBinarySecurityToken(securityElement, uuid, creds.getCertificate());
            
        SOAPElement securityTokenRef = security.addSecurityTokenReference(securityElement, "#".concat(uuid));
        
        XMLSignature signature =
            getContext().getSignatureFactory()
                .newAuthSignature("#".concat(timestampUri), securityTokenRef);

        creds.sign(signature, securityElement);
    }
    
    protected String createSecurityTokenUUID() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DMTA_PREFIX, DMTA_URI);
        super.addNamespaces(envelope);
        security.addNamespaces(envelope);
    }
    
    @Override
    public Authorization parseReceivedMessage(SOAPMessage message, Instant instant, Object request) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }

        Instant[] array = security.getTimestamp(message.getSOAPHeader());
        
        String result =
            SOAPUtils.parseGrandchild(message.getSOAPBody(), qResponse, qResult)
                    .getTextContent();
        
        if (!Authorization.isConsistent(result)) {
            LOG.log(Level.WARNING, "Inconsistent auth result({0})", result);
            throw new SvcParseException("unable to get consistent token");
        }
        
        return new Authorization(instant, array[0], array[1], result);
    }
    
    
    

    /*
<xsd:element name="Timestamp" type="wsu:TimestampType">
<xsd:annotation>
<xsd:documentation> This element allows Timestamps to be applied anywhere element wildcards are present, including as a SOAP header. </xsd:documentation>
</xsd:annotation>
</xsd:element>
<xsd:complexType name="TimestampType">
<xsd:annotation>
<xsd:documentation> This complex type ties together the timestamp related elements into a composite type. </xsd:documentation>
</xsd:annotation>
<xsd:sequence>
<xsd:element ref="wsu:Created" minOccurs="0"/>
<xsd:element ref="wsu:Expires" minOccurs="0"/>
<xsd:choice minOccurs="0" maxOccurs="unbounded">
<xsd:any namespace="##other" processContents="lax"/>
</xsd:choice>
</xsd:sequence>
<xsd:attributeGroup ref="wsu:commonAtts"/>
</xsd:complexType>
    ***
<xs:element name="Autentica">
<xs:complexType>
<xs:sequence/>
</xs:complexType>
</xs:element>
<xs:element name="AutenticaResult">
<xs:complexType>
<xs:sequence>
<xs:element minOccurs="0" name="AutenticaResult" nillable="true" type="xs:string"/>
</xs:sequence>
</xs:complexType>
</xs:element>
    
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
<AutenticaResult>eyJhbGciOiJodHRwOi8vd3d3LnczLm...3232353032</AutenticaResult>
</AutenticaResponse>
</s:Body>
</s:Envelope>
    */
    
  
    //////////////////////////////////////////////////////////////////////////
    
    
}
