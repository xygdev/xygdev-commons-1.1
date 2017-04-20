package xygdev.commons.server;

import java.io.File;

import xygdev.commons.util.PathKit;

/**
 * ServerFactory
 */
public class ServerFactory {
	
	private static final int DEFAULT_PORT = 80;
	private static final int DEFAULT_SCANINTERVALSECONDS = 5;
	
	private ServerFactory() {
		
	}
	
	/**
	 * Return web server.
	 * <p>
	 * important: if scanIntervalSeconds < 1 then you will turn off the hot swap
	 * @param webAppDir the directory of the project web root
	 * @param port the port
	 * @param context the context
	 * @param scanIntervalSeconds the scan interval seconds
	 * @param fileBase the CDN file path
	 * @param fileContext the CDN file web context
	 */
	public static IServer getServer(String webAppDir, int port, String context, int scanIntervalSeconds,String fileBase,String fileContext) {
		return new JettyServer(webAppDir, port, context, scanIntervalSeconds,fileBase,fileContext);
	}
	public static IServer getServer(String webAppDir, int port, String context, int scanIntervalSeconds) {
		return new JettyServer(webAppDir, port, context, scanIntervalSeconds,null,null);
	}
	public static IServer getHttpsServer(String webAppDir, int port, String context, String keyStorePath,String keyStorePassword,String keyManagerPassword) {
		return new JettyServer(webAppDir, port, context, keyStorePath,keyStorePassword,keyManagerPassword);
	}
	
	public static IServer getServer(String webAppDir, int port, String context) {
		return getServer(webAppDir, port, context, DEFAULT_SCANINTERVALSECONDS);
	}
	
	public static IServer getServer(int port, String context, int scanIntervalSeconds) {
		return getServer(detectWebAppDir(), port, context, scanIntervalSeconds);
	}
	
	public static IServer getServer(int port, String context) {
		return getServer(detectWebAppDir(), port, context, DEFAULT_SCANINTERVALSECONDS);
	}
	
	public static IServer getServer(int port) {
		return getServer(detectWebAppDir(), port, "/", DEFAULT_SCANINTERVALSECONDS);
	}
	
	public static IServer getServer() {
		return getServer(detectWebAppDir(), DEFAULT_PORT, "/", DEFAULT_SCANINTERVALSECONDS);
	}
	
	private static String detectWebAppDir() {
		String rootClassPath = PathKit.getRootClassPath();
		String[] temp = null;
		if (rootClassPath.indexOf("\\WEB-INF\\") != -1)
			temp = rootClassPath.split("\\\\");
		else if (rootClassPath.indexOf("/WEB-INF/") != -1)
			temp = rootClassPath.split("/");
		else
			throw new RuntimeException("WEB-INF directory not found.");
		return temp[temp.length - 3];
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private static String detectWebAppDir_old() {
		String rootClassPath = PathKit.getRootClassPath();
		String[] temp = null;
		try {
			temp = rootClassPath.split(File.separator);
		}
		catch (Exception e) {
			temp = rootClassPath.split("\\\\");
		}
		return temp[temp.length - 3];
	}
}




