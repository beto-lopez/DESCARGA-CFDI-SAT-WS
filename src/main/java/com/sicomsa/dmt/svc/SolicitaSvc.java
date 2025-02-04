/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.util.SOAPUtils;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import javax.xml.namespace.QName;

import java.time.Instant;

import java.util.Set;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * 
 * @version 2025.01.04
 * 
 * Implements Solicita Service from SAT.
 * Signs and sends the SOAPMessage, and parses the response returning a 
 * SolicitaResponse instance that will contain a requestId (IdSolicitud) that
 * will be used in the VerificaSvc to verify the request.
 * 
 */
public class SolicitaSvc extends AbstractGenericSvc<SolicitaResponse,Query> {
    
    public static final QName RESPONSE_NAME = new QName(DMT_URI, "SolicitaDescargaResponse", DMT_PREFIX); 
    public static final QName RESULT_NAME = new QName(DMT_URI, "SolicitaDescargaResult", DMT_PREFIX); 
    
    public static final QName REQUEST_ID = new QName("IdSolicitud"); //attribite
    
    public static final QName SOLICITA = new QName(DMT_URI, "SolicitaDescarga", DMT_PREFIX);
    public static final QName SOLICITUD = new QName(DMT_URI, "solicitud", DMT_PREFIX);
    
    public static final QName RFC_RECEPTORES = new QName(DMT_URI,"RfcReceptores",DMT_PREFIX);
    public static final QName RFC_RECEPTOR = new QName(DMT_URI,"RfcReceptor",DMT_PREFIX);

    public SolicitaSvc(SvcMessageFactory context) {
        super(context);
    }
   
    @Override public String getServiceName() {
        return "SolicitaSvc";
    }
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
    }
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescarga";
    }
    
    /**
     * 
     * @param message
     * @param rfc
     * @param query
     * @return
     * @throws SOAPException 
     * @throws IllegalArgumentException if message or query are null
     */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, Query query) throws SOAPException {
        if (message == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return addContent(message.getSOAPBody(), query);
    }
    
    public static SOAPElement addContent(SOAPElement parent, Query query) throws SOAPException {
        if (parent == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        SOAPElement element =
                parent.addChildElement(SOLICITA)
                      .addChildElement(SOLICITUD);
        addReceptores(element, query.getRfcReceptores());
        setAttributes(element, query);
        return element;
    }
    /**
     * 
     * @param element
     * @param receptoresList
     * @throws SOAPException 
     * 
     */
    public static void addReceptores(SOAPElement element, Set<String> receptoresList) throws SOAPException {
        if (receptoresList != null && !receptoresList.isEmpty()) {
            SOAPElement receptoresElement = element.addChildElement(RFC_RECEPTORES);
            for (String rfcReceptor : receptoresList) {
                receptoresElement.addChildElement(RFC_RECEPTOR)
                        .setTextContent(rfcReceptor);
            }
        }
    }
    
    public static void setAttributes(SOAPElement element, Query query) {
        query.getAttributes().forEachRemaining(name-> {
            element.setAttribute(name, query.getAttributeValue(name));
        });      
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param message
     * @param instant
     * @param request
     * @return
     * @throws SOAPException 
     */
    @Override
    public SolicitaResponse parseReceivedMessage(SOAPMessage message, Instant instant, Query request) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return parseResponse(message.getSOAPBody(), instant);
    }

    public static SolicitaResponse parseResponse(SOAPElement element, Instant instant) throws SOAPException {
        if (element == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        
        SOAPElement result = SOAPUtils.parseGrandchild(
                element,
                RESPONSE_NAME,
                RESULT_NAME);
        
        return new SolicitaResponse(
                instant,
                result.getAttributeValue(STS_CODE),
                result.getAttributeValue(MESSAGE),
                result.getAttributeValue(REQUEST_ID)); //retorna nulo si no viene dicho atr
    }
    
    ////////////////////////////////////////////////////////////////////////////
   
    
    /*
        
    Evento Mensaje Observaciones
    300 Usuario No Válido
    301 XML Mal Formado Este código de error se regresa cuando el
        request posee información invalida,
        ejemplo: un RFC de receptor no valido
    302 Sello Mal Formado
    303 Sello no corresponde con RfcSolicitante
    304 Certificado Revocado o Caduco El certificado puede ser invalido por
        múltiples razones como son el tipo, la vigencia, etc.
    305 Certificado Inválido El certificado puede ser invalido por
        múltiples razones como son el tipo, la vigencia, etc.
    5000 Query de descarga recibida con éxito
    5001 Tercero no autorizado El solicitante no tiene autorización de
         descarga de xml de los contribuyentes
    5002 Se han agotado las solicitudes de por vida
         Se ha alcanzado el límite de solicitudes,
         con el mismo criterio
    5004 No existe información (de filtros de solicitud)
    5005 Ya se tiene una solicitud registrada Ya existe una solicitud activa con los
         mismos criterios
    5006 Error interno en el proceso
    */
    
    ////////////////////////////////////////////////////////////////////////////
    
   /* 
<xs:element name="SolicitaDescargaResponse">
<xs:complexType>
<xs:sequence>
<xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaResult" type="tns:RespuestaSolicitudDescMasTercero"/>
</xs:sequence>
</xs:complexType>
</xs:element>
<xs:complexType name="RespuestaSolicitudDescMasTercero">
<xs:attribute name="IdSolicitud" type="xs:string"/>
<xs:attribute name="CodEstatus" type="xs:string"/>
<xs:attribute name="Mensaje" type="xs:string"/>
</xs:complexType>
 
 <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
 <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 <SolicitaDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
 <SolicitaDescargaResult IdSolicitud="e5d0847f-ASDFASD-FASDFA...bc2f37a52c6" CodEstatus="5000" Mensaje="Solicitud Aceptada"/>
 </SolicitaDescargaResponse>
 </s:Body>
 </s:Envelope>
 */
}
