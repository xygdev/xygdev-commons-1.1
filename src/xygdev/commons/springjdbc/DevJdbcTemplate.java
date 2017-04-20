package xygdev.commons.springjdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.util.Assert;

import xygdev.commons.entity.PlsqlRetValue;
import xygdev.commons.entity.SqlResultSet;
import xygdev.commons.util.LogUtil;

/**
 * 对SpringJDBC的代码的二次封装
 * 主要是方便使用。
 * </br>就是：一些Oracle数据库的查询以及DML处理和调用Pkg的封装好的公用类。属于数据库访问的专用代码。
 * </br>整个处理基于NamedParameterJdbc，可以完美实现输入输出参数的自动匹配
 * </br>统一逻辑：如果查询的没数据，则统一返回为空对象，而不是null！
 * @author Sam.T 2016/8/2
 * @version 1.0
 */
public class DevJdbcTemplate{
	
	/** The NamedParameterJdbcTemplate we are wrapping */
	private final NamedParameterJdbcOperations classicNamedParameterJdbcOperations;
	/** The JdbcTemplate we are wrapping-->为了找到connection来客户化！ */
	private final JdbcTemplate classicJdbcTemplate;
	/**
	 * Create a new DevJdbcTemplate for the given {@link DataSource}.
	 * <p>Creates a classic Spring {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate} and wraps it.
	 * @param dataSource the JDBC DataSource to access
	 */
	public DevJdbcTemplate(DataSource dataSource) {
		Assert.notNull(dataSource, "DevJdbcTemplate-->DataSource must not be null");
		this.classicNamedParameterJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
		this.classicJdbcTemplate = new JdbcTemplate(dataSource);
	}
	/**
	 * Create a new DevJdbcTemplate for the given classic
	 * Spring {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}.
	 * @param classicNamedParameterJdbcOperations the classic Spring NamedParameterJdbcTemplate to wrap
	 */
	public DevJdbcTemplate(NamedParameterJdbcOperations classicNamedParameterJdbcOperations,JdbcOperations classicJdbcTemplate) {
		Assert.notNull(classicNamedParameterJdbcOperations, "DevJdbcTemplate-->NamedParameterJdbcTemplate must not be null");
		Assert.notNull(classicJdbcTemplate, "DevJdbcTemplate-->classicJdbcTemplate must not be null");
		this.classicNamedParameterJdbcOperations = classicNamedParameterJdbcOperations;
		this.classicJdbcTemplate = (JdbcTemplate) classicJdbcTemplate;
	}
	
	/**
	 * Expose the classic Spring JdbcTemplate to allow invocation of
	 * less commonly used methods.
	 */
	public JdbcTemplate getJdbcTemplate() {
		return this.classicJdbcTemplate;
	}

	/**
	 * Expose the classic Spring JdbcTemplate to allow invocation of
	 * less commonly used methods.
	 */
	public NamedParameterJdbcOperations getNamedParameterJdbcOperations() {
		return this.classicNamedParameterJdbcOperations;
	}

	@SuppressWarnings("deprecation")
	public int queryForInt(String sql){
		LogUtil.log("queryForInt sql:"+sql);
		return this.getNamedParameterJdbcOperations().getJdbcOperations().queryForInt(sql);
	}
	
	@SuppressWarnings("deprecation")
	public long queryForLong(String sql){
		LogUtil.log("queryForLong sql:"+sql);
		return this.getJdbcTemplate().queryForLong(sql);
	}
	
	
	/**
	 * 返回对象列表的查询。
	 * <br/>注意：不再单独封装分页查询的功能，因为太简单。用getPageSql方法自动拼一下SQL即可。
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 * @param rowMapper sql查询column的结果和Entity对象的成员变量的匹配关系
	 * @return List<T> 对象列表
	 */
	public <T> List<T> queryForList(String sql,Map<String,Object> paramMap,RowMapper<T> rowMapper) throws Exception{
		LogUtil.log("queryForList sql:"+sql);
		return getNamedParameterJdbcOperations().query(sql, paramMap, rowMapper);
	}
	
