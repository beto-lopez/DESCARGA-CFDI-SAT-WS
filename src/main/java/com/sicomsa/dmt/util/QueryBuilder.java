/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import com.sicomsa.dmt.ExtendedQuery;
import com.sicomsa.dmt.QueryType;

import java.time.LocalDateTime;

/**
 * Builds queries for emitted and received certificates as of SAT's download
 * web service version 1.5
 * <p>You can reuse this builder. It will retain properties set even after each
 * build, or you can build and {@link QueryBuilder#reset() reset} it to clear all properties.
 * </p>
 * <p><b>Important:</b> When requesting CFDI type of download set <code>EstadoComprobante</code>
 * "Vigente", to avoid a response error, since SAT will not allow to download canceled CFDI's.
 * You can set to <code>EstadoComprobante</code> "Todos", if your choice of download is Metadata.</p>
 * <p>Examples:</p>
 * <pre>
 * {@code
 *      String rfc = "RFC SOLICITANTE"; //Valid RFC expected here
 *
 *      //We want to make emitted an received dowload queries for this rfc
 *      //from 2020-06-01 to 2020-06-15.
 *      //Properties set on this builder will remain until changed.
 *      QueryBuilder builder =
 *          new QueryBuilder()
 *              .setRfcSolicitante(rfc)
 *              .setFechaInicial(QueryMap.dayStart(2020, 6, 1))
 *              .setFechaFinal(QueryMap.dayEnd(2020, 6, 15));
 *
 *      //We use previous builder with defaults set and set properties
 *      //for emitted certificates
 *      //Note: since we are going to request download CFDI type we must
 *      //      set state of certificate to "Vigente", to avoid getting an
 *      //      error from SAT since we can not download cancelled CFDIs
 *      Query emitidos = builder
 *          .setRfcEmisor(rfc)
 *          .setTipoSolicitud(QueryBuilder.TIPO_CFDI)
 *          .setEstadoComprobante(QueryBuilder.ESTADO_VIGENTE)
 *          .buildEmitidosQuery(); //we use this method for emitted certificates
 *
 *      //We set an empty String("") instead of null to RfcEmisor property if we
 *      //want the received certificate from "any" emisor.
 *      //Since we are downloading Metadata we can also request canceled certificates,
 *      //so we set "Todos" in certificate state.
 *      //We do not need to set period or rfc emisor, since we alreade set them
 *      Query recibidos = builder
 *          .setRfcEmisor("")
 *          .setTipoSolicitud(QueryBuilder.TIPO_METADATA)
 *          .setEstadoComprobante(QueryBuilder.ESTADO_TODOS)
 *          .buildRecibidosQuery(rfc); //we use this method for received certificates
 * 
 *      //For download requests by Id we use FolioQueryBuilder
 *      Query folio = new FolioQueryBuilder()
 *          .setFolio("C1FFD659-6E3A-XXXX-9DDD-TTTT96B2471")
 *          .setRfcSolicitante(rfc)
 *          .build();
 * }
 * </pre>
 * <code>QueryBuilder</code> is not thread safe.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.2
 * @since       1.5 
 */
public class QueryBuilder {
    
    /**
     * Value of CFDI when defining TipoSolicitud
     */
    public static final String TIPO_CFDI = "CFDI";
    
    /**
     * Value of Metadata when defining TipoSolicitud
     */
    public static final String TIPO_METADATA = "Metadata";
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Value of TipoComprobante "Ingreso"
     */
    public static final String TIPO_INGRESO = "I";
    
    /**
     * Value of TipoComprobante "Egreso"
     */
    public static final String TIPO_EGRESO = "E";
    
    /**
     * Value of TipoComprobante "Traslado"
     */
    public static final String TIPO_TRASLADO = "T";
    
    /**
     * Value of TipoComprobante "Nomina"
     */
    public static final String TIPO_NOMINA = "N";
    
    /**
     * Value of TipoComprobante "Pago"
     */
    public static final String TIPO_PAGO = "P";
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Value of EstadoComprobante "all"
     */
    public static final String ESTADO_TODOS = "Todos";
    
    /**
     * Value of EstadoComprobante "Cancelado"
     */
    public static final String ESTADO_CANCELADO = "Cancelado";
    
    /**
     * Value of EstadoComprobante "Vigente"
     */
    public static final String ESTADO_VIGENTE = "Vigente";
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Builder used to do the builds
     */
    protected QueryMap.Builder builder;
        
    /**
     * Creates a new <code>QueryBuilder</code>
     */
    public QueryBuilder() {
        builder = new QueryMap.Builder();
    }
    
    /**
     * Resets all attributes from this builder
     * 
     * @return this builder
     */
    public QueryBuilder reset() {
        builder.reset();
        return this;
    }
        
    /**
     * Sets the value of the <code>"FechaInicial"</code> attribute with
     * the specified value.
     * 
     * @param fecha the date to set
     * @return this builder
     */
    public QueryBuilder setFechaInicial(LocalDateTime fecha) {
        builder.setFechaInicial(fecha);
        return this;
    }
    
