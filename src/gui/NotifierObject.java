package gui;

import java.io.File;

public class NotifierObject {
	private File f;
	private boolean binary;
	
	public NotifierObject(File f, boolean binary){
		this.f = f;
		this.binary = binary;
	}

	public File getFile() {
		return f;
	}

	public boolean isBinary() {
		return binary;
	}
	
}
