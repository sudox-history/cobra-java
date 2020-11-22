package ru.sudox.cobra.socket;

import java.nio.ByteBuffer;

public interface CobraSocketListener {
    void onConnect();
    void onData(ByteBuffer buffer);
    void onClose(Exception exception);
}
