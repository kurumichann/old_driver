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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RWJson {

	// for sever delete src/Clawer/
	// for local env make sure prefix is "src/Clawer/" 
	static String  RESOURCEPATH = "resourcelist.json";
	static String  RESOURCEBAKPATH = "resourcelist_bak.json";
	static String  NUMPATH = "totalrows.text";
	static String  VIEWED = "viewed_list.text";
	static String VIEWEDBAK = "viewed_list_bak.text";
	static String  INCREMENT = "incremental_count.text";
	static int INCREMENT_COUNT = 0;
	
	public JSONArray ReadFile(String path) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONArray jsonArray = JSONArray.fromObject(laststr);
		return jsonArray;
//		int size = jsonArray.size();
//		System.out.println("Size: " + size);
//		for (int i = 0; i < size; i++) {
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			System.out.println("[" + i + "]title=" + jsonObject.get("title"));
//			System.out.println("[" + i + "]magnet=" + jsonObject.get("magnet"));
//		}
	}
	
	public void WriteFile(String path, String text){
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			FileWriter fw = new FileWriter(path);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write(text);
						
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
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
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
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
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
	
	public void set_incremental_count(int count){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(INCREMENT));
			writer.write("count="+String.valueOf(count));
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
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
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
	public HashSet<String> get_title_sets(){
		JSONArray arr = ReadFile(RESOURCEBAKPATH);
		int size = arr.size();
		HashSet<String> titleSet = new HashSet<>();
		for( int i = 0 ; i < size ; i++){
			titleSet.add(arr.getJSONObject(i).getString("title"));
		}
		return titleSet;
	}
	
	public JSONArray incremental_add_arr(){
		JSONArray newArray = ReadFile(RESOURCEPATH);
		JSONArray oldArray = ReadFile(RESOURCEBAKPATH);
		int sizen = newArray.size();
		int sizeo = oldArray.size();
		for( int i = 0 ; i < sizen ; i++){
			for( int j = 0 ; j < sizeo ; j++){
				String temp1 = (String) newArray.getJSONObject(i).get("title");
				String temp2 = (String) oldArray.getJSONObject(j).get("title");
//				System.out.println(temp1+" vs "+temp2);
				if(temp1.equals(temp2)){
					break;
				}
				if( (!temp1.equals(temp2)) && j == sizeo-1){
					INCREMENT_COUNT++;
					oldArray.add(newArray.getJSONObject(i));
				}
			}
		}
		return oldArray;
	}
	
	public void write_json_arr(JSONArray arr){
		String content = "";
		int size = arr.size();
		JSONObject job;
		String[] magnets;
		int length;
		for( int i = 0 ; i < size ; i++){
			job = arr.getJSONObject(i);
			content += "\n{\n  \"title\" : \"" + job.getString("title") +"\",\n  \"magnet\" :  [";
			magnets = job.getString("magnet").split(",");
			length = magnets.length;
			if( length > 1){
				for(int j = 0 ; j < length ; j++){
					if(j == length - 1){
						if( i == size-1 ){
							content += "\n              "+magnets[j].replace("\"]", "")+"\"\n           ]\n}";
							break;
						}
						content += "\n              "+magnets[j].replace("\"]", "")+"\"\n           ]\n},";
						break;
					}
					if( j == 0 ){
						content += "\n              "+magnets[j].replace("[", "")+",";
						continue;
					}

					content += "\n              "+magnets[j]+",";
				}
			}else{
				if( i == size-1 ){
					content += "\n              "+magnets[0].replace("\"]", "").replace("[", "")+"\"\n           ]\n}";
					break;
				}
				content += "\n              "+magnets[0].replace("\"]", "").replace("[", "")+"\"\n            ]\n},";
			}
		}
		WriteFile(RESOURCEPATH, "["+content+"\n]");
		
	}
	//combine viewed list with bak.text
	public void incremental_add_list(){
		ArrayList<String> newarr = new ArrayList<String>();
		ArrayList<String> oldarr = new ArrayList<String>();
		int sizen = 0;
		int sizeo = 0;
		BufferedReader reader = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(VIEWED);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				newarr.add(tempString);
			}
			
			fileInputStream = new FileInputStream(VIEWEDBAK);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			while ((tempString = reader.readLine()) != null) {
				oldarr.add(tempString);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		sizen = newarr.size();
		sizeo = oldarr.size();
		//ensure add data to oldarr when its size equals 0
		if(sizeo == 0 ){
			sizeo = 1;
		}
		for( int i = 0 ; i < sizen ; i ++){
			for( int j = 0 ; j < sizeo ; j ++){
				String temp1 = newarr.get(i);
				String temp2 = oldarr.get(j);
				if(temp1.equals(temp2)){
					break;
				}
				if( (!temp1.equals(temp2)) && j == sizeo-1){
					oldarr.add(temp1);
				}
			}
		}
		String content = "";
		for( String tmp : oldarr){
			content += tmp+"\n";
		}
		WriteFile(VIEWED, content);
	}
//	public static void main(String[] args) {
//		RWJson json = new RWJson();
//		json.incremental_add_list();;
//	}
}
