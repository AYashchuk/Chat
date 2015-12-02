package Frame.serverNetworking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yashchuk A.F.
 */
public  class SendFile extends Thread {
    File file;
    Socket socket;                         
    ServerSocket serv;
    static String fileName;
    File MyDir= new File("C:\\Chat\\tmp\\");

    public SendFile(String ip, int port){
        if(!MyDir.exists())    MyDir.mkdirs();

        try {
            System.out.println(port);
            System.out.println(ip);
            this.socket = new Socket(ip,port);   // подключаюсь к клиенту который хочет передать файл
            this.serv   = new ServerSocket(port+1);// создаю соединение для клиента который ДОЛЖЕН принять файл

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run(){
                Socket send=null;
                receiveFile(MyDir,socket);
                while (send == null){
                    try {
                         send =  serv.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sendFile(new File("C:\\Chat\\tmp\\"+fileName),send);
        try {
            serv.close();
            send.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File tmp = new File("C:\\Chat\\tmp\\"+fileName);
        if(tmp.exists()) tmp.delete();
    }

    private  void sendFile(File file, Socket socket) {
        try{
            System.out.println(socket);
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
            System.out.println(socket);
            // поток для приема строки от сервера содержащей название файла
            DataInputStream disFileName = new DataInputStream(socket.getInputStream());
            System.out.println("Created new INstream...");
            // считуем имя файла
            String FileName = disFileName.readUTF();
            fileName = FileName;
            System.out.println("Recive File name: (" + FileName + ")");


            // создаем новый входной поток для записи принятого файла
            DataInputStream input = new DataInputStream(socket.getInputStream());
            // создаем новый файл в созданной нами директории
            File file = new File(path.getPath() + File.separator + FileName);
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
    }
}
