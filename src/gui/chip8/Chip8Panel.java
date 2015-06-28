package gui.chip8;

import interpreterImpl.chip8.Chip8;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class Chip8Panel extends JPanel{
	
	private static final long serialVersionUID = -1784075913477094190L;
	public static final byte SQUARE_SIZE = 15;
	
	byte [] screen;
	private Color background;
	private Color foreground;
	private Rectangle2D rec;
	private Graphics2D g2d;
	public Chip8Panel(){
		this(Color.BLACK, Color.WHITE);
	}
	
	

	public Chip8Panel(Color background, Color foreground){
		screen = new byte[Chip8.WIDTH*Chip8.HEIGHT];
		for(int i = 0; i < screen.length; i++){
			if(i >= screen.length/2)
				screen[i] = 1;
		}
		this.background = background;
		this.foreground = foreground;
		rec = new Rectangle2D.Double(0, 0, Chip8.WIDTH*SQUARE_SIZE, Chip8.HEIGHT*SQUARE_SIZE);
	}
	
	public void setScreen(byte [] s){
		this.screen = s;
	}
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScreen(g);
    }
	

	
	private void drawScreen(Graphics g) {
		g2d = (Graphics2D) g;
        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        
        
        g2d.setPaint(background);
        g2d.fill(rec);
        g2d.setPaint(foreground);

        for(int i = 0; i < screen.length; i++){
        	if(screen[i] == 1){
        		 int row = ((int)(i / Chip8.WIDTH))*SQUARE_SIZE;
        		 int col = (i % Chip8.WIDTH)*SQUARE_SIZE;
        		 g2d.fill(rec.createIntersection(new Rectangle2D.Double(col, 
        				 row, SQUARE_SIZE, SQUARE_SIZE)));
        	}
        }
              
        g2d.draw(rec);
    }
	public void setBackground(Color background) {
		this.background = background;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}
	
	
	public void myRepaint(){
		this.paintImmediately(0, 0, Chip8.WIDTH*SQUARE_SIZE, Chip8.HEIGHT*SQUARE_SIZE);
		//this.paintImmediately(rec.getBounds);
	}
	
}
