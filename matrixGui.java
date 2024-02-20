package calc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

public class matrixGui implements Runnable{
	
	private static int WIDTH = 500;
	private static int HEIGHT = 700;
	private JFrame frame = null;
	private JTextArea outputField = null;
	private JTextField aWidth = null;
	private JTextField aHeight = null;
	private JTextField aVal = null;
	private JTextField bWidth = null;
	private JTextField bHeight = null;
	private JTextField bVal = null;
	
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	private String serverName = "localhost";
	private int serverPort = 9898;
	private Socket socket;
	
	private String currentExpression = "";
	
	public matrixGui() {
		createMainFrame();
	}
	
	private void createMainFrame() {
		frame = new JFrame("Matrix Calculator");
		frame.setSize(matrixGui.WIDTH, matrixGui.HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2,1));

		JPanel matrixField = new JPanel();
		matrixField.setLayout(new GridLayout(2,1));
		JLabel sizeLabel = new JLabel("Matrix A size:");
		aWidth = new JTextField(4);
		JLabel xLabel = new JLabel("x");
		aHeight = new JTextField(4);
		JLabel valueLabelA = new JLabel("Value (space separated numbers):");
		JPanel valueLabelAPanel = new JPanel();
		valueLabelAPanel.add(valueLabelA);
		JPanel row1 = new JPanel();
		row1.add(sizeLabel);
		row1.add(aWidth);
		row1.add(xLabel);
		row1.add(aHeight);
		aVal = new JTextField();
		JPanel row2 = new JPanel(new BorderLayout());
		JPanel row2_2 = new JPanel(new BorderLayout());
		row2_2.add(valueLabelAPanel, BorderLayout.NORTH);
		row2_2.add(aVal, BorderLayout.CENTER);
		row2.add(row1, BorderLayout.NORTH);
		row2.add(row2_2, BorderLayout.CENTER);
		matrixField.add(row2);
		
		JLabel sizeBLabel = new JLabel("Matrix B size: ");
		bWidth = new JTextField(4);
		JLabel xBLabel = new JLabel("x");
		bHeight = new JTextField(4);
		JLabel valueLabelB = new JLabel("Value (space separated numbers):");
		JPanel valueLabelBPanel = new JPanel();
		valueLabelBPanel.add(valueLabelB);
		JPanel row3 = new JPanel();
		row3.add(sizeBLabel);
		row3.add(bWidth);
		row3.add(xBLabel);
		row3.add(bHeight);
		bVal = new JTextField();
		JPanel row4 = new JPanel(new BorderLayout());
		JPanel row4_2 = new JPanel(new BorderLayout());
		row4_2.add(valueLabelBPanel, BorderLayout.NORTH);
		row4_2.add(bVal, BorderLayout.CENTER);
		row4.add(row3, BorderLayout.NORTH);
		row4.add(row4_2, BorderLayout.CENTER);
		matrixField.add(row4);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(matrixField, BorderLayout.CENTER);
		JPanel buttPanel = new JPanel();
		buttPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton button_add = new JButton("Add");
		button_add.addActionListener(new buttonListener("Add"));
		buttPanel.add(button_add);
		JButton button_sub = new JButton("Subtract");
		button_sub.addActionListener(new buttonListener("Subtract"));
		buttPanel.add(button_sub);
		JButton button_mult = new JButton("Multiply");
		button_mult.addActionListener(new buttonListener("Multiply"));
		buttPanel.add(button_mult);
		JButton button_cl = new JButton("Clear");
		button_cl.addActionListener(new buttonListener("Clear"));
		buttPanel.add(button_cl);
		bottomPanel.add(buttPanel, BorderLayout.SOUTH);
		

		outputField = new JTextArea(10,10);
		outputField.setEditable(false);
		outputField.append("Welcome! Please connect to server to calculate.\n");
		outputField.append("Note:\n");
		outputField.append("Addition and subtraction can only be done on matrices of same size.\n");
		outputField.append("Multiplication of matrices mxp with pxn will result in mxn matrix.\n");
		JScrollPane scroll = new JScrollPane(outputField);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(scroll);
		frame.add(bottomPanel);

		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener((e) -> connect(serverName, serverPort));
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(connectItem);
		menu.add(exitItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		
		
		frame.setVisible(true);
		
	}
	
	
	private void connect(String serverName, int serverPort) {
		try {
		socket = new Socket("localhost", 9898);
		outputField.append("Connected\n");

	      // Create an input stream to receive data from the server
	      fromServer = new DataInputStream(socket.getInputStream());

	      // Create an output stream to send data to the server
	      toServer = new DataOutputStream(socket.getOutputStream());
	      Thread t= new Thread(this);
	      t.start();
		} catch (Exception e) {
			e.printStackTrace();
			outputField.append("Connection Failure\n");
		}
	
	}
	
	class buttonListener implements ActionListener {
		private String c;
		
