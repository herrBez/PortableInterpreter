package interfaces;

import java.io.File;
import java.text.ParseException;

public interface InterpreterInterface {
	/**
	 * 
	 * @param f
	 * @param inputString
	 */
	public boolean interpret(File f, String inputString);
	/**
	 * 
	 * @param s
	 * @param inputString 
	 * @throws ParseException
	 */
	public boolean interpret(String s, String inputString);
	/**
	 * This function validate the given code.
	 * @param code the code to parse
	 * @throws ParseException iff there is an error in parsing
	 */
	public void parse(String code) throws ParseException;
	
	public String getOutput();
	
	public String getErrorMessage();
}
