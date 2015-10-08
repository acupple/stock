package stormstock.data;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.HtmlPage;

import stormstock.data.WebStockDayK.DayKData;

public class WebStockDividendPayout {
	public static class DividendPayout implements Comparable
	{
		public String date;
		public float songGu;
		public float zhuanGu;
		public float paiXi;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DividendPayout sdto = (DividendPayout)o;
		    return this.date.compareTo(sdto.date);
		}
	}
	public static int getDividendPayout(String id, List<DividendPayout> out_list)
	{
		// e.g http://vip.stock.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/300163.phtml
		String urlStr = "http://vip.stock.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/";
		
		if(out_list.size() > 0) return -20;
		if(id.contains("999999")) return 0; // 上证指数
		
		try{  
			urlStr = urlStr + id + ".phtml";
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	        //设置超时间为3秒  
	        conn.setConnectTimeout(3*1000);  
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);
//			FileWriter fw = null; 
//			fw = new FileWriter("D:/test.txt");
//			fw.write(data);
//			fw.close();
	        
//			Parser myParser;     
//	        NodeList nodeList = null;     
//	        myParser =Parser.createParser(data, "utf-8");     
//	        NodeFilter tableFilter = new NodeClassFilter(TableTag.class);     
//	        OrFilter lastFilter = new OrFilter();     
//	        lastFilter.setPredicates(new NodeFilter[] { tableFilter });     
//	        nodeList =myParser.parse(lastFilter);
//	        System.out.println(nodeList.size());
//	        for (int i = 0; i <=nodeList.size(); i++) {
//	        	if (nodeList.elementAt(i) instanceof TableTag) {     
//	        		  
//                    TableTag tag = (TableTag)nodeList.elementAt(i);     
//                     
//                    Node cNodeFenHong = nodeList.elementAt(0);
//                    System.out.println(cNodeFenHong.getText());
////                     System.out.println(tag.getChildrenHTML());  
//                     System.out.println("-----------------------------------------------------");  
//	        	}
//	        }
			
	        Parser parser = Parser.createParser(data, "utf-8");
            TagNameFilter filter1 = new TagNameFilter("table");  
            NodeList tablelist = parser.parse(filter1); 
            Node cNodeFenHong = null;
            Node cNodePeiGu = null;
            //System.out.println(tablelist.size());
            for (int i = 0; i < tablelist.size(); i++) {  
                    Node cTmpNode = tablelist.elementAt(i);
                    if(cTmpNode.getText().contains("sharebonus_1"))
                    {
                    	cNodeFenHong = cTmpNode;
                    }
                    if(cTmpNode.getText().contains("sharebonus_2"))
                    {
                    	cNodePeiGu = cTmpNode;
                    }
	        }
            //System.out.println(cNodeFenHong.toHtml());
            //System.out.println(cNodePeiGu.toHtml());
            {
            	Parser parser2 = Parser.createParser(cNodeFenHong.toHtml(), "utf-8");
                TagNameFilter filter2 = new TagNameFilter("tr");
                NodeList tablelist2 = parser2.parse(filter2); 
                for (int i = 0; i < tablelist2.size(); i++) { 
                	if(i<3) continue;
                	DividendPayout cDividendPayout = new DividendPayout();
                	
                	Node cTmpNode = tablelist2.elementAt(i);
                	//System.out.println(cTmpNode.toHtml());
                	
                	Parser parser3 = Parser.createParser(cTmpNode.toHtml(), "utf-8");
                    TagNameFilter filter3 = new TagNameFilter("td");
                    NodeList tablelist3 = parser3.parse(filter3); 
                    for (int j = 0; j < tablelist3.size(); j++) {
                    	Node cTmpNodecol = tablelist3.elementAt(j);
                    	String tmpStr = cTmpNodecol.toPlainTextString();
                    	//System.out.println(tmpStr);
                    	if(5 == j)
                    		cDividendPayout.date = tmpStr;
                    	if(1 == j)
                    		cDividendPayout.songGu = Float.parseFloat(tmpStr);
                    	if(2 == j)
                    		cDividendPayout.zhuanGu = Float.parseFloat(tmpStr);
                    	if(3 == j)
                    		cDividendPayout.paiXi = Float.parseFloat(tmpStr);
                    }
                    if(cDividendPayout.date.replace("-", "").trim().length()==0)
                    {
                    	continue;
                    }
                    out_list.add(cDividendPayout);
                    //System.out.println("--------------------------------");
                }
            }
            {
            	Parser parser2 = Parser.createParser(cNodePeiGu.toHtml(), "utf-8");
                TagNameFilter filter2 = new TagNameFilter("tr");
                NodeList tablelist2 = parser2.parse(filter2); 
                for (int i = 0; i < tablelist2.size(); i++) { 
                	if(i<2) continue;
                	DividendPayout cDividendPayout = new DividendPayout();
                	
                	Node cTmpNode = tablelist2.elementAt(i);
                	//System.out.println(cTmpNode.toHtml());
                	
                	Parser parser3 = Parser.createParser(cTmpNode.toHtml(), "utf-8");
                    TagNameFilter filter3 = new TagNameFilter("td");
                    NodeList tablelist3 = parser3.parse(filter3); 
                    if(tablelist3.size()>1) 
                    {
                    	//System.out.println("WebStockDividendPayout PeiGu");
                    }
                    for (int j = 0; j < tablelist3.size(); j++) {
                    	Node cTmpNodecol = tablelist3.elementAt(j);
                    	String tmpStr = cTmpNodecol.toPlainTextString();
                    	//System.out.println(tmpStr);
                    }
                    //System.out.println("--------------------------------");
                }
            }
              
            if(out_list.size() <= 0) return -30;
            
        }catch (Exception e) {  
        	System.out.println("Exception[WebStockDividendPayout]:" + e.getMessage()); 
            // TODO: handle exception  
        	return -1;
        }  
		
		Collections.sort(out_list);
		return 0;
	}
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
		List<DividendPayout> retList = new ArrayList<DividendPayout>();
		int ret = getDividendPayout("600060", retList);
		if(0 == ret)
		{
			for(int i = 0; i < retList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = retList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + ret);
		}
	}
}
