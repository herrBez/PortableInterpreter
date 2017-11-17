package interpreterImpl.chip8.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class ChangeColor extends JMenuItem {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public ChangeColor(String title, final Color foreground, final Color background, final Chip8Panel c8p) {
		super(title);
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				c8p.setBackground(background);
				c8p.setForeground(foreground);
			}
		});
	}
	
	public ChangeColor(String title, final Chip8Panel c8p) {
		super(title);
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
					Color background, foreground;
					
					background = new Color((int)(Math.random()*0xFFFFFF));
					foreground = new Color((int) (Math.random() * 0xFFFFFF));
					
					c8p.setBackground(background);
					c8p.setForeground(foreground);
					
			}
		});
	}
	
	public ChangeColor(String title, boolean psycho, Chip8Panel c8p) {
		super(title);
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				c8p.changePsycho();
			}
		});
	}


}
