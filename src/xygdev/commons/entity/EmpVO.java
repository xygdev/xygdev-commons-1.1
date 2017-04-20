package xygdev.commons.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import xygdev.commons.core.BaseEntity;

/**
 * 主要是编写一个完整的entityVO对象的模版。特别是SpringJdbcTemplate需要用到的RowMapper的写法。
 * 注意：Cloneable主要是实现克隆对象的功能。按需使用。
 * @author Sam.T 2016.8.3
 */
@SuppressWarnings("rawtypes")
@Component("EmpVO")
public class EmpVO extends BaseEntity implements FactoryBean,RowMapper<EmpVO>, Cloneable{
	   private Long empId;
	   private String empNumber;
	   private String empName;
	   private java.util.Date hireDate;
	   private Long salary;
	   private java.util.Date enableDate;
	   private java.util.Date disabledDate;
	   private String remark;
	   private Long createdBy;
	   private java.util.Date creationDate;
	   private Long lastUpdatedBy;
	   private java.util.Date lastUpdateDate;
	   private Long lastUpdateLogin;
	   
		//此处定义Entity的属性和对应的基表或者基表视图的栏位的对应关系。
		//注意，栏位的名称必须要统一用大写！否则jsp页面自动产生Entity(匹配回来)的时候会有问题。
		public static final Map<String, String> mapCol = new HashMap<String, String>();  
		static {  
			mapCol.put("empId", "EMP_ID");  
			mapCol.put("empNumber", "EMP_NUMBER");  
			mapCol.put("empName", "EMP_NAME");  
			mapCol.put("hireDate", "HIRE_DATE");  
			mapCol.put("salary", "SALARY");  
			mapCol.put("enableDate", "ENABLE_DATE");  
			mapCol.put("disabledDate", "DISABLED_DATE");  
			mapCol.put("remark", "REMARK");  
			mapCol.put("createdBy", "CREATED_BY");  
			mapCol.put("creationDate", "CREATION_DATE");  
			mapCol.put("lastUpdatedBy", "LAST_UPDATED_BY");  
			mapCol.put("lastUpdateDate", "LAST_UPDATE_DATE");  
			mapCol.put("lastUpdateLogin", "LAST_UPDATE_LOGIN");  
		}
	 
	   //GET&SET Method
	   public Long getEmpId() {
	      return empId;
	   }
	   public void setEmpId(Long empId) {
	      this.empId = empId;
	   }
	   public String getEmpNumber() {
	      return empNumber;
	   }
	   public void setEmpNumber(String empNumber) {
	      this.empNumber = empNumber;
	   }
	   public String getEmpName() {
	      return empName;
	   }
	   public void setEmpName(String empName) {
	      this.empName = empName;
	   }
	   public java.util.Date getHireDate() {
	      return hireDate;
	   }
	   public void setHireDate(java.util.Date hireDate) {
	      this.hireDate = hireDate;
	   }
	   public Long getSalary() {
	      return salary;
	   }
	   public void setSalary(Long salary) {
	      this.salary = salary;
	   }
	   public java.util.Date getEnableDate() {
	      return enableDate;
	   }
	   public void setEnableDate(java.util.Date enableDate) {
	      this.enableDate = enableDate;
	   }
	   public java.util.Date getDisabledDate() {
	      return disabledDate;
	   }
	   public void setDisabledDate(java.util.Date disabledDate) {
	      this.disabledDate = disabledDate;
	   }
	   public String getRemark() {
	      return remark;
	   }
	   public void setRemark(String remark) {
	      this.remark = remark;
	   }
	   public Long getCreatedBy() {
	      return createdBy;
	   }
	   public void setCreatedBy(Long createdBy) {
	      this.createdBy = createdBy;
	   }
	   public java.util.Date getCreationDate() {
	      return creationDate;
	   }
	   public void setCreationDate(java.util.Date creationDate) {
	      this.creationDate = creationDate;
	   }
	   public Long getLastUpdatedBy() {
	      return lastUpdatedBy;
	   }
	   public void setLastUpdatedBy(Long lastUpdatedBy) {
	      this.lastUpdatedBy = lastUpdatedBy;
	   }
	   public java.util.Date getLastUpdateDate() {
	      return lastUpdateDate;
	   }
	   public void setLastUpdateDate(java.util.Date lastUpdateDate) {
	      this.lastUpdateDate = lastUpdateDate;
	   }
	   public Long getLastUpdateLogin() {
	      return lastUpdateLogin;
	   }
	   public void setLastUpdateLogin(Long lastUpdateLogin) {
	      this.lastUpdateLogin = lastUpdateLogin;
	   }

		@Override
	    public EmpVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			   return (EmpVO) mapRowCreator(EmpVO.class,mapCol,rs,rowNum);
		}
		
		@Override  
		public Object clone() {  
			EmpVO emp = null;  
		        try{  
		        	emp = (EmpVO)super.clone();  
		        }catch(CloneNotSupportedException e) {  
		            e.printStackTrace();  
		        }  
		        return emp;
		}
		
		@Override
		public Object getObject() throws Exception {
			return null;
		}
		@Override
		public Class getObjectType() {
			return null;
		}
		@Override
		public boolean isSingleton() {
			return false;
		}
}

/*建表代码：
CREATE TABLE XYG_ALD_EMP
(EMP_ID NUMBER NOT NULL
,EMP_NUMBER VARCHAR2(50) NOT NULL
,EMP_NAME VARCHAR2(240) NOT NULL
,HIRE_DATE DATE
,SALARY NUMBER
,ENABLE_DATE DATE
,DISABLED_DATE DATE
,REMARK VARCHAR2(240)
---5 who
,CREATED_BY        NUMBER DEFAULT -1 NOT NULL                                  --创建者
,CREATION_DATE     DATE DEFAULT SYSDATE NOT NULL                                  --创建日期
,LAST_UPDATED_BY   NUMBER DEFAULT -1NOT NULL                              --最后更新人
,LAST_UPDATE_DATE  DATE DEFAULT SYSDATE NOT NULL                              --最后更新日期
,LAST_UPDATE_LOGIN NUMBER DEFAULT -1NOT NULL                              --最后登陆人
,constraint XYG_ALD_EMP_PK primary key (EMP_ID)
);

--EMP_ID的取值seq
CREATE SEQUENCE XYG_ALD_EMP_S
   NOMAXVALUE
   MINVALUE 1;

INSERT INTO XYG_ALD_EMP(EMP_ID,EMP_NUMBER,EMP_NAME)
VALUES(XYG_ALD_EMP_S.NEXTVAL,'214492','王宪')

INSERT INTO XYG_ALD_EMP(EMP_ID,EMP_NUMBER,EMP_NAME)
VALUES(XYG_ALD_EMP_S.NEXTVAL,'214493','王X宪')

INSERT INTO XYG_ALD_EMP(EMP_ID,EMP_NUMBER,EMP_NAME)
VALUES(XYG_ALD_EMP_S.NEXTVAL,'214494','王宪X')

BEGIN
    XYG_ALD_GENSCRIPT_PKG.G_DEBUG_TYPE := 'DBMS_OUTPUT';
    XYG_ALD_GENSCRIPT_PKG.HANDLE_JAVAB_SOURCE_CODE('XYG_ALD_EMP');
    XYG_ALD_GENSCRIPT_PKG.HANDLE_JAVAEMAP_SOURCE_CODE('XYG_ALD_EMP','emp','rs');
END;
 */
