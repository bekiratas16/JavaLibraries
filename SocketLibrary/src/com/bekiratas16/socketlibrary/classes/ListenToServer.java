/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.classes;

import com.bekiratas16.socketlibrary.interfaces.MessageListener;
import com.bekiratas16.socketlibrary.interfaces.TCPConnectionStateListener;
import java.io.IOException;
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
public class ListenToServer extends Thread implements TCPLinker, MessageListener, TCPConnectionStateListener {

    public static final String END_OF_LINE = "\r\n";
    private BufferedInputStream bufferedInputStream;
    private PrintWriter printWriter;
    private final TCPClient client;
    private String readingText;

    public ListenToServer(TCPClient client) {
        this.client = client;
    }

    @Override
    public void run() {

        connect();
        if (!client.isConnected()) {
            return;
        }

        while (client.isConnected()) {
            readMessage();
        }

        disconnect();
        onDisconnect();

    }

    public void startListener() {
        this.start();

    }

    public void stopListener() {
        disconnect();
        client.setConnected(false);
    }

    public void readMessage() {
        try {

            int readedByte = bufferedInputStream.read();
            if (readedByte == -1) {
                onFailMessage("Server closed client connection.");
                client.setConnected(false);
                return;
            }
            readingText = "";
            readingText = Character.toString((char) readedByte);
            int readableBytes = bufferedInputStream.available();
            if (readableBytes > 0) {
                byte[] arrayOfByte = new byte[readableBytes];
                bufferedInputStream.read(arrayOfByte);
                readingText += new String(arrayOfByte);
                onSuccessMessage(readingText);
            }

        } catch (IOException e) {
            readingText = null;
            onFailMessage("Client closed connection");
            client.setConnected(false);
        }

    }

    @Override
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

    @Override
    public void onSuccessMessage(String message) {
        client.onSuccessMessage(message);

    }

    @Override
    public void onFailMessage(String message) {
        client.onFailMessage(message);
    }

    @Override
    public synchronized void disconnect() {
        try {
            if (client.getSocket() != null) {
                client.getSocket().close();
                client.setSocket(null);

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
        client.setConnected(false);

    }

    @Override
    public synchronized void connect() {
        try {
            client.setSocket(new Socket());
            client.getSocket().connect(
                    new InetSocketAddress(client.getIPAdress(), client.getPort()), client.getTimeout());
            bufferedInputStream = new BufferedInputStream(client.getSocket().getInputStream());
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream())), true);
            client.setConnected(true);
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
