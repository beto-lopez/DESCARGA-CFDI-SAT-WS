/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;




import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.*;

import java.util.Base64;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

 
/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.24
 *
 *
 */
public class LocalRepositoryTest {
    
    static Base64.Encoder encoder;
    static LocalRepositoryBridge bridge;
    static final Logger LOG = Logger.getLogger("com.sicomsa");
    
    public LocalRepositoryTest() {
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        encoder = Base64.getEncoder();
        bridge = new LocalRepositoryBridge();
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
    
    public static class ConsoleRepository extends LocalRepository {
        
        @Override protected void save(File file, byte[] data) throws IOException {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                out.write(data);
                out.flush();
                out.writeTo(System.out);
            }
            System.out.println();
        }
        
    
    }
    
  
    /**
     * Test of decode method, of class LocalRepository.
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testDecode() throws UnsupportedEncodingException {
        System.out.println("decode");
        LocalRepository repository = new LocalRepository();
        String string = "<a String before encoding>";
        String encoded = Base64.getEncoder().encodeToString(string.getBytes());
        byte[] array = repository.decode(encoded);
        String decoded = new String(array, "UTF-8");
        System.out.println("string="+string+",encoded="+encoded+",decoded="+decoded);
        assertEquals(string, decoded);   
    }

    /**
     * Test of getFile method, of class LocalRepository.
     */
    @Test
    public void testGetFile() {
        System.out.println("getFile");
        LocalRepository repository = new LocalRepository();
        String userHome = System.getProperty("user.home");
        String rfc = "rfc1";
        String packageId ="packageId1";
        String zip = repository.getZipFileName(rfc, packageId);
        File expected = new File(userHome, zip);
        System.out.println("expecting:"+expected.getPath());
        assertEquals(expected.getPath(), bridge.getFile(rfc, packageId).getPath());
        File selected = new File("my selected dir");
        expected = new File(selected, zip);
        LocalRepository tmp = new LocalRepository();
        tmp.setDownloadDirectory(selected);
        System.out.println("expecting:"+expected.getPath());
        assertEquals(expected.getPath(), tmp.getFile(rfc, packageId).getPath());
    }

    /**
     * Test of defineDirectory method, of class LocalRepository.
     */
    @Test
    public void testDefineDirectory() {
        System.out.println("defineDirectory");
        LocalRepository repository = new LocalRepository();
        String userHome = System.getProperty("user.home");
        File homeFile = new File(userHome);
        System.out.println("homeFile="+homeFile.getPath());
        assertEquals(homeFile.getPath(), bridge.defineDirectory().getPath());
        File dir = repository.findDownloadsDirectory(userHome);
        System.out.println("downloads dir="+dir);
        if (dir == null) { ///downloads not found
            assertEquals(homeFile.getPath(), repository.defineDirectory().getPath());
        }
        else {
            assertEquals(dir.getPath(), repository.defineDirectory().getPath());
        }
        File setDir = new File(userHome, "testDemo");
        System.getProperties().setProperty(LocalRepository.DOWNLOAD_PATH_PROPERTY, setDir.getPath());
        assertEquals(setDir.getPath(), repository.defineDirectory().getPath());
        System.out.println("set directory ="+setDir.getPath());
        
        ///clearing
        System.getProperties().setProperty(LocalRepository.DOWNLOAD_PATH_PROPERTY, "");
    }

    /**
     * Test of save method, of class LocalRepository.
     */
    @Test
    public void testSave_4args() throws IOException {
        System.out.println("save_4args");
        LocalRepository repository = new ConsoleRepository();
        String rfc = "DMTSaveTest1";
        String packageId = "ABCD-EFGH-IJKL-MNOPQ-RSTUVWXYZ";
        String packageContent = "CONTENT-OF=THE_ENCODEDxPACKAGE";
        String encodedPackage = generateEncodedPackage("abcd.txt", packageContent);
        Object params = null;
        repository.save(rfc, packageId, encodedPackage, params);
    }
    
    protected String getEncodedString(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes());
    }

    /**
     * Test of save method, of class LocalRepository.
     */
    @Test
    public void testSave_File_byteArr() throws Exception {
        System.out.println("save_byteArr");
        LocalRepository repository = new ConsoleRepository();
        File file = repository.getDownloadDirectory();
        file = new File(file.getPath(),"DMTSaveTest2.save.test.txt");
        System.out.println("saving to file:"+file.getPath());
        String packageContent = "CONTENT-OF=THE_ENCODEDxPACKAGE-anotherExample";
        repository.save(file, repository.decode(getEncodedString(packageContent)));
    }

    /**
     * Test of getZipFileName method, of class LocalRepository.
     */
    @Test
    public void testGetZipFileName() {
        System.out.println("getZipFileName");
        LocalRepository repository = new LocalRepository();
        String[] rfcs = new String[] {"", "   ", "rfc1"};
        String[] pkgs = new String[] {"", "   ", "package1"};
        for (String rfc : rfcs) {
            for (String packageId : pkgs) {
                if (rfc == null || rfc.isBlank() || packageId == null || packageId.isBlank()) {
                    Exception e = assertThrows(IllegalArgumentException.class, ()-> {
                        repository.getZipFileName(rfc, packageId);
                    });
                    assertEquals("rfc nor packageId can be null in order to produce a valid file name", e.getMessage());
                }
                else {
                    assertDoesNotThrow(()->{
                        repository.getZipFileName(rfc, packageId);
                    });
                }
            }
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public static class LocalRepositoryBridge extends LocalRepository {
        
        @Override
        protected String[] downloadsDirNames() {
            return new String[] {"abc", "def", "ghi"};
        }
    }
    
    //////////////////////////////////////////////////////////////////////////
    //String fileName = "abcd.txt";
    protected String generateEncodedPackage(String fileName, String content) throws IOException {
       System.out.println("generateEncodedPackage");
       try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            try (ZipOutputStream out = new ZipOutputStream(fos)) {
                ZipEntry entry = new ZipEntry(fileName);
                out.putNextEntry(entry);
                byte[] data = content.getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            return encoder.encodeToString(fos.toByteArray());
        }
    }

}