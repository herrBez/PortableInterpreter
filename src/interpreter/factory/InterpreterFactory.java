package interpreter.factory;

import interfaces.InterpreterInterface;
import interpreterImpl.brainfuck.BrainFuck;
import interpreterImpl.brainfuck.derivates.alphuck.Alphuck;
import interpreterImpl.brainfuck.derivates.ook.Ook;

public class InterpreterFactory {
	public static InterpreterInterface createInterpreter(SupportedInterpreter si) {
		InterpreterInterface i = null;
		switch(si){
			case BRAINFUCK: i = new BrainFuck(); break;
			case ALPHUCK: i = new Alphuck(); break;
			case Ook: i = new Ook(); break;
		} 
		return i;
	}
}
