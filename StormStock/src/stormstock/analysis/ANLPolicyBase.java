package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class ANLPolicyBase {
	public static Formatter fmt = new Formatter(System.out);
	public static String strLogName = "ANLPolicyBase.txt";
	public static void rmlog()
	{
		File cfile =new File(strLogName);
		cfile.delete();
	}
	public static void outputLog(String s, boolean enable)
	{
		if(!enable) return;
		fmt.format("%s", s);
		File cfile =new File(strLogName);
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
	// ������������
	public static int indexDayK(List<ANLStockDayKData> dayklist, String dateStr)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) <= 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	// ����i��j�յļ۸��ֵ
	public static float priceAve(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float ave = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			ave = ave + cDayKDataTmp.close;
		}
		ave = ave/(j-i+1);
		return ave;
	}
	// ����i��j�յ���߼۸�
	public static float priceHigh(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float high = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
				high = cDayKDataTmp.high;
		}
		return high;
	}
	// ����i��j�յ���ͼ۸�
	public static float priceLow(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
				low = cDayKDataTmp.low;
		}
		return low;
	}
	// ����i��j�յ���߼۸������
	public static int indexHigh(List<ANLStockDayKData> dayklist, int i, int j)
	{
		int index = i;
		float high = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
			{
				high = cDayKDataTmp.high;
				index = k;
			}
		}
		return index;
	}
	// ����i��j�յ���ͼ۸������
	public static int indexLow(List<ANLStockDayKData> dayklist, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
			{
				low = cDayKDataTmp.low;
				index = k;
			}
		}
		return index;
	}
	// ����i��j�յĲ������Ȳ���,��ֵԽ�󲨶�Խ��
	public static float waveParam(List<ANLStockDayKData> dayklist, int i, int j)
	{
		float wave = 0.0f;
		for(int k = i; k<=j; k++ )
		{
			ANLStockDayKData cDayKDataTmp = dayklist.get(k);
			float wave1 = Math.abs((cDayKDataTmp.close - cDayKDataTmp.open)/cDayKDataTmp.open);
			float wave2 = (cDayKDataTmp.high - cDayKDataTmp.low)/cDayKDataTmp.low;
			wave = wave + (wave1+wave2)/2;
		}
		wave = wave/(j-i+1);
		return wave;
	}
	// i�Ƿ��Ƕ����´�������
	public static boolean ShenFuXiaCuoQiWen(List<ANLStockDayKData> dayklist, int i)
	{
		String logstr;
		ANLStockDayKData cDayKData = dayklist.get(i);
		
		// ����i��ǰ10������´��λ��
		int pre10low_index = indexLow(dayklist, i-10, i-1);
		float last10high = priceHigh(dayklist, i-10, i-1);
		float last10low = priceLow(dayklist, i-10, i-1);
		float maxZhenFu = (last10low - last10high)/last10high;
		
		// ����ǰ2���� ����2��;�ֵ,���ֵ�´�
		float pre7Ave = priceAve(dayklist, i-10, i-4);
		float last3Ave = priceAve(dayklist, i-3, i-1);
		float xiacuo = (last3Ave - pre7Ave)/pre7Ave;
		
		// ǰ5����͵���i-1��, ����
		if(pre10low_index == i-1)
		{
			logstr = String.format("    %.2f\n",
					xiacuo);
			outputLog(logstr);
			//�����жϷ����ж�
			if(maxZhenFu < -0.08)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void transectionAnalysis(List<DayKData> dayklist, int m)
	{
		String logstr;
		DayKData cDayKDataPre1 = dayklist.get(m-1);
		DayKData cDayKDataPre2 = dayklist.get(m-2);
		
		boolean bEnter = false;
		int iEnter = 0;
		float enterPrice = 0.0f;
		float checkhigh = cDayKDataPre1.high;
		for(int j = m; j < m+5; j++ )
		{
			DayKData cDayKDataTmp = dayklist.get(j);
			if(cDayKDataTmp.high > checkhigh) checkhigh = cDayKDataTmp.high;
			float checkEnterPrice = (cDayKDataPre1.low + cDayKDataPre2.low)/2;
			
			if(cDayKDataTmp.low < checkEnterPrice && 
					checkEnterPrice < cDayKDataPre1.close) {
				logstr = String.format("    Enter %s %.2f\n",
						cDayKDataTmp.date,checkEnterPrice);
				
				outputLog(logstr);
				bEnter = true;
				iEnter = j;
				enterPrice = checkEnterPrice;
				break;
			}
		}
		if(bEnter)
		{
			for(int j = iEnter+1; j < iEnter+10; j++ )
			{
				DayKData cDayKDataTmp = dayklist.get(j);
				if(cDayKDataTmp.low < enterPrice*(1-0.05)) {
					logstr = String.format("        - Fail\n",
							cDayKDataTmp.date);
					outputLog(logstr);
					break;
				}
				if(cDayKDataTmp.high > enterPrice*(1+0.03))
				{
					logstr = String.format("        - Succ\n",
							cDayKDataTmp.date);
					outputLog(logstr);
					break;
				}
			}
		}
		else
		{
			logstr = String.format("    NotEnter\n"
					);
			outputLog(logstr);
		}
		
	}
	
	public static void analysisOne(String id, String fromDate, String toDate)
	{
		String logstr;
		
		ANLStock cANLStock = ANLStockPool.getANLStock(id);
		int iB = indexDayK(cANLStock.historyData, fromDate);
		int iE = indexDayK(cANLStock.historyData, toDate);
		
		int iLastHighIndex = 0;
		for(int i = iB; i<= iE; i++)
		{
			// ��鵱ǰ�죬ǰ6��20���������㼱������
			logstr = String.format("CheckDay %s\n",
					cANLStock.historyData.get(i).date);
			outputLog(logstr);
			
			int iCheckE = i;
			for(int iCheckB = iCheckE-6; iCheckB>=iCheckE-20; iCheckB--)
			{
				if(iCheckB >= iB)
				{
					// @ ��ߵ�����͵�������λ�õ��ж�
					boolean bCheckHighLowIndex = false;
					int indexHigh = indexHigh(cANLStock.historyData, iCheckB, iCheckE);
					int indexLow = indexLow(cANLStock.historyData, iCheckB, iCheckE);
					if(indexHigh>iCheckB && indexHigh<=(iCheckB+iCheckE)/2
							&& indexLow > (iCheckB+iCheckE)/2 && indexLow < iCheckE)
					{
						bCheckHighLowIndex = true;
					}
					
					// @ ��ߵ�����͵��´�����ж�
					boolean bCheckXiaCuo = false;
					float highPrice = cANLStock.historyData.get(indexHigh).high;
					float lowPrice = cANLStock.historyData.get(indexLow).low;
					float xiaCuoZhenFu = (lowPrice-highPrice)/highPrice;
					float xiaCuoMinCheck = -1.5f*0.01f*(indexLow-indexHigh);
					if(xiaCuoZhenFu < xiaCuoMinCheck)
					{
						bCheckXiaCuo = true;
					}
					
					// @ ǰ���䣬�������λ�ж�
					boolean bCheck3 = true;
					int cntDay = iCheckE-iCheckB;
					float midPrice = (highPrice+lowPrice)/2;
					for(int c= iCheckB; c<iCheckB + cntDay/3; c++)
					{
						if(cANLStock.historyData.get(c).low < midPrice)
						{
							bCheck3 = false;
						}
					}
					for(int c= iCheckE; c>iCheckE - cntDay/3; c--)
					{
						if(cANLStock.historyData.get(c).low > midPrice)
						{
							bCheck3 = false;
						}
					}
					
					
					if(indexHigh-iLastHighIndex > 5 &&
							bCheckHighLowIndex && 
							bCheckXiaCuo && 
							bCheck3)
					{
						logstr = String.format("    Test XiaCuoQuJian [%s,%s] ZhenFu(%.3f,%.3f)\n",
						cANLStock.historyData.get(iCheckB).date,
						cANLStock.historyData.get(iCheckE).date,
						xiaCuoZhenFu,xiaCuoMinCheck);
						outputLog(logstr);
						
						iLastHighIndex = indexHigh;
						break;
					}
					
				}
			}
		}
		
//		float priceAve_test = priceAve(cANLStock.historyData, iB, iE);
//		float priceHigh_test = priceHigh(cANLStock.historyData, iB, iE);
//		float priceLow_test = priceLow(cANLStock.historyData, iB, iE);
//		int indexHigh_test = indexHigh(cANLStock.historyData, iB, iE);
//		int indexLow_test = indexLow(cANLStock.historyData, iB, iE);
//		float waveParam_test = waveParam(cANLStock.historyData, iB, iE);
//		logstr = String.format("priceAve_test[%.2f] priceHigh_test[%.2f] priceLow_test[%.2f]"
//				+ " indexHigh_test[%s] indexLow_test[%s] waveParam_test[%.3f]\n",
//				priceAve_test, priceHigh_test, priceLow_test,
//				cANLStock.historyData.get(indexHigh_test).date, 
//				cANLStock.historyData.get(indexLow_test).date, waveParam_test);
//		outputLog(logstr);
		
		// ������ߵ���͵������Ϻ󣬵����������������8���ܵ��������������������ϵ������ñȽϴ��������Ƿ��ռ䣬ƫ��5��ʱ��۸��ֵ����һ����������
//		
//		// ȷ��Ҫ�鿴��׼������������
//		int beginPreCheckPos = 0;
//		int endPreCheckPos = 0;
//		for (int i = 0; i< retList.size(); i++)
//		{
//			DayKData cDayKDataTmp = retList.get(i);
//			
//			if(cDayKDataTmp.date.compareTo(fromDate) >= 0)
//			{
//				beginPreCheckPos = i;
////				logstr = String.format("inputcheck[%s %s] Cur %s beginPreCheckPos %d\n",
////						fromDate, toDate, cDayKDataTmp.date, beginPreCheckPos);
////				outputLog(logstr);
//				break;
//			}
//		}
//		for (int i = retList.size()-1; i> 0; i--)
//		{
//			DayKData cDayKDataTmp = retList.get(i);
//			
//			if(cDayKDataTmp.date.compareTo(toDate) <= 0)
//			{
//				endPreCheckPos = i;
////				logstr = String.format("inputcheck[%s %s] Cur %s endPreCheckPos %d\n",
////						fromDate, toDate, cDayKDataTmp.date, endPreCheckPos);
////				outputLog(logstr);
//				break;
//			}
//		}
//		
//		
//		int iLast = 0;
//		for (int i = beginPreCheckPos + 10; i<= endPreCheckPos; i++)
//		{
//			DayKData cDayKDataTmp = retList.get(i);
////			logstr = String.format("check %s\n",
////					cDayKDataTmp.date);
////			outputLog(logstr);
//			
//			
//			if(ShenFuXiaCuoQiWen(retList, i))
//			{
//				logstr = String.format("%s PreEnterPoint (%d) %d\n",
//						cDayKDataTmp.date, i, iLast);
//				outputLog(logstr);
//				
////					logstr = String.format("%s  Lianxu PreEnterPoint (%d) %d\n",
////							cDayKDataTmp.date, i, iLast);
////					outputLog(logstr);
//					
//					transectionAnalysis(retList, i+1);
//					
//				iLast = i;
//			}
//		}
	}
	public static void main(String[] args) {
		strLogName = "ANLPolicyBase.txt";
		rmlog();
		outputLog("Main Begin\n\n");
		// ��Ʊ�б�
		List<StockItem> cStockList = new ArrayList<StockItem>();
		cStockList.add(new StockItem("300312"));
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
			analysisOne(stockId, "2008-01-10", "2016-08-23");
		}
		outputLog("\n\nMain End");
	}
}
