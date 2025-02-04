/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;

import java.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Node;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.12.01
 *
 * This class will be tested with real life interaction requests to sat.
 * This test is only intented to document exceptions in javadoc.
 *
 */
public class SvcSignatureFactoryTest {
    protected SvcSignatureFactory factory;

    public SvcSignatureFactoryTest() {
        factory = SvcSignatureFactory.getInstance();
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
     * Test of getXMLSignatureFactory method, of class SvcSignatureFactory.
     */
    @Test
    public void testGetXMLSignatureFactory() {
        System.out.println("getXMLSignatureFactory");
        XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
        SvcSignatureFactory ssf = new SvcSignatureFactory(xsf);
        assertEquals(xsf, ssf.getXMLSignatureFactory());
    }


    /**
     * Test of newAuthReferenceList method, of class SvcSignatureFactory.
     */
    @Test
    public void testNewAuthReferenceList() throws Exception {
        System.out.println("newAuthReferenceList");
        factory.newAuthReferenceList(null);
        factory.newAuthReferenceList("");
        factory.newAuthReferenceList("abc");
    }

    /**
     * Test of newGenericReferenceList method, of class SvcSignatureFactory.
     */
    @Test
    public void testNewGenericReferenceList() throws Exception {
        System.out.println("newGenericReferenceList");
        factory.newGenericReferenceList(null);
        factory.newGenericReferenceList("");
        factory.newGenericReferenceList("abc");       
    }

    /**
     * Test of newStdExclusiveSignedInfo method, of class SvcSignatureFactory.
     */
    @Test
    public void testNewStdExclusiveSignedInfo() throws Exception {
        System.out.println("newStdExclusiveSignedInfo");
        Exception e = assertThrows(IllegalArgumentException.class, ()-> {
            factory.newStdExclusiveSignedInfo(null);
        });
        assertEquals("list of references must contain at least one entry", e.getMessage());
        
        e = assertThrows(IllegalArgumentException.class, ()-> {
             factory.newStdExclusiveSignedInfo(java.util.Collections.emptyList());
        });
        assertEquals("list of references must contain at least one entry", e.getMessage());
       
    }

    /**
     * Test of newKeyInfo method, of class SvcSignatureFactory.
     */
    @Test
    public void testNewKeyInfo_Node() {
        System.out.println("newKeyInfo_Node");
        Exception e = assertThrows(NullPointerException.class, ()-> {
            factory.newKeyInfo((Node)null);
        });
        System.out.println(e.getMessage());
    }

    /**
     * Test of newKeyInfo method, of class SvcSignatureFactory.
     */
    @Test
    public void testNewKeyInfo_X509Certificate() {
        System.out.println("newKeyInfo_Cert");
        Exception e = assertThrows(NullPointerException.class, ()-> {
            factory.newKeyInfo((X509Certificate)null);
        });
        System.out.println(e.getMessage());
    }

}