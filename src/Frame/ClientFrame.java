package Frame;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

/**
 * @author Yashchuk A.F.
 */
public class ClientFrame extends JFrame  {
    private static boolean isServerRun = false;
    private static ClientFrame frame;
    MyJPanelNorth serverFrame =  new MyJPanelNorth(true);
    MyJPanelNorth clientFrame =  new MyJPanelNorth(false);
    private static MyJPanel  MyJpCli;
    private SimpleDateFormat format = new SimpleDateFormat("[hh:mm]");

    public static String getIP() {
        return IP;
    }

    private static String IP;


    public ClientFrame(){
        MyJpCli = new MyJPanel();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(MyJpCli);
        setJMenuBar(creatJMenuBar());
    }

    public void setServer(){
        MyJpCli.remove(clientFrame);
        MyJpCli.add(serverFrame,BorderLayout.NORTH);
        MyJpCli.validate();
        MyJpCli.repaint();
        if(isServerRun==false){
            MyJPanelNorth.MyJPanelNortRhigt.conect.setEnabled(true);
        }else{
            MyJPanelNorth.MyJPanelNortRhigt.conect.setEnabled(false);
        }

    }
    public void setClie(){
        MyJpCli.remove(serverFrame);
        MyJpCli.add(clientFrame,BorderLayout.NORTH);
        MyJpCli.validate();
        MyJpCli.repaint();
        if(isServerRun==true){
            MyJPanelNorth.MyJPanelNortRhigt.conect.setEnabled(false);
        }else{
            MyJPanelNorth.MyJPanelNortRhigt.conect.setEnabled(true);
        }
    }



    public JMenuBar creatJMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu Menu = new JMenu("select mode");

