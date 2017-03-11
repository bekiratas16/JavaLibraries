/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.server.classes;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ACER
 */
public class ListenToClient extends Thread {

    private Socket socket;
    private int clientId;
    public static final int CONTROL_BUFFER_SIZE = 8;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    private ServerRunner serverRunner;
    private boolean connected;

    public boolean isConnected() {
        return connected;
    }

    private void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static final String END_OF_LINE = "\r\n";
    private BufferedInputStream bufferedInputStream;
    private PrintWriter printWriter;
    private String readingText;

    public ListenToClient(ServerRunner serverRunner, Socket socket) {
        this.socket = socket;
        this.serverRunner = serverRunner;

    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        prepare();
        if (!isConnected()) {
            return;
        }
        try {

            while (isConnected()) {
                readMessage();
            }
        } catch (IOException e) {
            readingText = null;
            serverRunner.getServer().onFailMessage(this, "Server closed" + clientId + "'s connection.\n");
            setConnected(false);
        }

        disconnect();
        serverRunner.getServer().onClientDisconnected(this);

    }

    public void readMessage() throws IOException {

        byte[] firstRead = new byte[CONTROL_BUFFER_SIZE];
        int readedByte = bufferedInputStream.read(firstRead);
        readingText = new String(firstRead);

        if (readedByte == -1) {
            serverRunner.getServer().onFailMessage(this, clientId + " Closed the connection.\n");
            setConnected(false);
            return;
        }

        int readableBytes = bufferedInputStream.available();
        if (readableBytes > 0) {
            byte[] arrayOfByte = new byte[readableBytes];
            bufferedInputStream.read(arrayOfByte);
            readingText += new String(arrayOfByte);
        }
        serverRunner.getServer().onSuccessMessage(this, readingText);

    }

    public synchronized void sendMessage(String message) {

        try {
            if (printWriter == null) {
                return;
            }
            String str = message + END_OF_LINE;
            printWriter.print(str);
            printWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startListener() {
        start();

    }

    public void stopListener() {
        disconnect();
        setConnected(false);
    }

    public synchronized void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;

            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
                bufferedInputStream = null;

            }
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        setConnected(false);

    }

    public synchronized void prepare() {

        try {
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
            setConnected(true);
            serverRunner.getServer().onClientConnected(this);
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();

        }

    }

}
