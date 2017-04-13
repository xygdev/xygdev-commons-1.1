package xygdev.commons.page;

/**
 * page解析器。主要是封装一些常用的功能。
 * @author Sam.T 2016.8.10
 */
public class PageAnalyze {
	
	/**
	 * 获取当页数据显示的min行数
	 */
    public static int getPageMinRow(int pageNo,int pageSize){
    	int pageMinRow=0;
    	pageMinRow=(pageNo-1)*pageSize+1;    	
    	return pageMinRow;
    }
    
	/**
	 * 获取当页数据显示的max行数
	 */
    public static int getPageMaxRow(int pageNo,int pageSize){
    	int pageMaxRow=0;
    	pageMaxRow=pageNo*pageSize;
    	return pageMaxRow;
    }
    
	/**
	 * 获取最后一页的数据显示的max行数
	 */
    public static int getLastPageMaxRow(int totalPages,int pageSize,long totalRecs){
    	int pageMaxRow=0;
    	long restRecs=totalRecs%pageSize;
    	if(restRecs!=0){
    		pageMaxRow=(int) ((totalPages-1)*pageSize+restRecs);   		
    	}
    	else{
    		pageMaxRow=totalPages*pageSize;
    	}
    	return pageMaxRow;
    }
    
	/**
	 * 获取所有的页数
	 */
    public static int getTotalPages(long totalRecs,int pageSize){
    	int totalPages=0;
    	totalPages=(int)Math.ceil((double)totalRecs/(double)pageSize);
    	return totalPages;
    }
    
	/**
	 * 获取首页的标识
	 */
    public static boolean getFirstPageFlag(int pageNo){
    	boolean firstPageFlag=false;
    	if(pageNo==1){
    		firstPageFlag=true;
    	}
    	return firstPageFlag;
    }
    
	/**
	 * 获取最后一页的标识
	 */
    public static boolean getLastPageFlag(int pageNo,int pageSize,double totalPages,int recsSize){
    	boolean lastPageFlag=false;
    	if(pageSize!=recsSize||totalPages==pageNo){
    		lastPageFlag=true;
    	}
    	return lastPageFlag;
    }   
    
}
