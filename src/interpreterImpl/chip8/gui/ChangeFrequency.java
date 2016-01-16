package interpreterImpl.chip8.gui;

import interpreterImpl.chip8.implementation.Chip8;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class ChangeFrequency extends JMenuItem{
	private static final long serialVersionUID = 1L;
	public ChangeFrequency(String title, final Chip8 c, final int frequency) {
		super(title);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.setFrequency(frequency);
			}
		});
	}
}
