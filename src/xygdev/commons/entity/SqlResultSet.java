package xygdev.commons.entity;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import xygdev.commons.util.Constant;
import xygdev.commons.util.TypeConvert;

/**
 *	SQL查询的返回的结果集对象。
 * <br>resultSet：SQL查询结果集。List代表行的结果，Object[]代表每一行的所有栏位的值。其实也就是一个二位数组！
 * <br>colName：栏位的名称
 * @author Sam.T 2016/5/6
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class SqlResultSet implements FactoryBean {
	public static String JSON_PROP = "rows";//转为JosnStr的默认属性名称
	private String[] colName;
	private List<Object[]> resultSet;//改为List，是为了让行数可以动态扩展。
	
	public SqlResultSet(){
	}
	
	public SqlResultSet(String[] colName,List<Object[]> resultSet){
		this.resultSet=resultSet;
		this.colName=colName;
	}

	public List<Object[]> getResultSet() {
		return resultSet;
	}
	public void setResultSet(List<Object[]> resultSet) {
		this.resultSet = resultSet;
	}
	
	public String[] getColName() {
		return colName;
	}
	public void setColName(String[] colName) {
		this.colName = colName;
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

	/**
	 * 这里自动转换为Array字符形式的数组字符，例如：[{"aaa":"123"},{"BBB":444}]
	 * 注意：是中括号[]括起来的结果。如果没数据返回，默认是空数组：[]
	 * 2016.10.24的值改为type2JsonStr
	 * @return String 数组。注意返回的是数组[]而不是Json对象{}。
	 */
	public String toArrayStr(){
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		if(this.getResultSet()!=null){
			for(int r=0;r<this.getResultSet().size();r++){
				sb.append("{");
				for(int i=0;i<this.getColName().length;i++){
					sb.append("\""+this.getColName()[i]+"\":\""
							+TypeConvert.type2JsonStr(this.getResultSet().get(r)[i])+"\",");
			    	if(i==(this.getColName().length-1)){
			    		sb.deleteCharAt(sb.lastIndexOf(","));
			    	}
				}
				sb.append("},");
			}
			if(this.getResultSet().size()>0){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
		}
		sb.append("]");
		if(TypeConvert.isNullValue(Constant.ENTER_REPLACE_STR)){
			return sb.toString();
		}else{
			return sb.toString().replaceAll(Constant.LINE_SEPARATOR, Constant.ENTER_REPLACE_STR);
		}
	}
	
	/**
	 * 新增方法toJsonObjStr方法，直接返回一个完整的单个Josn对象：{"rows":[{"aaa":"123"},{"BBB":444}]}
	 * @return Json对象格式
	 */
	public String toJsonStr(String s){
		return "{\""+s+"\":"+toArrayStr()+"}";
	}
	public String toJsonStr(){
		return toJsonStr(JSON_PROP);
	}
	
	/**
	 * 重写toString方法，这里自动转换为Json数据{"rows":[{"aaa":"123"},{"BBB":444}]}
	 * @return String Json格式的数组
	 */
	@Override
	public String toString(){
		return toJsonStr();
	}
	
}
