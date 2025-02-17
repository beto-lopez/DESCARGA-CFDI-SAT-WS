/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import javax.xml.namespace.QName;

import java.time.Instant;

import java.util.Set;

/**
 * Extends <code>AbstractGenericSvc</code> to provide a concrete implementation
 * of the WS defined for requesting downloads:<br>
 * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"
 * 
 * <p>This class has concrete methods to add content of the download request (<code>Query</code>)
 * to the <code>SOAPMessage</code> to send, sign the message, and parse the
 * response from SAT in a {@link com.sicomsa.dmt.SolicitaResponse} instance which will
 * contain the relevant data of the response.</p>
 * <p>If <code>SolicitaResponse</code>'s method {@link com.sicomsa.dmt.SolicitaResponse#isAccept() isAccept()},
 * returns true will mean the request was accepted and we can now verify using
 * <code>SolicitaResponse</code>'s method {@link com.sicomsa.dmt.SolicitaResponse#getRequestId() getRequestId()}
 * which will give us the request id that should be used to verify the request.</p>
 * 
 * @see com.sicomsa.dmt.SolicitaResponse for more information about the response.
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.04
 * @since 1.0
 * 
 * 
 */
public class SolicitaSvc extends AbstractGenericSvc<SolicitaResponse,Query> {
    
    /**
     * Name of the Solicita response Node
     */
    public static final QName RESPONSE_NAME = new QName(DMT_URI, "SolicitaDescargaResponse", DMT_PREFIX); 
    
    /**
     * Name of the Solicita result Node
     */
    public static final QName RESULT_NAME = new QName(DMT_URI, "SolicitaDescargaResult", DMT_PREFIX); 
    
    /**
     * QName of requestId
     */
    public static final QName REQUEST_ID = new QName("IdSolicitud"); //attribite
    
    /**
     * Name of parent Solicita request Node
     */
    public static final QName SOLICITA = new QName(DMT_URI, "SolicitaDescarga", DMT_PREFIX);
    
    /**
     * Name of child Solicita request Node
     */
    public static final QName SOLICITUD = new QName(DMT_URI, "solicitud", DMT_PREFIX);
    
    /**
     * Name of parent RFC receptors request Node
     */
    public static final QName RFC_RECEPTORES = new QName(DMT_URI,"RfcReceptores",DMT_PREFIX);
    
    /**
     * Name of child RFC receptors request Node
     */
    public static final QName RFC_RECEPTOR = new QName(DMT_URI,"RfcReceptor",DMT_PREFIX);

    /**
     * Builds a SolicitaSvc with the specified SvcMessageFactory
     * 
     * @param context the SvcMessageFactory to be used
     */
    public SolicitaSvc(SvcMessageFactory context) {
        super(context);
    }
   
    /**
     * Returns the service name of this service.
     * "SolicitaSvc"
     * 
     * @return the service name of this service
     */
    @Override public String getServiceName() {
        return "SolicitaSvc";
    }
    
    /**
     * Returns the location of this service.
     * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"
     * 
     * @return the location of this service.
     */
    @Override public String getLocation() {
        return "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
    }
    
    /**
     * Returns the SOAP action of this service.
     * "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescarga"
     * 
     * @return the SOAP action of this service
     */
    @Override public String getSoapAction() {
        return "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescarga";
    }
    
    /**
     * Adds attributes of Query and receptors to the <code>SOAPMessage</code>
     * received and returns the <code>SOAPElement</code> to be signed.
     * 
     * @param message the message to add content to
     * @param rfc the RFC of contributor making request
     * @param query the query with contributor´s request
     * @return the <code>SOAPElement</code> to be signed
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or query are null
     */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, Query query) throws SOAPException {
        if (message == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return addContent(message.getSOAPBody(), query);
    }
    
    /**
     * Adds attributes of Query and receptors to the <code>SOAPElement</code>
     * received and returns the <code>SOAPElement</code> to be signed.
     * 
     * @param parent the element to add query data to
     * @param query the query with the request data
     * @return the <code>SOAPElement</code> to be signed
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if parent or query are null
     */
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
     * Adds receptor elements to the specified element.
     * <p>This method follows de WSDL definition when having RFC receptors in
     * the requesting SOAPmessage to send to de WS.</p>
     * 
     * @param element element to be appended with receptor elements
     * @param receptores string Set of RFC's to add to the element
     * @throws SOAPException if there were SOAP related problems
     * @throws NullPointerException if element is null
     */
    public static void addReceptores(SOAPElement element, Set<String> receptores) throws SOAPException {
        if (receptores != null && !receptores.isEmpty()) {
            SOAPElement receptoresElement = element.addChildElement(RFC_RECEPTORES);
            for (String rfcReceptor : receptores) {
                receptoresElement.addChildElement(RFC_RECEPTOR)
                        .setTextContent(rfcReceptor);
            }
        }
    }
    
    /**
     * Sets attributes of the specified <code>Query</code> to the specified
     * <code>SOAPElement</code>.
     * 
     * @param element element to set attributes to
     * @param query query to get the attributes from
     * @throws NullPointerException if element or query are null
     */
    public static void setAttributes(SOAPElement element, Query query) {
        query.getAttributes().forEachRemaining(name-> {
            element.setAttribute(name, query.getAttributeValue(name));
        });      
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a <code>SolicitaResponse</code> parsing the specified <code>SOAPMessage</code>
     * using the specified instant as the time the response was received.
     * 
     * @param message to be parsed
     * @param instant instant to be used to set response time
     * @param request <code>Query</code> sent that originated the message
     * @return a <code>SolicitaResponse</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or instant are null
     * @throws SvcParseException if there were problems while parsing message
     */
    @Override
    public SolicitaResponse parseReceivedMessage(SOAPMessage message, Instant instant, Query request) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return parseResponse(message.getSOAPBody(), instant);
    }

    /**
     * Returns a <code>SolicitaResponse</code> parsed from the specified
     * <code>SOAPElement</code>.
     * <p>The specified instant will be used to set the time the response was
     * received</p>
     * 
     * @param element element to be parsed
     * @param instant the response was received
     * @return a <code>SolicitaResponse</code> 
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if element or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
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
