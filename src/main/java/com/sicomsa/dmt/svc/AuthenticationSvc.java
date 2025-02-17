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
 * Extends <code>AbstractSvc</code> to provide a concrete implementation of the
 * WS defined to authenticate:<br>
 * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/Autenticacion/Autenticacion.svc"
 *  
 * <p>This class has concrete methods to create a <code>SOAPMessage</code> and
 * sign it in order to request an authorization token. And parse the response
 * to provide an {@link com.sicomsa.dmt.Authorization} which provides methods to
 * wrap the token and determine its validity.</p>
 * 
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04  
 * @since 1.0
 * 
 * 
 */
public class AuthenticationSvc extends AbstractSvc<Authorization,Object> {

    /**
     * Name of the parent response Node
     */
    protected QName qResponse = new QName(DMTA_URI, "AutenticaResponse"); 
    
    /**
     * Name of the child response Node
     */
    protected QName qResult =  new QName(DMTA_URI, "AutenticaResult");
    
    /**
     * Name of the request Node
     */
    protected QName qAutentica = new QName(DMTA_URI, "Autentica", DMTA_PREFIX);
   
    /**
     * Reference to security related methods
     */
    protected OasisSecurity security;

    /**
     * Timestamp URI
     */
    protected String timestampUri = "TS"; 
    
    
    private static final System.Logger LOG = System.getLogger(AuthenticationSvc.class.getName());
    
    /**
     * Constructs an AuthenticationSvc with the specified context and a default
     * OasisSecurity.
     * 
     * @param context the SvcMessageFactory to use
     */
    public AuthenticationSvc(SvcMessageFactory context) {
        this(context, new OasisSecurity());
    }
    
    /**
     * Constructs an AuthenticationSvc with the specified context and security.
     * 
     * @param context the SvcMessageFactory to use
     * @param security the security to use
     */
    public AuthenticationSvc(SvcMessageFactory context, OasisSecurity security) {
        super(context);
        if (security == null) {
            throw new IllegalArgumentException("security is required");
        }
        this.security = security;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the name of this service.
     * "AutenticaSvc"
     * 
     * @return the name of this service
     */
    @Override public String getServiceName() {
        return "AutenticaSvc";
    }
    
    /**
     * Returns the location of this service.
     * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/Autenticacion/Autenticacion.svc"
     * 
     * @return the location of this service
     */
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/Autenticacion/Autenticacion.svc";
    }
    /**
     * Returns the SOAP action of this service.
     * "http://DescargaMasivaTerceros.gob.mx/IAutenticacion/Autentica";
     * 
     * @return the SOAP action of this service
     */
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.gob.mx/IAutenticacion/Autentica";
    }
    
    
    /**
     * Adds content to the specified <code>SOAPMessage</code> using the specified
     * credentials and signs the content according to the WSDL.
     * 
     * @param message message to add content to and sign
     * @param creds credentials to use
     * @param request additional data, can be null
     * @throws SOAPException if there were SOAP related problems
     * @throws GeneralSecurityException if there were security related problems
     * @throws SvcSignatureException  if there were other signature related problems
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
    
    /**
     * Returns a random UUID
     * 
     * @return a random UUID
     */
    protected String createSecurityTokenUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Override of <code>addNamespaces</code> to add authorization and security
     * namespaces.
     * 
     * @param envelope the envelope to add namespaces to
     * @throws SOAPException if there were SOAP related problems
     * @throws NullPointerException if envelope is null
     */
    @Override
    protected void addNamespaces(SOAPEnvelope envelope) throws SOAPException {
        envelope.addNamespaceDeclaration(DMTA_PREFIX, DMTA_URI);
        super.addNamespaces(envelope);
        security.addNamespaces(envelope);
    }
    
    /**
     * Returns an <code>Authorization</code> parsed from the specified message.
     * 
     * @param message message to be parsed
     * @param instant instant the message was received 
     * @param request request Object, can be null
     * @return an <code>Authorization</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
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
