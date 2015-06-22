package controller;

import gui.Gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer{
	Gui g;
	
	public Controller(Gui g){
		this.g = g;
		g.setVisible(true);
		g.addObserver(this);
	}
	
	
	public String getFileContent(File f){
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
		File f = (File) arg;
		g.putText(getFileContent(f));
	}
	
	

}
