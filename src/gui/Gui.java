package gui;

import interfaces.InterpreterInterface;
import interpreter.factory.InterpreterFactory;
import interpreter.factory.SupportedInterpreter;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
		final JComboBox<SupportedInterpreter> combo = new JComboBox<SupportedInterpreter>(
				SupportedInterpreter.values());
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
				InterpreterInterface i = InterpreterFactory
						.createInterpreter((SupportedInterpreter) combo
								.getSelectedItem());

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

		JMenu exp = new JMenu("Export");
		exp.setMnemonic(KeyEvent.VK_M);

		JMenuItem exportCode = new JMenuItem("Export Code");
		exportCode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.exists() && !file.isDirectory())
						file = new File(file.getAbsolutePath());

					System.out.println(file.getAbsolutePath());
					System.out.println("Opening: " + file.getName());
					String content = contentArea.getText();
					if (combo.getSelectedItem() == SupportedInterpreter.CHIP8) {
						try {
							FileOutputStream fos = new FileOutputStream(file);
							Scanner s = new Scanner(content);
							while (s.hasNextLine()) {
								
								String line = s.nextLine();
								System.out.println(line);
								int val = Integer.parseInt(line, 0x10);
								System.out.println(val);
								byte[] b = new byte[4];
								b[0] = (byte) ((val & 0xF000) >> 12);
								b[1] = (byte) ((val & 0x0F00) >> 8);
								b[2] = (byte) ((val & 0x00F0) >> 4);
								b[3] = (byte) ((val & 0x000F));
								fos.write(b);
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else {

						PrintWriter ps;
						try {
							ps = new PrintWriter(file);
							ps.print(content);
							ps.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}

					}

				} else {
					System.out.println("Open command cancelled by user.");
				}

			}
		});

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
					if (combo.getSelectedItem() == SupportedInterpreter.CHIP8)
						notifyObservers(new NotifierObject(file, true));
					else
						notifyObservers(new NotifierObject(file, true));

				} else {
					System.out.println("Open command cancelled by user.");
				}
			}
		});

		imp.add(importCode);
		exp.add(exportCode);
		file.addSeparator(); // add line
		file.add(imp);
		file.addSeparator();
		file.add(exp);
		file.addSeparator();

		menubar.add(file);

		frame.setJMenuBar(menubar);

		Container pane = frame.getContentPane();
		pane.setLayout(new GridLayout(0, 2, 3, 3));
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
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JFrame frame = (JFrame) e.getSource();

				int result = JOptionPane.showConfirmDialog(frame,
						"Are you sure you want to exit the application?",
						"Exit Application", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION){
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
	
	}

	public void putText(String s) {

		contentArea.setText(s);

	}

	public void setVisible(boolean flag) {
		frame.setVisible(flag);
	}

}
