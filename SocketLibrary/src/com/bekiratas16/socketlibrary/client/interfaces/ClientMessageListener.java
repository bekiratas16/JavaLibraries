/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bekiratas16.socketlibrary.client.interfaces;

/**
 *
 * @author ACER
 */
public interface ClientMessageListener {

    public void onSuccessMessage(String message);

    public void onFailMessage(String message);

}
