package com.geekbrains.chat.client;

@FunctionalInterface
public interface Callback {

    void onReceive(String message);

}
