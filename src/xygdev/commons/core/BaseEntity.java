package xygdev.commons.core;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
		
		/**
		 * 2017.4.24 sam.t
		 * <br/>封装类似Form的FND_STANDARD.SET_WHO。
		 * 主要是为了方便设定对应对象的5WHO栏位。包括INSERT和UPDATE的动作
		 * @param <T>
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void setWho(Object entity,String dmlType,Long userId,Long loginId)throws Exception{
			if(userId==null) userId=-1L;
			if(loginId==null) loginId=-1L;
			Class entityClass=entity.getClass();
			if(dmlType.equalsIgnoreCase("INSERT")){
				Method creaDateMethod = entityClass.getDeclaredMethod("setCreationDate", java.util.Date.class);
				creaDateMethod.invoke(entity, new Timestamp(System.currentTimeMillis()));
				Method creaByMethod = entityClass.getDeclaredMethod("setCreatedBy", Long.class);
				creaByMethod.invoke(entity, userId);
				Method lastUpdDateMethod = entityClass.getDeclaredMethod("setLastUpdateDate", java.util.Date.class);
				lastUpdDateMethod.invoke(entity, new Timestamp(System.currentTimeMillis()));
				Method lastUpdByMethod = entityClass.getDeclaredMethod("setLastUpdatedBy", Long.class);
				lastUpdByMethod.invoke(entity, userId);
				Method lastUpdLoginMethod = entityClass.getDeclaredMethod("setLastUpdateLogin", Long.class);
				lastUpdLoginMethod.invoke(entity, loginId);
			}else if (dmlType.equalsIgnoreCase("UPDATE")){
				Method lastUpdDateMethod = entityClass.getDeclaredMethod("setLastUpdateDate", java.util.Date.class);
				lastUpdDateMethod.invoke(entity, new Timestamp(System.currentTimeMillis()));
				Method lastUpdByMethod = entityClass.getDeclaredMethod("setLastUpdatedBy", Long.class);
				lastUpdByMethod.invoke(entity, userId);
				Method lastUpdLoginMethod = entityClass.getDeclaredMethod("setLastUpdateLogin", Long.class);
				lastUpdLoginMethod.invoke(entity, loginId);
			}else{
				throw new IllegalArgumentException("The  parameters dmlType must be INSERT or UPDATE. Current dmlType:"+dmlType);
			}
		}
}
