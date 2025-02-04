/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;


import com.sicomsa.dmt.PackageIds;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.SatResponse;
import com.sicomsa.dmt.solicitude.DownloadEvent;

import com.sicomsa.dmt.svc.SolicitaSvc;
import com.sicomsa.dmt.svc.VerificaSvc;
import com.sicomsa.dmt.svc.DescargaSvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import java.util.Iterator;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.12.27
 * 
 * Creates or resets a file, writing download requests in a SOAPEnvelope format.
 * 
 * After formatting the file with batch solicitudes through the formatFile
 * method you can add SAT responses and/or comments to the envelope (file).
 *  
 */
public class BatchWriter implements AutoCloseable {
    
    public static final String URI = "com.sicomsa.dmt/Batch/2024/12/27";
    public static final String PREFIX = "BATCH";
    
    public static final QName VERSION = new QName(URI, "Version", PREFIX);
    
    public static final QName REQUESTS = new QName(URI, "Requests", PREFIX);
    public static final QName REQUEST  = new QName(URI, "Request", PREFIX);
    
    public static final QName RESPONSE = new QName(URI, "Response", PREFIX);
    
    public static final QName BATCH_ID = new QName("BatchId");
    
    public static final QName INSTANT  = new QName("Instant");
    public static final QName SOLICITA_RESPONSE  = new QName("SolicitaResponse");
    public static final QName VERIFICA_RESPONSE  = new QName("VerificaResponse");
    public static final QName DESCARGA_RESPONSE  = new QName("DescargaResponse");
    
    public static final QName RFC  = new QName("RFC");
    public static final QName STATUS  = new QName("Status");
    public static final QName TYPE  = new QName("Type");
    public static final QName DISPOSED  = new QName("Disposed");
    
    protected RandomAccessFile raf;
    private SOAPWriter _writer;
    protected boolean closed = false;
    protected String version = "1.0";
    protected boolean formatted = false;
    protected String warning = "WARNING: do not update this file outside the application in order to preserve consistency.";
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param file
     * @throws FileNotFoundException  - if the given file object does not
     *      denote an existing regular file, or the given file object does not
     *      denote an existing, writable regular file and a new regular file of
     *      that name cannot be created, or if some other error occurs while
     *      opening or creating the file
     */
    public BatchWriter(File file) throws FileNotFoundException {
        raf = new RandomAccessFile(file, "rws");
    }
    
    public boolean isClosed() {
        return closed;
    }
    
    @Override public void close() throws IOException {
        //closing a ByteArrayOutputStream has no effect.
        //but release buf
        _writer = null;
        closed = true;
        if (raf != null) {
            raf.close();
            raf = null;
        }
    }
    
    /**
     * 
     * this method also formats the file.
     * @param iterator
     * @throws IOException
     * @throws SOAPException 
     * @throws NullPointerException if iterator is null
     */
    public void formatFile(Iterator<BatchSolicitude> iterator) throws IOException, SOAPException {
        if (iterator == null) {
            throw new NullPointerException("invalid iterator");
        }
        checkNotClosed(); //throws IOEx
        SOAPMessage message = newMessagePrototype(); //throws SOAPEx
        addComment(message.getSOAPHeader(), warning);
        addRequests(message.getSOAPHeader(), iterator);
        message.getSOAPBody().addChildElement(VERSION).addTextNode(version); ///needed here to open body element
        getWriter().write(message);
    }
    
       
    /**
     * file must be formated before writing responses.
     * @param event
     * @throws SOAPException
     * @throws IOException 
     */
    public void writeResponse(DownloadEvent event) throws SOAPException, IOException {
        if (event == null) {
            throw new IllegalArgumentException("null event");
        }
        checkNotClosed(); //throws IOEx
        appendElement(event);
        getWriter().writeAppendedElement();
    }
    
