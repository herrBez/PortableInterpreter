package interpreterImpl.chip8;

import interfaces.InterpreterInterface;

import java.io.File;
import java.text.ParseException;

public class InterpreterChip8 implements InterpreterInterface {
	Chip8 chip;
	
	@Override
	public boolean interpret(File f, String inputString) {
		System.err.println("Not yet implemented");
		
		return false;
	}
	
	private static void mySleep(int mills){
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public boolean interpret(String s, String inputString) {
		System.out.println("INTERPRETER BITCH");
		Chip8 c = new Chip8();
		c.loadGameIntoMemory(s);
		
		try{
		while(!c.isFinished()){
			c.emulateCycle();
			if(c.isToDraw()){
				c.repaint();
				c.setToDraw(false);
			}
		}
		} catch(Exception e){
			e.printStackTrace();
		}
			
			
		
	
		return true;
	}

	@Override
	public void parse(String code) throws ParseException {
		
	}

	@Override
	public String getOutput() {
		return "NO OUTPUT";
	}

	@Override
	public String getErrorMessage() {
		return "";
	}
	
}
