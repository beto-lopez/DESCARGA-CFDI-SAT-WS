/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <code>DefaultRequestContext</code> class test.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * 
 * @version     2025.6.10
 * @since       1.5 
 */
public class DefaultRequestContextTest {
    
    ///All of the following static parameters are for "test only" purposes. They are not precise.
    public static final String SERVICE_NAME = "SolicitaDescargaEmitidos";
    public static final String SOAP_ACTION = "SolicitaDescargaService.svc";
    public static final String LOCATION = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaEmitidos";
    public static final QName PARENT_REQUEST = new QName("SolicitaDescargaEmitidos");
    public static final QName CHILD_REQUEST = new QName("solicitud");
    public static final QName PARENT_RESPONSE = new QName("SolicitaDescargaEmitidosResponse");
    public static final QName CHILD_RESPONSE = new QName("SolicitaDescargaEmitidosResult");
    
    protected Ctx context;
    
    public DefaultRequestContextTest() {
        context = new Ctx();
    }
    
    public RequestContext getDefaultTestContext() {
        return context;
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getServiceName method, of class DefaultRequestContext.
     */
    @Test
    public void testGetServiceName() {
        System.out.println("getServiceName");
        DefaultRequestContext instance = context;
        String expResult = SERVICE_NAME;
        String result = instance.getServiceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setServiceName method, of class DefaultRequestContext.
     */
    @Test
    public void testSetServiceName() {
        System.out.println("setServiceName");
        String serviceName = "@serviceName";
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setServiceName(serviceName);
        assertEquals(serviceName, instance.serviceName);
        assertEquals(serviceName, instance.getServiceName());
    }

    /**
     * Test of getLocation method, of class DefaultRequestContext.
     */
    @Test
    public void testGetLocation() {
        System.out.println("getLocation");
        DefaultRequestContext instance = context;
        String expResult = LOCATION;
        String result = instance.getLocation();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLocation method, of class DefaultRequestContext.
     */
    @Test
    public void testSetLocation() {
        System.out.println("setLocation");
        String location = "@location";
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setLocation(location);
        assertEquals(location, instance.location);
        assertEquals(location, instance.getLocation());
    }

    /**
     * Test of getSoapAction method, of class DefaultRequestContext.
     */
    @Test
    public void testGetSoapAction() {
        System.out.println("getSoapAction");
        DefaultRequestContext instance = context;
        String expResult = SOAP_ACTION;
        String result = instance.getSoapAction();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSoapAction method, of class DefaultRequestContext.
     */
    @Test
    public void testSetSoapAction() {
        System.out.println("setSoapAction");
        String soapAction = "@soapAction";
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setSoapAction(soapAction);
        assertEquals(soapAction, instance.soapAction);
        assertEquals(soapAction, instance.getSoapAction());
    }

    /**
     * Test of getParentRequestName method, of class DefaultRequestContext.
     */
    @Test
    public void testGetParentRequestName() {
        System.out.println("getParentRequestName");
        DefaultRequestContext instance = context;
        QName expResult = PARENT_REQUEST;
        QName result = instance.getParentRequestName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setParentRequestName method, of class DefaultRequestContext.
     */
    @Test
    public void testSetParentRequestName() {
        System.out.println("setParentRequestName");
        QName name = new QName("parent request");
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setParentRequestName(name);
        assertEquals(name, instance.parentRequestName);
        assertEquals(name, instance.getParentRequestName());
    }

    /**
     * Test of getChildRequestName method, of class DefaultRequestContext.
     */
    @Test
    public void testGetChildRequestName() {
        System.out.println("getChildRequestName");
        DefaultRequestContext instance = context;
        QName expResult = CHILD_REQUEST;
        QName result = instance.getChildRequestName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setChildRequestName method, of class DefaultRequestContext.
     */
    @Test
    public void testSetChildRequestName() {
        System.out.println("setChildRequestName");
        QName name = new QName("child request name");
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setChildRequestName(name);
        assertEquals(name, instance.childRequestName);
        assertEquals(name, instance.getChildRequestName());
    }

    /**
     * Test of getResponseName method, of class DefaultRequestContext.
     */
    @Test
    public void testGetResponseName() {
        System.out.println("getResponseName");
        DefaultRequestContext instance = context;
        QName expResult = PARENT_RESPONSE;
        QName result = instance.getResponseName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResponseName method, of class DefaultRequestContext.
     */
    @Test
    public void testSetResponseName() {
        System.out.println("setResponseName");
        QName name = new QName("response name ");
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setResponseName(name);
        assertEquals(name, instance.responseName);
        assertEquals(name, instance.getResponseName());
    }

    /**
     * Test of getResultName method, of class DefaultRequestContext.
     */
    @Test
    public void testGetResultName() {
        System.out.println("getResultName");
        DefaultRequestContext instance = context;
        QName expResult = CHILD_RESPONSE;
        QName result = instance.getResultName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResultName method, of class DefaultRequestContext.
     */
    @Test
    public void testSetResultName() {
        System.out.println("setResultName");
        QName name = new QName("result name ");
        DefaultRequestContext instance = new DefaultRequestContext();
        instance.setResultName(name);
        assertEquals(name, instance.resultName);
        assertEquals(name, instance.getResultName());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Class to test the getters.
     */
    protected static class Ctx extends DefaultRequestContext {
        public Ctx() {
            this.serviceName = SERVICE_NAME;
            this.location = LOCATION;
            this.soapAction = SOAP_ACTION;
            this.parentRequestName = PARENT_REQUEST;
            this.childRequestName = CHILD_REQUEST;
            this.responseName = PARENT_RESPONSE;
            this.resultName = CHILD_RESPONSE;
        }
    }
    
}
