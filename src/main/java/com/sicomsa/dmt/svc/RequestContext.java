/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import javax.xml.namespace.QName;

/**
 * Contains methods needed to implement the context of a download request
 * to the SAT web service.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.2
 * @since       1.5 
 */
public interface RequestContext {

    /**
     * Returns the name of this service. Should be unique.
     * 
     * @return the name of this service.
     */
    public String getServiceName();
    
    /**
     * Returns the location this service should connect to
     * <p>This location will be used to connect to the web service</p>
     * 
     * @return the location this service should connect to
     */
    public String getLocation();
    
    /**
     * Returns the SOAP action this service invokes.
     * <p>This action will be set in the headers of the <code>SOAPMessage</code>s
     * sent by this service.</p> 
     * 
     * @return the SOAP action this service invokes.
     */
    public String getSoapAction();
    
    /**
     * Returns the name of the parent node that contains the message request.
     * 
     * @return the name of the parent node that contains the message request
     */
    public QName getParentRequestName();
    
    /**
     * Returns the name of the child node that contains the message request.
     * 
     * @return the name of the child node that contains the message request
     */
    public QName getChildRequestName();
    
    /**
     * Returns the name of the response node that contains the message response.
     * 
     * @return the name of the response node that contains the message response
     */
    public QName getResponseName();
    
    /**
     * Returns the name of the result node that contains the message result.
     * 
     * @return the name of the result node that contains the message result
     */
    public QName getResultName();
}
