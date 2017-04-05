package com.omisoft.server.common.microservice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.omisoft.server.common.interfaces.WebSocket;
import com.omisoft.server.common.metrics.MetricsService;
import com.omisoft.server.common.utils.InetUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


/**
 * Abstract class to init microservices Created by dido on 16.06.16.
 */
@Slf4j
public class MicroServiceApp {
  private String serviceName;
  private Server server;
  private boolean test;
  public boolean isAlive = false;
  public static MicroServiceApp INSTANCE;
  public static Injector INJECTOR;
  private List<Handler> handlers = new ArrayList<>();

  public static final MicroServiceApp getInstance() {
    return INSTANCE;
  }

  public void preSetup() {

  }

  public void start(String name, String logs_dir) {
    start(name, logs_dir, null);
  }


  public MicroServiceApp addWebSockets(String webSocketPath, List<WebSocket> sockets) {
    // Config WS
    ServletContextHandler wsContextHandler = new ServletContextHandler();
    wsContextHandler.setContextPath(webSocketPath);
    wsContextHandler.setServer(server);
    try {
      ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(wsContextHandler);
      // Add WebSocket endpoint to javax.websocket layer
      for (WebSocket ws : sockets)
        wscontainer.addEndpoint(ws.getClass());
    } catch (DeploymentException | ServletException e) {
      e.printStackTrace();
    }
    handlers.add(wsContextHandler);
    return this;
  }

  private void start(String name, String logs_dir, DependenciesEnum... dependecies) {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    createLogDir(logs_dir);
    INSTANCE = this;
    long begin = System.currentTimeMillis();
    log.info("STARTING MICROSERVICE:" + name);
    serviceName = name;
    registerGlobalErrorHandler();
    try {
      waitForServices(dependecies);
      preSetup();
      addShutdownHook();
      log.info("STARTING SERVICES");
      Handler[] handlerArray = new Handler[handlers.size()];
      contexts.setHandlers(handlers.toArray(handlerArray));
      server.setHandler(contexts);
      isAlive = true;
      log.info("SUCCESS STARTING MICROSERVICE:" + name + " FOR " + (System.currentTimeMillis() - begin) + " ms.");

      log.info("STARTING SERVER");

      server.start();
      log.info("SERVER IS STARTED!!!");
      server.join();


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public MicroServiceApp addRest(final String path, Class aplicationResourceClass) {
    final ServletContextHandler context = new ServletContextHandler(
        server, "/");
    final ServletHolder restEasyServlet = new ServletHolder(
        new HttpServletDispatcher());
    restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix",
        path);
    restEasyServlet.setInitParameter("javax.ws.rs.Application",
        aplicationResourceClass.getName());
    context.addServlet(restEasyServlet, path + "/*");
    handlers.add(context);
    return this;
  }

  /**
   * Shutdown hook, handles System.exit and kill -15
   */
  private void addShutdownHook() {
    Thread shutdownMonitor = new ShutdownMonitor(server);
    shutdownMonitor.start();
    isAlive = false;
    final MicroServiceApp instance = this;

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {

          instance.stop();
          log.info("SERVICE " + serviceName + " STOPPED!!!");
        } catch (Exception e) {
          log.error("Exception during server stop in shutdown hook", e);
        }
      }
    });
  }

  private void registerGlobalErrorHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        log.error("UNHANDLED ERROR IN THREAD:" + t.getName());
        log.error("UNHANDLED ERROR!!!", e);

      }
    });
  }

  public MicroServiceApp addHttps(int port, String keystorePath) {
    // === jetty-https.xml ===
    // SSL Context Factory
    log.info("SETTING KEYSTORE PATH:" + keystorePath);
    SslContextFactory sslContextFactory = new SslContextFactory(keystorePath);
    sslContextFactory.setUseCipherSuitesOrder(true);

    sslContextFactory.setIncludeCipherSuites("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
        "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
        "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA");
    sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
        "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
        "SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
        "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA",
        "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
        "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
        "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
        "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256");
    sslContextFactory.addExcludeProtocols("SSL", "SSLv2", "SSLv2Hello", "SSLv3");
    sslContextFactory.setIncludeProtocols("TLSv1", "TLSv1.1", "TLSv1.2");
    sslContextFactory.setRenegotiationAllowed(false);
    sslContextFactory.setKeyStorePassword("OBF:1fb11oky1fia1lml1mmc1iur1irv1mii1lj51fek1oi41f7l");
    sslContextFactory.setKeyManagerPassword("OBF:1fb11oky1fia1lml1mmc1iur1irv1mii1lj51fek1oi41f7l");

    HttpConfiguration http_config = new HttpConfiguration();
    http_config.setOutputBufferSize(32768);
    http_config.setRequestHeaderSize(8192);
    http_config.setResponseHeaderSize(8192);
    http_config.setBlockingTimeout(30000);
    http_config.setSendServerVersion(false);
    http_config.setSendDateHeader(false);

    // SSL HTTP Configuration
    HttpConfiguration https_config = new HttpConfiguration(http_config);
    // HSTS, set to 365 days* 2
    https_config.addCustomizer(new SecureRequestCustomizer(true, 31536000L * 2, true));
