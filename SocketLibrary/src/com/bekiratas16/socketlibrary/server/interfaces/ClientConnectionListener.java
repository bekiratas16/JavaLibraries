/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.server.interfaces;

import com.bekiratas16.socketlibrary.server.classes.ListenToClient;

/**
 *
 * @author ACER
 */
public interface ClientConnectionListener {
    
    public void onClientConnected(ListenToClient client);
    public  void onClientDisconnected(ListenToClient client);
    
}
