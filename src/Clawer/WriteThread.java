package Clawer;

import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONArray;

public class WriteThread implements Runnable{

	BlockingQueue<String> queue;
	public static Integer count = 0;
	Set<String> visitedSet;
	LinkedList<String> array;
	StringBuffer content;

	RWJson rw;
	
	public void setRw(RWJson rw) {
		this.rw = rw;
	}
	
	public void setQueue(BlockingQueue<String> queue) {
		this.queue = queue;
	}
	
	public void setVisitedSet(Set<String> visitedSet) {
		this.visitedSet = visitedSet;
	}
	
	@Override
	public void run() {
		Timer timer = new Timer();
		JSONArray arr = rw.ReadFile(RWJson.RESOURCEPATH);
		content = new StringBuffer();
		array = new LinkedList<String>();
		content.append(arr.toString().replace("[", "").replace("]", ""));
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					queue.drainTo(array);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("========IO=======");
				for(String resource : array){
					content.append(resource.substring(0, resource.length()-1)+",");
				}
				
				rw.WriteFile(RWJson.RESOURCEPATH, "[\n"+content.substring(0, content.length()-1)+"\n]");
				synchronized (count) {
					count = count+array.size();
				}
				rw.SetRows(count); 
				rw.set_viewed_list(visitedSet);
				System.out.println("size of queue: "+queue.size()+" count of result: "+count);
				array.clear();
			}
		}, 5000, 10000);
		
	}

}
