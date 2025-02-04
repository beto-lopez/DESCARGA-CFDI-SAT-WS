/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.19
 *
 *
 */
public class SolicitudeDataTest {
    
    static Instant now;
    
    static String requestId;
    static Delay delay;
    
    static boolean[] all3True;
    static boolean[] all3False;
    static boolean[] notDoneFlags;
    
    static PackageIds ids;
    static DownloadRegistry verifiedRegistry;
    static DownloadRegistry doneRegistry;
    static DownloadRegistry notDoneRegistry;
    static int cfdis;
    
    static SolicitudeData accepted;
    static SolicitudeData delayed;
    static SolicitudeData verified;
    static SolicitudeData done;
    static SolicitudeData notDone;
    
    

    public SolicitudeDataTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        now = Instant.now();
        
        requestId = "reqid";
        delay = Delay.IN_PROGRESS;
        
        cfdis = 122;
        all3True = new boolean[]{ true, true, true};
        all3False = new boolean[3];
        notDoneFlags = new boolean[] {true, true, false};
        
        ids = new PackageIds(List.of("abc", "def", "ghi"));
        
        verifiedRegistry = new DownloadRegistryImpl(ids, all3False);
        doneRegistry = new DownloadRegistryImpl(ids, all3True);
        notDoneRegistry = new DownloadRegistryImpl(ids, notDoneFlags);


        accepted = new SolicitudeData(requestId, now);
        delayed = new SolicitudeData.Delayed(requestId, delay, now);
        verified = new SolicitudeData.Verified(requestId, now, cfdis, verifiedRegistry);
        done  = new SolicitudeData.Verified(requestId, now, cfdis, doneRegistry);
        notDone  = new SolicitudeData.Verified(requestId, now, cfdis, notDoneRegistry);
        
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
     * Test of getStateValue method, of class SolicitudeData.
     */
    @Test
    public void testGetStateValue() {
        System.out.println("getStateValue");
        assertEquals(StateValue.ACCEPTED, accepted.getStateValue());
        assertEquals(StateValue.DELAYED, delayed.getStateValue());
        assertEquals(StateValue.VERIFIED, verified.getStateValue());
        assertEquals(StateValue.VERIFIED, done.getStateValue());
        assertEquals(StateValue.VERIFIED, notDone.getStateValue());
    }

    /**
     * Test of getRequestId method, of class SolicitudeData.
     */
    @Test
    public void testGetRequestId() {
        System.out.println("getRequestId");
        assertEquals(requestId, accepted.getRequestId());
        assertEquals(requestId, delayed.getRequestId());
        assertEquals(requestId, verified.getRequestId());
        assertEquals(requestId, done.getRequestId());
        assertEquals(requestId, notDone.getRequestId());
    }

    /**
     * Test of getLastAccepted method, of class SolicitudeData.
     */
    @Test
    public void testGetLastAccepted() {
        System.out.println("getLastAccepted");
        assertEquals(now, accepted.getLastAccepted());
        assertEquals(now, delayed.getLastAccepted());
        assertEquals(now, verified.getLastAccepted());
        assertEquals(now, done.getLastAccepted());
        assertEquals(now, notDone.getLastAccepted());
    }

    /**
     * Test of getDelay method, of class SolicitudeData.
     */
    @Test
    public void testGetDelay() {
        System.out.println("getDelay");
        assertNull(accepted.getDelay());
        assertEquals(delay, delayed.getDelay());
        assertNull(verified.getDelay());
        assertNull(done.getDelay());
        assertNull(notDone.getDelay());
    }

    /**
     * Test of getCfdis method, of class SolicitudeData1.
     */
    @Test
    public void testGetCfdis() {
        System.out.println("getCfdis");
        assertEquals(0, accepted.getCfdis());
        assertEquals(0, delayed.getCfdis());
        assertEquals(cfdis, verified.getCfdis());
        assertEquals(cfdis, done.getCfdis());
        assertEquals(cfdis, notDone.getCfdis());
    }

    
    
