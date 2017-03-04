/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.classes;

import com.bekiratas16.socketlibrary.interfaces.MessageListener;
import com.bekiratas16.socketlibrary.interfaces.TCPConnectionStateListener;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import com.bekiratas16.socketlibrary.interfaces.TCPLinker;

/**
 *
 * @author ACER
 */
public class ListenToServer<T> extends Thread implements TCPLinker<T>, MessageListener<T>, TCPConnectionStateListener {

    private boolean connected;
    public static final String END_OF_LINE = "\r\n";

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    private BufferedInputStream bufferedInputStream;
    private PrintWriter printWriter;
    private final TCPClient client;
    private final Gson gson;
    private String readingText;

    public ListenToServer(TCPClient client) {
        this.client = client;
        gson = new Gson();
        
  

    }

    @Override
    public void run() {

        connect();
        if (!isConnected()) {
            return;
        }

        readMessage();

    }

    public void startListener() {
        this.start();

    }

    public void stopListener() {
        setConnected(false);
    }

    public void readMessage() {
        try {

            int i = bufferedInputStream.read();
            if (i == -1) {
                return;
            }
            readingText = readingText + "" + (char) i;
            int j = bufferedInputStream.available();
            if (j > 0) {
                byte[] arrayOfByte = new byte[j];
                bufferedInputStream.read(arrayOfByte);
                readingText = readingText + new String(arrayOfByte);
            }

        } catch (IOException e) {
            readingText = null;
            disconnect();

        }

    }

    @Override
    public void sendMessage(T message) {

        try {
            if (printWriter != null) {
                String str = gson.toJson(message) + END_OF_LINE;
                printWriter.print(str);
                printWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessMessage(T message) {
        client.onSuccessMessage(message);

    }

    @Override
    public void onFailMessage(T message) {
        client.onFailMessage(message);
    }

    @Override
    public void disconnect() {
        try {
            if (client.getSocket() != null) {
                client.getSocket().close();
            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        setConnected(false);
        onDisconnect();

    }

    @Override
    public void connect() {
        try {
            client.setSocket(new Socket());
            client.getSocket().connect(
                    new InetSocketAddress(client.getIPAdress(), client.getPort()), client.getTimeout());
            bufferedInputStream = new BufferedInputStream(client.getSocket().getInputStream());
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream())), true);
            setConnected(true);
            onConnect();
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }

    }

    @Override
    public void onConnect() {
        client.onConnect();
    }

    @Override
    public void onDisconnect() {
        client.onDisconnect();
    }

}
