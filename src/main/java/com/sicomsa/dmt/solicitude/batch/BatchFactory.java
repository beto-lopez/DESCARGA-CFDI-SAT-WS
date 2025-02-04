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
import com.sicomsa.dmt.RepositoryException;
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
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 * @since 2025.01.02
 * 
 * BatchFactory provides a Builder to create a Batch instance with download
 * requests along with a File, which you can use with the load method in order
 * to get that Batch instance in the state it concluded last time executed.
 *  
 */
public class BatchFactory {
    
    protected DMTService service;
    protected CredentialsStore store;
    protected Map<String,DMTClient> clientMap;
    
    public BatchFactory(DMTService service, CredentialsStore store) {
        if (service == null || store == null) {
            throw new IllegalArgumentException("invalid parameters");
        }
        this.service = service;
        this.store = store;
        clientMap = new HashMap<>();
    }
    
    public Builder builder() {
        return new Builder();
    }
    
    public Batch load(String fileName) throws SOAPException, IOException {
        return load(new File(fileName));
    }
    
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

    protected Map<Long,BatchSolicitude> buildSolicitudeMap(BatchReader reader)
            throws SOAPException, IOException, SvcParseException{
        Map<Long,BatchSolicitude> map = buildSolicitudeMap(reader.getRequests()); //throws soap, svc
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
     * BatchFactory.Builder is used to create a Batch instance along with its
     * corresponding file.
     * 
     * You add requests to the builder and when done you build the batch
     * providing the file were it will be saved and later loaded from.
     * 
     * Each request you add must have a unique processId, with which you will
     * be able to identify the request within the batch.
     * 
     */
    public class Builder {
        protected LinkedHashMap<Long,BatchSolicitude> map;
        
        public Builder() {
            map = new LinkedHashMap<>();
        }
        
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
        
        public Batch build(String fileName) throws IOException, SOAPException {
            return build(new File(fileName)); //throws nullptrex
        }
        
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
    
    protected BatchSolicitude newSolicitude(String rfc, Query query, long id) {
        rfc = rfc.toUpperCase(); //throws nullpointerex
        return new BatchSolicitude(getClient(rfc), query, id);
    }

    protected DMTClient getClient(String rfc) {
        DMTClient client = clientMap.get(rfc);
        if (client == null) {
            client = new Client(getCredentials(rfc), service);
            clientMap.put(rfc, client);
        }
        return client;
    }
    
    protected Credentials getCredentials(String rfc) {
        return new StoredCredentials(rfc);
    }
    
    protected class StoredCredentials extends CredentialsProxy {
        public StoredCredentials(String rfc) {
            super(rfc);
        }
    
        @Override protected Credentials doGetCredentials() throws RepositoryException {
            return store.getCredentials(rfc);
        }
    }

}
