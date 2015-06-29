package interpreterImpl.chip8;

import interfaces.InterpreterInterface;
import interpreterImpl.chip8.implementation.Chip8;

import java.io.File;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InterpreterChip8 implements InterpreterInterface {
	Chip8 chip;

	@Override
	public boolean interpret(File f, String inputString) {
		System.err.println("Not yet implemented");

		return false;
	}

	

	@Override
	public boolean interpret(String s, String inputString) {
		System.out.println("INTERPRETER BITCH");
		Chip8 c = new Chip8();
		c.loadGameIntoMemory(s);
		Runnable r = new Runnable() {

			@Override
			public void run() {
				while (!c.isFinished()) {
					long start = System.nanoTime();
					try {
						c.emulateCycle();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (c.isToDraw()) {
						c.repaint();
						c.setToDraw(false);
					}
					c.storeKeyState();
					
						
				
					try{	
						//The cycle last 1s/60 (60 Hz) = 16666666 nano secondi
						
						long toWait = 16666666 -  (System.nanoTime() - start);
						if(toWait > 0){

							long millis = (long)(toWait/1000000);
							//long nano = (long)toWait%1000000;
							
							Thread.sleep(millis);//, (int) nano);
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}

			}
		};

		ExecutorService e = Executors.newSingleThreadExecutor();
		e.execute(r);
		e.shutdown();

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
