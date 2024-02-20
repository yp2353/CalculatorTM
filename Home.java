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
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

public class Home{
	
	private static int WIDTH = 600;
	private static int HEIGHT = 250;
	
	public Home() {
		JFrame frame = new JFrame("Home");
		frame.setSize(Home.WIDTH, Home.HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2,1));
		
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
	    JLabel message1 = new JLabel("CalculatorTM");
	    message1.setFont(new Font("Serif", Font.ITALIC, 48));  
	    jp.add(message1, BorderLayout.SOUTH);
	    message1.setHorizontalAlignment(SwingConstants.CENTER);
	    JLabel message2 = new JLabel("by Alicia Pan");
	    message2.setFont(new Font("SansSerif", Font.PLAIN, 15));  
	    message2.setHorizontalAlignment(SwingConstants.CENTER);
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new GridLayout(2,1));
	    bottomPanel.add(message2);
	    JPanel butts = new JPanel();
	    JButton serverButt = new JButton("Server");
	    serverButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new server();
			}
	    });
	    butts.add(serverButt);
	    JButton calcButt = new JButton("Simple Calculator");
	    calcButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new calcGui();
			}
	    });
	    butts.add(calcButt);
	    JButton matButt = new JButton("Matrix Calculator");
	    matButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new matrixGui();
			}
	    });
	    butts.add(matButt);
	    bottomPanel.add(butts);
	    frame.add(jp);
	    frame.add(bottomPanel);
	    frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Home h = new Home();
	}

}
