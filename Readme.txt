2017.4.13更新日志：
注意：
1 本次更新之后，之前的b2b系统不可以使用新版本的功能了。否则会出错。其实修改一下也可以使用。
2 Class类编译的JDK是1.7版本
更新日志：
1 SqlResultSet类修改：
原来的数据集公用返回值类SqlResultSet.toJsonStr方法，返回的是：[{"aaa":"123"},{"BBB":444}]，是不对的！
因为，[]是数组，{}才是Json对象！
所以，原来的toJsonStr方法更正为toArrayStr方法！
toJsonStr方法改为直接返回一个真正的完整的Josn对象：{"rows":[{"aaa":"123"},{"BBB":444}]}
注意：rows属性标识可以通过配置SqlResultSet.JSON_PROP修改。默认=rows
PagePub类和InteractPub类同步修改！

2 TypeConvert类修改：
添加一个转换功能：
public static final Object convert(Class<?> type, String s)

3 RowMapper匹配优化：
对于每一个Entity，新增一个mapCol变量，定义bean的属性和对应的数据库栏位的匹配关系！
方便以后的扩展。
使用样例在Emp类

4 添加core包，封装一些常用的核心处理类：
BaseController层的封装类：
功能：
所有的req和res都需要封装好。
添加渲染功能：render，目前render只支持json和txt这些。以后可以返照jFinal来做扩展。
添加方便的getPara和getAttr功能，另外，再添加getEntity功能！可以将req传来的参数自动封装为对应的entity！
BaseEntity封装类：所有Entity类的父类。
另外，所有的Spring Bean都由xml配置改为注解启用。-->还是建议用xml配置比较好，否则要一样要修改xml配置的扫描器！

5 添加Jetty服务器启用网站的功能！
----------总结用法：---------------2017.4.19
首先，如果之前是用Tomcat服务器的，需要将关联的包去掉关联。
接着，添加下面2个jar包到lib目录下：
slf4j-api-1.6.1.jar
slf4j-log4j12-1.6.1.jar
然后，添加jetty-9.2.19.v20160908相关的jar包，最好做一个path路径附加会比较好。
jar包在这里：D:\JSP_MyEclipse\Library\jetty-9.2-jdk7+
实际使用：
在任何一个类，输入下面的代码，然后执行即可：
	public static void main(String[] args) {
		Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5,"D:\\JSP_MyEclipse\\CDN\\image","/image");
	}

	
关于Jetty服务器的修改细节：
备注：
jetty8.1.8的使用步骤：
首先，如果之前是用Tomcat服务器的，需要将关联的包去掉关联。
接着，添加下面3个jar包：
jetty-server-8.1.8.jar
slf4j-api-1.6.1.jar
slf4j-log4j12-1.6.1.jar
实际使用：
在任何一个类，输入下面的代码，然后执行即可：
	public static void main(String[] args) {
		Server.start("WebRoot", 8080, "/XYG_WEBDEV_SAMPLE", 5,"D:\\JSP_MyEclipse\\CDN\\image","/image");
	}

文件服务器：
http://192.168.88.123:8080/image/login/xyg.gif
<Context docBase="D:\JSP_MyEclipse\CDN\image" path="/image"/>

关于jetty服务器，还有2点需要确认的：
其支持的java版本，以及对WebSocket的支持。
http://blog.csdn.net/gao36951/article/details/45318315
https://www.eclipse.org/jetty/download.html

升级Jetty为9.0版本，出现的问题：
首先，必须要注意，如果是单个jar，必须要下载all的版本。
首先报错：
2017-04-18 16:54:20.907:INFO:/XYG_WEBDEV_SAMPLE:qtp625576447-11: No JSP support.  
Check that JSP jars are in lib/jsp and that the JSP option has been specified to start.jar
jsp support not configured jetty
-->解决：将jsp的几个jar包给加载到Library里面。
----
用jetty-all-9.3.18.v20170406-uber.jar版本，并且将jetty-distribution-9.3.18.v20170406\lib\apache-jsp的4个jar复制到lib目录之下，又报错：
Provider org.eclipse.jetty.apache.jsp.JuliLog not a subtype
解决办法：http://blog.csdn.net/hadixlin/article/details/40426455
webApp.setParentLoaderPriority(true);
---
接着，又报错：
org.apache.jasper.JasperException: Unable to compile class for JSP
http://stackoverflow.com/questions/24299649/unable-to-compile-class-for-jsp-in-jetty
http://blog.csdn.net/tomato__/article/details/37756419
解决例子：
https://git.oschina.net/dongcb678/JfinalUIB/tree/JFinalUIB_V4
解决总结：
后来改为用jetty-all-9.2.19.v20160908就可以正常使用！
主要还是那些jar包，必须要完整，不可以多也不可以少。
需要注意的是，还不可以添加一些额外的功能，例如增加gzip支持之类的，否则启动服务的时候还是会报错。
对应的jar包在这里：D:\JSP_MyEclipse\Library\jetty-9.2-jdk7+
对应的jdk版本：9.2	2014	JVM版本：1.7	
软件的目录：E:\Sam Works\2 Oracle Developer Tools\Java专题\jFinal学习汇总\jFinal软件\jfinal-3.0-all\jetty的各个版本

