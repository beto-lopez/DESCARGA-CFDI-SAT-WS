/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;

import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.solicitude.Solicitude;
import com.sicomsa.dmt.solicitude.DownloadEvent;
import com.sicomsa.dmt.solicitude.DownloadListener;

import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.WebServiceException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.02
 * 
 * Batch downloads all available cfdis from the requests (solicitudes) it receives
 * through a map; and saves each solicitude download state in a file that can
 * later be loaded and continue each download process where it left off.
 * 
 * This needs to be done in parts and at different times since SAT's service
 * usually does not have all the information ready to be downloaded instantly.
 * 
 */
public class Batch {
    
    protected File file;
    
    protected Map<Long,Solicitude> batchMap; ///unmodifiableMap
    
    /**
     * @param file
     *          file must be a preformatted file by a BatchWriter.
     * @param batchMap 
     */
    public Batch(File file, Map<Long,BatchSolicitude> batchMap) {
        if (file == null || batchMap == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.file = file;
        this.batchMap = Collections.unmodifiableMap(batchMap); ///creo que esto deberia tronar.
    }
    
    public File getFile() {
        return file;
    }

    public Solicitude getSolicitude(long id) {
        return batchMap.get(id);
    }
    
    public Iterator<Long> batchIds() {
        return batchMap.keySet().iterator();
    }
    
    public Iterator<Solicitude> solicitudes() {
        return batchMap.values().iterator();
    }
    
    public boolean isPending() {
        return batchMap.values().stream().anyMatch(solicitude->solicitude.isPending());
    }
    
    public void download(SOAPConnection conn) throws BatchException {
        if (conn == null) {
            throw new NullPointerException("null connection");
        }
        if (!isPending()) {
            return;
        }
        try {
            verifyCredentials(); //will load all pending credentials before connecting to SAT
            doDownload(conn);
        }
        catch (SOAPException | IOException | GeneralSecurityException e) {
            throw new BatchException(e.getMessage(), e);
        }
    }
    
    protected void doDownload(SOAPConnection conn)
            throws BatchException, SOAPException, IOException {
        
        try (BatchWriter writer = new BatchWriter(file)) {
            writer.writeComment(getBatchComment("Batch begin"));
            DownloadHandler handler = new DownloadHandler(writer);
            try {
                downloadAllPending(conn, handler);
                writer.writeComment(getBatchComment("Batch end"));
            }
            finally {
                handler.dispose();
            }
        }
    }
    
    protected void downloadAllPending(SOAPConnection conn, DownloadListener listener)
        throws BatchException {
        
        Iterator<Solicitude> iterator = this.solicitudes();
        while (iterator.hasNext()) {
            BatchSolicitude solicitude = (BatchSolicitude)iterator.next();
            if (solicitude.isPending()) {
                try {
                    solicitude.addDownloadListener(listener);
                    solicitude.download(conn);
                }
                catch (SOAPException | WebServiceException e) {
                    throw new BatchException("Error while downloading batchId:"
                            +solicitude.getBatchId(), e);
                }
                finally {
                    solicitude.removeDownloadListener(listener);
                }
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    
    protected void verifyCredentials() 
            throws CertificateExpiredException, CertificateNotYetValidException {
        HashMap<String,DMTClient> clientMap = new HashMap<>();
        batchMap.values().forEach(solicitude-> {
            if (solicitude.isPending()) {
                DMTClient client = solicitude.getClient();
                clientMap.put(client.getRfc().toUpperCase(), client);
            } 
        });
        Iterator<DMTClient> iterator = clientMap.values().iterator();
        while (iterator.hasNext()) {
            checkCertificate(iterator.next());
        }
    }
    
    protected void checkCertificate(DMTClient client) 
            throws CertificateExpiredException, CertificateNotYetValidException {
        
        client.getCertificate().checkValidity();
    }
    
    protected String getBatchComment(String text) {
        return new StringBuilder(text)
                .append(" @LocalDT(")
                .append(format(LocalDateTime.now()))
                .append(")").toString();
    }
    
    protected String format(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    ////////////////////////////////////////////////////////////////////////////
        
    protected static class DownloadHandler implements DownloadListener {
        protected BatchWriter writer;
        
        public DownloadHandler(BatchWriter writer) {
            if (writer == null) {
                throw new NullPointerException();
            }
            this.writer = writer;
        }
        
        @Override public void stateChanged(DownloadEvent de) {
            write(de);
        }

        public void dispose() {
            writer = null;
        }
        
        protected void write(DownloadEvent de) {
            try {
                writer.writeResponse(de);
            }
            catch (SOAPException | IOException e) {
                throw new DownloadEventException(e.getMessage(), e);
            }
        }
        
    }//handler
    ///////////////////////////////////////////////////////////////////////////
    
}
