/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt;

import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @version 1-10-24
 * 
 * Defines the methods needed to create a massive download request to the
 * web service.
 * 
 */
public interface Query extends java.io.Serializable {
    
    public Set<String> getRfcReceptores();
    
    public LocalDateTime getFechaInicial();
    public LocalDateTime getFechaFinal();
    
    public String getRfcEmisor();
    public String getRfcSolicitante();
    
    public String getTipoSolicitud(); 
    public String getTipoComprobante();
    public String getEstadoComprobante();
    
    public String getRfcTerceros();
    
    public String getComplemento();
    
    public String getFolio();
    
    public Iterator<String> getAttributes();
    
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
