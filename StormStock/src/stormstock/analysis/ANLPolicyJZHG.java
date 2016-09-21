package stormstock.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserAcc.ANLUserAccStock;
import stormstock.analysis.ANLPolicy.ANLUserStockPool;
import stormstock.analysis.ANLPolicyAve.BounceData;
import stormstock.analysis.ANLPolicyCD.XiaCuoRange;

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
		cUserAcc.init(100000.0f);
		outputLog(logstr);
	}

	// ���й�Ʊ�ص�ɸѡ������ϵͳ�Զ��ص�������true�ı����뿼���Ʊ��
	public boolean stock_filter(ANLStock cANLStock)
	{
		// for test -------------------------------------------------------------
		boolean bEnableTest = false;
		if(bEnableTest)
		{
			List<String> testStockList = Arrays.asList(
					"600742", 
					"600790"
					);
			for(int i=0; i< testStockList.size();i++)
			{
				if(cANLStock.id.compareTo(testStockList.get(i)) == 0 )
				{
					return true;
				}
			}
			return false;
		}
		// -------------------------------------------------------------
		
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
		
		String logstr = String.format("add userpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
		outputLog(logstr);
		return true;
	}
	
	// ����i��j�յ���߼۸������
	public int indexHigh(List<ANLStockDayKData> dayklist, int i, int j)
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
	public int indexLow(List<ANLStockDayKData> dayklist, int i, int j)
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
	
	// ȷ��i���Ƿ����´����ȵ�
	public class XiaCuoRange
	{
		public int iBeginIndex;
		public int iHighIndex;
		public int iLowIndex;
		public int iEndEndex;
		public float highPrice;
		public float lowPrice;
		public float maxZhenFu()
		{
			return (lowPrice-highPrice)/highPrice;
		}
		public float xiaCuoXieLv()
		{
			return maxZhenFu()/(iLowIndex-iHighIndex);
		}
	}
	public XiaCuoRange CheckXiaCuoRange(List<ANLStockDayKData> dayklist, int i)
	{
		String logstr;
		int iCheckE = i;
		// ��鵱ǰ�죬ǰ6��20���������㼱����������
		for(int iCheckB = i-6; iCheckB>=i-20; iCheckB--)
		{
			if(iCheckB >= 0)
			{
				// @ ��ߵ�����͵�������λ�õ��ж�
				boolean bCheckHighLowIndex = false;
				int indexHigh = indexHigh(dayklist, iCheckB, iCheckE);
				int indexLow = indexLow(dayklist, iCheckB, iCheckE);
				if(indexHigh>iCheckB && indexHigh<=(iCheckB+iCheckE)/2
						&& indexLow > (iCheckB+iCheckE)/2 && indexLow < iCheckE)
				{
					bCheckHighLowIndex = true;
				}
				
				// @ ��ߵ�����͵��´�����ж�
				boolean bCheckXiaCuo = false;
				float highPrice = dayklist.get(indexHigh).high;
				float lowPrice = dayklist.get(indexLow).low;
				float xiaCuoZhenFu = (lowPrice-highPrice)/highPrice;
				float xiaCuoMinCheck = -1.5f*0.01f*(indexLow-indexHigh);
				if(xiaCuoMinCheck > -0.06f) xiaCuoMinCheck = -0.06f;
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
					if(dayklist.get(c).low < midPrice)
					{
						bCheck3 = false;
					}
				}
				for(int c= iCheckE; c>iCheckE - cntDay/3; c--)
				{
					if(dayklist.get(c).low > midPrice)
					{
						bCheck3 = false;
					}
				}
				
				if(bCheckHighLowIndex && 
						bCheckXiaCuo && 
						bCheck3)
				{
//						logstr = String.format("    Test findLatestXiaCuoRange [%s,%s] ZhenFu(%.3f,%.3f)\n",
//								dayklist.get(iCheckB).date,
//								dayklist.get(iCheckE).date,
//								xiaCuoZhenFu,xiaCuoMinCheck);
//						outputLog(logstr);
					
					XiaCuoRange retXiaCuoRange = new XiaCuoRange();
					retXiaCuoRange.iBeginIndex = iCheckB;
					retXiaCuoRange.iEndEndex = iCheckE;
					retXiaCuoRange.iHighIndex = indexHigh;
					retXiaCuoRange.iLowIndex = indexLow;
					retXiaCuoRange.highPrice = highPrice;
					retXiaCuoRange.lowPrice = lowPrice;
					return retXiaCuoRange;
				}
			}
		}
		return null;
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
			if(cANLStock.historyData.size() == 0) continue;
			
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
			// ���ھ�ֵ����
			cANLPolicyStockCK.c1 = short_pianlirate*(2/10.0f) 
					+ mid_pianlirate*(3/10.0f) 
					+ long_pianlirate*(5/10.0f);
			
			XiaCuoRange cXiaCuoRange = CheckXiaCuoRange(cANLStock.historyData, cANLStock.historyData.size()-1);
			if(null != cXiaCuoRange && cXiaCuoRange.maxZhenFu() > -0.5f)
			{
				logstr = String.format("%s %s ", date, cANLPolicyStockCK.stockID);
				logstr = logstr + String.format("P[ %.3f %.3f %.3f] ",
						short_pianlirate,mid_pianlirate,long_pianlirate);
				logstr = logstr + String.format("X[ %.3f %.3f %.3f %.3f]\n",
						cXiaCuoRange.highPrice, cXiaCuoRange.lowPrice, cXiaCuoRange.maxZhenFu(), cXiaCuoRange.xiaCuoXieLv());
				//outputLog(logstr);
				
				stockCKList.add(cANLPolicyStockCK);
				Collections.sort(stockCKList); //��ֵ������
			}
		}
		
		
		if(stockCKList.size() != 0) 
		{
			for(int i = 0; i < stockCKList.size(); i++)
			{
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(i);
//				logstr = String.format("    %s c1[ %.3f ]\n", cANLPolicyStockCK.stockID, cANLPolicyStockCK.c1);
//				outputLog(logstr);
			}
		}
			
		// �û����� ---------------------------------------------------
		for(int i = 0; i < cUserAcc.stockList.size(); i++)
		{
			ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(i);
			if(cANLUserAccStock.holdDayCnt > 3)
			{
				float cprice = spool.getStock(cANLUserAccStock.id).GetCurPrice();
				cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
		}
		if(cUserAcc.stockList.size() == 0)
		{
			if(stockCKList.size() != 0) // ���ڲ���Ʊ
			{
				int indexBuy = 5;
				if(stockCKList.size() < 6) indexBuy = stockCKList.size()-1;
				
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(indexBuy);
				String buy_id = cANLPolicyStockCK.stockID;
				float buy_price = spool.getStock(buy_id).GetCurPrice();
				int buy_amount = (int)(cUserAcc.money/buy_price)/100*100;
				cUserAcc.buyStock(buy_id, buy_price, buy_amount);
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		fmt.format("main begin\n");
		ANLPolicyJZHG cANLPolicyJZHG = new ANLPolicyJZHG();
		cANLPolicyJZHG.rmlog();
		cANLPolicyJZHG.run("2011-01-01", "2011-12-31");
		fmt.format("main end\n");
	}
}
