package simulator;

import java.util.Random;

public class Package extends Thread {
	protected int id, size;
	private final static int max_size = 1500;

	public Package(int id) {
		this.id = id;
		Random random = new Random(); 
		size = 1 + random.nextInt(max_size);
		this.start();
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	public void run() {
//		System.out.format("P%d: start\n", id);
		Router.package_sync_add(this);
	}
}
