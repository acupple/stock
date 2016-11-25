package stormstock.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import stormstock.analysis.ANLImgShow.CurvePoint;

public class ANLStrategy {
	// ���й�Ʊ�ص�ɸѡ����������ϵͳ�ص�������true�ı����뿼���Ʊ��
	public boolean strategy_pre_filter(ANLStock cANLStock) { return false;}
	// ÿ���û���Ʊ�ػص�������ϵͳ�Զ��ص���ȷ����������
	public void strategy_today(String date, ANLStockPool spool) {}
	
	public ANLStrategy()
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		String logfilename = this.getClass().getSimpleName() + ".txt";
		ANLLog.init(logfilename);
		String imgfilename = this.getClass().getSimpleName() + ".jpg";
		cImageShow = new ANLImgShow(1600,900,imgfilename);
		// create inner object
		stockListstore = new ArrayList<ANLStock>();
		cANLStockPool = new ANLStockPool();
		cUserAcc = new ANLUserAcc(cANLStockPool);
	}
	void run()
	{
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		ANLStockDayKData cANLDayKDataBegin = cANLStock.historyData.get(0);  
		ANLStockDayKData cANLDayKDataEnd = cANLStock.historyData.get(cANLStock.historyData.size()-1);  
		run(cANLDayKDataBegin.date, cANLDayKDataEnd.date);
	}
	protected void run(String beginDate, String endDate) {
		// �˻������ʼ��
		cUserAcc.init(100000.0f);
		// �������й�Ʊ�����û�ɸѡ���û���Ʊ��
		// ANLLog.outputConsole("loading user stock pool ...\n");
		List<String> cStockList = ANLDataProvider.getAllStocks();
		for(int i=0; i<cStockList.size();i++)
		{
			String stockId = cStockList.get(i);
			ANLStock cANLStock = ANLDataProvider.getANLStock(stockId);
			if(null!= cANLStock && strategy_pre_filter(cANLStock))
			{
				stockListstore.add(cANLStock);
				// ANLLog.outputConsole("stockListstore id:%s \n", cANLStock.id);
			}
		}
		ANLLog.outputConsole("load success, stock count : %d\n", stockListstore.size());
		
		List<CurvePoint> PoiList_shangzhen = new ArrayList<CurvePoint>();
		List<CurvePoint> PoiList_money = new ArrayList<CurvePoint>();
		
		// ����ָ֤����ȷ�ϻص�����
		ANLStock cANLStock = ANLDataProvider.getANLStock("999999");
		int iB = ANLUtils.indexDayKAfterDate(cANLStock.historyData, beginDate);
		int iE = ANLUtils.indexDayKBeforeDate(cANLStock.historyData, endDate);
		for(int i = iB; i <= iE; i++)  
        {  
			ANLStockDayKData cANLDayKData = cANLStock.historyData.get(i);  
			PoiList_shangzhen.add(new CurvePoint(i,cANLDayKData.close));
            // ANLLog.outputConsole("%s data generate\n", cANLDayKData.date);
			// �Ӵ洢��Ʊ�б�����ȡ��Ӧ���������ݵ��û���Ʊ���У��ص����û�����
			for(int iS=0;iS<stockListstore.size();iS++)
			{
				ANLStock cANLStockStore = stockListstore.get(iS);
				// fmt.format("   Stock %s generate\n", cANLStockStore.id);
				// ��ȡ�û����е���Ӧ��Ʊ����
				ANLStock cANLStockUser = null;
				for(int iUS=0;iUS<cANLStockPool.stockList.size();iUS++)
				{
					ANLStock cANLStockUserFind = cANLStockPool.stockList.get(iUS);
					if(cANLStockUserFind.id.compareTo(cANLStockStore.id) == 0)
					{
						cANLStockUser = cANLStockUserFind;
					}
				}
				if(null == cANLStockUser)
				{
					cANLStockUser = new  ANLStock(cANLStockStore.id, cANLStockStore.curBaseInfo);
					cANLStockPool.stockList.add(cANLStockUser);
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
			}
			
			// �ص����û�
			cUserAcc.update(cANLDayKData.date);
			strategy_today(cANLDayKData.date, cANLStockPool);
            
            PoiList_money.add(new CurvePoint(i,cUserAcc.GetTotalAssets()));
        } 
		
		cImageShow.writeLogicCurve(PoiList_shangzhen, 1);
		cImageShow.writeLogicCurve(PoiList_money, 2);
		cImageShow.GenerateImage();
	}
	
	private List<ANLStock> stockListstore;
	public ANLUserAcc cUserAcc;
	private ANLStockPool cANLStockPool;
	private ANLImgShow cImageShow;
}
