package Frame.serverNetworking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by admin on 10.11.2014.
 */
public class ServerRun extends Thread implements AutoCloseable{
    public  int getPORT() {
        return PORT;
    }

    private  int PORT =  8081;
    private  InetAddress addr;
    private  String myLANIP;
    private static ArrayList<ClientThead> connection = new ArrayList<ClientThead>();
    private  List<Integer> clientsOnline = new ArrayList<Integer>();
    private static ArrayList<PairConnection> pair = new ArrayList<PairConnection>();
    private ServerSocket ss;

    public  static ArrayList<ClientThead> getClientsOnline() {
        return connection;
    }

    public void removeOnline(int value){
        for(int i=0;i<connection.size();i++){
            if(connection.get(i).getCLIENT_ID() == value){
                try {
                    connection.get(i).interrupt();
                    System.out.println(value + " - closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        connection.remove(new Integer(value));
        clientsOnline.remove(new Integer(value));
        informClientsOfNewConnection();
        System.out.println("Client close");
    }

    private static ServerRun instance = new ServerRun();
    public ServerRun(){
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        myLANIP = addr.getHostAddress();
    }
    public static ServerRun getInstance(){
        return instance;
    }


    public String getMyLANIP() {
        return myLANIP;
    }

    @Override
    public  void run() {

        try {
            ss = new ServerSocket(PORT);
            System.out.println("-===(- Andrew Server -)===-");
            System.out.println("Server started...");
            System.out.println("THIS SERVER HAS local IP: < " + myLANIP + " >");
            while(!isInterrupted()){
                Socket client =  ss.accept();        // ждем нового подключения
                if(client.isConnected()){            // если коиент подключился
                    System.out.println("\n\n\n\n___________new     Client_____________");
                    System.out.println("New client connected");
                    System.out.println("His signature: " + client);
                }
                ClientThead newClient =  new ClientThead(client,getID()); // создаем в новом потоке клиентскую обработку (getID()- генерируэм для него новый айди)
                connection.add(newClient);                                  // добовляем нового клиента в список подключений
                clientsOnline.add(newClient.getCLIENT_ID());                // добавляем его айдишник в список айдишников (для простоты поиска)
                informClientsOfNewConnection();                             // информируем пользователей о новом подключении
                newClient.start();                                          // запускаем клиентский поток обработки
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("stop server...");
    }


    private static int getID(){
        return (int) (Math.random() * 10000);
    }





    public  void informClientsOfNewConnection(){   // формирует строку всех подключений
        String clientsOnServer=null;                     // в этой строке формируем системное сообщение всем пользователям о онлайн пользователях
        if(clientsOnline.size()>1){                      // если онлайн пользователей больше одного
            for(int i=0;i<clientsOnline.size();i++){     // проходим по цыклу и,
                // String name = clientsOnline.get(i).getName();
                if(clientsOnServer != null){             // если это не первый пользователь (строка не равна нуль), то
                    clientsOnServer = clientsOnServer+ ""+clientsOnline.get(i)+":"; // добовляем через двоеточия айдишники онлайн клиентов
                }else clientsOnServer = clientsOnline.get(0)+":";  // если первый пользователь просто добовляем первый елемент масива пользователей
            }
        }

        for(int i=0;i<connection.size();i++){                // проходим по масиву пользователей, и
            // String name = clientsOnline.get(i).getName();
            connection.get(i).sendMassage("%online="+clientsOnServer);  // отправляем всем пользователям строку с айдишниками онлайн, при етом
            // прикрепляем заголовок к не что строка системная ("%online=")
        }

    }


    public static ClientThead createNewConnection(int myID, int connectOn){
        boolean cheackIsBesyConnection = false;
        for(int i=0;i<pair.size();i++){
            if(connectOn == pair.get(i).getUser1_id() ||  connectOn == pair.get(i).getUser2_id()){
                cheackIsBesyConnection=true;
            }
        }
        if(cheackIsBesyConnection==false){
            pair.add(new PairConnection(myID,connectOn));
            for(int i =0;i<connection.size();i++){
                if(connection.get(i).getCLIENT_ID() == connectOn){
                    return connection.get(i);
                }
            }
        }else{
            return null;
        }
        return null;
    }

    public void deleteConnection(int myID){
        for(int i=0;i<pair.size();i++){
            if(pair.get(i).getUser1_id() == myID || pair.get(i).getUser2_id() == myID){
                pair.remove(i);
            }
        }
    }

    @Override
    public void close() throws Exception {

        System.out.println("Client Thread closing...");
        for(int i=0;i<connection.size();i++){

            connection.get(i).serverClose();
            connection.get(i).interrupt();
        }

    }


    public void closeServerSocket(){
        try {
            ss.close();
            System.out.println("server socket close..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PairConnection{
        private PairConnection(int user1_id, int user2_id) {
            User1_id = user1_id;
            User2_id = user2_id;
        }

        private  int User2_id;
        private  int User1_id;

        public int getUser1_id() {
            return User1_id;
        }

        public int getUser2_id() {
            return User2_id;
        }

    }

 // консоль+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   public  class ConsoleStream extends Thread{
       private Scanner console;
       public ConsoleStream(InputStream i){
           console = new Scanner(new InputStreamReader(i));
        }
       @Override
       public void run(){
           while (true){
               if(console.hasNext()){
                   String massage = console.nextLine() + "\n\r";
               }

           }
       }

    }









}
