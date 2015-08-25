package Frame;

import Frame.clientNetworking.Network;
import Frame.serverNetworking.ServerRun;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by admin on 29.11.2014.
 */
public class Adapter {
    private Network nt  = null;
    private ServerRun server = null;
    String chating= null;
    int PORT =  8081;


    private static Adapter instance = new Adapter();
    private Adapter(){

    }
    // client Mathod __________________________________________________________________________________||||||||||||||||||||||||||||||||||||||
    public Network getNt() {
        return nt;
    }

    public static Adapter getInstance(){
        return instance;
    }

    public void newNetwork(String IP){
        try {
            nt = new Network(IP.trim());
        } catch (IOException e) {
            e.printStackTrace();
            nt=null;
        }
        System.out.println("new connection");

    }

    public void addOnlineUser(JComboBox  user ){
        ClientFrame.JPanelConnectTO.getInstance().repasintCombo(user);
    }




    public void setID(String ID){
        ClientFrame.MyJPanelNorth.ConnecOnServer.jl.setText("  Your`s ID on server: " + ID);
    }


    public void chatingWith(String selectedID){
        if(nt!=null) {
            nt.connectWith(selectedID);
            chating = selectedID;
        }

    }


    public void printMassage(String massage){
        Text.getInstance().append(massage);
    }

    public void sendMassage(String massage){
        String mas =" "+ massage;
        if(nt!=null)
        nt.sendMassage(mas);
    }


    public void disconnect(){
        if(nt!=null)
        nt.disconnect();
    }

    public void close() {
        if(nt != null)
        nt.close();
    }


    public void serverClosed(){
        System.out.println("Server closed");
        ClientFrame.JPanelConnectTO.getInstance().repasintCombo(new JComboBox((new String [] {"    "})));
        setID(" no conection");
        close();
        if(nt != null) nt.interrupt();
        nt=null;
    }










    // servers Mathod ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    public void startServer() {
        nt = null;
        if(server == null){
            server = new ServerRun();
            server.start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            nt = new Network(ClientFrame.getIP());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void stopClientConnection(){
        close();
        if(nt !=null)
        nt.interrupt();
    }

    public void stopServer() {


        if(server!=null){
            server.closeServerSocket();
            server.interrupt();
        }
        server = null;

    }
    // Send / recive File
    public void sendFile(File file){
        if(nt!=null)  {
            nt.sendFile(file);
        }

    }

}
