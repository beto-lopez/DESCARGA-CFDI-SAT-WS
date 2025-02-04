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
 
import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.function.Executable;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.01.18
 *
 *
 */
public class PackageIdsTest {
    
    static PackageIds ids;
    static List<String> list;

    public PackageIdsTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        list = List.of("abc", "def", "ghi", "jkl");
        ids = new PackageIds(list);
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
     * Test of size method, of class PackageIds.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        PackageIds instance = new PackageIds(list);
        int expResult = list.size();
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of indexOf method, of class PackageIds.
     */
    @Test
    public void testIndexOf() {
        System.out.println("indexOf");
        PackageIds instance = new PackageIds(list);
        for (int idx = 0; idx < list.size(); idx++) {
            assertEquals(idx, instance.indexOf(list.get(idx)));
        }
    }

    /**
     * Test of getPackageId method, of class PackageIds.
     */
    @Test
    public void testGetPackageId() {
        System.out.println("getPackageId");
        PackageIds instance = new PackageIds(list);
        for (int idx = 0; idx < list.size(); idx++) {
            assertEquals(list.get(idx), instance.getPackageId(idx));
        }
    }

    /**
     * Test of toString method, of class PackageIds.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        PackageIds instance = new PackageIds(list);
        System.out.println("listToString="+list.toString());
        System.out.println("PackageIds.toString="+instance);
    }
    
    @Test
    public void testConstructors() {
        System.out.println("testConstructors");
 
        testIllegalArgument(()->{nullList();}, "non empty list is required"); 
        testIllegalArgument(()->{emptyList();}, "non empty list is required"); 
        List<String> fixedList = List.of("alkjflkj", "2009329", "2390mvx");
        
        
        ArrayList<String> mutableList = new ArrayList<>(fixedList);
        PackageIds pids = new PackageIds(mutableList);
        assertSameList(fixedList, pids);
        mutableList.add("new string");
        assertSameList(fixedList, pids);
        mutableList.remove(0);
        assertSameList(fixedList, pids);
    }
    
    protected void assertSameList(List<String> list, PackageIds ids) {
        for (int idx = 0; idx < list.size(); idx++) {
            assertEquals(list.get(idx), ids.getPackageId(idx));
        }
    }
    
                
    public PackageIds nullList() {
        return new PackageIds(null);
    }
    public PackageIds emptyList() {
        return new PackageIds(java.util.Collections.emptyList());
    }

    public void testIllegalArgument(Executable ex, String message) {
        Exception e = assertThrows(IllegalArgumentException.class, ex);
        assertEquals(e.getMessage(), message);
    }
}