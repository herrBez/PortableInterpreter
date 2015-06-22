package interpreterImpl.brainfuck;

import interfaces.InterpreterInterface;

import java.io.File;
import java.text.ParseException;
import java.util.Stack;

import staticutilities.ReadFile;
/**
 *
 * @author mirko
 *
 */
public class BrainFuck implements InterpreterInterface{
	private byte [] array;
	private int pointer;
	/**Contains open parenthesises*/
	private Stack<Integer> open; 
	int pCounter;
	StringBuilder output;
	StringBuilder errorLog;
	public BrainFuck(){
		pointer = 0;
		array = new byte[20];
		open = new Stack<Integer>();
		pCounter = 0;
		output = new StringBuilder();
		errorLog = new StringBuilder();
	}
	
	@Override
	public boolean interpret(File f, String inputString)   {
		String s = ReadFile.readFile(f, false);
		return interpret(s, inputString);
	}
	
	

	
	private int interpret(char [] cArray, String input, Stack<Integer> open) {
		int inputIndex = 0;
		for(pCounter = 0; pCounter < cArray.length; ++pCounter){
			char c = cArray[pCounter];
			switch(c){
			case '>': 
				/* The length of the array is adapted dinamically, in order to simulate
				 * the infinity large band and contemporary to save space
				 */
				if(pointer == array.length - 1){
					int oldLength = array.length;
					byte [] copy = array.clone();
					array = new byte[copy.length + 20];
					System.arraycopy(copy, 0, array, 0, oldLength);
				}
				++pointer; break;
			case '<': --pointer; break;
			case '+': ++array[pointer]; break;
			case '-': --array[pointer]; break;
			case ',': array[pointer] = (byte) input.charAt(inputIndex++); break;
			case '.': output.append((char) array[pointer]); break;
			case '[':
				open.push(pCounter);
				if(array[pointer] == 0){
					int openParenthesis = 1;
					while(openParenthesis != 0){
						++pCounter;
						if(cArray[pCounter] == ']')
							openParenthesis--;
						if(cArray[pCounter] == '[')
							openParenthesis++;
					}
					open.pop();
				}
				break;
			case ']':
				pCounter = open.pop() - 1; //retrieve the position of the last open parenthesis
				break;
			}
		}
		return pCounter;

		
	
	}
	
	
		
	
	private void parse(char [] cArray) throws ParseException{
		Stack<Integer> open = new Stack<Integer>();
		for(int i = 0; i < cArray.length; i++){
			char c = cArray[i];
			switch(c){
			case '[': open.push(i); break;
			case ']': 
			if(open.isEmpty()) {
				errorLog.append("Position: " + i + " Close parenthesis without an open one");
				throw new ParseException("Position: " + i + " Close parenthesis without an open one", i);
			}
			else 
				open.pop();
			}
		}
	}
	
	public String getOutput(){
		return output.toString();
		
	}
	
	@Override
	public boolean interpret(String s, String input)  {
		pCounter = 0;
		if(input == null)
			input = "";
		char [] cArray = s.toCharArray();
		try {
		parse(cArray);
		} catch(ParseException pe){
			return false;
		}
		interpret(cArray, input, open);
		return true;
	}

	@Override
	public void parse(String s) throws ParseException {
		parse(s.toCharArray());
	}

	@Override
	public String getErrorMessage() {
		return errorLog.toString();
	}

	
}
