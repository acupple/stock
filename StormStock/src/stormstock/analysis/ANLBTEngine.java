package stormstock.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.analysis.ANLImgShow.CurvePoint;
import stormstock.analysis.ANLStockDayKData.DetailData;
import stormstock.analysis.ANLStrategy.SelectResult;

/*
 * ANL Back Test Engine class
 */
public class ANLBTEngine {
	/*
	 * SelectResultWrapper�࣬����ѡ�����ȼ�����
	 */
	static private class SelectResultWrapper {
		public SelectResultWrapper(){
			selectRes = new SelectResult();
		}
		// ���ȼ��Ӵ�С����
		static public class SelectResultCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				SelectResultWrapper c1 = (SelectResultWrapper)object1;
				SelectResultWrapper c2 = (SelectResultWrapper)object2;
				int iCmp = Float.compare(c1.selectRes.fPriority, c2.selectRes.fPriority);
				if(iCmp > 0) 
					return -1;
				else if(iCmp < 0) 
					return 1;
				else
					return 0;
			}
		}
		public String stockId;
		public SelectResult selectRes;
	}

	/*
	 * ����
	 */
	public ANLBTEngine(String name)
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		m_logfilename = name + ".txt";
		ANLLog.init(m_logfilename);
		String imgfilename = name + ".jpg";
		m_cImageShow = new ANLImgShow(1600,900,imgfilename);
		m_poiList_shangzheng = new ArrayList<CurvePoint>();
		m_poiList_money = new ArrayList<CurvePoint>();
		
		m_stockListstore = new ArrayList<ANLStock>();
		m_cANLStockPool = new ANLStockPool();
		
		m_cUserAcc = new ANLUserAcc(m_cANLStockPool);
		
		m_eigenObjMap = new HashMap<String, ANLEigen>();
		m_strategyObj = null;
	}
	
	/*
	 * Ϊ����������������������ڼ����Ʊ������ֵ
	 */
	public void addEigen(ANLEigen cEigen)
	{
		m_eigenObjMap.put(cEigen.getClass().getSimpleName(), cEigen); 
	}
	
	/*
	 * Ϊ������������Ҫ���ԵĲ���
	 */
	public void setStrategy(ANLStrategy cStrategy)
	{
		m_strategyObj = cStrategy;
	}
	
	/*
	 * ���й�Ʊ�ز�
	 * beginDate: ��ʼ���� ���� ��2016-01-01��
	 * endDate: �������� ���� ��2016-12-31��
	 */
	public void runBT(String beginDate, String endDate)
	{
		if(null == m_strategyObj) {
			ANLLog.outputConsole("m_strategyObj is null\n");
			return;
		}
		
		// ------------------------------------------------------------------------------
		// �˻������ʼ��
		m_cUserAcc.init(100000.0f);
		
		// ------------------------------------------------------------------------------
		// �������й�Ʊ��Ԥ���ص����й�Ʊ�б�
		ANLLog.outputConsole("loading user stock pool ...");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && m_strategyObj.strategy_preload(cANLStock))
			{
				m_stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("==> load success, stockCnt(%d)\n", m_stockListstore.size());
		
		// ------------------------------------------------------------------------------
		// ����ָ֤����ȷ�ϻص�����
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cShangZhengKData = cANLStock.historyData.get(i);  
			String curDate = cShangZhengKData.date;
			m_poiList_shangzheng.add(new CurvePoint(i,cShangZhengKData.close));
			
			// ���ɵ����Ʊ��
			generateStockPoolToday(curDate);
            
			// �����˻���ǰ����
			m_cUserAcc.update(curDate);
			
			// ��ǰ��Ʊ���й�Ʊȫ���ص����û������select��Ʊ�б�
			List<String> cSelectStockList = new ArrayList<String>();
			callStockPoolUserSelect(curDate, cSelectStockList);
			
			// ģ���˻�����
			mockTransaction(curDate, cSelectStockList);
			m_poiList_money.add(new CurvePoint(i,m_cUserAcc.GetTotalAssets()));
        }
		
		m_cImageShow.writeLogicCurve(m_poiList_shangzheng, 1);
		m_cImageShow.writeLogicCurve(m_poiList_money, 2);
		m_cImageShow.GenerateImage();
		ANLLog.outputLog("--> run back test over!");
	}
	
	private void generateStockPoolToday(String date)
	{
		// ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
		// �Ӵ洢��Ʊ�б�����ȡ��Ӧ���������ݵ��û���Ʊ���У��ص����û�����
		for(int iS=0;iS<m_stockListstore.size();iS++)
		{
			ANLStock cANLStockStore = m_stockListstore.get(iS);
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
				if(cANLStockStoreKData.date.compareTo(date) <= 0)
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
				Object eigenVal = entry.getValue().calc(cANLStockUser);
				//ANLLog.outputConsole("ANLEigen %s %.3f\n", entry.getKey(), entry.getValue().calc(cANLStockUser));
				cANLStockUser.eigenMap.put(eigenKey, eigenVal);
			}   
		}
	}
	
	private void callStockPoolUserSelect(String date, List<String> out_selectList)
	{
		ANLLog.outputLog("--> strategy_enter date(%s) stockCnt(%d)\n", date, m_cANLStockPool.stockList.size());
		
		// �ص����û�����cSelectResultWrapperList���������
		List<SelectResultWrapper> cSelectResultWrapperList = new ArrayList<SelectResultWrapper>();
		for(int iStockIndex = 0; iStockIndex < m_cANLStockPool.stockList.size(); iStockIndex++)
		{
			ANLStock cCurStock = m_cANLStockPool.stockList.get(iStockIndex);
			
			SelectResultWrapper cSRW = new SelectResultWrapper();
			cSRW.stockId = cCurStock.id;
			m_strategyObj.strategy_select(date, cCurStock, cSRW.selectRes);
			if(cSRW.selectRes.bSelect){
				cSelectResultWrapperList.add(cSRW);
			}
		} 
		Collections.sort(cSelectResultWrapperList, new SelectResultWrapper.SelectResultCompare());
		
		// ������SelectResultWrapperList��ӵ�selectlist��
		for(int iSRW = 0; iSRW < cSelectResultWrapperList.size(); iSRW++)
		{
			String stockId = cSelectResultWrapperList.get(iSRW).stockId;
			out_selectList.add(stockId);
		}
		
		ANLLog.outputLog("    # strategy_enter date(%s) select [ ", date);
		if(out_selectList.size() == 0) ANLLog.outputLog("null ");
		for(int j=0; j< out_selectList.size(); j++)// �����ɲ���Ʊ
		{
			String stockId = out_selectList.get(j);
			ANLLog.outputLog("%s ", stockId);
		}
		ANLLog.outputLog("]\n");
	}
	
	private void mockTransaction(String date, List<String> cSelectStockList)
	{
		// �˻���������
		int iMaxHoldCnt = 3; // ���ֹɸ���
		for(int iHold = 0; iHold < m_cUserAcc.stockList.size(); iHold++) // �����ֲ�Ʊ�����������ж�
		{
			ANLUserAcc.ANLUserAccStock cANLUserAccStock = m_cUserAcc.stockList.get(iHold);
			float cprice = m_cANLStockPool.getStock(cANLUserAccStock.id).GetLastPrice();
			if(cANLUserAccStock.holdDayCnt > 10) // ����һ��ʱ������
			{
				m_cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
			float shouyi = (cprice - cANLUserAccStock.buyPrices)/cANLUserAccStock.buyPrices;
			if(shouyi > 0.02 || shouyi < -0.02) // ֹӯֹ������
			{
				m_cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
		}
		int iNeedBuyCnt = iMaxHoldCnt - m_cUserAcc.stockList.size();
		for(int iBuy = 0; iBuy< iNeedBuyCnt; iBuy++) // ���г�Ʊ��������ʱ��������
		{
			float usedMoney = m_cUserAcc.money/(iNeedBuyCnt-iBuy);//�ó���Ӧ��λǮ
			for(int j=0; j< cSelectStockList.size(); j++)// �����ɲ���Ʊ
			{
				String stockId = cSelectStockList.get(j);
				if(m_cANLStockPool.getStock(stockId).GetLastDate().compareTo(date)!=0) //��Ʊ��������뵱ǰ������ڲ�ͬ ������һ��
				{
					continue;
				}
				boolean alreayHas = false;
				for(int k = 0; k < m_cUserAcc.stockList.size(); k++) // �����ֲ�Ʊ���ж��Ƿ��Ѿ�����
				{
					ANLUserAcc.ANLUserAccStock cANLUserAccStock = m_cUserAcc.stockList.get(k);
					if(stockId.compareTo(cANLUserAccStock.id) == 0)
					{
						alreayHas = true;
						break;
					}
				}
				if(!alreayHas)
				{
					String buy_id = stockId;
					float buy_price = m_cANLStockPool.getStock(buy_id).GetLastPrice();
					int buy_amount = (int)(usedMoney/buy_price)/100*100;
					m_cUserAcc.buyStock(buy_id, buy_price, buy_amount);
					break;
				}
			}
		}
	}
	
	// log�ļ���
	private String m_logfilename;
	// ������ʾͼ��
	private ANLImgShow m_cImageShow;
	List<CurvePoint> m_poiList_shangzheng; // ��֤���ߵ�
	List<CurvePoint> m_poiList_money; // �˻��ʽ����ߵ�
	// ��Ʊ�������б�
	private List<ANLStock> m_stockListstore;
	// ÿ��ص����û��Ĺ�Ʊ��
	private ANLStockPool m_cANLStockPool;
	// �����˻�����
	public ANLUserAcc m_cUserAcc;
	// �û���ӵ�������
	private Map<String, ANLEigen> m_eigenObjMap;
	// �û����õĲ��Զ���
	private ANLStrategy m_strategyObj;
}
