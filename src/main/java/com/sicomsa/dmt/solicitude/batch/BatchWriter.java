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
 * <code>BatchWriter</code> formats a file with a <code>SOAPMessage</code> containing
 * the requests included in a <code>Batch</code> object, and adds the responses
 * received from the web service to the message that is then updated in this
 * writer's assigned file.
 * <p>This writer and its file should be accessed by a single thread.</p>
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.12.27
 * @since 1.0
 * 
 *  
 */
public class BatchWriter implements AutoCloseable {
    
    /**
     * URI of this writer's message Nodes
     */
    public static final String URI = "com.sicomsa.dmt/Batch/2024/12/27";
    
    /**
     * Prefix of this writer's message Nodes
     */
    public static final String PREFIX = "BATCH";
    
    /**
     * Name of this version Node
     */
    public static final QName VERSION = new QName(URI, "Version", PREFIX);
    
    /**
     * Name of parent request Nodes
     */
    public static final QName REQUESTS = new QName(URI, "Requests", PREFIX);
    
    /**
     * Name of child request Nodes
     */
    public static final QName REQUEST  = new QName(URI, "Request", PREFIX);
    
    /**
     * Name of response Nodes
     */
    public static final QName RESPONSE = new QName(URI, "Response", PREFIX);
    
    /**
     * Attribute name of batch identifier
     */
    public static final QName BATCH_ID = new QName("BatchId");
    
    /**
     * Attribute name of instant
     */
    public static final QName INSTANT  = new QName("Instant");
    
    /**
     * <code>SolicitaResponse</code>'s Node name.
     */
    public static final QName SOLICITA_RESPONSE  = new QName("SolicitaResponse");
    
    /**
     * <code>VerificaResponse</code>'s Node name.
     */
    public static final QName VERIFICA_RESPONSE  = new QName("VerificaResponse");
    
    /**
     * <code>DescargaResponse</code>'s Node name.
     */
    public static final QName DESCARGA_RESPONSE  = new QName("DescargaResponse");
    
    /**
     * Attribute name of RFC
     */
    public static final QName RFC  = new QName("RFC");
    
    /**
     * Attribute name of status
     */
    public static final QName STATUS  = new QName("Status");
    
    /**
     * Attribute name of type
     */
    public static final QName TYPE  = new QName("Type");
    
    /**
     * Attribute name of disposed
     */
    public static final QName DISPOSED  = new QName("Disposed");
    
    /**
     * The <code>RandomAccessFile</code> to write to
     */
    protected RandomAccessFile raf;
    
    /**
     * The <code>SOAPWriter</code> that will do the writing
     */
    private SOAPWriter _writer;
    
    /**
     * True if this writer is closed
     */
    protected boolean closed = false;
    
    /**
     * Version of this writer
     */
    protected String version = "1.0";
    
    /**
     * Not used 
     */
    @Deprecated protected boolean formatted = false;
    
    /**
     * Text warning to add in the xml file
     */
    protected String warning = "WARNING: do not update this file outside the application in order to preserve consistency.";
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a new <code>BatchWriter</code> that will write to the specified file.
     * 
     * @param file file to write to
     * @throws FileNotFoundException  if the given file object does not
     *      denote an existing regular file, or the given file object does not
     *      denote an existing, writable regular file and a new regular file of
     *      that name cannot be created, or if some other error occurs while
     *      opening or creating the file
     */
    public BatchWriter(File file) throws FileNotFoundException {
        raf = new RandomAccessFile(file, "rws");
    }
    
    /**
     * Returns true if this writer is closed.
     * 
     * @return true if this writer is closed
     */
    public boolean isClosed() {
        return closed;
    }
    
