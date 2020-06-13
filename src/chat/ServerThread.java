package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ServerThread extends Thread {
    private Socket clientSocket = null;
    private static int nrClienti = 0;
    private static MainServer mainServer;
    private Client receiver;

    public ServerThread(Socket socket, MainServer mainServer) {
        super("chat.ServerThread");
        this.clientSocket = socket;
        this.setName(this.getName() + "-" + ++nrClienti);
        ServerThread.mainServer = mainServer;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())){

            System.out.println("Conexiune acceptata de la client " +
                    clientSocket.getRemoteSocketAddress() + "; " + this.getName());

            String clientMessage = null;

            //Primul mesaj este prin conventie ID-ul si numele utilizatorului
            clientMessage = in.readUTF();
            String[] parts = clientMessage.split(",");
            int id = Integer.parseInt(parts[0].strip());
            String nume = parts[1].strip();

            //Client nou
            //Clientul are id, nume si un socket asociat
            //Acesta este unicul client al acestui ServerThread
            Client client = new Client(id, nume, clientSocket);

            //Notificam serverul principal ca avem un nou client
            MainServer.notifyNewClient(client);

            while (true) {
                //asteapta mesaj de la client
                clientMessage = in.readUTF();

                //Daca mesajul incepe cu "/pentru" -> comanda
                /*
                 *  CMD: /pentru <clientid>
                 *  Selecteaza clientul caruia ii va trimite urmatoarele mesaje
                 */
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    //Trimitem un ultim mesaj de deconectare catre clientSocket
                    //Conventie: Daca serverul trimite '---exit' catre client, ii comunica acestuia faptul ca a fost inchisa conexiunea
                    out.writeUTF("---exit");

                    //Inchidem conexiunea si scoatem utilizatorul din lista de utilizatori
                    //Oprim canalele de comunicare
                    in.close();
                    out.close();
                    //Deconectam utilizatorul
                    client.getSocket().close();
                    //Notificam serverul principal ca acest utilizator s-a deconectat
                    MainServer.notifyClientDisconnected(client);
                    break;
                }
                else if(clientMessage.equalsIgnoreCase("/utilizatori")){
                    //Afisam clientului lista cu utilizatorii conectati in acest moment
                    ArrayList<Client> listaClienti = MainServer.getClientList();
                    StringBuilder messageForClient = new StringBuilder();
                    for ( Client c : listaClienti){
                        messageForClient.append("Utilizatorul ").append(c.getNume()).append(" are id-ul pentru conectare ").append(c.getId()).append("\n");
                    }
                    out.writeUTF(Objects.requireNonNullElse(messageForClient.toString(), "Nu exista inca utilizatori conectati!"));

                }
                else if(clientMessage.startsWith("/pentru")){
                    String[] parameters = clientMessage.split(" ");
                    if(parameters.length != 2){
                        //Nu a specificat ID-ul clientului cu care vrea sa vorbeasca
                        out.writeUTF("ERROR! USAGE: /pentru <utilizator_id>");
                    }
                    else
                    {
                        Client searched = MainServer.getClientById(Integer.parseInt(parameters[1]));
                        if(searched != null){
                            receiver = searched;
                            out.writeUTF("Te-ai conectat cu " + receiver + " !");
                        }
                        else{
                            out.writeUTF("ERROR! Id invalid!");
                        }
                    }
                }
                else {
                    //sunt mesaje pt receiver

                    //pentru adaugare in lista de mesaje

                    if(receiver == null){
                        out.writeUTF("ERROR! Comenzi disponibile: /exit, /utilizatori, /pentru <utilizator_id>");
                        continue;
                    }

                    Message newMessage = new Message(client, receiver, clientMessage);
                    //Notificam MainSever ca s-a primit un mesaj nou.
                    //MainSever va trimite mai departe mesajul catre receiver
                    MainServer.notifyNewMessage(newMessage);

                    System.out.println("DEBUGGING - MESAJ PRIMIT: " + clientMessage + " (" + this.getName() + ")");

                    //Aici poate urma o procesare realizata de server
                    sleep(500);
                }
            }

        } catch (EOFException eof) {
            System.out.println("Deconectare de la client " +
                    "(" + this.getName() + ")");
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
