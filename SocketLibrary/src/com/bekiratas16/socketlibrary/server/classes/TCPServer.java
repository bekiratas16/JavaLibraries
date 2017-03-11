/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.server.classes;

import com.bekiratas16.socketlibrary.server.interfaces.ClientConnectionListener;
import com.bekiratas16.socketlibrary.server.interfaces.ServerMessageListener;
import com.bekiratas16.socketlibrary.server.interfaces.TCPServerRunningStateChangedListener;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public abstract class TCPServer implements ClientConnectionListener, TCPServerRunningStateChangedListener, ServerMessageListener {

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_TIMEOUT = -1;

    private ArrayList<ListenToClient> clients;
    private int port;
    private boolean serverRunning;
    private ServerSocket serverSocket;
    private ServerRunner runner;
    private int timeout;
    private int uniqeID;
    
    
    public TCPServer(int port, int timeout) {
        this.port = port;
        this.timeout = timeout;
        uniqeID=0;
    }


    public synchronized int getNextUniqeID() {
        return ++uniqeID;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public TCPServer() {
        this(DEFAULT_PORT, DEFAULT_TIMEOUT);

    }

    public TCPServer(int port) {
        this(port, DEFAULT_TIMEOUT);

    }

    public ArrayList<ListenToClient> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ListenToClient> clients) {
        this.clients = clients;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized void start() {
        runner = new ServerRunner(this);
        runner.startServer();

    }

    public synchronized void stop() {
        runner.stopServer();
    }

    public void sendMessage(ListenToClient sender, ListenToClient receiver, String message) {
        receiver.sendMessage(message);
    }

    public void broadCastMessage(ListenToClient sender, String message) {

        for (int i = 0; i < clients.size(); i++) {
            ListenToClient receiver = clients.get(i);
            if (receiver != null && receiver.isConnected()) {
                sendMessage(sender, receiver, message);
            }
        }

    }

}
