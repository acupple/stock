package stormstock.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;

import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList; 

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stormstock.data.DataWebStockAllList.StockItem;
import stormstock.data.DataWebStockDayK.DayKData;
import stormstock.data.DataWebStockDividendPayout.DividendPayout;

public class DataWebStockAllList {
	
	public static Random random = new Random();
	
	public static class StockItem
	{
		public StockItem(){}
		
		public StockItem(String in_id)
		{
			id = in_id;
		}
		public StockItem(String in_id, String in_name)
		{
			id = in_id;
			name = in_name;
		}
		public StockItem(StockItem cStockItem)
		{
			name = cStockItem.name;
			id = cStockItem.id;
		}
		public String name;
		public String id;
	}
	public static int getAllStockList(List<StockItem> out_list)
	{
		if(out_list.size() > 0) return -20;
		
		try{  
			String allStockListUrl = "http://quote.eastmoney.com/stocklist.html";
//            URL url = new URL(allStockListUrl);  
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
//            InputStream inputStream = conn.getInputStream(); 
//            byte[] getData = readInputStream(inputStream); 
//            String data = new String(getData, "gbk");  
//            System.out.println(data);
//            FileWriter fw = null; 
//            fw = new FileWriter("D:/add2.txt");
//            fw.write(data);
//            fw.close();
            
			URL url = new URL(allStockListUrl);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	        //设置超时间为3秒  
	        conn.setConnectTimeout(3*1000);  
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	        
            Parser parser = new Parser(conn); 
            parser.setEncoding("gbk");
            TagNameFilter filter1 = new TagNameFilter("DIV");  
            NodeList list1 = parser.parse(filter1);  
            
            // find qox node
            Node cQoxNode = null;
            if(list1!=null){  
                //System.out.println("list.size()==="+list.size());  
                for(int i=0; i<list1.size(); i++)  
                {
                	Node cNode = list1.elementAt(i);
                	if(cNode.getText().contains("class=\"qox\""))
                	{
                		cQoxNode = cNode;
                		break;
                	}
                }
            }  
            
			 TagNameFilter filter2=new TagNameFilter ("li");  
	         Parser p = Parser.createParser(cQoxNode.toHtml(), "gbk");
			 NodeList list2=p.extractAllNodesThatMatch(filter2);
 					
			 int allCount=0;
             for(int i=0;i<list2.size();i++)  
             {
             	Node cNode = list2.elementAt(i);
             	String tmpStr = cNode.toPlainTextString();
             	int il = tmpStr.indexOf("(");
             	int ir = tmpStr.indexOf(")");
             	String name = tmpStr.substring(0, il);
             	String id = tmpStr.substring(il+1,ir);
             	if(id.startsWith("60") || id.startsWith("00") || id.startsWith("30"))
             	{
             		if(id.length() == 6)
             		{
                 		// System.out.println(name + "," + id);
                 		allCount++;
                 		StockItem cStockItem = new StockItem();
                 		cStockItem.name = name;
                 		cStockItem.id = id;
                 		out_list.add(cStockItem);
             		}
             	}
             }
             // System.out.println(allCount); 
             
             if(out_list.size() <= 0) return -30;

        }catch (Exception e) {  
        	System.out.println("Exception[WebStockAllList]:" + e.getMessage()); 
            // TODO: handle exception  
        	return -1;
        }  
		return 0;
	}

	public static List<StockItem> getRandomStock(int count)
	{
		List<StockItem> retList = new ArrayList<StockItem>();
		if(0 != count)
		{
			List<StockItem> retListAll = new ArrayList<StockItem>();
			int ret = DataWebStockAllList.getAllStockList(retListAll);
			if(0 == ret)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockItem cStockItem = popRandomStock(retListAll);
					retList.add(cStockItem);
		        } 
			}
			else
			{
			}
		}
		return retList;
	}
	
	private static StockItem popRandomStock(List<StockItem> in_list)
	{
		if(in_list.size() == 0) return null;
		
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockItem cStockItem = new  StockItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockItem;
	}
	
//	private static String ENCODE = "GBK";
//    private static void message( String szMsg ) {
//        try{ 
//        	System.out.println(new String(szMsg.getBytes(ENCODE), System.getProperty("file.encoding"))); 
//        	Write(new String(szMsg.getBytes(ENCODE), System.getProperty("file.encoding")));
//        	Write("\n");
//        	}    
//        catch(Exception e )
//        {}
//    }
//    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
//        byte[] buffer = new byte[1024];    
//        int len = 0;    
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
//        while((len = inputStream.read(buffer)) != -1) {    
//            bos.write(buffer, 0, len);    
//        }    
//        bos.close();    
//        return bos.toByteArray();    
//    }    
//    public static void Write(String data) throws IOException
//    {
//        FileWriter fw = null; 
//        fw = new FileWriter("D:/add2xxx.txt", true);
//        fw.write(data);
//        fw.close();
//    }

}
