package xygdev.commons.springjdbc;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;

import xygdev.commons.entity.PlsqlRetValue;
import xygdev.commons.entity.SqlResultSet;
import xygdev.commons.entity.Emp;
import xygdev.commons.page.PagePub;
import xygdev.commons.util.Constant;
import xygdev.commons.util.TypeConvert;

/**
 * 专门用于测试JdbcPub的Java类。有一些实际的例子！项目在编写dao的时候，仿照这些实例来做即可。
 */
@Transactional(rollbackFor=Exception.class)
public class DevJdbcTemplateTest extends DevJdbcDaoSupport {
	//成员变量，方便测试用
	private DevJdbcTemplate devJdbc;
	private String sql;
	private Map<String,Object> paramMap=new HashMap<String,Object>();

	/**
	 * query查询类的测试实例
	 * <br/>实际使用需要注意的是，如果是用queryForList，则必须要编写Mapping类。可以直接写在Entity里面。
	 */
	public void queryTest(){
		devJdbc=this.getDevJdbcTemplate();
		int rows;
		sql="select count(*) from dual";
		rows = devJdbc.queryForInt(sql);
		System.out.println("rows:"+rows);
		paramMap.clear();
		//---------------queryForList
		List<Emp> empList=new ArrayList<Emp>();
		sql="select ROWNUM ROW_NUM,A.* from XYG_ALD_EMP A  where emp_id between :begin and :end";
		paramMap.clear();
		paramMap.put("begin", 1);
		paramMap.put("end", 2);
		try {
			empList=devJdbc.queryForList(sql, paramMap,new Emp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("empList:"+empList);
		System.out.println("empList.size"+empList.size());
		for(Emp emp:empList){
			System.out.println("1 empID:"+emp.getEmpId()+",empName:"+emp.getEmpName());
		}
		//---------------queryForObject
		Emp emp = new Emp();
		sql="select * from XYG_ALD_EMP  where emp_id in (1)";
		paramMap.clear();
		try {
			emp=devJdbc.queryForObject(sql, paramMap, new Emp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("2 empID:"+emp.getEmpId()+",empName:"+emp.getEmpName());
		//---------------queryForObjArray
		Object[] objArray = new Object[3];
		sql="select ENABLE_DATE from XYG_ALD_EMP  where emp_id in (1)";//EMP_ID,EMP_NUMBER,
		paramMap.clear();
		try {
			objArray=devJdbc.queryForObjArray(sql, paramMap);
			Object objSingle=devJdbc.queryForObjSingle(sql, paramMap);
			System.out.println(objSingle);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("2.1 empID:"+objArray[0]);//+",empName:"+objArray[1]+",enableDate:"+objArray[2]);
		//---------------queryForResultSet
		SqlResultSet rs=null;
		sql="select EMP_ID,EMP_NUMBER,FULL_NAME,HIRE_DATE,MANAGER_FULL_NAME"
				+ " from XYG_JBO_CRM_EMP_V  where emp_id in (83,278) AND 1=2";
		paramMap.clear();
		try{
			rs=devJdbc.queryForResultSet(sql, paramMap);
		} catch(Exception e){
			e.printStackTrace();
		}
		//列出rs的数据：
		System.out.println("1 rs:"+rs);
		
		//模拟分页的。注意，页面的栏位还是和数据库的栏位保持一致。		
		sql="SELECT * FROM XYG_JBO_CRM_EMP_V";
		sql=new PagePub().getPageSql(sql, "pageMinRow", "pageMaxRow");
		paramMap.clear();
		paramMap.put("pageMinRow", 11);
		paramMap.put("pageMaxRow", 12);
		try{
			rs=devJdbc.queryForResultSet(sql, paramMap);
		} catch(Exception e){
			e.printStackTrace();
		}
		//列出rs的数据：
		System.out.println("2 rs:"+rs);
	}

	/**
	 * clob dml类的测试实例
	 * <br/>由于clob或者blob是特殊的类型，所以处理麻烦点。对于读取是OK的，
	 * 但是，对于更新和插入，目前找不到很好的办法（将clob作为参数传入到jdbc里面）。
	 * <br/>只好当是String类型对clob字段来做dml！测试成功。
	 * 所以，对于CLOB还是可以这样子处理。但是，对于BLOB就不行了。
	 * 不过，对于图片等文件类型的，还是建议存在服务器比较好。
	 * <br/><b>2016.9.6已经可以成功用真正的Clob类型类传参！</b>
	 */
	public void queryClobTest() throws Exception{
		devJdbc=this.getDevJdbcTemplate();
		SqlResultSet rs=null;
		//主要是测试Clob的读取以及更新。
		//String sql = "SELECT RECEIVE_USER_DESC,MAIL_CONTENT FROM XYG_FFO_B2B_RECEIVE_V A WHERE SEND_ID = :1";
		String sql = "SELECT SEND_CONTENT FROM XYG_FFO_B2B_SEND A WHERE SEND_ID = :1";
		paramMap.clear();
		paramMap.put("1", 48);
		rs=devJdbc.queryForResultSet(sql, paramMap);
		System.out.println("rs:"+rs);
		List<Object[]> retList= devJdbc.queryForObjArrList(sql, paramMap);
		for(Object[] objArray:retList){
			for(Object val:objArray){
				System.out.println(TypeConvert.type2Str(val).replace(System.getProperty("line.separator"),Constant.ENTER_REPLACE_STR));
				//System.out.println(TypeConvert.type2Str(val).replaceAll(System.getProperty("line.separator"), "换行"));
			}
		}
		String str="abc\r\nbbb\rccc"+System.getProperty("line.separator")+"DDD";
		System.out.println("separator:"+System.getProperty("line.separator"));
		System.out.println("str:"+str);
		System.out.println("str2:"+str.replace(System.getProperty("line.separator"),Constant.ENTER_REPLACE_STR));
		//测试更新
		/*String sql2 = " UPDATE XYG_FFO_B2B_SEND "
		  +" SET SEND_CONTENT=:1 "
		  +" WHERE SEND_ID=:2 ";
		paramMap.clear();
		paramMap.put("1", "这个就是我要更新的内容！11111111");//clob的就按照String来处理即可。
		paramMap.put("2", 1);
		int updRow=devJdbc.update(sql2, paramMap);
		System.out.println("updRow:"+updRow);*/
		/*sql="begin "
				+ " UPDATE XYG_FFO_B2B_SEND "
				+ "  SET SEND_CONTENT=:1 "
				+ " WHERE SEND_ID=:2; "
				+  ":"+PlsqlRetValue.ERRBUF+":='处理完毕！'; "
				+  ":"+PlsqlRetValue.RETCODE+":=0; "
				+ "end; ";*/
		sql="declare "
				+ " l_clob clob; "
				+ " begin "
				+ " l_clob:=:4;"
				+ " INSERT INTO XYG_FFO_B2B_SEND(SEND_ID,SEND_USER_ID,SEND_TITLE,SEND_CONTENT,SEND_DATE) "
				+ " VALUES (:1,:2,:3,l_clob,SYSDATE); "
				+  ":"+PlsqlRetValue.ERRBUF+":='处理成功'; "
				+  ":"+PlsqlRetValue.RETCODE+":=0; "
				+ "end; ";
		/*sql="declare "
		+ " l_clob clob; "
		+ " begin "
		+ " XYG_FFO_TEST_CLOB(:1,:2,:3,:4); "//测试通过！
		+  ":"+PlsqlRetValue.ERRBUF+":='处理完毕！'; "
		+  ":"+PlsqlRetValue.RETCODE+":=0; "
		+ "end; ";*/
		/*paramMap.clear();
		//paramMap.put("1", TypeConvert.str2Clob("这个就是我要更新的内容！"));
		String clobStr=null;
		for(int i=0;i<10;i++){
			clobStr+="这个就是我要INSERT DE CLOB222............\n";
		}
		paramMap.put("1",this.getDevJdbcTemplate().queryForLong("select XYG_FFO_B2B_SEND_s.nextval from dual"));
		paramMap.put("2",1);
		paramMap.put("3", "TITLE101");
		paramMap.put("4", this.getOracleCLOB(clobStr));
		PlsqlRetValue ret=devJdbc.executeForRetValue(sql, paramMap);
		Assert.isTrue(ret.getRetcode()==0, "处理失败！信息："+ret.getErrbuf());
		System.out.println("ClobTest:"+ret);*/
	}
	
	/**
	 * 测试 java往oracle存储过程中传递数组
	 */
	public void arrayTest() throws Exception{
		System.out.println("status:"+TransactionInterceptor.currentTransactionStatus());
		devJdbc=this.getDevJdbcTemplate();
		String sql;
		sql=    " declare"
				+ " L_RECEIVE_USER_IDS XYG_FFO_B2B_SEND_PKG.NUM_TBL_TYPE; "
				+ " begin "
				+ " XYG_FFO_B2B_SEND_PKG.HANDLE_SEND( "
				+ " :SEND_USER_ID "
				+ " ,:SEND_TITLE "
				+ " ,:SEND_CONTENT "
				+ " ,:SEND_DATE "
				+ " ,:SEND_TYPE "
				+ " ,:RECEIVE_USER_IDS"//:RECEIVE_USER_IDS L_RECEIVE_USER_IDS
				+ " ,:"+PlsqlRetValue.PARAM1
				+ " ,:"+PlsqlRetValue.RETCODE
				+ " ,:"+PlsqlRetValue.ERRBUF
				+ " ); "
				+ "end; ";
		paramMap.clear();
		paramMap.put("SEND_USER_ID", 1L);
		paramMap.put("SEND_TITLE", "JAVA调用Test");
		paramMap.put("SEND_CONTENT", this.getOracleCLOB(null));
		paramMap.put("SEND_DATE", TypeConvert.str2sDate("2016-9-5"));
		paramMap.put("SEND_TYPE", "USERS");
		String idList="2";
		String[] array=null;
		System.out.println("idList:"+idList+",is null:"+TypeConvert.isNullValue(idList));
		if(!TypeConvert.isNullValue(idList)) {
			array=idList.split(",");
			System.out.println("idList:"+idList);
		}
		System.out.println("array:"+array+",is null:"+TypeConvert.isNullValue(array));
		//Long[] array= {3L};
		//ARRAY recUserIds=;
		//System.out.println("getNativeJdbcConnection:"+this.getNativeJdbcConnection());
		paramMap.put("RECEIVE_USER_IDS", this.getOracleARRAY("APEX_UAT.XYG_FFO_B2B_SEND_PKG.NUM_TBL_TYPE", array));
		PlsqlRetValue ret=devJdbc.executeForRetValue(sql, paramMap);
		System.out.println("Test:"+ret);
		//测试回滚
		//DevJdbcSubProcess.setRollbackOnly();
		//TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
		
	}
	
	/**
	 * update更新类的测试实例，包括DML。
	 * <br/>@Transactional测试结果：以某个方法(或者类)作为原子，如果抛出异常则整个事务回滚。
	 * <br/>需要注意的是，如果是嵌套调用的方法，则统一以当前调用的方法是否存在事务注释为主。
	 * <br/>举例子，如果方法1注释了事务，方法2注释了事务，方法3调用方法1和2，并没有注释事务。
	 * <br/>在调用方法3的时候，按照没有事务控制来处理！所有的处理都会被自动提交。
	 * <br/>但是如果单独调用方法1和方法2，均有事务处理。
	 * <br/>不过要注意，虽然1，2都有事务，但是是分开事务控制。换句话说，在main里分别调用他们：方法1执行成功了，则会提交。即使方法2出错，方法1不会回滚。
	 * <br/>所以，如果要实现方法1+方法2的事务原子性，要新增一个方法调用1和2，并且该方法启用事务注释。
	 * <br/>将注释提到类级，其实和将注释分别放到方法级的效果一样！
	 * <br/>关于语句级的回滚：TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	 * <br/>逻辑是：标记某个事务必须给回滚！注意，这个是针对某1个事务来说。只要是同一个事务，做了这个标记，则必须自动回滚！
	 * <br/>所以，如果想实现这种吊毛的需求：某一段执行完毕要提交，另外一段如果出错要回滚，则可以将这2段处理封装为2个方法（2个事务）即可。
	 */
	public void updateTest() throws Exception{
		devJdbc=this.getDevJdbcTemplate();
		int updRow;
		//---------------update
		/*sql = "UPDATE XYG_JBO_CRM_EMP  SET FIRST_NAME='宪法-3' WHERE EMP_ID IN (:emp1,:emp2)";
		//sql = "DELETE XYG_JBO_CRM_EMP WHERE EMP_ID IN (:emp1,:emp2)";
		paramMap.clear();
		paramMap.put("emp1", "631");
		paramMap.put("emp2", "632");
		updRow=devJdbc.update(sql, paramMap);
		System.out.println("updRow:"+updRow);
		Assert.isTrue(updRow == 10, "删除失败");
		
		paramMap.clear();
		paramMap.put("emp1", "633");
		paramMap.put("emp2", "634");
		updRow=devJdbc.update(sql, paramMap);
		System.out.println("updRow:"+updRow);*/
		//Assert.isTrue(updRow == 10, "删除失败");
		//---测试新增
		sql="insert into XYG_ALD_EMP"
				+ "(EMP_ID,EMP_NUMBER,EMP_NAME,HIRE_DATE,SALARY,ENABLE_DATE,DISABLED_DATE,REMARK)"
				+ " values (:emp_id,:emp_number,:emp_name,:hire_date,:salary,:enable_date,:disabled_date,:remark)";
		paramMap.clear();
		paramMap.put("emp_id", devJdbc.queryForInt("select XYG_ALD_EMP_S.nextval from dual"));
		paramMap.put("emp_number", "214492");
		paramMap.put("emp_name", "王宪");
		paramMap.put("hire_date", new Timestamp(System.currentTimeMillis()));
		paramMap.put("salary", "1000");
		paramMap.put("enable_date", new Timestamp(System.currentTimeMillis()));
		paramMap.put("disabled_date", null);
		paramMap.put("remark", "测试devJdbc.update 直接新增记录");
		updRow=devJdbc.update(sql, paramMap);
		System.out.println("updRow:"+updRow);
	}
	
	/**
	 * execute执行plsql类的测试实例。
	 */
	public void executeTest(){
		devJdbc=this.getDevJdbcTemplate();
		//---------------execute
		sql="declare "
				+ " l_line_id number;"
				+ " l_retcode number;"
				+ " l_errbuf varchar2(480);"
				+ "begin "
				+ "XYG_APDM_COMMON_PKG.ACCEPT_RANDOM( "
				+ ":1 "//P_HEADER_ID
				+ ",:2 "//P_ACCEPT_USER_ID
				+ ",:3 "//P_ACCEPT_DATE
				+ ",:4 "//P_DESCRIPTION
				+ ",:5"//X_LINE_ID              OUT NUMBER
				//+ ",l_retcode "
				//+ ",l_errbuf "
				+ ",:"+PlsqlRetValue.RETCODE  //x_retcode              OUT NUMBER
				+ ",:"+PlsqlRetValue.ERRBUF  //x_errbuf               OUT VARCHAR2
				+ "); "
				+ "end ;";
		paramMap.clear();
		paramMap.put("1", "6");
		paramMap.put("2", "21");
		paramMap.put("3", new Timestamp(System.currentTimeMillis()));
		paramMap.put("4", "4");
		//定义输出参数
		Map<String,Integer> outParamMap=new HashMap<String,Integer>();
		outParamMap.put(PlsqlRetValue.RETCODE, Types.INTEGER);
		outParamMap.put(PlsqlRetValue.ERRBUF, Types.VARCHAR);
		outParamMap.put("5", Types.VARCHAR);
		try {
			Map<String,Object> outValueMap=devJdbc.execute(sql, paramMap,outParamMap);
			for(String key:outValueMap.keySet()){
				System.out.println(key+":"+outValueMap.get(key));
			}
			//未解决问题：NamedParameterJdbcTemplate 无法注册输出参数！！2016.7.29 好像也无法解决！
			//原因是因为NamedParameterJdbcTemplate在执行PLSQL的时候，只支持PreparedStatement!不支持CallableStatement！
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//------------------------executePlsql
		sql="declare "
				+ " l_line_id number;"
				+ " l_retcode number;"
				+ " l_errbuf varchar2(480);"
				+ "begin "
				+ "XYG_APDM_COMMON_PKG.ACCEPT_RANDOM( "
				+ ":1 "//P_HEADER_ID
				+ ",:2 "//P_ACCEPT_USER_ID
				+ ",:3 "//P_ACCEPT_DATE
				+ ",:4 "//P_DESCRIPTION
				+ ",:"+PlsqlRetValue.PARAM1 //X_LINE_ID              OUT NUMBER
				//+ ",l_retcode "
				//+ ",l_errbuf "
				+ ",:"+PlsqlRetValue.RETCODE  //x_retcode              OUT NUMBER
				+ ",:"+PlsqlRetValue.ERRBUF  //x_errbuf               OUT VARCHAR2
				+ "); "
				+ "end ;";
		paramMap.clear();
		paramMap.put("1", "6");
		paramMap.put("2", "21");
		paramMap.put("3", new Timestamp(System.currentTimeMillis()));
		paramMap.put("4", "4");
		paramMap.put(PlsqlRetValue.PARAM1, "4");
		PlsqlRetValue ret = null;
		try {
			ret = devJdbc.executeForRetValue(sql, paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ret);
	}
		
	public void executeTransTest() throws Exception{
		devJdbc=this.getDevJdbcTemplate();
		sql="begin "
				+ " UPDATE XYG_JBO_CRM_EMP "
				+ "  SET FIRST_NAME='A-19' "
				+ " WHERE EMP_ID IN (:emp1,:emp2); "
				+  ":"+PlsqlRetValue.ERRBUF+":='处理完毕！'; "
				+  ":"+PlsqlRetValue.RETCODE+":=0; "
				+ "end; ";
		paramMap.clear();
		paramMap.put("emp1", "631");
		paramMap.put("emp2", "632");
		PlsqlRetValue ret=devJdbc.executeForRetValue(sql, paramMap);
		Assert.isTrue(ret.getRetcode()==0, "处理失败！信息："+ret.getErrbuf());
		System.out.println("executeTransTest:"+ret);
		//throw new RuntimeException("test uncheck exception rollback");
		//TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		//DevTransactionPub.setRollbackOnly();
	}
	
	public void executeTransTest2() throws Exception{
		devJdbc=this.getDevJdbcTemplate();
		sql="begin "
				+ " UPDATE XYG_JBO_CRM_EMP "
				+ "  SET FIRST_NAME='A-19' "
				+ " WHERE EMP_ID IN (:emp1,:emp2); "
				+  ":"+PlsqlRetValue.ERRBUF+":='处理失败！'; "
				+  ":"+PlsqlRetValue.RETCODE+":=1; "
				+ "end; ";
		paramMap.clear();
		paramMap.put("emp1", "633");
		paramMap.put("emp2", "634");
		PlsqlRetValue ret=devJdbc.executeForRetValue(sql, paramMap);
		//Assert.isTrue(ret.getRetcode()==0, "处理失败！结果："+ret.getErrbuf());//抛出异常
		System.out.println("executeTransTest2:"+ret);
		//throw new Exception("test check exception rollback");
		//TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
	
	public void executeTransTest3() throws Exception{		
		//TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		System.out.println("3.1");
		executeTransTest();
		System.out.println("3.2");
		executeTransTest2();
		System.out.println("3.4");
		DevJdbcSubProcess.setRollbackOnly();
	}
	

	@SuppressWarnings("resource")
	public static void main(String[] args){
    	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    	DevJdbcTemplateTest devJdbcTest= (DevJdbcTemplateTest)context.getBean("DevJdbcTemplateTest");
    	//devJdbcTest.queryTest();
    	try {
    		//System.out.println("0");
			//devJdbcTest.executeTransTest();
    		//System.out.println("1");
			//devJdbcTest.executeTransTest2();
    		//System.out.println("2");
			//devJdbcTest.executeTransTest3();
    		//devJdbcTest.updateTest();
    		//System.out.println("1");
    		//devJdbcTest.queryClobTest();
    		//devJdbcTest.arrayTest();
    		//System.out.println("2");
    		xygdev.commons.util.Constant.DEBUG_MODE = true;
    		int test1 =devJdbcTest.getDevJdbcTemplate().queryForInt("select count(*) from dual");
    		System.out.println("test1:"+test1);
    		//devJdbcTest.queryTest();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
    	/*
    	String str2="abcd\nabcd\reeee";
    	System.out.println("str2:"+str2);
    	System.out.println("str2 \\n:"+str2.replaceAll("\r", "\\r"));
    	System.out.println("str2 \\sys:"+str2.replaceAll(System.getProperty("line.separator"), Constant.LSEP_REPLACE_STR));
    	*/
	}
}






/*
//事务处理功能测试备份2016.8.12：
	public void executeTransTest3() throws Exception{		
		System.out.println("3.1 txManager:"+this.getTransactionManager());
		TransactionStatus status = this.getTransactionStatus();//+启用事务状态处理
		executeTransTest();
		System.out.println("3.2");
		this.getTransactionManager().rollback(status); //-处理本次状态，释放对应的事务处理状态
		
		executeTransTest();//没有启用事务处理的就自动提交
		
		TransactionStatus status2 = this.getTransactionStatus();//一个新的事务状态。如果启用了状态，如果没处理，则自动回滚。
		executeTransTest2();
		System.out.println("3.4");
		this.getTransactionManager().commit(status2);
		System.out.println("3.5");
		//this.getTransactionManager().rollback(this.getTransactionStatus()); 
		//this.getTransactionManager().rollback(status); 
	} 
	
CREATE OR REPLACE PROCEDURE XYG_FFO_TEST_CLOB(P_SEND_ID NUMBER,P_SEND_USER_ID NUMBER,P_SEND_TITLE VARCHAR2,P_SEND_CONTENT CLOB)
IS
BEGIN
INSERT INTO XYG_FFO_B2B_SEND(SEND_ID,SEND_USER_ID,SEND_TITLE,SEND_CONTENT,SEND_DATE)
VALUES (P_SEND_ID,P_SEND_USER_ID,P_SEND_TITLE,P_SEND_CONTENT,SYSDATE);
END;
 */
