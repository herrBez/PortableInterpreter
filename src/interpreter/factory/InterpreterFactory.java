package interpreter.factory;

import interfaces.InterpreterInterface;
import InterpreterImpl.Alphuck;
import InterpreterImpl.BrainFuck;

public class InterpreterFactory {
	public static InterpreterInterface createInterpreter(SupportedInterpreter si) {
		InterpreterInterface i = null;
		switch(si){
			case BRAINFUCK: i = new BrainFuck(); break;
			case ALPHUCK: i = new Alphuck(); break;
		} 
		return i;
	}
}
