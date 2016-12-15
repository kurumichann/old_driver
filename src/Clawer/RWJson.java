package Clawer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RWJson {

	// for sever delete src/Clawer/
	// for local env make sure prefix is "src/Clawer/" 
	static String  RESOURCEPATH = "resourcelist.json";
	static String  NUMPATH = "totalrows.text";
	static String  VIEWED = "viewed_list.text";
	
	public void ReadFile() {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(RESOURCEPATH);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONArray jsonArray = JSONArray.fromObject(laststr);
		int size = jsonArray.size();
		System.out.println("Size: " + size);
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			System.out.println("[" + i + "]title=" + jsonObject.get("title"));
			System.out.println("[" + i + "]magnet=" + jsonObject.get("magnet"));
		}
	}
	
	public void WriteFile(String path, String text){
		String content = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
			BufferedReader reader = new BufferedReader(inputStreamReader);
/*			while(i < last_rownum){
				//�������д�����
				if(i==1){
					i++;
					reader.readLine();
					continue;
				} 
				content += reader.readLine()+"\n";
				i++;
			}*/
			FileWriter fw = new FileWriter(path);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write("["+content+text+"\n]");			
			writer.flush();
			reader.close();
			writer.close();
			
		} catch (Exception e) {
			
		}
	}
	
	public void load_magnet_list(HashSet<String> hash){
		BufferedReader reader = null;
		String laststr = "";
		int start = 0;
		int end = 0; 
		try {
			FileInputStream fileInputStream = new FileInputStream(RESOURCEPATH);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				if(tempString.indexOf("magnet:?xt=urn:btih:") != -1){
					start = tempString.indexOf("\"");
					end = tempString.lastIndexOf("\"");
					hash.add(tempString.substring(start, end-1));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int GetRows(){
		String num = "";
		try {
			BufferedReader reader = null;
			FileInputStream fileInputStream = new FileInputStream(NUMPATH);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
			reader = new BufferedReader(inputStreamReader);
			num = reader.readLine();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(num == null){
			num = "0";
		}
		return Integer.valueOf(num);
	}
	
	public void SetRows(int number){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(NUMPATH));
			writer.write(String.valueOf(number));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void set_viewed_list(HashSet<String> list){
		Iterator<String> iterator = list.iterator();
		String content = "";
		while(iterator.hasNext()){
			content += iterator.next()+"\r\n";
		}
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(VIEWED));
			write.write(content);
			write.flush();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<String> get_viewed_list(){
		HashSet<String> hash = new HashSet<>();
		String temp = null;
		try {
			BufferedReader reader = null;
			FileInputStream fileInputStream = new FileInputStream(VIEWED);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
			reader = new BufferedReader(inputStreamReader);
			while((temp = reader.readLine()) != null){
				hash.add(temp);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hash;
	}
/*	public static void main(String[] args) {
		RWJson json = new RWJson();
		json.ReadFile();;
	}*/
}