	/**
	 * 返回对象列表的Map值。
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 * @param rowMapper sql查询column的结果和Entity对象的成员变量的匹配关系
	 * @return Map<String,Object> 对象列表的Map值
	 */
	public <T> Map<String,Object> queryForMap(String mapkey,String sql,Map<String,Object> paramMap,RowMapper<T> rowMapper) throws Exception{
		LogUtil.log("queryForMap sql:"+sql);
		List<T> queryList=queryForList(sql, paramMap, rowMapper);
		Map<String,Object> queryMap=new HashMap<String,Object>();
		queryMap.put(mapkey, queryList);
		return queryMap;
	}

	/**
	 * 返回唯一对象。
	 * <br/>这个逻辑和Oracle SQL的SELECT INTO处理很类似！
	 * <br/>这里只可以返回唯一的一行！无行返回或者返回多行都会报错！
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 * @param rowMapper sql查询column的结果和Entity对象的成员变量的匹配关系
	 */
	public <T> T queryForObject(String sql,Map<String,Object> paramMap,RowMapper<T> rowMapper) throws Exception{
		LogUtil.log("queryForObject sql:"+sql);
		//如果NO_DATA_FOUND，会报错：EmptyResultDataAccessException。
		//如果TOO_MANY_ROWS，则报错：IncorrectResultSizeDataAccessException
		//这个逻辑和Oracle SQL的处理很类似！这里只可以返回唯一的一行！
		return getNamedParameterJdbcOperations().queryForObject(sql, paramMap, rowMapper);
	}


	/**
	 * 返回SQL结果的Object的数组列表。和queryForObject的区别是：减少rowMapper的使用！
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 */
	public List<Object[]> queryForObjArrList(String sql,Map<String,Object> paramMap) throws Exception{
		LogUtil.log("queryForObjArrList sql:"+sql);
		SqlRowSet rs=getNamedParameterJdbcOperations().queryForRowSet(sql, paramMap);
		SqlRowSetMetaData rsmt = rs.getMetaData();
		String[] colName=new String[rsmt.getColumnCount()];
		for(int i=1;i<=rsmt.getColumnCount();i++){
			colName[i-1]= rsmt.getColumnName(i);
		}
		List<Object[]> rsList=new ArrayList<Object[]>();
		while(rs.next()){
			Object[] objArray=new Object[rsmt.getColumnCount()];
			for(int i=0;i<rsmt.getColumnCount();i++){
				objArray[i]=rs.getObject(i+1);
		    }
			rsList.add(objArray);
		}
		return rsList;
		/*
		return queryForObject(sql,paramMap,
				new RowMapper(){
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException
					{
						return TypeConvert.type2Str(rs.getObject(1));
					}
		});*/
	}
	
	/**
	 * 返回唯一行的Object列表。和queryForObject的区别是：减少rowMapper的使用！
	 * <br/>需要注意的是，如果木有返回或者返回多行会报错！这个逻辑和Oracle SQL的SELECT INTO处理很类似！
	 * <br/>如果要实现这种：SELECT COL1,COL2,COL3 INTO XX1,XX2,XX3 FROM TABLE WHERE 1=1这种，也可以用这个函数。
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 */
	public Object[] queryForObjArray(String sql,Map<String,Object> paramMap) throws Exception{
		return DataAccessUtils.requiredSingleResult(queryForObjArrList(sql,paramMap));
	}

	/**
	 * 返回唯一一个的Object。和queryForObject的区别是：减少rowMapper的使用！
	 * <br/>需要注意的是，如果木有返回或者返回多行会报错！这个逻辑和Oracle SQL的SELECT INTO处理很类似！
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 */
	public Object queryForObjSingle(String sql,Map<String,Object> paramMap) throws Exception{
		Object[] objArray=queryForObjArray(sql,paramMap);
		if(objArray.length!=1){
			throw getJdbcTemplate().getExceptionTranslator().translate("queryForObjSingle查询栏位只可以唯一1个", sql, new SQLException());
		}
		return objArray[0];
	}
	
