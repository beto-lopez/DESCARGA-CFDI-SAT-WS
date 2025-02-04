/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import com.sicomsa.dmt.RealCredentials;

import java.io.File;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2025.02.02
 * 
 * To enable this test you must provide your own certificates directory, cert
 * file names, password and RFC.
 *
 */
@Disabled("To enable test define DIR, CERT_FIE, KEY_FILE, PWD AND RFC")
public class CertUtilsTest {
    static final String DIR = "cert file directory"; 
    static final String CERT_FILE = "cert file name.cer";
    static final String KEY_FILE = "private key file name.key";
    
    static final String PWD ="your private key password";
    static final String RFC ="your rfc";
    
    static String certFileName;
    static String keyFileName;
    
    public CertUtilsTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        certFileName = new File(DIR, CERT_FILE).getAbsolutePath();
        keyFileName  = new File(DIR, KEY_FILE).getAbsolutePath();
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
     * Test of loadCredentials method, of class CertUtils.
     */
    @Test
    public void testLoadCredentials() {
        System.out.println("loadCredentials");
        String rfc = RFC;
        String certFile = certFileName;
        String keyFile = keyFileName;
        char[] pwd = PWD.toCharArray();
        RealCredentials creds = CertUtils.loadCredentials(rfc, certFile, keyFile, pwd);
        assertNotNull(creds);
    }

    /**
     * Test of loadKeyEntry method, of class CertUtils.
     */
    @Test
    public void testLoadAsKeyStoreEntry() {
        System.out.println("loadAsKeyStoreEntry");
        String certFile = certFileName;
        String keyFile = keyFileName;
        char[] pwd = PWD.toCharArray();
        KeyStore.PrivateKeyEntry result = CertUtils.loadAsKeyStoreEntry(certFile, keyFile, pwd);
        assertNotNull(result);
    }
    /**
     * Test of loadCertificate method, of class CertUtils.
     */
    @Test
    public void testLoadCertificate_String() throws Exception {
        System.out.println("loadCertificate");
        String fileName = certFileName;
        X509Certificate result = CertUtils.loadCertificate(fileName);
        assertNotNull(result);
    }

    /**
     * Test of loadCertificate method, of class CertUtils.
     */
    @Test
    public void testLoadCertificate_File() throws Exception {
        System.out.println("loadCertificate");
        File file = new File(certFileName);
        X509Certificate result = CertUtils.loadCertificate(file);
        assertNotNull(result);
    }

    /**
     * Test of loadCertificate method, of class CertUtils.
     */
    @Test
    public void testLoadCertificate_InputStream() throws Exception {
        System.out.println("loadCertificate");
        try (FileInputStream in = new FileInputStream(certFileName)) {
            X509Certificate result = CertUtils.loadCertificate(in);
            assertNotNull(result);
        }
    }

    /**
     * Test of loadPrivateKey method, of class CertUtils.
     */
    @Test
    public void testLoadPrivateKey_String_charArr() throws Exception {
        System.out.println("loadPrivateKey");
        String fileName = keyFileName;
        char[] pwd = PWD.toCharArray();
        PrivateKey result = CertUtils.loadPrivateKey(fileName, pwd);
        assertNotNull(result);
    }

    /**
     * Test of loadPrivateKey method, of class CertUtils.
     */
    @Test
    public void testLoadPrivateKey_File_charArr() throws Exception {
        System.out.println("loadPrivateKey");
        File file = new File(keyFileName);
        char[] pwd = PWD.toCharArray();
        PrivateKey result = CertUtils.loadPrivateKey(file, pwd);
        assertNotNull(result);
    }

    /**
     * Test of loadPrivateKey method, of class CertUtils.
     */
    @Test
    public void testLoadPrivateKey_byteArr_charArr() throws Exception {
        System.out.println("loadPrivateKey");
        File file = new File(keyFileName);
        byte[] data = Files.readAllBytes(file.toPath());
        char[] pwd = PWD.toCharArray();
        PrivateKey result = CertUtils.loadPrivateKey(data, pwd);
        assertNotNull(result);
    }


}