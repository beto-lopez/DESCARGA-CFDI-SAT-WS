/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
/**
 * This package contains the classes and interfaces needed to encapsulate the
 * status and information of a digital certificates download request using the
 * SAT massive download web service.
 * 
 * <p>See:&nbsp;<a href="#spanish">Español</a></p>
 * 
 * <p>In order to download certificates using this service, certain steps are required:</p>
 * <ol>
 * <li>Request the download by specifying the parameters needed to locate the
 * certificates we need do download.</li>
 * <li>Verify the request to see if it was accepted; or be awere it is not yet
 * ready; and when it is ready, obtain the identifiers of the packages that we
 * should download in order to get the certificates we requested.</li>
 * <li>Download each of the packages that were provided to us through the
 * verification service and save those packages in our repository.</li>
 * </ol>
 * <p>It is important to consider that the steps cannot necessarily be carried out
 * immediately, since verification can take hours or days. Therefore, it is necessary
 * to be able to save and maintain the status of a request in order to return to
 * it at another time and be able to continue the process where we left off.</p>
 * <p>Through {@link DefaultSolicitude} we can encapsulate the status and information
 * concerning a download request. We can also save its state and rebuild it later
 * to continue the download process where we left off the last time we tried.
 * </p>
 * <p>Using {@link DefaultSolicitude} we can download the certificates we require.
 * This class will execute the corresponding service given the information and
 * status that it saves. Since it implements {@link Solicitude}, it encapsulates
 * its information and status; and provies methods to query its state and information;
 * and to listen to it as it advances in its download processes.</p>
 * <br>
 * <hr>
 * <a id="spanish"></a>
 * 
 * Este paquete contiene las clases e interfaces para encapsular los estados
 * e información de una solicitud de descarga de certificacdos mediante el 
 * servicio de descarga masiva de terceros de SAT web service.
 * <p>Para poder realizar la descarga de certificados mediante dicho servicio,
 * se requieren ciertos pasos:</p>
 * <ol>
 * <li>Solicitar la descarga especificando los parmámetros de los certificados
 * (CFDI's) que queremos descargar.</li>
 * <li>Verificar la solicititud para ver si fue aceptada; o ver si todavía no está
 * lista; y al estar lista, obtener los identificadores de los paquetes que deberemos
 * descargar para obtener los certificados que solicitamos.</li>
 * <li>Ir descargando cada uno de los paquetes que se nos proporcionó mediante la
 * verificación e irlos guardando en nuestro repositorio.</li>
 * </ol>
 * <p>Es importante considerar que los pasos no necesariamente se pueden realizar
 * de forma inmediata ya que la verificación puede tomar horar o días. Por lo que
 * se requiere poder guardar y mantener el estado de una solicitud para poderla
 * retormar en otra ocasión y poder intentar continuar con dicha solicitud en
 * otro momento.
 * </p>
 * <p>Mediante {@link DefaultSolicitude} podemos realizar la descarga de los
 * certificados que requerimos. Dicha clase retormará el servicio que le corresponda
 * dada la información y estado que guarda. Y como implementa {@link Solicitude Solicitud},
 * encapsula la información y estado de la misma; e incluye diferentes métodos
 * para consultar la información de la solicitud y también para poderla escuchar
 * conforme avanza en sus procesos de descarga.</p>
 * 
 *  
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.02.15
 * @since 1.0
 */
package com.sicomsa.dmt.solicitude;
