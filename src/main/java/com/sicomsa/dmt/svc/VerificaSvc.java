/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.util.SvcParseException;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.Node;

import java.time.Instant;
import java.util.Iterator;
import javax.xml.namespace.QName;

import com.sicomsa.dmt.util.SOAPUtils;

/**
 * Extends <code>AbstractGenericSvc</code> to provide a concrete implementation
 * of the WS defined for verifying download requests:<br>
 * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/VerificaSolicitudDescargaService.svc"
 * 
 * <p>This class has concrete methods to create a download verification request;
 * sign, and send it to the web service. Then parse the response to provide a
 * {@link com.sicomsa.dmt.VerificaResponse} with methods to query the response.</p>
 * <p>User can test the state of the response to see if it was accepted, or if
 * the verification is done, or not yet done.</p>
 * <p>When the verification of the download request is accepted and finished,
 * then you can use <code>VerificaResponse</code> to obtain the package ids that
 * are now needed to download.</p>
 * 
 * {@see DescargaSvc}, service to download from the web service after verification.
 * 
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04  
 * @since 1.0
 * 
 */
public class VerificaSvc extends AbstractGenericSvc<VerificaResponse,String> {

    ///request
    /**
     * Name of parent verifica request Node
     */
    public static final QName VERIFICA_NAME = new QName(DMT_URI, "VerificaSolicitudDescarga", DMT_PREFIX);
    
    /**
     * Name of child verifica request Node
     */
    public static final QName SOLICITUD_NAME= new QName(DMT_URI, "solicitud", DMT_PREFIX);
    
    /**
     * Name of the requestId
     */
    public static final QName ID_NAME  = new QName("IdSolicitud"); //hay que cambiar este, usar mismo que SolicitaSvc, pasarlo a abstract
    
    /**
     * Name of RFC requestor
     */
    public static final QName RFC_NAME = new QName("RfcSolicitante");
    
    ///response
    /**
     * Name of parent Verifica response Node
     */
    public static final QName RESPONSE_NAME = new QName(DMT_URI, "VerificaSolicitudDescargaResponse", DMT_PREFIX); 
    
    /**
     * Name of child Verifica response Node
     */
    public static final QName RESULT_NAME   = new QName(DMT_URI, "VerificaSolicitudDescargaResult", DMT_PREFIX);
    
    /**
     * Name of packages id's Node
     */
    public static final QName PACKAGE_ID_NAME = new QName(DMT_URI, "IdsPaquetes", DMT_PREFIX);
    
    /**
     * Name of request state attribute
     */
    public static final QName STATE_NAME = new QName("EstadoSolicitud"); 
    
    /**
     * Name of request status code attribute
     */
    public static final QName SOLICITUDE_STS_CODE_NAME = new QName("CodigoEstadoSolicitud"); 
    
    /**
     * Name of ammount of CFDI's attribute
     */
    public static final QName CFDIS_NAME = new QName("NumeroCFDIs"); 
        
    
    private static final System.Logger LOG = System.getLogger(VerificaSvc.class.getName());
    
    /**
     * Creates a new VerificaSvc with the specified context.
     * 
     * @param context the SvcMessageFactory to use
     */
    public VerificaSvc(SvcMessageFactory context) {
        super(context);
    }
    
    /**
     * Returns the name of this service.
     * "VerificaSvc"
     * 
     * @return the name of this service
     */
    @Override public String getServiceName() {
        return "VerificaSvc";
    }
    
