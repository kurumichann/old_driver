package Clawer;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class test {
	public String website = null;
	static Document jsoup;
	public int MAXDEPTH = 40;
	public String title = null;
	public String magnet = null;
	Matcher match;
	String text;
	public Pattern get_a_patterna = Pattern.compile("<a.*?href=.*?>");
//[^0-9a-xA-X][0-9a-xA-X]{10,20}[\u4e00-\u9fa5]{0,10}[0-9a-xA-X]{10,20}[^0-9a-xA-X]
	public static Pattern get_magnet_pattern = Pattern.compile("[^0-9a-zA-Z][0-9a-zA-Z]{16,25}[\u4e00-\u9fa5]{0,10}[0-9a-zA-Z]{16,25}[\r|\n|<]");
	public Pattern get_title_pattern = Pattern.compile("<title>(.+)</title>");
	public List<Map<String, String>> resourceList;
	public static HashSet<String> visitedList = new HashSet<String>();
	
	public static void  main(String args[]){
		
		System.setProperty("http.proxySet", "true"); 
		System.setProperty("http.proxyHost", "127.0.0.1"); 
		System.setProperty("http.proxyPort", "1080");
		
		String url = "http://www.hacg.li/wp/17305.html";
		String text;
		try {
			jsoup  = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 ¡°+"
					+ "¡±(KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    text = jsoup.html();
	    System.out.println(text);
		Matcher match = get_magnet_pattern.matcher(text);
		LinkedList<String> result = new LinkedList<>();
		int number = 0;
		while(match.find()){
			result.add(match.group());
		}
		System.out.println(result);
		
		
	}
}
