package com.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServeurJeu extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ConcurrentLinkedQueue<MessageReseau> messagesRecus;
    private boolean running = true;
    
    public ServeurJeu(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        messagesRecus = new ConcurrentLinkedQueue<>();
        System.out.println("Serveur démarré sur le port " + port);
    }
    
    @Override
    public void run() {
        try {
            System.out.println("En attente d'un client...");
            clientSocket = serverSocket.accept();
            System.out.println("Client connecté : " + clientSocket.getInetAddress());
            
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            
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
        return clientSocket != null && clientSocket.isConnected();
    }
    
    public void arreter() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
