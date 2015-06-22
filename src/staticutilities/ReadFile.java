package staticutilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {
	public static String readFile(File f, boolean binary){
		BufferedReader br; 
		StringBuilder sb = new StringBuilder();

		try {
			br = new BufferedReader(new FileReader(f));

	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	       
	        br.close();
	       
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return  sb.toString();
	}
}
