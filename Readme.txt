2017.4.13������־��
ע�⣺
1 ���θ���֮��֮ǰ��b2bϵͳ������ʹ���°汾�Ĺ����ˡ�����������ʵ�޸�һ��Ҳ����ʹ�á�
2 Class������JDK��1.7�汾
������־��
1 SqlResultSet���޸ģ�
ԭ�������ݼ����÷���ֵ��SqlResultSet.toJsonStr���������ص��ǣ�[{"aaa":"123"},{"BBB":444}]���ǲ��Եģ�
��Ϊ��[]�����飬{}����Json����
���ԣ�ԭ����toJsonStr��������ΪtoArrayStr������
toJsonStr������Ϊֱ�ӷ���һ��������������Josn����{"rows":[{"aaa":"123"},{"BBB":444}]}
ע�⣺rows���Ա�ʶ����ͨ������SqlResultSet.JSON_PROP�޸ġ�Ĭ��=rows
PagePub���InteractPub��ͬ���޸ģ�

2 TypeConvert���޸ģ�
���һ��ת�����ܣ�
public static final Object convert(Class<?> type, String s)

3 RowMapperƥ���Ż���
����ÿһ��Entity������һ��mapCol����������bean�����ԺͶ�Ӧ�����ݿ���λ��ƥ���ϵ��
�����Ժ����չ��
ʹ��������Emp��

4 ���core������װһЩ���õĺ��Ĵ����ࣺ
BaseController��ķ�װ�ࣺ
���ܣ�
���е�req��res����Ҫ��װ�á�
�����Ⱦ���ܣ�render��Ŀǰrenderֻ֧��json��txt��Щ���Ժ���Է���jFinal������չ��
��ӷ����getPara��getAttr���ܣ����⣬�����getEntity���ܣ����Խ�req�����Ĳ����Զ���װΪ��Ӧ��entity��
BaseEntity��װ�ࣺ����Entity��ĸ��ࡣ
���⣬���е�Spring Bean����xml���ø�Ϊע�����á�-->���ǽ�����xml���ñȽϺã�����Ҫһ��Ҫ�޸�xml���õ�ɨ������

5 ���Jetty������������վ�Ĺ��ܣ�
----------�ܽ��÷���---------------2017.4.19
���ȣ����֮ǰ����Tomcat�������ģ���Ҫ�������İ�ȥ��������
���ţ��������2��jar����libĿ¼�£�
slf4j-api-1.6.1.jar
slf4j-log4j12-1.6.1.jar
Ȼ�����jetty-9.2.19.v20160908��ص�jar���������һ��path·�����ӻ�ȽϺá�
jar�������D:\JSP_MyEclipse\Library\jetty-9.2-jdk7+
ʵ��ʹ�ã�
���κ�һ���࣬��������Ĵ��룬Ȼ��ִ�м��ɣ�
	public static void main(String[] args) {
		Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5,"D:\\JSP_MyEclipse\\CDN\\image","/image");
	}

	
����Jetty���������޸�ϸ�ڣ�
��ע��
jetty8.1.8��ʹ�ò��裺
���ȣ����֮ǰ����Tomcat�������ģ���Ҫ�������İ�ȥ��������
���ţ��������3��jar����
jetty-server-8.1.8.jar
slf4j-api-1.6.1.jar
slf4j-log4j12-1.6.1.jar
ʵ��ʹ�ã�
���κ�һ���࣬��������Ĵ��룬Ȼ��ִ�м��ɣ�
	public static void main(String[] args) {
		Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5,"D:\\JSP_MyEclipse\\CDN\\image","/image");
	}

�ļ���������
http://192.168.88.123:8080/image/login/xyg.gif
<Context docBase="D:\JSP_MyEclipse\CDN\image" path="/image"/>

����jetty������������2����Ҫȷ�ϵģ�
��֧�ֵ�java�汾���Լ���WebSocket��֧�֡�
http://blog.csdn.net/gao36951/article/details/45318315
https://www.eclipse.org/jetty/download.html

����JettyΪ9.0�汾�����ֵ����⣺
���ȣ�����Ҫע�⣬����ǵ���jar������Ҫ����all�İ汾��
���ȱ���
2017-04-18 16:54:20.907:INFO:/XYG_WEBDEV_SAMPLE:qtp625576447-11: No JSP support.  
Check that JSP jars are in lib/jsp and that the JSP option has been specified to start.jar
jsp support not configured jetty
-->�������jsp�ļ���jar�������ص�Library���档
----
��jetty-all-9.3.18.v20170406-uber.jar�汾�����ҽ�jetty-distribution-9.3.18.v20170406\lib\apache-jsp��4��jar���Ƶ�libĿ¼֮�£��ֱ���
Provider org.eclipse.jetty.apache.jsp.JuliLog not a subtype
����취��http://blog.csdn.net/hadixlin/article/details/40426455
webApp.setParentLoaderPriority(true);
---
���ţ��ֱ���
org.apache.jasper.JasperException: Unable to compile class for JSP
http://stackoverflow.com/questions/24299649/unable-to-compile-class-for-jsp-in-jetty
http://blog.csdn.net/tomato__/article/details/37756419
������ӣ�
https://git.oschina.net/dongcb678/JfinalUIB/tree/JFinalUIB_V4
����ܽ᣺
������Ϊ��jetty-all-9.2.19.v20160908�Ϳ�������ʹ�ã�
��Ҫ������Щjar��������Ҫ�����������Զ�Ҳ�������١�
��Ҫע����ǣ������������һЩ����Ĺ��ܣ���������gzip֧��֮��ģ��������������ʱ���ǻᱨ��
��Ӧ��jar�������D:\JSP_MyEclipse\Library\jetty-9.2-jdk7+
��Ӧ��jdk�汾��9.2	2014	JVM�汾��1.7	
�����Ŀ¼��E:\Sam Works\2 Oracle Developer Tools\Javaר��\jFinalѧϰ����\jFinal���\jfinal-3.0-all\jetty�ĸ����汾

