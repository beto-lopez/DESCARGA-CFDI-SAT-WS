/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */

package com.sicomsa.dmt.svc;


import com.sicomsa.dmt.Authorization;
import com.sicomsa.dmt.DescargaResponse;
import com.sicomsa.dmt.Query;
import com.sicomsa.dmt.SolicitaResponse;
import com.sicomsa.dmt.VerificaResponse;
import com.sicomsa.dmt.PackageIds;
import com.sicomsa.dmt.Credentials;
import com.sicomsa.dmt.util.SOAPUtils;


import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.UUID;
import java.io.IOException;
import java.time.Clock;
import java.time.InstantSource;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;



/**
 *
 * @author https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198
 *
 * @since 2024.11.24
 *
 * 
 */
public class DownloadServiceMock {
    protected SOAPMessage invalidTokenMsg;
    protected DMTServiceMock serviceMock;
    protected List<Cfdi> cfdiList;
    protected SatMock sat;
    protected Base64.Encoder encoder;
    
    protected LocalDateTime fromDate;
    protected LocalDateTime toDate;
    
    protected boolean reject = false;
    protected boolean delay = false;

    public DownloadServiceMock() {
        serviceMock = new DMTServiceMock();
        sat = new SatMock();
        cfdiList = new ArrayList<>();
        fromDate = LocalDateTime.now().minusMonths(12);
        int days = 365;
        toDate = fromDate.plusDays(days);
        for (int idx = 0; idx < 100; idx++) {
            int random = (int)(Math.random() * (double)days);
            LocalDateTime date = fromDate.plusDays(random);
            String content = "cfdi #"+idx+" at "+date;
            cfdiList.add(new Cfdi(UUID.randomUUID().toString(), date, content));
        }
        encoder = Base64.getEncoder();
    }
    
    public synchronized void setReject(boolean reject) {
        this.reject = reject;
    }
    
    public synchronized void setDelay(boolean delay) {
        this.delay = delay;
    }
    
    public DMTServiceMock getServiceMock() {
        return serviceMock;
    }
    
    public LocalDateTime getFromDate() {
        return fromDate;
    }
    public LocalDateTime getToDate() {
        return toDate;
    }

