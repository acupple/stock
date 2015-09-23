package stormstock.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

public class TestDefault {
	/** 
     * ������Url�������ļ� 
     * @param urlStr 
     * @param fileName 
     * @param savePath 
     * @throws IOException 
     */  
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //���ó�ʱ��Ϊ3��  
        conn.setConnectTimeout(3*1000);  
        //��ֹ���γ���ץȡ������403����  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //�õ�������  
        InputStream inputStream = conn.getInputStream();    
        //��ȡ�Լ�����  
        byte[] getData = readInputStream(inputStream);      
  
        //�ļ�����λ��  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            saveDir.mkdir();  
        }  
        File file = new File(saveDir+File.separator+fileName);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        if(inputStream!=null){  
            inputStream.close();  
        }  
  
  
        System.out.println("info:"+url+" download success");   
  
    }  
    /** 
     * ���������л�ȡ�ֽ����� 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
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
    
    
	public static void main(String[] args) {
		try{  
            downLoadFromUrl("http://market.finance.sina.com.cn/downxls.php?date=2015-02-16&symbol=sz300163",  
                    "ddd.txt","d:/");  
            
            
            URL url = new URL("http://vip.stock.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/300163.phtml");  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            InputStream inputStream = conn.getInputStream();   //ͨ�������������վ����  
            byte[] getData = readInputStream(inputStream);     //�����վ�Ķ���������  
            String data = new String(getData, "gb2312");  
            System.out.println(data);  
            
        }catch (Exception e) {  
            // TODO: handle exception  
        }  
	}
}
