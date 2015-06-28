package gui.chip8;

import interpreterImpl.chip8.Chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Chip8Gui  {
	private Integer key;
	private Chip8Panel board;
	private JFrame frame;
	private Object lock;
	private boolean waitKeyPress;
	private final short[] keyArray = { 49, 50, 51, 52, // 1,2,3,4
			81, 87, 69, 82, // q,w,e,r
			65, 83, 68, 70, // a,s,d,f
			89, 88, 67, 86 // y,x,c,v
	};
	private byte[] keyState;

	public Chip8Gui() {
		keyState = new byte[16];
		lock = null;
		frame = new JFrame();
		waitKeyPress = false;
		board = new Chip8Panel();
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			private int getKeyIndex(int key) {

				int i;
				for (i = 0; i < keyArray.length; i++)
					if (keyArray[i] == key)
						break;
				return i;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				key = e.getKeyCode();
				int i = getKeyIndex(key);
				if (i < keyArray.length) {
					keyState[i] = 0;
				}
				if (waitKeyPress)
					lock.notify();

			}

			@Override
			public void keyPressed(KeyEvent e) {

				key = e.getKeyCode();
				int i = getKeyIndex(key);
				if (i < keyArray.length) {
					keyState[i] = 1;
				}
				if (waitKeyPress)
					lock.notify();

			}
		});

		frame.add(board);
		frame.setTitle("Chip8 Emulator");
		frame.setLocationRelativeTo(null);
		frame.setSize(Chip8.WIDTH * Chip8Panel.SQUARE_SIZE, Chip8.HEIGHT
				* Chip8Panel.SQUARE_SIZE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JFrame frame = (JFrame) e.getSource();

				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to exit the application?",
						"Exit Application", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION)
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	public Chip8Gui(Observable o) {
		this();
		

	}

	public byte[] getKeyState() {
		return keyState;
	}

	public int getLastKeyPressed() {
		return key == null ? 0 : key;
	}

	public short getExpectedKey(int index) {
		return keyArray[index];
	}

	public byte getKeyState(int index) {
		return keyState[index];
	}

	public void printScreen(byte[] screen) {
		board.setScreen(screen);
		board.myRepaint();
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public void waitKeyPress(Object o) {
		waitKeyPress = false;
		lock = o;
	}

	
	public static void main(String[] args) throws InterruptedException {
		Chip8Gui g = new Chip8Gui();
		g.setVisible(true);
		byte[] screen = new byte[64 * 32];
		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < screen.length; i++) {
				screen[i] = (byte) ((Math.random() * 100) % 2);
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					g.printScreen(screen);
				}
			}).start();
			Thread.sleep(500);
		}
	}

}