	/**
	 * 返回SqlResultSet对象。
	 * <br/>这个的作用是非常方便返回没有封装Entity对象的结果。
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 * @return SqlResultSet 整个结果集
	 */
	public SqlResultSet queryForResultSet(String sql,Map<String,Object> paramMap) throws Exception{
		LogUtil.log("queryForResultSet sql:"+sql);
		SqlRowSet rs=getNamedParameterJdbcOperations().queryForRowSet(sql, paramMap);
		SqlRowSetMetaData rsmt = rs.getMetaData();
		String[] colName=new String[rsmt.getColumnCount()];
		for(int i=1;i<=rsmt.getColumnCount();i++){
			colName[i-1]= rsmt.getColumnName(i);
		}
		//向resultSet填充值
		List<Object[]> resultSet=new ArrayList<Object[]>();
		while(rs.next()){
			Object[] rowObj=new Object[rsmt.getColumnCount()];
			for(int i=0;i<rsmt.getColumnCount();i++){
				rowObj[i]=rs.getObject(i+1);
		      }
			resultSet.add(rowObj);
		}
		return new SqlResultSet(colName,resultSet);
	}
	
	/**
	 * update方法用于执行新增、修改、删除等DML语句；返回处理的数量
	 * @param sql 查询语句
	 * @param paramMap 查询参数Map列表
	 * @return int 本次处理影响的行数
	 */
	public int update(String sql,Map<String,Object> paramMap) throws Exception{
		return getNamedParameterJdbcOperations().update(sql, paramMap);
	}

	/**
	 * 执行PLSQL的处理，不过可惜只可以处理输入参数，并不可以做输出参数的处理！
	 * @param sql 处理语句
	 * @param paramMap 查询参数Map列表
	 * @param action PreparedStatement的回调函数
	 * @return <T> 回调函数返回的对象
	 */
	public <T> T execute(String sql,Map<String,Object> paramMap,PreparedStatementCallback<T> action) throws Exception{
		LogUtil.log("execute sql:"+sql);
		return getNamedParameterJdbcOperations().execute(sql, paramMap, action);
	}

