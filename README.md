## Librerías en java para descarga masiva de CFDI's de SAT web service

Estas librearías son solo una parte de una aplicación de código abierto que queremos hacer para PYMES.  
Con estos paquetes puedes descargar todos tus CFDIs del servicio de descarga masiva SAT web service.

## Adecuada a versión 1.5 del WS de Descarga Masiva de Terceros (30 de mayo del 2025).

Puedes ver la documentación en esta liga: [Overview (com.sicomsa.dmt | DESCARGA-CFDI-SAT-WS)](https://beto-lopez.github.io/DESCARGA-CFDI-SAT-WS/).

### Ejemplo práctico para descargar cfdi´s emitidos y recibidos de un período.

    package DMTDemo;

    import com.sicomsa.dmt.Query;
    import com.sicomsa.dmt.util.QueryMap;
    import com.sicomsa.dmt.Credentials;
    import com.sicomsa.dmt.CredentialsStore;
    import com.sicomsa.dmt.DMTService;
    import com.sicomsa.dmt.RepositoryException;
    import com.sicomsa.dmt.solicitude.batch.Batch;
    import com.sicomsa.dmt.solicitude.batch.BatchFactory;
    import com.sicomsa.dmt.svc.LocalRepository;
    import com.sicomsa.dmt.svc.ServiceFactory;

    import com.sicomsa.dmt.util.CertUtils;
    import com.sicomsa.dmt.util.QueryBuilder;
    import com.sicomsa.dmt.util.FolioQueryBuilder;

    import java.io.File;
    import java.io.IOException;

    import jakarta.xml.soap.SOAPException;
    import jakarta.xml.soap.SOAPConnection;
    import jakarta.xml.soap.SOAPConnectionFactory;

    public class DMTUser {
        static final File DOWNLOAD_DIRECTORY = new File("C:/myDownloads");
        static final File BATCH_FILE  = new File(DOWNLOAD_DIRECTORY, "myBatchFile.xml");
    
        public static void main(String[] args) throws Exception {
            System.out.println("DMTUser.demo");
        
            DMTService service = ServiceFactory.newCfdiServiceFactory().newService();
            service.setRepository(new LocalRepository(DOWNLOAD_DIRECTORY));

            BatchFactory factory = new BatchFactory(service, new MyCredentialsStore());
            Batch batch;
            if (BATCH_FILE.exists()) {
                batch = factory.load(BATCH_FILE);
            }
            else {
                batch = buildBatch(factory, BATCH_FILE);
            }
            dump(batch);
            if (!batch.isPending()) {
                System.out.println("batch is not pending!");
                return;
            }
            try (SOAPConnection conn = SOAPConnectionFactory.newInstance().createConnection()) {
                batch.download(conn);
            }
            dump(batch);
        }
        
        protected Batch buildBatch(BatchFactory factory, File file) throws IOException, SOAPException {

            BatchFactory.Builder batchBuilder = factory.builder();
            String rfc = "valid rfc here";
        
            ///builds query with defaults
            QueryBuilder builder =
                new QueryBuilder()
                    .setRfcSolicitante(rfc)
                    .setFechaInicial(QueryMap.dayStart(2020, 6, 1))
                    .setFechaFinal(QueryMap.dayEnd(2020, 6, 15));
        
            ///builds query for emitted certificates
            Query emitidos = builder
                            .setRfcEmisor(rfc)
                            .setTipoSolicitud(QueryBuilder.TIPO_CFDI)
                            .setEstadoComprobante(QueryBuilder.ESTADO_VIGENTE)
                            .buildEmitidosQuery();       
        
            ///builds query for received certificates
            Query recibidos = builder
                            .setRfcEmisor("")
                            .setTipoSolicitud(QueryBuilder.TIPO_METADATA)
                            .setEstadoComprobante(QueryBuilder.ESTADO_TODOS)
                            .buildRecibidosQuery(rfc);
        
            ///builds query of certificate download by Id
            Query folio = new FolioQueryBuilder()
                .setFolio("C1FFD659-6E3A-WWWW-ZZZZ-ADFC396B2471")
                .setRfcSolicitante(rfc)
                .build();
       
            batchBuilder.addRequest(0, rfc, emitidos);
            batchBuilder.addRequest(1, rfc, recibidos);
            batchBuilder.addRequest(2, rfc, folio);
       
            return batchBuilder.build(file);
        }

        protected static void dump(Batch batch) {
            System.out.println("dumping batch");
            batch.solicitudes().forEachRemaining(solicitude-> {
                System.out.println("---------------------------------------------");
                System.out.println("client:"+solicitude.getClient().getRfc());
                System.out.println("query:"+solicitude.getQuery());
                System.out.println("current state:"+solicitude.getValue());
                System.out.println("SolicitudId:"+solicitude.getRequestId());
                System.out.println("packageIds:"+solicitude.getPackageIds());
            });
        }

        /**
         * A simple implementation demo of CredentialsStore for loading credentials
         * stored in file system for a single RFC.
        */
        protected static class MyCredentialsStore implements CredentialsStore {
            final String certificateFile = "C:/Users/usuario/.../micertificado.cer";
            final String privateKeyFile  = "C:/Users/usuario/.../miLlavePrivada.key";
            final char[] pwd = "micontraseña".toCharArray();
            final String rfc = "miRFC";

            @Override public Credentials getCredentials(String rfc) throws RepositoryException {
                if (!this.rfc.equalsIgnoreCase(rfc)) {
                    throw new RepositoryException("credentials not found:"+rfc);
                }
                return CertUtils.loadCredentials(rfc, certificateFile, privateKeyFile, pwd);
            }
        }
    }

