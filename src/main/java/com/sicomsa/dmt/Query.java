/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.Set;

/**
 * Defines methods needed to create a request to the massive download web service.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024-10-01
 * @since 1.0
 * 
 */
public interface Query extends java.io.Serializable {
    
    /**
     * Returns a Set of CFDI's RFC receptors.
     * 
     * @return a Set of receptor RFC's
     */
    public Set<String> getRfcReceptores();
    
    /**
     * Returns the initial date range CFDI must have
     * 
     * @return the initial date range CFDI must have
     */
    public LocalDateTime getFechaInicial();
    
    /**
     * Returns the final date range CFDI must have
     * 
     * @return the final date range CFDI must have
     */
    public LocalDateTime getFechaFinal();
    
    /**
     * Returns the RFC of the emisor of the CFDI
     * 
     * @return the RFC of the emisor of the CFDI
     */
    public String getRfcEmisor();
    
    /**
     * Returns the RFC of the solicitor of the download request.
     * 
     * @return the RFC of the solicitor of the download request.
     */
    public String getRfcSolicitante();
    
    /**
     * Returns the type of the request. Required field.
     * <p>Types of requests according to wsdl:</p>
     * <ul>
     * <li>Metadata</li>
     * <li>CFDI</li>
     * <li>PDF</li>
     * <li>PDFCOCEMA</li>
     * </ul>
     * 
     * @return the type of the request.
     */
    public String getTipoSolicitud(); 
    
    /**
     * Returns the CFDIs type to download. May be null.
     * <p>CFDI types:
     * <ul>
     * <li>NULL</li>
     * <li>I = Ingreso</li>
     * <li>E = Egreso</li>
     * <li>T = Traslado</li>
     * <li>N = Nomina</li>
     * <li>P = Pago</li>
     * </ul>
     * 
     * @return the CFDI's type to download. May be null.
     */
    public String getTipoComprobante();
    
    /**
     * Returns the state of CFDIs to download
     * <p>CFDI states:</p>
     * <ul>
     * <li>NULL</li>
     * <li>0 = Cancelado</li>
     * <li>1 = Vigente</li>
     * </ul>
     * 
     * @return the state of CFDIs to download.
     */
    public String getEstadoComprobante();
    
    /**
     * Returns the third party RFC of CFDIs to download.
     * 
     * @return the third party RFC of CFDIs to download
     */
    public String getRfcTerceros();
    
    /**
     * Returns the type of complement of the CFDIs to include. May be null.
     * 
     * @return the type of complement of the CFDIs to include. May be null.
     */
    public String getComplemento();
    
    /**
     * Returns the particular UUID of the CFDI to download.<p>
     * If UUID is specified, these parameters shuld not be declared:
     * FechaInicial, FechaFinal, RfcEmisor y RfcSolicitante.
     * 
     * @return the particular UUID of the CFDI to download.
     */
    public String getFolio();
    
    /**
     * Returns an Iterator of the names of the attributes that have a non null
     * value set. Names should be any of these defined parameter names:
     * <ul>
     * <li>Complemento</li>
     * <li>EstadoComprobante</li>
     * <li>FechaInicial</li>
     * <li>FechaFinal</li>
     * <li>Folio</li>
     * <li>RfcEmisor</li>
     * <li>RfcSolicitante</li>
     * <li>TipoComprobante</li>
     * <li>TipoSolicitud</li>
     * <li>RfcACuentaTerceros</li>
     * </ul>
     * 
     * @return an Iterator of the names of the attributes that have a non null
     *         value set.
     */
    public Iterator<String> getAttributes();
    
    /**
     * Returns the value of the attribute with the given <code>name</code>
     * 
     * @param name of the attribute to get value of
     * @return the value of the attribute with the given <code>name</code>
     */
    public String getAttributeValue(String name);
    
   
    /*
<xs:attribute name="Complemento" type="xs:string"/>
<xs:attribute name="EstadoComprobante" type="tns:EstadoComprobante"/>
<xs:attribute name="FechaInicial" type="xs:dateTime"/>
<xs:attribute name="FechaFinal" type="xs:dateTime"/>
<xs:attribute name="Folio" type="xs:string"/>
<xs:attribute name="RfcEmisor" type="xs:string"/>
<xs:attribute name="RfcSolicitante" type="xs:string"/>
<xs:attribute name="TipoComprobante" type="tns:TipoDeComprobante"/>
<xs:attribute name="TipoSolicitud" type="tns:TipoDescargaMasivaTerceros" use="required"/>
<xs:attribute name="RfcACuentaTerceros" type="xs:string"/>
    
<xs:simpleType name="EstadoComprobante">
<xs:restriction base="xs:string">
<xs:enumeration value="0"/>
<xs:enumeration value="1"/>
</xs:restriction>
</xs:simpleType>
    
<xs:simpleType name="TipoDeComprobante">
<xs:restriction base="xs:string">
<xs:enumeration value="I"/>
<xs:enumeration value="E"/>
<xs:enumeration value="T"/>
<xs:enumeration value="N"/>
<xs:enumeration value="P"/>
</xs:restriction>
</xs:simpleType>
    
<xs:simpleType name="TipoDescargaMasivaTerceros">
<xs:restriction base="xs:string">
<xs:enumeration value="Metadata"/>
<xs:enumeration value="CFDI"/>
<xs:enumeration value="PDF"/>
<xs:enumeration value="PDFCOCEMA"/>
</xs:restriction>
</xs:simpleType>
    */
}
