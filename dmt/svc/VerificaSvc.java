/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.VerificaResponse;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.Node;
import java.time.Instant;
import java.util.Iterator;
import javax.xml.namespace.QName;

import com.sicomsa.dmt.util.SOAPUtils;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * 
 * 
 * @version 2025.01.04  
 *    
 * 
 * Implements Verify request service from SAT.
 * 
 * Fills, signs and sends SOAPMessage to SAT with the verification request and
 * returns SAT response encapsulated in a VerificaResponse class that will
 * include the package id's needed to download what we requested, or it could
 * return a VerificaResponse with a "delayed" state meaning that the request
 * has not yet been verified.
 * 
 */
public class VerificaSvc extends AbstractGenericSvc<VerificaResponse,String> {

    ///request
    public static final QName VERIFICA_NAME = new QName(DMT_URI, "VerificaSolicitudDescarga", DMT_PREFIX);
    public static final QName SOLICITUD_NAME= new QName(DMT_URI, "solicitud", DMT_PREFIX);
    
    public static final QName ID_NAME  = new QName("IdSolicitud"); //hay que cambiar este, usar mismo que SolicitaSvc, pasarlo a abstract
    public static final QName RFC_NAME = new QName("RfcSolicitante");
    
    ///response
    public static final QName RESPONSE_NAME = new QName(DMT_URI, "VerificaSolicitudDescargaResponse", DMT_PREFIX); 
    public static final QName RESULT_NAME   = new QName(DMT_URI, "VerificaSolicitudDescargaResult", DMT_PREFIX);
    
       
    public static final QName PACKAGE_ID_NAME = new QName(DMT_URI, "IdsPaquetes", DMT_PREFIX);
    
    public static final QName STATE_NAME = new QName("EstadoSolicitud"); 
    public static final QName SOLICITUDE_STS_CODE_NAME = new QName("CodigoEstadoSolicitud"); 
    public static final QName CFDIS_NAME = new QName("NumeroCFDIs"); 
        
    private static final System.Logger LOG = System.getLogger(VerificaSvc.class.getName());
    
    public VerificaSvc(SvcMessageFactory context) {
        super(context);
    }
    
    
    @Override public String getServiceName() {
        return "VerificaSvc";
    }
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/VerificaSolicitudDescargaService.svc";
    }
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/IVerificaSolicitudDescargaService/VerificaSolicitudDescarga";
    }
    
    /*
<des:VerificaSolicitudDescarga>
<des:solicitud IdSolicitud="e5d0847f-caf6-48e8-a60d-3bc2f37a52c6" RfcSolicitante="SCM8608093V5">
<Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
    */
    /**
     * 
     * @param message
     * @param rfc
     * @param requestId
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException
     *          if message, rfc or requestId are null
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
    
    @Override
    public VerificaResponse parseReceivedMessage(SOAPMessage message, Instant instant, String requestId) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return parseResponse(message.getSOAPBody(), instant, requestId);
    }
    
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
