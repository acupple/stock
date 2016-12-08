package stormstock.analysis;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.data.DataEngine;
import stormstock.data.DataEngineBase.StockBaseInfo;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLStock {
	
	public ANLStock()
	{
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
		eigenMap = new HashMap<String, Object>();
	}	 
	public ANLStock(String sid, StockBaseInfo scurBaseInfo)
	{
		id = sid;
		curBaseInfo = scurBaseInfo;
		historyData = new ArrayList<ANLStockDayKData>();
		curBaseInfo = new StockBaseInfo();
		eigenMap = new HashMap<String, Object>();
	}	 

	// ������һ������̼�
	public float GetLastPrice()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).close;
		else
			return 0.0f;
	}
	
	// ������һ�������
	public String GetLastDate()
	{
		if(historyData.size() > 0)
			return historyData.get(historyData.size()-1).date;
		else
			return "0000-00-00";
	}
		
	// ���߼��㣬����date����ǰcount����߼۸�
	public float GetMA(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			sum = sum + cANLDayKData.close;
			sumcnt++;
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		value = sum/sumcnt;
		return value;
	}
	
	// ��ֵ���㣬����date����ǰcount����߼۸�
	public float GetHigh(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.high >= value)
			{
				value = cANLDayKData.high;
			}
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
	
	// ��ֵ���㣬����date����ǰcount����ͼ۸�
	public float GetLow(int count, String date)
	{
		if(historyData.size() == 0) return 0.0f;
		float value = 10000.0f;
		int iE = ANLUtils.indexDayKBeforeDate(historyData, date);
		int iB = iE-count+1;
		if(iB<0) iB=0;
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = historyData.get(i);  
			if(cANLDayKData.low <= value)
			{
				value = cANLDayKData.low;
			}
			//ANLLog.outputConsole("%s %.2f\n", cANLDayKData.date, cANLDayKData.close);
        }
		return value;
	}
	
	public String id;
	public StockBaseInfo curBaseInfo;
	public List<ANLStockDayKData> historyData;
	public Map<String, Object> eigenMap;
}
