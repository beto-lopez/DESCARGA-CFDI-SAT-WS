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

import static com.sicomsa.dmt.svc.AbstractSvc.DMT_PREFIX;
import static com.sicomsa.dmt.svc.AbstractSvc.DMT_URI;

import javax.xml.namespace.QName;

import com.sicomsa.dmt.DMTService;


/**
 * Creates services to call SAT's web service for downloading certificates, 
 * adapted to SAT's version 1.5 released 2025-05-30.
 * <p>To create a new {@link com.sicomsa.dmt.DMTService} with defaults this
 * should do:</p>
 * <pre>
 * {@code 
 *      //Creates a new CFDI download service
 *      DMTService service = ServiceFactory.newCfdiServiceFactory().newService();
 * 
 *      //Creates a new Retenciones download service
 *      DMTService service = ServiceFactory.newRetencionesServiceFactory().newService();
 * }
 * </pre>
 * <p>Other methods are provided in order to create more specific services.</p>
 * 
 * @author <a href="https://www.linkedin.com/in/alberto-carlos-lopez-montemayor-586202198">Beto Lopez</a>
 * @version     2025.06.03
 * @since       1.5
 * 
 */
public class ServiceFactory {
    
    /**
     * URL of cfdi download request service location, before downloading
     */
    public static final String REQUEST_CFDI_URL =
            "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/";
    
    /**
     * URL of retenciones download request service location, before downloading
     */
    public static final String REQUEST_RET_URL =
            "https://retendescargamasivasolicitud.clouda.sat.gob.mx/";
    
    
    /**
     * URL of cfdi download request service location, when downloading
     */
    public static final String DOWNLOAD_CFDI_URL = 
            "https://cfdidescargamasiva.clouda.sat.gob.mx/";
 
    /**
     * URL of retenciones download request service location, when downloading
     */
    public static final String DOWNLOAD_RET_URL = 
            "https://retendescargamasiva.clouda.sat.gob.mx/";
      
    /**
     * Flag to indicate if this factory is for downloading retenciones. False if
     * factory is for downloading Cfdis.
     */
    protected final boolean retenciones;
    
    /**
     * Security used for authentication
     */
    protected OasisSecurity osecurity;
    
    /**
     * MessageFactory used by the services
     */
    protected SvcMessageFactory messageFactory;

    
    /**
     * Creates a new <code>ServiceFactory</code>
     * 
     * @param retenciones true if building download requests for "retenciones"
     *                    false if building download requests for "cfdis"
     */
    protected ServiceFactory(boolean retenciones) {
        this.retenciones = retenciones;
        osecurity = new OasisSecurity();
        messageFactory = DefaultMessageFactory.newInstance();
    }
    
    /**
     * Creates a new <code>ServiceFactory</code> to download cfdis
     * 
     * @return <code>ServiceFactory</code> to download cfdis
     */
    public static ServiceFactory newCfdiServiceFactory() {
        return new ServiceFactory(false);
    }
    
