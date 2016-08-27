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
	
	// ����ͳ�ƽ��ȫ�ּ�¼
	public static String stockId;
	public static int succCnt = 0;
	public static int failCnt = 0;
	public static float initMoney = 10000.0f;
	public static float curMoney = initMoney;
	
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
	
	// ���в��Խ���
	public static void transectionAnalysis(List<ANLStockDayKData> dayklist, 
			String enterDate, int iMaxTranDays, float zhisun, float zhiying)
	{
		String logstr;
		logstr = String.format("    | transectionAnalysis  beginDate(%s) maxTranDays(%d) waitPrice[%.2f,%.2f] \n",
				enterDate, iMaxTranDays, zhisun, zhiying);
		outputLog(logstr);
		
		int iTranBegin = indexDayK(dayklist, enterDate);
		float enterPrice = dayklist.get(iTranBegin).open;
		float zhisunPrice = enterPrice * (1+zhisun);
		float zhiyingPrice = enterPrice * (1+zhiying);
		
		
		float outPrice = 0.0f;
		int outIndex = 0;
		for(int i = iTranBegin; i < iTranBegin + iMaxTranDays; i++ )
		{
			if(i >= dayklist.size())
			{
				break;
			}
			ANLStockDayKData cDayKDataTmp = dayklist.get(i);
			if(cDayKDataTmp.low < zhisunPrice) {
				outPrice = zhisunPrice;
				outIndex = i;
				break;
			}
			if(cDayKDataTmp.high > zhiyingPrice) {
				outPrice = zhiyingPrice;
				outIndex = i;
				break;
			}
			if(i == iTranBegin + iMaxTranDays - 1)
			{
				outPrice = cDayKDataTmp.close;
				outIndex = i;
				break;
			}
		}
		
		if(outIndex > 0)
		{
			float profit = (outPrice - enterPrice)/enterPrice;
			if(profit>0)
			{
				succCnt = succCnt + 1;
			}
			else
			{
				failCnt = failCnt + 1;
			}
			curMoney = curMoney*(1+profit);
			
			logstr = String.format("    | %s enterPrice:%.2f [%.2f,%.2f]  - ",
					dayklist.get(iTranBegin).date, enterPrice,zhisunPrice,zhiyingPrice);
			logstr += String.format("%s outPrice:%.2f curMoney:%.2f profit:%.3f\n",
					dayklist.get(outIndex).date, outPrice,curMoney, profit);
			outputLog(logstr);
		}
		else
		{
			logstr = String.format("    | waiting\n");
			outputLog(logstr);
		}
	}
	public static void initProfitInfo(String inStockId) {
		stockId = inStockId;
		succCnt = 0;
		failCnt = 0;
		curMoney = initMoney;
	}
	public static void printProfitInfo() {
		String logstr;
		logstr = String.format("\n --- printProfitInfo ---\n");
		outputLog(logstr);
		logstr = String.format("    succCnt: %d \n", succCnt);
		outputLog(logstr);
		logstr = String.format("    failCnt: %d \n", failCnt);
		outputLog(logstr);
		logstr = String.format("    initMoney: %.2f \n", initMoney);
		outputLog(logstr);
		logstr = String.format("    curMoney: %.2f \n", curMoney);
		outputLog(logstr);
		logstr = String.format("    totalProfit: %.3f \n", (curMoney- initMoney)/initMoney);
		outputLog(logstr);
	}
	
	public static void analysisOne(String id, String fromDate, String toDate)
	{
		initProfitInfo(id);
		
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
			
			int iEnterTran = -1;
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
					
					
					if(indexHigh-iLastHighIndex > 1 &&
							bCheckHighLowIndex && 
							bCheckXiaCuo && 
							bCheck3)
					{
						logstr = String.format("    Test XiaCuoQuJian [%s,%s] ZhenFu(%.3f,%.3f)\n",
						cANLStock.historyData.get(iCheckB).date,
						cANLStock.historyData.get(iCheckE).date,
						xiaCuoZhenFu,xiaCuoMinCheck);
						outputLog(logstr);
						
						// ���ײ��Խ׶�
						iEnterTran = iCheckE + 1;
						if(iEnterTran <= iE)
						{
							transectionAnalysis(cANLStock.historyData, 
									cANLStock.historyData.get(iEnterTran).date,
									10, -0.1f, +0.1f);
						}
						
						iLastHighIndex = indexHigh;
						break;
					}
					
				}
			}
		}
		
		printProfitInfo();
		
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
			analysisOne(stockId, "2008-05-10", "2016-08-23");
		}
		
		outputLog("\n\nMain End");
	}
}
