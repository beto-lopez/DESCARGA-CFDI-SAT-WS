/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.ClientTest;
import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.PackageIds;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SatResponse;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.util.QueryMap;
import jakarta.xml.soap.SOAPConnection;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.22
 *
 *
 */
public class DefaultSolicitudeTest {

    public DefaultSolicitudeTest() {
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
     * Test of getClient method, of class DefaultSolicitude.
     */
    @Test
    public void testGetClient() {
        System.out.println("getClient");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        DMTClient expResult = DefaultSolicitudePre.CLIENT;
        DMTClient result = instance.getClient();
        assertEquals(expResult, result);
    }

    /**
     * Test of getQuery method, of class DefaultSolicitude.
     */
    @Test
    public void testGetQuery() {
        System.out.println("getQuery");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        Query expResult = DefaultSolicitudePre.QUERY;
        Query result = instance.getQuery();
        assertEquals(expResult, result);
    }

    /**
     * Test of isReject method, of class DefaultSolicitude.
     */
    @Test
    public void testIsReject() {
        System.out.println("isReject");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        boolean expResult = instance.getReject() != null;
        boolean result = instance.isReject();
        assertEquals(expResult, result);
    }

    /**
     * Test of getReject method, of class DefaultSolicitude.
     */
    @Test
    public void testGetReject() {
        System.out.println("getReject");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        SatResponse expResult = DefaultSolicitudePre.REJECT;
        SatResponse result = instance.getReject();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRequestId method, of class DefaultSolicitude.
     */
    @Test
    public void testGetRequestId() {
        System.out.println("getRequestId");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        String expResult = DefaultSolicitudePre.REQUEST_ID;
        String result = instance.getRequestId();
        assertEquals(expResult, result);
    }

    /**
     * Test of isDelay method, of class DefaultSolicitude.
     */
    @Test
    public void testIsDelay() {
        System.out.println("isDelay");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        boolean expResult = instance.getDelay() != null;
        boolean result = instance.isDelay();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDelay method, of class DefaultSolicitude.
     */
    @Test
    public void testGetDelay_0args() {
        System.out.println("getDelay");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        Delay expResult = DefaultSolicitudePre.DELAY;
        Delay result = instance.getDelay();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastAccepted method, of class DefaultSolicitude.
     */
    @Test
    public void testGetLastAccepted() {
        System.out.println("getLastAccepted");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        Instant expResult = DefaultSolicitudePre.LAST_ACCEPTED;
        Instant result = instance.getLastAccepted();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCfdis method, of class DefaultSolicitude.
     */
    @Test
    public void testGetCfdis() {
        System.out.println("getCfdis");
        DefaultSolicitude instance = new DefaultSolicitudePre();
        int expResult = DefaultSolicitudePre.CFDIS;
        int result = instance.getCfdis();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPackageIds method, of class DefaultSolicitude.
     */
    @Test
    public void testGetPackageIds() {
        System.out.println("getPackageIds");
        DefaultSolicitude instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.FLAGS);
        PackageIds expResult = DefaultSolicitudePre.IDS;
        PackageIds result = instance.getPackageIds();
        assertEquals(expResult, result);
    }

    /**
     * Test of isDownloaded method, of class DefaultSolicitude.
     */
    @Test
    public void testIsDownloaded() {
        System.out.println("isDownloaded");
        DefaultSolicitude instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.FLAGS);
        for (int index = 0; index < DefaultSolicitudeSub.FLAGS.length; index++) {
            assertEquals(DefaultSolicitudeSub.FLAGS[index], instance.isDownloaded(index));
        }
    }

    /**
     * Test of isDownloadDone method, of class DefaultSolicitude.
     */
    @Test
    public void testIsDownloadDone() {
        System.out.println("isDownloadDone");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        boolean expResult = false;
        boolean result = instance.isDownloadDone();
        assertEquals(expResult, result);
        
        instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.DONE_FLAGS);
        expResult = true;
        result = instance.isDownloadDone();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRequestId method, of class DefaultSolicitude.
     */
    @Test
    public void testSetRequestId() {
        System.out.println("setRequestId");
        String requestId = "asdfasdfasdfasdfasd f";
        DefaultSolicitude instance = new DefaultSolicitudePre();
        instance.setRequestId(requestId);
        assertEquals(requestId, instance.getRequestId());
    }

    /**
     * Test of setLastAccepted method, of class DefaultSolicitude.
     */
    @Test
    public void testSetLastAccepted() {
        System.out.println("setLastAccepted");
        Instant instant = Instant.now().minusSeconds(8888888);
        DefaultSolicitude instance = new DefaultSolicitudePre();
        instance.setLastAccepted(instant);
        assertEquals(instant, instance.getLastAccepted());
    }

    /**
     * Test of setDelay method, of class DefaultSolicitude.
     */
    @Test
    public void testSetDelay() {
        System.out.println("setDelay");
        Delay delay = Delay.OTHER;
        DefaultSolicitude instance = new DefaultSolicitudePre();
        instance.setDelay(delay);
        assertEquals(delay, instance.getDelay());
    }

    /**
     * Test of setCfdis method, of class DefaultSolicitude.
     */
    @Test
    public void testSetCfdis() {
        System.out.println("setCfdis");
        int cfdis = 123123;
        DefaultSolicitude instance = new DefaultSolicitudePre();
        instance.setCfdis(cfdis);
        assertEquals(cfdis, instance.getCfdis());
    }

    /**
     * Test of setUnverified method, of class DefaultSolicitude.
     */
    @Test
    public void testSetUnverified() {
        System.out.println("setUnverified"); 
        DefaultSolicitude instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.FLAGS);
        assertNotEquals(instance.registry, DefaultSolicitude.UNVERIFIED_REGISTRY);
        instance.setUnverified();
        assertEquals(instance.registry, DefaultSolicitude.UNVERIFIED_REGISTRY);
    }

    /**
     * Test of setVerified method, of class DefaultSolicitude.
     */
    @Test
    public void testSetVerified_PackageIds() {
        System.out.println("setVerified");
        DefaultSolicitude instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.DONE_FLAGS);
        assertTrue(instance.isDownloadDone());
        
        PackageIds ids = new PackageIds(List.of("abc", "cde", "fgh"));
        instance.setVerified(ids);
        assertFalse(instance.isDownloadDone());
        assertEquals(ids, instance.getPackageIds());
        for (int idx = 0; idx < ids.size(); idx++) {
            assertFalse(instance.isDownloaded(idx));
        }
        instance.setVerified((PackageIds)null);
        assertNull(instance.getPackageIds());
    }

    /**
     * Test of setVerified method, of class DefaultSolicitude.
     */
    @Test
    public void testSetVerified_DownloadRegistry1() {
        System.out.println("setVerified");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        
        DownloadRegistryImpl registry = new DownloadRegistryImpl(DefaultSolicitudePre.IDS, DefaultSolicitudePre.FLAGS);
        instance.setVerified(registry);
        assertMatches(registry, instance);
        
        registry = new DownloadRegistryImpl(DefaultSolicitudePre.IDS, DefaultSolicitudePre.DONE_FLAGS);
        instance.setVerified(registry);
        assertMatches(registry, instance);
        
        instance.setVerified((DownloadRegistryImpl)null);
        assertNull(instance.getPackageIds());
    }
    
    protected void assertMatches(DownloadRegistryImpl registry, DefaultSolicitude instance) {
        PackageIds ids = registry.getPackageIds();
        assertEquals(ids, instance.getPackageIds());
        for (int idx = 0; idx < ids.size(); idx++) {
            assertEquals(registry.isDownloaded(idx), instance.isDownloaded(idx));
        }
        assertEquals(registry.isDownloadDone(), instance.isDownloadDone());
    }

    /**
     * Test of setState method, of class DefaultSolicitude.
     */
    @Test
    public void testSetState() {
        System.out.println("setState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertTestState(new DownloadState.New(), instance);
        assertTestState(new DownloadState.Accepted(), instance);
        assertTestState(new DownloadState.Delayed(), instance);
        assertTestState(new DownloadState.Verified(), instance);
    }
    
    protected void assertTestState(DownloadState state, DefaultSolicitude solicitude) {
        solicitude.setState(state);
        assertEquals(state.getValue(), solicitude.getValue());
    }

    /**
     * Test of doUpdate method, of class DefaultSolicitude.
     */
    @Test
    public void testDoUpdate_SolicitaResponse() {
        System.out.println("doUpdate");
        DefaultSolicitude instance  = new DefaultSolicitudeSub();
        DownloadAdapter listener = new DownloadAdapter();
        instance.addDownloadListener(listener);
        int expectedRejects = 0;
        int expectedAccepts = 0;
        
        SolicitaResponse rejectedResponse = new SolicitaResponse(Instant.now(),
            "7777", "rejected", "");
        assertFalse(instance.isReject());
        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertTrue(instance.isReject());
        assertEquals(expectedRejects, listener.rejectsCount());
        
        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertTrue(instance.isReject());
        assertEquals(expectedRejects, listener.rejectsCount());
        
        SolicitaResponse response = new SolicitaResponse(Instant.now().minusSeconds(7777),
                "5000", "accepted", "abc-def");
        instance.doUpdate(response); expectedAccepts++;
        assertFalse(instance.isReject());
        assertEquals(response.getInstant(), instance.getLastAccepted());
        assertEquals(response.getRequestId(), instance.getRequestId());
        assertEquals(expectedAccepts, listener.acceptsCount());
        
        assertIllegalState(()->{instance.doUpdate(response);}, "SolicitaResponse");
        assertIllegalState(()->{instance.doUpdate(rejectedResponse);}, "SolicitaResponse");
    }
    

    /**
     * Test of doUpdate method, of class DefaultSolicitude.
     */
    @Test
    public void testDoUpdate_VerificaResponse() {
        System.out.println("doUpdate");
        DefaultSolicitude instance  = new DefaultSolicitudeSub();
        DownloadAdapter listener = new DownloadAdapter();
        instance.addDownloadListener(listener);
        int expectedRejects = 0;
        int expectedVerifies = 0;
        int expectedDelays = 0;
        
        VerificaResponse rejectedResponse = new VerificaResponse(
                Instant.now().minusSeconds(7777), "5000", "rejected",
                VerificaResponse.RECHAZADA, "4444", "ffff");
        
        
        VerificaResponse delayedResponse = new VerificaResponse(
                Instant.now().minusSeconds(8888),"5000", "accepted", 
                VerificaResponse.ACEPTADA, "5000", "abc-def-ghi");
        
        String text = "VerificaResponse";
        assertEquals(StateValue.NEW, instance.getValue());
        assertIllegalState(()->{instance.doUpdate(rejectedResponse);}, text);
        instance.setState(instance.getVerifiedState());
        assertEquals(StateValue.VERIFIED, instance.getValue());
        assertIllegalState(()->{instance.doUpdate(delayedResponse);}, text);
        
        instance.setState(instance.getAcceptedState());
        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertEquals(expectedRejects, listener.rejectsCount());
        assertTrue(instance.isReject());
           
        instance.setState(instance.getDelayState());
        instance.doUpdate(delayedResponse); expectedDelays++;
        assertEquals(expectedDelays, listener.delaysCount());
        assertMatches(delayedResponse, instance);

        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertEquals(expectedRejects, listener.rejectsCount());
        assertTrue(instance.isReject());
        
        PackageIds ids = new PackageIds(List.of("abc", "def", "ghi"));
        VerificaResponse verifiedResponse = new VerificaResponse(
                Instant.now().minusSeconds(6666), "5000", "accepted",
                VerificaResponse.TERMINADA, "5000", "xyzabc-def-ghi", 777, ids);
        
        instance.doUpdate(verifiedResponse); expectedVerifies++;
        assertEquals(expectedVerifies, listener.verifiesCount());
        assertMatches(verifiedResponse, instance);

    }
    
    /**
     * Test of doUpdate method, of class DefaultSolicitude.
     */
    @Test
    public void testDoUpdate_DescargaResponse() {
        /*
        
    public DescargaResponse(Instant satInstant, String statusCode, String message, String packageId, String encodedPackage) {
        */
        System.out.println("doUpdate");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        
        DownloadAdapter listener = new DownloadAdapter();
        instance.addDownloadListener(listener);
        int expectedRejects = 0;
        int expectedDownloads = 0;
        
        DescargaResponse rejectedResponse = new DescargaResponse(Instant.now(),
                "7777", "cannot download", "abc", null);
        
        DescargaResponse acceptedResponse = new DescargaResponse(Instant.now(),
                "5000", "ok", "abc", "epackage");
        acceptedResponse.dispose();
        
        String text = "DescargaResponse";
        assertEquals(StateValue.NEW, instance.getValue());
        assertIllegalState(()->{instance.doUpdate(rejectedResponse);}, text);
        instance.setState(instance.getDelayState());
        assertEquals(StateValue.DELAYED, instance.getValue());
        assertIllegalState(()->{instance.doUpdate(rejectedResponse);}, text);
        instance.setState(instance.getAcceptedState());
        assertEquals(StateValue.ACCEPTED, instance.getValue());
        assertIllegalState(()->{instance.doUpdate(rejectedResponse);}, text);
        
        PackageIds ids = new PackageIds(List.of("abc", "def", "ghi"));
        DownloadRegistryImpl registry = new DownloadRegistryImpl(ids);
        SolicitudeData data = new SolicitudeData.Verified("abc-def", Instant.now(), 777, registry);
        instance.restore(data);
        assertEquals(StateValue.VERIFIED, instance.getValue());
        assertTrue(!instance.isDownloaded(0) && !instance.isDownloaded(1) && !instance.isDownloaded(2));
        
        assertFalse(instance.isReject());
        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertEquals(expectedRejects, listener.rejectsCount());
        assertTrue(instance.isReject());
        assertTrue(!instance.isDownloaded(0) && !instance.isDownloaded(1) && !instance.isDownloaded(2));
        
        instance.doUpdate(acceptedResponse); expectedDownloads++;
        assertEquals(expectedDownloads, listener.downloladsCount());
        assertFalse(instance.isReject());
        assertTrue(instance.isDownloaded(0) && !instance.isDownloaded(1) && !instance.isDownloaded(2));
        
        acceptedResponse = new DescargaResponse(Instant.now(), "5000", "ok", "ghi", "epackage");
        acceptedResponse.dispose(); //avoid save
        instance.doUpdate(acceptedResponse);expectedDownloads++;
        assertEquals(expectedDownloads, listener.downloladsCount());
        assertFalse(instance.isReject());
        assertTrue(instance.isDownloaded(0) && !instance.isDownloaded(1) && instance.isDownloaded(2));
        
        assertFalse(instance.isReject());
        instance.doUpdate(rejectedResponse); expectedRejects++;
        assertEquals(expectedRejects, listener.rejectsCount());
        assertTrue(instance.isReject());
        assertTrue(instance.isDownloaded(0) && !instance.isDownloaded(1) && instance.isDownloaded(2));
        
        assertFalse(instance.isDownloadDone());
        acceptedResponse = new DescargaResponse(Instant.now(), "5000", "ok", "def", "epackage");
        acceptedResponse.dispose(); //avoid save
        instance.doUpdate(acceptedResponse);expectedDownloads++;
        assertEquals(expectedDownloads, listener.downloladsCount());
        assertTrue(instance.isDownloadDone());
    }
    
    protected void assertMatches(VerificaResponse response, DefaultSolicitude instance) {
        assertEquals(response.getInstant(), instance.getLastAccepted());
        assertEquals(response.getRequestId(), instance.getRequestId());
        assertEquals(response.getCfdis(), instance.getCfdis());
        assertEquals(response.getPackageIds(), instance.getPackageIds());
        assertEquals(response.isDelay(), instance.isDelay());
        assertEquals(!response.isAccept(), instance.isReject());
    }
    
    protected void assertIllegalState(Executable executable, String text) {
        Exception e = assertThrows(IllegalStateException.class, ()->{
            executable.execute();
        });
        assertTrue(e.getMessage().contains(text));
        assertTrue(e.getMessage().contains("Invalid state"));
    }



    /**
     * Test of processReject method, of class DefaultSolicitude.
     */
    @Test
    public void testProcessReject() {
        System.out.println("processReject");
        SatResponse reject = new SolicitaResponse(Instant.now(), "4444", "rejected", null);
        DefaultSolicitude instance  = new DefaultSolicitudeSub();
        instance.processReject(reject);
        assertEquals(reject, instance.getReject());
        assertTrue(instance.isReject());
    }
    /**
     * Test of isAccept method, of class DefaultSolicitude.
     */
    @Test
    public void testIsAccept() {
        System.out.println("isAccept");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        SatResponse response = new SolicitaResponse(Instant.now(), "5555", "notAccepted", "");
        assertFalse(response.isAccept());
        assertEquals(response.isAccept(), instance.isAccept(response));
        
        response = new SolicitaResponse(Instant.now(), "5000", "Accepted", "abc");
        assertTrue(response.isAccept());
        assertEquals(response.isAccept(), instance.isAccept(response));
    }

    /**
     * Test of getDelay method, of class DefaultSolicitude.
     */
    @Test
    public void testGetDelay_VerificaResponse() {
        System.out.println("getDelay");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        
        VerificaResponse response = new VerificaResponse(Instant.now(),
                "5000", "ok", VerificaResponse.ACEPTADA, "5000", "abc");
        assertEquals(Delay.ACCEPTED, instance.getDelay(response));
        
        response = new VerificaResponse(Instant.now(),
                "5000", "ok", VerificaResponse.EN_PROCESO, "5000", "abc");
        assertEquals(Delay.IN_PROGRESS, instance.getDelay(response));
        
        response = new VerificaResponse(Instant.now(),
                "5005", "nok", VerificaResponse.EN_PROCESO, "5005", "abc");
        assertNull(instance.getDelay(response));
    }

    /**
     * Test of fireDownloadEvent method, of class DefaultSolicitude.
     */
    @Test
    public void testFireDownloadEvent() {
        System.out.println("fireDownloadEvent");
         SatResponse response = new SolicitaResponse(Instant.now(), "5000", "ok", "");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        DownloadAdapter listener = new DownloadAdapter();
        assertEquals(0, listener.getCount());
        instance.addDownloadListener(listener);
        instance.fireDownloadEvent(DownloadEvent.Result.REJECTED, response);
        instance.fireDownloadEvent(DownloadEvent.Result.ACCEPTED, response);
        instance.fireDownloadEvent(DownloadEvent.Result.DELAYED, response);
        instance.fireDownloadEvent(DownloadEvent.Result.VERIFIED, response);
        instance.fireDownloadEvent(DownloadEvent.Result.DOWNLOADED, response);
        assertEquals(5, listener.getCount());
    }

    /**
     * Test of setReject method, of class DefaultSolicitude.
     */
    @Test
    public void testSetReject() {
        System.out.println("setReject");
        SatResponse reject = null;
        DefaultSolicitude instance = new DefaultSolicitudePre();
        SatResponse oldReject = instance.getReject();
        assertNotEquals(reject, oldReject);
        instance.setReject(reject);
        assertEquals(reject, instance.getReject());
        instance.setReject(oldReject);
        assertEquals(oldReject, instance.getReject());
    }

    /**
     * Test of getNewRequestState method, of class DefaultSolicitude.
     */
    @Test
    public void testGetNewRequestState() {
        System.out.println("getNewRequestState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        DownloadState expResult = DefaultSolicitude.NEW_STATE;
        DownloadState result = instance.getNewRequestState();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAcceptedState method, of class DefaultSolicitude.
     */
    @Test
    public void testGetAcceptedState() {
        System.out.println("getAcceptedState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        DownloadState expResult = DefaultSolicitude.ACCEPTED_STATE;
        DownloadState result = instance.getAcceptedState();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDelayState method, of class DefaultSolicitude.
     */
    @Test
    public void testGetDelayState() {
        System.out.println("getDelayState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        DownloadState expResult = DefaultSolicitude.DELAYED_STATE;
        DownloadState result = instance.getDelayState();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVerifiedState method, of class DefaultSolicitude.
     */
    @Test
    public void testGetVerifiedState() {
        System.out.println("getVerifiedState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        DownloadState expResult = DefaultSolicitude.VERIFIED_STATE;
        DownloadState result = instance.getVerifiedState();
        assertEquals(expResult, result);
    }

    /**
     * Test of isPending method, of class DefaultSolicitude.
     */
    @Test
    public void testIsPending() {
        System.out.println("isPending");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertPending(instance, false, false);
              
        SatResponse reject = new SolicitaResponse(Instant.now(), "5555", "Rechazada", "");
        instance.setReject(reject);
        assertPending(instance, true, false);
     
        instance.setReject(null);
        assertPending(instance, false, false);
        
        DownloadRegistryImpl dr = getDoneRegistry();
        SolicitudeData data = new SolicitudeData.Verified("a request id", Instant.now(), 88, dr);
        instance.restore(data);
        assertPending(instance, false, true);
        
        instance.setReject(reject);
        assertPending(instance, true, true);
    }
    
    protected void assertPending(DefaultSolicitude instance, boolean reject, boolean done) {
        assertEquals(reject, instance.isReject());
        assertEquals(done, instance.isDownloadDone());
        assertEquals(!reject && !done, instance.isPending());
    }
    
    protected DownloadRegistryImpl getDoneRegistry() {
        boolean[] flags = new boolean[1];
        flags[0] = true;
        return new DownloadRegistryImpl(new PackageIds(List.of("abc")), flags);
    }

    /**
     * Test of addDownloadListener method, of class DefaultSolicitude.
     */
    @Test
    public void testAddDownloadListener() {
        System.out.println("addDownloadListener");
        DownloadListener listener = new DownloadAdapter();
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertFalse(hasListener(instance, listener));
        instance.addDownloadListener(listener);
        assertTrue(hasListener(instance, listener));
    }
    /**
     * Test of removeDownloadListener method, of class DefaultSolicitude.
     */
    @Test
    public void testRemoveDownloadListener() {
        System.out.println("removeDownloadListener");
        DownloadListener listener = new DownloadAdapter();
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        instance.addDownloadListener(listener);
        assertTrue(hasListener(instance, listener));
        instance.removeDownloadListener(listener);
        assertFalse(hasListener(instance, listener));
    }
    
    protected boolean hasListener(DefaultSolicitude instance, DownloadListener listener) {
        Object[] array = instance.listenerList.getListenerList();
        for (Object obj : array) {
            if (obj == listener) {
                return true;
            }
        }
        return false;
    }

    public class DownloadAdapter implements DownloadListener {
        protected int rejects, accepts, delays, verifies, downloads;
        
        public void assertCount(int count) {
            assertEquals(count, getCount());
        }
        public int getCount() {
            return (rejects+accepts+delays+verifies+downloads);
        }
        public int rejectsCount() { return rejects; }
        public int acceptsCount() { return accepts; }
        public int delaysCount() { return delays; }
        public int verifiesCount() { return verifies; }
        public int downloladsCount() { return downloads; }
        
        @Override public void stateChanged(DownloadEvent evt) {
            switch (evt.getResult()) {
                case REJECTED-> rejects++;
                case ACCEPTED-> accepts++;
                case DELAYED-> delays++;
                case VERIFIED-> verifies++;
                case DOWNLOADED-> downloads++;
                default-> throw new IllegalStateException("invalid result:"+evt.getResult());
            }
        }
    }

    /**
     * Test of pause method, of class DefaultSolicitude.
     */
    @Test
    public void testPause() {
        System.out.println("pause");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertFalse(instance.isPaused());
        instance.pause();
        assertTrue(instance.isPaused());
    }

    /**
     * Test of isPaused method, of class DefaultSolicitude.
     */
    @Test
    public void testIsPaused() {
        System.out.println("isPaused");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertFalse(instance.isPaused());
        instance.pause();
        assertTrue(instance.isPaused());
    }

    /**
     * Test of getValue method, of class DefaultSolicitude.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertEquals(StateValue.NEW, instance.getValue());
        instance.setState(DefaultSolicitude.ACCEPTED_STATE);
        assertEquals(StateValue.ACCEPTED, instance.getValue());
        instance.setState(DefaultSolicitude.DELAYED_STATE);
        assertEquals(StateValue.DELAYED, instance.getValue());
        instance.setState(DefaultSolicitude.VERIFIED_STATE);
        assertEquals(StateValue.VERIFIED, instance.getValue());
        instance.setState(DefaultSolicitude.NEW_STATE);
        assertEquals(StateValue.NEW, instance.getValue());
    }

    /**
     * Test of getState method, of class DefaultSolicitude.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertEquals(DefaultSolicitude.NEW_STATE, instance.getState(StateValue.NEW));
        assertEquals(DefaultSolicitude.ACCEPTED_STATE, instance.getState(StateValue.ACCEPTED));
        assertEquals(DefaultSolicitude.VERIFIED_STATE, instance.getState(StateValue.VERIFIED));
        assertEquals(DefaultSolicitude.NEW_STATE, instance.getState(StateValue.NEW));
        Exception e = assertThrows(NullPointerException.class,
                ()->{instance.setState(null);});
        assertEquals("invalid state", e.getMessage());
    }

    /**
     * Test of restore method, of class DefaultSolicitude.
     */
    @Disabled("tested in TestGenerateSolicitudeData")
    @Test
    public void testRestore() {
        System.out.println("restore");
        SolicitudeData data = null;
        DefaultSolicitude instance = null;
        instance.restore(data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateSolicitudeData method, of class DefaultSolicitude.
     */
    @Test
    public void testGenerateSolicitudeData() {
        System.out.println("generateSolicitudeData");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertNull(instance.generateSolicitudeData());
        SolicitudeData data = new SolicitudeData("request", Instant.now());
        assertGenerateRestore(instance, data);
        data = new SolicitudeData.Delayed("delayedRequestId", Delay.OTHER, Instant.now().minusSeconds(88888));
        assertGenerateRestore(instance, data);
        DownloadRegistryImpl dr = getDoneRegistry();
        data = new SolicitudeData.Verified("a request id", Instant.now(), 88, dr);
        assertGenerateRestore(instance, data);
    }
    
    protected void assertGenerateRestore(DefaultSolicitude instance, SolicitudeData data) {
        System.out.println(data);
        instance.restore(data);
        assertEquals(data.toString(), instance.generateSolicitudeData().toString());
    }

    /**
     * Test of download method, of class DefaultSolicitude.
     */
    @Disabled("tested manually with connection")
    @Test
    public void testDownload() throws Exception {
        System.out.println("download");
        SOAPConnection conn = null;
        DefaultSolicitude instance = null;
        instance.download(conn);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRequestable method, of class DefaultSolicitude.
     * able requestable
     * true true
     * true false
     * false true
     * false false
     */
    @Test
    public void testIsRequestable() {
        System.out.println("isRequestable");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        instance.setState(new FixedState(true));
        assertTrue(instance.isAble());
        assertTrue(instance.isRequestable());
        
        instance.setState(new FixedState(false));
        assertTrue(instance.isAble());
        assertFalse(instance.isRequestable());
        
        instance.pause();
        assertFalse(instance.isAble());
        assertFalse(instance.isRequestable());
        
        instance.setState(new FixedState(true));
        assertFalse(instance.isAble());
        assertFalse(instance.isRequestable());
    }

    /**
     * Test of isVerifiable method, of class DefaultSolicitude.
     */
    @Test
    public void testIsVerifiable() {
        System.out.println("isVerifiable");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        System.out.println(instance);
        instance.setState(new FixedState(true));
        System.out.println(instance);
        assertTrue(instance.isAble());
        assertTrue(instance.isVerifiable());
        
        instance.setState(new FixedState(false));
        assertTrue(instance.isAble());
        assertFalse(instance.isVerifiable());
        
        instance.pause();
        assertFalse(instance.isAble());
        assertFalse(instance.isVerifiable());
        
        instance.setState(new FixedState(true));
        assertFalse(instance.isAble());
        assertFalse(instance.isRequestable());
    }

    /**
     * Test of isDownloadable method, of class DefaultSolicitude.
     */
    @Test
    public void testIsDownloadable() {
        System.out.println("isDownloadable");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        instance.setState(new FixedState(true));
        assertTrue(instance.isAble());
        assertTrue(instance.isDownloadable());
        
        instance.setState(new FixedState(false));
        assertTrue(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance.pause();
        assertFalse(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance.setState(new FixedState(true));
        assertFalse(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance = new DefaultSolicitudeSub(DefaultSolicitudeSub.DONE_FLAGS);
        instance.setState(new FixedState(true));
        assertTrue(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance.setState(new FixedState(false));
        assertTrue(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance.pause();
        assertFalse(instance.isAble());
        assertFalse(instance.isDownloadable());
        
        instance.setState(new FixedState(true));
        assertFalse(instance.isAble());
        assertFalse(instance.isDownloadable());
    }

    protected static class FixedState extends DownloadState {
        final boolean xable;
        public FixedState(boolean xable) {
            this.xable = xable;
        }
        @Override public boolean isRequestable() { return xable; }
        @Override public boolean isVerifiable() { return xable; }
        @Override public boolean isDownloadable() { return xable; }
        
        @Override public StateValue getValue() { return null; }
    }
    
    /**
     * Test of setPaused method, of class DefaultSolicitude.
     */
    @Test
    public void testSetPaused() {
        System.out.println("setPaused");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertFalse(instance.isPaused());
        instance.setPaused(true);
        assertTrue(instance.isPaused());
        instance.setPaused(false);
        assertFalse(instance.isPaused());
    }

    /**
     * Test of isAble method, of class DefaultSolicitude.
     */
    @Test
    public void testIsAble() {
        System.out.println("isAble");
        DefaultSolicitude instance = new DefaultSolicitudeSub();
        assertTrue(instance.isAble());
        SatResponse reject = new SolicitaResponse(Instant.now(), "5555", "rejected", "");
        instance.setReject(reject);
        assertFalse(instance.isAble());
        instance.setReject(null);
        assertTrue(instance.isAble());
        instance.pause();
        assertFalse(instance.isAble());
        instance.setReject(reject);
        assertFalse(instance.isAble());
    }

    /**
     * Test of toString method, of class DefaultSolicitude.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        System.out.println(new DefaultSolicitudeSub());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected static class DefaultSolicitudeSub extends DefaultSolicitude {
        static final QueryMap QUERY = QueryMap.builder().setFolio("rfcx").build();
        static final DMTClient CLIENT = new ClientTest.ClientMock();
        
        static final PackageIds IDS = new PackageIds(List.of("aaa", "bbb", "ccc"));
        static final boolean[] FLAGS = new boolean[] {true, false, true};
        static final boolean[] DONE_FLAGS = new boolean[] {true, true, true};
        
        
        public DefaultSolicitudeSub() {
            this(null);
        }
        public DefaultSolicitudeSub(boolean[] flags) {
            super(CLIENT, QUERY);
            if (flags != null) {
                registry = new VerifiedRegistry(new DownloadRegistryImpl(IDS, flags));
            }
        }
    }
    
    protected static class DefaultSolicitudePre extends DefaultSolicitudeSub {

        static final SatResponse REJECT = 
                new SolicitaResponse(Instant.now(), "5002", "Solicitud rechazada", "requestIDX");
        static final String REQUEST_ID = "abc-def-ghi";
        static final Delay DELAY = Delay.ACCEPTED;
        static final int CFDIS = 888888877;
        static final Instant LAST_ACCEPTED = Instant.now();
   
        public DefaultSolicitudePre() {
            reject = REJECT;
            requestId = REQUEST_ID;
            delay = DELAY;
            cfdis = CFDIS;
            lastAccepted = LAST_ACCEPTED;
        }
   

    }
        

}