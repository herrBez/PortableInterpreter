package gui.chip8;

import interpreterImpl.chip8.Chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

public class Chip8Gui implements Observer{
	private Integer key;
	private Chip8Panel board;
	private JFrame frame;
	
	
	public Chip8Gui(){
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		board = new Chip8Panel();
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				key = null;
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyChar() + " :: " + e.getKeyCode());	
				key = e.getKeyCode();
			}
		});
		
		frame.add(board);
		frame.setTitle("Chip8 Emulator");
		frame.setLocationRelativeTo(null);
		frame.setSize(640,320);
	}
	public Chip8Gui(Observable o){
		this();
		o.addObserver(this);
		
	}
	
	public int getKeyPressed(){
		return (key == null?0:key);
	}
	public void printScreen(byte [] screen){
		board.setScreen(screen);
		board.repaint();
	}
	
	public void setVisible(boolean b){
		frame.setVisible(b);
	}

	@Override
	public void update(Observable o, Object arg) {
		byte[] screen = (byte[]) arg;
		printScreen(screen);
		System.out.println("NOTI");
	}
	public static void main(String[] args) {
		Chip8Gui g = new Chip8Gui();
		g.setVisible(true);
		byte [] screen = new byte[Chip8.WIDTH*Chip8.HEIGHT];
	
		for(int i = 0; i < screen.length; i++){
			screen[i] = (byte) ((i%2==0)?1:0);
			System.out.println(screen[i]);
		}
		g.printScreen(screen);
	}
}
