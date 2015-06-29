package interpreterImpl.chip8.implementation;

import interpreterImpl.chip8.gui.Chip8Gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

public class Chip8 {
	public static final int WIDTH = 64;
	public static final int HEIGHT = 32;
	private short[] memory; // byte is signed ==> impossible to represent 255 ..
	private int pc;
	private short[] V;
	private int I; // 16-Bit -.-
	private byte[] screen;
	private short delay_timer;
	private short sound_timer;
	private Stack<Integer> stack;
	
	private boolean drawFlag;
	private int opcode;
	private int fontset_start;
	private int last_index;
	private Chip8Gui gui;
	public static final short MAX_STACK_SIZE = 0x10;
	private short[] chip8_fontset = { 
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80 // F
	};
	
	public byte[] keyState;
	public Chip8() {
		initialize();
	}

	private void initialize() {
		keyState = new byte[0x10];
		memory = new short[0x1000];
		pc = 0x200; // The first 512 Bytes are occupied by the interprter
		V = new short[0x10]; // The registers
		Arrays.fill(V, (short)0);
		screen = new byte[64 * 32];
		stack = new Stack<Integer>();
		drawFlag = true;
		I = 0;
		opcode = 0;
		fontset_start = 0x050;
		for (int i = 0; i < chip8_fontset.length; i++){
			memory[fontset_start + i] = chip8_fontset[i];
		}
		gui = new Chip8Gui();

		
		SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					gui.setVisible(true);

				}
		});
		
		

	}

	private void copyInScreen(byte[][] sprite, short height, short X, short Y) {
		V[0xF] = 0;
		short actualPixel, actualY, actualX;
		short oldPixel, newPixel;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < 8; j++) {
				actualY = (short) ((Y + i) % HEIGHT);
				actualX = (short) ((X + j) % WIDTH);
				actualPixel = sprite[i][j];
				oldPixel = screen[actualY * WIDTH + actualX];
				
				screen[actualY * WIDTH + actualX] ^= actualPixel;
				newPixel = screen[actualY * WIDTH + actualX];
				// A PIXEL WAS ERASED
				if (oldPixel == 1 && newPixel == 0) {
					V[0xF] = 1;
				}

			}
		}
	}
	
	
	
	private short getX(){
		return (short) ((opcode & 0x0F00) >> 8);
	}
	private short getY(){
		return (short) ((opcode & 0x00F0) >> 4);
	}
	private short getNN(){
		return (short) (opcode & 0x00FF);
	}
	private int getNNN(){
		return (opcode & 0x0FFF);
	}
	
	public void executeOpcode() throws Exception {
		short x, y, NN;
	
		switch (opcode & 0xF000) {
		case 0x0000:
			switch (opcode & 0x00FF) {
			case 0x00E0: // 0x00E0: Clears the screen
				Arrays.fill(screen, (byte)0); 
				break;

			case 0x00EE: // 0x00EE: Returns from subroutine
				pc = stack.pop();
				break;
			//Ignore the other 0x0NNN Combinations: Calls RCA 1802 program at address NNN.
			}
			break;
			
			
			
		// 1NNN: jump to address NNN
		case 0x1000:
			pc = getNNN();
			break;
		// 2NNN: Call subroutine at adress NNN
			
			
			
			
		case 0x2000:
			stack.push(pc);
			if (stack.size() > MAX_STACK_SIZE)
				throw new StackOverflowError();
			pc = getNNN();
			break;
			
			
			
			
		// 3xNN: Skip the next instruction if V[x] == NN
		case 0x3000:
			x = getX();
			NN = getNN();
			if (V[x] == NN) {
				pc += 2;
			}
			break;
			
			
			
		// 4XNN Skips the next instruction if VX doesn't equal NN.
		case 0x4000:
			x = getX();
			NN = getNN();
			if (V[x] != NN) {
				pc += 2;
			}
			break;
			
			
			
			
		// 5XY0 Skips the next instruction if VX equals VY.
		case 0x5000:
			x = getX();
			y = getY();
			if ((opcode & 0x000F) != 0){
				System.err.printf("opcode %4X not known", opcode);
				System.exit(0);
			}
			if (V[x] == V[y])
				pc += 2;

			break;
		// 6XNN Sets VX to NN.
		case 0x6000:
			x = getX();
			NN = getNN();
			V[x] = NN;
			break;
			
			
			
		//Adds NN to VX.
		case 0x7000:
			x = getX();
			NN = getNN();
			V[x] = (short)((V[x] + NN) & 0xFF);
			
			break;
			
			
		case 0x8000:
			x = getX();
			y = getY();
			switch (opcode & 0x000F) {
			// Sets VX to the value of VY.
			case 0x0:
				V[x] = V[y];
				break;
			// Sets VX to VX or VY.
			case 0x1:
				V[x] = (short) ((V[x] | V[y]) & 0xFF);
				break;
			// Sets VX to VX and VY.
			case 0x2:
				V[x] = (short) ((V[x] & V[y]) & 0xFF);
				break;
			// Sets VX to VX xor VY.
			case 0x3:
				V[x] = (short) ((V[x] ^ V[y]) & 0xFF);
				break;
			// Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when
			// there isn't./
			case 0x4:
				int sum = V[x] + V[y];
				V[x] = (short) (sum & 0xFF); //max 255 0xFF
				V[0xF] = (short) (sum >= 0x100 ? 1 : 0);
				break;
			// VY is subtracted from VX. VF is set to 0 when there's a borrow,
			// and 1 when there isn't. Vx = Vx - Vy
			case 0x5:
				V[0xF] = (short) (V[x] < V[y]?0:1);
				V[x] = (short) ((V[x] - V[y]) & 0xFF);
				break;
			// Shifts VX right by one. VF is set to the value of the least
			// significant bit of VX before the shift.[2]
			case 0x6:
				V[0xF] = (short) (V[x] & 0x01);
				V[x] = (short) ((V[x] >> 1) & 0xFF);
				break;
			// Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and
			// 1 when there isn't. Vx = Vy-Vx
			case 0x7:
				V[0xF] = (short) (V[x] > V[y]?0:1);
				V[x] = (short) ((V[y]-V[x]) & 0xFF);
				break;
			// Shifts VX left by one. VF is set to the value of the most
			// significant bit of VX before the shift.[2]
			case 0xE:
				V[0xF] = (short) ((V[x] & 0x80) >> 7); // 0x8 == 1000 -> most significant bit
				V[x] = (short) ((V[x] << 1) & 0xFF);
				break;
			default:
				System.err.printf("opcode %04X not known", opcode);
				System.exit(0);
			}

			break;
		// Skips the next instruction if VX doesn't equal VY.
		case 0x9000:
			x = getX();
			y = getNN();
			if (V[x] != V[y])
				pc += 2;

			// Sets I to the address NNN.
		case 0xA000:
			I = getNNN();
			break;
		// Jumps to the address NNN plus V0.
		case 0xB000:
			pc = getNNN() + V[0];
			break;
		case 0xC000:
			x = getX();
			NN = getNN();
			short rand = (short) (Math.random()*0x100);
			V[x] = (short) (rand & NN);

			break;
		// 0xDxyN -> V[x] V[y] N = height of the sprite
		case 0xD000:
			x = getX();
			y = getY();
			if(V[x] < 0 || V[y] < 0) {
				printSituation();
				System.exit(0);
			}
			short height = (short) (opcode & 0x000F);
			short[] buffer = new short[height];
			byte[][] spriteMatrix = new byte[height][8];
			int tmpI = I;
			for (int i = 0; i < height; i++) {
				buffer[i] = memory[tmpI + i]; // read 8 pixel
				short fNibble = (short) ((buffer[i] & 0xF0) >> 4); // firstNibble
				spriteMatrix[i][0] = (byte) ((fNibble & 0x8) >> 3); // 8 = 1000
																	// only the
																	// first bit
				spriteMatrix[i][1] = (byte) ((fNibble & 0x4) >> 2); // 4 = 0100
																	// only the
																	// second
																	// bit
				spriteMatrix[i][2] = (byte) ((fNibble & 0x2) >> 1); // 2 = 0010
																	// only the
																	// third bit
				spriteMatrix[i][3] = (byte) ((fNibble & 0x1)); // 1 = 0001

				short sNibble = (short) (buffer[i] & 0x0F); // Second Nibble
				spriteMatrix[i][4] = (byte) ((sNibble & 0x8) >> 3);
				spriteMatrix[i][5] = (byte) ((sNibble & 0x4) >> 2);
				spriteMatrix[i][6] = (byte) ((sNibble & 0x2) >> 1);
				spriteMatrix[i][7] = (byte) ((sNibble & 0x1));
				
			}
			copyInScreen(spriteMatrix, height, V[x], V[y]);
			drawFlag = true;
			break;
		case 0xE000:
			System.out.printf(">>>%04X<<<\n", opcode);
			x = getX();
			switch (opcode & 0x00FF) {
			
			// EX9E Skips the next instruction if the key stored in VX is
			// pressed.
			case 0x009E:
				System.out.printf("Skip if V[%X] = %04X  %c is pressed\n", x,  V[x], (char)gui.getExpectedKey(V[x]));
				if (keyState[V[x]] == 1)
					pc += 2;

				break;
			// EXA1 Skips the next instruction if the key stored in VX isn't
			// pressed.
			case 0x00A1:
				System.out.printf("Skip if V[%X] = %04X %c is pressed \n", x, V[x], (char) gui.getExpectedKey(V[x]));

				if (keyState[V[x]]== 0)
					pc += 2;

				break;
			default:
				System.err.printf("opcode %X: NOT KNOWN", opcode);
			}
			break;
		case 0xF000:
			x = getX();
			switch (opcode & 0x00FF) {
			// Sets VX to the value of the delay timer.
			case 0x0007:
				V[x] = delay_timer;
				break;
			// A key press is awaited, and then stored in VX.
			case 0x000A:
				Object o = new Object();
				System.out.println("WAIT A KEY PRESS!!!");
				gui.waitKeyPress(o);
				o.wait();
				V[x] = (short) gui.getLastKeyPressed();
				break;
			// FX15 Sets the delay timer to VX.
			case 0x0015:
				delay_timer = V[x];
				break;
			// Sets the sound timer to VX.
			case 0x0018:
				sound_timer = V[x];
				break;
			// Adds VX to I
			case 0x001E:
				I += V[x];
				break;
			// Sets I to the location of the sprite for the character in VX.
			// Characters 0-F (in hexadecimal) are represented by a 4x5 font.
			case 0x0029:
				x = getX();
				I = fontset_start + (V[x] * 5);
				System.out.printf("V[%X] = 0x%X ==> I = 0x%X ==> VAL = 0x%X\n", x, V[x], I, memory[I]);
				
				break;
			/*
			 * Stores the Binary-coded decimal representation of VX, with the
			 * most significant of three digits at the address in I, the middle
			 * digit at I plus 1, and the least significant digit at I plus 2.
			 * (In other words, take the decimal representation of VX, place the
			 * hundreds digit in memory at location in I, the tens digit at
			 * location I+1, and the ones digit at location I+2.)
			 */
			case 0x0033:
				memory[I + 2] = (short) (V[x] % 10);
				memory[I + 1] = (short) ((V[x] / 10) % 10);
				memory[I] = (short) ((V[x] / 100) % 10);

				break;
			// Stores V0 to VX in memory starting at address I.
			case 0x0055:
				for (int i = 0; i < x; i++)
					memory[I + i] = V[i];

				break;
			// Fills V0 to VX with values from memory starting at address I
			case 0x0065:
				for (int i = 0; i < x; i++)
					V[i] = memory[I + i];

				break;

			}

			break;
		default:
			System.err.println("Unknown opcode" + opcode);
		}
		
	}
	
	
	public void emulateCycle() throws Exception {
		opcode = memory[pc] << 8 | memory[pc+1];
		System.out.printf("PC: %03X: %04X\n",pc, opcode);
		pc += 2;
		executeOpcode();

		// Update timers
		if (delay_timer > 0)
			delay_timer--;
		if (sound_timer > 0) {
			if (sound_timer == 1){
				System.out.println("beep");
				beep();
			}
			--sound_timer;
		}
		
		

	}
	
	public void storeKeyState(){
		keyState = gui.getKeyState();
	}
	
	public void loadGameIntoMemory(String source) {
		Scanner s = new Scanner(source);
		String line;
		ArrayList<Short> l = new ArrayList<Short>();
		while (s.hasNextLine()) {
			line = s.nextLine();
			if (line.charAt(0) != ';') {
				line.replaceAll("\\s+", "");
				short first = Short.parseShort(line.substring(0, 2), 0x10);
				short second = Short.parseShort(line.substring(2, 4), 0x10);
				l.add(first);
				l.add(second);
			}
		}
		System.out.println();
		for (int i = 0; i < l.size(); i++) {
			memory[0x200 + i] = l.get(i);
			
		}
		last_index = 0x200 + l.size();
		for(int i = 0x200; i < last_index; i+= 2){
			System.out.printf("mem[%03X-%03X] = %02X%02X\n", i, i+1, memory[i], memory[i+1]);
		}
		
		s.close();

	}
	public byte[] getScreen() {
		return screen;
	}

	public boolean isFinished() {
		return pc == last_index || gui.isClosed();
	}

	private void printSituation() {
		System.out.printf("POS: %X: OP: %X\n",pc-2, opcode);
		for (int i = 0; i < V.length; i++) {
			System.out.printf("[%X]=%d", i, V[i]);
		}
	}

	public void repaint() {
		
		gui.printScreen(screen);
	}
	public void setToDraw(boolean val){
		this.drawFlag = val;
	}
	public boolean isToDraw(){
		return drawFlag;
	}
	public void printScreen(){
		for(int i = 0; i < screen.length; i++){
			System.out.printf("%d ", screen[i]);
			if(i != 0 && i % WIDTH == 0)
				System.out.println();
		}
	}
	public void beep(){
	
			try{
				File f = new File("./Sound/beep.wav");
			    System.out.println(f.getAbsolutePath());

			    AudioInputStream audioInputStream =
			        AudioSystem.getAudioInputStream(f);
			    Clip clip = AudioSystem.getClip();
			    clip.open(audioInputStream);
			    clip.start();
			}
			catch(Exception ex)
			{
				ex.printStackTrace(	);
			}
	}

}
