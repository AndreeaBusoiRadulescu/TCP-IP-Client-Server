package chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MainServer {

    private static ArrayList<Client> clientList = new ArrayList<>();
    private static ArrayList<Message> messageList = new ArrayList<>();

    public static MainServer instance = new MainServer();

    public static MainServer getInstance() {
        return instance;
    }

    private MainServer() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Utilizare: Server <port>");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("Asteapta conexiuni client la portul " +
                    serverSocket.getLocalPort() + "...");

            while (true) {
                //Accepta conexiune de la un nou client intr-un thread distinct
                new ServerThread(serverSocket.accept(), instance).start();
            }
        } catch (IOException e) {
            System.err.println("Conexiune esuata pe portul " + port);
            System.exit(-2);
        }
    }

    public static void notifyNewClient(Client newClient){
        clientList.add(newClient);
        System.out.println("Lista de clienti arata asa acum: ");
        showClientList();
    }

    public static void notifyClientDisconnected(Client client){
        clientList.remove(client);
        System.out.println("Clientul " + client + " s-a deconectat!");
    }

    public static void notifyNewMessage(Message message){
        //"[ora] Catalin: mesaj"
        String outMessage = "[" + message.getTimeStamp() + "] " + message.getSender().getNume() + ": " + message.getMessage();
        try {
            DataOutputStream out = new DataOutputStream(message.getReceiver().getSocket().getOutputStream());
            out.writeUTF(outMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Adaugam mesajul in lista de mesaje
        addNewMessage(message);
    }

    public static void addNewMessage(Message message){
        messageList.add(message);
        System.out.println("Lista de mesaje arata asa acum: ");
        showMessageList();
    }

    private static void showMessageList() {
        for(Message message: messageList){
            System.out.println(message.toString());
        }
    }

    public static void showClientList(){
        for(Client client: clientList){
            System.out.println(client.toString());
        }
    }

    public static ArrayList<Client> getClientList() {
        return clientList;
    }

    public static Client getClientById(int id){
        for(Client client: clientList){
            if(client.getId()==id){
                return client;
            }
        }
        return null;
    }
}
