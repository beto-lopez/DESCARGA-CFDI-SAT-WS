/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude;

import com.sicomsa.dmt.PackageIds;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
import java.util.List;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.29
 *
 *
 */
public class DownloadRegistryImplTest {
    
    static PackageIds ids;

    public DownloadRegistryImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        ids = new PackageIds(List.of("abc", "def", "ghi"));
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
     * Test of getPackageIds method, of class DownloadRegistryImpl.
     */
    @Test
    public void testGetPackageIds() {
        System.out.println("getPackageIds");
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids);
        PackageIds expResult = ids;
        PackageIds result = instance.getPackageIds();
        assertEquals(expResult, result);
    }

    /**
     * Test of isDownloaded method, of class DownloadRegistryImpl.
     */
    @Test
    public void testIsDownloaded() {
        System.out.println("isDownloaded");
        DownloadRegistryImpl instance = new MutableRegistry(ids);
        for (int idx = 0; idx < ids.size(); idx++) {
            assertFalse(instance.isDownloaded(idx));
            ((MutableRegistry)instance).setDownloaded(idx);
            assertTrue(instance.isDownloaded(idx));
        }
    }

    /**
     * Test of size method, of class DownloadRegistryImpl.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids);
        int expResult = ids.size();
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of indexOf method, of class DownloadRegistryImpl.
     */
    @Test
    public void testIndexOf() {
        System.out.println("indexOf");
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids);
        for (int idx = 0; idx < ids.size(); idx++) {
            String id = ids.getPackageId(idx);
            assertEquals(idx, instance.indexOf(id));
        }
    }

    /**
     * Test of getPackageId method, of class DownloadRegistryImpl.
     */
    @Test
    public void testGetPackageId() {
        System.out.println("getPackageId");
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids);
        for (int idx = 0; idx < ids.size(); idx++) {
            assertEquals(ids.getPackageId(idx), instance.getPackageId(idx));
        }
    }

    /**
     * Test of isDownloadDone method, of class DownloadRegistryImpl.
     */
    @Test
    public void testIsDownloadDone() {
        System.out.println("isDownloadDone");
        DownloadRegistryImpl instance = new MutableRegistry(ids);
        for (int idx = 0; idx < ids.size(); idx++) {
            assertFalse(instance.isDownloadDone());
            ((MutableRegistry)instance).setDownloaded(idx);
        }
        assertTrue(instance.isDownloadDone());
    }

    /**
     * Test of getNextDownloadablePackageId method, of class DownloadRegistryImpl.
     */
    @Test
    public void testGetNextDownloadablePackageId() {
        System.out.println("getNextDownloadablePackageId");
        DownloadRegistryImpl instance = new MutableRegistry(ids);
        for (int idx = 0; idx < ids.size(); idx++) {
            assertEquals(ids.getPackageId(idx), instance.getNextDownloadablePackageId());
            ((MutableRegistry)instance).setDownloaded(idx);
        }
        assertNull(instance.getNextDownloadablePackageId());
    }

    /**
     * Test of toString method, of class DownloadRegistryImpl.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids);
        System.out.println(instance);
    }
    
    @Test
    public void testConstructor() {
        System.out.println("Constructors");
        boolean[] flags = new boolean[ids.size()];
        DownloadRegistryImpl instance = new DownloadRegistryImpl(ids, flags);
        assertMatches(flags, instance);
        flags[0] = true;
        instance = new DownloadRegistryImpl(ids, flags);
        assertMatches(flags, instance);
        
        Exception e = assertThrows(IllegalArgumentException.class,
                ()-> {newNullIdsInstance();});
        assertEquals("packageIds is required", e.getMessage());
        
        e = assertThrows(IllegalArgumentException.class,
                ()-> {newInstance(ids, new boolean[ids.size()+2]);});
        assertEquals("inconsistent parameter sizes", e.getMessage());
        
        e = assertThrows(IllegalArgumentException.class,
                ()-> {newInstance(ids, new boolean[ids.size()-1]);});
        assertEquals("inconsistent parameter sizes", e.getMessage());
    }
    
    protected DownloadRegistryImpl newNullIdsInstance() {
        return new DownloadRegistryImpl(null);
    }
    
    protected DownloadRegistryImpl newInstance(PackageIds ids, boolean[] flags) {
        return new DownloadRegistryImpl(ids, flags);
    }
    
    
    
    protected void assertMatches(boolean[] flags, DownloadRegistryImpl instance) {
        for (int idx = 0; idx < flags.length; idx++) {
            assertEquals(flags[idx], instance.isDownloaded(idx));
        }
    }
            

    
    protected static class MutableRegistry extends DownloadRegistryImpl {
        private static final long serialVersionUID = 20241027L;
        
        public MutableRegistry(PackageIds ids) {
            super(ids);
        }
        
        public void setDownloaded(int index) {
            flags[index] = true;
        }

    }
    
}