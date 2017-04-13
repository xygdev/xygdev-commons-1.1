package xygdev.commons.springjdbc;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CLOB;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

/**
 * Extension of JdbcDaoSupport that exposes a DevJdbcTemplate as well.
 * <br/>程序的逻辑主要是提供类devJdbcTemplate！主要用到的是2个成员变量：NamedParameterJdbcTemplate/JdbcTemplate
 * <br/>程序的作用主要是为了方便Dao层访问数据库。直接在Dao类 extends DevJdbcDaoSupport 即可使用DevJdbcTemplate等封装好的类
 * <br/>第一次写这种类，碰到很多问题。必须要理解它们之间的关系才可以成功编写。这里主要是仿照NamedParameterJdbcDaoSupport写。
 * @author Sam.T
 * @since 1.1
 * @see DevJdbcTemplate
 */

/*
JdbcTemplate
NamedParameterJdbcTemplate
DevJdbcTemplate

我写的DevJdbcTemplate是仿照NamedParameterJdbcTemplate写的。
它的作用主要是二次封装NamedParameterJdbcTemplate类，还有额外封装了一个execute方法非常方便执行plsql。
可以理解DevJdbcTemplate和NamedParameterJdbcTemplate的关系是平等的，扩展了JdbcTemplate的使用范围。

做的过程中对SpringJDBC的理解：
1 所有的JdbcOperations都是接口类，只对外开发一些经常使用的方法，例如query等。但是，实际工作的代码还是写在JdbcTemplate里面！
NamedParameterJdbcOperations和NamedParameterJdbcTemplate的关系也是这样子。
2 JdbcDaoSupport类，作用是可以直接提供JdbcTemplate类给开发者使用(需要开发的类继承之)！当然是用get方法使用！
这里会用setDataSource方法接收Spring所注入的dataSource变量。
NamedParameterJdbcDaoSupport和NamedParameterJdbcTemplate的关系也是一样。
所以，要使用对应的类（例如JdbcTemplate），只需要继承对应的DaoSupport类即可。
3 其实和上面所描述的有关联，简单总结一下，这里有2种方法接收Spring所注入的dataSource变量。
  1)第一种方法，就是最简单的，直接在类编写setDataSource方法接收。
  2)第二种方法，继承JdbcDaoSupport类或者JdbcDaoSupport类的子类。因为JdbcDaoSupport已经写了一个setDataSource方法接收。
  无论是什么方法，其实本质上还是用setDataSource方法接收。

这个DevJdbcDaoSupport的逻辑大概是这样子：
例如，我新建了一个类：public class DevJdbcTemplateTest extends DevJdbcDaoSupport
这里，DevJdbcTemplateTest继承DevJdbcDaoSupport，而DevJdbcDaoSupport又继承JdbcDaoSupport。
当用Srping框架(getBean等)实例化对应的类（DevJdbcTemplateTest）的时候，
由于applicationContext.xml配置了该bean：DevJdbcTemplateTest类会注入dataSource参数，
所以会自动执行setDataSource方法（JdbcDaoSupport的）。
该方法会初始化jdbcTemplate变量，并且会执行initTemplateConfig（注意，initTemplateConfig已经由DevJdbcTemplateTest类重写的），
就可以为类DevJdbcDaoSupport对应的成员变量（devJdbcTemplate等）赋值！
这时候类DevJdbcTemplateTest可以用get方法直接使用devJdbcTemplate等类（以及对应类开发对外的方法）。
-->所以，整个过程基本是一气呵成，对使用者隐藏！使用者只需要继承DevJdbcDaoSupport类，即可直接用对应的成员类（用get方法）。

*/

@SuppressWarnings("deprecation")
public class DevJdbcDaoSupport extends JdbcDaoSupport {
	private DevJdbcTemplate devJdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/** Custom NativeJdbcExtractor */
	private NativeJdbcExtractor nativeJdbcExtractor;

	/**
	 * Create a DevJdbcTemplate based on the configured JdbcTemplate.
	 */
	@Override
	protected void initTemplateConfig() {
		//System.out.println("0");
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
		//System.out.println("1:"+getNamedParameterJdbcTemplate());
		this.devJdbcTemplate = new DevJdbcTemplate(getNamedParameterJdbcTemplate(),getJdbcTemplate());
		//enableTransaction();
		//getJdbcTemplate().setNativeJdbcExtractor(nativeJdbcExtractor);
	}

