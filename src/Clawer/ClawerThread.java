package Clawer;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

public class ClawerThread implements Runnable{

	String text;
	private final String THREADNAME;
	private final int MAXDEPTH = 100;
	private final String URL;
	private final Pattern TAG_A_PATTERN = Pattern.compile("<a.*?href=.*?>");
	private final Pattern MAGNET_PATTERN = Pattern.compile("[^0-9a-zA-Z][0-9a-zA-Z]{16,25}[\u4e00-\u9fa5]{0,10}[0-9a-zA-Z]{16,25}[\r|\n|<|&]");
	private final Pattern TAG_TITLE_PATTERN = Pattern.compile("<title>(.+)</title>");
	private final Pattern ISSUE_TIME_PATTERN = Pattern.compile("(?<=<time class.{0,70}>).*(?=</time>)");
	private static final int RETRY = 5;
	public static Set<String> visitedList = Collections.synchronizedSet(new HashSet<String>());
	public static Set<String> magnet_list = Collections.synchronizedSet(new HashSet<String>());
	public static BlockingQueue<String> content_queue = new LinkedBlockingQueue<>();
	
	public ClawerThread(String thread_name, String url){
		this.THREADNAME = thread_name;
		this.URL = url;
	}


	public String get_response_html(String url) throws Exception{
			Connection connection  = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 ??+"
					+ "??(KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
			String html= connection.get().html();
			return html;
	}
	public  String getIssueTime(String text){
		Matcher matcher = ISSUE_TIME_PATTERN.matcher(text);
		matcher.matches();
		if(matcher.find()){
			return matcher.group();
		}
		return "";
	}
	public ArrayList<String> get_magnet(String text){
	    ArrayList<String> temp = new ArrayList<>();
	    LinkedList<String> magnets = get_group_list(text, MAGNET_PATTERN);
	   
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
		Matcher match = TAG_TITLE_PATTERN.matcher(text);
		match.matches();
		String title;
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
		for(String temp : get_group_list(text, TAG_A_PATTERN)){
			quote_start = temp.indexOf("href")+6;
			quote_end = quote_start; 
			while(quote_end<temp.length()){
				try{
				quote_end++;
				if(temp.charAt(quote_end) == '\"'){
					break;
				}
				}catch(StringIndexOutOfBoundsException e){
					e.printStackTrace();
					System.out.println(temp+"\n"+url);
				}
			}
			if(temp.charAt(quote_end) != '\"'){
				continue;
			}
			href = temp.substring(quote_start, quote_end);
			if(check_a_label(href) && !visitedList.contains(href)){
				list.add(href);
			}
			
		}
		return list;
	}
	
	public boolean check_a_label(String label){
		if(label.contains("#comment")||label.contains("?replytocom")){
			return false;
		}
		if(label.contains("www.hacg.li")||label.contains("www.hacg.fi")||label.contains("www.hacg.wiki")||label.contains("www.llss.me")||label.contains("www.hacg.love")){
			return true;
		}
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
	
	public void scan_url(String url, int depth,int retry_counts) {
		
		if(visitedList.contains(url)){
			return;
		}
		ArrayList<String> magnets;
		ArrayList<String> sub_links;
		String content = "";
		try {
			text = get_response_html(url);
			sub_links = get_sub_links(text, url);
			String title = get_title(text);
			String time = getIssueTime(text);
			//deal with next url while title or magnets are null
			if(title != null){			
				title = title.replace("\\", "");
				magnets = get_magnet(text);
				if (magnets.isEmpty()) {
					visitedList.add(url);
					return;
				}
				content += "\n{\n  \"title\" : \"" + title +"\",\n  \"time\" : \""+time+"\",\n  \"magnet\" :  [";
				//store magnets in format
				if(magnets.size() > 1){
					for(int i = 0 ; i < magnets.size() ; i++){
						if(i == magnets.size() - 1){
							content += "\n              \""+magnets.get(i)+"\"\n           ],\n  \"url\" : \""+url+"\"\n},";
							break;
						}
						content += "\n              \""+magnets.get(i)+"\",";
					}
				}else{
					content += "\n              \""+magnets.get(0)+"\"\n            ],\n  \"url\" : \""+url+"\"\n},";
				}
				content_queue.add(content);
				System.out.println("thread id: "+THREADNAME+" current depth: "+depth+"  current url:  "+url);
				System.out.println(title+"  "+magnets.toString());
			}
			visitedList.add(url);
		}catch(HttpStatusException e){
			if(e.toString().contains("Status=4")){
				return;
			}
			if( retry_counts != 0){
				scan_url(url, depth, retry_counts-1);
			}
			return;
		}catch (SocketTimeoutException e){
			System.out.println("read time out, reconnect...  url:  "+url);
			if( retry_counts != 0){
				scan_url(url, depth, retry_counts-1);
			}
			return;
		}catch (Exception e) {
			e.printStackTrace();
			if( retry_counts != 0){
				scan_url(url, depth, retry_counts-1);
			}
			return;
		}
		if(depth > MAXDEPTH){
			return;
		}
			
		for(String sub_url : sub_links){
			if (visitedList.contains(sub_url)) {
				continue;
			}
			if( retry_counts != 0 && !sub_url.equals("http://www.hacg.wiki/wp/")){
				scan_url(sub_url, depth+1, retry_counts);
			}

		}
		if( content.length() == 0 ){
			return;
		}
	}
	
	@Override
	public void run() {
		scan_url(URL, 0, RETRY);
	}
	
}

