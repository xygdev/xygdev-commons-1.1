package xygdev.commons.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.RowMapper;

/**
 * 主要是编写一个完整的entity对象的模版。特别是SpringJdbcTemplate需要用到的RowMapper的写法。
 * 注意：Cloneable主要是实现克隆对象的功能。按需使用。
 * @author Sam.T 2016.8.3
 */
@SuppressWarnings("rawtypes")
public class Emp implements FactoryBean,RowMapper<Emp>, Cloneable{
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
	    public Emp mapRow(ResultSet rs, int rowNum) throws SQLException {
			Emp emp = new Emp();
			emp.setEmpId(rs.getLong("emp_id"));
			emp.setEmpNumber(rs.getString("emp_number"));
			emp.setEmpName(rs.getString("emp_name"));
			emp.setHireDate(rs.getObject("hire_date")==null?null:rs.getDate("hire_date"));
			emp.setSalary(rs.getObject("salary")==null?null:rs.getLong("salary"));
			emp.setEnableDate(rs.getObject("enable_date")==null?null:rs.getDate("enable_date"));
			emp.setDisabledDate(rs.getObject("disabled_date")==null?null:rs.getDate("disabled_date"));
			emp.setRemark(rs.getObject("remark")==null?null:rs.getString("remark"));
			emp.setCreatedBy(rs.getLong("created_by"));
			emp.setCreationDate(rs.getDate("creation_date"));
			emp.setLastUpdatedBy(rs.getLong("last_updated_by"));
			emp.setLastUpdateDate(rs.getDate("last_update_date"));
			emp.setLastUpdateLogin(rs.getLong("last_update_login"));
			return emp;
		}
		
		@Override  
		public Object clone() {  
		Emp emp = null;  
		        try{  
		        	emp = (Emp)super.clone();  
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

BEGIN
    XYG_ALD_GENSCRIPT_PKG.G_DEBUG_TYPE := 'DBMS_OUTPUT';
    XYG_ALD_GENSCRIPT_PKG.HANDLE_JAVAB_SOURCE_CODE('XYG_ALD_EMP');
    XYG_ALD_GENSCRIPT_PKG.HANDLE_JAVAEMAP_SOURCE_CODE('XYG_ALD_EMP','emp','rs');
END;
 */
