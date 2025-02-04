/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.

 */

package com.sicomsa.dmt.svc;

import java.util.Collections;
import java.util.List;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.XMLSignature;

import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.w3c.dom.Node;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2024.10.31
 * 
 * Provides methods that allow signature of envelopes.
 * 
 */
public class SvcSignatureFactory  {
    public static final String XML_EXEC_C14 = "http://www.w3.org/2001/10/xml-exc-c14n#";

    protected XMLSignatureFactory signatureFactory;
    
    public SvcSignatureFactory() {
        this(XMLSignatureFactory.getInstance("DOM"));
    }
    
    public SvcSignatureFactory(XMLSignatureFactory signatureFactory) {
        if (signatureFactory == null) {
            throw new IllegalArgumentException("xml signature factory required");
        }
        this.signatureFactory = signatureFactory;
    }
    
    public static SvcSignatureFactory getInstance() {
        return new SvcSignatureFactory();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public XMLSignatureFactory getXMLSignatureFactory() {
        return signatureFactory;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param uri
     * @param tokenReference
     * @return
     * @throws GeneralSecurityException 
     * @throws NullPointerException if tokenReference is null.
     */
    public XMLSignature newAuthSignature(String uri, Node tokenReference)
            throws GeneralSecurityException {
        
        return signatureFactory
                .newXMLSignature(
                        newAuthSignedInfo(newAuthReferenceList(uri)),
                        newKeyInfo(tokenReference)
                );
    }
    /**
     * 
     * @param uri
     * @param cert
     * @return
     * @throws GeneralSecurityException 
     * @throws NullPointerException if cert is null.
     */
    public XMLSignature newGenericSignature(String uri, X509Certificate cert) throws GeneralSecurityException {
        return signatureFactory.newXMLSignature(
                newGenericSignedInfo(newGenericReferenceList(uri)), newKeyInfo(cert));
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * @param uri
     * @return
     * @throws GeneralSecurityException 
     */
    public List<Reference> newAuthReferenceList(String uri) throws GeneralSecurityException {
        return Collections.singletonList(
                signatureFactory.newReference(
                        uri,
                        signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList(
                                signatureFactory.newTransform(
                                        XML_EXEC_C14, (TransformParameterSpec)null
                                )
                        ),
                        null,
                        null
                )
        );
    }
    /**
     * 
     * @param uri
     * @return
     * @throws GeneralSecurityException 
     */
    public List<Reference> newGenericReferenceList(String uri) throws GeneralSecurityException {
        return Collections.singletonList(
                signatureFactory.newReference(
                        uri,
                        signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList(
                                signatureFactory.newTransform(
                                        Transform.ENVELOPED,
                                        (TransformParameterSpec)null
                                )
                        ),
                        null, null
                )
        );
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param refList
     * @return
     * @throws GeneralSecurityException 
     * @throws IllegalArgumentException if refList is null or empty
     */
    public SignedInfo newAuthSignedInfo(List<Reference> refList) throws GeneralSecurityException {
        return newStdExclusiveSignedInfo(refList);
    }
    
    /**
     * 
     * @param refList
     * @return
     * @throws GeneralSecurityException 
     * @throws IllegalArgumentException if refList is null or empty
     */
    public SignedInfo newGenericSignedInfo(List<Reference> refList) throws GeneralSecurityException {
        return newStdExclusiveSignedInfo(refList);
    }
    
    /**
     * 
     * @param refList
     * @return
     * @throws GeneralSecurityException 
     * @throws IllegalArgumentException if refList is null or empty
     */
    public SignedInfo newStdExclusiveSignedInfo(List<Reference> refList) throws GeneralSecurityException {
        if (refList == null || refList.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
        }
        return signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(
                        CanonicalizationMethod.EXCLUSIVE,
                        (C14NMethodParameterSpec)null
                ),
                signatureFactory.newSignatureMethod(
                        SignatureMethod.RSA_SHA1, null
                ),
                refList
        );
    }
    
////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param node
     * @return 
     * @throws NullPointerException if node is null.
     */
    public KeyInfo newKeyInfo(Node node) {
        return signatureFactory
                .getKeyInfoFactory()
                .newKeyInfo(Collections.singletonList(new DOMStructure(node)));
    }
    /**
     * 
     * @param certificate
     * @return 
     * @throws NullPointerException if certificate is null.
     * 
     */
    public KeyInfo newKeyInfo(X509Certificate certificate) {
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        final X509IssuerSerial issuer =
                keyInfoFactory.newX509IssuerSerial(
                        certificate.getIssuerX500Principal().getName(),
                        certificate.getSerialNumber()
                );
        X509Data data = keyInfoFactory.newX509Data(java.util.List.of(issuer, certificate));
        return keyInfoFactory.newKeyInfo(Collections.singletonList(data));
    }
}
