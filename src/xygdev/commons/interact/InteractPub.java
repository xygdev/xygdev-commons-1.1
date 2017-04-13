package xygdev.commons.interact;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;

import xygdev.commons.entity.PlsqlRetValue;
import xygdev.commons.entity.SqlResultSet;
import xygdev.commons.springjdbc.DevJdbcDaoSupport;
import xygdev.commons.util.LogUtil;

/**
 * 交互式报表操作的封装类。对外使用。
 * @author Sam.T 2016.8.10
 */
public class InteractPub extends DevJdbcDaoSupport  {
	/**
	 * 	获取用户的默认打开的交互式报表头ID。如果没有定义，则返回0。不允许返回空字符或者null！
	 * Hid=header id
	 * @param userId  用户ID
	 * @param interactCode 交互式报表Code
	 * @return 交互式报表的头ID
	 * @throws Exception
	 */
	public long getDefaultIrrHid(Long userId,String interactCode)  throws Exception{
		String sql;
		//根据用户和报表的名称获取默认打开的文件夹的头ID
		sql="SELECT HEADER_ID "
				+ " FROM XYG_ALD_INTERACT_HEADERS "
				+ "  WHERE 1=1 "
				+ " AND DEFAULT_FLAG = 'Y' "
				+ " AND INTERACT_CODE =:1  "
				+ " AND USER_ID = :2 "
				+ " AND ROWNUM<=1";
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("1", interactCode);
		paramMap.put("2", userId);
		long ret=0;
		//这里有一个逻辑就是特殊处理，如果没有返回行也正常处理。因为用户可能并没有配置默认的交互式报表
		try{
			ret=Long.parseLong(this.getDevJdbcTemplate().queryForObjSingle(sql, paramMap).toString());
		}catch (EmptyResultDataAccessException e){
			ret=0;
		}
		//由于Long不接受空字符值和null的处理，所以只好用0来代替！
		return ret;
	}
	
	/**
	 * 根据头ID获取对应的交互式报表的定义，返回json格式的数据
	 *   Hid=header id
	 * @param id 交互式报表头ID
	 * @return 交互式报表的定义
	 * @throws Exception
	 */
	public String getIrr(Long id) throws Exception{
		StringBuffer sb = new StringBuffer();
		String sqlStmtH;
		String sqlStmtL;
		//执行SQL
		sqlStmtH="SELECT HEADER_ID "
				+ " ,INTERACT_CODE "
				+ " ,USER_INTERACT_NAME "
				+ ",DESCRIPTION "
				+ " ,PUBLIC_FLAG "
				+ " ,AUTOQUERY_FLAG "
				+ ",DEFAULT_FLAG "
				+ ",ORDER_BY "
				+ ",PAGE_SIZE "
				+ " FROM XYG_ALD_INTERACT_HEADERS "
				+ "  WHERE 1=1 "
				+ " AND HEADER_ID = :1";
		sqlStmtL="SELECT LINE_SEQ,COLUMN_NAME"
				+ " FROM XYG_ALD_INTERACT_LINES "
				+ " WHERE HEADER_ID =:1 "
				+ " ORDER BY LINE_SEQ";
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("1",id);
		SqlResultSet resultSetH=new SqlResultSet();
		SqlResultSet resultSetL=new SqlResultSet();
		resultSetH=this.getDevJdbcTemplate().queryForResultSet(sqlStmtH, paramMap);
		resultSetL=this.getDevJdbcTemplate().queryForResultSet(sqlStmtL, paramMap);
	    sb.append("{"); 
	    if(resultSetH.getResultSet().size()>0){//存在数据
		    sb.append("\"EXISTS\": \"Y\",\"rows\":{\"HEADER\":"); 
		    sb.append(resultSetH.toJsonStr());
	    	//行：
			sb.append(",\"SEQ\":"); 
		    sb.append(resultSetL.toJsonStr());
		    sb.append("}");
	    }else{//不存在数据
		    sb.append("\"EXISTS\": \"N\",\"rows\":{}"); 
	    }
	    sb.append("}");
	    return sb.toString();
	}

