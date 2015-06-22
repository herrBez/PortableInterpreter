package interpreterImpl.brainfuck.derivates.alphuck;

import interpreterImpl.brainfuck.derivates.BrainfuckDerivate;


public class Alphuck extends BrainfuckDerivate{
	
	
	public String toBrainFuckCode(String s){
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
	
	

}
