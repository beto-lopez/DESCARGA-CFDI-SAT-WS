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
import java.time.DateTimeException;

/**
 * {@link Query} mplementation that uses a {@link Builder} class to create
 * SAT query requests. Does not validate SAT rules, it is designed to be used by
 * a closer to the user builder.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.19
 * @since 1.0
 * 
 */
public class QueryMap implements Query {
    
    private static final long serialVersionUID = 20241019L;
    
    ///Attribute names according to wsdl
    /**
     * Value = "Complemento"
     */
    public static final String COMPLEMENTO = "Complemento";
    /**
     * Value = "EstadoComprobante"
     */
    public static final String ESTADO_COMPROBANTE  = "EstadoComprobante";
    /**
     * Value = "FechaInicial"
     */
    public static final String FECHA_INICIAL = "FechaInicial";
    /**
     * Value = "FechaFinal"
     */
    public static final String FECHA_FINAL = "FechaFinal";
    /**
     * Value = "Folio"
     */
    public static final String FOLIO = "Folio";
    /**
     * Value = "RfcACuentaTerceros"
     */
    public static final String RFC_TERCEROS = "RfcACuentaTerceros";
    /**
     * Value = "RfcEmisor"
     */
    public static final String RFC_EMISOR = "RfcEmisor";
    /**
     * Value = "RfcSolicitante"
     */
    public static final String RFC_SOLICITANTE = "RfcSolicitante";
    /**
     * Value = "TipoComprobante"
     */
    public static final String TIPO_COMPROBANTE = "TipoComprobante";
    /**
     * Value = "TipoSolicitud"
     */
    public static final String TIPO_SOLICITUD = "TipoSolicitud";
    
    
    /**
     * TreeMap to store attributes of this query
     */
    protected TreeMap<String,Object> map;
    
    /**
     * TreeSet to store RFC's of receptors
     */
    protected TreeSet<String> receptorsSet;
    
    /**
     * Creates a QueryMap from the specified builder.
     * It will make its own map and set in order to be inmutable.
     * 
     * @param builder the builder that contains the query information
     * @throws IllegalArgumentException if builder is null
     */
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
    /**
     * Returns a new Builder without any data set.
     * 
     * @return a new Builder without any data set
     */
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
    
    /**
     * Returns a string representation of this query
     * 
     * @return a string representation of this query
     */
    @Override public String toString() {
        return new StringBuilder("QueryMap{Receptores:")
                .append(getRfcReceptores().toString())
                .append(", Attributes:").append(map.toString())
                .append("}").toString();
    }
    
    /**
     * Returns a LocalDateTime instance with the specified parameters, set to
     * the first second of the day it represents.
     * 
     * @param year the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day the day-of-month to represent, from 1 to 31
     * @return a LocalDateTime instance with the specified parameters, set to
     *         the first second of the day it represents.
     * @throws DateTimeException if the value of any field is out of range,
     *         or if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime dayStart(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0);
    }
        
    /**
     * Returns a LocalDateTime instance with the specified parameters, set to
     * the last second of the day it represents.
     * 
     * @param year the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day the day-of-month to represent, from 1 to 31
     * @return a LocalDateTime instance with the specified parameters, set to
     *         the last second of the day it represents.
     * @throws DateTimeException if the value of any field is out of range,
     *         or if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime dayEnd(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 23, 59, 59);
    }
    
    /////////////////////////////////////////////////////////////////
    
    /**
     * Formats the specified object. If object is a LocalDateTime it will
     * use <code>format(LocalDateTime)</code> method to format it, otherwise
     * will use <code>toString()</code> method to format it, or will return
     * null if specified object is null.
     * 
     * @param value value to format
     * @return the specified object. If object is a LocalDateTime it will
     *         use <code>format(LocalDateTime)</code> method to format it,
     *         otherwise will use <code>toString()</code> method to format it,
     *         or will return null if specified object is null.
     */
    protected String format(Object value) {
        return ((value == null)
                    ? null
                    :((value instanceof LocalDateTime date)
                        ? format(date)
                        : value.toString()));
    }
    
    /**
     * Returns a new date with the specified date truncated to seconds.
     * 
     * @param date the date this date will have before truncation
     * @return  a new date with the specified date truncated to seconds
     * @throws NullPointerException if date is null
     */
    protected String format(LocalDateTime date) {
        return getFormatter().format(date.truncatedTo(ChronoUnit.SECONDS));
    }
    
