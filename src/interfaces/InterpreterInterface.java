package interfaces;

import java.io.File;
import java.text.ParseException;

public interface InterpreterInterface {
	public void interpret(File f, String inputString) throws ParseException;
	public void interpret(String s, String inputString) throws ParseException;
	public void parse(String s) throws ParseException;
}
