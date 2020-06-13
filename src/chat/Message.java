package chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private Client sender;
    private Client receiver;
    private String message;
    private String timeStamp;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public Message(Client sender, Client receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;

        LocalDateTime localDateTime = LocalDateTime.now();
        timeStamp = dateTimeFormatter.format(localDateTime);
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
