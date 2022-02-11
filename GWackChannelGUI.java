import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GWackChannelGUI extends JFrame implements ActionListener, WindowListener {
 
    private JButton start;
    private JTextArea chatRoom, eventLog;
    private JTextField tPortNumber;

    private GWackChannel server;

    GWackChannelGUI(int port) {
        super("GWack Channel");
        server = null;

        JPanel north = new JPanel();
        north.add(new JLabel("Port: "));
        tPortNumber = new JTextField("  " + port);
        north.add(tPortNumber);

        start = new JButton("Start");
        start.addActionListener(this);
        north.add(start);
        add(north, BorderLayout.NORTH);


        JPanel center = new JPanel(new GridLayout(2,1));
        chatRoom = new JTextArea(80, 80);
        chatRoom.setEditable(false);
        appendChatRoom("Chat Room:\n");
        center.add(new JScrollPane(chatRoom));
        eventLog = new JTextArea(80, 80);
        eventLog.setEditable(false);
        appendEvent("Events Log:\n");
        center.add(new JScrollPane(eventLog));
        add(center);

        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);
    }      

    void appendChatRoom(String str) {
        chatRoom.append(str);
        chatRoom.setCaretPosition(chatRoom.getText().length() - 1);
    }
    void appendEvent(String str) {
        eventLog.append(str);
        eventLog.setCaretPosition(chatRoom.getText().length() - 1);
    }

    public void actionPerformed(ActionEvent e) {

        if(server != null) {
            server.stop();
            server = null;
            tPortNumber.setEditable(true);
            start.setText("Start");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(tPortNumber.getText().trim());
        }
        catch(Exception er) {
            appendEvent("Invalid port number");
            return;
        }
        server = new GWackChannel(port, this);
        new ServerRunning().start();
        start.setText("Stop");
        tPortNumber.setEditable(false);
    }


    public static void main(String[] arg) {
        new GWackChannelGUI(1500);
    }

    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    public void windowClosing(WindowEvent e) {

        if(server != null) {
            try {
                server.stop();         
            }
            catch(Exception eClose) {

            }
            server = null;
        }
        dispose();
        System.exit(0);
    }

    class ServerRunning extends Thread {
        public void run() {
            server.start();   
            start.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server closed.\n");
            server = null;
        }
    }
}
