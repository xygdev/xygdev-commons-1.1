package xygdev.commons.sqlStmt;

import java.util.Map;

import xygdev.commons.util.TypeConvert;

/**
 * Sql语句公用处理。主要是为了封装一些常用的处理Sql语句的字符的代码。例如and语句这些的。
 * @author Sam.T 2016.8.9
 */
public class SqlStmtPub {
	
	/**
	 * 判断对应的值是否属于有效的。所谓有效，就是：非空就是有效。
	 * @param colValue
	 * @return 对应的值是否属于有效-->true：有效。
	 */
	public static boolean isVaildValue(Object colValue){
		return !TypeConvert.isNullValue(colValue);
	}
	
	/**
	 * 获取A=B或者A LIKE B的AND语句
	 * <br/>相当于在POST-QUERY里面的copy功能，生成colName=colValue或者colName Like colValue的语句
	 * <br/>顺便将对应的参数也一起给赋值了！一步到位！
	 * @param colName DB栏位名称
	 * @param colValue 对应的值
	 * @param paramMap 自动添加对应所需的参数变量
	 * @param forceFlag 强制要该条件的标识，无论是否为空 (2017.1.5新增)
	 * @return sqlStmt用的and语句
	 */
	public static String getAndStmt(String colName,Object colValue,Map<String,Object> paramMap,Boolean forceFlag) throws Exception{
		if(!forceFlag&&!isVaildValue(colValue)){
			return "";
		}
		if (paramMap==null){//当参数列表为空的时候，必须要报错！否则后面执行的时候会有bug。
			throw new RuntimeException("所传入的参数Map变量不可以为空！");
		}
		String ret=null;
		if(colValue instanceof String){//如果是字符
			if(((String) colValue).indexOf("%")!=-1||((String) colValue).indexOf("_")!=-1){
				ret=" AND "+colName+" LIKE :"+colName+" ";
			}else{
				ret=" AND "+colName+" = :"+colName+" ";
			}
		}else{
			ret=" AND "+colName+" = :"+colName+" ";
		}
		paramMap.put(colName, colValue);
		return ret;
	}
	
	public static String getAndStmt(String colName,Object colValue,Map<String,Object> paramMap) throws Exception{
		return SqlStmtPub.getAndStmt(colName, colValue, paramMap, false);
	}

	public static String LOW_CODE="_L";
	public static String HIGH_CODE="_H";
	/**
	 * 获取低水位和高水位的AND语句
	 * <br/>相当于在POST-QUERY里面的APP_FIND.QUERY_RANGE功能，
	 * 生成colName between colValueLow and colValueHigh 的语句
	 * <br/>注意：目前2个低水位和高水位的栏位名称，目前用：栏位+LOW_CODE和栏位+HIGH_CODE来作为占位符（此乃约定）
	 * <br/>逻辑：如果2个参数都为空，则还是返回between a and b。其实最好在外面判断。
	 * @param colName DB栏位名称
	 * @param colValueLow 低水位
	 * @param colValueHigh 高水位
	 * @param paramMap 自动添加对应所需的参数变量
	 * @param forceFlag 强制要该条件的标识，无论是否为空 (2017.1.5新增)
	 * @return sqlStmt用的and语句
	 */
	public static String getAndStmt(String colName,Object colValueLow,Object colValueHigh,Map<String,Object> paramMap,Boolean forceFlag) throws Exception{
		if(!forceFlag&&!isVaildValue(colValueLow)&&!isVaildValue(colValueHigh)){
			return "";
		}
		if (paramMap==null){
			throw new RuntimeException("所传入的参数Map变量不可以为空！");
		}
		String ret=null;
		if(isVaildValue(colValueLow)&&colValueLow.equals(colValueHigh)){
			ret=" AND "+colName+" = :"+colName+LOW_CODE+" ";
			paramMap.put((colName+LOW_CODE), colValueLow);
		} else if(!isVaildValue(colValueLow)&&isVaildValue(colValueHigh)){
			ret=" AND "+colName+" <= :"+colName+HIGH_CODE+" ";
			paramMap.put((colName+HIGH_CODE), colValueHigh);
		} else if(isVaildValue(colValueLow)&&!isVaildValue(colValueHigh)){
			ret=" AND "+colName+" >= :"+colName+LOW_CODE+" ";
			paramMap.put((colName+LOW_CODE), colValueLow);
		}else{
			ret=" AND "+colName+" between :"+colName+LOW_CODE+" and :"+colName+HIGH_CODE+" ";
			paramMap.put((colName+LOW_CODE), colValueLow);
			paramMap.put((colName+HIGH_CODE), colValueHigh);
		}
		return ret;
	}
	
	public static String getAndStmt(String colName,Object colValueLow,Object colValueHigh,Map<String,Object> paramMap) throws Exception{
		return SqlStmtPub.getAndStmt(colName, colValueLow, colValueHigh, paramMap,false);
	}
	
	/*
	public static void main(String[] args){
		String userId="";
		String glassIndustry=null;
		StringBuffer sqlBuf=new StringBuffer();
		Map<String,Object> paramMap=new HashMap<String,Object>();
		sqlBuf.append("SELECT ORGANIZATION_ID,ORGANIZATION_CODE,ORGANIZATION_NAME FROM XYG_ALB2B_ONHAND_PERM_V");
		sqlBuf.append(" WHERE  1= 1");
		try {
			sqlBuf.append(SqlStmtPub.getAndStmt("USER_ID",userId,paramMap,true));
			sqlBuf.append(SqlStmtPub.getAndStmt("GLASS_INDUSTRY",glassIndustry,paramMap,true));
			sqlBuf.append(SqlStmtPub.getAndStmt("ORGANIZATION_CODE","",paramMap));
			sqlBuf.append(SqlStmtPub.getAndStmt("ORGANIZATION_NAME", "",paramMap));
			sqlBuf.append(" ORDER BY ORGANIZATION_ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sqlBuf:"+sqlBuf.toString());
		if(userId instanceof String){
			System.out.println("userId is string!");
		}
	}*/
}

/*2016.8.10 BACKUP
 	public static String getAndStmt(String colName,Object colValueLow,Object colValueHigh){
		String ret=null;
		//但是，无法确定这时候是用1个参数还是2个参数！所以暂时用这个办法NVL，让其可以用2个参数。
		if(colValueLow!=null&&colValueLow.equals(colValueHigh)){
			ret=" AND "+colName+" = NVL(:"+colName+LOW_CODE+" ,:"+colName+HIGH_CODE+" ) ";
		} else if(colValueLow==null&&colValueHigh!=null){
			ret=" AND "+colName+" <= NVL(:"+colName+HIGH_CODE+" ,:"+colName+LOW_CODE+" ) ";
		} else if(colValueLow!=null&&colValueHigh==null){
			ret=" AND "+colName+" >= NVL(:"+colName+LOW_CODE+" ,:"+colName+HIGH_CODE+" ) ";
		}else{
			ret=" AND "+colName+" between :"+colName+LOW_CODE+" and :"+colName+HIGH_CODE+" ";
		}
		return ret;
	}*/
 
