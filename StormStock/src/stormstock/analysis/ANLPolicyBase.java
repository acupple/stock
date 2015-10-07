package stormstock.analysis;

public abstract class ANLPolicyBase {
	public static class RetExitCheck
	{
		public RetExitCheck(int in_iExit, float in_profitPer)
		{
			iExit = in_iExit;
			profitPer = in_profitPer;
		}
		public int iExit;
		public float profitPer;
	}
	
	// ������
	public abstract boolean enterCheck(ANLStock cANLStock, int iCheckDay);
	
	// һ��Ҫ���˳��գ����Է���void
	public abstract RetExitCheck exitCheck(ANLStock cANLStock, int iEnter);
}
