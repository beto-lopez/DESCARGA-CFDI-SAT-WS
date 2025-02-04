/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.util.SOAPUtils;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.time.Instant;
import javax.xml.namespace.QName;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.22
 * 
 * @version 2025.01.04  
 * 
 * Implementation of AbstractGenericSvc for the download package SAT service.
 * 
 * If the request is accepted this class will return a DescargaResponse instance
 * that will incluide an encodedpackage that will contain the CFDIs or metadata
 * or whatever choice was made from the request.
 * 
 * DescargaSvc builds, fills and signs the SOAPMessage to send to SAT in order
 * to request the download of the defined packageId; sends it, receives the
 * response from SAT also a SOAPMessage, and parses it to return an instance
 * of DescargaResponse.
 * 
 */
public class DescargaSvc extends AbstractGenericSvc<DescargaResponse,String> {

    public static final QName HEADER_RESPONSE_QN = new QName(DMT_URI, "respuesta", DMT_PREFIX); 
    public static final QName BODY_RESPONSE_QN = new QName(DMT_URI, "RespuestaDescargaMasivaTercerosSalida", DMT_PREFIX); 
    public static final QName PACKAGE_QN = new QName(DMT_URI, "Paquete", DMT_PREFIX); 
    
    public static final QName DOWNLOAD_REQUEST =
            new QName(DMT_URI, "PeticionDescargaMasivaTercerosEntrada", DMT_PREFIX);
    
    public static final QName REQUEST =
            new QName(DMT_URI, "peticionDescarga", DMT_PREFIX);
        
    public static final QName PACKAGE_ID = new QName("IdPaquete");
    public static final QName RFC = new QName("RfcSolicitante");
    
    public DescargaSvc(SvcMessageFactory context) {
        super(context);
    }
    
    @Override public String getServiceName() {
        return "DescargaSvc";
    }
    @Override public String getLocation() {
        return "https://cfdidescargamasiva.clouda.sat.gob.mx/DescargaMasivaService.svc";
    }
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/IDescargaMasivaTercerosService/Descargar";
    }
    /*
    <des:PeticionDescargaMasivaTercerosEntrada>
    <des:peticionDescarga IdPaquete="xxxx" RfcSolicitante="yyyy">
    */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, String packageId) throws SOAPException {
        if (message == null || rfc == null || packageId == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return message.getSOAPBody()
                .addChildElement(DOWNLOAD_REQUEST)
                    .addChildElement(REQUEST)
                        .addAttribute(PACKAGE_ID, packageId)
                        .addAttribute(RFC, rfc);
    }
   
/*  
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
<s:Header>
<h:respuesta xmlns:h="http://DescargaMasivaTerceros.sat.gob.mx"
    xmlns="http://DescargaMasivaTerceros.sat.gob.mx"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    CodEstatus="5000" Mensaje="Solicitud Aceptada"/>
</s:Header>
<s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<RespuestaDescargaMasivaTercerosSalida xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
<Paquete>UEsDB...AAEAWQAAADwBAAAAAA==</Paquete>
</RespuestaDescargaMasivaTercerosSalida>
</s:Body>
</s:Envelope>   
*/

    /**
     * @param message
     * @param instant
     * @param packageId
     * @return
     * @throws SOAPException 
     */
    @Override
    public DescargaResponse parseReceivedMessage(SOAPMessage message, Instant instant, String packageId) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        
        String epackage = SOAPUtils.parseGrandchild(
                message.getSOAPBody(),
                BODY_RESPONSE_QN,
                PACKAGE_QN)
                .getTextContent();
                
        SOAPElement helement = SOAPUtils.parseChild(
                message.getSOAPHeader(),
                HEADER_RESPONSE_QN);
        
        return new DescargaResponse(
                instant,
                helement.getAttributeValue(STS_CODE),
                helement.getAttributeValue(MESSAGE),
                packageId,
                epackage);
    }

    /**
     * @param element
     * @return - the encoded package, or null if element not received.
     */
    public static String parseEncodedPackage(SOAPElement element) {
        if (element == null) { 
            throw new NullPointerException("element required");
        }
        element = SOAPUtils.parseChild(element, BODY_RESPONSE_QN, false);
        if (element != null) {
            element = SOAPUtils.parseChild(element, PACKAGE_QN, false);
            if (element != null) {
                return element.getTextContent();
            }
        }
        return null;
    }
}
