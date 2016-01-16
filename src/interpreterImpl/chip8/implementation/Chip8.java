package interpreterImpl.chip8.implementation;

import interpreterImpl.chip8.Chip8FamilyInterpreter;
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

public class Chip8 extends Chip8FamilyInterpreter{
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
	private byte round;
	private boolean drawFlag;
	private int fontset_start;
	private int last_index;
	private Chip8Gui gui;
	private boolean paused;
	public static final short MAX_STACK_SIZE = 0x10;
	private short[] chip8_fontset = { 0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
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
	private long cycleDurationTime;
	private short updateTimersAfterCycle;

	public Chip8() {
		initialize();
	}

	private Clip clip;
	private AudioInputStream audioInputStream;
	
	private void loadSound() {
		File f = new File("./Sound/beep.wav");
		System.out.println(f.getAbsolutePath());
		try {
			audioInputStream = AudioSystem
					.getAudioInputStream(f);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			System.err.println("Cannot find beep");
			;
		}
	}

	private void initialize() {
		loadSound();
		setFrequency(360);
		paused = false;
		round = 0;
		keyState = new byte[0x10];
		memory = new short[0x1000];
		pc = 0x200; // The first 512 Bytes are occupied by the interprter
		V = new short[0x10]; // The registers
		Arrays.fill(V, (short) 0);
		screen = new byte[64 * 32];
		stack = new Stack<Integer>();
		drawFlag = true;
		I = 0;
		opcode = 0;
		fontset_start = 0x050;
		for (int i = 0; i < chip8_fontset.length; i++) {
			memory[fontset_start + i] = chip8_fontset[i];
		}
		gui = new Chip8Gui(this);

		

	}

	public void setPaused() {
		paused = !paused;

	}

	public void setFrequency(int f) { // f = 60, 120, 180, 240, 300, 360Hz
		cycleDurationTime = (long) (1000000000 / f);
		updateTimersAfterCycle = (short) (f / 60);
	}

	private void copyInScreen(byte[][] sprite, short height, short X, short Y) {
		short actualPixel, actualY, actualX;
		short oldPixel, newPixel;
		V[0xF] = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < 8; j++) {
				actualY = (short) ((Y + i) & 0x1F);
				actualX = (short) ((X + j) & 0x3F);
				actualPixel = sprite[i][j];
				oldPixel = screen[actualY * WIDTH + actualX];

				screen[actualY * WIDTH + actualX] = (byte) ((screen[actualY
						* WIDTH + actualX] ^ actualPixel) & 0x1);
				newPixel = screen[actualY * WIDTH + actualX];
				// A PIXEL WAS ERASED
				if (oldPixel == 1 && newPixel == 0) {
					V[0xF] = 1;
				}

			}
		}
	}

	private short getX() {
		return (short) ((opcode & 0x0F00) >> 8);
	}

	private short getY() {
		return (short) ((opcode & 0x00F0) >> 4);
	}

	private short getNN() {
		return (short) (opcode & 0x00FF);
	}

	private int getNNN() {
		return (opcode & 0x0FFF);
	}

	

	private void updateTimers() {
		// Update timers
		if (delay_timer > 0)
			delay_timer--;
		if (sound_timer > 0) {
			System.out.println("beep");
			loadSound();
			--sound_timer;
		}
	}

	public void emulateCycle() throws Exception {
		opcode = memory[pc] << 8 | memory[pc + 1];
		System.out.printf("PC: %03X: %04X\n", pc, opcode);
		pc += 2;
		executeOpcode();

		if (round == 0)
			updateTimers();

		round = (byte) ((round + 1) % updateTimersAfterCycle);

	}

	public void storeKeyState() {
		keyState = gui.getKeyState();
	}

	public void mainLoop() {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				gui.setVisible(true);

			}
		});
		
		while (!this.isFinished()) {
			while (paused) {
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long start = System.nanoTime();
			try {
				this.emulateCycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isToDraw()) {
				repaint();
				setToDraw(false);
			}
			storeKeyState();

			try {
				// The cycle last 1s/240 (240 Hz)
				long toWait = cycleDurationTime - (System.nanoTime() - start);
				if (toWait > 0) {

					long millis = (long) (toWait / 1000000);
					long nano = (long) toWait % 1000000;

					Thread.sleep(millis, (int) nano);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
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
		for (int i = 0x200; i < last_index; i += 2) {
			System.out.printf("mem[%03X-%03X] = %02X%02X\n", i, i + 1,
					memory[i], memory[i + 1]);
		}

		for (int i = 0x200; i < last_index; i += 2) {
			opcode = memory[i] << 8 | memory[i + 1];
			if (getX() == 2)
				System.out.printf("X:::%04X\n", opcode);

			if (getY() == 2)
				System.out.printf("Y:::%04X\n", opcode);
			if ((opcode & 0xF0FF) == 0xF033)
				System.out.printf("Fx29:::%04X\n", opcode);

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
		System.out.printf("POS: %X: OP: %X\n", pc - 2, opcode);
		for (int i = 0; i < V.length; i++) {
			System.out.printf("[%X]=%d", i, V[i]);
		}
	}

	public void repaint() {

		gui.printScreen(screen);
	}

	public void setToDraw(boolean val) {
		this.drawFlag = val;
	}

	public boolean isToDraw() {
		return drawFlag;
	}

	public void printScreen() {
		for (int i = 0; i < screen.length; i++) {
			System.out.printf("%d ", screen[i]);
			if (i != 0 && i % WIDTH == 0)
				System.out.println();
		}
	}

	

	public String disassembly() {
		int index = 0x200;
		StringBuilder sb = new StringBuilder();
		short x, y;
		while (index < last_index) {
			opcode = memory[index] << 8 | memory[index + 1];
			sb.append("" + Integer.toHexString(index) + ": ");
			switch (opcode & 0xF000) {
			case 0x0000:
				switch (opcode & 0x00FF) {
				case 0x00E0:
					sb.append("CLS\n");
					break;
				case 0x00EE:
					sb.append("RET\n");
					break;
				default:
					sb.append("SYS " + Integer.toHexString(getNNN()) + "\n");
					break;
				}
				break;
			case 0x1000:
				sb.append("JP " + Integer.toHexString(getNNN()) + "\n");
				break;
			case 0x2000:
				sb.append("CALL " + Integer.toHexString(getNNN()) + "\n");
				break;
			case 0x3000:
				sb.append("SE V" + Integer.toHexString(getX()) + "," + Integer.toHexString(getNN()) + "\n");
				break;
			case 0x4000:
				sb.append("SNE V" + Integer.toHexString(getX()) + "," + Integer.toHexString(getNN()) + "\n");
				break;
			case 0x5000:
				sb.append("SE V" + Integer.toHexString(getX()) + ", V" + Integer.toHexString(getY()) + "\n");
				break;
			case 0x6000:
				sb.append("LD V" + Integer.toHexString(getX()) + ", " + Integer.toHexString(getNN()) + "\n");
				break;
			case 0x7000:
				sb.append("ADD V" + Integer.toHexString(getX()) + ", " + Integer.toHexString(getNN()) + "\n");
				break;
			case 0x8000:
				x = getX();
				y = getY();
				switch (opcode & 0x000F) {
				// Sets VX to the value of VY.
				case 0x0:
					sb.append("LD V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;
				// Sets VX to VX or VY.
				case 0x1:
					sb.append("OR V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;

				// Sets VX to VX and VY.
				case 0x2:
					sb.append("AND V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;
				// Sets VX to VX xor VY.
				case 0x3:
					sb.append("XOR V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;
				// Adds VY to VX. VF is set to 1 when there's a carry, and to 0
				// when
				// there isn't./
				case 0x4:
					sb.append("ADD V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;

				// VY is subtracted from VX. VF is set to 0 when there's a
				// borrow,
				// and 1 when there isn't. Vx = Vx - Vy
				case 0x5:
					sb.append("SUB V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;
				/*
				 * 8XY6 Store the value of register VY shifted right one bit in
				 * register VX Set register VF to the least significant bit
				 * prior to the shift
				 */
				case 0x6:
					sb.append("SHR V" + Integer.toHexString(x) + "{, V" + Integer.toHexString(y) + "}\n");
					break;
				// Sets VX to VY minus VX. VF is set to 0 when there's a borrow,
				// and
				// 1 when there isn't. Vx = Vy-Vx
				case 0x7:
					sb.append("SUBN V" + Integer.toHexString(x) + ", V" + Integer.toHexString(y) + "\n");
					break;
				// 8XYE Store the value of register VY shifted left one bit in
				// register VX
				// Set register VF to the most significant bit prior to the
				// shift
				case 0xE:
					sb.append("SHL V" + Integer.toHexString(x) + "{, V" + Integer.toHexString(y) + "}\n");
					break;
				default:
					System.err.printf("opcode %04X not known", opcode);
					System.exit(0);
				}

				break;
			// Skips the next instruction if VX doesn't equal VY.
			case 0x9000:
				sb.append("SNE V" + Integer.toHexString(getX()) + ", V" + Integer.toHexString(getY()) + "\n");
				break;

			// Sets I to the address NNN.
			case 0xA000:
				sb.append("LD I," + Integer.toHexString(getNNN()) + "\n");
				break;
			// Jumps to the address NNN plus V0.
			case 0xB000:
				sb.append("JP V0, " + Integer.toHexString(getNNN()) + "\n");
				break;
			case 0xC000:
				sb.append("RND V" + Integer.toHexString(getX()) + ", " + Integer.toHexString(getNN()) + "\n");

				break;
			// 0xDxyN -> V[x] V[y] N = height of the sprite
			case 0xD000:
				int height = opcode & 0xF;
				sb.append("DRW V" + Integer.toHexString(getX()) + ",V" + Integer.toHexString(getY()) + "," + height
						+ "\n");
				break;
			case 0xE000:
				x = getX();
				switch (opcode & 0x00FF) {

				case 0x009E:
					sb.append("SKP V" + Integer.toHexString(x) + "\n");

					break;
				// EXA1 Skips the next instruction if the key stored in VX isn't
				// pressed.
				case 0x00A1:
					sb.append("SKNP V" + x + "\n");

					break;
				default:
				}
				break;
			case 0xF000:
				x = getX();
				String x1 = Integer.toHexString(x);
				switch (opcode & 0x00FF) {
				// Sets VX to the value of the delay timer.
				case 0x0007:
					sb.append("LD V" + x1 + ", DT\n");
					break;
				// A key press is awaited, and then stored in VX.
				case 0x000A:
					sb.append("LD V" + x1 + ", K\n");
					break;
				// FX15 Sets the delay timer to VX.
				case 0x0015:
					sb.append("LD DT, V" + x1 + "\n");

					break;
				// Sets the sound timer to VX.
				case 0x0018:
					sb.append("LD ST, V" + x1 + "\n");

					break;
				// Adds VX to I
				case 0x001E:
					sb.append("ADD I, V" + x1 + "\n");

					break;
				// Sets I to the location of the sprite for the character in VX.
				// Characters 0-F (in hexadecimal) are represented by a 4x5
				// font.
				case 0x0029:
					sb.append("LD F, V" + x1 + "\n");

					break;
				/*
				 * Stores the Binary-coded decimal representation of VX, with
				 * the most significant of three digits at the address in I, the
				 * middle digit at I plus 1, and the least significant digit at
				 * I plus 2. (In other words, take the decimal representation of
				 * VX, place the hundreds digit in memory at location in I, the
				 * tens digit at location I+1, and the ones digit at location
				 * I+2.)
				 */
				case 0x0033:
					sb.append("LD B, V" + x1 + "\n");

					break;
				// Stores V0 to VX in memory starting at address I.
				case 0x0055:
					sb.append("LD [I], V" + x1 + "\n");

					break;
				// Fills V0 to VX with values from memory starting at address I
				case 0x0065:
					sb.append("LD V" + x1 + ", [I]\n");

					break;

				}

				break;
			default:
				System.err.println("Unknown opcode" + opcode);
			}
			index += 2;
		}
		return sb.toString();
	}

	@Override
	public void operation0() {
		switch (opcode & 0x00FF) {
		case 0x00E0: // 0x00E0: Clears the screen
			Arrays.fill(screen, (byte) 0);
			break;

		case 0x00EE: // 0x00EE: Returns from subroutine
			pc = stack.pop();
			break;
		// Ignore the other 0x0NNN Combinations: Calls RCA 1802 program at
		// address NNN.
		default:
			pc = getNNN();
		}		
	}

	@Override
	public void operation1() {
		pc = getNNN();
		
	}

	@Override
	public void operation2() {
	
		stack.push(pc);
		if (stack.size() > MAX_STACK_SIZE)
			throw new StackOverflowError();
		pc = getNNN();
	}

	

	

	@Override
	public void operation3() {
		short x = getX();
		short NN = getNN();
		if (V[x] == NN) {
			pc += 2;
		}		
	}

	@Override
	public void operation4() {
		short x = getX();
		short NN = getNN();
		if (V[x] != NN) {
			pc += 2;
		}		
	}

	@Override
	public void operation5() {
		short x = getX();
		short y = getY();
		if ((opcode & 0x000F) != 0) {
			System.err.printf("opcode %4X not known", opcode);
			System.exit(0);
		}
		if (V[x] == V[y])
			pc += 2;		
	}

	@Override
	public void operation6() {
		short x = getX();
		short NN = getNN();
		V[x] = NN;
		System.out.println("OP 6");
		
	}

	@Override
	public void operation7() {
		short x = getX();
		short NN = getNN();
		V[x] = (short) ((V[x] + NN) & 0xFF);		
	}

	@Override
	public void operation8() {
		short x = getX();
		short y = getY();
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
			V[x] = (short) (sum & 0xFF); // max 255 0xFF
			V[0xF] = (short) (sum >= 0x100 ? 1 : 0);
			break;
		// VY is subtracted from VX. VF is set to 0 when there's a borrow,
		// and 1 when there isn't. Vx = Vx - Vy
		case 0x5:
			V[0xF] = (short) (V[x] < V[y] ? 0 : 1);
			V[x] = (short) ((V[x] - V[y]) & 0xFF);
			break;
		/*
		 * 8XY6 Store the value of register VY shifted right one bit in
		 * register VX Set register VF to the least significant bit prior to
		 * the shift
		 */
		case 0x6:
			System.err.printf("WARNING: %4X", opcode);

			V[0xF] = (short) (V[y] & 0x01);
			V[x] = (short) ((V[y] >> 1) & 0xFF);

			break;
		// Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and
		// 1 when there isn't. Vx = Vy-Vx
		case 0x7:
			V[0xF] = (short) (V[x] > V[y] ? 0 : 1);
			V[x] = (short) ((V[y] - V[x]) & 0xFF);
			break;
		// 8XYE Store the value of register VY shifted left one bit in
		// register VX
		// Set register VF to the most significant bit prior to the shift
		case 0xE:
			System.err.printf("WARNING: %4X", opcode);

			V[0xF] = (short) ((V[y] & 0x80) >> 7); // 0x8 == 1000 -> most
													// significant bit
			V[x] = (short) ((V[y] << 1) & 0xFF);

			break;
		default:
			System.err.printf("opcode %04X not known", opcode);
			System.exit(0);
		}
	}

	@Override
	public void operation9() {
		short x = getX();
		short y = getY();
		if (V[x] != V[y])
			pc += 2;		
	}

	@Override
	public void operationA() {
		I = getNNN();		
	}

	@Override
	public void operationB() {
		pc = (getNNN() + V[0]) & 0xFFF;
		
	}

	@Override
	public void operationC() {
		short x = getX();
		short NN = getNN();
		short rand = (short) (Math.random() * 0x100);
		V[x] = (short) (rand & NN);
		
	}

	@Override
	public void operationD() {
		short x = getX();
		short y = getY();
		if (V[x] < 0 || V[y] < 0) {
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
	}

	@Override
	public void operationE() {
		System.out.printf(">>>%04X<<<\n", opcode);
		short x = getX();
		switch (opcode & 0x00FF) {

		// EX9E Skips the next instruction if the key stored in VX is
		// pressed.
		case 0x009E:
			System.out.printf("Skip if V[%X] = %04X  %c is pressed\n", x,
					V[x], (char) gui.getExpectedKey(V[x]));
			if (keyState[V[x]] == 1)
				pc += 2;

			break;
		// EXA1 Skips the next instruction if the key stored in VX isn't
		// pressed.
		case 0x00A1:
			System.out.printf("Skip if V[%X] = %04X %c is pressed \n", x,
					V[x], (char) gui.getExpectedKey(V[x]));

			if (keyState[V[x]] == 0)
				pc += 2;

			break;
		default:
			System.err.printf("opcode %X: NOT KNOWN", opcode);
		}
	}

	@Override
	public void operationF() throws InterruptedException  {
		short x = getX();
		int tmpI;
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
			synchronized (o) {
				o.wait();
			}
			V[x] = (short) gui.getLastKeyPressed();
			System.out.printf("V[%X] set to %2X", x, V[x]);
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
			I = ((I + V[x]) & 0xFFF);
			break;
		// Sets I to the location of the sprite for the character in VX.
		// Characters 0-F (in hexadecimal) are represented by a 4x5 font.
		case 0x0029:
			x = getX();
			I = fontset_start + (V[x] * 5);
			System.out.printf("V[%X] = 0x%X ==> I = 0x%X ==> VAL = 0x%X\n",
					x, V[x], I, memory[I]);

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
			System.out.println(V[x] + ":::" + memory[I] + ""
					+ memory[I + 1] + "" + memory[I + 2]);

			break;
		// Stores V0 to VX in memory starting at address I.
		case 0x0055:
			for (int i = 0; i <= x; i++) {
				tmpI = ((I + i) & 0xFFF);
				memory[tmpI] = V[i];
			}
			I = (I + x + 1) & 0xFFF;
			break;
		// Fills V0 to VX with values from memory starting at address I
		case 0x0065:
			for (int i = 0; i <= x; i++) {
				tmpI = ((I + i) & 0xFFF);
				V[i] = memory[tmpI];
			}
			I = (I + x + 1) & 0xFFF;
			System.out.printf("F%X65 = I = %03X", x, I);

			break;

		}

	}

}
