package stormstock.run;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stormstock.analysis.ANLPolicyBase;
import stormstock.analysis.ANLPolicyX1;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLPolicyBase.RetExitCheck;
import stormstock.data.DataEngine;
import stormstock.data.DataWebStockAllList;
import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayDetail.DayDetailItem;
import stormstock.data.DataWebStockDayK.DayKData;

public class RunSuccRateCheckByDays {
	public static Formatter fmt = new Formatter(System.out);
	static class DistriStockItem
	{
		public String id;
		public int iEnter;
		public RetExitCheck cRetExitCheck;
	}
	public static class DistributionItem implements Comparable
	{
		public DistributionItem()
		{
			date="";
			distriStockItemList =  new ArrayList<DistriStockItem>();
		}
		public String date;
		public List<DistriStockItem> distriStockItemList;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			DistributionItem sdto = (DistributionItem)arg0;
		    return this.date.compareTo(sdto.date);
		}
	}
	public static class CheckResult
	{
		public CheckResult()
		{
			distributionList = new ArrayList<DistributionItem>();
		}
		public List<DistributionItem> distributionList;
		public void add(ANLStock cANLStock, int iEnter, RetExitCheck cRetExitCheck)
		{
			String stockId = cANLStock.id;
			String enterDate = cANLStock.historyData.get(iEnter).date;
			float protit = cRetExitCheck.profitPer;
			int costDayCnt = cRetExitCheck.iExit-iEnter;
			
			// �ҵ� ��Ӧ���ڵ� DistributionItem ����, û�оʹ���
			DistributionItem cDistributionItem = null;
			for(int iDate=0; iDate<distributionList.size();iDate++)
			{
				// 
				if(distributionList.get(iDate).date.contains(enterDate))
				{
					cDistributionItem =  distributionList.get(iDate);
					break;
				}
			}
			if(null == cDistributionItem)
			{
				cDistributionItem = new DistributionItem();
				cDistributionItem.date = enterDate;
				distributionList.add(cDistributionItem);
			}
			
			// �ҵ� ��Ӧ���ڵ� DistriStockItem ����, û�оʹ���
			DistriStockItem cDistriStockItem = null;
			for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
			{
				DistriStockItem tmpObj = cDistributionItem.distriStockItemList.get(iStockItem);
				if(tmpObj.id.contains(stockId))
				{
					cDistriStockItem = tmpObj;
					break;
				}
			}
			if(null == cDistriStockItem)
			{
				cDistriStockItem = new DistriStockItem();
				cDistriStockItem.id = stockId;
				cDistributionItem.distriStockItemList.add(cDistriStockItem);
			}
			
			// ��ֵ
			cDistriStockItem.iEnter = iEnter;
			cDistriStockItem.cRetExitCheck = new RetExitCheck(cRetExitCheck);
			
			if(cRetExitCheck.hasEnoughDays)
			{
				fmt.format("   # Check StockID:%s EnterDate: %s profit:%.3f costDayCnt:%d\n", 
						stockId, enterDate, protit, costDayCnt);
			}
			else
			{
				fmt.format("   # Check StockID:%s EnterDate: %s Wait More day!!!\n", 
						stockId, enterDate, protit, costDayCnt);
			}
			
			
		}
		public void printResult(String FileName)
		{
			Collections.sort(distributionList);
			try
			{
				File cfile =new File(FileName);
				cfile.delete();
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				String wLine = "";
				
				wLine = String.format("================================================================\n");
				cOutputStream.write(wLine.getBytes());
		        int iOKCntSum = 0;
		        int iOKCostDayCntSum = 0;
		        float iOKProfitSum = 0.0f;
		        int iNGCntSum = 0;
		        int iNGCostDayCntSum = 0;
		        float iNGProfitSum = 0;
				for(int iDate=0; iDate<distributionList.size();iDate++)
				{
					DistributionItem cDistributionItem = distributionList.get(iDate);
					wLine = String.format("Check date:%s\n", cDistributionItem.date);
					cOutputStream.write(wLine.getBytes());
					for(int iStockItem = 0; iStockItem< cDistributionItem.distriStockItemList.size();iStockItem++)
					{
						DistriStockItem cDistriStockItem = cDistributionItem.distriStockItemList.get(iStockItem);
						String SuccFlag = "";
						if(cDistriStockItem.cRetExitCheck.hasEnoughDays)
						{
							if(cDistriStockItem.cRetExitCheck.profitPer > 0)
							{
								SuccFlag = "[OK]";
								iOKCntSum++;
								iOKCostDayCntSum = iOKCostDayCntSum + (cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter);
								iOKProfitSum = iOKProfitSum + cDistriStockItem.cRetExitCheck.profitPer;
							}
							else
							{
								SuccFlag = "[NG]";
								iNGCntSum++;
								iNGCostDayCntSum = iNGCostDayCntSum +  (cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter);
								iNGProfitSum = iNGProfitSum + cDistriStockItem.cRetExitCheck.profitPer;
							}
							wLine = String.format("    StockId:%s %s Profit:%f costDayCnt:%d\n", 
									cDistriStockItem.id, 
									SuccFlag, 
									cDistriStockItem.cRetExitCheck.profitPer, 
									(cDistriStockItem.cRetExitCheck.iExit - cDistriStockItem.iEnter));
						}
						else
						{
							wLine = String.format("    StockId:%s Wait More day!!!\n", cDistriStockItem.id);
						}
						
						cOutputStream.write(wLine.getBytes());
					}
				}
				
				float succRate = (float)iOKCntSum/(iOKCntSum+iNGCntSum);
				float OKAveCostDayCnt = (float)iOKCostDayCntSum/iOKCntSum;
				float OKAveProfit = iOKProfitSum/iOKCntSum;
				float NGAveCostDayCnt = (float)iNGCostDayCntSum/iNGCntSum;
				float NGAveProfit = iNGProfitSum/iNGCntSum;
				wLine = String.format("\n# ------------------------------------ #\n");
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    suncRate:%.3f\n", succRate);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    OKAveCostDayCnt:%.3f OKAveProfit:%.3f\n", OKAveCostDayCnt, OKAveProfit);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				wLine = String.format("    NGAveCostDayCnt:%.3f NGAveProfit:%.3f\n", NGAveCostDayCnt, NGAveProfit);
				cOutputStream.write(wLine.getBytes());
				System.out.print(wLine);
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println("Exception:" + e.getMessage()); 
			}
		}
	}
	
	public static StockItem popRandomStock(List<StockItem> in_list)
	{
		if(in_list.size() == 0) return null;
		Random random = new Random();
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockItem cStockItem = new  StockItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockItem;
	}
	public static List<StockItem> getRandomStock(int count)
	{
		List<StockItem> retList = new ArrayList<StockItem>();
		if(0 != count)
		{
			List<StockItem> retListAll = new ArrayList<StockItem>();
			int ret = DataWebStockAllList.getAllStockList(retListAll);
			if(0 == ret)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockItem cStockItem = popRandomStock(retListAll);
					retList.add(cStockItem);
		        } 
			}
			else
			{
			}
		}
		return retList;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("### Main Begin");
		// ����
		ANLPolicyBase cPolicy = new ANLPolicyX1();
		List<StockItem> retStockList = null;
		// ֻ��������
		retStockList = getRandomStock(100);
		// �������й�Ʊ
