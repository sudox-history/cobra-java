package ru.sudox.cobra.server;

import ru.sudox.cobra.socket.CobraSocket;

import java.nio.ByteBuffer;

public interface CobraServerListener {
    void onConnectionOpen(CobraServer server, CobraSocket socket);

    void onConnectionDrain(CobraServer server, CobraSocket socket);

    void onConnectionData(CobraServer server, CobraSocket socket, ByteBuffer buffer);

    void onConnectionClose(CobraServer server, CobraSocket socket, Exception exception);

    void onServerClose(CobraServer server, Exception exception);
}
