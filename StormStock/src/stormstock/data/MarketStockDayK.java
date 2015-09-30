package stormstock.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MarketStockDayK {
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }  
	public static void main(String[] args){
		try
		{
			String urlStr = "http://table.finance.yahoo.com/table.csv?s=000001.sz";
			urlStr = "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz300163&begin_date=20080101&end_date=20151010";
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	                //���ó�ʱ��Ϊ3��  
	        conn.setConnectTimeout(20*1000);  
	        //��ֹ���γ���ץȡ������403����  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	  
	        //�õ�������  
	        InputStream inputStream = conn.getInputStream();    
	        //��ȡ�Լ�����  
	        byte[] getData = readInputStream(inputStream);    
	        String data = new String(getData, "gbk");  
	        System.out.println(data.toString()); 
	        //�ļ�����λ��  
	        File file = new File("D:/test.txt");      
	        FileOutputStream fos = new FileOutputStream(file);       
	        fos.write(getData);   
	        if(fos!=null){  
	            fos.close();    
	        }  
	        if(inputStream!=null){  
	            inputStream.close();  
	        }  
	        System.out.println("info:"+urlStr+" download success"); 
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
		}
	}
}
