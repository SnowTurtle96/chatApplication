/**
 * 
 */

/**
 * @author Jamie
 *
 */


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class client extends JFrame implements ActionListener
{
	
	 private DataOutputStream output;
	  private DataInputStream input;
	  private String message = "";
	  private String host;
	  private int port;
	  private boolean clientRunning;
	  public static final int BUFFSZ = 80;
	  public static final String fieldSep = "#";
	  public static final String endInst = ";";
	  public static final String endMkr = ">";
	  public JButton getBtn, catBtn, logBtn, regBtn, newBtn, uptBtn, delBtn, abortBtn, connectBtn;
	  public JTextArea display, textEntry;
	  public JTextField hostEntry, portEntry;
	  public JLabel labelHost, labelPort;
	  

	  public client() {
	    super("Scoreboard Client");
	    clientRunning = true;
	    getBtn = new JButton("Get information for a specific tour");       getBtn.addActionListener(this); 
	    catBtn = new JButton("See our full catalogue of tours");      catBtn.addActionListener(this); 
	    logBtn = new JButton("Sign in to our server");     logBtn.addActionListener(this); 
	
	    abortBtn = new JButton("Terminate session");      abortBtn.addActionListener(this); 
	    connectBtn = new JButton("Connect"); connectBtn.addActionListener(this); 
	    display = new JTextArea(10,43);
	    hostEntry = new JTextField();
	    portEntry = new JTextField();
	    labelHost = new JLabel("IP address");
	    labelPort = new JLabel("Port");
	    textEntry = new JTextArea(5, 43);
	    JPanel p1 = new JPanel();
	    JPanel p2 =  new JPanel();
	    JPanel p3 = new JPanel();
	    
	   
	 
	    p1.setLayout(new GridLayout(4,2));
	  
	    add(p1, BorderLayout.CENTER); //default JFrame layout
	    add(new JScrollPane(display), BorderLayout.CENTER);
	    
	    p2.setLayout(new GridLayout(1,5));
	    p2.add(labelHost);
	    p2.add(hostEntry);
	    p2.add(labelPort);
	    p2.add(portEntry);
	    p2.add(connectBtn);
	    p3.add(textEntry);
	    display.setEditable(false);
	    add(p2,BorderLayout.NORTH);
	    add(p3, BorderLayout.SOUTH);
	  

	    setSize(500, 600);
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //Do nothing on close
	  }
	
	
	

	
	
	
	
	//Helper Functions
	

	  // Convert a dotted-decimal string to an array of bytes
	  byte[] decDD(String ddStr) {
	    if (!ddStr.contains(".")) 
	      return null;
	    String[] cpts = ddStr.split("\\.");
	    byte[] b = new byte[cpts.length];
	    try {
	      for (int i=0; i<cpts.length; i++)
	        b[i] = Byte.parseByte(cpts[i]);
	    } catch(NumberFormatException ex) {
	      System.err.println("Invalid IP address string");
	      return null;
	    }
	    return b;
	  }
	  
	  public void runClient()    {
		    Socket client;
		    System.out.println("works");
		    try {
		      // Step 1: Create a Socket to make connection.
		      byte[] b = decDD(host);
		      if (b != null) {
		        client = new Socket(InetAddress.getByAddress(b), port);
		      } else {
		        client = new Socket(InetAddress.getByName(host), port);
		      }
		      System.out.println("Connected to: "
		            + client.getInetAddress().getHostName());

		      // Step 2: Get the input and output streams.
		      output = new DataOutputStream(client.getOutputStream());
		      output.flush();
		      input = new DataInputStream(client.getInputStream());
		      System.out.println("Got I/O streams");

		      // Step 3: Process connection.
		      byte[] buff = new byte[BUFFSZ];
		      int len;
		      do {
		        len = input.read(buff);
		        if (len > 0)
		          System.out.println(new String(buff, 0, len));
		      } while (clientRunning);

		      // Step 4: Close connection.
		      System.out.println("\nClosing connection.");
		      output.close();
		      input.close();
		      client.close();
		      System.out.println("Closing connection.");
		    }
		    catch (EOFException eof) {
		      System.out.println("Server terminated connection");
		    }
		    catch (Exception e) { //IOException or ConnectException
		      System.err.println(e);
		    }
		  }

 //Process commands from button clicks ...
		  
	  public void actionPerformed(ActionEvent evt) {
		  
		  
		    String cmdStr = null, inputStr = null, inputStr2 = null, inputStr3 = null;
		    Object src = evt.getSource();
		    
		    if(src == connectBtn)
			  {
		    	Thread t = new Thread(new Runnable() {
		            public void run()
		            {
		            	 host = hostEntry.getText();
						  String portString = portEntry.getText();
						  port = Integer.parseInt(portString);
						  System.out.println(host);
					      runClient();
		            }
		   });		
			    t.start();

			  }
		    
		    if (src == getBtn) {
			inputStr = JOptionPane.showInputDialog("Tour:");
            cmdStr = "GET"+endInst+fieldSep+inputStr;
		    }
		    else if (src ==  catBtn) {
		      cmdStr = "CAT"+endInst;
		    } 
		    else if (src ==  logBtn) {
		      inputStr = JOptionPane.showInputDialog("USERNAME:");
		      if (inputStr == null) return;
		      cmdStr = "USER"+endInst+fieldSep+inputStr;
		    }
		    else if (src ==  regBtn) {
		      inputStr = JOptionPane.showInputDialog("USERNAME:");
		      if (inputStr == null) return;
		      inputStr2 = JOptionPane.showInputDialog("PASSWORD:");
		      if (inputStr2 == null) return;
		      cmdStr = "REG"+ endInst+fieldSep+inputStr+fieldSep+inputStr2;
		    }
		    else if (src ==  newBtn) {
		      inputStr = JOptionPane.showInputDialog("Tour Name:");
		      if (inputStr == null) return;
		      inputStr2 = JOptionPane.showInputDialog("Cost:");
		      if (inputStr2 == null) return;
		      inputStr3 = JOptionPane.showInputDialog("Min To Run:");
              if (inputStr3 == null) return;

              cmdStr = "NEW"+endInst+fieldSep+inputStr+fieldSep+inputStr2+fieldSep+inputStr3;
		    }
		  
		    else if (src == abortBtn) {
		      cmdStr = "ABORT"+endInst;
		      clientRunning = false;
		    }
		

		    if (cmdStr == null) return;
		    try {
		      output.writeBytes(cmdStr);  //send
		        System.out.printf("%d bytes sent\n", cmdStr.length());
		    } 
		    catch (IOException ex) {
		      System.err.println("Error writing object");
		    }
		    display.setCaretPosition(display.getText().length());
		  } // actionPerformed()

		  public static void main(String args[])   {
		    client app;
		    
		     
		      app = new client();
		    }
		  } // main
		 //class



	
	
	
	
	
	
