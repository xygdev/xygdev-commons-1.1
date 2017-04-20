package xygdev.commons.core;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Map;

import xygdev.commons.util.TypeConvert;

public class BaseEntity {
	   public Object mapRowCreator(Class<?> entityClass,Map<String, String> mapCol ,ResultSet rs, int rowNum) {
		   Object entity;//Class<?> beanClass = c;
		   try {
			   entity = entityClass.newInstance();//这里相当于new Class();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		   Method[] methods = entityClass.getMethods();
		   for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("set") == false || methodName.length() <= 3) {	// only setter method
					continue;
				}
				Class<?>[] types = method.getParameterTypes();
				if (types.length != 1) {						// only one parameter
					continue;
				}
				String attrName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				//System.out.println("methodName:"+methodName+",attrName:"+attrName);
				if (mapCol.containsKey(attrName)) {
					try {
						Object paraValue=rs.getObject(mapCol.get(attrName));
						//System.out.println("methodParaType:"+types[0]);
						Object value = paraValue != null ? TypeConvert.convert(types[0], paraValue.toString()) : null;
						method.invoke(entity, value);
						//System.out.println("set methodName:"+method.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
	       return entity;
	   }
}
