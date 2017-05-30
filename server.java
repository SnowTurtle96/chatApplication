

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
import java.io.PrintWriter;
import java.io.File;

public class server {

    class Users {
        String userNm;
        String pswd;

        public Users (String u, String p){
            userNm = u;
            pswd = p;
        }

        public String toString()
        {
            return userNm + " " + pswd;
        }

        public String getUsername()
        {
            return userNm;
        }

        public String getPassword()
        {
            return pswd;
        }
    }

   

    //Array lists and special character declarations
    public static final int BUFFSZ = 80;
    public static final String fieldSep = "#";
    public static final String endInst = ";";
    private static String usersfile = "user.txt";
    private static String toursfile = "tours.txt";
    private ArrayList<Users> users = new ArrayList<Users>();
  

    public static final String endMkr = ">";
    public File file;
    public BufferedWriter pw, pw1, pw2, pw3;

    //Server related variables 
    private ServerSocket servSock = null;
    private ArrayList<Thread> serviceThrds = new ArrayList<Thread>();
    private int connCtr = 0;
    private boolean serverUp = false;
    private int port;

    public server() {
        loadUsers();
        startup();

        try
        {
            pw = new BufferedWriter(new FileWriter("users.txt", true));
            pw1 = new BufferedWriter(new FileWriter("bookings.txt", true));
            pw3 = new BufferedWriter(new FileWriter("signups.txt", true));

        }
        catch(  IOException e)
        {
            System.out.println("File users can not be found");
        }

        runServer(port);
    }

    public void startup()
    {
        System.out.println("Enter a port you would like the server to operate on");
        Scanner sc = new Scanner(System.in);
        port = sc.nextInt();

    }
    //Populate ArrayList of user records from file
    public void loadUsers() {
        try {
            Scanner scan = new Scanner(new BufferedReader(new FileReader("users.txt")));

            while (scan.hasNext())
            {
                users.add(new Users(scan.next(),scan.next().trim()));
            }

            System.out.println("Users Added");
            scan.close();
        } catch(IOException ex) {
            System.err.println("Could not open users file for reading");
        }
    }

    

   

  

    public int arraySize()
    {
        return users.size();
    }

    

    public void displayUsers() {

        for (Users users1: users) {
            System.out.println((users1.toString()+"\n"));
        }
    }

   

