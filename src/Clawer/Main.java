package Clawer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;

public class Main {
	public static void  main(String args[]){
		
		System.setProperty("http.proxySet", "true"); 
		System.setProperty("http.proxyHost", "127.0.0.1"); 
		System.setProperty("http.proxyPort", "1080");

		ExecutorService executor_clawer = Executors.newFixedThreadPool(3);
		executor_clawer.execute(new ClawerThread("1","http://www.hacg.love/wp/all/anime/%E3%80%90%E7%86%9F%E8%82%89%E3%80%91%E6%A1%9C%E9%83%BD%E5%AD%97%E5%B9%95%E7%BB%84-poro%E8%BB%A2%E7%94%9F%E5%89%A3%E5%A5%B4%E3%81%AE%E5%AD%90%E4%BD%9C%E3%82%8A%E9%97%98%E6%8A%80%E5%9C%BA%EF%BC%88/"));
		executor_clawer.execute(new ClawerThread("2","http://www.hacg.love/wp/all/anime/%E7%86%9F%E8%82%89%E3%81%9B%E3%82%8B%E3%81%B5%E3%81%83%E3%81%A3%E3%81%97%E3%82%85-%E3%82%82%E3%82%93%E3%82%80%E3%81%99%E3%83%BB%E3%81%8F%E3%81%88%E3%81%99%E3%81%A8-2%E3%80%8C%E9%AD%85%E5%87%AA/"));
		executor_clawer.execute(new ClawerThread("3","http://www.hacg.love/wp/all/anime/%E3%83%89s%E3%81%AA%E3%83%9E%E3%82%A4%E3%83%8A%E4%BC%9A%E9%95%B7%E3%82%B5%E3%83%9E%E3%81%8Cm%E3%83%8E%E3%83%BC%E3%83%88%E3%81%AB%E6%94%AF%E9%85%8D%E3%81%95%E3%82%8C%E3%81%BE%E3%81%97%E3%81%9F%E3%80%82/"));
	
		WriteThread write = new WriteThread();
		write.setQueue(ClawerThread.content_queue);
		write.setVisitedSet(ClawerThread.visitedList);
		write.setRw(new RWJson());
		write.setExecutor(executor_clawer);
		Thread b = new Thread(write);
		b.start();
		
		RWJson rw = new RWJson();
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
