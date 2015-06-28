package interpreterImpl.chip8;

import interfaces.InterpreterInterface;

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

	private static void mySleep(int mills) {
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
		Runnable r = new Runnable() {

			@Override
			public void run() {
				while (!c.isFinished()) {
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
					try {
						Thread.sleep(16, 5);
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
