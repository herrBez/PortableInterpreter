package gui;


import interfaces.InterpreterInterface;
import interpreter.factory.InterpreterFactory;
import interpreter.factory.SupportedInterpreter;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Gui extends Observable {
	
	private JFrame frame;
	private JTextArea contentArea;
	private JTextArea inputArea;
	private JTextArea errorArea;
	private JLabel result;
	
	public Gui(String windowTitle) {
		initUI(windowTitle);
	}
	
	private void initUI(String windowTitle) {
		frame = new JFrame(windowTitle);
		final JFileChooser fc = new JFileChooser();
		final JLabel interpreterLabel = new JLabel("Choose Interpreter");
		final JLabel writeLabel = new JLabel("Write The code or Import one");
		final JLabel outputLabel = new JLabel("Output:");
		final JLabel inputLabel = new JLabel("Input:");
		final JComboBox<SupportedInterpreter> combo = new JComboBox<SupportedInterpreter>(SupportedInterpreter.values());
		final JButton executeButton = new JButton("Excute");
		final JButton cleanButton = new JButton("Clean All");
		final JLabel errorLabel = new JLabel("ErrorLog");
		result = new JLabel();
		inputArea = new JTextArea(20, 50);
		inputArea.setLineWrap(true);
		contentArea = new JTextArea(20, 50);
		contentArea.setLineWrap(true);
		errorArea = new JTextArea(20, 50);
		errorArea.setLineWrap(true);
		executeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				InterpreterInterface i = InterpreterFactory.createInterpreter((SupportedInterpreter)combo.getSelectedItem());
				
				i.interpret(contentArea.getText(), inputArea.getText());
				System.out.println(i.getOutput());
				result.setText(i.getOutput());
				errorArea.setText(i.getErrorMessage());
				
			}
		});
		
		cleanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				result.setText("");
				errorArea.setText("");
				inputArea.setText("");
				contentArea.setText("");
			}
		});
		
		
		

		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenu imp = new JMenu("Open");
		imp.setMnemonic(KeyEvent.VK_M);
		
		JMenuItem importCode = new JMenuItem("Import Code");
		importCode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(frame);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                System.out.println(file.getAbsolutePath());
		                System.out.println("Opening: " + file.getName());
		                System.out.println("I will notify the observer");
		               
		                setChanged();
		                if(combo.getSelectedItem() == SupportedInterpreter.CHIP8)
		                	notifyObservers(new NotifierObject(file, true));
		                else
		                	notifyObservers(new NotifierObject(file, true));

		                
		                
		            } else {
		                System.out.println("Open command cancelled by user." );
		            }
			}
		});

		imp.add(importCode);
		
		file.addSeparator(); // add line
		file.add(imp);
		file.addSeparator();

		menubar.add(file);
		
		frame.setJMenuBar(menubar);
	
		Container pane =  frame.getContentPane();
		pane.setLayout(new GridLayout(0, 2 , 3, 3));
		pane.add(interpreterLabel);
		pane.add(combo);
		pane.add(writeLabel);
		JScrollPane contentAreaPane = new JScrollPane(contentArea);
		pane.add(contentAreaPane);
		pane.add(inputLabel);
		JScrollPane inputAreaPane = new JScrollPane(inputArea);

		pane.add(inputAreaPane);
		pane.add(executeButton);
		pane.add(cleanButton);
		pane.add(outputLabel);
		pane.add(errorLabel);
		JScrollPane errorPane = new JScrollPane(errorArea);
		pane.add(errorPane);
		pane.add(result);
		
		frame.setTitle(windowTitle);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public void putText(String s){
		
			contentArea.setText(s);
		
	}
	
	public void setVisible(boolean flag){
		frame.setVisible(flag);
	}

	
}
