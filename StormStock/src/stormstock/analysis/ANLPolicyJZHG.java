package stormstock.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserStockPool;
import stormstock.analysis.ANLPolicyAve.BounceData;

// ��ֵ�ع�
public class ANLPolicyJZHG extends ANLPolicy {
	// �����Ʊ����ϵ����
	static class ANLPolicyStockCK implements Comparable
	{
		public String stockID;
		float c1; // ��ֵ�ع��ֵ   ϵ��(��ǰ��-�������)+(��ǰ��-�������)+(��ǰ��-15�վ�)
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			ANLPolicyStockCK ck = (ANLPolicyStockCK)arg0;
			if(this.c1 <= ck.c1)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
	}
	
	// init ��ʼ������
	public void init()
	{
		String logstr = String.format("init\n");
		outputLog(logstr);
	}

	// ���й�Ʊ�ص�ɸѡ������ϵͳ�Զ��ص�������true�ı����뿼���Ʊ��
	public boolean stock_filter(ANLStock cANLStock)
	{
		// ��ֵ������˵�
		if(cANLStock.curBaseInfo.allMarketValue > 200.0f 
				|| cANLStock.curBaseInfo.circulatedMarketValue > 100.0f)
		{
			return false;
		}
		// ������ʷ����ʱ��϶̵Ĺ��˵�
		if(cANLStock.historyData.size() < 250*2)
		{
			return false;
		}
		// PE��ӯ�ʹ���Ĺ��˵�
		if(cANLStock.curBaseInfo.peRatio > 100.0f 
				|| cANLStock.curBaseInfo.peRatio == 0)
		{
			return false;
		}
		// ���ֹ���
		if(cANLStock.curBaseInfo.name.contains("S")
				|| cANLStock.curBaseInfo.name.contains("*")
				|| cANLStock.curBaseInfo.name.contains("N"))
		{
			return false;
		}
		
//		if(cANLStock.id.compareTo("300312") == 0 )
//		{
//			return true;
//		}
//		else{
//			return false;
//		}
		
		String logstr = String.format("add userpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
		outputLog(logstr);
		return true;
	}
	
	public void check_today(String date, ANLUserStockPool spool)
	{
		String logstr = String.format("check_today %s --->>>\n", date);
		outputLog(logstr);
		
		//���������ֵ��
		List<ANLPolicyStockCK> stockCKList = new ArrayList<ANLPolicyStockCK>();
		// �������й�Ʊ����ֵ�������
		for(int i = 0; i < spool.stockList.size(); i++)
		{
			ANLStock cANLStock = spool.stockList.get(i);
			ANLPolicyStockCK cANLPolicyStockCK = new ANLPolicyStockCK();
			cANLPolicyStockCK.stockID = cANLStock.id;
			
			// ���ڼ۸��ֵ��Ϊ��׼ֵ
			float long_base_price = cANLStock.GetMA(500, date);
			// ��ǰ�۸����
			float cur_price = cANLStock.historyData.get(cANLStock.historyData.size()-1).close;
			float cur_pricePa = (cur_price - long_base_price)/long_base_price;
			
			// �������ƫ���
			float short_low_price = cANLStock.GetLow(20, date);
			float short_low_pricePa = (short_low_price - long_base_price)/long_base_price;
			float short_high_price = cANLStock.GetHigh(20, date);
			float short_high_pricePa = (short_high_price - long_base_price)/long_base_price;
			float short_pianlirate = 0.0f;
			if(short_high_pricePa-short_low_pricePa != 0)
				short_pianlirate = (cur_pricePa-short_low_pricePa)/(short_high_pricePa-short_low_pricePa);

			// ��������ƫ���
			float mid_low_price = cANLStock.GetLow(60, date);
			float mid_low_pricePa = (mid_low_price - long_base_price)/long_base_price;
			float mid_high_price = cANLStock.GetHigh(60, date);
			float mid_high_pricePa = (mid_high_price - long_base_price)/long_base_price;
			float mid_pianlirate = 0.0f;
			if(mid_high_pricePa-mid_low_pricePa != 0)
				mid_pianlirate = (cur_pricePa-mid_low_pricePa)/(mid_high_pricePa-mid_low_pricePa);

			// ���㳤��ƫ���
			float long_low_price = cANLStock.GetLow(250, date);
			float long_low_pricePa = (long_low_price - long_base_price)/long_base_price;
			float long_high_price = cANLStock.GetHigh(250, date);
			float long_high_pricePa = (long_high_price - long_base_price)/long_base_price;
			float long_pianlirate = 0.0f;
			if(long_high_pricePa-long_low_pricePa != 0)
				long_pianlirate = (cur_pricePa-long_low_pricePa)/(long_high_pricePa-long_low_pricePa);
			
			logstr = String.format("    %s test[ %.3f %.3f %.3f]\n", cANLPolicyStockCK.stockID, 
					short_pianlirate,mid_pianlirate,long_pianlirate);
			outputLog(logstr);
			
			// ���ھ�ֵ����
			cANLPolicyStockCK.c1 = short_pianlirate*(5/10.0f) + mid_pianlirate*(3/10.0f) + long_pianlirate*(2/10.0f);
			
			stockCKList.add(cANLPolicyStockCK);

		}
		//��ֵ������
		Collections.sort(stockCKList);
		
		for(int i = 0; i < stockCKList.size(); i++)
		{
			ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(i);
			logstr = String.format("    %s c1[ %.3f ]\n", cANLPolicyStockCK.stockID, cANLPolicyStockCK.c1);
			outputLog(logstr);
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.rmlog();
		cANLPolicyJZHG.run("2016-09-19", "2016-09-19");
		List<ANLStock> cStockObjList = new ArrayList<ANLStock>();
		fmt.format("main end\n");
	}
}
