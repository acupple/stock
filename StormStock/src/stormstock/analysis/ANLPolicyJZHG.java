package stormstock.analysis;

import java.util.List;

// ��ֵ�ع�
public class ANLPolicyJZHG extends ANLPolicy {

	public void check_today(String date)
	{
		fmt.format("check_today %s\n", date);
		// ��Ʊȫ�б�������й�Ʊid
		List<String> cStockList = ANLStockPool.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			//outputLog(stockId + "\n");
			// ���һֻ��Ʊ������k����
			ANLStock cANLStock = ANLStockPool.getANLStock(stockId, date);
			if(null == cANLStock) continue;
			
			for(int j = 0; j < cANLStock.historyData.size(); j++)  
	        {  
				ANLStockDayKData cANLDayKData = cANLStock.historyData.get(j);  
	            //fmt.format("date:%s open %.2f\n", cANLDayKData.date, cANLDayKData.open);
	        }
		}
	}
	public static void main(String[] args) {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.run("2016-01-01", "2016-01-08");
		fmt.format("main end\n");
	}

}
