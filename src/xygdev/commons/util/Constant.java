package xygdev.commons.util;

/**
 * 全局静态参数。正式上线的时候，如果要取消调试模式，需要设定DEBUG_MODE=false
 */
public class Constant {
	public static boolean DEBUG_MODE = false;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String ENTER_REPLACE_STR = "\\\\r\\\\n";
	public static String LINE_SEPARATOR = "\\r\\n";//System.getProperty("line.separator");
	//换行符替换的字符System.getProperty("line.separator")，因为json的回车符是\r\n
	//http://www.linuxidc.com/Linux/2014-07/104513.htm
	//换行符号的处理结论：
	/*http://long2010.iteye.com/blog/1887701
	 * 如果需要用到换行符，则必须用：System.getProperty("line.separator")，而不可以单独用\r或者\n
	 * 因为一般Windows系统用的是\r\n作为换行。所以数据库存储的也是\r\n。
	 * 目前对单独的\r(回车)或者\n(换行)是无法自动替换的，会导致json传输给前端的时候出错！
	 * 反正，一个很简单的逻辑：换行符都用System.getProperty("line.separator")即可。
	 * 2016.12.19备注：不可以用这个System.getProperty("line.separator");，会导致问题：
	 * 如果service是Unix/Linux的，这个是/n，会导致替换的时候，还会出现多一个换行符号。
	 * 所以，现在统一用这个："\\r\\n"
	 */
}