    @BeforeAll
    public static void setUpClass() {
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
    

    ////////////////////////////////////////////////////////////////////////////
    public static class Cfdi {
        protected String uuid;
        protected LocalDateTime emited;
        protected String receptor;
        protected String emisor;
        protected String content;
        
        public Cfdi(String uuid, LocalDateTime emited, String content) {
            this.uuid = uuid;
            this.emited = emited;
            this.content = content;
        }
        public String uuid() {
            return uuid;
        }
        
        public boolean inRange(LocalDateTime from, LocalDateTime to) {
            return !emited.isBefore(from) && !emited.isAfter(to);
        }
        @Override
        public String toString() {
            return new StringBuilder("cfdi{")
                    .append("uuid=").append(uuid)
                    .append(",emited:").append(emited)
                    .append(",receptor:").append(receptor)
                    .append(",emisor:").append(emisor)
                    .append(",content:").append(content)
                    .toString();
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    
    public class DMTServiceMock extends DownloadService {
        public DMTServiceMock() {
            autenticaSvc = new AuthMock(factory);
            solicitaSvc  = new SolicitaMock(factory);
            verificaSvc  = new VerificaMock(factory);
            descargaSvc  = new DescargaMock(factory);
        }
        
    }
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * AuthMock will not manage rfc's. Will just generate and validate tokens
     */
    protected class AuthMock extends AuthenticationSvc {
    
        public AuthMock(SvcMessageFactory context) {
            super(context);
        }
        @Override
        public synchronized Authorization callTheService(
                SOAPConnection connection, Credentials credentials, Object request, String token) throws SOAPException {
            System.out.println("AuthMock.callTheService");
            Authorization auth = sat.getAutorization(credentials);
            System.out.println("authorization:"+auth);
            return auth;
        }
    }
    ////////////////////////////////////////////////////////////////////////////
   
    protected class SolicitaMock extends SolicitaSvc {
        public SolicitaMock(SvcMessageFactory context) {
            super(context);
        }
        @Override
        public synchronized SolicitaResponse callTheService(
                SOAPConnection connection, Credentials credentials, Query query, String token) throws SOAPException {
            System.out.println("SolicitaMock.callTheService");
            if (reject) {
                return sat.getSolicitaReject();
            }
            if (sat.isValid(token)) {
                SolicitaResponse sr = sat.getSolicitaResponse(query);
                System.out.println(sr);
                return sr;
            }
            return parseReceivedMessage(sat.getInvalidTokenMessage(), getContext().instant(), query);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
  
    protected class VerificaMock extends VerificaSvc {
        public VerificaMock(SvcMessageFactory context) {
            super(context);
        }
        @Override
        public synchronized VerificaResponse callTheService(
                SOAPConnection connection, Credentials credentials, String requestId, String token) throws SOAPException {
            System.out.println("VerificaMock.callTheService");
            if (delay) {
                return sat.getVerificaDelay(requestId);
            }
            if (sat.isValid(token)) {
                VerificaResponse vr = sat.getVerificaResponse(requestId);
                System.out.println(vr);
                return vr;
            }
            return parseReceivedMessage(sat.getInvalidTokenMessage(), getContext().instant(), requestId);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    
    protected class DescargaMock extends DescargaSvc {
        public DescargaMock(SvcMessageFactory context) {
            super(context);
        }
        @Override
        public synchronized DescargaResponse callTheService(
                SOAPConnection connection, Credentials credentials, String packageId, String token) throws SOAPException {
            System.out.println("DescargaMock.callTheService");
            if (sat.isValid(token)) {
                try {
                    DescargaResponse dr = sat.getDescargaResponse(packageId);
                    System.out.println(dr);
                    return dr;
                }
                catch (IOException e) {
                    throw new SOAPException(e);
                }
            }
            return parseReceivedMessage(sat.getInvalidTokenMessage(), getContext().instant(), packageId);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    
    protected synchronized List<Cfdi> select(LocalDateTime from, LocalDateTime to) {
        System.out.println("selecting cfids from "+from+" to "+to);
        Iterator<Cfdi> iterator = cfdiList.iterator();
        ArrayList<Cfdi> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Cfdi cfdi = iterator.next();
            if (cfdi.inRange(from, to)) {
                result.add(cfdi);
                iterator.remove();
            }
        }
        System.out.println("found "+result.size()+" cfdis");
        return result;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    protected class SatMock {
        protected Map<String,Authorization> tokenMap;
        protected int requests = 0;
        protected InstantSource clock;
        protected long tokenDurationMillis;
        protected SOAPMessage invalidTokenMsg;
        protected Map<String,Query> requestMap;
        protected Map<String,CfdisPackage> packageMap;
        int packageSize = 4;
    
        public SatMock() {
            this(1000*60);
        }
        public SatMock(long tokenDurationMillis) {
            tokenMap = new HashMap<>();
            clock = Clock.systemUTC();
            this.tokenDurationMillis = tokenDurationMillis;
            requestMap = new HashMap<>();
            packageMap = new HashMap<>();
        }
        
        public synchronized Authorization getAutorization(Credentials credentials) {
            cleanup();
            Instant created = clock.instant();
            Instant expires = created.plusMillis(tokenDurationMillis);
            String token = UUID.randomUUID().toString();
            Authorization auth = new Authorization(created, created, expires, token);
            tokenMap.put(Authorization.wrapp(token), auth);
            return auth;      
        }
        
        public SolicitaResponse getSolicitaReject() {
            return new SolicitaResponse(
                    Instant.now(), "5555", "Rejected response message", "");
        }
        
        public VerificaResponse getVerificaDelay(String requestId) {
            return new VerificaResponse(
                Instant.now(),
                "5000", "Solicitud Aceptada", VerificaResponse.EN_PROCESO, "5000", requestId);
        }
        
        public synchronized boolean isValid(String wrappedToken) {
            return isValid(tokenMap.get(wrappedToken));
        }
        
        public synchronized SolicitaResponse getSolicitaResponse(Query query) {
            String requestId = UUID.randomUUID().toString();
            requestMap.put(requestId, query);
            return new SolicitaResponse(clock.instant(), "5000","Solicitud Aceptada", requestId);
        }
        
        public synchronized VerificaResponse getVerificaResponse(String requestId) {
            Query query = requestMap.remove(requestId); //hacer wraper y quitarlo en n veces de tries
            if (query == null) {
                return getRequestIdNotFoundMessage(requestId);
            }
            List<Cfdi> list = select(query.getFechaInicial(), query.getFechaFinal());
            return getResponse(requestId, list);
        }
        
        public synchronized DescargaResponse getDescargaResponse(String packageId) throws IOException {
            CfdisPackage cpack = packageMap.remove(packageId);
            if (cpack == null) {
                return getPackageNotFoundMessage(packageId);
            }
            return new DescargaResponse(clock.instant(), "5000", "", packageId, cpack.generateEncodedPackage());
        }
        /*
        
        
    public VerificaResponse(
            Instant satInstant,
            String statusCode,
            String message,
            int solicitudeState,
            String solicitudeStsCode,
            int cfdis,
            List<String> idList) {
        */
        protected VerificaResponse getRequestIdNotFoundMessage(String requestId) {
            return new VerificaResponse(clock.instant(), "5004", "No se encontró información", VerificaResponse.RECHAZADA, "5004", requestId);
        }
        protected DescargaResponse getPackageNotFoundMessage(String packageId) {
            return new DescargaResponse(clock.instant(), "5004", "No se encontró información", "", packageId);
        }
        protected VerificaResponse getResponse(String requestId, List<Cfdi> list) {
            if (list == null || list.isEmpty()) {
                return getNoInfoFoundResponse(requestId);
            }
            int cfdis = list.size();
            return new VerificaResponse(clock.instant(), "5000", "Solicitud Aceptada",
                    VerificaResponse.TERMINADA, "5000", requestId, cfdis, new PackageIds(getPackageList(list)));
        }
        protected VerificaResponse getNoInfoFoundResponse(String requestId) {
            return new VerificaResponse(
                    clock.instant(), "5000", "No se encontró información", VerificaResponse.RECHAZADA, "5004", requestId);
        }
        protected List<String> getPackageList(List<Cfdi> list) {
            List<String> packageList = new ArrayList<>();
            List<Cfdi> current = new ArrayList<>(Math.min(list.size(), packageSize));
            for (int idx = 0; idx < list.size(); idx++) {
                if (current.size() >= packageSize) {
                    String packageId = addPackage(current);
                    packageList.add(packageId);
                    current = new ArrayList<>(Math.min(packageSize, list.size()-idx+1));
                }
                current.add(list.get(idx));
            }
            if (!current.isEmpty()) {
                packageList.add(addPackage(current));
            }
            return packageList;
        }
        protected String addPackage(List<Cfdi> list) {
            String packageId = UUID.randomUUID().toString();
            CfdisPackage cp = new CfdisPackage(packageId, list);
            packageMap.put(packageId, cp);
            return packageId;
        }
        
        public SOAPMessage getInvalidTokenMessage() throws SOAPException {
            if (invalidTokenMsg == null) {
                String txt = """
                    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                    <s:Body xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                    <SolicitaDescargaResponse xmlns="http://DescargaMasivaTerceros.sat.gob.mx">
                    <SolicitaDescargaResult CodEstatus="300" Mensaje="Token invalido."/>
                    </SolicitaDescargaResponse>
                    </s:Body>
                    </s:Envelope>""";
                try {
                    invalidTokenMsg = SOAPUtils.fromString(txt);
                }
                catch (IOException e) {
                    throw new SOAPException(e);
                }
            }
            return invalidTokenMsg;
        }    
        protected boolean isValid(Authorization auth) {
            return (auth != null
                    && clock.instant().isBefore(auth.getExpires()));
        }
        protected void cleanup() {
            if (++requests > 50) {
                requests = 0;
                Iterator<Authorization> iterator = tokenMap.values().iterator();
                while (iterator.hasNext()) {
                    if (!isValid(iterator.next())) {
                        iterator.remove();
                    }
                }
            }
        }
    }
    
    protected class CfdisPackage {
        protected String packageId;
        protected List<Cfdi> list;
        
        public CfdisPackage(String packageId, List<Cfdi> list) {
            this.packageId = packageId;
            this.list = list;
        }
        public String generateEncodedPackage() throws IOException {
            System.out.println("generateEncodedPackage");
            try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                try (ZipOutputStream out = new ZipOutputStream(fos)) {
                    for (int idx = 0; idx < list.size(); idx++) {
                        addEntry(out, list.get(idx));
                    }
                }
                return encoder.encodeToString(fos.toByteArray());
            }
        }
        protected void addEntry(ZipOutputStream zos, Cfdi cfdi) throws IOException {
            ZipEntry entry = new ZipEntry(cfdi.uuid()+".txt");
            zos.putNextEntry(entry);
            byte[] data = cfdi.toString().getBytes();
            zos.write(data, 0, data.length);
            zos.closeEntry();
        }
    
    }
    
}