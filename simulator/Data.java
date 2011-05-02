package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class Data {
	public int n;
	public int packages_done_max = 0;
	public int packages_done_min = 100000;
	public int packages_done_total = 0;
	public float idle_time_max = 0;
	public float idle_time_min = 100000;
	public float idle_time_total = 0;
	
	public static float overall_packages_done_mean;
	public static int overall_packages_done_min;
	public static int overall_packages_done_max;
	
	public static float overall_idle_time_mean;
	public static float overall_idle_time_min;
	public static float overall_idle_time_max;
	
	public static void generate_plot_data(int test_id, Data data[]) {
		Formatter formatter = null;
		Locale.setDefault(Locale.US);
		
		
		String file_name = "plot/data/T"+test_id+"_pacotes.data";
		File file = open_file(file_name);
		formatter = create_formatter(file_name, file);
		
		formatter.format("#COPIER ID MEAN MIN MAX%n");
		for(int i = 0; i < data.length; i++) {
			formatter.format("C%d %d %.2f %d %d%n", i, i, data[i].packages_done_mean(),
					data[i].packages_done_min, data[i].packages_done_max);
		}
		formatter.flush();
		formatter.close();

		file_name = "plot/data/T"+test_id+"_ociosidade.data";
		file = open_file(file_name);
		
		formatter = create_formatter(file_name, file);
		
		formatter.format("#COPIER ID MEAN MIN MAX%n");
		for(int i = 0; i < data.length; i++) {
			formatter.format("C%d %d %.2f %.2f %.2f%n", i, i, data[i].idle_time_mean(),
					data[i].idle_time_min, data[i].idle_time_max);
		}
		formatter.flush();
		formatter.close();
	}

	private static Formatter create_formatter(String file_name, File file) {
		Formatter formatter = null;
		try {
			formatter = new Formatter(file);
		} catch (FileNotFoundException e) {
			System.err.println("Arquivo: "+file_name+" nao foi encontrada");
			e.printStackTrace();
			System.exit(-1);
		}
		return formatter;
	}

	private static File open_file(String file_name) {
		File file = new File(file_name);
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.err.println("Arquivo: "+file_name+" nao pode ser criado");
			e.printStackTrace();
			System.exit(-1);
		}
		return file;
	}
	
	public void update(int packages_done, float idle_time) {
		n++;
		packages_done_total += packages_done;
		if(packages_done > packages_done_max)
			packages_done_max = packages_done;
		if(packages_done < packages_done_min)
			packages_done_min = packages_done;		
		idle_time_total += idle_time;
		if(idle_time > idle_time_max)
			idle_time_max = idle_time;
		if(idle_time < idle_time_min)
			idle_time_min = idle_time;
	}
	
	public static void update_overall(Data data[]) {
		overall_packages_done_mean = 0;
		overall_packages_done_min = 100000;
		overall_packages_done_max = 0;
		
		overall_idle_time_mean = 0;
		overall_idle_time_min = 100000;
		overall_idle_time_max = 0;
		
		for(int i = 0; i < data.length; i++) {
			overall_packages_done_mean += data[i].packages_done_mean();
			if(overall_packages_done_max < data[i].packages_done_max)
				overall_packages_done_max = data[i].packages_done_max;
			if(overall_packages_done_min > data[i].packages_done_min)
				overall_packages_done_min = data[i].packages_done_min;
		
			overall_idle_time_mean += data[i].idle_time_mean();
			if(overall_idle_time_max < data[i].idle_time_max)
				overall_idle_time_max = data[i].idle_time_max;
			if(overall_idle_time_min > data[i].idle_time_min)
				overall_idle_time_min = data[i].idle_time_min;
		}
		overall_packages_done_mean /= data.length;
		overall_idle_time_mean /= data.length;
	}
	
	public float packages_done_mean() {
		return (float)packages_done_total/(float)n;
	}

	public float idle_time_mean() {
		return idle_time_total/n;
	}

}
