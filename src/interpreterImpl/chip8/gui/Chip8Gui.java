package interpreterImpl.chip8.gui;

import interpreterImpl.chip8.implementation.Chip8;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class Chip8Gui  {
	private Integer key;
	private Chip8Panel board;
	private JFrame frame;
	private Object lock;
	private boolean waitKeyPress;
	private boolean closed;
	private boolean paused;
	private final short[] keyArray = { 49, 50, 51, 52, // 1,2,3,4
			81, 87, 69, 82, // q,w,e,r
			65, 83, 68, 70, // a,s,d,f
			89, 88, 67, 86 // y,x,c,v
	};
	private byte[] keyState;

	public Chip8Gui(Chip8 c) {
		paused = false;
		keyState = new byte[16];
		lock = null;
		frame = new JFrame();
		waitKeyPress = false;
		board = new Chip8Panel();
		closed = false;
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
				
			}

			@Override
			public void keyPressed(KeyEvent e) {

				key = e.getKeyCode();
				int i = getKeyIndex(key);
				if (i < keyArray.length) {
					keyState[i] = 1;
				} else {
					System.out.println("WARNING: Key " + key + " Not recognized");
					key = null;
				}
				if (waitKeyPress && key != null){
					
					synchronized(lock){
						System.out.println("HERE" + key);
						lock.notifyAll();
						waitKeyPress = false;
					}
				}

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
						"Close Chip8 Emulator", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION){
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					closed = true;
				}
			}
		});
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu changeColor = new JMenu("Change color");
		changeColor.setMnemonic(KeyEvent.VK_M);
		
		JMenu changeFrequency = new JMenu("Frequency");
		int freq = 60;
		for(int i = 0; i < 15; i++){
			changeFrequency.add(new ChangeFrequency(freq + "Hz", c, freq));
			changeFrequency.addSeparator();
			freq += 60;
		}
	

		

		
		
		changeColor.add(new ChangeColor("WHITE/BLACK", Color.WHITE, Color.BLACK, board));
		changeColor.addSeparator();
		changeColor.add(new ChangeColor("GREEN/BLACK", Color.GREEN, Color.BLACK, board));
		changeColor.addSeparator();
		changeColor.add(new ChangeColor("BLACK/WHITE", Color.BLACK, Color.WHITE, board));
		changeColor.addSeparator();		
		changeColor.add(new ChangeColor("BLUE/WHITE", Color.BLUE, Color.WHITE, board));
		changeColor.addSeparator();
		changeColor.add(new ChangeColor("BLUE/BLACK", Color.BLUE, Color.BLACK, board));
		changeColor.addSeparator();	
		changeColor.add(new ChangeColor("RANDOM", board));
		changeColor.addSeparator();
		changeColor.add(new ChangeColor("PSYCHO", true, board));
		changeColor.addSeparator();

		
		JMenuItem pauseItem = new JMenuItem("PAUSE");
	
		pauseItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				c.setPaused();
			}
		});
		menubar.add(changeColor);
		menubar.add(pauseItem);
		menubar.add(changeFrequency);
		
		frame.setJMenuBar(menubar);
		

	}
	
	
	public boolean isPaused(){
		return paused;
	}
	
	public boolean isClosed(){
		return closed;
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
		waitKeyPress = true;
		lock = o;
	}

	
	public static void main(String[] args) throws InterruptedException {
		Chip8Gui g = new Chip8Gui(new Chip8());
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
