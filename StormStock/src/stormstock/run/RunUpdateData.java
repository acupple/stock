package stormstock.run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
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
	public static Formatter fmt = new Formatter(System.out);
	public static void main(String[] args) {
		int iRetupdateStock = 0;
		// ����ָ��k
		String ShangZhiId = "999999";
		String ShangZhiName = "��ָ֤��";
		iRetupdateStock = DataEngineBase.updateStock(ShangZhiId);
		if(iRetupdateStock >= 0)
		{
			fmt.format("update success: %s (%s) item:%d\n", ShangZhiId, ShangZhiName, iRetupdateStock);
		}
		else
		{
			fmt.format("update ERROR: %s (%s) item:%d\n", ShangZhiId, ShangZhiName, iRetupdateStock);
		}
		
		// ��������k
		List<StockItem> retList = new ArrayList<StockItem>();
		int ret = DataWebStockAllList.getAllStockList(retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				StockItem cStockItem = retList.get(i);  
	            iRetupdateStock = DataEngineBase.updateStock(cStockItem.id);
	            if( iRetupdateStock >= 0)
	            {
	            	fmt.format("update success: %s (%s) item:%d\n", cStockItem.id, cStockItem.name, iRetupdateStock);
	            }
	            else
	            {
	            	fmt.format("update ERROR: %s (%s) item:%d\n", cStockItem.id, cStockItem.name, iRetupdateStock);
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