    public void runServer(int port) {
        Thread newThread = null;
        serverUp = true;
        try {
            servSock = new ServerSocket(port, 20);
            while (serverUp) {
                System.out.printf("Server waiting for conn request on port %d\n", port);
                newThread = new ServiceThread(servSock.accept(), connCtr);
                connCtr++;
                serviceThrds.add(newThread);
                System.out.printf("Now %d service threads\n", serviceThrds.size());
                newThread.start();
            } //end while. Server down now.
            if (servSock != null)
                servSock.close();
            int nThrds = serviceThrds.size();
            if (nThrds > 0)
                System.out.printf("WARNING: there are still %d service threads\n", nThrds);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        Scanner console = new Scanner(System.in);
        System.out.println(
            "Once all service threads are finished, ENTER to confirm shutdown");
        console.nextLine();
    } //runServer(...)

    /******************* Client service helper functions ********************/

    /* Return 0 if password valid for this acct,
     *        1 if invalid for this account;
     *        2 if account no not recognised.
     */
    byte checkPswd(String name, String sPswd) {
        for (Users usr: users) {
            if (usr.userNm.equals(name)) {
                return usr.pswd.equals(sPswd)? (byte)0: (byte)1;
            }
        } //else ...
        return (byte)2;
    }

    // Display contents of message buffer in hex formate on console
    //   (diagnostic only) 
    void displayBuffer(byte[] buff, int len) {
        for (int k = 0; k < buff.length && k < len; k++)
            System.out.printf("%2x ", buff[k]);
        System.out.println();
    }

    /******************** The server thread class **************************/
    class ServiceThread extends Thread {
        private Socket conn;
        private DataOutputStream output;
        private DataInputStream input;
        boolean loggedIn;
        public String username;

        ServiceThread(Socket c, int i) { //constructor
            conn = c;
            setName("Conn_"+i);
            loggedIn = false;
        }

        public void setUser(String userLog)
        {
            username = userLog;
        }

        public String getUser()
        {
            return username;
        }

        /* The 'main' code to serve a particular client: runs in its own thread */
        public void run() {
            byte[] buff = new byte[BUFFSZ];
            int len=0, acctNo = 0;
            byte rslt;
            String recStg, sPsw = null;
            String[] recData;
            try {
                System.out.printf("Thread %s serving client %s\n",
                    getName(), conn.getInetAddress().getHostName());
                output = new DataOutputStream(conn.getOutputStream());
                input = new DataInputStream(conn.getInputStream());
                conn.setTcpNoDelay(true);
                System.out.printf("Thread %s has I-O streams\n", getName());
                sendMsg(output, "Connected");


                boolean running = true;
                while(running) { //Service thread duty cycle
                    recStg = new String();
                    do {
                        len = input.read(buff);
                        System.out.printf("%d bytes received\n", len);
                        displayBuffer(buff, len);
                        recStg += new String(buff, 0, len);
                    } while (!recStg.contains(endMkr) && recStg.length() < BUFFSZ);
                    recData = recStg.substring(0, recStg.indexOf(endMkr)).split(fieldSep);
                    //Process request from client ...
                    if (recData.length == 0) {
                        System.err.println("Empty request!");
                    }

                    else if (recData[0].equals("Down")) {
                        if (loggedIn) {
                            running = false;
                            loggedIn = false;
                            sendMsg(output, "Server going Down.");
                        } else {
                            sendMsg(output, "You need to be logged in.");
                        }
                    }
                 

                

                    
                  
                } //end while(running)
                input.close(); output.close(); conn.close(); pw.close(); pw1.close();
                System.out.printf("Connection %s done\n", getName());
                if (serviceThrds.remove(this))
                    System.out.println("Service thread deleted");

                if (serviceThrds.remove(this))
                {
                    System.out.println("Service thread deleted");
                }

            } //end try
            catch (EOFException ex) {
                System.err.printf("Unexpected EOF: %s", ex);
            }//end while(running)

            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // run()

        //Try to log client in; send client response to 'Login' command 
        private void login(String usrNm, String pswd) throws IOException {

            System.out.printf("Login %s: %s\n", usrNm, pswd);
            byte rslt = checkPswd(usrNm, pswd);
            System.out.printf("Result of check = %d\n", rslt);
            loggedIn = (rslt == 0);
            output.writeBytes("\n");
            if (loggedIn) {
                setUser(usrNm);
                sendMsg(output,"\nLogged in as " + getUser()); // reply to client
            } else if (rslt == 1) {
                output.writeBytes("\nPassword invalid");
            } else if (rslt == 2) {
                output.writeBytes("\nUser not recognised");
            }

        }
        // class ServiceThread
    }

    
    /***************** Client service helper functions ********************/

    //General-purpose messaging function
    private void sendMsg(DataOutputStream output, String msg) throws IOException {
        output.writeBytes(String.format("\n%s\n%s\n", msg, endMkr));
        output.flush();
    }

    /* Delete ALL score records with matching name from DB; warn if none or >1;
     * write amended scoreboard to file; send diagnostic or confirmation message */
    private synchronized void register(DataOutputStream output,
    String userC, String passC) throws IOException {
        boolean exists = false;
        output.writeBytes("\n");
        for (Users user : users) {
            if (user.getUsername().equals(userC)) {
                exists = true;
                sendMsg(output,("User already exists please choose another name"));

            }
        }

        if(exists == false)
        {
            System.out.println("Final stage");
            Users newUser = new Users(userC, passC);
            users.add(newUser);
            pw.newLine();
            pw.append("\n" + userC+" "+passC);
            pw.flush();
            sendMsg(output, ("USER " + userC + " with password " + passC + " has been created"));

        }
        output.writeBytes(">" +"\n");
        output.flush();

    

   
        output.writeBytes(">" +"\n");
        output.flush();
    }

   
   
 



    public void eraseFile()throws IOException
    {
        pw2 = new BufferedWriter(new FileWriter("bookings.txt"));
        pw2.close();

    }

    

    public void updateSignupFile(DataOutputStream output, String user, int tourID, String date) throws IOException
    {

        Date timestamp = new Date();
        pw3.newLine();
        pw3.append(user + " " + tourID + " " + date + " " + timestamp);
        pw3.flush();
        sendMsg(output, ("USER: " + user + " tourID: " + tourID + " date: " + date + "timestamp: " + timestamp));



          
    }

    

    public static void main(String [] args)
    {
        server server = new server();
    }

}