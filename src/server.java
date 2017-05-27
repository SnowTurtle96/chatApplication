/**
 * 
 */

/**
 * @author Jamie
 *
 */
/**
 * 
 */

/**
 * @author W14017307
 *
 */
/* ScoreBdServer.java - server program to manage a scoreboard in response to 
 *   commands from clients (see readme.txt). 
 *
 * Multithreaded - each client is handled by its own thread. 
 *   Methods which may concurrently modify shared data are declared 'synchronized'
 *
 * Shutdown protocol: 
 *   A logged-in client should send a "down" command, then terminate;
 *   Another client will then be able to connect and terminate;
 *   The server will then be EoJ
 *  
 * MJB Mar 15
 */
import java.io.*;   // DataInput/OutputStream, etc
import java.net.*;  // Socket, ServerSocket, ...
import java.util.*; // ArrayList, Scanner, ...

public class server {
	 class Users {
		    String userNm;
		    String pswd;
	 }
	 
	 //Array lists and special character declarations
	  public static final int BUFFSZ = 80;
	  public static final String fieldSep = "#";
	  public static final String endInst = ";";
	  private static String passwords = "passwords.txt";
	  private static String Users = "users.txt";
	  private ArrayList<Users> users = new ArrayList<Users>();
	  
	  //Server related variables 
	  private ServerSocket servSock = null;
	  private ArrayList<Thread> serviceThrds = new ArrayList<Thread>();
	  private int connCtr = 0;
	  private boolean serverUp = false;

   
  public server(int port) {
  
  }
  


//Populate ArrayList of user records from file
private void loadUsers() {
 
}
}