// HTTP2
//    HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(httpConfiguration);
//
//    NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();
//    ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
//    alpn.setDefaultProtocol(HttpVersion.HTTP_1_1.asString()); // sets default protocol to HTTP 1.1


    // SSL Connector
    log.error("ADDED SSL CONFIG");
    ServerConnector sslConnector = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
        new HttpConnectionFactory(https_config));
    log.info("CREATED SSL CONECTOR");
    sslConnector.setPort(port);
    sslConnector.setHost("0.0.0.0");

    server.addConnector(sslConnector);
    return this;

  }

  public MicroServiceApp addHttp(int port) {
    HttpConfiguration http_config = new HttpConfiguration();
    http_config.setOutputBufferSize(32768);
    http_config.setRequestHeaderSize(8192);
    http_config.setResponseHeaderSize(8192);
    http_config.setBlockingTimeout(30000);
    http_config.setSendServerVersion(false);
    http_config.setSendDateHeader(false);
    ServerConnector http =
        new ServerConnector(server, new HttpConnectionFactory(http_config));
    http.setPort(port);
    http.setHost("0.0.0.0");
    http.setIdleTimeout(30000);
    server.addConnector(http);

    return this;
  }

  public MicroServiceApp addJmxSupport() {
    MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    server.addBean(mbContainer);
    return this;
  }

  public MicroServiceApp addDISupport(Module... modules) {

    Injector injector = Guice.createInjector(modules);
    INJECTOR = injector;
    final ServletContextHandler context = new ServletContextHandler(server,
        "/", ServletContextHandler.SESSIONS);

    // Add the GuiceFilter
    context.addFilter(GuiceFilter.class, "/*",
        EnumSet.allOf(DispatcherType.class));
    context.addEventListener(injector.getInstance((GuiceResteasyBootstrapServletContextListener.class)));

    context.addServlet(DefaultServlet.class, "/");
    handlers.add(context);

    return this;

  }


  public MicroServiceApp addWebApp() {
    org.eclipse.jetty.webapp.Configuration.ClassList classlist =
        org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);

    classlist.addBefore(JettyWebXmlConfiguration.class.getName(),
        AnnotationConfiguration.class.getName());


    String webDir = MicroServiceApp.class.getClassLoader().getResource("webapp").toExternalForm();

    WebAppContext webAppContext = new WebAppContext();
    webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
    webAppContext.setContextPath("/");
    webAppContext.getSessionHandler().setHttpOnly(true);


    webAppContext.setBaseResource(new ResourceCollection(new String[]{webDir}));
    webAppContext.setDescriptor(webDir + "/WEB-INF/web.xml");
    webAppContext.setWelcomeFiles(new String[]{"/secure/index.html"});
    webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
        ".*/target/classes/*|.*/[^/]*servlet-api-[^/]*\\\\.jar$|.*/javax.servlet.jsp.jstl-.*\\\\.jar$|.*/[^/]*taglibs.*\\\\.jar$");
    handlers.add(webAppContext);
    return this;
  }


  public MicroServiceApp() {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    // Setup Threadpool
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMinThreads(8);
    threadPool.setMaxThreads(1024);
    Server server = new Server(threadPool);
    this.server = server;
    INSTANCE = this;


    // Set Form Limits
    server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 10000);
    server.setAttribute("org.eclipse.jetty.server.Request.maxFormKeys", 200);


    MetricsService.initMetrics(Thread.currentThread().getStackTrace()[1].getClassName());


  }


  private void createLogDir(String logs_dir) {
    // todo hardcoded filename/path according to findbugs. Do something maybe.
    File logDir = new File(logs_dir);
    // todo RV_RETURN_VALUE_IGNORED_BAD_PRACTICE - findbugs???
    boolean madeDirs = logDir.mkdirs();
  }


  /**
   * Wait for services that are dependencies of the current service
   *
   * @param dependencies
   */
  private void waitForServices(DependenciesEnum[] dependencies) {
    if (dependencies != null) {
      for (DependenciesEnum d : dependencies) {

        try {
          InetUtils.checkAvailabilityAndBlock(d.getHost(), d.getPort());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  }


  /**
   * Stop server. Code is executed by shutdown hook
   */
  protected void stop() {
    try {
      log.info("Exiting service:" + serviceName);
      server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Generic catch all error handler
   */
  public static class ErrorHandler extends ErrorPageErrorHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.getWriter()
          .append("{\"status\":\"GENERIC ERROR\",\"message\":\"HTTP ")
          .append(String.valueOf(response.getStatus()))
          .append("\"}");
    }
  }
}


