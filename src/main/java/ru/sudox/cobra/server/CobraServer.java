package ru.sudox.cobra.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketError;
import ru.sudox.cobra.socket.CobraSocketListener;

import java.nio.ByteBuffer;

public final class CobraServer implements CobraSocketListener {

    private final long pointer;
    private CobraServerListener listener;

    public CobraServer(int writeQueueSize) {
        this.pointer = create(CobraLoader.getPointer(), writeQueueSize);
    }

    static {
        CobraLoader.loadInternal();
    }

    public CobraServerError listen(@NotNull String host, @NotNull String port) {
        return CobraServerError.values()[listen(pointer, host, port)];
    }

    public CobraServerError close() {
        return CobraServerError.values()[close(pointer)];
    }

    @Override
    public void onConnect(@NotNull CobraSocket socket) {
        // Ignore
    }

    @Override
    public void onData(@NotNull CobraSocket socket, @NotNull ByteBuffer buffer) {
        if (listener != null) {
            listener.onConnectionData(this, socket, buffer);
        }
    }

    @Override
    public void onClose(@NotNull CobraSocket socket, @NotNull CobraSocketError error) {
        if (listener != null) {
            listener.onConnectionClose(this, socket, error);
        }
    }

    @Override
    public void onDrain(@NotNull CobraSocket socket) {
        if (listener != null) {
            listener.onConnectionDrain(this, socket);
        }
    }

    public void setListener(@Nullable CobraServerListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    private void onConnectionOpen(CobraSocket socket) {
        socket.setListener(this);

        if (listener != null) {
            listener.onConnectionOpen(this, socket);
        }
    }

    @SuppressWarnings("unused")
    private void onServerClose(int error) {
        if (listener != null) {
            listener.onServerClose(this, CobraServerError.values()[error]);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native void destroy(long pointer);

    private native int listen(long pointer, String host, String port);

    private native int close(long pointer);
}
