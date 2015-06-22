package interpreterImpl.brainfuck.derivates.ook;

import interpreterImpl.brainfuck.derivates.BrainfuckDerivate;


public class Ook extends BrainfuckDerivate{
	
	@Override
	public String toBrainFuckCode(String s) {
		char [] array = s.toCharArray();
		StringBuilder tmp = new StringBuilder();
		StringBuilder out = new StringBuilder();
		OokState state = OokState.EXPECT_O;
		for(int i = 0; i< array.length;i++){
			switch(state){
			case EXPECT_O: 
				if(array[i] == 'O') {
					tmp = new StringBuilder(""+ array[i]);
					state = OokState.EXPECT_o;
				}
				break;
			case EXPECT_o:
				if(array[i] == 'o') {
					tmp.append(array[i]);
					state = OokState.EXPECT_k;
				}
				else
					state = OokState.EXPECT_O;
				break;
			case EXPECT_k:
				if(array[i] == 'k') {
					tmp.append(array[i]);
					state = OokState.EXPECT_SPECIAL;
				} else {
					state = OokState.EXPECT_O;
				}
				break;
			case EXPECT_SPECIAL:
				
				if(array[i] == '?' || array[i] == '!' || array[i] == '.'){
					tmp.append(array[i]);
					out.append(tmp);
					
				}
				state = OokState.EXPECT_O;
				break;
			}
		}
		String code = out.toString();
		System.out.println(code);
		code = code.replaceAll("[Ook]", "");
		out = new StringBuilder();
		for(int i = 0; i+2 < code.length(); i+=2){
		
			String actual = code.substring(i, i+2);
			System.out.println("(" + i + "," + (i+2) +  "):"+ actual);
			if(actual.equals(".?"))
				out.append(">");
			else if(actual.equals("?."))
				out.append("<");
			else if(actual.equals(".."))
				out.append("+");
			else if(actual.equals("!!"))
				out.append("-");
			else if(actual.equals("!."))
				out.append(".");
			else if(actual.equals(".!"))
				out.append(",");
			else if(actual.equals("!?"))
				out.append("[");
			else if(actual.equals("?!"))
				out.append("]");
			
		}
		
				
				

		System.out.println(out);
		return out.toString();
	}
	
	
}	

