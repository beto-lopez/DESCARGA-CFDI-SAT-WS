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
 * Provides digital signature related methods needed for the WS this package was
 * made for.
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2024.10.31
 * @since 1.0
 * 
 * 
 */
public class SvcSignatureFactory  {
    /**
     * URI of c14 transform algorithm
     */
    public static final String XML_EXEC_C14 = "http://www.w3.org/2001/10/xml-exc-c14n#";

    /**
     * Reference to XMLSignatureFactory
     */
    protected XMLSignatureFactory signatureFactory;
    
    /**
     * Creates a new SvcSignatureFactory with default configuration
     */
    public SvcSignatureFactory() {
        this(XMLSignatureFactory.getInstance("DOM"));
    }
    
    /**
     * Creates a new SvcSignatureFactory with the specified signatureFactory
     * @param signatureFactory the signature factory to use
     * @throws IllegalArgumentException if signatureFactory is null
     */
    public SvcSignatureFactory(XMLSignatureFactory signatureFactory) {
        if (signatureFactory == null) {
            throw new IllegalArgumentException("xml signature factory required");
        }
        this.signatureFactory = signatureFactory;
    }
    
    /**
     * Returns a default implementation of SvcSignatureFactory.
     * 
     * @return a default implementation of SvcSignatureFactory
     */
    public static SvcSignatureFactory getInstance() {
        return new SvcSignatureFactory();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the XMLSignatureFactory used by this factory.
     * 
     * @return the XMLSignatureFactory used by this factory
     */
    public XMLSignatureFactory getXMLSignatureFactory() {
        return signatureFactory;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the <code>XMLSignature</code> used for authentication on the
     * specified parameters.
     * 
     * @param uri the uri for the reference
     * @param tokenReference the Node with the tokenReference
     * @return the <code>XMLSignature</code> used for authentication on the
     *         specified parameters
     * @throws GeneralSecurityException if there were security related problems
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
     * Returns the <code>XMLSignature</code> used for generic requests messages
     * on the specified parameters.
     * 
     * @param uri reference uri
     * @param cert the certificate to use
     * @return the <code>XMLSignature</code> used for generic requests messages
     *         on the specified parameters
     * @throws GeneralSecurityException if there were security related problems
     * @throws NullPointerException if cert is null.
     */
    public XMLSignature newGenericSignature(String uri, X509Certificate cert) throws GeneralSecurityException {
        return signatureFactory.newXMLSignature(
                newGenericSignedInfo(newGenericReferenceList(uri)), newKeyInfo(cert));
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns reference list for the authentication signature on the specified uri.
     * 
     * @param uri the reference uri
     * @return a reference list for the authentication signature on the specified uri
     * @throws GeneralSecurityException if there were security related problems
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
     * Returns the reference list used for generic message signatures.
     * 
     * @param uri the uri
     * @return the reference list used for generic message signatures
     * @throws GeneralSecurityException if there were security related problems
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
     * Returns the SignedInfo to use for authentication message signatures.
     * 
     * @param refList list of references
     * @return the SignedInfo to use for authentication message signatures
     * @throws GeneralSecurityException if there were security related problems
     * @throws IllegalArgumentException if refList is null or empty
     */
    public SignedInfo newAuthSignedInfo(List<Reference> refList) throws GeneralSecurityException {
        return newStdExclusiveSignedInfo(refList);
    }
    
    /**
     * Returns the SignedInfo used for generic request message signatures.
     * 
     * @param refList list of references
     * @return the SignedInfo used for generic request messages
     * @throws GeneralSecurityException if there were security related problems
     * @throws IllegalArgumentException if refList is null or empty
     */
    public SignedInfo newGenericSignedInfo(List<Reference> refList) throws GeneralSecurityException {
        return newStdExclusiveSignedInfo(refList);
    }
    
    /**
     * Returns a SignedInfo using exclusive RSA_SHA1 with the specified reference list.
     * 
     * @param refList list of references
     * @return a SignedInfo using exclusive RSA_SHA1 with the specified reference list.
     * @throws GeneralSecurityException if there were security related problems
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
     * Returns the KeyInfo if using a Node.
     * 
     * @param node the node
     * @return the KeyInfo if using a Node
     * @throws NullPointerException if node is null.
     */
    public KeyInfo newKeyInfo(Node node) {
        return signatureFactory
                .getKeyInfoFactory()
                .newKeyInfo(Collections.singletonList(new DOMStructure(node)));
    }
    
    /**
     * Returns the KeyInfo of the specified certificate.
     * 
     * @param certificate the certificate
     * @return the KeyInfo of the specified certificate
     * @throws NullPointerException if certificate is null.
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
