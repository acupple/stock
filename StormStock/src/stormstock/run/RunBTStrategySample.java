package stormstock.run;

import java.util.Arrays;
import java.util.List;

import stormstock.analysis.ANLBTEngine;
import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLLog;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockPool;
import stormstock.analysis.ANLStrategy;

public class RunBTStrategySample {
	// ����EISample1��
	public static class EigenSample1 extends ANLEigen {
		public float calc(ANLStock cANLStock)
		{
			// ������60�վ��ߵļ۸�ƫ��ٷֱ�
			float MA60 = cANLStock.GetMA(60, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA60)/MA60;
			return val;
		}
	}
	// ����EISample2��
	public static class EigenSample2 extends ANLEigen {
		public float calc(ANLStock cANLStock)
		{
			// ������250�վ��ߵļ۸�ƫ��ٷֱ�
			float MA250 = cANLStock.GetMA(250, cANLStock.GetLastDate());
			float curPrice = cANLStock.GetLastPrice();
			float val = (curPrice-MA250)/MA250;
			return val;
		}
	}

	// ����Sample��
	public static class StrategySample extends ANLStrategy {
		public boolean strategy_preload(ANLStock cANLStock)
		{
			if(cANLStock.id.compareTo("000001") == 0
				|| cANLStock.id.compareTo("600030") == 0)
			{	
				ANLLog.outputLog("add userpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
				return true;
			} else {
				return false;
			}
		}
		public void strategy_filter(ANLStock cANLStock)
		{
			
		}
	}
	
	public static void main(String[] args) {
		ANLLog.init("RunBTStrategySample.txt");
		ANLLog.outputConsole("RunBTStrategySample begin\n");
		
		ANLBTEngine cANLBTEngine = new ANLBTEngine();
		// �������
		cANLBTEngine.addEigen(new EigenSample1());
		cANLBTEngine.addEigen(new EigenSample2());
		// ���ò���
		cANLBTEngine.setStrategy(new StrategySample());
		// ���лز�
		cANLBTEngine.runBT("2016-01-01", "2016-01-05");
		
		ANLLog.outputConsole("RunBTStrategySample end\n");
	}
}