        JMenuItem ServerMenu = new JMenuItem("Server");
        Menu.add(ServerMenu);
        ServerMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setServer();
            }
        });

        JMenuItem clientMenu = new JMenuItem("Client");
        Menu.add(clientMenu);
        clientMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setClie();
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        Menu.add(exitItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuBar.add(Menu);
        return menuBar;
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });

    }

    public static void createGUI(){
        frame =  new ClientFrame();
        frame.setTitle("[Chating] @ created by Andrew Yashchuk");
        frame.setSize(700,500);
        frame.addWindowListener(new MyFrameListener());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //  frame.pack();
    }



    public class MyJPanel extends JPanel{


        JTextArea text = Text.getInstance();
        JScrollPane sclor = new JScrollPane(text);
        MyJPanelSouth jpS = new MyJPanelSouth(text);
        BorderLayout bl = new BorderLayout();



        public MyJPanel(){
            setLayout(bl);
            add(clientFrame, BorderLayout.NORTH);
            add(sclor ,BorderLayout.CENTER);
            add(jpS,BorderLayout.SOUTH);
        }

    }


    public static class JPanelConnectTO extends JPanel{


        private static JPanelConnectTO instance = new JPanelConnectTO();
        JLabel jl = new JLabel("Sellect online user");
        JButton chating = new JButton("chating");
        JButton stop = new JButton("stop");
        String [] elements = {null} ;
        JComboBox <String> combo = new JComboBox<String>(elements); // JComboBoxOnline.getInstance();

        private JPanelConnectTO(){
            setBorder(new TitledBorder("clients online"));
            setLayout(new FlowLayout());
            add(jl);
            add(combo);
            add(chating) ;
            add(stop);

            chating.addActionListener(new MyACtionLIstener());
            stop.addActionListener(new MyACtionLIstener());
        }
        public static JPanelConnectTO getInstance(){
            return instance;
        }


        class MyACtionLIstener implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == chating){
                    String selectedID = (String) combo.getSelectedItem();
                    Adapter.getInstance().chatingWith(selectedID);
                }
                if(e.getSource() == stop){
                     Adapter.getInstance().disconnect();
                }

            }
        }
           public void repasintCombo(JComboBox user ){
               combo = user;
               removeAll();
               add(jl);
               add(combo);
               add(chating) ;
               add(stop) ;
               validate();
               repaint();;
           }
    }


    public static class MyJPanelNorth extends JPanel{

        Font font = new Font( "Calibri", Font.PLAIN, 10);
        MyJPanelNortRhigt right = new MyJPanelNortRhigt();
        MyJPanelNorthLeft left = new MyJPanelNorthLeft();
        JPanelConnectTO connectPane = JPanelConnectTO.getInstance();

       public MyJPanelNorth(boolean isServer){
           setBorder(new TitledBorder("Servers connection"));
           right.setFont(font);
           left.setFont(font);
           setLayout(new GridLayout(1,2));
           if(!isServer) {
               add(new ConnecOnServer());
               add(connectPane);
           } else {
               add(left);
               //add(new JPanelConnectTO());
           }

           
        }

        public static class ConnecOnServer extends JPanel{
            static JLabel jl = new JLabel("Servers data");


             MyJPanelNortRhigt right = new MyJPanelNortRhigt();
            ConnecOnServer(){
                setLayout(new GridLayout(2,1));
                add(right);
                add(jl);
            }

        }


        public static class MyJPanelNortRhigt extends JPanel{

           public static JButton conect = new JButton("connect");

            JTextField tf  = new JTextField("192.168.1.4   ");
            private MyJPanelNortRhigt(){
                setLayout(new FlowLayout());
                conect.addActionListener(new MyACtionLIstener());
                add(conect);
                add(tf);
            }
           class MyACtionLIstener implements ActionListener{

               @Override
               public void actionPerformed(ActionEvent e) {
                   String IP = tf.getText();
                   if(Adapter.getInstance().getNt() == null){
                       Adapter.getInstance().newNetwork(IP);
                   }
               }
           }

        }



    }
    public static class MyJPanelNorthLeft extends JPanel{
        Font font = new Font( "Calibri", Font.BOLD, 18);
        JButton start = new JButton("start");
        JLabel ip = new JLabel("    Server");
        JLabel status = new JLabel();
        BorderLayout br = new BorderLayout();


        public MyJPanelNorthLeft(){
            ip.setFont(font);
            status.setFont(font);
            status.setIcon(new ImageIcon("Icon/grean.png"));
            status.setText("Status: stop");
            start.addActionListener(new MyACtionLIstener1());
            setBorder(new TitledBorder("server control"));
            setLayout(br);
            add(start,BorderLayout.WEST);
            add(ip,BorderLayout.CENTER);
            add(status,BorderLayout.NORTH);

        }

        class MyACtionLIstener1 implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                if(isServerRun == false){
                    Adapter.getInstance().stopClientConnection();
                    try {
                        IP = InetAddress.getLocalHost().getHostAddress();
                        ip.setText( "  IP: " + IP);
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }
                    status.setText("Status: run");
                    status.setIcon(new ImageIcon("Icon/red.png"));
                    start.setText("Stop");
                    Adapter.getInstance().startServer();
                    isServerRun=true;
                }else{
                    status.setText("Status: stop");
                    status.setIcon(new ImageIcon("Icon/grean.png"));
                    start.setText("Start");
                    Adapter.getInstance().stopServer();
                    isServerRun = false;

                }
            }
        }

    }



    public class MyJPanelSouth extends JPanel{
        Font font = new Font( "Corbel", Font.BOLD, 20);
        JButton clear = new  JButton("Clear");
        JTextField tf  =  new JTextField (17);


        public MyJPanelSouth(JTextArea text){
            //clear.addActionListener();
            tf.setFont(font);

            clear.setFont(font);
            setLayout(new BorderLayout());
            add(clear,BorderLayout.EAST);
            add(new JpanelAddSend(),BorderLayout.WEST);
            add(tf,BorderLayout.CENTER);
        }

          public class JpanelAddSend extends JPanel{
                JButton add = new  JButton("+");
                JButton send = new  JButton("Send:");
                 Font font = new Font( "Corbel", Font.BOLD, 20);


                  JpanelAddSend(){
                      send.addActionListener(new MyACtionLIstener1());
                      add.addActionListener(new MyACtionLIstener1());
                      clear.addActionListener(new MyACtionLIstener2());
                      setLayout(new FlowLayout());
                           send.setFont(font);
                           add.setFont(font);
                           add(add);
                           add(send);
                  }

              class MyACtionLIstener1 implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                 if(e.getSource() == send){
                     String massage = tf.getText();
                  Adapter.getInstance().sendMassage(massage);
                  Adapter.getInstance().printMassage(massage);
                  tf.setText("");
                 }if(e.getSource() == add){
                        JFileChooser fileopen = new JFileChooser();
                        int ret = fileopen.showDialog(null, "send file");
                        if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = fileopen.getSelectedFile();
                           Adapter.getInstance().printMassage("SEND FILE: "+file.getPath()+"\\"+file.getName());
                           Adapter.getInstance().sendFile(file);
                        }
                }

            }
           }
          }


        class MyACtionLIstener2 implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                 Text.getInstance().clear();
            }
        }

    }







    static class MyFrameListener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            if(isServerRun){
                Adapter.getInstance().stopServer();
            }else{
                Adapter.getInstance().close();
                System.out.println("Exit ----");
            }
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }


}