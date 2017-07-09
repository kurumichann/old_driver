package Clawer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;

public class Main {
	public static void  main(String args[]){
		
		System.setProperty("http.proxySet", "true"); 
		System.setProperty("http.proxyHost", "127.0.0.1"); 
		System.setProperty("http.proxyPort", "1080");

		String web = "http://www.llss.me/wp/50407.html";
		Executor executor_clawer = Executors.newFixedThreadPool(3);
		executor_clawer.execute(new ClawerThread("1","http://www.llss.me/wp/50407.html"));
		executor_clawer.execute(new ClawerThread("2","http://www.llss.me/wp/170.html"));
		executor_clawer.execute(new ClawerThread("3","http://www.llss.me/wp/50226.html"));
	
		Executor executor_w = Executors.newFixedThreadPool(2);
		WriteThread write = new WriteThread();
		write.setQueue(ClawerThread.content_queue);
		write.setVisitedSet(ClawerThread.visitedList);
		write.setRw(new RWJson());
		executor_w.execute(write);
		executor_w.execute(write);
		
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
