#include "Common.h"

#include "windows.h"
#include "commctrl.h"

int THSAPI_TongHuaShunInit();

// ��ȡ�˻����ý��
float THSAPI_GetAvailableMoney();
// ��ȡ�˻����ʲ�
float THSAPI_GetAllMoney();
// ��ȡ�˻���Ʊ����ֵ
float THSAPI_GetAllStockMarketValue();

// �����Ʊ�ӿ�
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);
// ������Ʊ�ӿ�
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);


