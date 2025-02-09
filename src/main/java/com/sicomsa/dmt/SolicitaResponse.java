/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import java.time.Instant;
/**
 * <code>SolicitaResponse</code> is SAT's response from a download request to
 * the massive download web service when requesting a download for the first
 * time of a particular query.
 * 
 * The property requestId received can be used to verify the request.
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version 2024.10.27
 * @since 1.0
 * 
 */
public class SolicitaResponse extends SatResponse {
    
    private static final long serialVersionUID = 20241027L;
    
    /**
     * The request id provided by SAT to identify the download request.
     */
    protected String requestId;
    
    /**
     * Returns a <code>SolicitaResponse</code> with the given parameters.
     * 
     * @param satInstant <code>Instant</code> mesage was received from SAT
     * @param statusCode status code of the received message
     * @param message message received
     * @param requestId 
     *          IdSolicitud attribute. Could be null according to wsdl, however
     *          method <code>isAccept</code> will return false if no 
     *          <code>requestId</code> was provided from SAT.
     * @throws IllegalArgumentException if satInstant is null
     */
    public SolicitaResponse(Instant satInstant, String statusCode, String message, String requestId) {
        super(satInstant, statusCode, message);
        this.requestId = requestId;
    }
    
    /**
     * Returns the request Id provided from SAT in order to identify and do
     * follow-up on the download request that originated this response.
     * 
     * @return the requestId provided from SAT.
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Returns true if this response has an accepted status code and has a non
     * null and non blank <code>requestId</code>.
     * 
     * @return true if this is an accepted response
     */
    @Override public boolean isAccept() {
        return (super.isAccept() && requestId != null && !requestId.isBlank());
    }

    /**
     * Returns a string representation of this response
     * 
     * @return a string representation of this response
     */
    @Override public String toString() {
        return new StringBuilder("SolicitaResponse{")
                .append("instant=").append(satInstant)
                .append(",statusCode=").append(statusCode)
                .append(",message=").append(message)
                .append(",requestId=").append(requestId)
                .append("}")
                .toString();
    }
    
    /*
 <xs:element name="SolicitaDescargaResult">
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
    */
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
    5000 Solicitud de descarga recibida con éxito
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
    
}