	public boolean checkExecuteParam=true;
	/**
	 * 执行PLSQL处理，这里完全可以输入输出参数的处理！而且完美支持输入输出参数。
	 * <br/>这个是JdbcPub里面唯一用核心是用原生的jdbc写的，借用NamedParameterJdbcTemplate的核心功能，完美解决参数顺序的问题。
	 * <br/>注意：目前暂时不支持游标等特殊参数的处理。以后可以新增！
	 * @param sql 处理语句
	 * @param inParamMap 输入参数
	 * @param outParamMap 输出参数
	 * @return Map<String, Object> 输出参数对应的值
	 */
	@SuppressWarnings("resource")
	public Map<String, Object> execute(String sql,Map<String,Object> inParamMap,Map<String,Integer> outParamMap) throws Exception{
		LogUtil.log("execute sql:"+sql);
    	Map<String,Object> retValueMap = new HashMap<String,Object>(); 
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
		SqlParameterSource paramSource =new MapSqlParameterSource(inParamMap);
		List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        Connection con=null;
		CallableStatement cs=null;
		try{
	        con=DataSourceUtils.getConnection(getJdbcTemplate().getDataSource());
	        cs=(CallableStatement)con.prepareCall(sql);
	        StringBuffer paramLog=new StringBuffer();
	        //declaredParameters：sql里面所有定义的参数列表
	        for(SqlParameter sqlParam:declaredParameters){
	        	if(inParamMap.containsKey(sqlParam.getName())){
	        		if(xygdev.commons.util.Constant.DEBUG_MODE)paramLog.append("[inParam:"+sqlParam.getName()+",value:"+inParamMap.get(sqlParam.getName())+"]");
	            	cs.setObject(sqlParam.getName(), inParamMap.get(sqlParam.getName()));
	        	}
	        	if(outParamMap.containsKey(sqlParam.getName())){
	        		if(xygdev.commons.util.Constant.DEBUG_MODE)paramLog.append("[outParam:"+sqlParam.getName()+"]");
	            	cs.registerOutParameter(sqlParam.getName(), outParamMap.get(sqlParam.getName()));
	            	retValueMap.put(sqlParam.getName(),null);
	        	}
	        }
	        LogUtil.log(paramLog.toString());
	        if(checkExecuteParam){//验证参数
	        	for(String key:outParamMap.keySet()){
	        		if(!(retValueMap.containsKey(key))){
	        			throw new SQLException("参数:"+key+" 不存在sql的参数列表");
	        		}
	        	}
	        }
	        cs.execute();
	        for(String key:retValueMap.keySet()){
	        	retValueMap.put(key, cs.getObject(key));  
	        }
		}catch (SQLException ex) {
			JdbcUtils.closeStatement(cs);
			cs = null;
			DataSourceUtils.releaseConnection(con, getJdbcTemplate().getDataSource());
			con = null;
			LogUtil.log("execute ex LOG:"+ex.toString());
			throw getJdbcTemplate().getExceptionTranslator().translate("xyg-execute", sql, ex);
		}finally {
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, getJdbcTemplate().getDataSource());
		}
		return retValueMap;
	}
	
	/**
	 * 直接执行plsql的存储过程，只有输入参数的封装
	 * <p> 具体用法请看完整的execute(sql,inParamMap,outParamMap)
	 * @param inParamMap 输入参数
	 */
	@SuppressWarnings("unused")
	public void execute(String sql,Map<String,Object> inParamMap) throws Exception{
		Map<String,Object> retValueMap= execute(sql,inParamMap,new HashMap<String,Integer>());
	}
	
	/**
	 * 执行PLSQL处理，输出prv=PlsqlRetValue。这里完全可以输入输出参数的处理。
	 * <br/>输出参数默认是自动匹配的(自动匹配PlsqlRetValue)，但是编写PLSQL的时候必须要注意命名规则。
	 * <br/>
	 * @param sql 处理语句
	 * @param inParamMap 输入参数
	 * @return PlsqlRetValue 对应的输出参数的返回值
	 */
	public PlsqlRetValue executeForRetValue(String sql,Map<String,Object> inParamMap) throws Exception{
		Map<String,Integer> outParamMap=new HashMap<String,Integer>();
		PlsqlRetValue ret=new PlsqlRetValue();
		checkExecuteParam=false;
		outParamMap.put(PlsqlRetValue.RETCODE, Types.INTEGER);
		outParamMap.put(PlsqlRetValue.ERRBUF, Types.VARCHAR);
		outParamMap.put(PlsqlRetValue.PARAM1, Types.VARCHAR);
		outParamMap.put(PlsqlRetValue.PARAM2, Types.VARCHAR);
		outParamMap.put(PlsqlRetValue.PARAM3, Types.VARCHAR);
		outParamMap.put(PlsqlRetValue.PARAM4, Types.VARCHAR);
		outParamMap.put(PlsqlRetValue.PARAM5, Types.VARCHAR);
		Map<String,Object> retValueMap=execute(sql,inParamMap,outParamMap);
		for(String key:retValueMap.keySet()){
			if(key.equals(PlsqlRetValue.RETCODE)) {
				ret.setRetcode(Integer.parseInt(retValueMap.get(key).toString()));
				continue;
			}
			if(key.equals(PlsqlRetValue.ERRBUF)) {
				ret.setErrbuf((String) retValueMap.get(key));
				continue;
			}
			if(key.equals(PlsqlRetValue.PARAM1)) {
				ret.setParam1((String) retValueMap.get(key));
				continue;
			}
			if(key.equals(PlsqlRetValue.PARAM2)) {
				ret.setParam2((String) retValueMap.get(key));
				continue;
			}
			if(key.equals(PlsqlRetValue.PARAM3)) {
				ret.setParam3((String) retValueMap.get(key));
				continue;
			}
			if(key.equals(PlsqlRetValue.PARAM4)) {
				ret.setParam4((String) retValueMap.get(key));
				continue;
			}
			if(key.equals(PlsqlRetValue.PARAM5)) {
				ret.setParam5((String) retValueMap.get(key));
				continue;
			}
		}
		return ret;
	}
}
