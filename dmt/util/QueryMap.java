/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import com.sicomsa.dmt.Query;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.19
 * 
 * Utility to build SAT queries. Does not validate SAT rules, it is designed
 * to be used by a closer to the user builder.
 */
public class QueryMap implements Query {
    
    private static final long serialVersionUID = 20241019L;
    
    ///Attribute names according to wsdl
    public static final String COMPLEMENTO = "Complemento";
    public static final String ESTADO_COMPROBANTE  = "EstadoComprobante";
    public static final String FECHA_INICIAL = "FechaInicial";
    public static final String FECHA_FINAL = "FechaFinal";
    public static final String FOLIO = "Folio";
    public static final String RFC_TERCEROS = "RfcACuentaTerceros";
    public static final String RFC_EMISOR = "RfcEmisor";
    public static final String RFC_SOLICITANTE = "RfcSolicitante";
    public static final String TIPO_COMPROBANTE = "TipoComprobante";
    public static final String TIPO_SOLICITUD = "TipoSolicitud";
    
    
    protected TreeMap<String,Object> map;
    protected TreeSet<String> receptorsSet;
    
    protected QueryMap(Builder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("need builder");
        }
        this.map = new TreeMap<>();
        builder.getAttributeMap().forEach((key,value)-> {
            if (value != null) {
                this.map.put(key, value);
            }
        });
        if (!builder.getReceptorsSet().isEmpty()) {
            receptorsSet = new TreeSet<>(builder.getReceptorsSet());
        }
    }
        
    ////////////////////////////////////////////////////////////////////////////
    public static Builder builder() {
        return new Builder();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////  Query implementation
    
    @Override public Set<String> getRfcReceptores() {
        return (receptorsSet == null ? Collections.emptySet()
                : Collections.unmodifiableSet(receptorsSet));
    }
        
    @Override public LocalDateTime getFechaInicial() {
        return (LocalDateTime)map.get(FECHA_INICIAL);
    }
    @Override public LocalDateTime getFechaFinal() {
        return (LocalDateTime)map.get(FECHA_FINAL);
    }
    @Override public String getRfcEmisor() {
        return (String)map.get(RFC_EMISOR);
    }
    @Override public String getRfcSolicitante() {
        return (String)map.get(RFC_SOLICITANTE);
    }
    @Override public String getTipoSolicitud() {
        return (String)map.get(TIPO_SOLICITUD);
    }
    @Override public String getTipoComprobante() {
        return (String)map.get(TIPO_COMPROBANTE);
    }
    @Override public String getEstadoComprobante() {
        return (String)map.get(ESTADO_COMPROBANTE);
    }
    @Override public String getRfcTerceros() {
        return (String)map.get(RFC_TERCEROS);
    }
    @Override public String getComplemento() {
        return (String)map.get(COMPLEMENTO);
    }
    @Override public String getFolio() {
        return (String)map.get(FOLIO);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    @Override public Iterator<String> getAttributes() {
        return Collections.unmodifiableSet(map.keySet()).iterator();
    }
    
    @Override public String getAttributeValue(String name) {
        return format(map.get(name));
    }
    
    ////////////////////////////////////////////////////////////////////////////
    

    @Override public String toString() {
        return new StringBuilder("QueryMap{Receptores:")
                .append(getRfcReceptores().toString())
                .append(", Attributes:").append(map.toString())
                .append("}").toString();
    }
    
    
    public static LocalDateTime dayStart(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0);
    }
        
    public static LocalDateTime dayEnd(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 23, 59, 59);
    }
    
    /////////////////////////////////////////////////////////////////
    
    protected String format(Object value) {
        return ((value == null)
                    ? null
                    :((value instanceof LocalDateTime date)
                        ? format(date)
                        : value.toString()));
    }
    
    protected String format(LocalDateTime date) {
        return getFormatter().format(date.truncatedTo(ChronoUnit.SECONDS));
    }
    
    protected DateTimeFormatter getFormatter() {
        return  DateTimeFormatter.ISO_LOCAL_DATE_TIME;//'2011-12-03T10:15:30'
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static class Builder {
        
        protected HashMap<String,Object> map;
        protected HashSet<String> receptorsSet;
        
        public Builder() {
            map = new HashMap<>();
            receptorsSet = new HashSet<>();
        }
        
        public Builder resetQuery(Query query) {
            if (query == null) {
                throw new IllegalArgumentException("invalid query");
            }
            reset();
            if (query.getRfcReceptores() != null) {
                receptorsSet.addAll(query.getRfcReceptores());
            }
            setFechaInicial(query.getFechaInicial());
            setFechaFinal(query.getFechaFinal());
            setRfcEmisor(query.getRfcEmisor());
            setRfcSolicitante(query.getRfcSolicitante());
            setTipoSolicitud(query.getTipoSolicitud());
            setTipoComprobante(query.getTipoComprobante());
            setEstadoComprobante(query.getEstadoComprobante());
            setRfcTerceros(query.getRfcTerceros());
            setComplemento(query.getComplemento());
            setFolio(query.getFolio());
            return this;
        }
        
        public QueryMap build() {
            return new QueryMap(this);
        }
        
        public Builder reset() {
            map.clear();
            receptorsSet.clear();
            return this;
        }
        
        public Builder addReceptores(Collection<String> list) {
            list.forEach(receptor-> {
                addReceptor(receptor);
            });
            return this;
        }
        
        public Builder addReceptor(String receptor) {
            if (receptor == null || receptor.isBlank()) {
                throw new IllegalArgumentException("need real rfc receptor");
            }
            receptorsSet.add(up(receptor));
            return this;
        }
        
        public Builder setFechaInicial(LocalDateTime fechaInicial) {
            map.put(FECHA_INICIAL, fechaInicial);
            return this;
        }

        public Builder setFechaFinal(LocalDateTime fechaFinal) {
            map.put(FECHA_FINAL, fechaFinal);
            return this;
        }
        
        public Builder setRfcEmisor(String rfcEmisor) {
            map.put(RFC_EMISOR, up(rfcEmisor));
            return this;
        }
        public Builder setRfcSolicitante(String rfcSolicitante) {
            map.put(RFC_SOLICITANTE, up(rfcSolicitante));
            return this;
        }
        /**
         * TipoSolicitud TipoDescargaMasivaTerceros
         * Define el tipo de descarga:
         * • Metadata
         * • CFDI
         * 
         * @param tipo
         * @return 
         */
        public Builder setTipoSolicitud(String tipo) {
            map.put(TIPO_SOLICITUD,tipo);
            return this;
        }
        
        /**
         * Define el tipo de comprobante:
         * • Null
         * • I = Ingreso
         * • E = Egreso
         * • T= Traslado
         * • N = Nomina
         * • P = Pago
         * 
         * Null es el valor predeterminado y en caso de no declararse,
         * se obtendrán todos los comprobantes sin importar el tipo comprobante
         * 
         * @param tipo
         * @return 
         */
        public Builder setTipoComprobante(String tipo) {
            map.put(TIPO_COMPROBANTE, tipo);
            return this;
        }
        public Builder setEstadoComprobante(String estado) {
            map.put(ESTADO_COMPROBANTE, estado);
            return this;
        }
        public Builder setRfcTerceros(String rfc) {
            map.put(RFC_TERCEROS, up(rfc));
            return this;
        }
        public Builder setComplemento(String complemento) {
            map.put(COMPLEMENTO, complemento);
            return this;
        }
        public Builder setFolio(String folio) {
            map.put(FOLIO, folio);
            return this;
        }
        ///////////////////////////////////////////////////////////////////
        
        protected Map<String,Object> getAttributeMap() {
            return map;
        }
        
        protected Set<String> getReceptorsSet() {
            return receptorsSet;
        }
        
        protected String up(String rfc) {
            return (rfc == null ? null : rfc.toUpperCase());
        }
        
    
    }// Builder
    
}