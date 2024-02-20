package calc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.swing.*;


public class server implements Runnable{
	
	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private JFrame frame = null;
	private JTextArea ta;
	private static int clientNo = 0;

	public server() {
		createMainFrame();
		Thread t = new Thread(this);
		t.start();
	}
	
	private void createMainFrame() {
		frame = new JFrame("Server");
		frame.setSize(server.WIDTH, server.HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ta = new JTextArea(40,30);
		frame.add(ta, BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
	        // Create a server socket
	        ServerSocket serverSocket = new ServerSocket(9898);
	        ta.append("Server started at " 
	          + new Date() + '\n');
	    
	        while (true) {
	          // Listen for a new connection request
	          Socket socket = serverSocket.accept();
	    
	          // Increment clientNo
	          clientNo++;
	          
	          ta.append("Starting thread for client " + clientNo +
	              " at " + new Date() + '\n');

	          // Find the client's host name, and IP address
	          InetAddress inetAddress = socket.getInetAddress();
	          ta.append("Client " + clientNo + "'s host name is "
	              + inetAddress.getHostName() + "\n");
	          ta.append("Client " + clientNo + "'s IP Address is "
	              + inetAddress.getHostAddress() + "\n");
	          
	          // Create and start a new thread for the connection
	          HandleAClient newClient = new HandleAClient(socket, clientNo);
	          new Thread(newClient).start();
	        }
	      }
	      catch(IOException ex) {
	        System.err.println(ex);
	      }
		
	}
	
	// Define the thread class for handling new connection
	class HandleAClient implements Runnable {
		private Socket socket; // A connected socket
		private int clientNum;
  
		/** Construct a thread */
		public HandleAClient(Socket socket, int clientNum) {
		    this.socket = socket;
		    this.clientNum = clientNum;
		}

		  
		/** Run a thread */
		public void run() {
		    try {
		      // Create data input and output streams
		      DataInputStream inputFromClient = new DataInputStream(
		        socket.getInputStream());
		      DataOutputStream outputToClient = new DataOutputStream(
		        socket.getOutputStream());

		      // Continuously serve the client
		      while (true) {
		        String message = inputFromClient.readUTF();
		        char check = message.charAt(0);
		        
		        String fin;
		        
		        if(check == 'A'|| check == 'S' || check == 'M') {
		        	fin = matrix(message);
		        } else {
		        	fin = calculate(message);
		        }

		        outputToClient.writeUTF(fin);
		      }
		    }
		    catch(IOException ex) {
		      //ex.printStackTrace();
		    }
		}
	}
		
		
	public static void main(String[] args) {
		server s = new server();
	}
	

	
	private String calculate(String s) {
		String fin = "Error";
		//Check for invalid input: when expression begins or ends with operator
		if(s.charAt(0) == '/' || s.charAt(0) == '*' || s.charAt(0) == '-' || s.charAt(0) == '+'
						|| s.charAt(s.length()-1) == '/' || s.charAt(s.length()-1) == '*' || s.charAt(s.length()-1) == '-' || s.charAt(s.length()-1) == '+') {
			fin = "Error: invalid expression. Please double check your input.";
			return fin;
		}
		
		//CONVERTING INFIX TO POSTFIX
		ArrayList<Object> exp = new ArrayList<>();
		StringBuilder opB = new StringBuilder();
		boolean negated = false;
		
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			if(c == '_') {
				if(i < s.length()-1 && Character.isDigit(s.charAt(i+1))) {
					negated = true;
					continue;
				} else {
					fin = "Error: negative needs to be right before a digit.";
					return fin;
				}
			}
			
			if((c == '.') || Character.isDigit(c)) {
				opB.append(c);
			} else {
				//We hit a operator
				if(opB.length() > 0) {
					//Adding the number we had to operands list
					double d = Double.parseDouble(opB.toString());
					if(negated) {
						d = -d;
						negated = false;
					}
					exp.add(d);
					opB.setLength(0);
				}
				
				exp.add(c);
			}
		}
		
		//In case the last character is a . or digit:
		if(opB.length() > 0) {
			double d = Double.parseDouble(opB.toString());
			if(negated)
				d = -d;
			exp.add(d);
		}
		
		
		ArrayList<Object> postFix = new ArrayList<>();
		Stack<Object> stack = new Stack<>();
		char temp1 = '(';
		char temp2 = ')';
		stack.push(temp1);
		exp.add(temp2);
		
		for(Object o: exp) {
			if(o instanceof Double) {
				postFix.add(o);
			} else {
				if((char)o == ')') {
					while(!stack.empty()) {
						Object top = stack.peek();
						if((char)top == '(') {
							break;
						} else {
							postFix.add(top);
							stack.pop();
						}
					}
					if(stack.empty()) {
						fin = "Error: wrong pairing from parenthesis.";
						return fin;
					} else {
						//pop the '(' still in the stack
						stack.pop();
					}
				} else {
					Object top = stack.peek();
					int pStack = precedence('s', top);
					int pInput = precedence('i', o);
					
					if(pInput > pStack) {
						stack.push(o);
					} else if(pInput == pStack) {
						postFix.add(top);
						stack.pop();
						stack.push(o);
					} else {
						//pInput < pStack
						
						while(pInput <= pStack && !stack.empty()) {
							postFix.add(top);
							stack.pop();
							top = stack.peek();
							pStack = precedence('s', top);
						}
						
						stack.push(o);
					}

				}

			}
		}
		
		//error checking
		if(!stack.empty()) {
			fin = "Error: double check your input. Error code: 1";
			return fin;
		}
		
		//debugging
		for(Object p: postFix) {
			System.out.print(p + " ");
		}
		System.out.println();
		
		
		//EVALUATING POSTFIX TO VALUE
		Stack<Object> eval = new Stack<>();
		for(Object o: postFix) {
			if(o instanceof Double) {
				eval.push(o);
			} else {
				if(eval.empty()) {
					fin = "Error: double check your input. Error code: 2";
					return fin;
				}
				Double rightNum = (Double)eval.pop();
				if(eval.empty()) {
					fin = "Error: double check your input. Error code: 3";
					return fin;
				}
				Double leftNum = (Double)eval.pop();
				
				Double res = 0.0;
				if((char)o == '+')
					res = leftNum + rightNum;
				else if((char)o == '-')
					res = leftNum - rightNum;
				else if((char)o == '*')
					res = leftNum * rightNum;
				else if((char)o == '/')
					res = leftNum / rightNum;
				
				eval.push(res);
				
			}
		}
		
		//error checking
		if(eval.size() != 1) {
			fin = "Error: double check your input. Error code: 4";
			return fin;	
		}
		
		if(!(eval.peek() instanceof Double)) {
			fin = "Error: double check your input. Error code: 5";
			return fin;	
		}
		
		Double result = (Double)eval.peek();
		fin = Double.toString(result);
		
		return fin;
		
	}
	
	
	private int precedence(char sOrI, Object op) {
		int fin = -1;
		if((char)op == '(') {
			if(sOrI == 's')
				fin = 0;
			else
				fin = 5;
		} else if((char)op == '+' || (char)op == '-') {
			if(sOrI == 's')
				fin = 2;
			else
				fin = 1;
		} else if((char)op == '*' || (char)op == '/') {
			if(sOrI == 's')
				fin = 4;
			else
				fin = 3;
		}
		
		return fin;
	}
	
