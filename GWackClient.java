import java.net.*;
import java.io.*;
import java.util.*;


public class GWackClient  {

    private String server, username;
    private int port;
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader in;
    private GWackClientGUI cGUI;


    GWackClient(String server, int port, String username) {
        this(server, port, username, null);
    }

    String getUsername(){
        return username;
    }

    GWackClient(String server, int port, String username, GWackClientGUI cGUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.cGUI = cGUI;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        }
        catch(Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }
        try {
            pw = new PrintWriter(socket.getOutputStream());
            if(server.equals("ssh-cs2113.adamaviv.com")){
                pw.println("SECRET");
                pw.println("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87");
                pw.println("NAME");
                pw.println(username);
            }
            else{
                pw.println(username);
            }
            pw.flush();
        }
        catch(Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        try
        {
            pw = new PrintWriter(socket.getOutputStream());
            in =
                new BufferedReader(
                         new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        new ListenServer().start();
        return true;
    }

    private void display(String msg) {
        if(cGUI == null){
            System.out.println(msg);    
        }
        else{
            cGUI.append(msg + "\n");   
        }
    }

    void sendMessage2(String msg) {
        try {
            pw.println(msg);
            pw.flush();
        }
        catch(Exception ec) {
            display("Exception writing to server: " + ec);
        }
    }

    private void disconnect() {
        try {
           if(in != null) in.close();
        }
        catch(Exception e) {
  
        } 
        try {
            if(pw != null) pw.close();
        }
        catch(Exception e) {

        } 
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {

        } 

        if(cGUI != null){
            cGUI.connectionFailed();
        }
    }

    public static void main(String[] args) {
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";
        switch(args.length) {
            case 3: 
                serverAddress = args[2];
            case 2: 
                try {
                    portNumber = Integer.parseInt(args[1]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            case 1: 
                userName = args[0];
            case 0: 
                break;
            default:
                System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
            return;
        }

        GWackClient client = new GWackClient(serverAddress, portNumber, userName);
        if(!client.start()){
            return;
        }
        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String msg = scan.nextLine();
            if(msg.equalsIgnoreCase("LOGOUT")) {
                break;
            }
            else {              
                client.sendMessage2(msg);
            }
        }
        client.disconnect();   
    }

 
    class ListenServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg;
                    msg = in.readLine();
                    if(msg.equals("START_CLIENT_LIST")){
                        cGUI.refresh1(msg);
                        msg = in.readLine();
                        cGUI.refresh2(msg);
                        msg = in.readLine();
                        while(!(msg.equals("END_CLIENT_LIST"))){
                            cGUI.refresh2(msg);
                            msg = in.readLine();
                        }
                    }
                    if(cGUI == null) {
                        System.out.println(msg);
                        System.out.print("> ");
                    }
                    else if(!(msg.equals("END_CLIENT_LIST"))) {
                        cGUI.append(msg);
                    }
                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    if(cGUI != null){
                        cGUI.connectionFailed();
                    }
                    break;
                }
            }
        }
    }
}
