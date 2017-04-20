package xygdev.commons.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;

import xygdev.commons.util.TypeConvert;


/**
 * 基本控制器封装，其他控制器的基类(父类)
 *
 * @author Sam.T
 * @version 1.0
 * @date 2017年4月13日
 */
public class BaseController {

	public static String CHAR_ENCODE = "utf-8";//转为JosnStr的默认属性名称
	protected HttpServletRequest request; 
    protected HttpServletResponse response; 
    protected HttpSession session; 
    protected Long loginId; 
    
    @ModelAttribute 
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{ 
        this.request = request; 
        this.response = response; 
        this.session = request.getSession(); 
        request.setCharacterEncoding(CHAR_ENCODE);
        request.setCharacterEncoding(CHAR_ENCODE);
        response.setContentType("text/html;charset="+CHAR_ENCODE);  
	    loginId=(Long)session.getAttribute("LOGIN_ID");
    }

	/**
	 * Stores an attribute in this request
	 * @param name a String specifying the name of the attribute
	 * @param value the Object to be stored
	 */
	public BaseController setAttr(String name, Object value) {
		request.setAttribute(name, value);
		return this;
	}
	
	/**
	 * Removes an attribute from this request
	 * @param name a String specifying the name of the attribute to remove
	 */
	public BaseController removeAttr(String name) {
		request.removeAttribute(name);
		return this;
	}
	
	/**
	 * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
	 * @param attrMap key and value as attribute of the map to be stored
	 */
	public BaseController setAttrs(Map<String, Object> attrMap) {
		for (Map.Entry<String, Object> entry : attrMap.entrySet())
			request.setAttribute(entry.getKey(), entry.getValue());
		return this;
	}
	
	/**
	 * Returns the value of a request parameter as a String, or null if the parameter does not exist.
	 * <p>
	 * You should only use this method when you are sure the parameter has only one value. If the 
	 * parameter might have more than one value, use getParaValues(java.lang.String). 
	 * <p>
	 * If you use this method with a multivalued parameter, the value returned is equal to the first 
	 * value in the array returned by getParameterValues.
	 * @param name a String specifying the name of the parameter
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name) {
		return request.getParameter(name);
	}
	
	/**
	 * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
	 * @param name a String specifying the name of the parameter
	 * @param defaultValue a String value be returned when the value of parameter is null
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name, String defaultValue) {
		String result = request.getParameter(name);
		return result != null && !"".equals(result) ? result : defaultValue;
	}
	
	/**
	 * Returns the values of the request parameters as a Map.
	 * @return a Map contains all the parameters name and value
	 */
	public Map<String, String[]> getParaMap() {
		return request.getParameterMap();
	}
	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttr(String name) {
		return (T)request.getAttribute(name);
	}
	
	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an String Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public String getAttrForStr(String name) {
		return (String)request.getAttribute(name);
	}
	
	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public Integer getAttrForInt(String name) {
		return (Integer)request.getAttribute(name);
	}
	
	/**
	 * Returns the value of the specified request header as a String.
	 */
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	private Integer toInt(String value, Integer defaultValue) {
			if (TypeConvert.isNullValue(value))
				return defaultValue;
			return TypeConvert.str2Int(value.trim());
	}
	/**
	 * Returns the value of a request parameter and convert to Integer.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name) {
		return toInt(request.getParameter(name),null);
	}
	
	/**
	 * Returns the value of a request parameter and convert to Integer with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name, Integer defaultValue) {
		return toInt(request.getParameter(name), defaultValue);
	}
	
	private Long toLong(String value, Long defaultValue) {
		if (TypeConvert.isNullValue(value))
			return defaultValue;
		return TypeConvert.str2Long(value.trim());
	}
	/**
	 * Returns the value of a request parameter and convert to Long.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name) {
		return toLong(request.getParameter(name), null);
	}
	
	/**
	 * Returns the value of a request parameter and convert to Long with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name, Long defaultValue) {
		return toLong(request.getParameter(name), defaultValue);
	}
	
	private Boolean toBoolean(String value, Boolean defaultValue) {
		if (TypeConvert.isNullValue(value))
			return defaultValue;
		if ("1".equals(value) || "true".equals(value))
			return Boolean.TRUE;
		else if ("0".equals(value) || "false".equals(value))
			return Boolean.FALSE;
		return TypeConvert.str2Boolean(value.trim());
	}
	
	/**
	 * Returns the value of a request parameter and convert to Boolean.
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
	 */
	public Boolean getParaToBoolean(String name) {
		return toBoolean(request.getParameter(name), null);
	}
	
