/*
 * Copyright (c) Alberto Carlos Lopez Montemayor
 * All rights reserved.
 */
/**
 * Package with classes to create and load a {@link Batch} that allows
 * downloading multiple download requests using the same connection.
 * <p>See:&nbsp;<a href="#spanish">Español</a></p>
 * <p>We use a {@link BatchFactory} that allows us to add download requests
 * for different contributors, and it generates an instance of the <code>Batch</code>
 * class that enables us to perform downloads of digital certificates (CFDIs)
 * from the included requests; and stores the responses received from the web service
 * in a file that can later be loaded and resumed from the state where each request
 * was left off.</p>
 * <p><code>BatchFactory</code> maintains a map of clients (contributors) to
 * avoid having multiple instances of the same client and thus be able to use
 * their credentials and obtained tokens for different requests.</p>
 * <p>This is done in this way because the web service does not necessarily have
 * the downloads available immediately, as their verification may take several
 * hours or days, so there is a need to save the states in some way to be able
 * to continue later.<br>
 * This package was created with that objective, to save the states of the
 * downloads to continue their processes later, avoiding information loss.</p>
 * <p>The file corresponding to each <code>Batch</code> can be viewed since it is
 * saved in xml format and contains the requests and responses from the web
 * service. However, it should not be edited since it is the file with which a
 * <code>Batch</code> can be reloaded.</p>
 * <p>To consume the bulk download service of CFDIs, perhaps the most convenient
 * thing would be to have a database with statistical information on the downloads
 * of the managed contributors, but we did not want users of our package to depend
 * on a specific database, besides that we still do not have it implemented since
 * we first want to decode all the digital certificates we download to have more
 * information and to be able to define a more complete repository that we can
 * later implement in a package for those who want to use our definition and
 * implementation.<br>
 * For this reason, we decided to implement a package with which we could reliably
 * save the states of our downloads while we implement a more adequate repository.
 * But we also wanted to implement something that, although it could be temporary,
 * would be viable and reliable for certain uses and/or users.</p>
 * 
 * <br>
 * <hr>
 * <a id="spanish"></a>
 *
 * Paquete con clases para poder crear y cargar un {@link Batch} o lote que
 * permite la descarga de varias solicitudes usando la misma conexión.
 * <p>Usamos un  {@link BatchFactory} que nos permite agregar solicitudes de
 * descarga para diferentes contribuyentes, y nos genera una instancia de la
 * clase <code>Batch</code> que nos permite realizar las descargas de certificados
 * (CFDIs) de las solicitudes que incluye; y almacena las respuestas recibidas
 * del servicio de descarga masiva en un archivo que posteriormente se puede
 * cargar y retomar el estado donde se quedó cada solicitud.</p>
 * <p><code>BatchFactory</code> mantiene un mapa de clientes (contribuyentes)
 * para evitar tener múltiples instancias del mismo contribuyente y poder así
 * usar sus credenciales y tokens obtenidos para diferentes solicitudes.</p>
 * <p>Esto se hace de esta forma debido a que el servicio de descarga masiva no
 * necesariamente tiene las descargas disponibles inmediatamente, sino que su
 * verificación puede tardar varias horas o días, por lo que hay necesidad de
 * guardar los estados de alguna forma para poder continuar en otro momento.<br>
 * Este paquete fue creado con ese objetivo, de poder guardar los estados de las
 * descargas para poder continuar sus procesos en otro momento, evitando perder
 * información.</p>
 * <p>El archivo correspondiente a cada <code>Batch</code> puede ser visto ya que
 * se guarda en formato xml, y contiene las solicitudes y respuestas del servicio
 * de descarga masiva. Aunque no debe ser editado ya que es el archivo con el que
 * se podrá volver a cargar un <code>Batch</code>.</p>
 * <p>Para consumir el servicio de descarga masiva de CFDIs quizás lo más conveniente
 * sería tener una base de datos con información estadística de las descargas de
 * los contribuyentes administrados, pero no quisimos que usuarios de nuestro
 * paquete tuvieran que depender de una base de datos específica, además de que
 * todavía no la tenemos implementada ya que primero queremos decodificar todos
 * los certificados digitales que descarguemos para así tener más información y
 * poder definir un repositorio más completo que posteriormente podamos implementar
 * en un paquete para los que quieran usar nuestra definición e implementación.<br>
 * Por esto decidimos implementar un paquete con el que pudiéramos guardar los
 * estados de nuestras descargas de forma confiable, mientras implementamos un 
 * repositorio más adecuado. Pero también quisimos implementar algo que, aunque
 * pudiera ser temporal, fuera viable y confiable para ciertos usos y/o usuarios.</p>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version 2025.02.16
 * @since 1.0
 */
package com.sicomsa.dmt.solicitude.batch;
