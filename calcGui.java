package calc;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

public class calcGui implements Runnable{
	
	private static int WIDTH = 500;
	private static int HEIGHT = 700;
	private JFrame frame = null;
	private JTextField textField = null;
	private JTextArea outputField = null;
	private String currentExpression = "";
	private String ans = "0";
	
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	private String serverName = "localhost";
	private int serverPort = 9898;
	private Socket socket;
	
	
	public calcGui() {
		createMainFrame();
	}
	
	private void createMainFrame() {
		frame = new JFrame("Calculator");
		frame.setSize(calcGui.WIDTH, calcGui.HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2,1));
		JPanel buttonsAndInput = new JPanel();
		buttonsAndInput.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5,4));
		buttonsAndInput.add(panel, BorderLayout.CENTER);
		textField = new JTextField();
		textField.setEditable(false);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		buttonsAndInput.add(textField, BorderLayout.NORTH);
		JPanel clearButton = new JPanel();
		buttonsAndInput.add(clearButton, BorderLayout.SOUTH);
		outputField = new JTextArea(10,10);
		outputField.setEditable(false);
		outputField.append("Welcome! Please connect to server to calculate.\n");
		outputField.append("Note:\n");
		outputField.append("Use the Neg button instead of the '-' button to define a negative number.\n");
		JScrollPane scroll = new JScrollPane(outputField);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(scroll);
		frame.add(buttonsAndInput);
		
		
		JButton button_d = new JButton("Del");
		button_d.addActionListener(new buttonListener("Del"));
		clearButton.add(button_d);
		JButton button_c = new JButton("Clear");
		button_c.addActionListener(new buttonListener("Clear"));
		clearButton.add(button_c);
		JButton button_op = new JButton("(");
		button_op.addActionListener(new buttonListener("("));
		panel.add(button_op);
		JButton button_cp = new JButton(")");
		button_cp.addActionListener(new buttonListener(")"));
		panel.add(button_cp);
		JButton button_n = new JButton("Neg");
		button_n.addActionListener(new buttonListener("Neg"));
		panel.add(button_n);
		JButton button_dd = new JButton("/");
		button_dd.addActionListener(new buttonListener("/"));
		panel.add(button_dd);
		JButton button_7 = new JButton("7");
		button_7.addActionListener(new buttonListener("7"));
		panel.add(button_7);
		JButton button_8 = new JButton("8");
		button_8.addActionListener(new buttonListener("8"));
		panel.add(button_8);
		JButton button_9 = new JButton("9");
		button_9.addActionListener(new buttonListener("9"));
		panel.add(button_9);
		JButton button_m = new JButton("*");
		button_m.addActionListener(new buttonListener("*"));
		panel.add(button_m);
		JButton button_4 = new JButton("4");
		button_4.addActionListener(new buttonListener("4"));
		panel.add(button_4);
		JButton button_5 = new JButton("5");
		button_5.addActionListener(new buttonListener("5"));
		panel.add(button_5);
		JButton button_6 = new JButton("6");
		button_6.addActionListener(new buttonListener("6"));
		panel.add(button_6);
		JButton button_mm = new JButton("-");
		button_mm.addActionListener(new buttonListener("-"));
		panel.add(button_mm);
		JButton button_1 = new JButton("1");
		button_1.addActionListener(new buttonListener("1"));
		panel.add(button_1);
		JButton button_2 = new JButton("2");
		button_2.addActionListener(new buttonListener("2"));
		panel.add(button_2);
		JButton button_3 = new JButton("3");
		button_3.addActionListener(new buttonListener("3"));
		panel.add(button_3);
		JButton button_p = new JButton("+");
		button_p.addActionListener(new buttonListener("+"));
		panel.add(button_p);
		JButton button_0 = new JButton("0");
		button_0.addActionListener(new buttonListener("0"));
		panel.add(button_0);
		JButton button_pp = new JButton(".");
		button_pp.addActionListener(new buttonListener("."));
		panel.add(button_pp);
		JButton button_ans = new JButton("Prev. Ans");
		button_ans.addActionListener(new buttonListener("Prev. Ans"));
		panel.add(button_ans);
		JButton button_e = new JButton("=");
		button_e.addActionListener(new buttonListener("="));
		panel.add(button_e);
		
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
			String currentText = textField.getText();
			if(c.equals("Clear")) {
				textField.setText("");
				currentExpression = "";
			} else if(c.equals("Del")) {
				if(currentExpression.length() > 0) {
					String delCurrentExpression = currentExpression.substring(0, currentExpression.length()-1);
					currentExpression = delCurrentExpression;
					String delTextField = currentText.substring(0, currentText.length()-1);
					textField.setText(delTextField);
				}
			} else if(c.equals("Neg")) {
				currentExpression = currentExpression + "_";
				String addedText = currentText + "-";
				textField.setText(addedText);
			} else if(c.equals("=")) {
				if(currentExpression.length() > 0) {
					outputField.append(currentText + " =\n");
					sendMessage();	
				}
			} else if(c.equals("Prev. Ans")) {
				currentExpression = currentExpression + ans;
				String addedText = currentText + ans;
				textField.setText(addedText);
			} else {
				currentExpression = currentExpression + c;
				String addedText = currentText + c;
				textField.setText(addedText);
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
				if(Character.isDigit(inString.charAt(0))) {
					ans = inString;
				}
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
		textField.setText("");
		currentExpression = "";
	}
	
	
	public static void main(String[] args) {
		calcGui calcGui = new calcGui();
	}


}
