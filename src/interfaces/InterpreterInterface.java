package interfaces;

import java.io.File;
import java.text.ParseException;

public interface InterpreterInterface {
	/**
	 * 
	 * @param f
	 * @param inputString
	 * @throws ParseException
	 */
	public void interpret(File f, String inputString) throws ParseException;
	/**
	 * 
	 * @param s
	 * @param inputString 
	 * @throws ParseException
	 */
	public void interpret(String s, String inputString) throws ParseException;
	/**
	 * This function validate the given code.
	 * @param code the code to parse
	 * @throws ParseException iff there is an error in parsing
	 */
	public void parse(String code) throws ParseException;
}