		public buttonListener(String c) {
			this.c = c;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(c.equals("Add")) {
				int widthOfA = 0;
				int heightOfA = 0;
				int widthOfB = 0;
				int heightOfB = 0;
				
				try {
					widthOfA = Integer.parseInt(aWidth.getText());
					heightOfA = Integer.parseInt(aHeight.getText());
					widthOfB = Integer.parseInt(bWidth.getText());
					heightOfB = Integer.parseInt(bHeight.getText());
					
					if(widthOfA != widthOfB || heightOfA != heightOfB) {
						outputField.append("Error: dimension mismatch.\n");
						return;
					}
				} catch (NumberFormatException nfe) {
					outputField.append("Error: enter integers for size.\n");
					return;
				}
				
				String aV = aVal.getText();
				String bV = bVal.getText();
				String[] aParts = aV.split("\\s+");
				int count = aParts.length;
				
				if(count != (widthOfA*heightOfA)) {
					outputField.append("Error: count of values entered for A do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double a = Double.parseDouble(aParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for A.\n");
						return;
					}
					
				}
				
				String[] bParts = bV.split("\\s+");
				count = bParts.length;
				if(count != (widthOfB*heightOfB)) {
					outputField.append("Error: count of values entered for B do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double b = Double.parseDouble(bParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for B.\n");
						return;
					}
					
				}
				
				currentExpression += "A ";
				currentExpression += aWidth.getText() + " " + aHeight.getText() + " " + bWidth.getText() + " " + bHeight.getText() + " ";
				currentExpression += aVal.getText() + " ";
				currentExpression += bVal.getText();
				sendMessage();	
			} else if(c.equals("Subtract")) {
				int widthOfA = 0;
				int heightOfA = 0;
				int widthOfB = 0;
				int heightOfB = 0;
				
				try {
					widthOfA = Integer.parseInt(aWidth.getText());
					heightOfA = Integer.parseInt(aHeight.getText());
					widthOfB = Integer.parseInt(bWidth.getText());
					heightOfB = Integer.parseInt(bHeight.getText());
					
					if(widthOfA != widthOfB || heightOfA != heightOfB) {
						outputField.append("Error: dimension mismatch.\n");
						return;
					}
				} catch (NumberFormatException nfe) {
					outputField.append("Error: enter integers for size.\n");
					return;
				}
				
				String aV = aVal.getText();
				String bV = bVal.getText();
				String[] aParts = aV.split("\\s+");
				int count = aParts.length;
				
				if(count != (widthOfA*heightOfA)) {
					outputField.append("Error: count of values entered for A do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double a = Double.parseDouble(aParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for A.\n");
						return;
					}
					
				}
				
				String[] bParts = bV.split("\\s+");
				count = bParts.length;
				if(count != (widthOfB*heightOfB)) {
					outputField.append("Error: count of values entered for B do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double b = Double.parseDouble(bParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for B.\n");
						return;
					}
					
				}
				
				currentExpression += "S ";
				currentExpression += aWidth.getText() + " " + aHeight.getText() + " " + bWidth.getText() + " " + bHeight.getText() + " ";
				currentExpression += aVal.getText() + " ";
				currentExpression += bVal.getText();
				sendMessage();

			} else if(c.equals("Multiply")){
				int widthOfA = 0;
				int heightOfA = 0;
				int widthOfB = 0;
				int heightOfB = 0;
				
				try {
					widthOfA = Integer.parseInt(aWidth.getText());
					heightOfA = Integer.parseInt(aHeight.getText());
					widthOfB = Integer.parseInt(bWidth.getText());
					heightOfB = Integer.parseInt(bHeight.getText());
					
					if(heightOfA != widthOfB) {
						outputField.append("Error: dimension mismatch.\n");
						return;
					}
				} catch (NumberFormatException nfe) {
					outputField.append("Error: enter integers for size.\n");
					return;
				}
				
				String aV = aVal.getText();
				String bV = bVal.getText();
				String[] aParts = aV.split("\\s+");
				int count = aParts.length;
				
				if(count != (widthOfA*heightOfA)) {
					outputField.append("Error: count of values entered for A do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double a = Double.parseDouble(aParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for A.\n");
						return;
					}
					
				}
				
				String[] bParts = bV.split("\\s+");
				count = bParts.length;
				if(count != (widthOfB*heightOfB)) {
					outputField.append("Error: count of values entered for B do not match size.\n");
					return;
				}
				
				for(int i = 0; i < count; i++) {
					try {
						double b = Double.parseDouble(bParts[i]);
					} catch (NumberFormatException nfe) {
						outputField.append("Error: enter numbers for B.\n");
						return;
					}
					
				}
				
				currentExpression += "M ";
				currentExpression += aWidth.getText() + " " + aHeight.getText() + " " + bWidth.getText() + " " + bHeight.getText() + " ";
				currentExpression += aVal.getText() + " ";
				currentExpression += bVal.getText();
				sendMessage();

			} else if(c.equals("Clear")) {
				aWidth.setText("");
				aHeight.setText("");
				aVal.setText("");
				bWidth.setText("");
				bHeight.setText("");
				bVal.setText("");
			}
			
		}
	}
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				String inString = fromServer.readUTF();
				outputField.append(inString + "\n");	
			} catch (IOException e) {
				System.err.println("Cannot connect to server: " + e.getMessage());
				e.printStackTrace();
				socket = null;
				break;
			}
			
		}
		
	}
	
	
	private void sendMessage() {
		if (socket == null) {
			outputField.append("Cannot calculate anything, not connected.\n");
		} else {
			try {
			//toServer.writeUTF(textField.getText());
			toServer.writeUTF(currentExpression);
			toServer.flush();
			} catch (Exception e) {
				outputField.append("Error sending message\n");
			}
			
		}
		currentExpression = "";
	}
	
	
	
	
	public static void main(String[] args) {
		matrixGui matrixGui = new matrixGui();
	}

}
