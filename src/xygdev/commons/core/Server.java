package xygdev.commons.core;

import xygdev.commons.server.IServer;
import xygdev.commons.server.ServerFactory;

/**
 * 
 * 2017.4.18
 * <br/>Server：提供Jetty服务器的类
 * <br/>JettyServer和IServer，ServerFactory，core.Server的关系是：
 * <br/>IServer是接口类，只提供JettyServer的start和stop的方法供使用
 * <br/>JettyServer是IServer的实现类。是整个Jetty服务器处理的核心类。提供了开启Jetty服务器以及重载类等核心方法。
 * <br/>ServerFactory（服务器工厂）是静态方法类，主要是为了调用JettyServer产生IServer（就是new一个IServer）
 * <br/>Server是提供静态Jetty服务器(IServer)的类对象，它调用ServerFactory产生IServer（封装了ServerFactory的方法）。
 * <br/>是对外提供Jetty服务器的统一处理类！
 * <br/>
 * <br/>所以，用Jetty启动服务器只需要在任意一个类的main函数调用这里的start方法即可。要停止服务器只需要直接终止JVM即可。
 * <br/>关于执行main方法对应的进程的逻辑，可以看：Java编程学习笔记.doc/49
 */
public class Server {

	private static IServer server;
	
	/**
	 * 用于在 Eclipse 中，通过创建 main 方法的方式启动项目，支持热加载
	 */
	public static void start(String webAppDir, int port, String context, int scanIntervalSeconds,String fileBase,String fileContext) {
		server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds,fileBase,fileContext);
		server.start();
	}
	public static void start(String webAppDir, int port, String context, int scanIntervalSeconds) {
		server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds);
		server.start();
	}
	
	public static void startSSLServer(String webAppDir, int port, String context, String keyStorePath,String keyStorePassword,String keyManagerPassword) {
		server = ServerFactory.getHttpsServer(webAppDir, port, context, keyStorePath, keyStorePassword, keyManagerPassword);
		server.start();
	}
	
	public static void start() {
		server = ServerFactory.getServer();
		server.start();
	}
	
	public static void stop() {
		server.stop();
	}
	
	/*
	public static void main(String[] args) {
		Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5,"D:\\JSP_MyEclipse\\CDN\\image","/image");
		//Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5);
		//System.out.println("Server.server:"+Server.server.getServer());
		//Server.server.stop();
	}*/
}
