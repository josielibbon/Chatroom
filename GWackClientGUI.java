import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GWackClientGUI extends JFrame implements ActionListener{
  
    private JTextArea tf;
    private JTextArea membersOnline;
    private JTextField tfServer, tfPort, name;
    private JButton connect, send;
    private JTextArea ta;
    private boolean connected;
    private GWackClient client;
    private int defaultPort;
    private String defaultHost;

    GWackClientGUI(String host, int port){
        super();
        this.setTitle("GWack -- GW Slack Simulator (not connected)");
        this.setSize(300,400);          
        this.setLocation(100,100); 
        defaultPort = port;
        defaultHost = host;     
       
        tfServer = new JTextField("", 10);
        tfPort = new JTextField("", 5);
        name = new JTextField("", 10);
        JPanel o1panel = new JPanel(new FlowLayout(FlowLayout.TRAILING)); 
        o1panel.add(new JLabel("Name"));
        o1panel.add(name);
        o1panel.add(new JLabel("IP Address"));
        o1panel.add(tfServer);
        o1panel.add(new JLabel("Port"));
        o1panel.add(tfPort);

        //o2panel
        JPanel o2panel = new JPanel();
        o2panel.setLayout(new BoxLayout(o2panel, BoxLayout.X_AXIS));

        //k1panel - WEST
        JPanel k1panel = new JPanel();
        k1panel.setLayout(new BoxLayout(k1panel, BoxLayout.Y_AXIS));

        //k1_1panel - NORTH
        JPanel k1_1panel = new JPanel(new FlowLayout(FlowLayout.LEADING)); //panel with input message
        k1_1panel.add(new JLabel("Members Online"));

        //k1_2panel - SOUTH
        JPanel k1_2panel = new JPanel(new FlowLayout()); //panel with input message
        membersOnline = new JTextArea(15, 20);
        membersOnline.setEditable(false); // set textArea non-editable
        JScrollPane membersOnlineScroll = new JScrollPane(membersOnline);
        membersOnlineScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        k1_2panel.add(membersOnlineScroll);

        k1panel.add(k1_1panel, BorderLayout.NORTH);
        k1panel.add(k1_2panel, BorderLayout.SOUTH);

        //k2panel - EAST
        JPanel k2panel = new JPanel();
        k2panel.setLayout(new BoxLayout(k2panel, BoxLayout.Y_AXIS));

        //k2_1panel - NORTH
        JPanel k2_1panel = new JPanel();
        k2_1panel.setLayout(new BoxLayout(k2_1panel, BoxLayout.Y_AXIS));

        //k2_1_1panel - NORTH
        JPanel k2_1_1 = new JPanel(new FlowLayout(FlowLayout.LEADING)); //panel with input message
        k2_1_1.add(new JLabel("Messages"));

        //k2_1_2 - SOUTH
        JPanel k2_1_2 = new JPanel(new FlowLayout()); //panel with input message
        ta = new JTextArea(10, 50);
        ta.setEditable(false); // set textArea non-editable
        JScrollPane taScroll = new JScrollPane(ta);
        taScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        k2_1_2.add(taScroll);

        k2_1panel.add(k2_1_1, BorderLayout.NORTH);
        k2_1panel.add(k2_1_2, BorderLayout.SOUTH);

        //k2_2panel - NORTH
        JPanel k2_2panel = new JPanel();
        k2_2panel.setLayout(new BoxLayout(k2_2panel, BoxLayout.Y_AXIS));

        //k2_2_1panel - NORTH
        JPanel k2_2_1 = new JPanel(new FlowLayout(FlowLayout.LEADING)); //panel with input message
        JLabel tfLabel = new JLabel("Compose Message");
        k2_2_1.add(tfLabel);

        //k2_2_2 - SOUTH
        JPanel k2_2_2 = new JPanel(new FlowLayout()); //panel with input message
        tf = new JTextArea(2, 50);
        tf.setEditable(true); // set textArea non-editable
        JScrollPane tfScroll = new JScrollPane(tf);
        tfScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        k2_2_2.add(tfScroll);
        
        k2_2panel.add(k2_2_1, BorderLayout.NORTH);
        k2_2panel.add(k2_2_2, BorderLayout.SOUTH);

        k2panel.add(k2_1panel, BorderLayout.NORTH);
        k2panel.add(k2_2panel, BorderLayout.SOUTH);

        o2panel.add(k1panel);
        o2panel.add(k2panel);

        connect = new JButton("Connect");
        connect.addActionListener(this);
        o1panel.add(connect);

        //o3panel
        JPanel o3panel = new JPanel(new FlowLayout(FlowLayout.TRAILING)); 
        send = new JButton("Send"); //encrypt button
        send.addActionListener(this);
        o3panel.add(send);

        //ALLALLpanel
        JPanel ALLALLpanel = new JPanel();
        ALLALLpanel.setLayout(new BoxLayout(ALLALLpanel, BoxLayout.Y_AXIS));
        ALLALLpanel.add(o1panel, BorderLayout.NORTH);
        ALLALLpanel.add(o2panel, BorderLayout.CENTER);
        ALLALLpanel.add(o3panel, BorderLayout.SOUTH);

        //add opanel to frame
        this.add(ALLALLpanel);
        this.pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    // called by the client to append text in the TextArea
    void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
        ta.append("\n");
    }

    void refresh1(String str) {
        membersOnline.setText("");
    }

    void refresh2(String str) {
        membersOnline.append(str + "\n");
    }

    // reset buttons, label, textfield, text area
    void connectionFailed() {
        connect.setEnabled(true);
        send.setEnabled(false);
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        connected = false;
    }
  
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if(o == connect && connect.getText() == "Disconnect") {
            connect.setText("Connect");
            setTitle("GWack -- GW Slack Simulator (not connected)");
            tfServer.setEditable(true);
            tfPort.setEditable(true);
            name.setEditable(true);
            ta.setText("");
            membersOnline.setText("");
            client.sendMessage2("LOGOUT");
            return;
        }

        if(o == send) {
            client.sendMessage2(tf.getText());    
            tf.setText("");
            return;
        }

        if(o == connect && connect.getText() == "Connect") {
            connect.setText("Disconnect");
            setTitle("GWack -- GW Slack Simulator (connected)");
            String username = name.getText().trim();
            if(username.length() == 0){
                return;
            }
            String server = tfServer.getText().trim();
            if(server.length() == 0){
                return;
            }
            String portNumber = tfPort.getText().trim();
            if(portNumber.length() == 0){
                return;
            }
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception en) {
                return;   
            }

            client = new GWackClient(server, port, username, this);

            if(!client.start()){
                return;
            }
            tf.setText("");

            connected = true;
            send.setEnabled(true);
            name.setEditable(false);
            tfServer.setEditable(false);
            tfPort.setEditable(false);
        }
    }

    public static void main(String[] args) {
        new GWackClientGUI("localhost", 1500);
    }
}
