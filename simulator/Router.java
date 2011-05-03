package simulator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;

public class Router {

	protected static int total_packages;
	protected static Queue<Package> package_queue;
	
	// delay entre pacotes
	private final static int min_wait = 250;
	private final static int max_wait = 1000;

	protected synchronized static Package package_sync_remove() {
		try {
			return package_queue.remove();
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}
	
	protected synchronized static boolean package_sync_add(Package p) {
		return package_queue.add(p);
	}
	
	public void simulate(int test_id, int test_number, int m, int n, Data data[]) {
    
		// INIT
    	
		total_packages = m;
		Package pack[] = new Package[m];
		Copier copier[] = new Copier[n];
		package_queue = new LinkedList<Package>();
		System.out.format("Teste %d.%d\n%d pacote(s), %d copiador(es)\n",test_id, test_number, m, n);

		// copiadores sao criados primeiros e ficam esperando pacotes chegarem
		for (int i = 0; i < n; i++) {
			try {
				// segundo argumento eh o tempo de sleep quando a lista
				// de pacotes esta vazia
				copier[i] = new Copier(i, (int)(Math.log(n)*80+100));
			} catch (OutOfMemoryError e) {
				n = i;
				System.err.format("R: sem memoria, foram criados somente %d copiadores\n", n);
				e.printStackTrace();
				break;
			}
		}
		
		for (int i = 0; i < m; i++) {
			try {
				if(i != 0) {
					// delay de chegada
					Random random = new Random();
					int sleep_time = min_wait + random.nextInt(max_wait-min_wait+1); 
					try {
						Thread.sleep(sleep_time);
					} catch (InterruptedException e) {
						System.err.println("R: nao pode esperar");
					}
                }
				pack[i] = new Package(i);
			} catch (OutOfMemoryError e) {
				total_packages = m = i;
				System.err.format("R: sem memoria, foram criados somente %d pacotes\n", m);
				e.printStackTrace();
				break;
			}
		}
		
		// WAIT + REPORT
		
		for (int i = 0; i < n; i++) {
			try {
				copier[i].join();
			} catch (InterruptedException e) {
				System.err.println("R: nao pode esperar");
			}
			copier[i].report(data[i]);
		}
		System.out.println("");
		
		
		// CLEANUP
		
		if(!package_queue.isEmpty()) {
			System.err.println("R: alguns pacotes nao foram atendidos");
			Iterator<Package> i = package_queue.iterator();
			while(i.hasNext())
				package_queue.remove();
		}
		package_queue = null;
		
		for (int i = 0; i < n; i++) {
			try {
				copier[i].finalize();
			} catch (Throwable e) {
				System.err.println("R: nao pode finalizar C"+copier[i].id);
			}
		}
		copier = null;
		for (int i = 0; i < m; i++) {
			try {
				pack[i].finalize();
			} catch (Throwable e) {
				System.err.println("R: nao pode finalizar P"+pack[i].id);
			}
		}
		pack = null;
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Router router = new Router();
		int test[][] = { 
				{10, 1}, // m, n
				{100, 1},
				{1000, 1},
				{1000, 10},
				{1000, 100},
				{1000, 1000},
				// consegui criar no max 2550 threads
//				{1000, 10000}, 
		};
		
		for(int i = 0; i < test.length; i++) {
			Data data[] = new Data[test[i][1]];
			for(int j = 0; j < test[i][1]; j++)
				data[j] = new Data();
			
			// SIMULACAO
			
			for(int j = 0; j < 3; j++)
				router.simulate(i, j, test[i][0], test[i][1], data);
			
			// ARQUIVOS DE SAIDA
			
			Data.update_overall(data);
			Data.generate_plot_data(i, data);
			
			// SAIDA PADRAO
			System.out.println("Medias do Teste "+i);
			
			for(int j = 0; j < test[i][1]; j++) {
				System.out.format("C%d: ~%.2f pacotes, min=%d, max=%d, ~%.2fs, min=%.2f max=%.2f\n", j,
						data[j].packages_done_mean(), data[j].packages_done_min, data[j].packages_done_max,
						data[j].idle_time_mean(), data[j].idle_time_min, data[j].idle_time_max);
			}
			System.out.format("Geral: ~%.2f pacotes, min=%d, max=%d, ~%.2fs, min=%.2f max=%.2f\n\n",
					Data.overall_packages_done_mean, Data.overall_packages_done_min, Data.overall_packages_done_max,
					Data.overall_idle_time_mean, Data.overall_idle_time_min, Data.overall_idle_time_max);
		}
		System.out.println("Fim dos testes");
	}    

}
