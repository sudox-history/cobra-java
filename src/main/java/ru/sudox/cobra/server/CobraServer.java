package ru.sudox.cobra.server;

import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.server.exceptions.*;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketListener;

import java.nio.ByteBuffer;

import static ru.sudox.cobra.server.CobraServerErrors.*;

public final class CobraServer implements CobraSocketListener {

    private final long pointer;
    private CobraServerListener listener;

    public CobraServer(int writeQueueSize) {
        this.pointer = create(CobraLoader.getPointer(), writeQueueSize);
    }

    static {
        CobraLoader.loadLibrary();
    }

    public void listen(String host, String port) throws CobraServerAlreadyListeningException, CobraServerUnhandledException {
        int result = listen(pointer, host, port);

        if (result == ALREADY_LISTENING_ERROR) {
            throw new CobraServerAlreadyListeningException();
        } else if (result != OK) {
            throw new CobraServerUnhandledException(result);
        }
    }

    public void close() {
        int res = close(pointer);


    }

    public void onConnectionOpen(CobraSocket socket) {
        socket.setListener(this);

        if (listener != null) {
            listener.onConnectionOpen(this, socket);
        }
    }

    public void onServerClose(int error) {
        if (listener != null) {
            switch (error) {
                case OK -> listener.onServerClose(this, null);
                case ALREADY_LISTENING_ERROR -> listener.onServerClose(this, new CobraServerAlreadyListeningException());
                case RESOLVING_ERROR -> listener.onServerClose(this, new CobraServerResolvingException());
                case BINDING_ERROR -> listener.onServerClose(this, new CobraServerBindingException());
                case LISTENING_ERROR -> listener.onServerClose(this, new CobraServerListeningException());
                default -> listener.onServerClose(this, new CobraServerUnhandledException(error));
            }
        }
    }

    @Override
    public void onConnect(CobraSocket socket) {
        // Ignore
    }

    @Override
    public void onData(CobraSocket socket, ByteBuffer buffer) {
        if (listener != null) {
            listener.onConnectionData(this, socket, buffer);
        }
    }

    @Override
    public void onClose(CobraSocket socket, Exception exception) {
        if (listener != null) {
            listener.onConnectionClose(this, socket, exception);
        }
    }

    @Override
    public void onDrain(CobraSocket socket) {
        if (listener != null) {
            listener.onConnectionDrain(this, socket);
        }
    }

    public void setListener(CobraServerListener listener) {
        this.listener = listener;
    }

    @Override
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native void destroy(long pointer);

    private native int listen(long pointer, String host, String port);

    private native int close(long pointer);
}
