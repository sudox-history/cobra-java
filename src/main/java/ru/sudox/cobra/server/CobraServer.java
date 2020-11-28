package ru.sudox.cobra.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        CobraLoader.loadInternal();
    }

    public void listen(@NotNull String host, @NotNull String port) throws CobraServerAlreadyListeningException, CobraServerUnhandledException {
        int result = listen(pointer, host, port);

        if (result == ALREADY_LISTENING_ERROR) {
            throw new CobraServerAlreadyListeningException();
        } else if (result != OK) {
            throw new CobraServerUnhandledException(result);
        }
    }

    public void close() throws CobraServerAlreadyListeningException, CobraServerUnhandledException {
        int res = close(pointer);

        if (res == ALREADY_LISTENING_ERROR) {
            throw new CobraServerAlreadyListeningException();
        } else if (res != OK) {
            throw new CobraServerUnhandledException(res);
        }
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
    public void onClose(@NotNull CobraSocket socket, Exception exception) {
        if (listener != null) {
            listener.onConnectionClose(this, socket, exception);
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
    @SuppressWarnings("deprecation")
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native void destroy(long pointer);

    private native int listen(long pointer, String host, String port);

    private native int close(long pointer);
}
