package com.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientJeu extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ConcurrentLinkedQueue<MessageReseau> messagesRecus;
    private boolean running = true;
    
    public ClientJeu(String host, int port) throws IOException {
        socket = new Socket(host, port);
        messagesRecus = new ConcurrentLinkedQueue<>();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connecté au serveur " + host + ":" + port);
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                try {
                    MessageReseau message = (MessageReseau) in.readObject();
                    messagesRecus.add(message);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }
    
    public void envoyerMessage(MessageReseau message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public MessageReseau recupererMessage() {
        return messagesRecus.poll();
    }
    
    public boolean isConnecte() {
        return socket != null && socket.isConnected();
    }
    
    public void arreter() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
