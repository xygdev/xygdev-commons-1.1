package xygdev.commons.util;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.serial.SerialException;

/**
 * 封装好的数据类型转换类。主要是为了考虑一些null值的处理！
 * <br/>还有一些基础数据类型的互相转换。类似封装Oracle经常用的to_date/to_char/to_number的函数
 * @author Sam.T 2016/8/2
 * @version 1.0
 */
public class TypeConvert {

	/**
	 * 判断对应的值是否属于空值。
	 * <br/>所谓非空，就是：如果是字符（数组和列表），则非空并且有值。非字符就是非空即可。
	 * @param x
	 * @return 对应的值是否空值-->true：空值。false:非空值，可以继续操作
	 */
	public static boolean isNullValue(Object x){
		boolean ret=true;
		if(x!=null){
			if(x instanceof String){//如果是字符
				if(((String) x).trim().length()>0){
					ret=false;
				}
			}else if(x instanceof Object[]){//如果是数组
				if (((Object[]) x).length>0){
					ret=false;
				}
			}else if(x instanceof ArrayList){//如果是列表
				if (((ArrayList<?>) x).size()>0){
					ret=false;
				}
			}else if(x instanceof HashMap){//如果是Map
				if (((HashMap<?, ?>) x).size()>0){
					ret=false;
				}
			}else{
				ret=false;
			}
		}
		return ret;
	}

	/**
	 * 2016.10.24 数据类型转json字符！为了解决那些空的时候显示null字符的问题！
	 * 2016.11.2 新增双引号的处理。只有字符串和clob的才可能出现这个问题。
	 */
    public static String type2JsonStr(Object x){
    	//return x==null?"":type2Str(x);
    	if(x==null){
    		return "";
    	}else{
        	if(x instanceof java.sql.Date){
        		return type2Str((java.sql.Date)x);
        	}else if(x instanceof java.util.Date ){
        		return type2Str((java.util.Date)x);
        	}else if(x instanceof Timestamp ){
        		return type2Str((Timestamp)x);
        	}else if(x instanceof Integer ){
        		return x.toString();
        	}else if(x instanceof Long ){
        		return x.toString();
        	}else if(x instanceof Clob ){
            	return type2Str((Clob)x).replaceAll("\"", "\\\\\"");
        	}else{
        		return type2Str(x).replaceAll("\"", "\\\\\"");
        	}
    	}
    }
	/**
	 * 数据类型转字符
	 */
    public static String type2Str(Object x){
    	if(x instanceof java.sql.Date){
    		return type2Str((java.sql.Date)x);
    	}else if(x instanceof java.util.Date ){
    		return type2Str((java.util.Date)x);
    	}else if(x instanceof Timestamp ){
    		return type2Str((Timestamp)x);
    	}else if(x instanceof Clob ){
    		return type2Str((Clob)x);
    	}else{
    		return x==null?null:x.toString();
    	}
    }
	public static String type2Str(Long x){
		return x==null?null:x.toString();
	}
    public static String type2Str(Double x){
		return x==null?null:x.toString();
	}
    public static String type2Str(Timestamp x){
    	//new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(ts);用这个，00点居然会出现24！
    	return x==null?null:new SimpleDateFormat(Constant.DATETIME_FORMAT).format(x);
    }
    public static String type2Str(java.sql.Date x){
    	return x==null?null:new SimpleDateFormat(Constant.DATE_FORMAT).format(x);
    }
    public static String type2Str(java.util.Date x){
    	return x==null?null:new SimpleDateFormat(Constant.DATETIME_FORMAT).format(x);
    }
    public static String type2Str(java.util.Date x,String f){
    	return x==null?null:new SimpleDateFormat(f).format(x);
    }
    public static String type2Str(Clob x){
    	if(isNullValue(x)) return null;
    	StringBuffer sb = new StringBuffer();  
        Reader clobStream = null;  
        try {  
            clobStream = x.getCharacterStream();  
            char[] b = new char[60000];// 每次获取60K  
            int i = 0;  
            while ((i = clobStream.read(b)) != -1) {  
                sb.append(b, 0, i);  
            }  
        } catch (Exception ex) {  
            sb = null;  
        } finally {  
            try {  
                if (clobStream != null) {  
                    clobStream.close();  
                }  
            } catch (Exception e) {  
            }  
        }  
        if (sb == null)  
            return null;  
        else  
            return sb.toString(); 
    }
    
