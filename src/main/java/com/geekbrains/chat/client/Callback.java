package com.geekbrains.chat.client;

import java.util.List;

@FunctionalInterface
public interface Callback {

    void onReceive(List<String> files);

}
