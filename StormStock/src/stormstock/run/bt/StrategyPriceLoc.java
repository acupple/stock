package stormstock.run.bt;

import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLStock;

/*
 * �۸�Ĵ�ֱλ�ñ���
 */
public class StrategyPriceLoc {
	
	/*
	 * �����ڴ�ֱλ�ñ�����4������
	 */
	public static class StrategyPriceLocLongLong extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(250*4, date);
			float highPrice = cANLStock.GetHigh(250*4, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * ���ڴ�ֱλ�ñ�����1������
	 */
	public static class StrategyPriceLocLong extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(250, date);
			float highPrice = cANLStock.GetHigh(250, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * ���ڴ�ֱλ�ñ�����60������
	 */
	public static class StrategyPriceLocMid extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(60, date);
			float highPrice = cANLStock.GetHigh(60, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
	
	/*
	 * ���ڴ�ֱλ�ñ�����20������
	 */
	public static class StrategyPriceLocShort extends ANLEigen {
		@Override
		public Object calc(ANLStock cANLStock) {
			if(cANLStock.historyData.size() == 0) 
				return 0.0f;
			String date = cANLStock.GetLastDate();
			float lowPrice = cANLStock.GetLow(20, date);
			float highPrice = cANLStock.GetHigh(20, date);
			float curPrice = cANLStock.GetLastPrice();
			return (curPrice-lowPrice)/(highPrice-lowPrice);
		}
	}
}
