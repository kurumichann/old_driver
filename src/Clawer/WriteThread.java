package Clawer;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class WriteThread implements Runnable{

	BlockingQueue<String> queue;
	public static Integer count = 0;
	Set<String> visitedSet;


	RWJson rw;
	String content;
	
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
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					content = queue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("========IO=======");
				rw.WriteFile(RWJson.RESOURCEPATH, "[\n"+content.substring(0, content.length()-1)+"\n]");
				synchronized (count) {
					count++;
				}
				rw.SetRows(count); 
				rw.set_viewed_list(visitedSet);
				System.out.println("size of queue: "+queue.size()+" count of result: "+count);
			}
		}, 5000, 5000);
		
	}

}
