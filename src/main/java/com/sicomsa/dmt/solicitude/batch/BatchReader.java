/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;


import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.svc.SolicitaSvc;
import com.sicomsa.dmt.svc.VerificaSvc;
import com.sicomsa.dmt.svc.DescargaSvc;
import com.sicomsa.dmt.util.QueryMap;
import com.sicomsa.dmt.util.SvcParseException;
import com.sicomsa.dmt.util.SOAPUtils;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.Name;

import javax.xml.namespace.QName;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import java.time.Instant;
import java.time.LocalDateTime;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.01
 * 
 * BatchReader reads requests and SAT's responses to those requests
 * from a file in the format defined by BatchWriter.
 *
 */
public class BatchReader {
    
    protected String version = "1.0";
    protected SOAPMessage message;
    
    protected BatchReader(SOAPMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("invalid message");
        }
        this.message = message;
    }
    
    public static BatchReader read(File file) throws IOException, SOAPException, SvcParseException {
        if (file == null) {
            throw new IllegalArgumentException("invalid file");
        }
        SOAPMessage message = SOAPUtils.loadSoap(file); //throws soapEx & ioex
        BatchReader reader = new BatchReader(message);
        reader.checkVersion(); //throws soapEx & svcParce 
        return reader;
    }
    
    protected void checkVersion() throws SOAPException, SvcParseException {
        if (!this.version.equals(parseMessageVersion())) {
            throw new SvcParseException("BatchReader invalid version, expecting:"+version);
        }
    }
    
    protected String parseMessageVersion() throws SOAPException, SvcParseException {
        SOAPElement element = SOAPUtils.parseChild(message.getSOAPBody(), BatchWriter.VERSION);
        return element.getTextContent();
    }
    
    
    public Iterator<BatchRequest> getRequests() throws SOAPException, SvcParseException { 
        SOAPElement requests = SOAPUtils.parseChild(message.getSOAPHeader(), BatchWriter.REQUESTS, false);
        if (requests == null) {
            return java.util.Collections.emptyIterator();
        }
        return new RequestsIterator(requests.getChildElements(BatchWriter.REQUEST));
    }
    
    public Iterator<EventResponse> getResponses() throws SOAPException {
        return new ResponsesIterator(
                message.getSOAPBody().getChildElements(BatchWriter.RESPONSE));
    }
    
    protected class ResponsesIterator implements Iterator<EventResponse> {
        final Iterator<Node> iterator;
        public ResponsesIterator(Iterator<Node> iterator) {
            if (iterator == null) {
                throw new NullPointerException();
            }
            this.iterator = iterator;
        }
        @Override public boolean hasNext() {
            return iterator.hasNext();
        }
        @Override public EventResponse next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return parseResponse((SOAPElement)iterator.next());
            }
            catch (SOAPException e) {
                throw new SvcParseException("unable to parse next response", e);
            }
        }
        @Override public void remove() {
            throw new UnsupportedOperationException("can not delete responses through this iterator");
        }
    }

    protected EventResponse parseResponse(SOAPElement element) throws SOAPException {
        long processId = SOAPUtils.parseLongAttributeValue(element,BatchWriter.BATCH_ID);
        String status = element.getAttributeValue(BatchWriter.STATUS);
        String type = element.getAttributeValue(BatchWriter.TYPE);
        return switch (type) {
            case "SolicitaResponse"->
                new EventResponse(processId,
                            parseSolicitaResponse(element),
                        status);
            case "VerificaResponse"->
                new EventResponse(processId,
                          parseVerificaResponse(element),
                        status);
            case "DescargaResponse"->
                new EventResponse(processId,
                        parseDescargaResponse(element),
                        status);
            default->
                throw new SvcParseException("unknown response type:"+type);
        };
                
    }
    
    
    protected SolicitaResponse parseSolicitaResponse(SOAPElement element) {
        SOAPElement response = SOAPUtils.parseChild(element, BatchWriter.SOLICITA_RESPONSE); //throws SvcParseEx
        return new SolicitaResponse(
            parseInstant(response, BatchWriter.INSTANT),
            response.getAttributeValue(SolicitaSvc.STS_CODE),
            response.getAttributeValue(SolicitaSvc.MESSAGE),
            response.getAttributeValue(SolicitaSvc.REQUEST_ID));
    }
    
    protected VerificaResponse parseVerificaResponse(SOAPElement element) {
        SOAPElement response = SOAPUtils.parseChild(element, BatchWriter.VERIFICA_RESPONSE); //throws SvcParseEx
        VerificaResponse.Builder builder = new VerificaResponse.Builder();
        builder.setSatInstant(parseInstant(response, BatchWriter.INSTANT))
               .setStatusCode(response.getAttributeValue(VerificaSvc.STS_CODE))
               .setMessage(response.getAttributeValue(VerificaSvc.MESSAGE))
               .setSolicitudeStsCode(response.getAttributeValue(VerificaSvc.SOLICITUDE_STS_CODE_NAME))
               .setSolicitudeState(SOAPUtils.parseIntAttributeValue(response, VerificaSvc.STATE_NAME))
               .setCfdisAmmount(SOAPUtils.parseIntAttributeValue(response, VerificaSvc.CFDIS_NAME))
               .setRequestId(response.getAttributeValue(VerificaSvc.ID_NAME));
        Iterator<Node> iterator = response.getChildElements(VerificaSvc.PACKAGE_ID_NAME);
        while (iterator.hasNext()) {
            builder.addPackageId(iterator.next().getTextContent());
        }
        return builder.build();
    }
    
    protected Instant parseInstant(SOAPElement element, QName name) {
        String instant = element.getAttributeValue(name);
        return (instant == null ? null : Instant.parse(instant));
    }
    
    
    protected DescargaResponse parseDescargaResponse(SOAPElement element) {
        SOAPElement response = SOAPUtils.parseChild(element, BatchWriter.DESCARGA_RESPONSE); //throws SvcParseEx
                
        return new DisposedResponse(
            parseInstant(response, BatchWriter.INSTANT),
            response.getAttributeValue(DescargaSvc.STS_CODE),
            response.getAttributeValue(DescargaSvc.MESSAGE),
            response.getAttributeValue(DescargaSvc.PACKAGE_ID),
            isDisposed(response));
    }
    
    protected boolean isDisposed(SOAPElement response) {
        String disposedValue = response.getAttributeValue(BatchWriter.DISPOSED);
        return (disposedValue != null && disposedValue.equalsIgnoreCase("true"));
    }
    
    protected static class DisposedResponse extends DescargaResponse {
        private static final long serialVersionUID = 20250104L;
        
        public DisposedResponse(Instant satInstant, String statusCode,
                String message, String packageId, boolean disposed) {
            super(satInstant, statusCode, message, packageId, disposed);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected static class RequestsIterator implements Iterator<BatchRequest> {
        protected Iterator<Node> iterator;
        
        /**
         * iterator nodes must be SOAPElements
         * @param iterator 
         */
        public RequestsIterator(Iterator<Node> iterator) {
            if (iterator == null) {
                throw new NullPointerException();
            }
            this.iterator = iterator;
        }
        @Override public boolean hasNext() {
            return iterator.hasNext();
        }
        @Override public BatchRequest next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return parseRequest((SOAPElement)iterator.next());
        }
        @Override public void remove() {
            throw new UnsupportedOperationException("can not delete requests through this iterator");
        }
        
        protected BatchRequest parseRequest(SOAPElement element) {
            return new BatchRequest(
                SOAPUtils.parseLongAttributeValue(element, BatchWriter.BATCH_ID),
                element.getAttributeValue(BatchWriter.RFC),
                parseQuery(getQueryElement(element)));
        }
        
        protected SOAPElement getQueryElement(SOAPElement element) {
            return SOAPUtils.parseGrandchild(
                    element,
                    SolicitaSvc.SOLICITA,
                    SolicitaSvc.SOLICITUD);
        }
       
        protected Query parseQuery(SOAPElement solicitud) {
            QueryMap.Builder builder = new QueryMap.Builder();
            Iterator<Name> attributes = solicitud.getAllAttributes();
            while (attributes.hasNext()) {
                Name name = attributes.next();
                String value = solicitud.getAttributeValue(name);
                switch (name.getLocalName()) {
                    case QueryMap.COMPLEMENTO->
                        builder.setComplemento(value);
                    case QueryMap.ESTADO_COMPROBANTE->
                        builder.setEstadoComprobante(value);
                    case QueryMap.FECHA_INICIAL->
                        builder.setFechaInicial(LocalDateTime.parse(value));
                    case QueryMap.FECHA_FINAL->
                        builder.setFechaFinal(LocalDateTime.parse(value));
                    case QueryMap.FOLIO->
                        builder.setFolio(value);
                    case QueryMap.RFC_TERCEROS->
                        builder.setRfcTerceros(value);
                    case QueryMap.RFC_EMISOR->
                        builder.setRfcEmisor(value);
                    case QueryMap.RFC_SOLICITANTE->
                        builder.setRfcSolicitante(value);
                    case QueryMap.TIPO_COMPROBANTE->
                        builder.setTipoComprobante(value);
                    case QueryMap.TIPO_SOLICITUD->
                        builder.setTipoSolicitud(value);
                }
            }
            SOAPElement receptores =
                    SOAPUtils.parseChild(solicitud, SolicitaSvc.RFC_RECEPTORES, false);
            if (receptores != null) {
                Iterator<Node> rfcNodes =
                        receptores.getChildElements(SolicitaSvc.RFC_RECEPTOR);
                 while (rfcNodes.hasNext()) {
                    builder.addReceptor(rfcNodes.next().getTextContent());
                }
            }
            return builder.build();
        }
    
    }

}
