/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import com.sicomsa.dmt.svc.LocalRepositoryTest;
import com.sicomsa.dmt.svc.LocalRepository;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.sicomsa.dmt.svc.DownloadService;
import com.sicomsa.dmt.svc.DefaultMessageFactory;
import java.time.Instant;
import java.time.InstantSource;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;
import java.util.List;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.24
 *
 *
 */
public class ClientTest {
    static SOAPConnection closedConn;

    static final Logger LOG = Logger.getLogger("com.sicomsa");
    
    public ClientTest(){
    }

    @BeforeAll
    public static void setUpClass() throws SOAPException {
        closedConn = SOAPConnectionFactory.newInstance().createConnection();
        closedConn.close();
        LOG.setLevel(Level.ALL);
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
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
    
    public static class UselessCredentials extends RealCredentials {
        @Override public String getRfc() {
            return "USELESS";
        }
    }
    
    public static class ClientMock extends Client {
        public ClientMock() {
            super(new UselessCredentials(), new ServiceMock());
        }
        public ServiceMock getServiceMock() {
            return (ServiceMock)service;
        }
        public boolean isValidToken() {
            return this.isValid(authorization);
        }
    }
    
    public static class ServiceMock implements DMTService {
        protected DownloadService service;
        protected DownloadRepository repository;
        
        protected boolean reject = false;
        protected boolean delay  = false;
        protected boolean noInfo = false;
        
        protected SolicitaResponse solicitaResponse;
        protected SolicitaResponse solicitaResponseRejected;
        protected VerificaResponse verificaResponse;
        protected VerificaResponse verificaResponseRejected;
        protected VerificaResponse verificaResponseDelayed;
        protected VerificaResponse verificaResponseNoInfo;
        protected DescargaResponse descargaResponse;
        protected DescargaResponse descargaResponseRejected;
    
        public ServiceMock() {
            service = new DownloadService();
            repository = new LocalRepositoryTest.ConsoleRepository();
            Instant now = Instant.now();
            solicitaResponse = new SolicitaResponse(now, "5000", "Solicitud Aceptada", "5555555");
            solicitaResponseRejected = new SolicitaResponse(now.plusSeconds(3), "5004", "Solicitud Rechazada", "");
            verificaResponse =
                    new VerificaResponse(now.plusSeconds(20), "5000", "Solicitud Aceptada",
                            VerificaResponse.TERMINADA, "5000", "rid-abc", 88, new PackageIds(List.of("abc", "cde", "fgh")));
            verificaResponseRejected =
                    new VerificaResponse(now.plusSeconds(55), "5500", "Solicitud Invalida",
                            VerificaResponse.ERROR, "5040", "rid-123");
            verificaResponseDelayed =
                    new VerificaResponse(now.plusSeconds(555), "5000", "In progress",
                            VerificaResponse.EN_PROCESO, "5000", "rid-abc");
            verificaResponseNoInfo = 
                    new VerificaResponse(now.plusSeconds(15), "5000", "",
                            VerificaResponse.RECHAZADA, "5004", "rid-abc");
            descargaResponse = new DescargaResponse(now.plusSeconds(2000), "5000", "Accepted x", "pid-abc", "package-data");
            descargaResponseRejected = new DescargaResponse(now.plusSeconds(3000), "5009", "Rejected x","pid-abc", "");
        }
        public SolicitaResponse getFixedSolicitaResponse() {
            return (reject ? solicitaResponseRejected : solicitaResponse);
        }
        public VerificaResponse getFixedVerificaResponse() {
            if (reject) {
                return verificaResponseRejected;
            }
            if (delay) {
                return verificaResponseDelayed;
            }
            if (noInfo) {
                return verificaResponseNoInfo;
            }
            return verificaResponse;
        }
        public DescargaResponse getFixedDescargaResponse() {
            return (reject ? descargaResponseRejected : descargaResponse);
        }
        
        public void setReject(boolean reject) {
            this.reject = reject;
        }
        
        public void setDelay(boolean delay) {
            this.delay = delay;
        }
        
        public void setNoInfo(boolean noInfo) {
            this.noInfo = noInfo;
        }
        
        @Override public Instant instant() {
            return service.instant();
        }
        @Override public Authorization autentica(SOAPConnection conn,
                Credentials creds) throws SOAPException{
            Instant now = service.instant();
            return new Authorization(now, now.plusSeconds(1), now.plusSeconds(60*5), "validtoken");
        }
        @Override public SolicitaResponse solicita(SOAPConnection conn,
                Credentials creds, Query query, String token) throws SOAPException {
            return getFixedSolicitaResponse();
        }
        @Override  public VerificaResponse verifica(SOAPConnection conn, Credentials creds, String requestId, String token) throws SOAPException {
            return getFixedVerificaResponse();
        }
        @Override public DescargaResponse descarga(SOAPConnection conn, Credentials creds, String packageId, String token) throws SOAPException {
            return getFixedDescargaResponse();
        }
        
        @Override public DownloadRepository getRepository() {
            return repository;
        }
        @Override public void setRepository(DownloadRepository repository) {
            this.repository = repository;
        }
        
    }
    
    

    /**
     * Test of getRfc method, of class Client.
     */
    @Test
    public void testGetRfc() {
        System.out.println("getRfc");
        Client instance = new Client(new UselessCredentials());
        String expResult = "USELESS";
        String result = instance.getRfc();
        assertEquals(expResult, result);
    }

    /**
     * Test of requestDownload method, of class Client.
     */
    @Test
    public void testRequestDownload() throws Exception {
        System.out.println("requestDownload");
        ClientMock client = new ClientMock();
        Query query = null;
        assertFalse(client.isValidToken());
        assertEquals(client.getServiceMock().getFixedSolicitaResponse(),
                client.requestDownload(closedConn, query));
        assertTrue(client.isValidToken());
    }

    /**
     * Test of verifyRequest method, of class Client.
     */
    @Test
    public void testVerifyRequest() throws Exception {
        System.out.println("verifyRequest");
        ClientMock client = new ClientMock();
        String requestId = "xrequest";
        assertFalse(client.isValidToken());
        assertEquals(client.getServiceMock().getFixedVerificaResponse(),
                client.verifyRequest(closedConn, requestId));
        assertTrue(client.isValidToken());
    }

    /**
     * Test of download method, of class Client.
     */
    @Test
    public void testDownload() throws Exception {
        System.out.println("download");
        ClientMock client = new ClientMock();
        String packageId = "xid";
        assertFalse(client.isValidToken());
        assertEquals(client.getServiceMock().getFixedDescargaResponse(),
                client.download(closedConn, packageId));
        assertTrue(client.isValidToken());
    }

    /**
     * Test of save method, of class Client.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        String packageId = "packageId-to-save";
        String packageInfo = "theEncodedPackage";
        String encodedPackage = Base64.getEncoder().encodeToString(packageInfo.getBytes());
        DownloadService service = new DownloadService();
        LocalRepositoryTest.ConsoleRepository repository = new LocalRepositoryTest.ConsoleRepository();
        service.setRepository(repository);
        Client instance = new Client(new UselessCredentials(), service);
        instance.save(packageId, encodedPackage);
    }

    /**
     * Test of getRepository method, of class Client.
     */
    @Test
    public void testGetRepository() {
        System.out.println("getRepository");
        DownloadService service = new DownloadService();
        DownloadRepository expected = new LocalRepository();
        service.setRepository(expected);
        Client instance = new Client(new UselessCredentials(), service);
        DownloadRepository result = instance.getRepository();
        assertEquals(expected, result);
    }

    /**
     * Test of isValid method, of class Client.
     */
    @Test
    public void testIsValid() {
        System.out.println("isValid");
        DefaultMessageFactory factory = DefaultMessageFactory.newInstance();
        DownloadService service = new DownloadService(factory);
        Client instance = new Client(new UselessCredentials(), service);
        assertFalse(instance.isValid(null));
        // public Authorization(Instant satInstant, Instant created, Instant expires, String token) {      
        Authorization auth = new Authorization(Instant.now(),
                Instant.now().plusSeconds(1), Instant.now().plusSeconds(60*5), "token");
        Instant current = auth.getExpires().minusSeconds(10);
        Instant expired = auth.getExpires().plusSeconds(4);
        factory.setInstantSource(InstantSource.fixed(expired));
        assertFalse(instance.isValid(auth));
        factory.setInstantSource(InstantSource.fixed(current));
        assertTrue(instance.isValid(auth));
    }

}