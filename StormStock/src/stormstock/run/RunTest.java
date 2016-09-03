package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class RunTest {
	public static Formatter fmt = new Formatter(System.out);
	public static void rmlog()
	{
		File cfile =new File("test.txt");
		cfile.delete();
	}
	public static void outputLog(String s, boolean enable)
	{
		if(!enable) return;
		fmt.format("%s", s);
		File cfile =new File("test.txt");
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	public static void outputLog(String s)
	{
		outputLog(s, true);
	}
	
	public static void main(String[] args) {
		rmlog();
		outputLog("Main Begin\n\n");
		
		// ��Ʊȫ�б�������й�Ʊid
		List<String> cStockList = ANLStockPool.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			outputLog(stockId + "\n");
		}
		
		
		// ���һֻ��Ʊ������k����
		ANLStock cANLStock = ANLStockPool.getANLStock("600020");
		for(int j = 0; j < cANLStock.historyData.size(); j++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
            fmt.format("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
            
            // ���һ�콻�׵���ϸ����
            if(j == cANLStock.historyData.size()-1)
            {
            	cANLDayKData.LoadDetail();
            	for(int k = 0; k < cANLDayKData.detailDataList.size(); k++)  
            	{
            		fmt.format("    %s %.2f\n", 
            				cANLDayKData.detailDataList.get(k).time,
            				cANLDayKData.detailDataList.get(k).price);
            	}
            }
        } 
		
		outputLog("\n\nMain End");
	}

}
