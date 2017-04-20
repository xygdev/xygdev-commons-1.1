package xygdev.commons.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import xygdev.commons.util.FileKit;
import xygdev.commons.util.PathKit;
import xygdev.commons.util.TypeConvert;

/**
 * JettyServer is used to config and start jetty web server.
 * Jetty version 8.1.8-->2017.4.18 up to jetty9(9.2.19.v20160908)
 * https://my.oschina.net/myaniu/blog/470050
 */
class JettyServer implements IServer {
	
	private String webAppDir;
	private int port;
	private String context;
	private int scanIntervalSeconds;
	private boolean running = false;
	private Server server;
	private WebAppContext webApp;
	//添加文件服务器到Server里面
	//参考：http://blog.csdn.net/zmx729618/article/details/53185779
	private String fileBase;//CDN文件的本地电脑路径
	private String fileContext;//CND文件的web Context路径
	
	private String keyStorePath = null;
	private String keyStorePassword = null;
	private String keyManagerPassword = null;
	
	JettyServer(String webAppDir, int port, String context, int scanIntervalSeconds) {
		if (webAppDir == null)
			throw new IllegalStateException("Invalid webAppDir of web server: " + webAppDir);
		if (port < 0 || port > 65536)
			throw new IllegalArgumentException("Invalid port of web server: " + port);
		if (TypeConvert.isNullValue(context))
			throw new IllegalStateException("Invalid context of web server: " + context);
		
		this.webAppDir = webAppDir;
		this.port = port;
		this.context = context;
		this.scanIntervalSeconds = scanIntervalSeconds;
	}
	
	JettyServer(String webAppDir, int port, String context, int scanIntervalSeconds,String fileBase,String fileContext) {
		this(webAppDir,port,context,scanIntervalSeconds);
		this.fileBase=fileBase;
		this.fileContext=fileContext;
	}
	
	JettyServer(String webAppDir, int port, String context, String keyStorePath,String keyStorePassword,String keyManagerPassword) {
		this(webAppDir,port,context,0);
		this.keyManagerPassword = keyManagerPassword;
		this.keyStorePassword = keyStorePassword;
		this.keyStorePath = keyStorePath;
	}
	
	public void start() {
		if (!running) {
			try {doStart();} catch (Exception e) {e.printStackTrace();}
			running = true;
		}
	}
	
	public void stop() {
		if (running) {
			try {webApp.stop();server.stop();} catch (Exception e) {e.printStackTrace();}
			running = false;
		}
	}
	
