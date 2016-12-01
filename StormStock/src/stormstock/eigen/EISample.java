package stormstock.eigen;

import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLStock;

public class EISample extends ANLEigen {
	// ������60�վ��ߵļ۸�ƫ��ٷֱ�
	public float calc(ANLStock cANLStock)
	{
		float MA60 = cANLStock.GetMA(60, cANLStock.GetLastDate());
		float curPrice = cANLStock.GetLastPrice();
		float val = (curPrice-MA60)/MA60;
		return val;
	}
}
