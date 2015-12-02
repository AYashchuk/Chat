package Frame.clientNetworking;

import Frame.Adapter;
import Frame.Text;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Yashchuk A.F.
 */
public class Network extends Thread{
   private static PrintWriter out;
    private static Scanner in;
    private static String host = "192.168.1.3";
    static   int PORT =8081;
    File recive = new File("C:\\Chat\\recive\\");

    public String getMyID() {
        return MyID;
    }

    private String MyID =null;
    private String online [];
    Socket s;

    public Network(String host) throws IOException {
             if(!recive.exists()){
                 recive.mkdirs();
             }
            this.host = host;
            s = new  Socket(host, PORT);
        System.out.println("Socket "+ host +" Port " + PORT);
        if(s != null){
            start();
        }
    }


    @Override
    public  void run( ){

        try{
            in = new Scanner(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());


            while (!isInterrupted()){
                if(in.hasNext()){
                    String massage = in.nextLine();
                    if(massage.toCharArray()[0] == '%'){
                        systemMasage(massage);
                        System.out.println(massage);
                    }else Text.getInstance().append(massage);

                }

            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        disconnect();
    }

    private void systemMasage(String massage) {
        if(massage.split("=")[0].equals("%reciveFile")){
            TransmitFile thiss = new TransmitFile("r",host, recive);
            thiss.setOnPort(Integer.parseInt(massage.split("=")[1]));
            System.out.println("Saving File...");
            thiss.start();
        }
        if(massage.split("=")[0].equals("%online")){
             online  = massage.split("=")[1].split(":");
            Adapter.getInstance().addOnlineUser(new JComboBox(getOnlineMas()));
        }
        if(massage.split("=")[0].equals("%id")){
            MyID = massage.split("=")[1];
            Adapter.getInstance().setID(getMyID());
            Adapter.getInstance().addOnlineUser(new JComboBox(getOnlineMas()));
        }
        if(massage.split("=")[0].equals("%Connection Accept!")){
            Adapter.getInstance().printMassage("ID = "+massage.split("=")[1] + " Connection Accept!");
        }if(massage.split("=")[0].equals("%disconnect!")){
            Adapter.getInstance().printMassage("disconnect!");
        }if(massage.split("=")[0].equals("%serverClose")){
            Adapter.getInstance().printMassage("serverClose!");
            Adapter.getInstance().serverClosed();
        }


    }

    public String [] getOnlineMas(){
        if(MyID != null){
            ArrayList<String> tmp = new ArrayList<String>();
            for(int i=0;i<online.length;i++){
                 if(!online[i].equals(MyID)){
                     tmp.add(online[i]);
                 }
            }
            String returnMas [] = new String[tmp.size()];
            for(int i=0;i<returnMas.length;i++){
                returnMas[i] = tmp.get(i);

            }
            return returnMas;
        }else{
            return  new String [] {"       "};
        }
    }


    public void connectWith(String ID){
        String massage = "%connect="+ID+"\n\r";
        sendMassage(massage);
    }
    public  void sendMassage(String massage){
        out.write(massage+"\n\r");
        out.flush();
    }

    public void disconnect() {
        sendMassage("%disconnect=" +222+"\n\r");
    }

    public void close() {
        sendMassage("%close=" +MyID+"\n\r");
    }
    public void sendFile(File file){
        new TransmitFile("s",host,file).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String massage = "%sendFile="+(PORT+7)+"\n";
        sendMassage(massage);
    }

    // консоль+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

 }