	private void doStart() {
		if (!available(port))
			throw new IllegalStateException("port: " + port + " already in use!");		
		deleteSessionData();		
		//System.out.println("Starting JFinal " + Const.JFINAL_VERSION);
		server = new Server();		
		if(null == this.keyStorePath){//http配置。
			HttpConfiguration http_config = new HttpConfiguration();	        // HTTP connector
	        ServerConnector connector = new ServerConnector(server,new HttpConnectionFactory(http_config));
			connector.setReuseAddress(true);
			connector.setIdleTimeout(30000);
			connector.setPort(port);
			server.addConnector(connector);
		}else{//https 配置
			HttpConfiguration https_config = new HttpConfiguration();
			https_config.setSecureScheme("https");
			https_config.setSecurePort(port);
			https_config.setOutputBufferSize(32768);
			https_config.addCustomizer(new SecureRequestCustomizer());
			SslContextFactory sslContextFactory = new SslContextFactory();
	        sslContextFactory.setKeyStorePath(this.keyStorePath);
	        sslContextFactory.setKeyStorePassword(this.keyStorePassword);
	        sslContextFactory.setKeyManagerPassword(this.keyManagerPassword);
	        ServerConnector httpsConnector = new ServerConnector(server,
	                new SslConnectionFactory(sslContextFactory,"http/1.1"),
	                new HttpConnectionFactory(https_config));
	        httpsConnector.setPort(port);
	        httpsConnector.setIdleTimeout(500000);
	        server.addConnector(httpsConnector);
		}
		webApp = new WebAppContext();		
	    //增加gzip支持
		/*
		FilterHolder  fh = new FilterHolder();
		fh.setAsyncSupported(true);
		fh.setClassName("org.eclipse.jetty.servlets.GzipFilter");
		fh.setInitParameter("mimeTypes", "text/html,text/plain,text/xml,text/css,text/javascript,application/javascript,image/gif,image/png");
		EnumSet<DispatcherType> set = EnumSet.noneOf(DispatcherType.class);
		set.add(DispatcherType.REQUEST);
		set.add(DispatcherType.FORWARD);
		set.add(DispatcherType.INCLUDE);
		set.add(DispatcherType.ERROR);
		set.add(DispatcherType.ASYNC);
		webApp.addFilter(fh, "/*", set);

		webApp.setContextPath(context);
		webApp.setResourceBase(webAppDir);
		webApp.setMaxFormContentSize(81920000);
		//webApp.setParentLoaderPriority(true);
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "true");
		webApp.getInitParams().put("org.eclipse.jetty.server.Request.maxFormContentSize", "-1");
		*/
		
		webApp.setThrowUnavailableOnStartupException(true);	// 在启动过程中允许抛出异常终止启动并退出 JVM
		webApp.setContextPath(context);
		webApp.setResourceBase(webAppDir);	// webApp.setWar(webAppDir);
		//webApp.setParentLoaderPriority(true);
		webApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		webApp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");	// webApp.setInitParams(Collections.singletonMap("org.mortbay.jetty.servlet.Default.useFileMappedBuffer", "false"));
		
		persistSession(webApp);
		
		//添加CDN目录映射 2017.4.18
        HandlerList handlers = new HandlerList();
		if(this.fileBase!=null){
			ResourceHandler resourceHandler = new ResourceHandler();  
	        resourceHandler.setDirectoriesListed(true);  
	        resourceHandler.setResourceBase(this.fileBase);  
	        resourceHandler.setStylesheet(""); 
	        ContextHandler staticContextHandler = new ContextHandler();  
	        staticContextHandler.setContextPath(this.fileContext);  //设置静态目录路径  
	        staticContextHandler.setHandler(resourceHandler);
	        handlers.addHandler(staticContextHandler); //添加到handlers
	        //server.setHandler(handlers);
			System.out.println("setResource:"+fileBase+" mapping to "+fileContext);
		}
		handlers.addHandler(webApp); 
		// Set JSP to NOT use Standard JavaC always
        //System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
		server.setHandler(handlers);
		changeClassLoader(webApp);
		
		// configureScanner
		if (scanIntervalSeconds > 0) {
			Scanner scanner = new Scanner(PathKit.getRootClassPath(), scanIntervalSeconds) {
				public void onChange() {
					try {
						System.err.println("\nLoading changes ......");
						webApp.stop();
						ClassLoader loader = new ClassLoader(webApp, getClassPath());
						webApp.setClassLoader(loader);
						webApp.start();
						System.err.println("Loading complete.");
					} catch (Exception e) {
						System.err.println("Error reconfiguring/restarting webapp after change in watched files");
						e.printStackTrace();
					}
				}
			};
			System.out.println("Starting scanner at interval of " + scanIntervalSeconds + " seconds.");
			scanner.start();
		}
		
		try {
			System.out.println("Starting web server on port: " + port);
			server.start();
			System.out.println("Starting Complete. Welcome To The XYG Developer World :)");
			//System.out.println("Server path: http://localhost:"+port+context);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		return;
	}
	
	private void changeClassLoader(WebAppContext webApp) {
		try {
			String classPath = getClassPath();
			@SuppressWarnings("resource")
			ClassLoader wacl = new ClassLoader(webApp, classPath);
			wacl.addClassPath(classPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getClassPath() {
		return System.getProperty("java.class.path");
	}
	
	private void deleteSessionData() {
		try {
			FileKit.delete(new File(getStoreDir()));
		}
		catch (Exception e) {
		}
	}
	
	private String getStoreDir() {
		String storeDir = PathKit.getWebRootPath() + "/../../session_data" + context;
		if ("\\".equals(File.separator))
			storeDir = storeDir.replaceAll("/", "\\\\");
		return storeDir;
	}
	
	private void persistSession(WebAppContext webApp) {
		String storeDir = getStoreDir();
		
		SessionManager sm = webApp.getSessionHandler().getSessionManager();
		if (sm instanceof HashSessionManager) {
			try {
				((HashSessionManager)sm).setStoreDirectory(new File(storeDir));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ;
		}
		
		HashSessionManager hsm = new HashSessionManager();
		try {
			hsm.setStoreDirectory(new File(storeDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		SessionHandler sh = new SessionHandler();
		sh.setSessionManager(hsm);
		webApp.setSessionHandler(sh);
	}
	
	private static boolean available(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}
		
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
			
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					// should not be thrown, just detect port available.
				}
			}
		}
		return false;
	}
}
