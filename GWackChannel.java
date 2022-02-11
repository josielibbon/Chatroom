import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class GWackChannel {

    private static int uniqueId;
    private ArrayList<ClientThread> membersO;
    private GWackChannelGUI sGUI;
    private SimpleDateFormat sdf;
    private int port;
    private boolean dontStop;
    private PrintWriter pw;
    private BufferedReader in;

    public GWackChannel(int port) {
        this(port, null);
    }

    public GWackChannel(int port, GWackChannelGUI sGUI) {

        this.sGUI = sGUI;
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        membersO = new ArrayList<ClientThread>();
    }

    public void start() {
        dontStop = true;
        //make server socket for connections
        try
        {

            ServerSocket serverSocket = new ServerSocket(port);
            while(dontStop)
            {
                display("The server is waiting for clients on port " + port + ".");
                Socket socket = serverSocket.accept();     
                if(!dontStop){
                    break;
                }
                //make thread for accepted collection
                ClientThread t = new ClientThread(socket);  
                //add them to the list of clients
                membersO.add(t);                       
                //send out list of clients           
                pw = new PrintWriter(socket.getOutputStream());
                broadcast2("START_CLIENT_LIST");
                    for(int i = 0; i < membersO.size(); ++i) {
                        ClientThread ct = membersO.get(i);
                        broadcast2(ct.username);
                    }
                    broadcast2("END_CLIENT_LIST");
                t.start();
            }
            try {
                serverSocket.close();
                for(int i = 0; i < membersO.size(); ++i) {
                    ClientThread tc = membersO.get(i);
                    try {
                    tc.in.close();
                    tc.pw.close();
                    tc.socket.close();
                    }
                    catch(IOException ioE) {
                       
                    }
                }
            }
            catch(Exception e) {
                display("Exception while closing the server and clients: " + e);
            }
        }
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on the ServerSocket: " + e + "\n";
            display(msg);
        }

    }      


    //for stopping the server
    protected void stop() {
        dontStop = false;
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {

        }

    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if(sGUI == null){
            System.out.println(time);
        }
        else{
            sGUI.appendEvent(time + "\n");
        }
    }

    //send message to all clients and add to servergui
    private synchronized void broadcast(String message) {

        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        if(sGUI == null){
            System.out.print(messageLf);
        }
        else{
            sGUI.appendChatRoom(messageLf);     
        }
         
        for(int i = membersO.size(); --i >= 0;) {
            ClientThread ct = membersO.get(i);
            //if it can't write to the client, disconnect the client
            if(!ct.writeMsg(ct, messageLf)) {
                membersO.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    private synchronized void broadcast2(String message) {

        for(int i = membersO.size(); --i >= 0;) {
            ClientThread ct = membersO.get(i);
            if(!ct.writeMsg(ct, message)) {
                membersO.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    //for removing clients
    synchronized void remove(int id) {
        for(int i = 0; i < membersO.size(); i++) {
            ClientThread ct = membersO.get(i);
            if(ct.id == id) {
                membersO.remove(i);
                return;
            }
        }
      
    }

    public static void main(String[] args) {
        // default server is 1500 if one isn't given
        int portNumber = 1500;
        switch(args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;
        }
        // create server and start it
        GWackChannel server = new GWackChannel(portNumber);
        server.start();
    }

    //thread that each client will have
    class ClientThread extends Thread {

        Socket socket;
        BufferedReader in;
        PrintWriter pw;
        int id;
        String username;
        String cm;
        String date;

        ClientThread(Socket socket) {

            id = ++uniqueId;
            this.socket = socket;
            try{
 
                 pw = new PrintWriter(socket.getOutputStream());
                 in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()));
                username = in.readLine();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean dontStop = true;
            while(dontStop) {
                try {
                    cm = in.readLine();
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;             
                }
                if(cm.equals("LOGOUT")){
                    display(username + " disconnected.");
                    remove(id);
                    broadcast2("START_CLIENT_LIST");
                    for(int i = 0; i < membersO.size(); ++i) {
                        ClientThread ct = membersO.get(i);
                        broadcast2(ct.username);
                    }
                    broadcast2("END_CLIENT_LIST");
                    dontStop = false;
                }
                else{
                    broadcast(username + ": " + cm);
                }  
            }
            close();
        }

        private void close() {
            try{
                if(pw != null) pw.close();
            }
            catch(Exception e){

            }
            try{
                if(in != null) in.close();
            }
            catch(Exception e){

            }
            try{
                if(socket != null) socket.close();
            }
            catch (Exception e){

            }
        }

        private boolean writeMsg(ClientThread ct, String msg) {
            if(!socket.isConnected()) {
                close();
                return false;
            }
            try {
                pw.println(msg);
                pw.flush();
            }

            // if an error occurs, do not abort just inform the user
            catch(Exception e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }
}
