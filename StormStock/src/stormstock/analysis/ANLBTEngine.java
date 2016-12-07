package stormstock.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLImgShow.CurvePoint;
import stormstock.analysis.ANLStockDayKData.DetailData;

public class ANLBTEngine {
	public ANLBTEngine()
	{
		stockListstore = new ArrayList<ANLStock>();
		m_eigenObjMap = new HashMap<String, ANLEigen>();
		m_strategyObj = null;
		m_cANLStockPool = new ANLStockPool();
	}
	public void addEigen(ANLEigen cEigen)
	{
		m_eigenObjMap.put(cEigen.getClass().getSimpleName(), cEigen); 
	}
	public void setStrategy(ANLStrategy cStrategy)
	{
		m_strategyObj = cStrategy;
	}
	public void runBT(String beginDate, String endDate)
	{
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		
		// ------------------------------------------------------------------------------
		// �������й�Ʊ�����û�ɸѡ����Ʊ��
		// ANLLog.outputConsole("loading user stock pool ...\n");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && m_strategyObj.strategy_preload(cANLStock))
			{
				stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("===> load success, stockCnt(%d)\n", stockListstore.size());
		
		// ------------------------------------------------------------------------------
		// ����ָ֤����ȷ�ϻص�����
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
            // ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
			// �Ӵ洢��Ʊ�б�����ȡ��Ӧ���������ݵ��û���Ʊ���У��ص����û�����
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// ��ȡ�û����е���Ӧ��Ʊ����
				ANLStock cANLStockUser = null;
				for(int iUS=0;iUS<m_cANLStockPool.stockList.size();iUS++)
				{
					ANLStock cANLStockUserFind = m_cANLStockPool.stockList.get(iUS);
					if(cANLStockUserFind.id.compareTo(cANLStockStore.id) == 0)
					{
						cANLStockUser = cANLStockUserFind;
					}
				}
				if(null == cANLStockUser)
				{
					cANLStockUser = new  ANLStock(cANLStockStore.id, cANLStockStore.curBaseInfo);
					m_cANLStockPool.stockList.add(cANLStockUser);
				}
				// �����Ӧ���������ݣ������Ϻ��Ƴ�
				int iRemoveCnt = 0;
				for(int iStore = 0; iStore<cANLStockStore.historyData.size();iStore++)
				{
					ANLStockDayKData cANLStockStoreKData = cANLStockStore.historyData.get(iStore);
					// fmt.format("   check date %s\n", cANLStockStoreKData.date);
					if(cANLStockStoreKData.date.compareTo(cANLDayKData.date) <= 0)
					{
						ANLStockDayKData cpObj = new ANLStockDayKData(cANLStockStoreKData, cANLStockUser);
						cANLStockUser.historyData.add(cpObj);
						iRemoveCnt++;
					}
				}
				for(int iRmCnt = 0;iRmCnt<iRemoveCnt;iRmCnt++)
				{
					cANLStockStore.historyData.remove(0);
				}
				// Ϊ��Ʊ��������ֵ
				for(Map.Entry<String, ANLEigen> entry:m_eigenObjMap.entrySet()){     
					String eigenKey = entry.getKey();
					float eigenVal = entry.getValue().calc(cANLStockUser);
					//ANLLog.outputConsole("ANLEigen %s %.3f\n", entry.getKey(), entry.getValue().calc(cANLStockUser));
					cANLStockUser.eigenMap.put(eigenKey, eigenVal);
				}   
			}
			
			// �ص����û�
			List<String> cSelectStockList = new ArrayList<String>();
			ANLLog.outputLog("---> strategy_enter date(%s) stockCnt(%d)\n", cANLDayKData.date, m_cANLStockPool.stockList.size());
			m_strategyObj.strategy_enter(cANLDayKData.date, m_cANLStockPool, cSelectStockList);
        } 
	}
	
	private List<ANLStock> stockListstore;
	private Map<String, ANLEigen> m_eigenObjMap;
	private ANLStrategy m_strategyObj;
	private ANLStockPool m_cANLStockPool;
	
}