2017.4.19可以将WebSocket测试通了！
大体步骤：
1 添加java websocket包：SystemWebSocketHandler.java，WebSocketConfig.java，WebSocketHandshakeInterceptor.java
2 确认您的项目在plugin/js存在下面几个核心处理的js：sockjs-03.js，webSocket.js
3 添加jsp：webSocket.jsp
4 添加Controller：AdminController.java
5 最后，在要接收信息的jsp界面添加代码：<script src="plugin/js/webSocket.js"></script>
需要注意js的放的位置要是在最前面。否则会出现js冲突的问题。

至此，在开发的时候已经完全可以用Jetty替代Tomcat服务器了。


关于简化Web开发流程：
如果程序想要扩展性，又要稳定性，要简化开发还是太难了。
可以分步骤看：
第一步：表格，视图，DML包的创建：
表格和视图肯定是少不了的。不过DML包的创建就有点分歧。其实Insert和delete都相对好处理，问题就是更新！
因为，更新的时候必须要考虑并发的问题，就是更新的时候记录是否被别人给更新了。所以必须要有一个lock的过程（处理逻辑）！否则系统有bug。
所以，如果要简化开发流程，就是不创建dml的包，则这些步骤都要搬到java里面做，就必须要考虑逐个栏位的数据的对比的问题。
而且也会出现过多的和数据库的交互：
首先，给一笔记录java做lock，首先要从数据库根据这个记录的id获取出数据库的记录；
接着，需要根据所获取出的记录和所提供的记录的栏位做一个自动对比的动作；
最后，如果对比的结果都一致的话，则认为数据没被修改，用户可以更新数据。否则用户不可以更新，要刷新。
这些和数据库操作密切相关的动作如果都转到java里面做，那效率低，而且处理起来也麻烦。
-->备注：其实insert也不怎么好处理，因为在Java端定义的Entity，其实是View！所以，Insert的时候也一样要逐个栏位写传入参数来做Insert！
因为并不是所有栏位都可以直接insert。换句话说，工作量一样有，区别是都写在Java里面还是用PKG封装好再传参实现insert。
再想到的一点是，如果用PKG封装好，扩展性是很强的！可以很简单实现在Insert之前的一些非常复杂的和数据库相关的校验！否则这些校验你也只好放在Java端做了。。。
第二步：Java Entity的建立，Dao层的建立
Entity对象就是必须的了。因为它是链接Java Bean和数据库表数据的桥梁。
现在已经有所优化了，就是需要定义一个mapCol的对象：定义Entity的属性和表格栏位的关系。后面的mapRow对象以及控制器层的自动匹配对象都是从这个匹配关系中获取。
关于Dao层，这个也是没办法，因为是用包来做dml，所以这里只好逐个栏位来传值。这个也是工作量相对大的地方！
第三步：Service层的建立，Controller层的建立
Service层还是必须的，属于业务处理层，同时也是事务处理一致性的层次。还是必须的。
Controller层，url路由控制层，更加是必须的。现在已经做了简化！
因为之前有定义了栏位和Entity属性的关系，所以，这里可以实现批量自动从请求参数获取属性的功能！
同时仿照JFinal，封装了大量的非常实用的功能，控制器层的代码已经做了很完善的封装！
例如获取参数之后可以自动转换为常用的几种类型：
以前：TypeConvert.str2Long(req.getParameter("GREATER_BOX"))或者Long.parseLong(req.getParameter("GREATER_BOX"))(会有隐含的null异常！)
现在只需要：this.getParaToLong("GREATER_BOX")即可，封装好之后一步到位！

结论：
在ERP开发一个脚本批量自动产生的功能，只需要在Oracle创建好基表和视图，别的：
DML包：自动生成
Entity：自动生成
Dao：自动生成，包括对外的Interface以及对应的Implement类
---
这样子，基本上90%的代码工作量都可以省下来了。。。
高效又方便！






