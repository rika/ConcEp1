package simulator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Copier extends Thread {
	protected int id;
	private final static int speed = 750;
	private static int idle_sleep_time = 100;
	private long total_idle_time = 0;
	// guarda os pacotes que foram copiados por este copiador
	private Queue<Package> done = new LinkedList<Package>();
	
	private static int total_done = 0;

	public Copier(int id, int idle_sleep_time) {
		Copier.idle_sleep_time = idle_sleep_time;
		this.id = id;
		this.start();
	}
	
	protected void finalize() throws Throwable {
		total_done = 0;
		Iterator<Package> i = done.iterator();
		while(i.hasNext())
			done.remove();
		done = null;
		super.finalize();
	}
	
    public void run() {
//		System.out.format("C%d: start\n", id);
    	while(true) {
    		if(total_done == Router.total_packages)	return;

    		else if(!Router.package_queue.isEmpty()) {
    		    Package p = receive();
				if(p != null) copy(p);
    		}
    		else {
    			try {
    				total_idle_time += idle_sleep_time;
    				Thread.sleep(idle_sleep_time);
    			} catch (InterruptedException e) {
    				System.err.format("C%d: acordou antes do tempo\n", id);
    			}
    		}
    	}
    }
    
    public Package receive() {
    	return Router.package_sync_remove();
    }
    
    public synchronized static void done() {
    	total_done++;
    }
    
	public void copy(Package p) {
		try {
			float copying_time = 1000*p.size/(float)speed;
			Thread.sleep((int)(copying_time));
			done.add(p);
			done();
		}
		catch(InterruptedException e) {
			System.err.format("C%d: nao acabou de copiar o pacote %d\n", id, p.id);
		}
	}
	
	public void report(Data data) {
		int total_packages_done = 0;
		int total_bytes_copied = 0;
		Iterator<Package> i = done.iterator();
		while(i.hasNext()) {
			total_packages_done++;
			total_bytes_copied += i.next().size;
		}
		float total_idle_time_f = (float)total_idle_time/1000;
		System.out.println("C"+id+": "+total_packages_done+" pacotes, "+
				total_bytes_copied+" bytes, "+total_idle_time_f +"s");
		
		data.update(total_packages_done, total_idle_time_f);
	}
}
