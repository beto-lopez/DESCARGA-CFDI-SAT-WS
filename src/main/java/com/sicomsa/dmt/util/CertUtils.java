/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.util;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.operator.OperatorCreationException;
 
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.Security;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.sicomsa.dmt.RealCredentials;

import com.sicomsa.dmt.RepositoryException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.08.17
 * 
 * Methods for loading certificates and private keys from files.
 * 
 */
public final class CertUtils {
    
    /**
     * 
     * @param rfc
     * @param certFile
     * @param keyFile
     * @param pwd
     * @return
     * @throws RepositoryException
     */
    public static RealCredentials loadCredentials(String rfc, String certFile, String keyFile, char[] pwd)
            throws RepositoryException {
        try {
            X509Certificate cert = loadCertificate(certFile);
            PrivateKey pk = loadPrivateKey(keyFile, pwd);
            return new RealCredentials(rfc, cert, pk);
        }
        catch (IOException | CertificateException | PKCSException | OperatorCreationException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }
    
    /**
     * 
     * @param certFile
     * @param keyFile
     * @param pwd
     * @return
     * @throws RepositoryException 
     */
    public static KeyStore.PrivateKeyEntry loadAsKeyStoreEntry(String certFile, String keyFile, char[] pwd) 
            throws RepositoryException {
        try {
            Certificate[] chain = new Certificate[1];
            chain[0] = loadCertificate(certFile);
            PrivateKey pk = loadPrivateKey(keyFile, pwd);
            return new KeyStore.PrivateKeyEntry(pk, chain);
        }
        catch (IOException | CertificateException | PKCSException | OperatorCreationException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }
    
    /**
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @throws CertificateException 
     * @throws NullPointerException if fileName is null
     */
    public static X509Certificate loadCertificate(String fileName) throws IOException, CertificateException {
        return loadCertificate(new File(fileName));
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     * @throws CertificateException 
     * @throws NullPointerException if file is null
     */
    public static X509Certificate loadCertificate(File file) throws IOException, CertificateException {
        if (file == null) {
            throw new NullPointerException("can not load certificate with null file");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return loadCertificate(fis);
        }
    }
    
    /**
     * 
     * @param in
     * @return
     * @throws CertificateException 
     * @throws NullPointerException if in is null
     */
    public static X509Certificate loadCertificate(InputStream in) throws CertificateException {
        if (in == null) {
            throw new NullPointerException("cannot load certificate with null input stream");
        }
        return (X509Certificate)CertificateFactory.getInstance("X.509")
                .generateCertificate(in);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * 
     * @param fileName
     * @param pwd
     * @return
     * @throws IOException
     * @throws PKCSException
     * @throws OperatorCreationException 
     * @throws NullPointerException if fileName or pwd are null
     */
    public static PrivateKey loadPrivateKey(String fileName, char[] pwd)
            throws IOException, PKCSException, OperatorCreationException {
        return loadPrivateKey(new File(fileName), pwd);
    }
    
    /**
     * 
     * @param file
     * @param pwd
     * @return
     * @throws IOException
     * @throws PKCSException
     * @throws OperatorCreationException 
     * @throws NullPointerException if file or pwd are null
     */
    public static PrivateKey loadPrivateKey(File file, char[] pwd)
            throws IOException, PKCSException, OperatorCreationException {
        if (file == null || pwd == null) {
            throw new NullPointerException("invalid parameters");
        }
        return loadPrivateKey(Files.readAllBytes(file.toPath()), pwd);
    }
    
    /**
     * 
     * @param data
     * @param pwd
     * @return
     * @throws IOException
     * @throws PKCSException
     * @throws OperatorCreationException 
     * @throws NullPointerException if data or pwd are null
     */
    public static PrivateKey loadPrivateKey(byte[] data, char[] pwd)
            throws IOException, PKCSException, OperatorCreationException {
        if (data == null || pwd == null) {
            throw new NullPointerException("invalid parameters");
        }
        Security.addProvider(new BouncyCastleProvider());
        
        EncryptedPrivateKeyInfo encryptedKeyInfo = 
                EncryptedPrivateKeyInfo.getInstance(
                        ASN1Sequence.getInstance(data));
        
        PKCS8EncryptedPrivateKeyInfo pkcs8EncryptedPrivateKeyInfo =
                new PKCS8EncryptedPrivateKeyInfo(encryptedKeyInfo);

        InputDecryptorProvider decryptorProvider =
                new JceOpenSSLPKCS8DecryptorProviderBuilder().build(pwd);
        
        PrivateKeyInfo privateKeyInfo =
                pkcs8EncryptedPrivateKeyInfo.decryptPrivateKeyInfo(
                        decryptorProvider);
        
        return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
    }
    
}
