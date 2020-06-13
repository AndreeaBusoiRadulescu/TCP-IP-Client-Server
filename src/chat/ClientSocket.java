package chat;

import java.io.*;
import java.net.Socket;


public class ClientSocket {

    private static ClientSocket instance = new ClientSocket();

    private ClientSocket() {
    }

    public static ClientSocket getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        // "localhost" este numele asociat adresei de IP 127.0.0.1
        if (args.length != 4) {
            System.err.println("Utilizare: java Client " +
                    "<nume server> <port> <id client> <nume client>");
            System.exit(-1);
        }

        String numeServer = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            System.out.println("Conectare la " + numeServer +
                    " port " + String.valueOf(port));
            Socket client = new Socket(numeServer, port);

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Scrie '/exit' pentru a te deconecta!");
            System.out.println("Scrie '/pentru <utilizator_id>' pentru a incepe sa vorbesti cu un utilizator!");
            System.out.println("Scrie '/utilizatori' pentru a vedea lista cu utilizatori!");

            // Repeta pana cand este furnizat mesajul 'exit'

            int clientID = Integer.parseInt(args[2]);
            String numeClient = args[3];

            //Trimitem ID-ul, numele utilizatorului
            out.writeUTF(clientID + "," + numeClient);

            //Pornim thread pentru primit mesaje
            new ClientMessageListener(client, instance).start();

            while (true) {
                String mesaj = commandLine.readLine();
                try{
                    out.writeUTF(mesaj);
                }
                catch (Exception e){
                    //Daca nu poate trimite mesaj inseamna ca serverul a incheiat conexiunea cu clientul
                    //Atunci oprim programul
                    in.close();
                    out.close();
                    System.out.println("Conexiunea a fost oprita de catre server");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void notifyNewMessage(String message) {
        //S-a primit mesaj nou
        if(message.equalsIgnoreCase("---exit")) {
            //S-a primit mesaj de inchidere
            System.out.println("Sesiune inchisa! La revedere!");
            System.exit(1);
        }
        else
        {
            System.out.println(message);
        }
    }
}
