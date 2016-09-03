package stormstock.analysis;

import java.util.ArrayList;
import java.util.List;

import stormstock.analysis.ANLStockDayKData.DetailData;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockRealTimeInfo.RealTimeInfo;

public class ANLStockPool {
	// ������й�Ʊid�б�
	public static List<String> getAllStocks()
	{
		List<String> retList = new ArrayList<String>();
		
		List<StockItem> cStockList = DataEngine.getLocalAllStock();
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			retList.add(stockId);
		}
		
		return retList;
	}
	
	// �����ݼ���
	public static ANLStock getANLStock(String id)
	{
		List<DayKData> retList = new ArrayList<DayKData>();
		int ret = DataEngine.getDayKDataQianFuQuan(id, retList);
		if(0 != ret || retList.size() == 0)
		{
			return null;
		}
			
		ANLStock cANLStock = new ANLStock();
		cANLStock.id = id;
		
		for(int i = 0; i < retList.size(); i++)  
        {  
			ANLStockDayKData cANLStockDayKData = new ANLStockDayKData();
			DayKData cDayKData = retList.get(i);  
			cANLStockDayKData.ref_ANLStock = cANLStock;
			cANLStockDayKData.date = cDayKData.date;
			cANLStockDayKData.open = cDayKData.open;
			cANLStockDayKData.close = cDayKData.close;
			cANLStockDayKData.low = cDayKData.low;
			cANLStockDayKData.high = cDayKData.high;
			cANLStockDayKData.volume = cDayKData.volume;
//            System.out.println(cDayKData.date + "," 
//            		+ cDayKData.open + "," + cDayKData.close); 
			cANLStock.historyData.add(cANLStockDayKData);
        } 
		
		return cANLStock;
	}
}
