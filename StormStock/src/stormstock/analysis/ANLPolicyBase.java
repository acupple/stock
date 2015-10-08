package stormstock.analysis;

public abstract class ANLPolicyBase {
	public static class RetExitCheck
	{
		public RetExitCheck(boolean in_hasEnoughDays, int in_iExit, float in_profitPer)
		{
			hasEnoughDays = in_hasEnoughDays;
			iExit = in_iExit; 
			profitPer = in_profitPer;
		}
		public RetExitCheck(RetExitCheck cRetExitCheck)
		{
			hasEnoughDays = cRetExitCheck.hasEnoughDays;
			iExit = cRetExitCheck.iExit; 
			profitPer = cRetExitCheck.profitPer;
		}
		public boolean hasEnoughDays;
		public int iExit;
		public float profitPer;
	}
	
	// ������
	public abstract boolean enterCheck(ANLStock cANLStock, int iCheckDay);
	
	// һ��Ҫ���˳��գ����Է���void
	public abstract RetExitCheck exitCheck(ANLStock cANLStock, int iEnter);
}