	/**
	 * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
	 */
	public Boolean getParaToBoolean(String name, Boolean defaultValue) {
		return toBoolean(request.getParameter(name), defaultValue);
	}
	private Date toDate(String value, Date defaultValue){
		Date d=null;
		if (TypeConvert.isNullValue(value))
			return defaultValue;
		try {
			d= TypeConvert.str2uDate(value.trim());
		} catch (ParseException e) {
			throw new IllegalArgumentException("The toDate raise Error:"+e);
		}
		return d;
	}
	
	/**
	 * Returns the value of a request parameter and convert to Date.
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name) {
		return toDate(request.getParameter(name), null);
	}
	
	/**
	 * Returns the value of a request parameter and convert to Date with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name, Date defaultValue) {
		return toDate(request.getParameter(name), defaultValue);
	}
	/**
	 * Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	
	/**
	 * Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * Return HttpSession.
	 */
	public HttpSession getSession() {
		return this.session;//request.getSession();
	}
	
	/**
	 * Return HttpSession.
	 * @param create a boolean specifying create HttpSession if it not exists
	 */
	public HttpSession getSession(boolean create) {
		return request.getSession(create);
	}
	/**
	 * Return a Object from session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttr(String key) {
		//HttpSession session = request.getSession(false);
		return this.session != null ? (T)this.session.getAttribute(key) : null;
	}
	
	/**
	 * Store Object to session.
	 * @param key a String specifying the key of the Object stored in session
	 * @param value a Object specifying the value stored in session
	 */
	public BaseController setSessionAttr(String key, Object value) {
		//request.getSession(true).setAttribute(key, value);
		this.session.setAttribute(key, value);
		return this;
	}
	
	/**
	 * Remove Object in session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	public BaseController removeSessionAttr(String key) {
		//HttpSession session = request.getSession(false);
		if (this.session != null)
			this.session.removeAttribute(key);
		return this;
	}

	/**
	 * Get entity from http request.
	 */
	public <T> T getEntity(Class<T> entityClass,Map<String, String> mapCol) {
		return (T)Injector.injectEntity(entityClass,mapCol, request, false);
	}
	
	public <T> T getEntity(Class<T> entityClass,Map<String, String> mapCol, boolean skipConvertError) {
		return (T)Injector.injectEntity(entityClass,mapCol, request, skipConvertError);
	}
	
	public <T> T getEntity(Class<T> entityClass,Map<String, String> mapCol, String beanName) {
		return (T)Injector.injectEntity(entityClass, mapCol,beanName, request, false);
	}
	
	public <T> T getEntity(Class<T> entityClass,Map<String, String> mapCol, String beanName, boolean skipConvertError) {
		return (T)Injector.injectEntity(entityClass,mapCol, beanName, request, skipConvertError);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getEntity(Object entity,Map<String, String> mapCol) {
		return (T)Injector.injectEntity(entity,mapCol, request, false);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntity(Object entity,Map<String, String> mapCol, boolean skipConvertError) {
		return (T)Injector.injectEntity(entity,mapCol, request, skipConvertError);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntity(Object entity,Map<String, String> mapCol, String beanName) {
		return (T)Injector.injectEntity(entity, mapCol,beanName, request, false);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntity(Object entity,Map<String, String> mapCol, String beanName, boolean skipConvertError) {
		return (T)Injector.injectEntity(entity,mapCol, beanName, request, skipConvertError);
	}

	/**
	 * 直接渲染(响应)String结果到页面。
	 * @throws IOException 
	 */
	public void renderStr(String s) throws IOException {
		response.getWriter().print(s);
	}
}
