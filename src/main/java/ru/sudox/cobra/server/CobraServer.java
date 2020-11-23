package ru.sudox.cobra.server;

import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.server.exceptions.CobraServerAlreadyListeningException;
import ru.sudox.cobra.server.exceptions.CobraServerUnhandledException;
import ru.sudox.cobra.socket.CobraSocket;

import static ru.sudox.cobra.server.CobraServerErrors.*;

public final class CobraServer {

    private final long pointer;

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

    public void onConnectionOpen(CobraSocket socket) {

    }

    public void onServerClose(int error) {

    }

    @Override
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native void destroy(long pointer);

    private native int listen(long pointer, String host, String port);
}
