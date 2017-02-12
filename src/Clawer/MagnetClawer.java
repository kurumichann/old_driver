package Clawer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;

public class MagnetClawer {

	public String website = null;
	public int MAXDEPTH = 40;
	public String title = null;
	public String magnet = null;
	Matcher match;
	String text;
	public Pattern get_a_patterna = Pattern.compile("<a.*?href=.*?>");
//[^0-9a-xA-X][0-9a-xA-X]{10,20}[\u4e00-\u9fa5]{0,10}[0-9a-xA-X]{10,20}[^0-9a-xA-X]
	public Pattern get_magnet_pattern = Pattern.compile("[^0-9a-zA-Z][0-9a-zA-Z]{16,25}[\u4e00-\u9fa5]{0,10}[0-9a-zA-Z]{16,25}[\r|\n|<|&]");
	public Pattern get_title_pattern = Pattern.compile("<title>(.+)</title>");
	public List<Map<String, String>> resourceList;
	public static HashSet<String> visitedList;
	public static HashSet<String> magnet_list;
	public int count = 0;
	public String content = "";
	public String get_response_html(String url) throws Exception{
			Connection connection  = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 ??+"
					+ "??(KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
			/*Response res = connection.execute();*/
			String html= connection.get().html();
			/*System.out.println(connection.get().html());*/
			return html;
	}
	
	public ArrayList<String> get_magnet(String text){
	    ArrayList<String> temp = new ArrayList<>();
	    LinkedList<String> magnets = get_group_list(text, get_magnet_pattern);
	   
	    for(String magnet : magnets){
	    	
	    	magnet = magnet.replace("[\\u4e00-\\u9fa5]*", "");
	     	int length_of_magnet = magnet.length();  
	    	magnet = magnet.substring(1, length_of_magnet-1);
	    	magnet.replaceAll(" ",  "");
	        if(magnet.length()==32||magnet.length()==40){
	        	magnet = "magnet:?xt=urn:btih:"+magnet;
	        	if(magnet_list.add(magnet)){
	        		temp.add(magnet);
	        	}
	        }
	        
	    }
	    return temp;
	}
	
	public String get_title(String text){
		match = get_title_pattern.matcher(text);
		match.matches();
		if(match.find()){
			title = match.group();
		}else{
			return null;
		}
		if(title.contains("wp/tag/")||title.contains("wp/page/")||title.contains("分享同人动漫的快乐")){
			return null;
		}
		return title.replace("<title>", "").replace("</title>", "");
		
	}
	
	public ArrayList<String> get_sub_links(String text, String url){
		int quote_start = 0;
		int quote_end = 0;
		String href = null;
		ArrayList<String> list = new ArrayList<>();
		for(String temp : get_group_list(text, get_a_patterna)){
			quote_start = temp.indexOf("href")+6;
			quote_end = quote_start; 
			while(temp.charAt(++quote_end) != '\"'){
				
			}
			href = temp.substring(quote_start, quote_end);
			if(check_a_label(href, url)){
				if(href.startsWith("/")){
				   href = url+href;
				}				
				if(visitedList.contains(href)){
					continue;
				}
				list.add(href);
			}
			
		}
		return list;
	}
	
	public boolean check_a_label(String label, String url){
		if(label.contains("#comment")||label.contains("?replytocom")){
			return false;
		}
		if(label.contains("www.hacg.li")||label.contains("www.hacg.fi")){
			return true;
		}
/*		if(label.startsWith("/")){
			return true;
		}*/
		return false;
	}
	
	public LinkedList<String> get_group_list(String text, Pattern pattern){
		Matcher match = pattern.matcher(text);
		LinkedList<String> result = new LinkedList<>();
		while(match.find()){
			result.add(match.group());
		}
		return result;
	}
	
	public void scan_url(String url, int depth, RWJson rw) {

		
		ArrayList<String> magnets;
		ArrayList<String> sub_links;
		try {
			text = get_response_html(url);
			sub_links = get_sub_links(text, url);
			title = get_title(text);
			//deal with next url while title or magnets are null
			if(title != null){			
				magnets = get_magnet(text);
				if (magnets.isEmpty()) {
					visitedList.add(url);
					return;
				}
				content += "\n{\n  \"title\" : \"" + title +"\",\n  \"magnet\" :  [";
				//store magnets in format
				if(magnets.size() > 1){
					for(int i = 0 ; i < magnets.size() ; i++){
						if(i == magnets.size() - 1){
							content += "\n              \""+magnets.get(i)+"\"\n           ]\n},";
							break;
						}
						content += "\n              \""+magnets.get(i)+"\",";
					}
				}else{
					content += "\n              \""+magnets.get(0)+"\"\n            ]\n},";
				}
				System.out.println("current depth: "+depth+"  current url:  "+url);
				System.out.println(title+"  "+magnets.toString());
				count++;
				System.out.println("Count: "+count);
			}
			visitedList.add(url);
		}catch(HttpStatusException e){
			e.printStackTrace();
			if(e.toString().contains("Status=4")){
				return;
			}
			scan_url(url, depth, rw);
			return;
		}catch (SocketTimeoutException e){
			System.out.println("read time out, reconnect...  url:  "+url);
			scan_url(url, depth, rw);
			return;
		}catch (Exception e) {
			e.printStackTrace();
			scan_url(url, depth, rw);
			return;
		}
		if(depth > MAXDEPTH){
			return;
		}
			
		for(String sub_url : sub_links){
			if (visitedList.contains(sub_url)) {
				continue;
			}
			scan_url(sub_url, depth+1, rw);

		}
		System.out.println("========IO=======");
		rw.WriteFile(RWJson.RESOURCEPATH, "[\n"+content.substring(0, content.length()-1)+"\n]");
		rw.SetRows(count); 
		rw.set_viewed_list(visitedList);

	}
	public void initialnization(RWJson rw){
		/*this.visitedList = rw.get_viewed_list();*/
		MagnetClawer.visitedList = new HashSet<>();
	}
	public static void  main(String args[]){
		
		System.setProperty("http.proxySet", "true"); 
		System.setProperty("http.proxyHost", "127.0.0.1"); 
		System.setProperty("http.proxyPort", "1080");
		
//		Scanner sc = new Scanner(System.in);
//		System.out.println("type the website we'll search");
//		String web = sc.next();
//		sc.close();
		String web = "http://www.hacg.fi/wp/25470.html";
		MagnetClawer clawer = new MagnetClawer();
		RWJson rw = new RWJson();
		magnet_list = new HashSet<>();
		clawer.initialnization(rw);
		clawer.scan_url(web, 0, rw);
		JSONArray arr = rw.incremental_add_arr();
		
		if( RWJson.INCREMENT_COUNT > 0){
			System.out.println("新增了"+RWJson.INCREMENT_COUNT);
			rw.write_json_arr(arr);
			rw.SetRows(arr.size());
			rw.incremental_add_list();
		}
		rw.set_incremental_list(RWJson.INCREMENTAL_LIST);
		rw.set_incremental_count(RWJson.INCREMENT_COUNT);
	}
	
}
