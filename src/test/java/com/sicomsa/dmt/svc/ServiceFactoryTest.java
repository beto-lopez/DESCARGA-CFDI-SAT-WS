/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;


/**
 * <code>ServiceFactory</code> class test.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version     2025.06.03
 * @since       1.5
 */
public class ServiceFactoryTest {
    protected ServiceFactory cfdiFactory;
    protected ServiceFactory retFactory;
    
    public ServiceFactoryTest() {
        cfdiFactory = ServiceFactory.newCfdiServiceFactory();
        retFactory = ServiceFactory.newRetencionesServiceFactory();
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
     * Test of newCfdiServiceFactory method, of class ServiceFactory.
     */
    @Test
    public void testNewCfdiServiceFactory() {
        System.out.println("newCfdiServiceFactory");
        ServiceFactory result = ServiceFactory.newCfdiServiceFactory();
        assertNotNull(result);
        assertFalse(result.isRetenciones());
    }

    /**
     * Test of newRetencionesServiceFactory method, of class ServiceFactory.
     */
    @Test
    public void testNewRetencionesServiceFactory() {
        System.out.println("newRetencionesServiceFactory");
        ServiceFactory result = ServiceFactory.newRetencionesServiceFactory();
        assertNotNull(result);
        assertTrue(result.isRetenciones());
    }

    /**
     * Test of isRetenciones method, of class ServiceFactory.
     */
    @Test
    public void testIsRetenciones() {
        System.out.println("isRetenciones");
        testIsRetenciones(ServiceFactory.newCfdiServiceFactory());
        testIsRetenciones(ServiceFactory.newRetencionesServiceFactory());
    }
    
    protected void testIsRetenciones(ServiceFactory instance) {
        boolean expResult = instance.retenciones;
        boolean result = instance.isRetenciones();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSecurity method, of class ServiceFactory.
     */
    @Test
    public void testGetSecurity() {
        System.out.println("getSecurity");
        testGetSecurity(ServiceFactory.newCfdiServiceFactory());
        testGetSecurity(ServiceFactory.newRetencionesServiceFactory());
    }
    
    protected void testGetSecurity(ServiceFactory instance) {
        OasisSecurity expResult = instance.osecurity;
        OasisSecurity result = instance.getSecurity();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMessageFactory method, of class ServiceFactory.
     */
    @Test
    public void testGetMessageFactory() {
        System.out.println("getMessageFactory");
        testGetMessageFactory(ServiceFactory.newCfdiServiceFactory());
        testGetMessageFactory(ServiceFactory.newRetencionesServiceFactory());
    }
    
    protected void testGetMessageFactory(ServiceFactory instance) {
        SvcMessageFactory expResult = instance.messageFactory;
        SvcMessageFactory result = instance.getMessageFactory();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSecurity method, of class ServiceFactory.
     */
    @Test
    public void testSetSecurity() {
        System.out.println("setSecurity");
        OasisSecurity security = new OasisSecurity();
        testSetSecurity(security, ServiceFactory.newCfdiServiceFactory());
        testSetSecurity(security, ServiceFactory.newRetencionesServiceFactory());
    }
    
    protected void testSetSecurity(OasisSecurity security, ServiceFactory instance) {
        instance.setSecurity(security);
        assertEquals(security, instance.osecurity);
    }

    /**
     * Test of setMessageFactory method, of class ServiceFactory.
     */
    @Test
    public void testSetMessageFactory() {
        System.out.println("setMessageFactory");
        SvcMessageFactory messageFactory = DefaultMessageFactory.newInstance();
        testSetMessageFactory(messageFactory, ServiceFactory.newCfdiServiceFactory());
        testSetMessageFactory(messageFactory, ServiceFactory.newRetencionesServiceFactory());
    }
    
    protected void testSetMessageFactory(SvcMessageFactory messageFactory, ServiceFactory instance) {
        instance.setMessageFactory(messageFactory);
        assertEquals(messageFactory, instance.messageFactory);
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Test of newService method, of class ServiceFactory.
     */
    @Test
    public void testNewService() {
        System.out.println("newService");
        assertTrue(cfdiFactory.newService() instanceof DMTDownloadService);
        assertTrue(retFactory.newService() instanceof DMTDownloadService);
    }
    

    /**
     * Test of newAuthenticationService method, of class ServiceFactory.
     */
    @Test
    public void testNewAuthenticationService() {
        System.out.println("newAuthenticationService");
        assertTrue(cfdiFactory.newAuthenticationService() instanceof ServiceFactory.AuthenticationService);
        assertTrue(retFactory.newAuthenticationService() instanceof ServiceFactory.AuthenticationService);
    }

    /**
     * Test of newVerificaService method, of class ServiceFactory.
     */
    @Test
    public void testNewVerificaService() {
        System.out.println("newVerificaService");
        assertTrue(cfdiFactory.newVerificaService() instanceof ServiceFactory.VerificaService);
        assertTrue(retFactory.newVerificaService() instanceof ServiceFactory.VerificaService);
    }

    /**
     * Test of newDescargaService method, of class ServiceFactory.
     */
    @Test
    public void testNewDescargaService() {
        System.out.println("newDescargaService");
        assertTrue(cfdiFactory.newDescargaService() instanceof ServiceFactory.DescargaService);
        assertTrue(retFactory.newDescargaService() instanceof ServiceFactory.DescargaService);
    }

    /**
     * Test of newSolicitaService method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaService() {
        System.out.println("newSolicitaService");
        assertTrue(cfdiFactory.newSolicitaService() instanceof SolicitaSvcAdapter);
        assertTrue(retFactory.newSolicitaService() instanceof SolicitaSvcAdapter);
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Test of newSolicitaEmitidosService method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaEmitidosService() {
        System.out.println("newSolicitaEmitidosService");
        
        testNewSolicitaXXXService(
                cfdiFactory, new CFDIEmitidosCtx(), cfdiFactory.newSolicitaEmitidosService());
        
        testNewSolicitaXXXService(
                retFactory, new RetsEmitidosCtx(), retFactory.newSolicitaEmitidosService());
    }

    /**
     * Test of newSolicitaRecibidosService method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaRecibidosService() {
        System.out.println("newSolicitaRecibidosService");
        
        testNewSolicitaXXXService(
                cfdiFactory, new CFDIRecibidosCtx(), cfdiFactory.newSolicitaRecibidosService());
        
        testNewSolicitaXXXService(
                retFactory, new RetsRecibidosCtx(), retFactory.newSolicitaRecibidosService());
    }

    /**
     * Test of newSolicitaFolioService method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaFolioService() {
        System.out.println("newSolicitaFolioService");
     
        testNewSolicitaXXXService(
                cfdiFactory, new CFDIFolioCtx(), cfdiFactory.newSolicitaFolioService());
        
        testNewSolicitaXXXService(
                retFactory, new RetsFolioCtx(), retFactory.newSolicitaFolioService());
    }
    
    protected void testNewSolicitaXXXService(
            ServiceFactory factory,
            RequestContext requestContext,
            Service<SolicitaResponse, Query> result) {
        
        System.out.println("newSolicitaXXXService:"+requestContext.getServiceName());
        assertTrue(result instanceof SolicitaService);
        SolicitaService svc = (SolicitaService)result;
        assertEquals(factory.getMessageFactory(), svc.getContext());
        assertEqualRequestContexts(requestContext, svc.getRequestContext());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Test of newSolicitaEmitidosContext method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaEmitidosContext() {
        System.out.println("newSolicitaEmitidosContext");
        assertEqualRequestContexts(new CFDIEmitidosCtx(), cfdiFactory.newSolicitaEmitidosContext());
        assertEqualRequestContexts(new RetsEmitidosCtx(), retFactory.newSolicitaEmitidosContext());
    }

    /**
     * Test of newSolicitaRecibidosContext method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaRecibidosContext() {
        System.out.println("newSolicitaRecibidosContext");
        assertEqualRequestContexts(new CFDIRecibidosCtx(), cfdiFactory.newSolicitaRecibidosContext());
        assertEqualRequestContexts(new RetsRecibidosCtx(), retFactory.newSolicitaRecibidosContext());
    }

    /**
     * Test of newSolicitaFolioContext method, of class ServiceFactory.
     */
    @Test
    public void testNewSolicitaFolioContext() {
        System.out.println("newSolicitaFolioContext");
        assertEqualRequestContexts(new CFDIFolioCtx(), cfdiFactory.newSolicitaFolioContext());
        assertEqualRequestContexts(new RetsFolioCtx(), retFactory.newSolicitaFolioContext());
    }

    /**
     * Test of newDefaultContext method, of class ServiceFactory.
     */
    @Disabled 
    @Test
    public void testNewDefaultContext() {
        System.out.println("newDefaultContext");
        String serviceName = "";
        ServiceFactory instance = null;
        DefaultRequestContext expResult = null;
        DefaultRequestContext result = instance.newDefaultContext(serviceName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
/*

REQ <xs:element name="SolicitaDescargaEmitidos">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroEmitidos"/>
RES <xs:element name="SolicitaDescargaEmitidosResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaEmitidosResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <wsdl:operation name="SolicitaDescargaEmitidos">
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaEmitidos" style="document"/>
    <soap:address location="https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
*/
    protected class CFDIEmitidosCtx extends DefaultRequestContext {
        public CFDIEmitidosCtx() {
            serviceName = "SolicitaDescargaEmitidos";
            location = "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaEmitidos";
            parentRequestName = qname("SolicitaDescargaEmitidos");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaEmitidosResponse");
            resultName = qname("SolicitaDescargaEmitidosResult");
        }
    }
    
    /*
    
REQ <xs:element name="SolicitaDescargaRecibidos">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroRecibidos"/>
RES <xs:element name="SolicitaDescargaRecibidosResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaRecibidosResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <wsdl:operation name="SolicitaDescargaRecibidos">
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaRecibidos" style="document"/>
    <soap:address location="https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
    */
    protected class CFDIRecibidosCtx extends DefaultRequestContext {
        public CFDIRecibidosCtx() {
            serviceName = "SolicitaDescargaRecibidos";
            location = "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaRecibidos";
            parentRequestName = qname("SolicitaDescargaRecibidos");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaRecibidosResponse");
            resultName = qname("SolicitaDescargaRecibidosResult");
        }
    }
    /*
    
REQ <xs:element name="SolicitaDescargaFolio">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroFolio"/>
RES <xs:element name="SolicitaDescargaFolioResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaFolioResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaFolio" style="document"/>
    ----
    <soap:address location="https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
    */
    protected class CFDIFolioCtx extends DefaultRequestContext {
        public CFDIFolioCtx() {
            serviceName = "SolicitaDescargaFolio";
            location = "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaFolio";
            parentRequestName = qname("SolicitaDescargaFolio");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaFolioResponse");
            resultName = qname("SolicitaDescargaFolioResult");
        }
    }

    /*
    
REQ <xs:element name="SolicitaDescargaEmitidos">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroEmitidos"/>
RES <xs:element name="SolicitaDescargaEmitidosResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaEmitidosResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <wsdl:operation name="SolicitaDescargaEmitidos">
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaEmitidos" style="document"/>
    <soap:address location="https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
    */
    protected class RetsEmitidosCtx extends DefaultRequestContext {
        public RetsEmitidosCtx() {
            serviceName = "SolicitaDescargaEmitidos";
            location = "https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaEmitidos";
            parentRequestName = qname("SolicitaDescargaEmitidos");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaEmitidosResponse");
            resultName = qname("SolicitaDescargaEmitidosResult");
        }
    }
    /*
REQ <xs:element name="SolicitaDescargaRecibidos">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroRecibidos"/>
RES <xs:element name="SolicitaDescargaRecibidosResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaRecibidosResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <wsdl:operation name="SolicitaDescargaRecibidos">
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaRecibidos" style="document"/>
    <soap:address location="https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
    
    */
    protected class RetsRecibidosCtx extends DefaultRequestContext {
        public RetsRecibidosCtx() {
            serviceName = "SolicitaDescargaRecibidos";
            location = "https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaRecibidos";
            parentRequestName = qname("SolicitaDescargaRecibidos");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaRecibidosResponse");
            resultName = qname("SolicitaDescargaRecibidosResult");
        }
    }
    /*
    REQ <xs:element name="SolicitaDescargaFolio">
    <xs:element minOccurs="0" maxOccurs="1" name="solicitud" type="tns:SolicitudDescargaMasivaTerceroFolio"/>
RES <xs:element name="SolicitaDescargaFolioResponse">
    <xs:element minOccurs="0" maxOccurs="1" name="SolicitaDescargaFolioResult" type="tns:RespuestaSolicitudDescMasTercero"/>
    <wsdl:operation name="SolicitaDescargaFolio">
    <soap:operation soapAction="http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaFolio" style="document"/>
    ---
    <soap:address location="https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc"/>
    */
    protected class RetsFolioCtx extends DefaultRequestContext {
        public RetsFolioCtx() {
            serviceName = "SolicitaDescargaFolio";
            location = "https://retendescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
            soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescargaFolio";
            parentRequestName = qname("SolicitaDescargaFolio");
            childRequestName = qname("solicitud");
            responseName = qname("SolicitaDescargaFolioResponse");
            resultName = qname("SolicitaDescargaFolioResult");
        }
    }
    
    protected void assertEqualRequestContexts(RequestContext expected, RequestContext result) {
        System.out.println("assertEqualRequestContexts:"+expected.getServiceName());
        assertEquals(expected.getServiceName(), result.getServiceName());
        assertEquals(expected.getLocation(), result.getLocation());
        assertEquals(expected.getSoapAction(), result.getSoapAction());
        assertEquals(expected.getParentRequestName(), result.getParentRequestName());
        assertEquals(expected.getChildRequestName(), result.getChildRequestName());
        assertEquals(expected.getResponseName(), result.getResponseName());
        assertEquals(expected.getResultName(), result.getResultName());
    }
    ////////////////////////////////////////////////////////////////////////////
    protected QName qname(String name) {
        return cfdiFactory.getQName(name);
    }

    /**
     * Test of getRequestLocation method, of class ServiceFactory.
     * 
     * // return getRequestPrefix().concat(svcName);
     */
    @Test
    public void testGetRequestLocation() {
        System.out.println("getRequestLocation");
        testGetRequestLocation(cfdiFactory, "namexx");
        testGetRequestLocation(retFactory, "asdlkj");
    }
    
    protected void testGetRequestLocation(ServiceFactory instance, String svcName) {
        String prefix = instance.getRequestPrefix();
        String result = instance.getRequestLocation(svcName);
        assertEquals(prefix.concat(svcName), result);
    }

    /**
     * Test of getDownloadLocation method, of class ServiceFactory.
     * 
     * // return getDownloadPrefix().concat(svcName);
     */
    @Test
    public void testGetDownloadLocation() {
        System.out.println("getDownloadLocation");
        testGetDownloadLocation(cfdiFactory, "namexx");
        testGetDownloadLocation(retFactory, "asdlkj");
    }
    
    protected void testGetDownloadLocation(ServiceFactory instance, String svcName) {
        String prefix = instance.getDownloadPrefix();
        String result = instance.getDownloadLocation(svcName);
        assertEquals(prefix.concat(svcName), result);
    }

    /**
     * Test of getRequestPrefix method, of class ServiceFactory.
     *
     * // return (retenciones ? REQUEST_RET_URL : REQUEST_CFDI_URL);
     */
    @Test
    public void testGetRequestPrefix() {
        System.out.println("getRequestPrefix");
        testGetRequestPrefix(cfdiFactory, ServiceFactory.REQUEST_CFDI_URL);
        testGetRequestPrefix(retFactory, ServiceFactory.REQUEST_RET_URL);
    }

    protected void testGetRequestPrefix(ServiceFactory instance, String expResult) {
        String result = instance.getRequestPrefix();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getDownloadPrefix method, of class ServiceFactory.
     * 
     *  return (retenciones ? DOWNLOAD_RET_URL : DOWNLOAD_CFDI_URL);
     */
    @Test
    public void testGetDownloadPrefix() {
        System.out.println("getDownloadPrefix");
        testGetDownloadPrefix(cfdiFactory, ServiceFactory.DOWNLOAD_CFDI_URL);
        testGetDownloadPrefix(retFactory, ServiceFactory.DOWNLOAD_RET_URL);
    }
    
    protected void testGetDownloadPrefix(ServiceFactory instance, String expResult) {
        String result = instance.getDownloadPrefix();
        assertEquals(expResult, result);
    }

    /**
     * Test of getQName method, of class ServiceFactory.
     */
    @Test
    public void testGetQName() {
        System.out.println("getQName");
        String name = "abc";
        ServiceFactory instance = cfdiFactory;
        QName expResult = new QName(AbstractSvc.DMT_URI, name, AbstractSvc.DMT_PREFIX);
        QName result = instance.getQName(name);
        assertEquals(expResult, result);
    }
    
}