    /**
     * file must be formated before writing comments
     * @param text
     * @throws SOAPException
     * @throws IOException 
     */
    public void writeComment(String text) throws SOAPException, IOException { 
        checkNotClosed();
        getWriter().writeComment(text);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    protected SOAPMessage newMessagePrototype() throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX, URI);
        envelope.addNamespaceDeclaration(SolicitaSvc.DMT_PREFIX, SolicitaSvc.DMT_URI);
        return message;
    }
    
    protected SOAPWriter getWriter() throws SOAPException, IOException {
        if (_writer == null && !closed) { //if already closed dont reopen
            _writer = new SOAPWriter(newMessagePrototype());
            _writer.init();
        }
        return _writer;
    }
    
    protected void addComment(SOAPElement element, String text) {
        element.appendChild(
                element.getOwnerDocument().createComment(text));
    }
    
    protected void checkNotClosed() throws IOException {
        if (closed) {
            throw new IOException("closed writer");
        }
    }
    
    
    protected void addRequests(SOAPElement element, Iterator<BatchSolicitude> iterator) throws SOAPException {
        SOAPElement requests = element.addChildElement(REQUESTS);
        while (iterator.hasNext()) {
            addRequest(requests, iterator.next());
        }
    }
    
    protected void addRequest(SOAPElement parent, BatchSolicitude solicitude) throws SOAPException {
        SOAPElement request = parent.addChildElement(REQUEST)
                .addAttribute(BATCH_ID, Long.toString(solicitude.getBatchId()))
                .addAttribute(RFC, solicitude.getClient().getRfc());
        SolicitaSvc.addContent(request, solicitude.getQuery());
    }
    
    protected void appendElement(DownloadEvent event) throws SOAPException, IOException {
        SatResponse response = event.getResponse();
        if (response == null) {
            throw new NullPointerException("null response");
        }
        SOAPElement responseWrapper = getWriter().addChildElement(RESPONSE);
        String type = "unknown";
        if (response instanceof SolicitaResponse sr) {
            type = "SolicitaResponse";
            addResponse(responseWrapper, sr);
        }
        else if (response instanceof VerificaResponse vr) {
            type = "VerificaResponse";
            addResponse(responseWrapper, vr);
        }
        else if (response instanceof DescargaResponse dr) {
            type = "DescargaResponse";
            addResponse(responseWrapper, dr);
        }
        BatchSolicitude solicitude = (BatchSolicitude)event.getSolicitude();
        responseWrapper.addAttribute(BATCH_ID, Long.toString(solicitude.getBatchId()))
                .addAttribute(TYPE, type)
                .addAttribute(STATUS, event.getResult().toString()); //EventName());
        if (solicitude.isReject()) {
            addComment(responseWrapper, "DONE STATE");
        }
        else if (solicitude.isDownloadDone()) {
            addComment(responseWrapper, "DOWNLOAD DONE");
        }
    }

    protected void addResponse(SOAPElement element, SolicitaResponse response) throws SOAPException {
        SOAPElement result = element.addChildElement(SOLICITA_RESPONSE)
                .addAttribute(INSTANT, response.getInstant().toString());
        addAttribute(result, SolicitaSvc.STS_CODE, response.getStatusCode());
        addAttribute(result, SolicitaSvc.MESSAGE, response.getMessage());
        addAttribute(result, SolicitaSvc.REQUEST_ID, response.getRequestId());
    }
    
    protected void addResponse(SOAPElement element, VerificaResponse response) throws SOAPException {
        SOAPElement result = element.addChildElement(VERIFICA_RESPONSE)
                .addAttribute(INSTANT, response.getInstant().toString())
                .addAttribute(VerificaSvc.STATE_NAME, Integer.toString(response.getSolicitudeState()))
                .addAttribute(VerificaSvc.CFDIS_NAME, Integer.toString(response.getCfdis()));
        addAttribute(result, VerificaSvc.STS_CODE, response.getStatusCode());
        addAttribute(result, VerificaSvc.MESSAGE, response.getMessage());
        addAttribute(result, VerificaSvc.SOLICITUDE_STS_CODE_NAME, response.getSolicitudeStsCode());
        addAttribute(result, VerificaSvc.ID_NAME, response.getRequestId());
        if (response.hasPackagesInfo()) {
            PackageIds ids = response.getPackageIds();
            for (int idx = 0; idx < ids.size(); idx++) {
                result.addChildElement(VerificaSvc.PACKAGE_ID_NAME).setTextContent(ids.getPackageId(idx));
            }
        }
    }
    
    protected void addResponse(SOAPElement element, DescargaResponse response) throws SOAPException {
        SOAPElement result = element.addChildElement(DESCARGA_RESPONSE)
                .addAttribute(INSTANT, response.getInstant().toString());

        addAttribute(result, DescargaSvc.STS_CODE, response.getStatusCode());
        addAttribute(result, DescargaSvc.MESSAGE, response.getMessage());
        addAttribute(result, DescargaSvc.PACKAGE_ID, response.getPackageId());

        if (response.isAccept()) {
            result.addAttribute(DISPOSED, "true");
        }
    }
    
    protected void addAttribute(SOAPElement e, QName name, String value) throws SOAPException {
        if (value != null) {
            e.addAttribute(name, value);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * method init() should be called before any other method public method.
     */
    protected class SOAPWriter extends ByteArrayOutputStream {
        static final String TAG = "BodyContent";
        protected SOAPMessage message; //prototype
        protected SOAPElement body;
        protected int headerSize;
        protected int tailSize;
        
        protected SOAPWriter(SOAPMessage message) throws SOAPException {
            if (message == null) {
                throw new NullPointerException("requires message prototype");
            }
            this.message = message;
        }
        
        protected void init() throws SOAPException, IOException {
            body = message.getSOAPBody();
            body.addChildElement(TAG, PREFIX);
            writeToBuffer(message);
            String string = new String(toByteArray(), "UTF-8");
            String tag = buildTag();
            headerSize = string.indexOf(tag);
            tailSize = string.length() - headerSize - tag.length();
            body.removeContents();
        }
        
        protected String buildTag() {
            return new StringBuilder("<")
                    .append(PREFIX).append(":").append(TAG).append("/>")
                    .toString();
        }
        
        public void write(SOAPMessage message) throws SOAPException, IOException {
            writeToBuffer(message);
            raf.seek(0);
            raf.write(buf, 0, count);
            raf.setLength(size());
        }
        
        public SOAPElement addChildElement(QName qname) throws SOAPException {
            return body.addChildElement(qname);
        }
        
        public void writeComment(String text) throws SOAPException, IOException {
            addComment(body, text);
            writeAppendedElement();
        }
        
        /**
         * 
         * @throws SOAPException
         * @throws IOException 
         */
        public void writeAppendedElement() throws SOAPException, IOException {
            writeToBuffer(this.message);
            raf.seek(raf.length() - tailSize);
            raf.write(buf, headerSize, size() - headerSize);
            body.removeContents();
        }
        
        protected void writeToBuffer(SOAPMessage message) throws SOAPException, IOException {
            reset();
            message.writeTo(this);
        }
        
    }
    ////////////////////////////////////////////////////////////////////////////
  
}