//		retStockList = new ArrayList<StockItem>();
//		DataWebStockAllList.getAllStockList(retStockList);
		// ������ǰ�������
		int iTestMaxDaysCnt = 250;
		///////////////////////////////////////////////////////////////////////////////////

		CheckResult cCheckResult = new CheckResult();
		
		// ��ʼ���ֲ��б�������ָ֤��
		List<DayKData> retList999999 = new ArrayList<DayKData>();
		int retgetDayKDataQianFuQuan = DataEngine.getDayKDataQianFuQuan("999999", retList999999);
		if(0 == retgetDayKDataQianFuQuan)
		{
			// ��ʼ���б������Ŀ
			for(int i = 0; i < retList999999.size(); i++)  
	        {  
				DayKData cDayKData = retList999999.get(i);  
				//fmt.format("date:%s \n", cDayKData.date);  
				DistributionItem cDistributionItem = new DistributionItem();
				cDistributionItem.date = cDayKData.date;
				cCheckResult.distributionList.add(cDistributionItem);
	        } 
		}
		else
		{
			fmt.format("ERROR retgetDayKDataQianFuQuan:%d", retgetDayKDataQianFuQuan);
			return;
		}

		if(retStockList.size() != 0)
		{
			for(int iStock = 0; iStock < retStockList.size(); iStock++)  
	        {  
				StockItem cStockItem = retStockList.get(iStock);
				String stockId = cStockItem.id;
				
				// �Ե�ֻ��Ʊ���м���
				fmt.format("check for stockId:%s ...\n", stockId);
				ANLStock cANLStock = ANLStockPool.getANLStockNF(stockId);
				if(null == cANLStock)
				{
					continue;
				}
				
				int lenlist = cANLStock.historyData.size();
				int iBegin = 0;
				if(lenlist > iTestMaxDaysCnt)
				{
					iBegin = lenlist-iTestMaxDaysCnt;
				}
				for(; iBegin < lenlist; iBegin++) 
				{
					ANLStockDayKData cCheckDayKData = cANLStock.historyData.get(iBegin);
					if(cPolicy.enterCheck(cANLStock, iBegin))
					{
						RetExitCheck cRetExitCheck = cPolicy.exitCheck(cANLStock, iBegin);

						cCheckResult.add(cANLStock, iBegin, cRetExitCheck);
						
						iBegin = iBegin + 20;
					}
				}
	        } 
		}
		else
		{
			fmt.format("ERROR retgetAllStockList: NULL");
		}
		
		cCheckResult.printResult("TestDistribution.txt");
		
		System.out.println("### Main End");
	}
}