	/**
	 * Return a DevJdbcTemplate wrapping the configured JdbcTemplate.
	 */
	public DevJdbcTemplate getDevJdbcTemplate() {
	  return devJdbcTemplate;
	}
	
	/**
	 * Return a NamedParameterJdbcTemplate wrapping the configured JdbcTemplate.
	 */
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		  return namedParameterJdbcTemplate;
	}

	/**
	 * Set a NativeJdbcExtractor to extract native JDBC objects from wrapped handles.
	 * Useful if native Statement and/or ResultSet handles are expected for casting
	 * to database-specific implementation classes, but a connection pool that wraps
	 * JDBC objects is used (note: <i>any</i> pool will return wrapped Connections).
	 */
	public void setNativeJdbcExtractor(NativeJdbcExtractor extractor) {
		this.nativeJdbcExtractor = extractor;
	}
	/**
	 * Return the current NativeJdbcExtractor implementation.
	 */
	public NativeJdbcExtractor getNativeJdbcExtractor() {
		return this.nativeJdbcExtractor;
	}

	/**
	 * Return the current NativeJdbc Connection.
	 * @throws SQLException 
	 */
	public Connection getNativeJdbcConnection() throws SQLException {
		Connection con = this.getConnection();
		if(con==null){
			throw new SQLException("The con is null!");
		}
		if(this.getNativeJdbcExtractor()==null){
			throw new SQLException("The getNativeJdbcExtractor is null!");
		}
		Connection conToUse =con;
		conToUse = this.getNativeJdbcExtractor().getNativeConnection(con);
		return conToUse;
	}
	

	/**
	 * 封装常用的Java数组转换为oracle.sql.ARRAY的方法。
	 * <br/>根据原生的JDBC转换。
	 * <br/>支持空数组的处理。
	 * @param ArrayTypeDB DB的数组类型，例如："APEX_UAT.XYG_FFO_B2B_SEND_PKG.NUM_TBL_TYPE"
	 * @param array Java数组
	 * @return oracle.sql.ARRAY
	 * @throws SQLException
	 */
	public ARRAY getOracleARRAY(String ArrayTypeDB,Object[] array) throws SQLException{
		//if(TypeConvert.isNullValue(array)) return null;
		Connection conToUse=getNativeJdbcConnection();
		ArrayDescriptor des = ArrayDescriptor.createDescriptor(ArrayTypeDB,conToUse);            
		return new ARRAY(des,conToUse,array);
	}
	
	/**
	 * 封装常用的String转换为oracle.sql.CLOB的方法。
	 * <br/>由于O记的特殊性，必须要用到原生的JDBC Connection，所以必须封装在此！
	 * <br/>注意：直接支持空字符的转换clob。
	 * @param x 要转换的字符
	 * @return oracle.sql.CLOB
	 * @throws SQLException
	 */
	public CLOB getOracleCLOB(String x) throws SQLException{
		//if(TypeConvert.isNullValue(x)) return null;
		OracleConnection oCon=(OracleConnection) this.getNativeJdbcConnection();
		CLOB clob = new CLOB(oCon);
		clob = oracle.sql.CLOB.createTemporary(oCon,true,1);
		clob.putString(1,x);        
		return clob;
	}
}

/*
2016.8.11 添加事务处理的功能备份
暂时不需要在这里添加事务处理功能。统一用@Transactional注解添加。

	//新增事务处理功能
    private DataSourceTransactionManager transactionManager;
    private DefaultTransactionDefinition def;
    
    
   //启用事务处理管理功能
	public void enableTransaction(){
		//System.out.println("2");
		if(this.transactionManager==null){
			this.transactionManager = new DataSourceTransactionManager(getJdbcTemplate().getDataSource());
			// 建立事务的定义 
			def = new DefaultTransactionDefinition(); 
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		}
	}
	
	//Return a TransactionManager wrapping the configured.
	public DataSourceTransactionManager getTransactionManager() {
		  return this.transactionManager;
	}
	
	//Return a TransactionStatus wrapping the configured.
	public TransactionStatus getTransactionStatus() {
		  return transactionManager.getTransaction(def);
	}
 */
