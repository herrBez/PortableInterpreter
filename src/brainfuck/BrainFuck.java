package brainfuck;

import interfaces.InterpreterInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Stack;
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
	
	public BrainFuck(){
		pointer = 0;
		array = new byte[1000];
		open = new Stack<Integer>();
		pCounter = 0;
	}
	
	@Override
	public void interpret(File f, String inputString)   {
		BufferedReader br; 

		try {
			br = new BufferedReader(new FileReader(f));

			StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        br.close();
	        interpret(everything, inputString);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	
	void printState(char[] cArray, int pCounter){
	
		StringBuilder sb = new StringBuilder("INSTRUCTION:" + pCounter + "/" + (cArray.length-1) + "[");
		
		for(int i = 0; i < 20; i++){
			sb.append(array[i] + ",");
		}
		sb.append("]");
		
		System.out.println(sb);
	}
	
	private int interpret(char [] cArray, String input, Stack<Integer> open) {
		int inputIndex = 0;
		for(pCounter = 0; pCounter < cArray.length; ++pCounter){
			char c = cArray[pCounter];
			switch(c){
			case '>': ++pointer; break;
			case '<': --pointer; break;
			case '+': ++array[pointer]; break;
			case '-': --array[pointer]; break;
			case ',': array[pointer] = (byte) input.charAt(inputIndex++); break;
			case '.': System.out.print((char) array[pointer]); break;
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
			if(open.isEmpty()) 
				throw new ParseException("Position: " + i + " Close parenthesis without an open one", 0);
			else 
				open.pop();
			}
		}
	}
	
	@Override
	public void interpret(String s, String input) throws ParseException {
		pCounter = 0;
		if(input == null)
			input = "";
		char [] cArray = s.toCharArray();
		parse(cArray);
		interpret(cArray, input, open);
	}

	@Override
	public void parse(String s) throws ParseException {
		parse(s.toCharArray());
	}

	
}
