package main;

import java.text.ParseException;

import interfaces.InterpreterInterface;
import brainfuck.BrainFuck;

public class Test {
	public static void main(String[] args) throws ParseException {
		InterpreterInterface i = new BrainFuck();
		//i.interpret("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.",null);
		i.interpret("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.", null);
	}
}
