/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.solicitude.batch;


import com.sicomsa.dmt.DMTService;
import com.sicomsa.dmt.DMTClient;
import com.sicomsa.dmt.Client;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.CredentialsProxy;
import com.sicomsa.dmt.CredentialsStore;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.util.SvcParseException;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Iterator;

import java.io.File;
import java.io.IOException;
import java.io.StreamCorruptedException;

import jakarta.xml.soap.SOAPException;
/**
 * <code>BatchFactory</code> provides a <code>Builder</code> to create a <code>Batch</code>
 * instance with download requests along with a file; which you can use with this
 * factory's {@link BatchFactory#load(java.io.File)} method to load a <code>Batch</code>
 * instance in the state it concluded last time executed.
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.01.02
 * @since 1.0
 * 
 *  
 */
public class BatchFactory {
    
    /**
     * Service to call web service 
     */
    protected DMTService service;
    
    /**
     * Credentials store of this factory
     */
    protected CredentialsStore store;
    
    /**
     * Client map used to avoid multiple instances of same client within this factory.
     */
    protected Map<String,DMTClient> clientMap;
    
    /**
     * Creates a new <code>BatchFactory</code> with the specified parameters.
     * 
     * @param service service to use
     * @param store <code>CredentialsStore</code> to load credentials from
     * @throws IllegalArgumentException if service or store are null
     */
    public BatchFactory(DMTService service, CredentialsStore store) {
        if (service == null || store == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.service = service;
        this.store = store;
        clientMap = new HashMap<>();
    }
    
    /**
     * Returns a new instance of <code>Builder</code>.
     * 
     * @return a new instance of <code>Builder</code>
     */
    public Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a new <code>Batch</code> instance with information read from
     * the specified file.
     * <p>File must have been priorly created through the method {@link Builder#build(java.io.File) }</p>
     * 
     * @param fileName name of file to read from
     * @return <code>Batch</code> loaded from the specified file
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if fileName is null
     * @throws SvcParseException if file version not compatible or other parsing
     *         problems arose
     */
    public Batch load(String fileName) throws SOAPException, IOException {
        return load(new File(fileName));
    }
    
    /**
     * Creates a new <code>Batch</code> instance with information read from
     * the specified file.
     * <p>File must have been priorly created through the method {@link Builder#build(java.io.File) }</p>
     * 
     * @param file to read from
     * @return <code>Batch</code> loaded from the specified file
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if file is null
     * @throws SvcParseException if file version not compatible or other parsing
     *         problems arose
     */
    public Batch load(File file) throws SOAPException, IOException {
        try {
            BatchReader reader = BatchReader.read(file);
            Map<Long,BatchSolicitude> map = buildSolicitudeMap(reader);
            return new Batch(file, map);
        }
        catch (SvcParseException e) {
            throw new IOException("Error while parsing batch file", e);
        }
    }

    /**
     * Creates a <code>BatchSolicitude</code> map with iterators provided from
     * the specified <code>BatchReader</code>,
     * 
     * @param reader <code>BatchReader</code> to read from
     * @return a <code>BatchSolicitude</code> map
     * @throws SOAPException if there were any SOAP problems.
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if reader is null
     * @throws SvcParseException if parsing problems arose
     */
    protected Map<Long,BatchSolicitude> buildSolicitudeMap(BatchReader reader)
            throws SOAPException, IOException {
        Map<Long,BatchSolicitude> map = buildSolicitudeMap(reader.getRequests()); //throws soap
        if (map.isEmpty()) {
            throw new StreamCorruptedException("no requests found in batch file");
        }
        Iterator<EventResponse> iterator = reader.getResponses(); //throws soap, svc
        while (iterator.hasNext()) {
            EventResponse response = iterator.next();
            BatchSolicitude  solicitude = map.get(response.getProcessId());
            if (solicitude == null) {
                throw new StreamCorruptedException("downloadProcess not found, id:"
                        +response.getProcessId());
            }
            switch (response.getType()) {
                case "SolicitaResponse"->
                    solicitude.batchUpdate((SolicitaResponse)response.getResponse());
                case "VerificaResponse"->
                    solicitude.batchUpdate((VerificaResponse)response.getResponse());
                case "DescargaResponse"->
                    solicitude.batchUpdate((DescargaResponse)response.getResponse());
                default-> 
                    throw new StreamCorruptedException("invalid response type:"
                            +response.getType()+",id:"+response.getProcessId());
            }
        }
        return map;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * <code>BatchFactory.Builder</code> is used to create a <code>Batch</code>
     * instance along with its corresponding file.
     * <p>You add requests to the builder and when done you build the batch
     * providing the file were it will be saved and later loaded from.</p>
     * <p>Each request you add must have a unique processId within the <code>Batch</code>,
     * with which you will be able to identify the request within the batch.</p>
     * 
     * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
     * @version 2025.01.02
     * @since 1.0
     */
    public class Builder {
        
        /**
         * Map of solicitudes
         */
        protected LinkedHashMap<Long,BatchSolicitude> map;
        
        /**
         * Creates a new <code>Builder</code>,
         */
        public Builder() {
            map = new LinkedHashMap<>();
        }
        
        /**
         * Adds a request with the specified parameters to this <code>Builder</code>'s
         * instance.
         * <p>each request must have a unique <code>processId</code> within the
         * <code>Batch</code> this <code>Builder</code> will create.</p>
         * 
         * @param processId unique request identifier within this
         *                  <code>Builder</code> instance
         * @param rfc RFC of request to add
         * @param query <code>Query</code> of request to add
         * @return this <code>Builder</code>
         * @throws IllegalArgumentException if rfc or query are null, or if
         *         there is already another request with the specified processId.
         */
        public Builder addRequest(long processId, String rfc, Query query) {
            if (rfc == null || query == null) {
                throw new IllegalArgumentException("invalid parameters");
            }
            if (map.putIfAbsent(
                    processId, newSolicitude(rfc, query, processId)) != null) {
                throw new IllegalArgumentException("batchId must be unique among batch requests:"+processId);
            }
            return this;
        }
        
        /**
         * Creates and formats a file with the requests added to this builder,
         * and assigns it to a new batch that will be created to contain the
         * added requests.
         * 
         * @param fileName name of file to create
         * @return a new <code>Batch</code> containing the added requests.
         * @throws IOException if an I/O error occurs
         * @throws SOAPException if there were any SOAP problems.
         * @throws NullPointerException if fileName is null
         * @throws IllegalStateException if no requests have been added
         */
        public Batch build(String fileName) throws IOException, SOAPException {
            return build(new File(fileName)); //throws nullptrex
        }
        
        /**
         * Creates and formats a file with the requests added to this builder,
         * and assigns it to a new batch that will be created to contain the
         * added requests.
         * 
         * @param file file to create
         * @return a new <code>Batch</code> containing the added requests.
         * @throws IOException if an I/O error occurs
         * @throws SOAPException if there were any SOAP problems.
         * @throws IllegalStateException if no requests have been added
         */
        public Batch build(File file) throws IOException, SOAPException {
            if (map.isEmpty()) {
                throw new IllegalStateException("can not build batch without requests");
            }
            try (BatchWriter writer = new BatchWriter(file)) { ///throws FileNotFoundEx
                writer.formatFile(Collections.unmodifiableCollection(map.values()).iterator());
            }
            return new Batch(file, map);
        }
        
    } //Builder
    
    ///////////////////////////////////////////////////////////////////////////
   
    /**
     * This method iterates the specified <code>BatchRequest</code> iterator to
     * produce a new <code>LinkedHashMap</code> populated with newly created
     * <code>BatchRequests</code> using information extracted from the iterated
     * batch requests.
     * 
     * @param iterator iterator to scan
     * @return <code>Map</code> of new <code>BatchSolicitudes</code>
     */
    protected Map<Long,BatchSolicitude> buildSolicitudeMap(Iterator<BatchRequest> iterator) {
        if (!iterator.hasNext()) {
            return java.util.Collections.emptyMap();
        }
        LinkedHashMap<Long,BatchSolicitude> map = new LinkedHashMap<>();
        do {
            BatchRequest request = iterator.next();
            BatchSolicitude solicitude =
                newSolicitude(request.getRfc(), request.getQuery(), request.getId());
            map.put(solicitude.getBatchId(), solicitude);
        }
        while (iterator.hasNext());
        return map;
    }
    
    /**
     * Returns a new <code>BatchSolicitude</code> with the specified parameters.
     * 
     * @param rfc RFC of client
     * @param query <code>Query</code> of the solicitude
     * @param id batch identifier of the solicitude
     * @return a new <code>BatchSolicitude</code> with the specified parameters
     * @throws NullPointerException if rfc is null
     * @throws IllegalArgumentException if query is null
     */
    protected BatchSolicitude newSolicitude(String rfc, Query query, long id) {
        rfc = rfc.toUpperCase(); //throws nullpointerex
        return new BatchSolicitude(getClient(rfc), query, id);
    }

    /**
     * Returns a <code>DMTClient</code> instance with the corresponding rfc.
     * <p>Client instances are kept in a map to avoid having multiple instances
     * of the same Client in a <code>Batch</code>.</p>
     * 
     * @param rfc client's rfc
     * @return a <code>DMTClient</code> instance with the corresponding rfc
     * @throws IllegalArgumentException if rfc is null
     */
    protected DMTClient getClient(String rfc) {
        DMTClient client = clientMap.get(rfc);
        if (client == null) {
            client = new Client(getCredentials(rfc), service);
            clientMap.put(rfc, client);
        }
        return client;
    }
    
    /**
     * Returns the <code>Credentials</code> of the specified rfc.
     * 
     * @param rfc selected rfc
     * @return the <code>Credentials</code> of the specified rfc.
     * @throws IllegalArgumentException if rfc is null
     */
    protected Credentials getCredentials(String rfc) {
        return new StoredCredentials(rfc);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * <code>CredentialsProxy</code>'s concrete implementation that loads credentials
     * when needed using the {@link com.sicomsa.dmt.CredentialsStore} property
     * of its parent <code>BatchFactory</code> class.
     */
    protected class StoredCredentials extends CredentialsProxy {
        
        /**
         * Creates a new <code>StoredCredentials</code> for the specified RFC.
         * @param rfc RFC owner of this credentials
         * @throws IllegalArgumentException if rfc is null
         */
        public StoredCredentials(String rfc) {
            super(rfc);
        }
    
        @Override protected Credentials doGetCredentials() {
            return store.getCredentials(rfc);
        }
    }

}