    /**
     * Closes this writer and its file.
     * 
     * @throws IOException if an I/O error occurs
     */
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
     * Formats the file of this <code>BatchWriter</code> with a <code>SOAPMessage</code>
     * prototype including the requests of each <code>BatchSolicitude</code> the
     * specified iterator provides.
     * 
     * @param iterator iterator of solicitudes
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
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
     * Adds a new element with contents of the specified download event to the
     * <code>SOAPMessage</code> this writer keeps updated in this writer's file.
     * 
     * @param event event with information to add
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if event is null
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
     * Adds a new comment element with the specified text to the <code>SOAPMessage</code>
     * this writer keeps updated in this writer's file.
     * 
     * @param text text of the comment to add
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     */
    public void writeComment(String text) throws SOAPException, IOException { 
        checkNotClosed();
        getWriter().writeComment(text);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a new message prototype.
     * 
     * @return a new message prototype
     * @throws SOAPException if there were any SOAP problems.
     */
    protected SOAPMessage newMessagePrototype() throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX, URI);
        envelope.addNamespaceDeclaration(SolicitaSvc.DMT_PREFIX, SolicitaSvc.DMT_URI);
        return message;
    }
    
    /**
     * Returns the <code>_writer</code> property that references the <code>SOAPWriter</code>
     * used in this <code>BatchWriter</code>.
     * <p>This method will create and initialize a new <code>SOAPWriter</code> if
     * it has not yet been created.</p>
     * 
     * @return <code>SOAPWriter</code>  of this writer
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     */
    protected SOAPWriter getWriter() throws SOAPException, IOException {
        if (_writer == null && !closed) { //if already closed dont reopen
            _writer = new SOAPWriter(newMessagePrototype());
            _writer.init();
        }
        return _writer;
    }
    
    /**
     * Adds a comment element with the specified text, to the specified element.
     * 
     * @param element element to add comment to
     * @param text text of the comment to add
     * @throws NullPointerException if element is null
     */
    protected void addComment(SOAPElement element, String text) {
        element.appendChild(
                element.getOwnerDocument().createComment(text));
    }
    
    /**
     * Validates the writer is not closed.
     * 
     * @throws IOException if the writer is closed
     */
    protected void checkNotClosed() throws IOException {
        if (closed) {
            throw new IOException("closed writer");
        }
    }
    
    /**
     * Adds a new <code>SOAPElement</code> named {@link BatchWriter#REQUESTS}
     * to the specified element.
     * <p>The added element will contain a request element for each for each
     * solicitude provided by the specified iterator.</p>
     * 
     * @param element element to add requests element
     * @param iterator solicitude's iterator
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if iterator is null
     */
    protected void addRequests(SOAPElement element, Iterator<BatchSolicitude> iterator) throws SOAPException {
        SOAPElement requests = element.addChildElement(REQUESTS);
        while (iterator.hasNext()) {
            addRequest(requests, iterator.next());
        }
    }
    
    /**
     * Adds a new <code>SOAPElement</code> with information of the specified
     * solicitude to the specified parent element.
     * 
     * @param parent element to add to
     * @param solicitude solicitude with information to add
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if parent or solicitude are null
     */
    protected void addRequest(SOAPElement parent, BatchSolicitude solicitude) throws SOAPException {
        SOAPElement request = parent.addChildElement(REQUEST)
                .addAttribute(BATCH_ID, Long.toString(solicitude.getBatchId()))
                .addAttribute(RFC, solicitude.getClient().getRfc());
        SolicitaSvc.addContent(request, solicitude.getQuery());
    }
    
    /**
     * Adds the contents of the specified download event and its response to the
     * <code>SOAPMessage</code> containing the <code>SOAPWriter</code>.
     * 
     * @param event event to add to the stream
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if event is null
     */
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

    /**
     * Adds the specified request download response to the specified element.
     * 
     * @param element element to append to
     * @param response response to append
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if element is null
     */
    protected void addResponse(SOAPElement element, SolicitaResponse response) throws SOAPException {
        SOAPElement result = element.addChildElement(SOLICITA_RESPONSE)
                .addAttribute(INSTANT, response.getInstant().toString());
        addAttribute(result, SolicitaSvc.STS_CODE, response.getStatusCode());
        addAttribute(result, SolicitaSvc.MESSAGE, response.getMessage());
        addAttribute(result, SolicitaSvc.REQUEST_ID, response.getRequestId());
    }
    
    /**
     * Adds the specified verify request response to the specified element.
     * 
     * @param element element to append to
     * @param response response to append
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if element is null
     */
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
    
    /**
     * Adds the specified download package response to the specified element.
     * 
     * @param element element to append to
     * @param response response to append
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if element is null
     */
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
    
    /**
     * Adds the specified (name,value) attribute to the specified <code>SOAPElement</code>
     * if the specified value is not null.
     * 
     * @param e  element to add the attribute to
     * @param name name of the attribute to add
     * @param value value of the attribute to add
     * @throws SOAPException if there were any SOAP problems.
     * @throws NullPointerException if e is null
     */
    protected void addAttribute(SOAPElement e, QName name, String value) throws SOAPException {
        if (value != null) {
            e.addAttribute(name, value);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * <code>SOAPWriter</code> uses a <code>SOAPMessage</code> prototype to add
     * temporary content to its <code>SOAPBodyElement</code> using the
     * {@link SOAPWriter#addChildElement(javax.xml.namespace.QName) } method
     * which adds elements to the body of this message tat can be appended to
     * the <code>BatchWriter</code>'s file using the {@link SOAPWriter#writeAppendedElement() }
     * method.
     * <p>This class that can also be seen as a stream, updates and appends
     * conten to this <code>BatchWriter</code>'s file by directly accessing
     * this <code>BatchWriter</code>'s {@link java.io.RandomAccessFile}.</p>
     * <p>Note: after creating this stream and befora accessing any of its methods
     * it is required to call its {@link SOAPWriter#init() } method which
     * initializes certain critical properties.</p>
     *  
     * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
     * @version 2024.12.27
     * @since 1.0
     *  
     */
    protected class SOAPWriter extends ByteArrayOutputStream {
        
        /**
         * Tag name
         */
        static final String TAG = "BodyContent";
        
        /**
         * Message property. 
         */
        protected SOAPMessage message; //prototype
        
        /**
         * The body property of this stream. It is actually the body part
         * of the message property of this stream.
         */
        protected SOAPElement body;
        
        /**
         * The header size of the message property
         */
        protected int headerSize;
        
        /**
         * The tail size of the message property
         */
        protected int tailSize;
        
        /**
         * Creates a new <code>SOAPWriter</code> with the specified <code>SOAPMessage</code> 
         * as its message prototype.
         * 
         * @param message message prototype
         * @throws NullPointerException if message is null
         */
        protected SOAPWriter(SOAPMessage message) {
            if (message == null) {
                throw new NullPointerException("requires message prototype");
            }
            this.message = message;
        }
        
        /**
         * Method that should be called after creating the <code>SOAPWriter</code> 
         * in order to initialize some of its properties.
         * 
         * @throws SOAPException if there were any SOAP problems.
         * @throws IOException if an I/O error occurs
         */
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
        
        /**
         * Returns a string tag used to locate the body content within a
         * <code>SOAPMessage</code>.
         * 
         * @return the tag
         */
        protected String buildTag() {
            return new StringBuilder("<")
                    .append(PREFIX).append(":").append(TAG).append("/>")
                    .toString();
        }
        
        /**
         * Resets the file contents of this <code>BatchWriter</code> to contain
         * only the specified <code>SOAPMessage</code>.
         * 
         * @param message <code>SOAPMessage</code> to write
         * @throws SOAPException if there were any SOAP problems.
         * @throws IOException if an I/O error occurs
         */
        public void write(SOAPMessage message) throws SOAPException, IOException {
            writeToBuffer(message);
            raf.seek(0);
            raf.write(buf, 0, count);
            raf.setLength(size());
        }
        
        /**
         * Adds a new <code>SOAPElement</code> with the specifiead name to the
         * body property of this stream and returns it.
         * 
         * @param qname the name of the element to add
         * @return the <code>SOAPElement</code> that was created and added
         * @throws SOAPException if there were any SOAP problems.
         */
        public SOAPElement addChildElement(QName qname) throws SOAPException {
            return body.addChildElement(qname);
        }
        
        /**
         * Adds a comment element with the specified text to the body propery
         * of this stream, and appends the body content to this <code>BatchWriter</code>'s
         * file, removing the body contents afterwards.
         * 
         * @param text text of comment to write
         * @throws SOAPException if there were any SOAP problems.
         * @throws IOException if an I/O error occurs
         */
        public void writeComment(String text) throws SOAPException, IOException {
            addComment(body, text);
            writeAppendedElement();
        }
        
        /**
         * Appends the content of the body property of this stream to the end
         * of this <code>BatchWriter</code>'s file, removing the body contents afterwards.
         * 
         * @throws SOAPException if there were any SOAP problems.
         * @throws IOException if an I/O error occurs
         */
        public void writeAppendedElement() throws SOAPException, IOException {
            writeToBuffer(this.message);
            raf.seek(raf.length() - tailSize);
            raf.write(buf, headerSize, size() - headerSize);
            body.removeContents();
        }
        
        /**
         * Calls the {@link java.io.ByteArrayOutputStream#reset() } method of this
         * {@link java.io.BufferedOutputStream} and writes the specified message
         * to this stream.
         * 
         * @param message message to write to this stream
         * @throws SOAPException if there were any SOAP problems.
         * @throws IOException if an I/O error occurs
         */
        protected void writeToBuffer(SOAPMessage message) throws SOAPException, IOException {
            reset();
            message.writeTo(this);
        }
        
    }
    ////////////////////////////////////////////////////////////////////////////
  
}
