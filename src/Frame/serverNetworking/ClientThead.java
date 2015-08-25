package Frame.serverNetworking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by admin on 25.11.2014.
 */
public class ClientThead extends Thread implements AutoCloseable{


    private ServerRun mainServer = ServerRun.getInstance();
    private ClientThead connectOn;
    private final int CLIENT_ID;
    private Socket client;
    private Scanner in;
    private PrintWriter out;
    private String signature;
    //private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.hh:mm");




    public void setConnectOn(ClientThead connectOn) {
        this.connectOn = connectOn;
    }
    public ClientThead(Socket soc, int id){
        this.client = soc;
        try {
            this.in = new Scanner(new InputStreamReader(client.getInputStream()));
            this.out = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.CLIENT_ID = id;
        this.signature = "id = " + CLIENT_ID + "; [signature: " +soc+"]";
    }

    public int getCLIENT_ID() {
        return CLIENT_ID;
    }


    public Scanner getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run(){
        String massage = null;
        sendMassage("%id="+CLIENT_ID);
        while (!isInterrupted()){
            if(in.hasNext()){
                massage = in.nextLine();
            }
            if(massage!=null)
                if(massage.toCharArray().length !=0)
                    if(massage.toCharArray()[0] == '%'){
                        doSystemComand(massage);
                    }else{
                        if(connectOn!=null){
                            connectOn.sendMassage(" ID_"+CLIENT_ID+":  "+massage);
                        }else{
                            sendMassage("no conection");
                        }
                    }
        }
        try {
            close();
        } catch (Exception e) {
            System.out.println("не хорошо");
        }
        System.out.println("client thread closed...");
    }

    public Socket getClient() {
        return client;
    }

    public void doSystemComand(String massage){
        String msg = massage.split("=")[1];
        String SystemMassage = new String(massage.split("=")[0]);
        if(SystemMassage.equalsIgnoreCase("%connect")){
            systemComandConnect(msg);
        }if(SystemMassage.equalsIgnoreCase("%disconnect")){
            systemComandDisconnect();
        }if(SystemMassage.equalsIgnoreCase("%sendFile")) {                    // первиый клиент хочет передать файл
            int newPort = Integer.parseInt(msg);
            Socket ss = this.client;                               // получаем
            SendFile newSending =  new SendFile( ss.getInetAddress().getHostAddress(), newPort);
            newSending.start();
            connectOn.sendMassage("%reciveFile="+(newPort+1));
        }if(SystemMassage.equalsIgnoreCase("%close")){
            systemComandClose(msg);
        }
    }

    private void systemComandClose(String msg) {
        if (connectOn != null){
            connectOn.sendMassage("%disconnect!");
            connectOn.setConnectOn(null);
            connectOn = null;
        }
        mainServer.deleteConnection(CLIENT_ID);
        mainServer.removeOnline(Integer.parseInt(msg));

    }


    public void systemComandConnect(String msg){
        if(Integer.parseInt(msg) !=CLIENT_ID){
            connectOn = ServerRun.createNewConnection(CLIENT_ID, Integer.parseInt(msg));
        }

        if(connectOn == null){
            sendMassage("not corect ID or this conecction byse");
            System.out.println(1);
        }else {
            sendMassage("%Connection Accept!="+connectOn.CLIENT_ID);
            connectOn.sendMassage("%Connection Accept!="+this.CLIENT_ID);
            connectOn.setConnectOn(this);
        }
    }


    public void systemComandDisconnect(){
        sendMassage("%disconnect!");
        connectOn.sendMassage("%disconnect!");
        connectOn.setConnectOn(null);
        connectOn = null;
        mainServer.deleteConnection(CLIENT_ID);
    }


    public void systemComandReceiveFile(){

    }

    public synchronized void sendMassage(String massage){
        out.write(massage+"\n");
        out.flush();
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
    }

    public void serverClose(){
        sendMassage("%serverClose="+CLIENT_ID);
    }



}
