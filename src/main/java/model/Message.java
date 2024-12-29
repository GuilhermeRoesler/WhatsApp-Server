package model;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private long timestamp;
    private String conversationId;
    
    public Message() {}
    
    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.conversationId = generateConversationId(sender, receiver);
    }

    public static String generateConversationId(String sender, String receiver) {
        if (sender.compareToIgnoreCase(receiver) < 0) {
            return sender + "_" + receiver;
        } else {
            return receiver + "_" + sender;
        }
    }
    
    // Getters and setters
    public  String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
