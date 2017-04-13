package xygdev.commons.springjdbc;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Dev Jdbc子处理流程封装。必须是运行在DevJdbc的基础上才可以执行。
 * 封装DevJdbc的一些公用处理
 * @author Sam.T 2016/9/6
 * @version 1.0
 */
public class DevJdbcSubProcess {
	/**
	 * 实现标记@Transactional的事务可以自动回滚的功能。而不是在有unchecked的异常之后才回滚。
	 * <br/>逻辑是：标记某个事务必须给回滚！注意，这个是针对某1个事务来说。只要是同一个事务，做了这个标记，则自动回滚！
	 * <br/>所以，如果想实现这种吊毛的需求：某一段执行完毕要提交，另外一段如果出错要回滚，则可以将这2段处理封装为2个方法（2个事务）即可。
	 * <br/>注意：经过测试，如果事务注释为：propagation=Propagation.NOT_SUPPORTED，则操作无法回滚！估计是没启用事务，用默认的处理了。
	 */
	public static void setRollbackOnly(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
}