    /**
     * Returns the default DateTimeFormatter ISO_LOCAL_DATE_TIME.
     * 
     * @return the default DateTimeFormatter ISO_LOCAL_DATE_TIME
     */
    protected DateTimeFormatter getFormatter() {
        return  DateTimeFormatter.ISO_LOCAL_DATE_TIME;//'2011-12-03T10:15:30'
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Provides methods for building Queries.
     * <p>Instances of <code>Builder</code> can be reused after building a
     * query and can also be reset; or you can make another query with the
     * same parameters and update only the ones you need to change before
     * building a next <code>Query</code> instance.</p>
     * <p>This class does not validate any SAT rule. It should be used as a
     * helper from a builder that can performa that validation.</p>
     * 
     * @version 2024.10.19
     * @since 1.0
     * 
     */
    public static class Builder {
        
        /**
         * HashMap to store attributes
         */
        protected HashMap<String,Object> map;
        
        /**
         * HashSet to store RFC's of receptors
         */
        protected HashSet<String> receptorsSet;
        
        /**
         * Creates a new builder without any property or attribute set
         */
        public Builder() {
            map = new HashMap<>();
            receptorsSet = new HashSet<>();
        }
        
        /**
         * Updates this builder copying attributes from the specified query.
         * This method will call <code>reset()</code> before it begins to copy.
         * 
         * @param query the query to copy the attributes from
         * @return this builder
         * @throws IllegalArgumentException if query is null
         */
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
        
        /**
         * Returns a new QueryMap with the parameters set in this builder.
         * 
         * @return a new QueryMap with the parameters set in this builder.
         */
        public QueryMap build() {
            return new QueryMap(this);
        }
        
        /**
         * Resets all properties and attributes in this builder.
         * 
         * @return this builder
         */
        public Builder reset() {
            map.clear();
            receptorsSet.clear();
            return this;
        }
        
        /**
         * Adds all the strings included in the specified list to the set of
         * RFC's receptors.
         * 
         * @param list with the RFC's of receptors
         * @return this builder
         * @throws NullPointerException if list is null
         */
        public Builder addReceptores(Collection<String> list) {
            list.forEach(receptor-> {
                addReceptor(receptor);
            });
            return this;
        }
        
        /**
         * Adds the specified string to the set of RFC's receptors of this builder.
         * 
         * @param receptor the RFC of the receptor
         * @return this builder
         * @throws IllegalArgumentException if receptor is null or blank
         */
        public Builder addReceptor(String receptor) {
            if (receptor == null || receptor.isBlank()) {
                throw new IllegalArgumentException("need real rfc receptor");
            }
            receptorsSet.add(up(receptor));
            return this;
        }
        
        /**
         * Sets the value of the <code>FECHA_INICIAL</code> attribute with
         * the specified value.
         * 
         * @param fechaInicial the date to set
         * @return this builder
         */
        public Builder setFechaInicial(LocalDateTime fechaInicial) {
            map.put(FECHA_INICIAL, fechaInicial);
            return this;
        }

        /**
         * Sets the value of the <code>FECHA_FINAL</code> attribute with
         * the specified value.
         * 
         * @param fechaFinal the date to set
         * @return this builder
         */
        public Builder setFechaFinal(LocalDateTime fechaFinal) {
            map.put(FECHA_FINAL, fechaFinal);
            return this;
        }
        
        /**
         * Sets the value of the <code>RFC_EMISOR</code> attribute with
         * the specified value.
         * 
         * @param rfcEmisor the RFC of the emisor to set
         * @return this builder
         */
        public Builder setRfcEmisor(String rfcEmisor) {
            map.put(RFC_EMISOR, up(rfcEmisor));
            return this;
        }
        
        /**
         * Sets the value of the <code>RFC_SOLICITANTE</code> attribute with
         * the specified value.
         * 
         * @param rfcSolicitante the RFC of the requestor to set
         * @return this builder
         */
        public Builder setRfcSolicitante(String rfcSolicitante) {
            map.put(RFC_SOLICITANTE, up(rfcSolicitante));
            return this;
        }
        
        /**
         * Sets the value of the <code>TIPO_SOLICITUD</code> attribute with
         * the specified value.
         * 
         * @param tipo the type to set
         * @return this builder
         * @see com.sicomsa.dmt.Query#getTipoSolicitud() 
         */
        public Builder setTipoSolicitud(String tipo) {
            map.put(TIPO_SOLICITUD,tipo);
            return this;
        }
        
        /**
         * Sets the value of the <code>TIPO_COMPROBANTE</code> attribute with
         * the specified value.
         * 
         * @param tipo the type to set
         * @return this builder
         * @see com.sicomsa.dmt.Query#getTipoComprobante() 
         */
        public Builder setTipoComprobante(String tipo) {
            map.put(TIPO_COMPROBANTE, tipo);
            return this;
        }
        
        /**
         * Sets the value of the <code>ESTADO_COMPROBANTE</code> attribute with
         * the specified value.
         * 
         * @param estado the state to set
         * @return this builder
         * @see com.sicomsa.dmt.Query#getEstadoComprobante() 
         */
        public Builder setEstadoComprobante(String estado) {
            map.put(ESTADO_COMPROBANTE, estado);
            return this;
        }
        
        /**
         * Sets the value of the <code>RFC_TERCEROS</code> attribute with
         * the specified value.
         * 
         * @param rfc the RFC to set
         * @return this builder
         */
        public Builder setRfcTerceros(String rfc) {
            map.put(RFC_TERCEROS, up(rfc));
            return this;
        }
        
        /**
         * Sets the value of the <code>COMPLEMENTO</code> attribute with
         * the specified value.
         * 
         * @param complemento the complement to set
         * @return this builder
         */
        public Builder setComplemento(String complemento) {
            map.put(COMPLEMENTO, complemento);
            return this;
        }
        
        /**
         * Sets the value of the <code>FOLIO</code> attribute with
         * the specified value.
         * 
         * @param folio the folio to set
         * @return this builder
         * @see com.sicomsa.dmt.Query#getFolio() 
         */
        public Builder setFolio(String folio) {
            map.put(FOLIO, folio);
            return this;
        }
        ///////////////////////////////////////////////////////////////////
        
        /**
         * Returns the map of this builder.
         * 
         * @return the map of this builder
         */
        protected Map<String,Object> getAttributeMap() {
            return map;
        }
        
        /**
         * Returs the receptor set of this builder.
         * 
         * @return the receptor set of this builder
         */
        protected Set<String> getReceptorsSet() {
            return receptorsSet;
        }
        
        /**
         * Returns the specified rfc to upper case, or null if rfc is null.
         * 
         * @param rfc the specified rfc
         * @return the specified rfc to upper case, or null if rfc is null.
         */
        protected String up(String rfc) {
            return (rfc == null ? null : rfc.toUpperCase());
        }
        
    
    }// Builder
    
}