    /**
     * Sets the value of the <code>"FechaFinal"</code> attribute with
     * the specified value.
     * 
     * @param fecha the date to set
     * @return this builder
     */
    public QueryBuilder setFechaFinal(LocalDateTime fecha) {
        builder.setFechaFinal(fecha);
        return this;
    }
    
    /**
     * Sets the value of the <code>"RfcEmisor"</code> attribute with
     * the specified value.
     * 
     * @param rfc the RFC of the emisor to set
     * @return this builder
     */
    public QueryBuilder setRfcEmisor(String rfc) {
        builder.setRfcEmisor(rfc);
        return this;
    }
    
    /**
     * Sets the value of the <code>"RfcSolicitante"</code> attribute with
     * the specified value.
     * 
     * @param rfc the RFC of the requestor to set
     * @return this builder
     */
    public QueryBuilder setRfcSolicitante(String rfc) {
        builder.setRfcSolicitante(rfc);
        return this;
    }
    
    /**
     * Sets the value of the <code>"TipoSolicitud"</code> attribute with
     * the specified value.
     * 
     * @param tipo the type to set
     * @return this builder
     * @see com.sicomsa.dmt.Query#getTipoSolicitud() 
     */
    public QueryBuilder setTipoSolicitud(String tipo) {
        builder.setTipoSolicitud(tipo);
        return this;
    }
    
    /**
     * Sets the value of the <code>"TipoComprobante"</code> attribute with
     * the specified value.
     * 
     * @param tipo the type to set
     * @return this builder
     * @see com.sicomsa.dmt.Query#getTipoComprobante() 
     */
    public QueryBuilder setTipoComprobante(String tipo) {
        builder.setTipoComprobante(tipo);
        return this;
    }
    
    /**
     * Sets the value of the <code>"EstadoComprobante"</code> attribute with
     * the specified value.
     * 
     * @param estado the state to set
     * @return this builder
     * @see com.sicomsa.dmt.Query#getEstadoComprobante() 
     */
    public QueryBuilder setEstadoComprobante(String estado) {
        builder.setEstadoComprobante(estado);
        return this;
    }
    
    /**
     * Sets the value of the <code>"RfcACuentaTerceros"</code> attribute with
     * the specified value.
     * 
     * @param rfc the RFC to set
     * @return this builder
     */
    public QueryBuilder setRfcTerceros(String rfc) {
        builder.setRfcTerceros(rfc);
        return this;
    }
    
    /**
     * Sets the value of the <code>"Complemento"</code> attribute with
     * the specified value.
     * 
     * @param complemento the complement to set
     * @return this builder
     */
    public QueryBuilder setComplemento(String complemento) {
        builder.setComplemento(complemento);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////    
        
    /**
     * Returns an <code>ExtendedQuery</code> to request <b>emitted</b> certificates
     * to any receptor built from the attributes set to this builder.
     * 
     * @return <code>ExtendedQuery</code> to request <b>emitted</b> certificates
     *         to any receptor built from the attributes set to this builder
     */
    public ExtendedQuery buildEmitidosQuery() {
        return buildEmitidosQuery(null);
    }
          
    /**
     * Returns an <code>ExtendedQuery</code> to request <b>emitted</b> certificates
     * to the specific rfc receptors included in the received list; build from
     * the attributes set to this builder.
     * 
     * @param receptorsList list of rfc receptors that certificates were emmited to.
     *                      It can be null or empty, which will mean emitted to
     *                      "any" rfc receptor.
     * 
     * @return  <code>ExtendedQuery</code> to request <b>emitted</b> certificates
     *          to the specific rfc receptors included in the received list; build from
     *          the attributes set to this builder
     */
    public ExtendedQuery buildEmitidosQuery(java.util.List<String> receptorsList) {
        ExtendedQueryMap query  = new ExtendedQueryMap(builder, QueryType.EMITIDOS);
        if (receptorsList != null && !receptorsList.isEmpty()) {
            query.addAllRfcReceptors(receptorsList);
        }
        return query;
    }
    
    ///////////////////////////////////////////////////////////////////////////
     
    /**
     * Returns an <code>ExtendedQuery</code> to request the certificates the
     * specified rfc receptor <b>received</b>.
     *  
     * @param rfcReceptor rfc of the receiver of certificates. <b>This is a required
     *                    query parameter by SAT</b>, when requesting received certificates
     * .
     * @return <code>ExtendedQuery</code> to request the certificates the
     *         specified rfc receptor <b>received</b>
     * @throws IllegalArgumentException if rfcReceptor is null or empty
     */
    public ExtendedQuery buildRecibidosQuery(String rfcReceptor) {
        if (rfcReceptor == null || rfcReceptor.isEmpty()) {
            throw new IllegalArgumentException("rfc is required");
        }
        ExtendedQueryMap query = new ExtendedQueryMap(builder, QueryType.RECIBIDOS);
        query.setRfcReceptor(rfcReceptor);
        return query;
    }
    
    
}
