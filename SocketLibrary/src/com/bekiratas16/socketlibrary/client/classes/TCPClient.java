/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.client.classes;

import java.net.Socket;
import com.bekiratas16.socketlibrary.client.interfaces.TCPClientConnectionStateChangedListener;
import com.bekiratas16.socketlibrary.client.interfaces.TCPClientLinker;
import com.bekiratas16.socketlibrary.client.interfaces.ClientMessageListener;

/**
 *
 * @author ACER
 * @param <T>
 */
public abstract class TCPClient implements TCPClientLinker, ClientMessageListener, TCPClientConnectionStateChangedListener {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_TIMEOUT = 10000;

    private String host;
    private int port;
    private Socket socket;
    private int timeout;
    private boolean connected;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
//    private TCPConnectionStateListener connectionStateListener;
//    private ClientMessageListener messageListener;

    public TCPClient() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT);

    }

    public TCPClient(String host, int port) {
        this(host, port, DEFAULT_TIMEOUT);

    }

    public TCPClient(String host, int port, int timeout) {

        if (host == null || host.equals("") || port <= 0) {
            this.host = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
            this.timeout = DEFAULT_TIMEOUT;
            return;
        }

        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getIPAdress() {
        return host;
    }

    public void setIPAdress(String IPAdress) {
        this.host = IPAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    private ListenToServer listenFromServer;

    @Override
    public synchronized void sendMessage(String message) {
        listenFromServer.sendMessage(message);
    }

    @Override
    public synchronized void disconnect() {
        listenFromServer.stopListener();
    }

    @Override
    public synchronized void connect() {
        listenFromServer = new ListenToServer(this);
        listenFromServer.startListener();
    }
}
