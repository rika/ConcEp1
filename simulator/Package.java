package simulator;

import java.util.Random;

public class Package extends Thread {
	protected int id, size;
	
	private final static int min_wait = 5;
	private final static int max_wait = 10;
	private final static int max_size = 75;
	
	public Package(int id) {
		this.id = id;
		this.start();
	}
	
	public void start() {
		Random random = new Random(); 
		size = 1 + random.nextInt(max_size);
		int sleep_time = min_wait + random.nextInt(max_wait-min_wait+1); 
		try {
			Thread.sleep(sleep_time);
		} catch (InterruptedException e) {
			System.err.format("P%d: nao pode esperar\n", id);
		}
		finally {
			super.start();
		}
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	public void run() {
//		System.out.format("P%d: start\n", id);
		Router.package_sync_add(this);
	}
}
