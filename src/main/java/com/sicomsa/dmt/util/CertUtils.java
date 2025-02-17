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
 * Helper methods for loading certificates and private keys from files.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.08.17
 * @since 1.0
 * 
 */
public final class CertUtils {
    
    /**
     * Creates RealCredentials from the contents of a certificate file
     * and a private key file with a given password.
     * 
     * @param rfc the RFC that the RealCredentials will have
     * @param certFile the file name of the file that contains the certificate
     * @param keyFile the file name of the file that contains the private key
     * @param pwd the password related to the private key
     * @return the RealCredentials with with a X509Certificate and a PrivateKey
     *         retrieved from a certificate file and a private key file with a
     *         given password and rfc.
     * @throws IllegalArgumentException if rfc is null or blank
     * @throws NullPointerException if certFile, keyFile or pwd are null
     * @throws RepositoryException if any other problem arose while creating the entry
     */
    public static RealCredentials loadCredentials(String rfc, String certFile,
            String keyFile, char[] pwd) {
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
     * Creates a KeyStore.PrivateKeyEntry from the contents of a certificate file
     * and a private key file with a given password.
     * 
     * @param certFile the file name of the file that contains the certificate
     * @param keyFile the file name of the file that contains the private key
     * @param pwd the password related to the private key
     * @return a KeyStore.PrivateKeyEntry with a X509Certificate and a PrivateKey
     *         retrieved from a certificate file and a private key file with a
     *         given password.
     * @throws NullPointerException if certFile, keyFile or pwd are null
     * @throws RepositoryException if any other problem arose while creating the entry
     */
    public static KeyStore.PrivateKeyEntry loadAsKeyStoreEntry(String certFile,
            String keyFile, char[] pwd) {
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
    
    ////////////////////////////////////////////////////////////////////////////
    /// Certificate loading
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a X509Certificate from the contents of a file with the specified
     * fileName.
     * 
     * @param fileName the fileName of the file to get the certificate from
     * @return a X509Certificate read from a file
     * @throws IOException if there were IO related problems
     * @throws CertificateException if there were Certificate related problems
     * @throws NullPointerException if fileName is null
     */
    public static X509Certificate loadCertificate(String fileName) throws IOException, CertificateException {
        return loadCertificate(new File(fileName));
    }
    
    /**
     * Creates a X509Certificate from the contents of a File.
     * 
     * @param file the file to get the certificate from
     * @return a X509Certificate read from a file
     * @throws IOException if there were IO related problems
     * @throws CertificateException if there were Certificate related problems
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
     * Creates a X509Certificate from an inputstream.
     * 
     * @param in the imputstream that contains the certificate
     * @return a X509Certificate build from the inputstream
     * @throws CertificateException if there were Certificate related problems
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
    /// Private Key loading
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a PrivateKey from the specified fileName and password.
     * 
     * @param fileName the fileName of the file containing the data of the private key
     * @param pwd the password related to the private key
     * @return a PrivateKey created from the specified file and password
     * @throws IOException if there was IO problems
     * @throws PKCSException if there were other conversion problems
     * @throws OperatorCreationException  if there were other problems
     * @throws NullPointerException if fileName or pwd are null
     */
    public static PrivateKey loadPrivateKey(String fileName, char[] pwd)
            throws IOException, PKCSException, OperatorCreationException {
        return loadPrivateKey(new File(fileName), pwd);
    }
    
    /**
     * Creates a PrivateKey from the specified file and password.
     * 
     * @param file the file containing the data of the private key
     * @param pwd the password related to the private key
     * @return a PrivateKey created from the specified file and password
     * @throws IOException if there was IO problems
     * @throws PKCSException if there were other conversion problems
     * @throws OperatorCreationException  if there were other problems
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
     * Creates a PrivateKey from the specified data and password.
     * 
     * @param data the data that contains the private key
     * @param pwd the password related to the private key
     * @return a PrivateKey created from the specified data and password
     * @throws IOException if there was IO problems
     * @throws PKCSException if there were other conversion problems
     * @throws OperatorCreationException  if there were other problems
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
