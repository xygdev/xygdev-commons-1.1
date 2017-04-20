package xygdev.commons.core;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import xygdev.commons.util.LogUtil;
import xygdev.commons.util.TypeConvert;

public class Injector {

	private static <T> T createInstance(Class<T> objClass) {
		try {
			return objClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 自动将请求的参数批量注入到对应的Entity里面！提高工作效率！
	 * 相对比较复杂的是：这里整合了新增对象和更新对象的功能！看输入的参数决定！
	 * @param entityClass 对应要注入的Entity的对象的class类定义
	 * @param entity 对应要注入的Entity的对象
	 * @param entityName 页面JSP传的参数的命名空间，允许为空，如果不输入默认=类名称的首字母小写
	 * @param mapCol 类的方法和JSP页面栏位 对应的匹配关系
	 * @param request 请求对象
	 * @param skipConvertError出错是否显示：false=>出错立刻提示
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T injectEntity(Class<T> entityClass,Object entity,Map<String, String> mapCol, String entityName, HttpServletRequest request, boolean skipConvertError) {
		if (entity == null&&entityClass==null) {
			throw new IllegalArgumentException("Both of the  parameters entity&entityClass  can not be null.");
		}else if (entityClass != null&&entity==null){
			entity = createInstance(entityClass);
		}else if (entity != null&&entityClass==null){
			entityClass=(Class<T>) entity.getClass();
		}else{
			Class<T> eClass=(Class<T>) entity.getClass();
			if(!eClass.equals(entityClass))
				throw new IllegalArgumentException("The parameter entity&entityClass can not mapping!");
		}
		String modelNameAndDot = TypeConvert.isNullValue(entityName) ? null : entityName + ".";
		//LogUtil.log("modelNameAndDot:"+modelNameAndDot);
		Map<String, String[]> parasMap = request.getParameterMap();
		Method[] methods = entityClass.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("set") == false || methodName.length() <= 3) {	// only setter method
				continue;
			}
			Class<?>[] types = method.getParameterTypes();
			if (types.length != 1) {						// only one parameter
				continue;//LogUtil.log("1 methodName:"+methodName);
			}
			String attrName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
			if(mapCol.containsKey(attrName)){
				String colName = mapCol.get(attrName);//转为栏位
				String paraName = modelNameAndDot != null ? modelNameAndDot + colName : colName;
				//LogUtil.log("2 paraName:"+paraName);
				if (parasMap.containsKey(paraName)) {
					try {
						String paraValue = request.getParameter(paraName);
						Object value = paraValue != null ? TypeConvert.convert(types[0], paraValue) : null;
						method.invoke(entity, value);
						LogUtil.log("-->para:"+paraName+"-->method:"+methodName+"-->value:"+paraValue);
					} catch (Exception e) {
						if (skipConvertError == false) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return (T)entity;
	}
	/**
	 * 自动将请求的参数批量注入到对应的Entity里面！(注：这里是新建一个对象)。提高工作效率！
	 * @param entityClass 对应要注入的Entity的类
	 * @param entityName 页面JSP传的参数的命名空间，允许为空，如果不输入默认=类名称的首字母小写
	 * @param mapCol 类的方法和JSP页面栏位 对应的匹配关系
	 * @param request 请求对象
	 * @param skipConvertError出错是否显示：false=>出错立刻提示
	 * @return
	 */
	public static final <T> T injectEntity(Class<T> entityClass,Map<String, String> mapCol, String entityName, HttpServletRequest request, boolean skipConvertError) {
		return (T)injectEntity(entityClass,null, mapCol, entityName,request, skipConvertError);
	}
	public static <T> T injectEntity(Class<T> entityClass,Map<String, String> mapCol, HttpServletRequest request, boolean skipConvertError) {
		String entityName = entityClass.getSimpleName();
		return (T)injectEntity(entityClass, mapCol, TypeConvert.firstCharToLowerCase(entityName),request, skipConvertError);
	}
	/**
	 * 自动将请求的参数批量注入到对应的Entity里面！(注：这里是更新对应的对象)。提高工作效率！
	 * @param entityClass 对应要注入的Entity的类
	 * @param entityName 页面JSP传的参数的命名空间，允许为空，如果不输入默认=类名称的首字母小写
	 * @param mapCol 类的方法和JSP页面栏位 对应的匹配关系
	 * @param request 请求对象
	 * @param skipConvertError出错是否显示：false=>出错立刻提示
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T injectEntity(Object entity,Map<String, String> mapCol,String entityName, HttpServletRequest request, boolean skipConvertError) {
		return (T)injectEntity(null,entity, mapCol, entityName,request, skipConvertError);
	}
	@SuppressWarnings("unchecked")
	public static <T> T injectEntity(Object entity,Map<String, String> mapCol, HttpServletRequest request, boolean skipConvertError) {
		String entityName = (String) entity.getClass().getSimpleName();
		return (T)injectEntity(entity, mapCol, TypeConvert.firstCharToLowerCase(entityName),request, skipConvertError);
	}
	
}
