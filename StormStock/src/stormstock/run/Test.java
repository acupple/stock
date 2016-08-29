package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLStockDayKData;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.run.RunSuccRateCheckByStocks.ProfitResult;

public class Test {
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
	
	public static void analysisOne(String id, int maxDayCount)
	{
		String logstr;
		
		List<DayKData> retList = new ArrayList<DayKData>();
		DataEngine.getDayKDataQianFuQuan(id, retList);
		
		// 5��ƽ���´����
		float xiacuo_ave = 0.0f;
		int xiacuo_cnt = 0;
		
		// ��ʷ�ɹ�ʧ�ܴ���
		int SuccCnt = 0;
		int FailCnt = 0;
		
		//�ϴμ������
		int ilastpoint = 0;
		
		for (int i =10; i< retList.size()-1; i++)
		{
			DayKData cDayKData = retList.get(i);
			
			// ����i��ǰ��������´��λ�����λ��ֵ 
			float last5low = 1000.0f;
			float last5ave = 0.0f;
			for(int j = i-5; j!=i; j++ )
			{
				DayKData cDayKDataTmp = retList.get(j);
				last5ave = last5ave + (cDayKDataTmp.high + cDayKDataTmp.low)/2;
				if(cDayKDataTmp.low < last5low) {
					last5low = cDayKDataTmp.low;
				}
			}
			last5ave = last5ave/5;
			
			// ����i����һ�����ֵ
			float nextlow = 0.0f;
			DayKData cDayKDataNext = retList.get(i+1);
			nextlow = cDayKDataNext.low;
			
			// @@@ ����������i��Ϊ��5������´��
			if(cDayKData.low < last5low 
					&& (cDayKDataNext.low > cDayKData.low)
					)
			{
				// ͳ���´������5���´�ƽ��ֵ
				float xiacuo_all = xiacuo_ave*xiacuo_cnt;
				xiacuo_cnt++;
				xiacuo_ave = (xiacuo_all + (cDayKData.low - last5ave)/last5ave)/xiacuo_cnt;
				
				float xiacuo = (cDayKData.low - last5ave)/last5ave;
				
				// @@@ ����������i�յ��´���ȱȽϴ�
				if((cDayKData.low - last5ave)/last5ave <= xiacuo_ave/3*2) 
				{
					
					// @@@ �������������ڻ������´��
					if(i - ilastpoint > 2 && i - ilastpoint <= 15) 
					{
						// �ж������־����
						boolean enablelog = false;
						if(retList.size()-1 - i < maxDayCount)
						{
							enablelog = true;
						}
							
						logstr = String.format("%s  %s - " + "Low(%.2f)"
								+ " Last5Low(%.2f) "
								+ "last5ave(%.2f) XiaCuo(%.2f) XiaCuoAve(%.2f)"
								+ " ",
								id, cDayKData.date, cDayKData.low, 
								last5low, 
								last5ave, xiacuo, xiacuo_ave);
						outputLog(logstr, enablelog);
						
						// ���������
						float gueesHigh = (last5ave - cDayKDataNext.low)/4*1 + cDayKDataNext.low;
						float gueesHighRate = (gueesHigh - cDayKDataNext.low)/cDayKDataNext.low;
						float guessLow = cDayKDataNext.low * (1-gueesHighRate/3*3);
						float guessLowRate = (guessLow - cDayKDataNext.low)/cDayKDataNext.low;
						
						float realHigh = 0.0f;
						float realLow = 1000.0f;
						float lastDayClose = 0.0f;
						float lastDayCloseRate = 0.0f;
						for(int j =i+1; j<retList.size()-1 && j<=i+5; j++)
						{
							DayKData cDayKDataTmp = retList.get(j);
							if(cDayKDataTmp.high > realHigh) realHigh = cDayKDataTmp.high;
							if(cDayKDataTmp.low < realLow) realLow = cDayKDataTmp.low;
							lastDayClose = cDayKDataTmp.close;
							lastDayCloseRate = (lastDayClose - cDayKData.low)/cDayKData.low;
							
						}
						
						logstr = String.format(""
								+ "gueesHigh(%.2f,%.2f) guessLow(%.2f,%.2f) "
								+ "realHigh(%.2f) reallow(%.2f) "
								+ "lastDayClose(%.2f,%.2f) ", 
								gueesHigh, gueesHighRate,guessLow,guessLowRate,
								realHigh, realLow,
								lastDayClose,lastDayCloseRate);
						outputLog(logstr, enablelog);
						
						float succRate = 0.0f;
						if(realHigh > gueesHigh) succRate = gueesHighRate;
						if(realLow < guessLow) succRate = guessLowRate;
						if(realHigh <= gueesHigh && realLow >= guessLow) succRate = lastDayCloseRate;
						if(succRate > 0) SuccCnt++;
						if(succRate <= 0) FailCnt++;
						float HisSucc = 0.0f;
						if(SuccCnt!=0 || FailCnt!=0) HisSucc = (float)SuccCnt/(SuccCnt+FailCnt);
						logstr = String.format("Result(%.2f) HisSucc(%.2f) AllCnt(%d)\n",
								succRate, HisSucc, SuccCnt + FailCnt);
						outputLog(logstr, enablelog);
						
						
					}
					
					// �����ϴν����
					ilastpoint = i;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		rmlog();
		outputLog("Main Begin\n\n");
		// ��Ʊ�б�
		List<StockItem> cStockList = new ArrayList<StockItem>();
		cStockList.add(new StockItem("600000"));
//		cStockList.add(new StockItem("300191"));
// 		cStockList.add(new StockItem("002344"));
//		cStockList.add(new StockItem("002695"));
//		cStockList.add(new StockItem("300041"));
//		cStockList.add(new StockItem("600030"));
		if(cStockList.size() <= 0)
		{
			// cStockList =  DataEngine.getLocalRandomStock(30);
			cStockList = DataEngine.getLocalAllStock();
		}
		
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i).id;
			analysisOne(stockId, 10000);
		}
		outputLog("\n\nMain End");
	}

}
