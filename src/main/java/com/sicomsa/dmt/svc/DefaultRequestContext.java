/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import javax.xml.namespace.QName;


/**
 * Concrete implementation of {@link RequestContext}.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.6
 * @since       1.5 
 */
public class DefaultRequestContext implements RequestContext {
    /**
     * Name of service in this context
     */
    protected String serviceName;
    
    /**
     * Location of service in this context
     */
    protected String location;
    
    /**
     * SOAPAction to performe in this context
     */
    protected String soapAction;
    
    /**
     * Name of parent node containing request
     */
    protected QName parentRequestName;
    
    /**
     * Name of child node containint request
     */
    protected QName childRequestName;
    
    /**
     * Name of parent node containint response
     */
    protected QName responseName;
    
    /**
     * Name of child node containing response
     */
    protected QName resultName;
    
    /**
     * Returns the name of this service. Should be unique.
     * 
     * @return the name of this service.
     */
    @Override public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Sets the name of the service of this context.
     * 
     * @param serviceName name of service of this context
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    /**
     * Returns the location this service should connect to
     * <p>This location will be used to connect to the web service</p>
     * 
     * @return the location this service should connect to
     */
    @Override public String getLocation() {
        return location;
    }
    
    /**
     * Sets the location of this request withing the web service.
     * 
     * @param location location of this request within the web service
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Returns the SOAP action this service invokes.
     * <p>This action will be set in the headers of the <code>SOAPMessage</code>s
     * sent by this service.</p> 
     * 
     * @return the SOAP action this service invokes.
     */
    @Override public String getSoapAction() {
        return soapAction;
    }
    
    /**
     * Sets the SOAP action within the web service for this request.
     * 
     * @param soapAction SOAP action within web service for this request
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }
    
    /**
     * Returns the name of the parent node that contains the message request.
     * 
     * @return the name of the parent node that contains the message request
     */
    @Override public QName getParentRequestName() {
        return parentRequestName;
    }
    
    /**
     * Sets element´s parent node name of this request
     * 
     * @param name element´s parent node name of this request
     */
    public void setParentRequestName(QName name) {
        this.parentRequestName = name;
    }
    
    /**
     * Returns the name of the child node that contains the message request.
     * 
     * @return the name of the child node that contains the message request
     */
    @Override public QName getChildRequestName() {
        return childRequestName;
    }
    
    /**
     * Sets the element´s child node name of this request
     * 
     * @param name element´s child node name of this request
     */
    public void setChildRequestName(QName name) {
        this.childRequestName = name;
    }
    /**
     * Returns the name of the parent node that contains the message response.
     * 
     * @return the name of the parent node that contains the message response
     */
    @Override public QName getResponseName() {
        return responseName;
    }
    
    /**
     * Sets the response's parent node name of this request.
     * 
     * @param name response's parent node name of this request
     */
    public void setResponseName(QName name) {
        this.responseName = name;
    }
    
    /**
     * Returns the name of the child node that contains the message result.
     * 
     * @return the name of the child node that contains the message result
     */
    @Override public QName getResultName() {
        return resultName;
    }
    
    /**
     * Sets the response's child node name of this request.
     * 
     * @param name response's child node name of this request
     */
    public void setResultName(QName name) {
        this.resultName = name;
    }
    
}