	/**
	 * 字符转别的数据类型
	 */
    public static Long str2Long(String x){
		return isNullValue(x)?null:Long.parseLong(x);
	}
    public static Integer str2Int(String x){
		return isNullValue(x)?null:Integer.parseInt(x);
	}
    public static Double str2Double(String x){
		return isNullValue(x)?null:Double.parseDouble(x);
	}
    public static Boolean str2Boolean(String x){
		return Boolean.parseBoolean(x);
	}
    public static Timestamp str2Timestamp(String x) throws ParseException{
    	//new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(ts);用这个，00点居然会出现24！
    	if(isNullValue(x)) return null;
    	 String fmt = Constant.DATETIME_FORMAT;
    	 //兼容yyyy-MM-dd的格式！
		if (x.length()>=8&&x.length()<=10){
			fmt = Constant.DATE_FORMAT;
		}
		return new Timestamp(new SimpleDateFormat(fmt).parse(x).getTime());
    }
    public static Clob str2sClob(String x) throws SerialException, SQLException{
		return isNullValue(x)?null:(Clob)new javax.sql.rowset.serial.SerialClob(x.toCharArray());
		//return isNullValue(x)?null:new java.sql.Clob();
	}
    
	/**
	 * 字符转sql date
	 */
    public static java.sql.Date str2sDate(String x) throws ParseException{
    	Date ret = null;
		ret =  isNullValue(x)?null:
			new Date(new SimpleDateFormat(Constant.DATE_FORMAT).parse(x).getTime());
		return ret;
    }
	/**
	 * 字符转util date
	 */
    public static java.util.Date str2uDate(String x) throws ParseException{
    	if(isNullValue(x)) return null;
   	 	String fmt = Constant.DATETIME_FORMAT;
   	 	//兼容yyyy-MM-dd的格式！
		if (x.length()>=8&&x.length()<=10){
			fmt = Constant.DATE_FORMAT;
		}
		return new java.util.Date(new SimpleDateFormat(fmt).parse(x).getTime());
    }

	/**
	 * sqlDate(sql的只有日期) 转 utilDate(日期+时间的)
	 */
    public static java.util.Date q2uDate(java.sql.Date qDate){
    	return qDate==null?null:new java.util.Date(qDate.getTime());
    }
    
	/**
	 *   utilDate(日期+时间的)  转 sqlDate(sql的只有日期)
	 */
    public static java.sql.Date u2qDate(java.util.Date uDate){
    	return uDate==null?null:new java.sql.Date(uDate.getTime());
    }

	/**
	 * sqlTimestamp(sql的日期+时分秒) 转 utilDate(日期+时间的)
	 */
    public static java.util.Date t2uDate(Timestamp tDate){
    	return tDate==null?null:new java.util.Date(tDate.getTime());
    }	
    /**
	 * utilDate(日期+时间的) 转 sqlTimestamp(sql的日期+时分秒) 
	 */
    public static Timestamp u2tDate(java.util.Date uDate){
    	return uDate==null?null:new Timestamp(uDate.getTime());
    }
    
    /*
    public static void main(String[] args){
    	String str="\"我们\"";
    	//System.out.println(TypeConvert.type2JsonStr(str));
    	System.out.println(str+"-->"+str.replaceAll("\"", "\\\\\""));
    	Double d=null;
    	System.out.println(d);
    	String dt = null;//"2015-1-1 23:00:00";
    	String x="T";
    	try{
        	System.out.println("str2uDate:"+type2Str(str2uDate(dt)));
        	System.out.println("u2tDate:"+u2tDate(str2uDate(dt)));
        	System.out.println("str2Timestamp:"+str2Timestamp(dt));
        	System.out.println("t2uDate:"+t2uDate(str2Timestamp(dt)));
        	System.out.println("type2Str:"+type2Str(str2Timestamp(dt)));
        	System.out.println("str2Boolean:"+type2Str(str2Boolean(x)));
        	System.out.println("Boolean:"+type2Str(Boolean.parseBoolean(x)));
        	
        	//System.out.println(type2Str(str2Timestamp(dt)));
        	java.util.Date uDate = new java.util.Date();
        	System.out.println(type2Str(uDate,"yyMMddkkmmss"));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	HashMap<String,Object> map=new HashMap<String,Object>();
    	System.out.println(TypeConvert.isNullValue(map));
    }*/
}
