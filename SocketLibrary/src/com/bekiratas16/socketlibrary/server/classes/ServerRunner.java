/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.server.classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public class ServerRunner extends Thread {

    private TCPServer server;

    public TCPServer getServer() {
        return server;
    }

    public ServerRunner(TCPServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.setServerSocket(new ServerSocket(server.getPort()));
            if (server.getTimeout() > 0) {
                server.getServerSocket().setSoTimeout(server.getTimeout());
            }
            server.setServerRunning(true);
            server.onServerStarted();

            while (server.isServerRunning()) {

                Socket socket = server.getServerSocket().accept();
                ListenToClient client = new ListenToClient(this, socket);
                client.setClientId(server.getNextUniqeID());
                server.getClients().add(client);
                client.startListener();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        stopServer();
        server.onServerStopped();
    }

    public synchronized void startServer() {
        server.setClients(new ArrayList<ListenToClient>());
        start();
    }

    public synchronized void stopServer() {
        server.setServerRunning(false);
        try {
            server.getServerSocket().close();
            for (int i = 0; i < server.getClients().size(); ++i) {
                ListenToClient client = server.getClients().get(i);
                client.stopListener();

            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
