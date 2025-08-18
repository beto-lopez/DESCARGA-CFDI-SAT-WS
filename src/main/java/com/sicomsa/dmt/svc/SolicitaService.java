/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.SvcParseException;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import java.time.Instant;
import javax.xml.namespace.QName;


/**
 * Concrete implementation that consumes SAT's download web service for
 * requesting downloads:<br>
 * "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"
 * 
 * <p>As of SAT's certificate download web service new version (1.5) 2025-05-30,
 * download requests are now split in these three types: download request for
 * emitted certificates; received certificates; and downlad request by id (folio).
 * This class extends {@link com.sicomsa.dmt.svc.SolicitaSvc} to adapt the service
 * to allow diferent types of requests and responses when requesting a download.
 * It uses a {@link com.sicomsa.dmt.svc.RequestContext} that provides the specific
 * information of the request to be made to the service.
 * </p>
 * <p>This class has concrete methods to add content of the download request (<code>Query</code>)
 * to the <code>SOAPMessage</code> to send, sign the message, and parse the
 * response from SAT in a {@link com.sicomsa.dmt.SolicitaResponse} instance which will
 * contain the relevant data of the response.</p>
 * <p>If <code>SolicitaResponse</code>'s method {@link com.sicomsa.dmt.SolicitaResponse#isAccept() isAccept()},
 * returns true will mean the request was accepted and we can now verify using
 * <code>SolicitaResponse</code>'s method {@link com.sicomsa.dmt.SolicitaResponse#getRequestId() getRequestId()}
 * which will give us the request id that should be used to verify the request.</p>
 * 
 * @see com.sicomsa.dmt.SolicitaResponse for more information about the response.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.3
 * @since       1.5 
 */
public class SolicitaService extends SolicitaSvc {
    
     /**
     * RFCSolicitante QName according to WSDL = "RfcSolicitante"
     */
    public static final QName RFC_SOLICITANTE = new QName("RfcSolicitante"); 
    
    /**
     * <code>RequestContext</code> used by this service
     */
    protected RequestContext requestCtx;
    
    /**
     * Creates a new <code>SolicitaService</code> with the specified parameters
     * 
     * @param context <code>SvcMessageFactory</code> to use
     * @param svc <code>RequestContext</code> to use
     */
    public SolicitaService(SvcMessageFactory context, RequestContext svc) {
        super(context);
        if (svc == null) {
            throw new IllegalArgumentException("invalid svc");
        }
        this.requestCtx = svc;
    }
    
    /**
     * Returns the <code>RequestContext</code> this service uses
     * 
     * @return the <code>RequestContext</code> this service uses
     */
    public RequestContext getRequestContext() {
        return requestCtx;
    }
  
    /**
     * Returns the name of this service. Should be unique.
     * 
     * @return the name of this service.
     */
    @Override public String getServiceName() {
        return requestCtx.getServiceName();
    }
    
    /**
     * Returns the location this service should connect to
     * <p>This location will be used to connect to the web service</p>
     * 
     * @return the location this service should connect to
     */
    @Override public String getLocation() {
        return requestCtx.getLocation();
    }
    
    /**
     * Returns the SOAP action this service invokes.
     * <p>This action will be set in the headers of the <code>SOAPMessage</code>s
     * sent by this service.</p> 
     * 
     * @return the SOAP action this service invokes.
     */
    @Override public String getSoapAction() {
        return requestCtx.getSoapAction();
    }
     