	private String matrix(String s) {
		String fin = "";
		
		char check = s.charAt(0);
		s = s.substring(2);
		
		String[] parts = s.split("\\s+");
		int widthOfA = Integer.parseInt(parts[0]);
		int heightOfA = Integer.parseInt(parts[1]);
		int widthOfB = Integer.parseInt(parts[2]);
		int heightOfB = Integer.parseInt(parts[3]);
		
		matrixObj a = new matrixObj(widthOfA, heightOfA);
		matrixObj b = new matrixObj(widthOfB, heightOfB);
		
		int iterator = 4;
		
		for(int i = 0; i < widthOfA; i++) {
			for(int j = 0; j < heightOfA; j++) {
				double curVal = Double.parseDouble(parts[iterator]);
				iterator++;
				a.setVal(i, j, curVal);
			}
		}
		
		for(int i = 0; i < widthOfB; i++) {
			for(int j = 0; j < heightOfB; j++) {
				double curVal = Double.parseDouble(parts[iterator]);
				iterator++;
				b.setVal(i, j, curVal);
			}
		}
        
        if(check == 'A') {
        	matrixObj c = a.add(b);
        	
        	fin = c.toString();
        	
        } else if(check == 'S') {
        	matrixObj c = a.subtract(b);
        	
        	fin = c.toString();
        	
        } else if(check == 'M') {
        	matrixObj c = a.multiply(b);
        	
        	fin = c.toString();
        }
        
        return fin;
		
	}
	
	

}
