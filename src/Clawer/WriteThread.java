package Clawer;

import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class WriteThread implements Runnable{

	BlockingQueue<String> queue;
	public static Integer count = 0;
	Set<String> visitedSet;
	LinkedList<String> array;
	StringBuffer content;
	ExecutorService executor;
	int retry_cnt;
	RWJson rw;
	
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
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
		long startTime = new Date().getTime();
		Timer timer = new Timer();
		content = new StringBuffer();
		array = new LinkedList<String>();
		retry_cnt = 0;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(retry_cnt == 5){
					System.out.println(new Date()+"\n总计 "+count+" 条资源\n"+"用时 "+(new Date().getTime()-startTime)/1000+" 秒");
					executor.shutdown();
					this.cancel();
				}else if(queue.size() == 0){
					retry_cnt++;
				}else{
					retry_cnt = (retry_cnt == 0 ? 0 : retry_cnt-1);
				}
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
