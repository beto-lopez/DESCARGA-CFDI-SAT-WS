## Librerías en java para descarga masiva de CFDI's de SAT web service

Estas librearías son solo una parte de una aplicación de código abierto que queremos hacer para PYMES.  
Con estos paquetes puedes descargar todos tus CFDIs del servicio de descarga masiva SAT web service.  
Puedes ver la documentación en esta liga: [Overview (com.sicomsa.dmt | DESCARGA-CFDI-SAT-WS)](https://beto-lopez.github.io/DESCARGA-CFDI-SAT-WS/).

### Ejemplo práctico para descargar cfdi´s emitidos y recibidos de un período.

        package com.sicomsa.dmtuser;

        import com.sicomsa.dmt.Query;
        import com.sicomsa.dmt.Credentials;
        import com.sicomsa.dmt.CredentialsStore;
        import com.sicomsa.dmt.RepositoryException;
        import com.sicomsa.dmt.svc.DownloadService;
        import com.sicomsa.dmt.solicitude.batch.Batch;
        import com.sicomsa.dmt.solicitude.batch.BatchFactory;
        import com.sicomsa.dmt.util.QueryMap;
        import com.sicomsa.dmt.util.CertUtils;

        import java.io.File;
        import java.io.IOException;

        import jakarta.xml.soap.SOAPException;
        import jakarta.xml.soap.SOAPConnection;
        import jakarta.xml.soap.SOAPConnectionFactory;

        /**
         * Ejemplo para descargar cfdis de SAT.
         */
        public class Ejemplo {
    
            public static void main(String[] args) throws Exception {
                DownloadService service = new DownloadService();
                File batchFile = new File("EnviadosYRecibidos-2020-04.xml");
                BatchFactory factory = new BatchFactory(service, new MyCredentialsStore());
                Batch batch;
                //Si no existe archivo es la primera ves que ejecutamos y creamos batch
                //con las solicitudes a enviar.
                if (!batchFile.exists()) {
                    batch = buildBatch(factory, batchFile);
                }
                else {
                    //Si ya existe, ya enviamos las solicitudes pero probablemente
                    //todavía no están disponibles las descargas. Cargamos archivo
                    //con las solicitudes y sus estados.
                    batch = factory.load(batchFile);
                }
                dump(batch); //aquí vemos el estado de las solicitudes de este batch
                if (!batch.isPending()) {
                    System.out.println("batch is not pending!");
                    return;
                }
                ///creamos conexión y hacemos otro intento de descarga.
                try (SOAPConnection conn = SOAPConnectionFactory.newInstance().createConnection()) {
                    batch.download(conn);
                }
                dump(batch); //aquí vemos como quedan después de conectarnos a SAT
            }
    
            /**
             * Muesta los estados e información de las solcitudes
             * @param batch 
             */
            protected static void dump(Batch batch) {
                System.out.println("dumping batch");
                batch.solicitudes().forEachRemaining(solicitude-> {
                    System.out.println("-----------------------------------------------------");
                    System.out.println("client:"+solicitude.getClient().getRfc());
                    System.out.println("query:"+solicitude.getQuery());
                    System.out.println("current state:"+solicitude.getValue());
                    System.out.println("SolicitudId:"+solicitude.getRequestId());
                    System.out.println("packageIds:"+solicitude.getPackageIds());
                });
            }

            /**
             * Ejemplo de como crear un batch para solicitar los cfdis emitidos de
             * abril 2020, y el metadata de los recibidos también en ese mes para un rfc.
             * De hecho podrían ser varios RFC's, siempre que se tengan los certificados.
             * @param factory
             * @param file
             * @return
             * @throws IOException
             * @throws SOAPException 
             */
            protected static Batch buildBatch(BatchFactory factory, File file) throws IOException, SOAPException {
                BatchFactory.Builder batchBuilder = factory.builder();
                String rfc1 = "XAXX010101000";         
                Query q0 = QueryMap.builder()
                        .setRfcEmisor(rfc1).setRfcSolicitante(rfc1)
                        .setFechaInicial(QueryMap.dayStart(2020, 4, 1))
                        .setFechaFinal(QueryMap.dayEnd(2020, 4, 30))
                        .setTipoSolicitud("CFDI")
                        .build();               
                Query q1 = QueryMap.builder()
                        .setRfcEmisor("").setRfcSolicitante(rfc1).addReceptor(rfc1)
                        .setFechaInicial(q0.getFechaInicial())
                        .setFechaFinal(q0.getFechaFinal())
                        .setTipoSolicitud("Metadata")
                        .build();
       
                //agregamos las solicitudes y cramos el lote y su archivo
                return batchBuilder.addRequest(0, rfc1, q0)
                        .addRequest(1, rfc1, q1)
                        .build(file);
            }
    
    
            /**
             * Implementación sencilla para obtener los certificados de archivo
             * .cer y .key. Se puede implementar con KeyStore's, bases de datos, etc.
             * En este ejemplo usamos certificados en archivo ya que es la forma más
             * ententible.
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




