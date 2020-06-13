package chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientMessageListener extends Thread {
    private Socket client;
    private ClientSocket parent;

    public ClientMessageListener(Socket client, ClientSocket parent){
        this.client = client;
        this.parent = parent;
    }

    @Override
    public void run() {
        InputStream inFromServer = null;
        while(true){
            try {
                inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);
                String mesaj = in.readUTF();
                parent.notifyNewMessage(mesaj);
                //A primit semnal de inchidere
                if(mesaj.equalsIgnoreCase("---exit"))
                    break;
            } catch (IOException e) {
                //Conexiunea s-a incheiat dintr-un anume motiv
                break;
            }
        }
    }
}