    /**
     * Creates a new <code>ServiceFactory</code> to download "retenciones"
     * 
     * @return <code>ServiceFactory</code> to download "retenciones"
     */
    public static ServiceFactory newRetencionesServiceFactory() {
        return new ServiceFactory(true);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns true if this factory is for "retenciones", false if it is for "cfdis".
     * 
     * @return true if this factory is for "retenciones", false if it is for "cfdis"
     */
    public boolean isRetenciones() {
        return retenciones;
    }
    
    /**
     * Returns the security to use in the authentication service
     * 
     * @return the security to use in the authentication service
     */
    public OasisSecurity getSecurity() {
        return osecurity;
    }

    /**
     * Returns the <code>SvcMessageFactory</code> to build <code>SOAP</code>
     * messages with; that the services this factory builds, will use.
     * 
     * @return <code>SvcMessageFactory</code> to build <code>SOAP</code>
     *         messages with; that the services this factory builds, will use
     */
    public SvcMessageFactory getMessageFactory() {
        return messageFactory;
    }
    
    /**
     * Sets de security to use in this factory.
     * <p>Initially the security is set to a stable default security.</p>
     * <p>Security should be set before building the authentication service.
     * Once the service is built, it will use the security set before its
     * creation.</p>
     * 
     * @param security security to use.
     */
    public void setSecurity(OasisSecurity security) {
        if (security == null) {
            throw new IllegalArgumentException("invalid security");
        }
        this.osecurity = security;
    }
    
    /**
     * Sets the <code>SvcMessageFactory</code> that will be used by the services
     * built with this factory.
     * <p>This property is set to a stable default when factory is created.</p>
     * <p>The services built before setting this property will use the one
     * this factory had when they were built.</p>
     * 
     * @param messageFactory message factory to use by new services built.
     */
    public void setMessageFactory(SvcMessageFactory messageFactory) {
        if (messageFactory == null) {
            throw new IllegalArgumentException("invalid factory");
        }
        this.messageFactory = messageFactory;
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new {@link com.sicomsa.dmt.DMTService} with default configuration.
     * <p>The security and message factory properties set will be used. If they
     * are not set, the defaults will be used.</p>
     * 
     * @return a new {@link com.sicomsa.dmt.DMTService} with default configuration
     */
    public DMTService newService() {
        return new DMTDownloadService(
                messageFactory,
                newAuthenticationService(),
                newSolicitaService(),
                newVerificaService(),
                newDescargaService());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a new {@literal Service<Authorization,Object>} for authentication.
     * 
     * @return a new {@literal Service<Authorization,Object>}  for authentication
     */
    public Service<Authorization,Object> newAuthenticationService() {
        return new AuthenticationService();
    }
   
    /**
     * Returns a new {@literal Service<VerificaResponse,String}  for requests'
     * verification.
     * 
     * @return a new {@literal Service<VerificaResponse,String}  for requests'
     *         verification
     */
    public Service<VerificaResponse,String> newVerificaService() {
        return new VerificaService();
    }
    
    /**
     * Returns a new {@literal Service<DescargaResponse,String}  for downloading
     * 
     * @return a new {@literal Service<DescargaResponse,String} for downloading
     */
    public Service<DescargaResponse,String> newDescargaService() {
        return new DescargaService();
    }
    
    /**
     * Returns a new {@literal Service<SolicitaResponse,Query>} for
     * download requests.
     * <p>This service will invoke the service corresponding to the type of query</p>
     * @see com.sicomsa.dmt.QueryType
     * 
     * @return a new {@literal Service<SolicitaResponse,Query>} for
     *         download requests
     */
    public Service<SolicitaResponse,Query> newSolicitaService() {
        return new SolicitaSvcAdapter(
                newSolicitaEmitidosService(),
                newSolicitaRecibidosService(),
                newSolicitaFolioService());
    }
    
    /**
     * Returns a new {@literal Service<SolicitaResponse,Query>} that calls
     * for download requests of <b>emitted certificates</b>.
     * 
     * @return a new {@literal Service<SolicitaResponse,Query>} that calls for
     *         download requests of <b>emitted certificates</b>
     */
    public Service<SolicitaResponse,Query> newSolicitaEmitidosService() {
        return new SolicitaService(messageFactory, newSolicitaEmitidosContext());
    }
    
    /**
     * Returns a new {@literal Service<SolicitaResponse,Query>} that calls
     * for download requests of <b>received certificates</b>.
     * 
     * @return a new {@literal Service<SolicitaResponse,Query>} that calls for
     *         download requests of <b>received certificates</b>
     */
    public Service<SolicitaResponse,Query> newSolicitaRecibidosService() {
        return new SolicitaService(messageFactory, newSolicitaRecibidosContext());
    }
    
    /**
     * Returns a new {@literal Service<SolicitaResponse,Query>} that calls
     * for download requests of <b>certificates by ID</b>.
     * 
     * @return a new {@literal Service<SolicitaResponse,Query>} that calls for
     *         download requests of <b>certificates by ID</b>
     */
    public Service<SolicitaResponse,Query> newSolicitaFolioService() {
        return new SolicitaService(messageFactory, newSolicitaFolioContext());
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new <code>RequestContext</code> to download emitted certificates.
     * 
     * @return <code>RequestContext</code> to download emitted certificates
     */
    public RequestContext newSolicitaEmitidosContext() {
        DefaultRequestContext ctx = newDefaultContext("SolicitaDescargaEmitidos");
        ctx.setParentRequestName(getQName("SolicitaDescargaEmitidos"));
        ctx.setResponseName(getQName("SolicitaDescargaEmitidosResponse"));
        ctx.setResultName(getQName("SolicitaDescargaEmitidosResult"));
        return ctx;
    }
    
    /**
     * Returns a new <code>RequestContext</code> to download received certificates.
     * 
     * @return <code>RequestContext</code> to download received certificates
     */
    public RequestContext newSolicitaRecibidosContext() {
        DefaultRequestContext ctx = newDefaultContext("SolicitaDescargaRecibidos");
        ctx.setParentRequestName(getQName("SolicitaDescargaRecibidos"));
        ctx.setResponseName(getQName("SolicitaDescargaRecibidosResponse"));
        ctx.setResultName(getQName("SolicitaDescargaRecibidosResult"));
        return ctx;
    }
    
    /**
     * Returns a new <code>RequestContext</code> to download certificates by Id.
     * 
     * @return <code>RequestContext</code> to download certificates by Id
     */
    public RequestContext newSolicitaFolioContext() {
        DefaultRequestContext ctx = newDefaultContext("SolicitaDescargaFolio");
        ctx.setParentRequestName(getQName("SolicitaDescargaFolio"));
        ctx.setResponseName(getQName("SolicitaDescargaFolioResponse"));
        ctx.setResultName(getQName("SolicitaDescargaFolioResult"));
        return ctx;
    }
    
    /**
     * Returns a new <code>RequestContext</code> with defaults set.
     * 
     * @param serviceName name of service of this context
     * 
     * @return <code>RequestContext</code> with defaults set
     */
    protected DefaultRequestContext newDefaultContext(String serviceName) {
        DefaultRequestContext ctx = new DefaultRequestContext();
        ctx.setServiceName(serviceName);
        ctx.setLocation(getRequestLocation("SolicitaDescargaService.svc"));
        ctx.setSoapAction(
            "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/"
                    .concat(serviceName)
        );
        ctx.setChildRequestName(getQName("solicitud"));
        return ctx;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Subclass of {@link com.sicomsa.dmt.svc.AuthenticationSvc} that overrides
     * the service name and location to the new specifications of SAT's web
     * service 1.5
     */
    protected class AuthenticationService extends AuthenticationSvc {

        /**
         * Creates a new <code>AuthenticationService</code>
         */
        public AuthenticationService() {
            super(messageFactory, osecurity);
        }
        
        /**
         * Returns the name of this service as of SAT's version 1.5
         * 
         * @return the name of this service as of SAT's version 1.5
         */
        @Override public String getServiceName() {
            return "Autenticacion";
        }
        
        /**
         * Returns the location of this service as of SAT's version 1.5
         * 
         * @return the location of this service as of SAT's version 1.5
         */
        @Override public String getLocation() {
            return getRequestLocation("Autenticacion/Autenticacion.svc");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Subclass of {@link com.sicomsa.dmt.svc.VerificaSvc} that overrides
     * the service name and location to the new specifications of SAT's web
     * service 1.5
     */
    protected class VerificaService extends VerificaSvc {
        /**
         * Creates a new <code>VerificaService</code>
         */
        public VerificaService() {
            super(messageFactory);
        }
        /**
         * Returns the name of this service as of SAT's version 1.5
         * 
         * @return the name of this service as of SAT's version 1.5
         */
        @Override public String getServiceName() {
            return "VerificaSolicitudDescarga";
        }
        /**
         * Returns the location of this service as of SAT's version 1.5
         * 
         * @return the location of this service as of SAT's version 1.5
         */
        @Override public String getLocation() {
            return getRequestLocation("VerificaSolicitudDescargaService.svc");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Subclass of {@link com.sicomsa.dmt.svc.DescargaSvc} that overrides
     * the service name and location to the new specifications of SAT's web
     * service 1.5
     */
    protected class DescargaService extends DescargaSvc {
        
        /**
         * Creates a new <code>DescargaService</code>
         */
        public DescargaService() {
            super(messageFactory);
        }
        /**
         * Returns the name of this service as of SAT's version 1.5
         * 
         * @return the name of this service as of SAT's version 1.5
         */
        @Override public String getServiceName() {
            return "Descargar";
        }
        /**
         * Returns the location of this service as of SAT's version 1.5
         * 
         * @return the location of this service as of SAT's version 1.5
         */
        @Override public String getLocation() {
            return getDownloadLocation("DescargaMasivaService.svc");
        }
    }
          
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the service location of request along with the service name.
     * 
     * @param svcName name of service
     * @return service location of request along with the service name
     */
    protected String getRequestLocation(String svcName) {
        return getRequestPrefix().concat(svcName);
    }
    
    /**
     * Returns the service location to download certificates along with the service name.
     * 
     * @param svcName name of service
     * @return service location to download certificates along with the service name
     */
    protected String getDownloadLocation(String svcName) {
        return getDownloadPrefix().concat(svcName);
    }
    
    /**
     * Returns URL of service to call for download requests
     * 
     * @return URL of service to call for download requests
     */
    protected String getRequestPrefix() {
        return (retenciones ? REQUEST_RET_URL : REQUEST_CFDI_URL);
    }
    
    /**
     * Returns URL of service to call for downloading certificates
     * 
     * @return URL of service to call for downloading certificates
     */
    protected String getDownloadPrefix() {
        return (retenciones ? DOWNLOAD_RET_URL : DOWNLOAD_CFDI_URL);
    }
    
    /**
     * Returns the QName relative to the web service of the name provided
     * 
     * @param name name of service
     * @return QName relative to the web service of the name provided
     * @throws IllegalArgumentException if name is null
     */
    protected QName getQName(String name) {
        return new QName(DMT_URI, name, DMT_PREFIX);
    } 
    
}
