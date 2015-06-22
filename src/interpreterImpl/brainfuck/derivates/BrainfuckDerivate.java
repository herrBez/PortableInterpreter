package interpreterImpl.brainfuck.derivates;

import java.io.File;
import java.text.ParseException;

import staticutilities.ReadFile;
import interfaces.InterpreterInterface;
import interpreterImpl.brainfuck.BrainFuck;

public abstract class BrainfuckDerivate implements InterpreterInterface{
	protected BrainFuck brainfuck;
	
	public BrainfuckDerivate(){
		brainfuck = new BrainFuck();
	}

	@Override
	public boolean interpret(File f, String inputString) {
		String code = ReadFile.readFile(f, false);
		return interpret(code, inputString);
	}
	
	protected abstract String toBrainFuckCode(String s);
	
	@Override
	public boolean interpret(String s, String inputString) {
		
		return brainfuck.interpret(toBrainFuckCode(s), inputString);
	}
	@Override
	public void parse(String code) throws ParseException {
		brainfuck.parse(toBrainFuckCode(code));
	}
	@Override
	public String getOutput() {
		return brainfuck.getOutput();
	}

	@Override
	public String getErrorMessage() {
		return brainfuck.getOutput();
	}

	
}
