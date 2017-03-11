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
public interface ServerMessageListener {

    public void onSuccessMessage(ListenToClient client, String message);

    public void onFailMessage(ListenToClient client, String message);

}
