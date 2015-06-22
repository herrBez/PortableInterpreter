package InterpreterImpl;

import java.io.File;
import java.text.ParseException;

import staticutilities.ReadFile;
import interfaces.InterpreterInterface;

public class Alphuck implements InterpreterInterface{
	BrainFuck brainfuck;
	public Alphuck(){
		brainfuck = new BrainFuck();
	}
	
	@Override
	public boolean interpret(File f, String inputString)  {
		String code = ReadFile.readFile(f, false);
		return interpret(code, inputString);
	}
	
	private String toBrainFuckCode(String s){
		s = s.replace('a', '>');
		s = s.replace('c', '<');
		s = s.replace('e', '+');
		s = s.replace('i', '-');
		s = s.replace('j', '.');
		s = s.replace('o', ',');
		s = s.replace('p', '[');
		s = s.replace('s', ']');
		return s;
	}
	/*
	private String toAlphuckCode(String s){
		s = s.replace('>', 'a');
		s = s.replace('<', 'c');
		s = s.replace('+', 'e');
		s = s.replace('-', 'i');
		s = s.replace('.', 'j');
		s = s.replace(',', 'o');
		s = s.replace(']', 'p');
		s = s.replace('[', 's');
		return s;
	}*/


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
