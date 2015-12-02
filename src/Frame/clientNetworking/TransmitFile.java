package Frame.clientNetworking;

import Frame.Adapter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yashchuk A.F.
 */
public class TransmitFile extends Thread {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    File reciveFile = new File("C:\\Chat\\recive\\");
    File file ;//= new File("C:\\Users\\admin\\Desktop\\курсач СВЧ\\курсач.xmcd");
    String action;
    String host;

    
    public void setOnPort(int omPort) {

        this.onPort = omPort;
    }

    int onPort;



    ServerSocket server;
    TransmitFile(String action, String host, File file){
        this.file = file;

        try {
            if(action.equals("s")){
                server = new ServerSocket(8088) ;
            }else{

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.action = action;
        this.host = host;
    }


    public File getReciveFile() {
        return reciveFile;
    }

    @Override
    public void run(){
        if(action.equals("s")){
            Socket send=null;
            while (send == null){
                try {
                    System.out.println("wait");
                    send =  server.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sendFile(file,send);
        }
        if(action.equals("r")){
            Socket recive=null;
            try {
                 recive = new Socket(host,onPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
            receiveFile(reciveFile,recive);
            try {
                recive.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }






    private  void sendFile(File file, Socket socket) {
        try{
            String name = file.getName();
            System.out.println("Sending File: " + file.getName()+" ...");
            // передаём имя файла
            DataOutputStream outFileName = new DataOutputStream(socket.getOutputStream());
            outFileName.writeUTF(name);
            outFileName.flush();
            // создаем входной поток для передачи файла
            FileInputStream inputFile = new FileInputStream (file);
            // записываем в переменную size размер (length) файла в байтах
            int size = (int)file.length();
            // создаем массив buffer типа byte в котором будем передавать байты информации
            byte[] buffer = new byte[size];
            System.out.println("Create new Out Stream...");
            // создаем новый выходной поток для передачи файла серверу
            DataOutputStream outputFile = new DataOutputStream(socket.getOutputStream());
            System.out.println("New Out strea, is created...");

            // отправляем файл на сервер
            System.out.println("Send file...");
            int receivedBytes = 0;  // переменная счетчика
            while(true) {
                System.out.println(receivedBytes);
                receivedBytes = inputFile.read(buffer);
                if (receivedBytes > 0) {
                    outputFile.write(buffer, 0, receivedBytes);
                }
                if (receivedBytes == -1) {
                    break;
                }

            }


            System.out.println("File "+file.getName()+" is sent!");
            outFileName.close();
            // закрываем входной поток
            inputFile.close();
            // закрываем выходной поток
            outputFile.close();
            System.out.println("IO Streams wear closed");
        }catch(Exception e){

            System.out.println("Error not sended file...");
        }


    }




    private  void receiveFile(File path,Socket socket) {
        System.out.println("recive file...");
        try{
            // поток для приема строки от сервера содержащей название файла
            DataInputStream disFileName = new DataInputStream(socket.getInputStream());
            System.out.println("Created new INstream...");
            // считуем имя файла
            String FileName = disFileName.readUTF();
            System.out.println("Recive File name: (" + FileName + ")");


            // создаем новый входной поток для записи принятого файла
            DataInputStream input = new DataInputStream(socket.getInputStream());
            // создаем новый файл в созданной нами директории
            File file = new File(path.getPath() + File.separator + FileName);
            filePath = file.getPath()+"\\"+file.getName();
            System.out.println("recive file: "+FileName);



            // создаем новый выходной поток чтобы сохранить созданный файл на диск
            System.out.println("Creted new stream..." + file.getPath());
            FileOutputStream output = new FileOutputStream(file);
            System.out.println("New Stream is created!");
            // создаем массив типа byte с количесством элементов (байт) равным 1024 (1 Кбайт)
            byte[] b = new byte[1024];

            // принимаем и сохраняем файл на диск
            System.out.println("Save File...");
            int count = 0;  // переенная счетчика
            while (true) {
                count = input.read(b);
                if (count > 0) {
                    output.write(b, 0, count);
                }
                if (count == -1) {
                    System.out.println("File is saved!");
                    output.close();

                    break;
                }


            }
            disFileName.close();
            input.close();

        }catch(Exception e){
            System.out.println("Error: File not recive...");
        }
       Adapter.getInstance().printMassage("FILE RECIVE: "+ getFilePath());
    }
}
