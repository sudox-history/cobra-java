package ru.sudox.cobra.socket;

import java.nio.ByteBuffer;

public interface CobraSocketListener {
    void onConnect(CobraSocket socket);
    void onData(CobraSocket socket, ByteBuffer buffer);
    void onClose(CobraSocket socket, Exception exception);
    void onDrain(CobraSocket socket);
}