2017.4.19���Խ�WebSocket����ͨ�ˣ�
���岽�裺
1 ���java websocket����SystemWebSocketHandler.java��WebSocketConfig.java��WebSocketHandshakeInterceptor.java
2 ȷ��������Ŀ��plugin/js�������漸�����Ĵ����js��sockjs-03.js��webSocket.js
3 ���jsp��webSocket.jsp
4 ���Controller��AdminController.java
5 �����Ҫ������Ϣ��jsp������Ӵ��룺<script src="plugin/js/webSocket.js"></script>
��Ҫע��js�ķŵ�λ��Ҫ������ǰ�档��������js��ͻ�����⡣

���ˣ��ڿ�����ʱ���Ѿ���ȫ������Jetty���Tomcat�������ˡ�


���ڼ�Web�������̣�
���������Ҫ��չ�ԣ���Ҫ�ȶ��ԣ�Ҫ�򻯿�������̫���ˡ�
���Էֲ��迴��
��һ���������ͼ��DML���Ĵ�����
������ͼ�϶����ٲ��˵ġ�����DML���Ĵ������е���硣��ʵInsert��delete����Ժô���������Ǹ��£�
��Ϊ�����µ�ʱ�����Ҫ���ǲ��������⣬���Ǹ��µ�ʱ���¼�Ƿ񱻱��˸������ˡ����Ա���Ҫ��һ��lock�Ĺ��̣������߼���������ϵͳ��bug��
���ԣ����Ҫ�򻯿������̣����ǲ�����dml�İ�������Щ���趼Ҫ�ᵽjava���������ͱ���Ҫ���������λ�����ݵĶԱȵ����⡣
����Ҳ����ֹ���ĺ����ݿ�Ľ�����
���ȣ���һ�ʼ�¼java��lock������Ҫ�����ݿ���������¼��id��ȡ�����ݿ�ļ�¼��
���ţ���Ҫ��������ȡ���ļ�¼�����ṩ�ļ�¼����λ��һ���Զ��ԱȵĶ�����
�������ԱȵĽ����һ�µĻ�������Ϊ����û���޸ģ��û����Ը������ݡ������û������Ը��£�Ҫˢ�¡�
��Щ�����ݿ����������صĶ��������ת��java����������Ч�ʵͣ����Ҵ�������Ҳ�鷳��
-->��ע����ʵinsertҲ����ô�ô�����Ϊ��Java�˶����Entity����ʵ��View�����ԣ�Insert��ʱ��Ҳһ��Ҫ�����λд�����������Insert��
��Ϊ������������λ������ֱ��insert�����仰˵��������һ���У������Ƕ�д��Java���滹����PKG��װ���ٴ���ʵ��insert��
���뵽��һ���ǣ������PKG��װ�ã���չ���Ǻ�ǿ�ģ����Ժܼ�ʵ����Insert֮ǰ��һЩ�ǳ����ӵĺ����ݿ���ص�У�飡������ЩУ����Ҳֻ�÷���Java�����ˡ�����
�ڶ�����Java Entity�Ľ�����Dao��Ľ���
Entity������Ǳ�����ˡ���Ϊ��������Java Bean�����ݿ�����ݵ�������
�����Ѿ������Ż��ˣ�������Ҫ����һ��mapCol�Ķ��󣺶���Entity�����Ժͱ����λ�Ĺ�ϵ�������mapRow�����Լ�����������Զ�ƥ������Ǵ����ƥ���ϵ�л�ȡ��
����Dao�㣬���Ҳ��û�취����Ϊ���ð�����dml����������ֻ�������λ����ֵ�����Ҳ�ǹ�������Դ�ĵط���
��������Service��Ľ�����Controller��Ľ���
Service�㻹�Ǳ���ģ�����ҵ����㣬ͬʱҲ��������һ���ԵĲ�Ρ����Ǳ���ġ�
Controller�㣬url·�ɿ��Ʋ㣬�����Ǳ���ġ������Ѿ����˼򻯣�
��Ϊ֮ǰ�ж�������λ��Entity���ԵĹ�ϵ�����ԣ��������ʵ�������Զ������������ȡ���ԵĹ��ܣ�
ͬʱ����JFinal����װ�˴����ķǳ�ʵ�õĹ��ܣ���������Ĵ����Ѿ����˺����Ƶķ�װ��
�����ȡ����֮������Զ�ת��Ϊ���õļ������ͣ�
��ǰ��TypeConvert.str2Long(req.getParameter("GREATER_BOX"))����Long.parseLong(req.getParameter("GREATER_BOX"))(����������null�쳣��)
����ֻ��Ҫ��this.getParaToLong("GREATER_BOX")���ɣ���װ��֮��һ����λ��

���ۣ�
��ERP����һ���ű������Զ������Ĺ��ܣ�ֻ��Ҫ��Oracle�����û������ͼ����ģ�
DML�����Զ�����
Entity���Զ�����
Dao���Զ����ɣ����������Interface�Լ���Ӧ��Implement��
---
�����ӣ�������90%�Ĵ��빤����������ʡ�����ˡ�����
��Ч�ַ��㣡






