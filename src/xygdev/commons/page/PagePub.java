package xygdev.commons.page;

import java.util.HashMap;
import java.util.Map;

import xygdev.commons.entity.SqlResultSet;
import xygdev.commons.springjdbc.DevJdbcDaoSupport;
import xygdev.commons.util.LogUtil;


/**
 * 封装好的page共用处理类。
 * @author Sam.T 2016.8.10
 */
public class PagePub extends DevJdbcDaoSupport {
	/**
	 * 获取分页的完整SQL
	 * @param sql 分页查询的基础sql语句
	 * @param pageMinRowParaName 分页起始行的参数名称
	 * @param pageMaxRowParaName 分页结束行的参数名称
	 * @return String 分页的完整SQL
	 */
	public String getPageSql(String sql,String pageMinRowParaName,String pageMaxRowParaName){
		if(pageMinRowParaName==null)
			pageMinRowParaName="pageMinRow";
		if(pageMaxRowParaName==null)
			pageMaxRowParaName="pageMaxRow";
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT /*+ FIRST_ROWS */ R.*  FROM (");
		sb.append(" SELECT ROWNUM RN,INNER_Q.* ");
		sb.append(" FROM (");
		sb.append(sql);
		sb.append(" ) INNER_Q ");
		sb.append(" WHERE ROWNUM <= :"+pageMaxRowParaName+" ");
		sb.append(" ) R ");
		sb.append(" WHERE RN >= :"+pageMinRowParaName+" ");
		return sb.toString();
	}
	
	/**
	 * 返回分页的数据结果，SqlResultSet对象。
	 * <br/> 这里主要是封装了最大最小行的查询参数。
	 * @param sql 基础查询语句，包括order by
	 * @param paramMap 查询参数Map列表，不包括最小最大页的参数！
	 * @return SqlResultSet 整个结果集
	 */
	public SqlResultSet qPageForResultSet(String sql,Map<String,Object> paramMap,int pageMinRow,int pageMaxRow) throws Exception{
		String sqlPage=getPageSql(sql, "pageMinRowPara", "pageMaxRowPara");
		LogUtil.log("qPageForResultSet-->sqlPage:"+sqlPage);
		if(paramMap==null){
			paramMap=new HashMap<String,Object>();
		}
		paramMap.put("pageMinRowPara", pageMinRow);
		paramMap.put("pageMaxRowPara", pageMaxRow);
		LogUtil.log("qPageForResultSet-->paramMap:"+paramMap);
		return this.getDevJdbcTemplate().queryForResultSet(sqlPage, paramMap);
	}

	/**
	 * 返回分页的结果的json数据的封装函数
	 * <br/> 这里主要是封装了分页查询结果的整个过程。一步到位！
	 * <br/> 需要注意的是，如果goLastPage=真，才需要计算totalRecs！所以，goLastPage的值需要谨慎赋值！
	 * 为了节省服务器效率。毕竟计算所有行太慢！当数据量大的时候
	 * @param sql 基础查询语句，包括order by
	 * @param paramMap 查询参数Map列表，不包括最小最大页的参数！
	 * @return String Json数据集合。
	 */
	public String qPageForJson(String sql,Map<String,Object> paramMap,int pageSize,int pageNo,boolean goLastPage) throws Exception{
		StringBuffer sb = new StringBuffer();
		boolean firstPageFlag=PageAnalyze.getFirstPageFlag(pageNo);
		int recsSize=0;
		int totalPages=0;
		long totalRecs=0;
		boolean lastPageFlag=false;
		SqlResultSet rs=null;
		int pageMinRow=0;
		int pageMaxRow=0;
		if(goLastPage==false){
			pageMinRow=PageAnalyze.getPageMinRow(pageNo, pageSize);
			pageMaxRow=PageAnalyze.getPageMaxRow(pageNo, pageSize);
			rs=qPageForResultSet(sql,paramMap,pageMinRow, pageMaxRow);
			recsSize=rs.getResultSet().size();
			//System.out.println(recsSize);
			if(recsSize>0){
			    pageMaxRow=pageMinRow+recsSize-1;
		    }else{
		    	pageMinRow=0;
		    	pageMaxRow=0;
		    }
		}else{
			//totalRecs=this.getDevJdbcTemplate().queryForLong("select count(*) from ("+sql+")");
			totalRecs=Long.parseLong(String.valueOf(this.getDevJdbcTemplate().queryForObjSingle("select count(*) from ("+sql+")", paramMap)));
			totalPages=PageAnalyze.getTotalPages(totalRecs, pageSize);			
			pageMinRow=PageAnalyze.getPageMinRow(totalPages, pageSize);
			pageMaxRow=PageAnalyze.getLastPageMaxRow(totalPages, pageSize, totalRecs);
			lastPageFlag=PageAnalyze.getLastPageFlag(pageNo, pageSize, totalPages, recsSize);
			rs=qPageForResultSet(sql,paramMap,pageMinRow, pageMaxRow);
		}
	    sb.append("{\"pageMinRow\":\""+pageMinRow+"\"");
	    sb.append(",\"pageMaxRow\":\""+pageMaxRow+"\"");
	    sb.append(",\"firstPageFlag\":\""+firstPageFlag+"\"");
	    sb.append(",\"lastPageFlag\":\""+lastPageFlag+"\"");
	    sb.append(",\"totalPages\":\""+totalPages+"\"");
		sb.append(",\"rows\":");
		sb.append(rs.toJsonStr());
	    sb.append("}"); 
	    //log(sb.toString());
		return sb.toString();
	}

	/**
	 * 根据SQL返回json数据的封装函数
	 * <br/> 主要是封装一些Hardcode：rows。因为前端的Lov和List都是写好用rows抓取的。
	 * @param sql 基础查询语句，包括order by
	 * @param paramMap 查询参数Map列表
	 * @return String Json数据集合。例如：{"rows":[{xxx},{xxx}...]}
	 */
	public String qSqlForJson(String sql,Map<String,Object> paramMap) throws Exception{
		return "{\"rows\":"+this.getDevJdbcTemplate().queryForResultSet(sql, paramMap).toJsonStr()+"}";
	}
	
	/*验证的不用封装也是可以的。
	public String validForJson(String sql,Map<String,Object> paramMap) throws Exception{
		StringBuffer sb=new StringBuffer();
		SqlResultSet rs=this.getDevJdbcTemplate().queryForResultSet(sql, paramMap);
		sb.append("{\"rows\":");
		sb.append(rs.toJsonStr());
		sb.append("}");
		return sb.toString();
	}*/
	/*
	public static void main(String[] args){
    	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    	PagePub pagePub= (PagePub)context.getBean("PagePub");
		long totalRecs=0;
		int pageSize=10;//Integer.parseInt(req.getParameter("pageSize"));
		int pageNo=1;//Integer.parseInt(req.getParameter("pageNo"));
		boolean goLastPage=false;//Boolean.parseBoolean(req.getParameter("goLastPage"));
		String sql="select * from XYG_JBO_CRM_EMP_V A WHERE 1=1";
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("1", "1");
		if(goLastPage==true){
			totalRecs=1000;
		}
		try {
			System.out.println(pagePub.qPageForJson(sql, null, pageSize, pageNo, goLastPage, totalRecs));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