    /**
     * Adds attributes of Query and receptors to the <code>SOAPMessage</code>
     * received and returns the <code>SOAPElement</code> to be signed.
     * 
     * @param message the message to add content to
     * @param rfc the RFC of contributor making request
     * @param query the query with contributor´s request
     * @return the <code>SOAPElement</code> to be signed
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or query are null
     */
    @Override
    protected SOAPElement addContent(SOAPMessage message, String rfc, Query query) throws SOAPException {
        if (message == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return addSvcContent(message.getSOAPBody(), query);
    }
    
    /**
     * Adds attributes of Query and receptors to the <code>SOAPElement</code>
     * received and returns the <code>SOAPElement</code> to be signed.
     * 
     * @param parent the element to add query data to
     * @param query the query with the request data
     * @return the <code>SOAPElement</code> to be signed
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if parent or query are null
     */
    public SOAPElement addSvcContent(SOAPElement parent, Query query) throws SOAPException {
        if (parent == null || query == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        SOAPElement element =
                parent.addChildElement(requestCtx.getParentRequestName())
                      .addChildElement(requestCtx.getChildRequestName());
        addReceptores(element, query.getRfcReceptores());
        setAttributes(element, query);
        return element;
    }
    
    
    /**
     * Returns a <code>SolicitaResponseExtended</code> parsing the specified
     * <code>SOAPMessage</code> using the specified instant as the time the
     * response was received.
     * 
     * @param message to be parsed
     * @param instant instant to be used to set response time
     * @param request <code>Query</code> sent that originated the message
     * @return a <code>SolicitaResponse</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if message or instant are null
     * @throws SvcParseException if there were problems while parsing message
     */
    @Override
    public SolicitaResponse parseReceivedMessage(SOAPMessage message, Instant instant, Query request) throws SOAPException {
        if (message == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        return parseSvcResponse(message.getSOAPBody(), instant);
    }

    /**
     * Returns a <code>SolicitaResponseExtended</code> parsed from the specified
     * <code>SOAPElement</code>.
     * <p>The specified instant will be used to set the time the response was
     * received</p>
     * 
     * @param element element to be parsed
     * @param instant the response was received
     * @return <code>SolicitaResponseExtennded</code> parsed from the specified
     *         <code>SOAPElement</code>
     * @throws SOAPException if there were SOAP related problems
     * @throws IllegalArgumentException if element or instant are null
     * @throws SvcParseException if there were parsing related problems
     */
    public SolicitaResponse parseSvcResponse(SOAPElement element, Instant instant) throws SOAPException {
        if (element == null || instant == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        
        SOAPElement result = SOAPUtils.parseGrandchild(
                element,
                requestCtx.getResponseName(),
                requestCtx.getResultName());
        
        return getResponse(instant, result);
    }
    
    /**
     * Returns the <code>SolicitaResponseExtended</code> from the element provided.
     * 
     * @param instant the response was received
     * @param result a <code>SolicitaResponse</code>
     * @return <code>SolicitaResponseExtended</code> from the element provided
     */
    protected SolicitaResponseExtended getResponse(Instant instant, SOAPElement result) {
        
        SolicitaResponseExtended response = 
                new SolicitaResponseExtended(
                    instant,
                    result.getAttributeValue(STS_CODE),
                    result.getAttributeValue(MESSAGE),
                    result.getAttributeValue(REQUEST_ID)); //retorna nulo si no viene dicho atr
        response.setRfcSolicitante(result.getAttributeValue(RFC_SOLICITANTE));
        return response;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * <code>SolicitaResponseExtended</code> is SAT's response from a download request
     * to the massive download web service when requesting a download for the first
     * time of a particular query, including the RFC of the requesting party as
     * of version 1.5 released by SAT on 2025-05-30.
     * 
     * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
     * 
     * @version 2025.06.10
     * @since 1.5
     */
    public static class SolicitaResponseExtended extends SolicitaResponse {
    
        private static final long serialVersionUID = 20250610L;
    
        /**
         * RFC of certificate's requestor
         */
        protected String rfcSolicitante;
        
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
        public SolicitaResponseExtended(Instant satInstant, String statusCode, String message, String requestId) {
            super(satInstant, statusCode, message, requestId);
        }
        
        /**
         * Returns the RFC of the certificate applicant
         * 
         * @return the RFC of the certificate applicant
        */
        public String getRfcSolicitante() {
            return rfcSolicitante;
        }
    
        /**
         * Sets the RFC of the certificate applicant
         * 
         * @param rfc RFC of applicant
         */
        public void setRfcSolicitante(String rfc) {
            this.rfcSolicitante = rfc;
        }
    
        /**
         * Returns a string representation of this response
         * 
         * @return a string representation of this response
         */
        @Override public String toString() {
            return new StringBuilder("SolicitaResponseExtended{")
                    .append("instant=").append(satInstant)
                    .append(",statusCode=").append(statusCode)
                    .append(",message=").append(message)
                    .append(",requestId=").append(requestId)
                    .append(",rfcSolicitante=").append(rfcSolicitante)
                    .append("}")
                    .toString();
        }
    }
    
}