    /**
     * Returns the location of this service.
     * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/VerificaSolicitudDescargaService.svc"
     * 
     * @return the location of this service
     */
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/VerificaSolicitudDescargaService.svc";
    }
    
    /**
     * Returns the SOAP action of this service.
     * "http://DescargaMasivaTerceros.sat.gob.mx/IVerificaSolicitudDescargaService/VerificaSolicitudDescarga"
     * 
     * @return the SOAP action of this service
     */
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/IVerificaSolicitudDescargaService/VerificaSolicitudDescarga";
    }
    
    /*
<des:VerificaSolicitudDescarga>
<des:solicitud IdSolicitud="e5d0847f-caf6-48e8-a60d-3bc2f37a52c6" RfcSolicitante="SCM8608093V5">
<Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
    */
    /**
     * Adds the specified RFC and requestId to the message received.
     * 
     * @param message the message to add content to
     * @param rfc the RFC of the contributor requesting verification
     * @param requestId the id of the request to be verified
     * @return a <code>VerificaResponse</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message, rfc or requestId are null
     */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, String requestId) throws SOAPException {
        if (message == null || rfc == null || requestId == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return message.getSOAPBody()
                .addChildElement(VERIFICA_NAME)
                .addChildElement(SOLICITUD_NAME)
                .addAttribute(ID_NAME, requestId)
                .addAttribute(RFC_NAME, rfc);
    }
  
    ////////////////////////////////////////////////////////////////////////////
    /*
<xs:complexType name="RespuestaVerificaSolicitudDescMasTercero">
<xs:sequence>
<xs:element minOccurs="0" maxOccurs="unbounded" name="IdsPaquetes" type="xs:string"/>
</xs:sequence>
<xs:attribute name="CodEstatus" type="xs:string"/>
<xs:attribute name="EstadoSolicitud" type="xs:int" use="required"/>
<xs:attribute name="CodigoEstadoSolicitud" type="xs:string"/>
<xs:attribute name="NumeroCFDIs" type="xs:int" use="required"/>
<xs:attribute name="Mensaje" type="xs:string"/>
</xs:complexType>
</xs:schema>
    
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<VerificaSolicitudDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<VerificaSolicitudDescargaResult CodEstatus="5000" EstadoSolicitud="3" CodigoEstadoSolicitud="5000" NumeroCFDIs="163" Mensaje="Solicitud Aceptada">
<IdsPaquetes>CE03852F-FBD8-4616-AEE5-5F8493206063_01</IdsPaquetes>
</VerificaSolicitudDescargaResult>
</VerificaSolicitudDescargaResponse>
</s:Body>
</s:Envelope>

    */
    
    /**
     * Parses the specified <code>SOAPMessage</code> to provide a <code>VerificaResponse</code>
     * instance which contains methods to query the response.
     * 
     * @param message the message to parse
     * @param instant the instant the message was received
     * @param requestId the id of the request that generated the response message
     * @return a <code>VerificaResponse</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
    @Override
    public VerificaResponse parseReceivedMessage(SOAPMessage message, Instant instant, String requestId) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return parseResponse(message.getSOAPBody(), instant, requestId);
    }
    
    /**
     * Parses the specified <code>SOAPElement</code> to provide a <code>VerificaResponse</code>
     * instance which contains methods to query the response.
     * 
     * @param element the element to parse
     * @param instant the instant the message was received
     * @param requestId the id of the request that generated the response message
     * @return a <code>VerificaResponse</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if element or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
    public static VerificaResponse parseResponse(SOAPElement element, Instant instant, String requestId) throws SOAPException {
        if (element == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
               
        SOAPElement e =
                SOAPUtils.parseGrandchild(
                        element,
                        RESPONSE_NAME,
                        RESULT_NAME
                );
        
        VerificaResponse.Builder builder = new VerificaResponse.Builder();
        int cfdis = SOAPUtils.parseIntAttributeValue(e, CFDIS_NAME);
        builder.setSatInstant(instant)
                .setStatusCode(e.getAttributeValue(STS_CODE))
                .setMessage(e.getAttributeValue(MESSAGE))
                .setRequestId(requestId)
                .setCfdisAmmount(cfdis)
                .setSolicitudeStsCode(e.getAttributeValue(SOLICITUDE_STS_CODE_NAME))
                .setSolicitudeState(SOAPUtils.parseIntAttributeValue(e, STATE_NAME));
        Iterator<Node> iterator = e.getChildElements(PACKAGE_ID_NAME);
        if (iterator.hasNext()) {
            if (cfdis == 0) {
                LOG.log(System.Logger.Level.DEBUG, "zero cfdis with package Ids");
            }
            int blanks = 0;
            do {
                String packageId = ((SOAPElement)iterator.next()).getTextContent();
                if (packageId == null || packageId.isBlank()) {
                    blanks++;
                }
                builder.addPackageId(packageId);
            }
            while (iterator.hasNext());
            if (blanks != 0) {
                LOG.log(System.Logger.Level.DEBUG, "({0}) blank packageIds received", blanks);
            }
        }
        return builder.build();
    }
    
}
