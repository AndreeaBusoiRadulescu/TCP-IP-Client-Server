package chat;

import java.net.Socket;

public class Client {

    private int id;
    private String nume;
    private Socket socket;

    public Client(int id, String nume, Socket socket) {
        this.id = id;
        this.nume = nume;
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", socket=" + socket +
                '}';
    }
}
