/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
/**
 * Provides classes and interfaces to consume SAT's massive download web service.
 * 
 * <p>See:&nbsp;<a href="#spanish">Español</a></p>
 * <p>To be able to perform a download, the following steps are required:</p>
 * <ul>
 * <li>Authenticate with SAT using valid and current credentials to obtain a token
 * that we can use for a few minutes and that will be requested in each of the following steps.</li>
 * <li>Request a download with the parameters we define, to be able to obtain a
 * request Id with which we can verify the request we made.</li>
 * <li>Verify the request so that when its verification is concluded, we can obtain
 * the names of the packages we need to request download. Verification is not
 * immediate and we will probably require several attempts until it is ready.</li>
 * <li>For each package name or identifier we receive, request its download to
 * receive each package and be able to save it.</li>
 * </ul>
 * 
 * The {@link DMTService} class contains the methods to authenticate; request;
 * verify and download the packages. It is not advisable to use it outside of a
 * {@link DMTClient} that should be the one using <code>DMTService</code>
 * and thus be able to manage its tokens. However, we are going to describe
 * <code>DMTService</code>'s methods for the purpose of explaining the classes
 * within this package.
 * 
 * <p><strong><code>DMTService</code> Methods:</strong></p>
 * 
 * <p><strong>public {@link Authorization} authenticate(SOAPConnection conn,
 *      {@link Credentials} creds) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invokes the <b>authentication service</b> using the connection and credentials
 * received as parameters</li>
 * <li>Returns SAT's response in an <code>Authorization</code> class that contains
 * the received token and other methods that help us to know the validity of the
 * received authorization.</li>
 * </ul>
 * 
 * <p><strong>public {@link SolicitaResponse} request(SOAPConnection conn, Credentials creds,
 *      {@link Query} query, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invokes the <b>download request service</b> using the received connection,
 * query, and token as parameters.</li>
 * <li><code>Query</code> contains the parameters of the CFDI's
 * we want to download. Its period, or its status, type of download, etc.</li>
 * <li>This method parses the request response into the <code>SolicitaResponse</code> class that
 * contains the request Id that we can use to verify the request.</li>
 * <li><code>SolicitaResponse</code> includes the request status code and the
 * message received in the response of the request we made, since the request could
 * be rejected for different reasons.</li>
 * </ul>
 * 
 * <p><strong>public {@link VerificaResponse} verify(SOAPConnection conn, Credentials creds,
 *      String requestId, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invokes the <b>verify request service</b> with the received parameters.
 * In particular, it invokes to verify the id received in the <code>requestId</code> parameter.</li>
 * <li>This method returns the SAT response in the <code>VerificaResponse</code> class that
 * contains methods to know if the verification was rejected and why. Or if the
 * verification is still not finished; or if it has already successfully concluded.<br>
 * <li><code>VerificaResponse</code> also has methods to extract the names of the
 * packages that need to be downloaded.</li>
 * </ul>
 * 
 * <p><strong>public {@link DescargaResponse} download(SOAPConnection conn, Credentials creds,
 *      String packageId, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invokes the <b>package download service</b>, in particular the package received
 * as the <code>packageId</code> parameter.</li>
 * <li>This method returns the response in a <code>DescargaResponse</code> instance
 * that contains the download status; the response message, and if the request
 * was accepted: the downloaded package to be saved.</li>
 * </ul>
 * 
 * <p><strong>public {@link DownloadRepository} getRepository();</strong></p>
 * <ul>
 * <li>With this method, we will be able to save the packages we are downloading.</li>
 * </ul>
 * 
 * <br>
 * <hr>
 * <a id="spanish"></a>
 * 
 * <p>Contiene las clases e interfaces definidas para poder consumir
 * el servicio de descarga masiva de CFDI´s de SAT web service.</p>
 * 
 * <p>Para poder realizar una descarga se requieren los siguientes pasos:</p>
 * <ul>
 * <li>Autenticarnos con SAT con las credenciales válidas y vigentes para obtener
 *    un token que podremos usar por algunos minutos y que se nos pedirá en cada
 *    uno de los siguientes pasos.</li>
 * <li>Solicitar una descarga con los parámetros que definamos, para poder obtener
 *    un IdSolicitud con el que podremos verificar la solicitud que hicimos.</li>
 * <li>Verificar la solicitud para cuando concluya su verificación poder obtener
 *    los nombres de los paquetes que requerimos solicitar descargar. La verificación
 *    no es inmediata y probablemente requeriremos varios intentos hasta que esté.</li>
 * <li>Por cada nombre o identificador de paquete que recibimos, solicitar su
 *    descarga para ir así recibiendo cada paquete y poderlo guardar.</li>
 * </ul>
 * <p>La clase {@link DMTService} contiene los métodos para autenticarnos; solicitar;
 * verificar y descargar los paquetes. No es recomendable usarlo fuera de un
 * {@link DMTClient} que es quien debería ser quien use <code>DMTService</code>
 * y así pueda administrar sus tokens. Sin embargo, vamos a describir los métodos
 * de <code>DMTService</code> para efectos de explicar las clases dentro de este paquete.</p>
 * 
 * <p><strong>Métodos de <code>DMTService</code>:</strong>
 * 
 * <p><strong>public {@link Authorization} autentica(SOAPConnection conn,
 *      {@link Credentials} creds) throws SOAPException;</strong></p>
 * <p>Invoca al <b>servicio de autenticación</b> de SAT usando la conexión y credenciales
 * recibidas como parámetros y retorna la respuesta de SAT en una instancia de 
 * <code>Authorization</code> que contiene el token recibido y otros métodos que nos sirven
 * para conocer la vigencia de la autorización recibida.</p>
 * 
 * <p><strong>public {@link SolicitaResponse} solicita(SOAPConnection conn,
 *      Credentials creds, {@link Query} query, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invoca el servicio de solicitud de descarga de SAT usando la conexión, query
 * y token recibidos como parámetros.</li>
 * <li><code>Query</code> contiene los parámetros de los CFDI's que queremos descargar.
 * Su período, su estado, tipo de descarga, etc.</li>
 * <li>Este método retorna la respuesta de la solicitud en una instancia de
 * <code>SolicitaResponse</code> que contiene el IdSolicitud que podremos usar
 * para verificar la solicitud.</li>
 * <li><code>SolicitaResponse</code> también tiene el código de estatus de la
 * solicitud y el mensaje que se recibió en la respuesta de la solicitud que
 * hicimos, puesto que la solicitud pudiera haber sido rechazada por diferentes motivos.</li>
 * </ul>
 * 
 * <p><strong>public {@link VerificaResponse} verifica(SOAPConnection conn,
 *      Credentials creds, String requestId, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invoca <b>servicio de verificación de descarga</b> con los parámetros recibidos.
 * En particular lo invoca para verificar la IdSolicitud recibida en el parámetro requestId.</li>
 * <li>Este método retorna la respuesta de SAT en la clase <code>VerificaResponse</code>
 * que contiene métodos para saber si la verificación fue rechazada y por qué.<br>
 * O si la verificación todavía no está terminada; o si ya concluyó satisfactoriamente y en dicho caso
 * tiene métodos para extraer los nombres de los paquetes que se requiere descargar.</li>
 * </ul>
 * 
 * <p><strong>public {@link DescargaResponse} descarga (SOAPConnection conn,
 *      Credentials creds, String packageId, String token) throws SOAPException;</strong></p>
 * <ul>
 * <li>Invoca <b>servicio de descarga de paquetes</b> de SAT, en particular el paquete
 * recibido como parámetro packageId.</li>
 * <li>Este método retorna la respuesta en una instancia de <code>DescargaResponse</code> que
 * contiene el estatus de la descarga, el mensaje de la respuesta y de haberse
 * aceptado la petición: el paquete descargado a guardar.</li>
 * </ul>
 * 
 * <p><strong>public {@link DownloadRepository} getRepository();</strong></p>
 * <ul>
 * <li>Mediante este método podremos guardar las descargas que vayamos haciendo.</li>
 * </ul>
 * 
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.02.08
 * @since 1.0
 */
package com.sicomsa.dmt;
