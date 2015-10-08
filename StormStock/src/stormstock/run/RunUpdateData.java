package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stormstock.data.DataEngineBase;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;
import stormstock.data.DataWebStockRealTimeInfo;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class RunUpdateData {
	
	
	public static void main(String[] args) {

		// ����ָ��k
		if(0 == DataEngineBase.updateStock("999999"))
		{
			System.out.println("update success: 999999 ����ָ֤��  ��");
		}
		else
		{
			System.out.println("update success: 999999 ����ָ֤��  �� ERROR");
		}
		
		// ��������k
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = DataWebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id); 
	            if(0 == DataEngineBase.updateStock(cStockItem.id))
	            {
	            	System.out.println("update success: " +  cStockItem.id);
	            }
	            else
	            {
	            	System.out.println("update ERROR:" + cStockItem.id);
	            }
	            
	        } 
			System.out.println("count:" + retList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
