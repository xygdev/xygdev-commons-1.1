package xygdev.commons.entity;

import org.springframework.beans.factory.FactoryBean;

import xygdev.commons.util.Constant;
import xygdev.commons.util.TypeConvert;

/**
 *	加载在Java中调用Oracle的PLSQL时候对应的结果返回值(OUT参数)。
 * <br>retcode：0:成功  非0:失败( 或者：0:成功  1:警告   2:错误  ----注意：确定警告的时候要做什么动作)
 * <br>errbuf：具体的处理结果信息
 * @author Sam.T 2016/4/26
 * @version 1.0
 */

public class PlsqlRetValue implements FactoryBean<Object> {
	//静态值，为用在PLSQL里面匹配参数用。
	public static final String RETCODE="retcode";
	public static final String ERRBUF="errbuf";
	public static final String PARAM1="param1";
	public static final String PARAM2="param2";
	public static final String PARAM3="param3";
	public static final String PARAM4="param4";
	public static final String PARAM5="param5";
	
	private int retcode;
	private String errbuf;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String param6;
	private String param7;
	private String param8;
	private String param9;
	private String param10;
	private String param11;
	private String param12;
	private String param13;
	private String param14;
	private String param15;

	//目的是给默认值，但是不可以写为构造方法，否则会报错：
	//org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'RetValue' defined in class path
	//估计和Spring管理Bean有出现冲突的原因！
	/*public void initRetValue(int retcode,String errbuf){
		   this.setRetcode(retcode);
		   this.setErrbuf(errbuf);
	   }*/
	
	//GET&SET Method
	public int getRetcode() {
		return this.retcode; }

	public void setRetcode(int retcode) {
		this.retcode = retcode; }

	public String getErrbuf() {
		return this.errbuf; }

	public void setErrbuf(String errbuf) {
		this.errbuf = errbuf; }

	public String getParam1() {
		return this.param1; }

	public void setParam1(String param1) {
		this.param1 = param1; }

	public String getParam2() {
		return this.param2; }

	public void setParam2(String param2) {
		this.param2 = param2; }

	public String getParam3() {
		return this.param3; }

	public void setParam3(String param3) {
		this.param3 = param3; }

	public String getParam4() {
		return this.param4; }

	public void setParam4(String param4) {
		this.param4 = param4; }

	public String getParam5() {
		return this.param5; }

	public void setParam5(String param5) {
		this.param5 = param5; }

	public String getParam6() {
		return this.param6; }

	public void setParam6(String param6) {
		this.param6 = param6; }

	public String getParam7() {
		return this.param7; }

	public void setParam7(String param7) {
		this.param7 = param7; }

	public String getParam8() {
		return this.param8; }

	public void setParam8(String param8) {
		this.param8 = param8; }

	public String getParam9() {
		return this.param9; }

	public void setParam9(String param9) {
		this.param9 = param9; }

	public String getParam10() {
		return this.param10; }

	public void setParam10(String param10) {
		this.param10 = param10; }

	public String getParam11() {
		return this.param11; }

	public void setParam11(String param11) {
		this.param11 = param11; }

	public String getParam12() {
		return this.param12; }

	public void setParam12(String param12) {
		this.param12 = param12; }

	public String getParam13() {
		return this.param13; }

	public void setParam13(String param13) {
		this.param13 = param13; }

	public String getParam14() {
		return this.param14; }

	public void setParam14(String param14) {
		this.param14 = param14; }

	public String getParam15() {
		return this.param15; }

	public void setParam15(String param15) {
		this.param15 = param15;
	}

	@Override
	public Object getObject() throws Exception {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
	@Override
	public String toString(){
		return "retcode:"+retcode+",errbuf:"+errbuf
				+": param1:"+param1+",param2:"+param2+",param3:"+param3+",param4:"+param4+",param5:"+param5;
	}
	
	public String toJsonStr(){
		StringBuffer sb=new StringBuffer();
		sb.append("{");
		sb.append("\""+PlsqlRetValue.RETCODE+"\":\""+this.getRetcode()+"\"");
		sb.append(",\""+PlsqlRetValue.ERRBUF+"\":\""+TypeConvert.type2JsonStr(this.getErrbuf())+"\"");;
		sb.append(",\""+PlsqlRetValue.PARAM1+"\":\""+TypeConvert.type2JsonStr(this.getParam1())+"\"");
		sb.append(",\""+PlsqlRetValue.PARAM2+"\":\""+TypeConvert.type2JsonStr(this.getParam2())+"\"");
		sb.append(",\""+PlsqlRetValue.PARAM3+"\":\""+TypeConvert.type2JsonStr(this.getParam3())+"\"");
		sb.append(",\""+PlsqlRetValue.PARAM4+"\":\""+TypeConvert.type2JsonStr(this.getParam4())+"\"");
		sb.append(",\""+PlsqlRetValue.PARAM5+"\":\""+TypeConvert.type2JsonStr(this.getParam5())+"\"");
		sb.append("}");
		if(TypeConvert.isNullValue(Constant.ENTER_REPLACE_STR)){
			return sb.toString();
		}else{
			return sb.toString().replaceAll(Constant.LINE_SEPARATOR, Constant.ENTER_REPLACE_STR);
		}
	}
}
