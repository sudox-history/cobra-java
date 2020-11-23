package ru.sudox.cobra.server;

import ru.sudox.cobra.socket.CobraSocket;

import java.nio.ByteBuffer;

public interface CobraServerListener {
    void onConnectionOpen(CobraSocket socket);
    void onConnectionDrain(CobraSocket socket);
    void onConnectionData(CobraSocket socket, ByteBuffer buffer);
    void onConnectionClose(CobraSocket socket, Exception exception);
    void onServerClose(Exception exception);
}
