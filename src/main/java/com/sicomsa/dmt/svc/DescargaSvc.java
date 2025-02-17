/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.time.Instant;
import javax.xml.namespace.QName;


/**
 *
 * Extends <code>AbstractGenericSvc</code> to provide a concrete implementation
 * of the WS defined for downloading specific packages:<br>
 * "https://cfdidescargamasiva.clouda.sat.gob.mx/DescargaMasivaService.svc"
 * 
 * <p>This class has concrete methods to create a download package request;
 * sign, and send it to the web service. Then parse the response to provide a
 * {@link com.sicomsa.dmt.DescargaResponse} with methods to query the response.</p>
 * <p>If the package was downloaded then <code>DescargaResponse</code> will have
 * the package and you can access it with the method {@link com.sicomsa.dmt.DescargaResponse#getEncodedPackage() getEncodedPackage()}.</p>
 * <p>The encoded package will be a String extracted from the <code>SOAPMessage</code>
 * received from the web service; to save it you should decode it:</p>
 * <pre>
 *      byte[] decoded = java.util.Base64.getDecoder().decode(encodedPackage);
 * </pre>
 * 
 *  
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04  
 * @since 1.0
 * 
 */
public class DescargaSvc extends AbstractGenericSvc<DescargaResponse,String> {

    /**
     * Name of response Node in <code>SOAPHeader</code>
     */
    public static final QName HEADER_RESPONSE_QN = new QName(DMT_URI, "respuesta", DMT_PREFIX); 
    
    /**
     * Name of response NOde in <code>SOAPBody</code>
     */
    public static final QName BODY_RESPONSE_QN = new QName(DMT_URI, "RespuestaDescargaMasivaTercerosSalida", DMT_PREFIX); 
    
    /**
     * Name of package Node
     */
    public static final QName PACKAGE_QN = new QName(DMT_URI, "Paquete", DMT_PREFIX); 
    
    /**
     * Name of parent request Node
     */
    public static final QName DOWNLOAD_REQUEST =
            new QName(DMT_URI, "PeticionDescargaMasivaTercerosEntrada", DMT_PREFIX);
    
    /**
     * Name of child request Node
     */
    public static final QName REQUEST =
            new QName(DMT_URI, "peticionDescarga", DMT_PREFIX);
        
    /**
     * Name of package id attribute
     */
    public static final QName PACKAGE_ID = new QName("IdPaquete");
    
    /**
     * Name of RFC requestor's attribute
     */
    public static final QName RFC = new QName("RfcSolicitante");
    
    /**
     * Constructs a DescargaSvc with the specified context.
     * 
     * @param context the SvcMessageFactory to use
     */
    public DescargaSvc(SvcMessageFactory context) {
        super(context);
    }
    
    /**
     * Returns the name of this service.
     * "DescargaSvc"
     * 
     * @return the name of this service
     */
    @Override public String getServiceName() {
        return "DescargaSvc";
    }
    
    /**
     * Returns the location of this service.
     * "https://cfdidescargamasiva.clouda.sat.gob.mx/DescargaMasivaService.svc"
     * 
     * @return the location of this service
     */
    @Override public String getLocation() {
        return "https://cfdidescargamasiva.clouda.sat.gob.mx/DescargaMasivaService.svc";
    }
    
    /**
     * Returns the SOAP action of this service.
     * "http://DescargaMasivaTerceros.sat.gob.mx/IDescargaMasivaTercerosService/Descargar"
     * 
     * @return the SOAP action of this service.
     */
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/IDescargaMasivaTercerosService/Descargar";
    }
    
    /*
    <des:PeticionDescargaMasivaTercerosEntrada>
    <des:peticionDescarga IdPaquete="xxxx" RfcSolicitante="yyyy">
    */
    /**
     * Adds the RFC and package id specified to the <code>SOAPMessge</code>
     * specified according to the WSDL for package download. And returns the
     * <code>SOAPElement</code> that should be signed.
     * 
     * @param message the message to add contento
     * @param rfc the RFC of the contributor requesting the package
     * @param packageId the id of the package to download
     * @return the <code>SOAPElement</code> that should be signed.
     * @throws SOAPException if there were SOAP related problems
     */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, String packageId)
                    throws SOAPException {
        
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
     * Returns a <code>DescargaResponse</code> instant with contents of the
     * specified message and parameters.
     * 
     * @param message message to parse
     * @param instant instant the message was received
     * @param packageId the id of the package that was requested for download
     *                  and caused this response message.
     * @return a <code>DescargaResponse</code> instant with contents of the
     *         specified message and parameters
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
    @Override
    public DescargaResponse parseReceivedMessage(SOAPMessage message, Instant instant,
                    String packageId) throws SOAPException {
        
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
     * Returns the encoded package parsed from the specified element or null
     * if no package was received.
     * 
     * @param element the element to parse
     * @return the encoded package parsed from the specified element or null
     *         if no package was received.
     * @throws NullPointerException if element is null
     * 
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
