/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.client.classes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import com.bekiratas16.socketlibrary.client.interfaces.TCPClientLinker;
import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ACER
 */
public class ListenToServer extends Thread implements TCPClientLinker {

    public static final String END_OF_LINE = "\r\n";
    public static final int CONTROL_BUFFER_SIZE = 8;
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

        try {
            while (client.isConnected()) {
                readMessage();
            }
        } catch (IOException e) {
            readingText = null;
            client.onFailMessage("Client closed connection.\n");
            client.setConnected(false);
        }

        disconnect();
        client.onDisconnected();

    }

    public void startListener() {
        start();

    }

    public void stopListener() {
        disconnect();
        client.setConnected(false);
    }

    public void readMessage() throws IOException {

        byte[] firstRead = new byte[CONTROL_BUFFER_SIZE];
        int readedByte = bufferedInputStream.read(firstRead);
        readingText = new String(firstRead);
        if (readedByte == -1) {
            client.onFailMessage("Server closed client connection.\n");
            client.setConnected(false);
            return;
        }

        int readableBytes = bufferedInputStream.available();
        if (readableBytes > 0) {
            byte[] arrayOfByte = new byte[readableBytes];
            bufferedInputStream.read(arrayOfByte);
            readingText += new String(arrayOfByte);
        }
        client.onSuccessMessage(readingText);

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

            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream(), StandardCharsets.UTF_8)), true);
            client.setConnected(true);
            client.onConnected();
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }

    }

}