	/**
	 * 根据用户ID和交互式报表ID获取所有交互式报表的定义，返回Json格式
	 *   IrrHead=Irr Head，就是报表定义头
	 * @param userId
	 * @param interactCode
	 * @return 所有交互式报表的Header定义
	 * @throws Exception
	 */
	public String getIrrHead(Long userId,String interactCode) throws Exception{
		StringBuffer sb = new StringBuffer();
		String sql;
		//根据用户和报表的名称获取该用户所有可以打开的文件夹
		sql="SELECT HEADER_ID,USER_INTERACT_NAME "
				+ " FROM XYG_ALD_INTERACT_HEADERS "
				+ "  WHERE 1=1 "
				+ " AND INTERACT_CODE =:1  "
				+ " AND (USER_ID = :2 OR PUBLIC_FLAG = 'Y') "
				+ " ORDER BY DECODE(USER_ID,:3,0,1),USER_INTERACT_NAME ";
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("1", interactCode);
		paramMap.put("2", userId);
		paramMap.put("3", userId);
		SqlResultSet resultSet=this.getDevJdbcTemplate().queryForResultSet(sql, paramMap);
		LogUtil.log("getIrrHead sql result:"+resultSet.getResultSet().size());
	    if(resultSet.getResultSet().size()>0){//存在数据
		    sb.append("{\"EXISTS\": \"Y\",\"rows\":"); 
		    sb.append(resultSet.toJsonStr());
		    sb.append("}");
	    }else{//不存在数据
		    sb.append("{\"EXISTS\": \"N\",\"rows\":{}}"); 
	    }
	    return sb.toString();
	}
	
	/**
	 * 保存交互式报表的定义，逻辑：无则新增，有则覆盖！
	 * @return PlsqlRetValue
	 * @throws Exception
	 */
	public PlsqlRetValue saveIrr(
			 Long userId
			,String interactCode
			,String userInteractName
			,String description
			,String publicFlag
			,String autoqueryFlag
			,String defaultFlag
			,String orderBy
			,int pageSize
			,String seq
			) throws Exception{
		String sql ="begin "
				+ "XYG_ALD_INTERACT_PKG.HANDLE_INTERACT( "
				+ ":1 "//P_USER_ID              NUMBER
				+ ",:2 "//P_INTERACT_CODE        VARCHAR2
				+ ",:3 "//P_USER_INTERACT_NAME   VARCHAR2
				+ ",:4 "//P_DESCRIPTION          VARCHAR2
				+ ",:5 "//P_PUBLIC_FLAG          VARCHAR2
				+ ",:6 "//P_AUTOQUERY_FLAG       VARCHAR2
				+ ",:7 "//P_DEFAULT_FLAG         VARCHAR2
				+ ",:8 "//P_ORDER_BY             VARCHAR2
				+ ",:9 "//P_Page_Size            NUMBER
				+ ",:10 "//P_LANGUAGE             VARCHAR2
				+ ",:11 "//P_INTERACT_LINES       VARCHAR2
				+ ",:"+PlsqlRetValue.PARAM1  //X_HEADER_ID            OUT NUMBER
				+ ",:"+PlsqlRetValue.RETCODE  //x_retcode              OUT NUMBER
				+ ",:"+PlsqlRetValue.ERRBUF  //x_errbuf               OUT VARCHAR2
				+ "); "
				+ "end ;";
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("1", userId);
		paramMap.put("2", interactCode);
		paramMap.put("3", userInteractName);
		paramMap.put("4", description);
		paramMap.put("5", publicFlag);
		paramMap.put("6", autoqueryFlag);
		paramMap.put("7", defaultFlag);
		paramMap.put("8", orderBy);
		paramMap.put("9", pageSize);
		paramMap.put("10", "ZHS");
		paramMap.put("11", seq);
		PlsqlRetValue ret=this.getDevJdbcTemplate().executeForRetValue(sql, paramMap);
		//Assert.isTrue(ret.getRetcode()==0, "HANDLE_INTERACT处理失败！信息："+ret.getErrbuf());
		return ret;
	}
}
