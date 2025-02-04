/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.10.27
 *
 *
 */
public class VerificaResponseTest {
    
    static Instant instant;
    static String statusCode;
    static String message;
    static int state;
    static String solicitudeStsCode;
    static String requestId;
    static int cfdis;
    static PackageIds packageIds;
    static VerificaResponse validResponse, delayResponse, rejectedResponse;
    static VerificaResponse noInfoResponse;
     
     
    public VerificaResponseTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        instant = Instant.now();
        statusCode = SatResponse.STATUS_CODE_ACCEPT;
        message = "messageValue";
        state = VerificaResponse.TERMINADA;
        requestId = "request identification id";
        cfdis = 77;
        solicitudeStsCode = SatResponse.STATUS_CODE_ACCEPT;
        packageIds = new PackageIds(List.of("packageId1", "packageId2"));
        validResponse = new VerificaResponse(instant, statusCode, message, state, solicitudeStsCode, requestId, cfdis, packageIds);
        delayResponse = new VerificaResponse(instant, statusCode, message, VerificaResponse.EN_PROCESO, solicitudeStsCode, requestId);
        rejectedResponse = new VerificaResponse(instant, statusCode, message, state, "8888", requestId);
        noInfoResponse = 
                new VerificaResponse(instant, statusCode, message,
                        VerificaResponse.RECHAZADA, VerificaResponse.NO_INFO_FOUND_STS_CODE, requestId);
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
     * Test of getRequestState method, of class VerificaResponse.
     */
    @Test
    public void testGetSolicitudeState() {
        System.out.println("getSolicitudeState");
        assertEquals(state, validResponse.getSolicitudeState());
    }

    /**
     * Test of getPackageIds method, of class VerificaResponse.
     */
    @Test
    public void testGetPackageIds() {
        assertEquals(packageIds, validResponse.getPackageIds());
        assertNull(delayResponse.getPackageIds());
        assertNull(rejectedResponse.getPackageIds());
        assertNull(noInfoResponse.getPackageIds());  
    }

    /**
     * Test of getRquestStatusCode method, of class VerificaResponse.
     */
    @Test
    public void testGetSolicitudeStsCode() {
        System.out.println("getSolicitudeStsCode");
        assertEquals(solicitudeStsCode, validResponse.getSolicitudeStsCode());
    }
   
    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsAccept() {
        System.out.println("isAccept");
        assertTrue(validResponse.isAccept());
        assertTrue(delayResponse.isAccept());
        assertFalse(rejectedResponse.isAccept());
        assertFalse(noInfoResponse.isAccept());
        assertEquals(shouldBeAccept(validResponse), validResponse.isAccept());
        assertEquals(shouldBeAccept(delayResponse), delayResponse.isAccept());
        assertEquals(shouldBeAccept(rejectedResponse), rejectedResponse.isAccept());
        assertEquals(shouldBeAccept(noInfoResponse), noInfoResponse.isAccept());
    }
    
    /**
     * @param response
     * @return 
     */
    protected boolean shouldBeAccept(VerificaResponse response) {
        return (response.isDelay() || response.isDownloadable());
    }
    
    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsDelay() {
        System.out.println("isDelay");
        assertFalse(validResponse.isDelay());
        assertTrue (delayResponse.isDelay());
        assertFalse(rejectedResponse.isDelay());
        assertFalse(noInfoResponse.isDelay());
        assertEquals(validResponse.isAccept(), SatResponse.STATUS_CODE_ACCEPT.equals(validResponse.getStatusCode()));
    }
    /**
     * Test of isAccept method, of class SatResponse.
     */
    @Test
    public void testIsNoInfoFound() {
        System.out.println("isNoInfoFound");
        assertFalse(validResponse.isNoInfoFound());
        assertFalse(delayResponse.isNoInfoFound());
        assertFalse(rejectedResponse.isNoInfoFound());
        assertTrue(noInfoResponse.isNoInfoFound());
    }
    
    /**
     * Test of toString method, of class VerificaResponse.
     */
    @Test
    public void testToString() {
        System.out.println("toString Valid:"+validResponse.toString());
        System.out.println("toString Delay:"+delayResponse.toString());
        System.out.println("toString Reject:"+rejectedResponse.toString());
        System.out.println("toString NoInfo:"+noInfoResponse.toString());
    }

    @Test
    public void testBuilder() {
        System.out.println("testBuilder");
        testBuilder(validResponse);
        testBuilder(delayResponse);
        testBuilder(rejectedResponse);
        testBuilder(noInfoResponse);
    }
    
    protected void testBuilder(VerificaResponse response) {
        VerificaResponse.Builder builder = new VerificaResponse.Builder();
        PackageIds info = response.getPackageIds();
        
        builder.setSatInstant(response.getInstant())
                .setStatusCode(response.getStatusCode())
                .setMessage(response.getMessage())
                .setRequestId(response.getRequestId())
                .setSolicitudeState(response.getSolicitudeState())
                .setSolicitudeStsCode(response.getSolicitudeStsCode())
                .setCfdisAmmount(response.getCfdis());
        if (info != null) {
            for (int idx = 0; idx < info.size(); idx++) {
                builder.addPackageId(info.getPackageId(idx));
            }
        }
        VerificaResponse built = builder.build();
        testSameContent(response, built);
        
    }
    
    protected void testSameContent(VerificaResponse r0, VerificaResponse r1) {
        System.out.println("testSameContent");
        assertEquals(r0.getInstant(), r1.getInstant());
        assertEquals(r0.getStatusCode(), r1.getStatusCode());
        assertEquals(r0.getMessage(), r1.getMessage());
        assertEquals(r0.getSolicitudeState(), r1.getSolicitudeState());
        assertEquals(r0.getSolicitudeStsCode(), r1.getSolicitudeStsCode());
        assertEquals(r0.getCfdis(), r1.getCfdis());
        assertSameContent(r0.getPackageIds(), r1.getPackageIds());
        assertEquals(r0.toString(), r1.toString());
    }
    
    protected void assertSameContent(PackageIds i0, PackageIds i1) {
        if (i0 == null || i1 == null) {
            assertEquals(i0, i1);
        }
        else {
            assertEquals(i0.size(), i1.size());
            for (int idx = 0; idx < i0.size(); idx++) {
                assertEquals(i0.getPackageId(idx), i1.getPackageId(idx));
            }
        }
    }
    

}