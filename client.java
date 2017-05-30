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
	  JButton send;
	public JButton connectBtn;
	  public JTextArea display, textEntry;
	  public JTextField hostEntry, portEntry;
	  public JLabel labelHost, labelPort;
	  

	  public client() {
	    super("Messenger Application");
	    clientRunning = true;
	 
	   
	    connectBtn = new JButton("Connect"); connectBtn.addActionListener(this); 
	    display = new JTextArea(10,43);
	    hostEntry = new JTextField();
	    portEntry = new JTextField();
	    labelHost = new JLabel("IP address");
	    labelPort = new JLabel("Port");
	    send = new JButton("Send"); send.addActionListener(this); 
	    textEntry = new JTextArea(5, 43);
	    JPanel p1 = new JPanel();
	    JPanel p2 =  new JPanel();
	    JPanel p3 = new JPanel();
	    
	   
	 
	    p1.setLayout(new GridLayout(4,2));
	    p3.setLayout(new BorderLayout());
	  
	    add(p1, BorderLayout.CENTER); //default JFrame layout
	    add(new JScrollPane(display), BorderLayout.CENTER);
	    
	    p2.setLayout(new GridLayout(1,5));
	    p2.add(labelHost);
	    p2.add(hostEntry);
	    p2.add(labelPort);
	    p2.add(portEntry);
	    p2.add(connectBtn);
	    p3.add(textEntry);
	     p3.add(send, BorderLayout.EAST);
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
	        try {
	            // Step 1: Create a Socket to make connection.
	            display.setText(String.format("Attempting connection to %s\n", host));
	            client = new Socket(InetAddress.getByName(host), port);

	            display.append("Connected to: "
	                + client.getInetAddress().getHostName());

	            // Step 2: Get the input and output streams.
	            output = new DataOutputStream(client.getOutputStream());
	            output.flush();
	            input = new DataInputStream(client.getInputStream());
	            display.append( "\nGot I/O streams\n" );

	            // Step 3: Process connection.
	            byte[] buff = new byte[BUFFSZ];
	            int len;
	            String recStg;

	            recStg = new String();
	            do {
	                len = input.read(buff);

	                recStg +=  new String(buff, 0, len);
	                System.out.println(recStg);
	                try{
	                    Thread.sleep(150);
	                }
	                catch(InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }
	                if (len > 1 && recStg.contains(">"))
	                {
		                recStg = recStg.replaceAll(">","");

	                    display.append(recStg);
	                    recStg = new String();

	                }

	            }while(clientRunning);

	            // Step 4: Close connection.
	            if(clientRunning == false)
	            {
	                display.append("\nClosing connection.");
	                output.close();
	                input.close();
	                client.close();
	                dispose();
	            }

	        }
	        catch (EOFException eof) {
	            display.append("\nServer terminated connection");
	        }
	        catch (Exception e) { //may be an IOException or a ConnectException
	            //System.err.println(e);
	            e.printStackTrace();
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
		    try{
		    if(src == send)
		    {
		    	if(output == null)
		    	{
		    	display.setText("Not Connected");
                textEntry.setText("");

		    	}
		    	
		    	else
		    	{
                cmdStr = textEntry.getText() + endMkr;
                textEntry.setText("");
                output.writeBytes(cmdStr);
                output.flush();
		    	}
		    }
		    

		   
		    }
		    catch (IOException cnfex) {
	            display.append("\nError writing object");
	        }
	        display.setCaretPosition(display.getText().length());
	    }
		   // actionPerformed()

		  public static void main(String args[])   {
		    client app;
		    
		     
		      app = new client();
		    }
		  } // main
		 //class



	
	
	
	
	
	
