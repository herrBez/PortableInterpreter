package controller;

import gui.Gui;
import gui.NotifierObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import staticutilities.StaticUtility;

public class Controller implements Observer {
	Gui g;

	public Controller(Gui g) {
		this.g = g;
		g.setVisible(true);
		g.addObserver(this);
	}
	

	
	
	public String getBinaryFileContent(File f) {
		String everything = null;
		FileInputStream fi;
		try {
			fi = new FileInputStream(f);

			StringBuilder sb = new StringBuilder();
			byte[] b = new byte[2];
			while (fi.read(b) > 0) {
				short b0s = StaticUtility.toUnsignedByte(b[0]);
				short b1s = StaticUtility.toUnsignedByte(b[1]);
					
				int val = (b0s << 8) + b1s;
				StringBuilder myHexString = new StringBuilder(Integer.toHexString(val));
				while(myHexString.toString().length() < 4)
					myHexString.insert(0, '0');
				sb.append(myHexString + "\n");
			
				

			}
			everything = sb.toString();
			fi.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return everything;
	}

	public String getFileContent(File f) {
		BufferedReader br;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(f));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
			System.out.println(everything);
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return everything;
	}

	@Override
	public void update(Observable o, Object arg) {
		NotifierObject no = (NotifierObject) arg;
		if (no.isBinary())
			g.putText(getBinaryFileContent(no.getFile()));
		else
			g.putText(getFileContent(no.getFile()));
	}

}
