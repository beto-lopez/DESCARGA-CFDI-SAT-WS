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
import java.time.format.DateTimeParseException;


/**
 * <code>BatchReader</code> provides methods to parse <code>BatchRequest</code>s
 * and <code>EventResponse</code>s contained in Nodes of a <code>SOAPMessage</code>
 * generated and saved to a file by a {@link BatchWriter}.
 * <p>The file that will be read by this reader should have a <code>SOAPMessage</code>
 * that can be extracted from a xml format file.</p>
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.01
 * @since 1.0
 * 
 *
 */
public class BatchReader {
    
    /**
     * Version of this reader
     */
    protected String version = "1.0";
    
    /**
     * Message to read from
     */
    protected SOAPMessage message;
    
    /**
     * Creates a new <code>BatchReader</code> that will read from the specified
     * message.
     * 
     * @param message message to read from
     * @throws IllegalArgumentException if message is null
     */
    protected BatchReader(SOAPMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("invalid message");
        }
        this.message = message;
    }
    
    /**
     * Returns a new <code>BatchReader</code> that will read from the specified
     * file.
     * 
     * @param file file to read
     * @return a new <code>BatchReader</code> that will read from the specified
     *         file
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if file is null
     * @throws SvcParseException if file version not compatible or other parsing
     *                           problems arose
     */
    public static BatchReader read(File file) throws IOException, SOAPException, SvcParseException {
        if (file == null) {
            throw new IllegalArgumentException("invalid file");
        }
        SOAPMessage message = SOAPUtils.loadSoap(file); //throws soapEx & ioex
        BatchReader reader = new BatchReader(message);
        reader.checkVersion(); //throws soapEx & svcParce 
        return reader;
    }
    
    /**
     * Validates this reader's version with the one of its message property, and
     * throws <code>SvcParseException</code> if not compatible.
     * 
     * @throws SOAPException if there were SOAP related problems
     * @throws SvcParseException if version not compatible or other parsing problems arose
     */
    protected void checkVersion() throws SOAPException {
        if (!this.version.equals(parseMessageVersion())) {
            throw new SvcParseException("BatchReader invalid version, expecting:"+version);
        }
    }
    
    /**
     * Returns the version of the message property of this reader.
     * 
     * @return the version of the message property of this reader
     * @throws SOAPException if there were SOAP related problems
     * @throws SvcParseException if the version Node was not found
     */
    protected String parseMessageVersion() throws SOAPException {
        SOAPElement element = SOAPUtils.parseChild(message.getSOAPBody(), BatchWriter.VERSION);
        return element.getTextContent();
    }
    
    
    /**
     * Returns a <code>BatchRequest</code> iterator.
     * 
     * @return a <code>BatchRequest</code> iterator
     * @throws SOAPException if there were SOAP related problems
     */
    public Iterator<BatchRequest> getRequests() throws SOAPException { 
        SOAPElement requests = SOAPUtils.parseChild(message.getSOAPHeader(), BatchWriter.REQUESTS, false);
        if (requests == null) {
            return java.util.Collections.emptyIterator();
        }
        return new RequestsIterator(requests.getChildElements(BatchWriter.REQUEST));
    }
    
    /**
     * Returns an <code>EventeResponse</code> iterator.
     * 
     * @return an <code>EventeResponse</code> iterator
     * @throws SOAPException if there were SOAP related problems while building iterator
     */
    public Iterator<EventResponse> getResponses() throws SOAPException {
        return new ResponsesIterator(
                message.getSOAPBody().getChildElements(BatchWriter.RESPONSE));
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This class implements an <code>EventResponse</code> iterator, parsing
     * responses from a specified Node iterator.
     * <p>This iterator does not support removals.</p>
     */
    protected class ResponsesIterator implements Iterator<EventResponse> {
        
        /**
         * Node iterator
         */
        final Iterator<Node> iterator;
        
        /**
         * Creates a new <code>ResponsesIterator</code> with the specified Node
         * iterator.
         * 
         * @param iterator node iterator
         * @throws NullPointerException if iterator is null
         */
        public ResponsesIterator(Iterator<Node> iterator) {
            if (iterator == null) {
                throw new NullPointerException();
            }
            this.iterator = iterator;
        }
        
        /**
         * Returns true if this iterator has more elements.
         * 
         * @return true if this iterator has more elements
         */
        @Override public boolean hasNext() {
            return iterator.hasNext();
        }
        
        /**
         * Returns the next <code>EventResponse</code> of this iterator.
         * 
         * @return the next <code>EventResponse</code> of this iterator
         * @throws NoSuchElementException if the iteration has no more elements
         * @throws SvcParseException if unable to parse next element
         */
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
        
        /**
         * This method will throw an UnsupportedOperationException to avoid
         * removals through this iterator.
         * 
         * @throws UnsupportedOperationException if not supported
         */
        @Override public void remove() {
            throw new UnsupportedOperationException("can not delete responses through this iterator");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new <code>EventResponse</code> parsed from the specified element.
     * 
     * @param element <code>SOAPElement</code> to be parsed
     * @return a new <code>EventResponse</code> parsed from the specified element
     * @throws SOAPException if there were SOAP related problems
     * @throws DateTimeParseException if unable to parse instant from element
     * @throws IllegalArgumentException if element is null
     * @throws SvcParseException if there were parse related problems
     */
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
    
    
    /**
     * Returns a new <code>SolicitaResponse</code> parsed from the specified element.
     * 
     * @param element <code>SOAPElement</code> to be parsed
     * @return a new <code>SolicitaResponse</code> parsed from the specified element
     * @throws DateTimeParseException if unable to parse instant from element
     * @throws IllegalArgumentException if element is null or parsed instant was null
     * @throws SvcParseException if <code>SolicitaResponse</code> node was not found
     */
    protected SolicitaResponse parseSolicitaResponse(SOAPElement element) {
        SOAPElement response = SOAPUtils.parseChild(element, BatchWriter.SOLICITA_RESPONSE); //throws SvcParseEx
        return new SolicitaResponse(
            parseInstant(response, BatchWriter.INSTANT),
            response.getAttributeValue(SolicitaSvc.STS_CODE),
            response.getAttributeValue(SolicitaSvc.MESSAGE),
            response.getAttributeValue(SolicitaSvc.REQUEST_ID));
    }
    
    /**
     * Returns a new <code>VerificaResponse</code> parsed from the specified element.
     * 
     * @param element <code>SOAPElement</code> to be parsed
     * @return a new <code>VerificaResponse</code> parsed from the specified element
     * @throws DateTimeParseException if unable to parse instant from element
     * @throws IllegalArgumentException if element is null or parsed instant was null
     * @throws SvcParseException if <code>VerificaResponse</code> node was not found
     */
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
    
    /**
     * Returns a new <code>DisposedResponse</code> parsed from the specified element.
     * 
     * @param element <code>SOAPElement</code> to be parsed
     * @return a new <code>DisposedResponse</code> parsed from the specified element
     * @throws DateTimeParseException if unable to parse instant from element
     * @throws IllegalArgumentException if element is null or parsed instant was null
     * @throws SvcParseException if <code>DescargaResponse</code> node was not found
     */
    protected DescargaResponse parseDescargaResponse(SOAPElement element) {
        SOAPElement response = SOAPUtils.parseChild(element, BatchWriter.DESCARGA_RESPONSE); //throws SvcParseEx
                
        return new DisposedResponse(
            parseInstant(response, BatchWriter.INSTANT),
            response.getAttributeValue(DescargaSvc.STS_CODE),
            response.getAttributeValue(DescargaSvc.MESSAGE),
            response.getAttributeValue(DescargaSvc.PACKAGE_ID),
            isDisposed(response));
    }
    
    /**
     * Returns an <code>Instant</code> parsed from the attribute value of an
     * attribute with the specified name from the specified <code>SOAPElement</code>,
     * or null if attribute was not found.
     * 
     * @param element element with an instant attribute
     * @param name name of the instant attribute
     * @return an <code>Instant</code> parsed from the attribute value of an
     *         attribute with the specified name from the specified <code>SOAPElement</code>,
     *         or null if attribute was not found
     * @throws DateTimeParseException if value of attribute's text cannot be parsed
     * @throws NullPointerException if element is null
     */
    protected Instant parseInstant(SOAPElement element, QName name) {
        String instant = element.getAttributeValue(name);
        return (instant == null ? null : Instant.parse(instant));
    }
    
    
    /**
     * Returns true if the specified response element contains a disposed
     * attribute with "true" as its value.
     * 
     * @param response element to scan
     * @return true if the specified response element contains a disposed
     *         attribute with "true" as its value
     * @throws NullPointerException if response is null
     */
    protected boolean isDisposed(SOAPElement response) {
        String disposedValue = response.getAttributeValue(BatchWriter.DISPOSED);
        return (disposedValue != null && disposedValue.equalsIgnoreCase("true"));
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Extends <code>DescargaResponse</code> to be able to be created with a
     * disposed state.
     */
    protected static class DisposedResponse extends DescargaResponse {
        private static final long serialVersionUID = 20250104L;
      
        /**
         * Creates a new <code>DisposedResponse</code> with the specified parameters.
         * 
         * @param satInstant instant the response was received from the web service
         * @param statusCode status code of the response
         * @param message web service's response message
         * @param packageId package identifier of this response
         * @param disposed  true if this response's encoded package was disposed
         * @throws IllegalArgumentException if satInstant is null
         */
        public DisposedResponse(Instant satInstant, String statusCode,
                String message, String packageId, boolean disposed) {
            super(satInstant, statusCode, message, packageId, disposed);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * This class implements a <code>BatchRequest</code> iterator, parsing
     * requests from a specified Node iterator.
     * <p>This iterator does not support removals.</p>
     */
    protected static class RequestsIterator implements Iterator<BatchRequest> {
        
        /**
         * Node iterator to iterate with.
         */
        protected Iterator<Node> iterator;
        
        /**
         * Creates a new <code>RequestIterator</code> using the specified
         * <code>Node</code> iterator.
         * 
         * @param iterator Node iterator
         * @throws NullPointerException if iterator is null
         */
        public RequestsIterator(Iterator<Node> iterator) {
            if (iterator == null) {
                throw new NullPointerException();
            }
            this.iterator = iterator;
        }
        
        /**
         * Returns true if this iterator has more elements.
         * 
         * @return true if this iterator has more elements
         */
        @Override public boolean hasNext() {
            return iterator.hasNext();
        }
        
        /**
         * Returns the next parsed <code>BatchRequest</code>. 
         * 
         * @return the next parsed <code>BatchRequest</code>
         * @throws NoSuchElementException if the iteration has no more elements
         * @throws SvcParseException if unable to parse next element
         */
        @Override public BatchRequest next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return parseRequest((SOAPElement)iterator.next());
        }
        
        /**
         * This method will throw an UnsupportedOperationException to avoid
         * removals through this iterator.
         * 
         * @throws UnsupportedOperationException if not supported
         */
        @Override public void remove() {
            throw new UnsupportedOperationException("can not delete requests through this iterator");
        }
        
        /**
         * Returns a <code>BatchRequest</code> parsed from the specified element.
         * 
         * @param element <code>SOAPElement</code> to parse
         * @return a <code>BatchRequest</code> parsed from the specified element
         * @throws IllegalArgumentException if element is null
         * @throws SvcParseException if unable to parse element to extract request.
         */
        protected BatchRequest parseRequest(SOAPElement element) {
            return new BatchRequest(
                SOAPUtils.parseLongAttributeValue(element, BatchWriter.BATCH_ID),
                element.getAttributeValue(BatchWriter.RFC),
                parseQuery(getQueryElement(element)));
        }
        
        /**
         * Returns the <code>SOAPElement</code> that contains the query of 
         * the request of a solicitude.
         * 
         * @param element the element to parse
         * @return the <code>SOAPElement</code> that contains the query of 
         *         the request of a solicitude
         * @throws IllegalArgumentException if element is null
         * @throws SvcParseException if the Node was not found
         */
        protected SOAPElement getQueryElement(SOAPElement element) {
            return SOAPUtils.parseGrandchild(
                    element,
                    SolicitaSvc.SOLICITA,
                    SolicitaSvc.SOLICITUD);
        }
       
        /**
         * Returns a <code>Query</code> parsed from the specified element.
         * 
         * @param solicitud element to be parsed
         * 
         * @return a <code>Query</code> parsed from the specified element
         * @throws NullPointerException if solicitud is null
         */
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
