/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.SvcSignatureException;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;

import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.util.SOAPUtils;
import com.sicomsa.dmt.util.QueryMap;

import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.CredentialsProxy;
import com.sicomsa.dmt.RepositoryException;
import com.sicomsa.dmt.SolicitaResponse;


import jakarta.xml.soap.SOAPFault;
 
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.time.LocalDateTime;


import jakarta.xml.ws.soap.SOAPFaultException;
import java.security.GeneralSecurityException;
import javax.xml.crypto.MarshalException;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;


/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.30
 *
 *
 */
public class AbstractSvcTest {
    static SvcMessageFactory factory;
    static AbstractSvcImpl service;
    static QueryMap query;
    static Credentials credentials;
    static SOAPConnection closedConn;
    static final Logger LOG = Logger.getLogger("com.sicomsa");

    public AbstractSvcTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        System.out.println("setUpClass");
        factory = DefaultMessageFactory.newInstance();
        service = new AbstractSvcImpl(factory);
        credentials = new UselessCredentials("an rfc");
        query = new QueryMap.Builder()
                .setFechaInicial(LocalDateTime.now())
                .setFechaFinal(LocalDateTime.now().plusDays(2))
                .setRfcEmisor(credentials.getRfc())
                .setRfcSolicitante(credentials.getRfc()).build();
        LOG.setLevel(Level.ALL);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
        closedConn = SOAPConnectionFactory.newInstance().createConnection();
        closedConn.close();
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
    
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Test of getContext method, of class AbstractSvc.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        assertEquals(factory, service.getContext());
    }


    /**
     * Test of callTheService method, of class AbstractSvc.
     */
    @Test
    public void testCallTheService() throws Exception {
        System.out.println("callTheService");
        AbstractSvcImpl svc = new AbstractSvcImpl(factory);
        SOAPConnection connection = closedConn;
        svc.callTheService(connection, credentials, query, "a token");
        svc.setParseFault(true);
        Exception e = assertThrows(SOAPFaultException.class, ()-> {
            svc.callTheService(connection, credentials, query, "a token");
        });
        System.out.println("faultexmsg="+e.getMessage());
    }
    
    

    /**
     * Test of createMessageToSend method, of class AbstractSvc.
     */
    @Disabled()
    @Test
    public void testCreateMessageToSend() throws Exception {
    }
    

    /**
     * Test of fillAndSign method, of class AbstractSvc.
     */
    @Test
    public void testFillAndSign() throws Exception {
        System.out.println("fillAndSign");
        SOAPMessage message = service.getAMessage();
        Query request = query;
        AbstractSvcImpl svc = new AbstractSvcImpl(factory);
        svc.setSecurityFail("security");
        Exception e = assertThrows(SvcSignatureException.class, ()-> {
            svc.fillAndSign(message, credentials, request);
        });
        System.out.println("securityex.message="+e.getMessage());

        svc.setSecurityFail("credentials");
        e = assertThrows(SvcSignatureException.class, ()-> {
            svc.fillAndSign(message, credentials, request);
        });
        System.out.println("securityex.message="+e.getMessage());

        svc.setSecurityFail("marshal");
        e = assertThrows(SvcSignatureException.class, ()-> {
            svc.fillAndSign(message, credentials, request);
        });
        System.out.println("securityex.message="+e.getMessage());
    }

    /**
     * We do no test this method in this Abstract class
     * /
    @Test
    public void testCallService() throws Exception {
    }

    /**
     * Test of newMessage method, of class AbstractSvc.
     */
    @Test
    public void testNewMessage() {
        System.out.println("newMessage");
        assertDoesNotThrow(()-> {service.newMessage();});
    }

    /**
     * Test of addNamespaces method, of class AbstractSvc.
     */
    @Test
    public void testAddNamespaces() throws Exception {
        System.out.println("addNamespaces");
        Exception e = assertThrows(NullPointerException.class, ()-> {
            service.addNamespaces(null);
        });
        System.out.println("nullex="+e.getMessage());
    }
    
    /**
     * Test of setHeaders method, of class AbstractSvc.
     */
    @Test
    public void testSetHeaders() throws SOAPException {
        System.out.println("setHeaders");
        String token = "a random token";
        SOAPMessage message = service.createMessageToSend(null, null, token);
        MimeHeaders headers = message.getMimeHeaders();
        assertEquals(service.getSoapAction(), getFirst(headers.getHeader("SOAPAction")));
        assertEquals(token, getFirst(headers.getHeader("Authorization")));
        Exception e = assertThrows(NullPointerException.class, ()-> {
            service.setHeaders(null, "token");
        });
        System.out.println("nullex="+e.getMessage());
        assertDoesNotThrow(()->{service.setHeaders(headers, null);});
    }
    
    protected String getFirst(String[] array) {
        return array[0];
    }

    /**
     * Test of checkFault method, of class AbstractSvc.
     */
    @Test
    public void testCheckFault() throws SOAPException {
        System.out.println("checkFault");
        Exception e;
        e = assertThrows(SOAPFaultException.class, ()-> {service.checkFault(service.getFaultMessage());});
        System.out.println("faultEx="+e.getMessage());
        assertDoesNotThrow(()-> {service.checkFault(service.getAMessage());});
        e = assertThrows(NullPointerException.class, ()-> {
                service.checkFault(null);
        });
        System.out.println("nullex="+e.getMessage());
    }


    /**
     * Test of getLogMsg method, of class AbstractSvc.
     * 
     *   protected String getLogMsg(SOAPFault fault, String rfc, Q request) {
     */
    @Test
    public void testfaultMessage() throws SOAPException {
        System.out.println("faultMessage");
        String result = service.faultMessage(service.getFault());
        System.out.println("faultLogMsg="+result);
        Exception e = assertThrows(NullPointerException.class, ()-> {
            service.faultMessage((SOAPFault)null);
        });
        System.out.println("fault null ex="+e.getMessage());
    }


    ////////////////////////////////////////////////////////////////////////////
    
    public static class AbstractSvcImpl extends AbstractSvc<SolicitaResponse,Query> {
              
        protected boolean parseFault = false;
        protected String signatureFail = "";

        public AbstractSvcImpl(SvcMessageFactory factory) {
            super(factory);
        }
        
        @Override
        public SOAPMessage callService(SOAPConnection connection, SOAPMessage request) throws SOAPException {
            System.out.println("callService, parseFault="+parseFault);
            if (parseFault) {
                return getFaultMessage();
            }
            return getAMessage();
        }
        
        protected void setParseFault(boolean parseFault) {
            this.parseFault = parseFault;
        }
        
        protected void setSecurityFail(String type) {
            this.signatureFail = type;
        }

        @Override public String getServiceName() {
            return "a test service name";
        }

        @Override public String getLocation() {
            return "a service location";
        }

        @Override public String getSoapAction() {
            return "a soap action";
        }

        @Override public SolicitaResponse parseReceivedMessage(SOAPMessage message, Instant instant, Query query) throws SOAPException {
            return new SolicitaResponse(Instant.now(),"5000", "Solicitud Aceptada", "ABCD-EFG-HIJKLMN-OPQ");
        }
        
        @Override protected void addSignedContent(SOAPMessage message, Credentials credentials, Query request)
            throws SOAPException, GeneralSecurityException, SvcSignatureException { //, MarshalException, XMLSignatureException {
            
            try {
                if ("security".equals(signatureFail)) {
                    getContext().getSignatureFactory()
                            .getXMLSignatureFactory()
                            .newCanonicalizationMethod(
                            "alksjdfñlaksdjf ñasdlkfj",(C14NMethodParameterSpec)null);
               }
                else if ("credentials".equals(signatureFail)) {
                    throw new XMLSignatureException("test thrown signature exception");
                }
                else if ("marshal".equals(signatureFail)) {
                    throw new MarshalException("test thrown signature exception Marshal");
                }
            }
            catch (MarshalException | XMLSignatureException e) {
                throw new SvcSignatureException(e.getMessage(), e);
            }
        }
        
        protected SOAPMessage getAMessage() throws SOAPException {
            SOAPMessage msg = this.newMessage();
            msg.getSOAPBody().addChildElement(SolicitaSvc.SOLICITA);
            msg.saveChanges();
            return msg;
        }
        
        protected SOAPFault getFault() throws SOAPException {
            return getFaultMessage().getSOAPBody().getFault();
        }
        
        protected SOAPMessage getFaultMessage() throws SOAPException {
            String message = """
            <s:envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                <s:body>
                    <s:fault>
                        <faultcode xmlns:a="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">a:InvalidSecurity</faultcode>
                        <faultstring xml:lang="en-US">An error occurred when verifying security for the message.</faultstring>
                    </s:fault>
                </s:body>
            </s:envelope>""";
            try {
                return SOAPUtils.fromString(message);
            }
            catch (java.io.IOException e) {
                throw new RuntimeException("unable to create soap:",e);
            }
        }
    }
    
    protected static class UselessCredentials extends CredentialsProxy {
        public UselessCredentials(String rfc) {
            super(rfc);
        }
        @Override protected Credentials doGetCredentials() throws RepositoryException {
            throw new UnsupportedOperationException("cannot get credentials");
        }
    
    }
}