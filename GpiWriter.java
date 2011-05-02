import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class GpiWriter {
	
	final static String program = 
		"set xlabel \"ID copiador\"\n" +
		"set boxwidth 0.5\n" +
		"set autoscale\n" +
		"set term png xffffff x000000\n" +
		"set style fill solid 0.5\n" +
		"plot input_file using 2:3 w boxes lc rgb \"#444444\" lw 1.0 title \"mean\", \\\n" +
		"     input_file using 2:4 w points lc rgb \"#AA0000\" lw 2.0 title \"min\", \\\n" +
		"     input_file using 2:5 w points lc rgb \"#0000AA\" lw 2.0 title \"max\"\n";

	final static int range_table[][] = {
		{11, 2},
		{101, 2},
		{1001, 2},
		{1001, 11},
		{1001, 101},
		{1001, 1001},
		{1001, 1501},
	};
	
	static void write_gpi(int test_id) {
		Formatter formatter = null;
		Locale.setDefault(Locale.US);
		String file_name = "plot/T"+test_id+"_pacotes.gpi";
		File file = open_file(file_name);
		formatter = create_formatter(file_name, file);
		
		formatter.format("input_file = 'data/T%d_pacotes.data'\n", test_id);
		formatter.format("set output 'T%d_pacotes.png'\n", test_id);
		formatter.format("set xrange [-2:%d]\n", range_table[test_id][1]);
		formatter.format("set yrange [0:%d]\n", range_table[test_id][0]);
		formatter.format("set ylabel \"Numero de pacotes copiados\"\n");
		formatter.format(program);
		
		formatter.flush();
		formatter.close();
		
		file_name = "plot/T"+test_id+"_ociosidade.gpi";
		file = open_file(file_name);
		formatter = create_formatter(file_name, file);
		
		formatter.format("input_file = 'data/T%d_ociosidade.data'\n", test_id);
		formatter.format("set output 'img/T%d_ociosidade.png'\n", test_id);
		formatter.format("set xrange [-2:%d]\n", range_table[test_id][1]);
		formatter.format("set yrange [0:%d]\n", range_table[test_id][0]);
		formatter.format("set ylabel \"Tempo ocioso\"\n");
		formatter.format(program);
		
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
		File file;
		file = new File(file_name);
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.err.println("Arquivo: "+file_name+" nao pode ser criado");
			e.printStackTrace();
			System.exit(-1);
		}
		return file;
	}
	
	public static void main(String args[]) {
		int number_of_tests = 5;
		for(int i = 0; i < number_of_tests; i++)
			write_gpi(i);
	}
}