    @Test
    public void testToString() {
        System.out.println("testToString");
        System.out.println(accepted.toString());
        System.out.println(delayed.toString());
        System.out.println(verified.toString());
        System.out.println(done.toString());
        System.out.println(notDone.toString());
    }

    @Test
    public void testConstructors() {
        System.out.println("testConstructors");
        String ip = "invalid parameters";
        testIllegalArgument(()->{invalidRequestData1();}, ip); 
        testIllegalArgument(()->{invalidRequestData2();}, ip); 
        testIllegalArgument(()->{invalidInstantData();}, ip); 
        
        testIllegalArgument(()->{invalidDelayed();}, "delay required in this constructor");
        
        testIllegalArgument(()->{invalidRegistry();}, "download registry is required");
        testIllegalArgument(()->{invalidCfdis();}, "invalid cfdis ammount");

    }
                
    public SolicitudeData invalidRequestData1() {
        return new SolicitudeData(null, now);
    }
    public SolicitudeData invalidRequestData2() {
        return new SolicitudeData("  ", now);
    }
    public SolicitudeData invalidInstantData() {
        return new SolicitudeData("asdfad", null);
    }
    public SolicitudeData.Delayed invalidDelayed() {
        return new SolicitudeData.Delayed("asdf", null, now);
    }
    public SolicitudeData.Verified invalidRegistry() {
        return new SolicitudeData.Verified("asd", now, 10, null);
    }
    public SolicitudeData.Verified invalidCfdis() {
        return new SolicitudeData.Verified("asd", now, -1, doneRegistry);
    }

    public void testIllegalArgument(Executable ex, String message) {
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        assertEquals(e.getMessage(), message);
    }

    /**
     * Test of getPackageIds method, of class SolicitudeData.
     */
    @Test
    public void testGetPackageIds() {
        System.out.println("getPackageIds");
        assertNull(accepted.getPackageIds());
        assertNull(delayed.getPackageIds());
        assertEquals(verifiedRegistry.getPackageIds(), verified.getPackageIds());
        assertEquals(doneRegistry.getPackageIds(), done.getPackageIds());
        assertEquals(notDoneRegistry.getPackageIds(), notDone.getPackageIds());
    }

    /**
     * Test of isDownloaded method, of class SolicitudeData.
     */
    @Test
    public void testIsDownloaded() {
        System.out.println("isDownloaded");
        assertMatches(all3False, accepted);
        assertMatches(all3False, delayed);
        assertMatches(all3False, verified);
        assertMatches(all3True, done);
        assertMatches(notDoneFlags, notDone);
    }
    
    protected void assertMatches(boolean[] flags, SolicitudeData data) {
        for (int idx = 0; idx < flags.length; idx++) {
            assertEquals(flags[idx], data.isDownloaded(idx));
        }
    }

    /**
     * Test of isDownloadDone method, of class SolicitudeData.
     */
    @Test
    public void testIsDownloadDone() {
        System.out.println("isDownloadDone");
        assertFalse(accepted.isDownloadDone());
        assertFalse(delayed.isDownloadDone());
        assertFalse(verified.isDownloadDone());
        assertTrue(done.isDownloadDone());
        assertFalse(notDone.isDownloadDone());
    }

    /**
     * Test of getNextDownloadablePackageId method, of class SolicitudeData.
     */
    @Test
    public void testGetNextDownloadablePackageId() {
        System.out.println("getNextDownloadablePackageId");
        assertEquals(nextId(accepted), accepted.getNextDownloadablePackageId());
        assertEquals(nextId(delayed), delayed.getNextDownloadablePackageId());
        assertEquals(nextId(verified), verified.getNextDownloadablePackageId());
        assertEquals(nextId(done), done.getNextDownloadablePackageId());
        assertEquals(nextId(notDone), notDone.getNextDownloadablePackageId());
    }
    
    protected static String nextId(SolicitudeData data) {
        PackageIds ids = data.getPackageIds();
        if (ids != null) {
            for (int idx = 0; idx < ids.size(); idx++) {
                if (!data.isDownloaded(idx)) {
                    return ids.getPackageId(idx);
                }
            }
        }
        return null;
    }


}