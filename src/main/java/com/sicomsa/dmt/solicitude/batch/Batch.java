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
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

/***
 * <code>Batch</code> downloads all available digital certificates (CFDIs) from
 * the <code>BatchSolicitude</code>s it receives through a map; and saves the
 * download status of each solicitude in a file that can be uploaded later and
 * continue each download process where it left off.
 * <p>This is usually done in parts and at different times since the web service
 * generally does not have all the information ready to be downloaded instantly.</p>
*
 *
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.02
 * @since 1.0
 * 
 */
public class Batch {
    
    /**
     * Batch's file
     */
    protected File file;
    
    /**
     * Map of batch solicitudes
     */
    protected Map<Long,Solicitude> batchMap; ///unmodifiableMap
    
    /**
     * Creates a new <code>Batch</code> that will write web servide responses to
     * the specified file, and will able to download <code>BatchSolicitude</code>s
     * included in the specified map.
     * <p>The specified file must exist and have been build by a {@link BatchFactory}.</p>
     * @param file existing file built from a <code>BatchFactory</code>.
     * @param batchMap map of batch solicitudes.
     * @throws IllegalArgumentException if file of batchMap are null
     */
    public Batch(File file, Map<Long,BatchSolicitude> batchMap) {
        if (file == null || batchMap == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.file = file;
        this.batchMap = Collections.unmodifiableMap(batchMap); ///creo que esto deberia tronar.
    }
    
    /**
     * Returns the file of this <code>Batch</code>.
     * 
     * @return the file of this <code>Batch</code>
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the <code>Solicitude</code> whithin this <code>Batch</code> whose
     * identifier matches the specified id, or null if not found.
     * 
     * @param id identifier to seek
     * @return the <code>Solicitude</code> whithin this <code>Batch</code> whose
     *         identifier mathces the specified id, or null if not found
     */
    public Solicitude getSolicitude(long id) {
        return batchMap.get(id);
    }
    
    /**
     * Returns an iterator of the batch identifier of all <code>Solicitude</code>s
     * within this <code>Batch</code>.
     * 
     * @return an iterator of all <code>Solicitude</code>s included in this
     *         <code>Batch</code>
     */
    public Iterator<Long> batchIds() {
        return batchMap.keySet().iterator();
    }
    
    /**
     * Returns an iterator of all <code>Solicitude</code>s included in this
     * <code>Batch</code>.
     * 
     * @return an iterator of all <code>Solicitude</code>s included in this
     *         <code>Batch</code>
     */
    public Iterator<Solicitude> solicitudes() {
        return batchMap.values().iterator();
    }
    
    /**
     * Returns true if this <code>Batch</code> has any pending solicitude.
     * 
     * @return true if this <code>Batch</code> has any pending solicitude
     */
    public boolean isPending() {
        return batchMap.values().stream().anyMatch(solicitude->solicitude.isPending());
    }
    
    /**
     * Tries to download all pending solicitudes of this batch, saving all
     * solicitude's states and responses to its file so it can be reloaded in
     * the future with another <code>Batch</code> instance.
     * 
     * @param conn <code>SOAPConnection</code> to use
     * @throws BatchException if there was a batch error
     * @throws NullPointerException if conn is null
     */
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
    
    /**
     * Tries to download all pending solicitudes of this batch, writing all
     * responses received from the web service in this batch's file.
     * <p>This method will halt the download process of any solicitude that was
     * paused, delayed or rejected. And will continue with the rest until
     * they are halted or all of its certificates have been downloaded.</p>
     *  
     * @param conn <code>SOAPConnection</code> to use
     * @throws BatchException if any problems arose while downloading a solicitude.
     * @throws SOAPException if there were any SOAP problems
     * @throws IOException if there was an I/O error
     * @throws IllegalArgumentException if conn is null
     */
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
    
    /**
     * Tries to download all pending solicitudes of this batch.
     * <p>This method will halt the download process of any solicitude that was
     * paused, delayed or rejected. And will continue with the rest until
     * they are halted or all of its certificates have been downloaded.</p>
     * <p>The specified listener will be added to all the solicitudes that
     * will be downloaded, and will be removed from them before this method
     * exits.</p>
     * 
     * @param conn <code>SOAPConnection</code> to use
     * @param listener to add to solicitudes to download
     * @throws BatchException if any problems arose while downloading a solicitude.
     * @throws IllegalArgumentException if conn is null
     */
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
    
    /**
     * Checks certificate's validity of all clients with pending solicitudes
     * in this Batch.
     * 
     * @throws CertificateExpiredException if any client's certifiate is expired
     * @throws CertificateNotYetValidException if any client's certiticate is not yet valid
     */
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
    
    /**
     * Checks the validity of the specified client's certificate.
     * 
     * @param client client to validate
     * @throws CertificateExpiredException if client's certifiate is expired
     * @throws CertificateNotYetValidException if client's certiticate is not yet valid
     * @throws NullPointerException if client is null
     */
    protected void checkCertificate(DMTClient client) 
            throws CertificateExpiredException, CertificateNotYetValidException {
        
        client.getCertificate().checkValidity();
    }
    
    /**
     * Returns the text to use a batch comment using the specified text.
     * 
     * @param text text to include to the comment
     * @return the text to use a batch comment using the specified text
     */
    protected String getBatchComment(String text) {
        return new StringBuilder(text)
                .append(" @LocalDT(")
                .append(format(LocalDateTime.now()))
                .append(")").toString();
    }
    
    /**
     * Formats this date-time using the {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}
     * formatter.
     * 
     * @param time <code>LocalDateTime</code> to format
     * @return formatted date-time string, not null
     * @throws NullPointerException if time is null
     * @throws DateTimeException if an error occurs during printing
     */
    protected String format(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Implementation of <code>DownloadListener</code> that uses a specified
     * <code>BatchWriter</code> to write all download events that are notified
     * to it.
     * <p>If there is any <code>SOAPException</code> or <code>IOException</code>
     * throw while writing to the batch file, a <code>DownloadEventException</code>
     * will be re-thrown wrapping the original cause of the exception.</p>
     */
    protected static class DownloadHandler implements DownloadListener {
        
        /**
         * Writer of this handler.
         */
        protected BatchWriter writer;
        
        /**
         * Creates a new <code>DownloadHandler</code> that will use the
         * specified writer to write the events heard.
         * 
         * @param writer <code>BatchWriter</code> to write events with
         */
        public DownloadHandler(BatchWriter writer) {
            if (writer == null) {
                throw new NullPointerException();
            }
            this.writer = writer;
        }
        
        /**
         * Invokes this handler's {@link DownloadHandler#write(com.sicomsa.dmt.solicitude.DownloadEvent) }
         * method to write the specified download event.
         * 
         * @param de <code>DownloadEvent</code> object
         * @throws DownloadEventException if there were problems while writing
         *         the event.
         */
        @Override public void stateChanged(DownloadEvent de) {
            write(de);
        }

        /**
         * Releases reference from this handler's writer
         */
        public void dispose() {
            writer = null;
        }
        
        /**
         * Writes the specified <code>DownloadEvent</code> to the batch file
         * through this handler's <code>BatchWriter</code>.
         * 
         * @param de the download event to write
         * @throws IllegalArgumentException if de is null
         * @throws DownloadEventException if there were problems while writing
         *         the event.
         */
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
