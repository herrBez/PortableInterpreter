package staticutilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StaticUtility {
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
	
	public static short toUnsignedByte(byte signedByte){
		short unsignedByte = (short)(signedByte & 0xFF);
		return unsignedByte;
	}
	public static byte toSignedByte(short unsignedByte){
		byte signedByte = (byte) (unsignedByte & 0xFF);
		return signedByte;
	}

}
