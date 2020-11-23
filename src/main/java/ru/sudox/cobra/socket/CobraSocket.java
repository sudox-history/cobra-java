package ru.sudox.cobra.socket;

import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.socket.exceptions.*;

import java.nio.ByteBuffer;

import static ru.sudox.cobra.socket.CobraSocketErrors.*;

public final class CobraSocket {

    private final long pointer;
    private CobraSocketListener listener;

    public CobraSocket(int writeQueueSize) {
        this.pointer = create(CobraLoader.getPointer(), writeQueueSize);
    }

    private CobraSocket(long pointer) {
        this.pointer = pointer;
    }

    static {
        CobraLoader.loadLibrary();
    }

    public void connect(String host, String port) throws CobraSocketAlreadyConnectedException, CobraSocketUnhandledException {
        int code = connect(pointer, host, port);

        if (code == ALREADY_CONNECTED_ERROR) {
            throw new CobraSocketAlreadyConnectedException();
        } else if (code != OK) {
            throw new CobraSocketUnhandledException(code);
        }
    }

    public void send(ByteBuffer buffer) throws CobraSocketNotConnectedException, CobraSocketUnhandledException {
        int status = send(pointer, buffer);

        if (status == NOT_CONNECTED_ERROR) {
            throw new CobraSocketNotConnectedException();
        } else if (status != OK) {
            throw new CobraSocketUnhandledException(status);
        }
    }

    public void close() throws CobraSocketNotConnectedException, CobraSocketUnhandledException {
        int status = close(pointer);

        if (status == NOT_CONNECTED_ERROR) {
            throw new CobraSocketNotConnectedException();
        } else if (status != OK) {
            throw new CobraSocketUnhandledException(status);
        }
    }

    public void onConnect() {
        if (listener != null) {
            listener.onConnect();
        }
    }

    public void onClose(int reason) {
        if (listener != null) {
            switch (reason) {
                case OK -> listener.onClose(null);
                case ALREADY_CONNECTED_ERROR -> listener.onClose(new CobraSocketAlreadyConnectedException());
                case NOT_CONNECTED_ERROR -> listener.onClose(new CobraSocketNotConnectedException());
                case RESOLVING_ERROR -> listener.onClose(new CobraSocketResolvingException());
                case CONNECTING_ERROR -> listener.onClose(new CobraSocketConnectingException());
                case QUEUE_OVERFLOW_ERROR -> listener.onClose(new CobraSocketQueueOverflowException());
                case WRITING_ERROR -> listener.onClose(new CobraSocketWritingException());
                default -> listener.onClose(new CobraSocketUnhandledException(reason));
            }
        }
    }

    public void onData(ByteBuffer buffer) {
        if (listener != null) {
            listener.onData(buffer);
        }
    }

    public void setListener(CobraSocketListener listener) {
        this.listener = listener;
    }

    @Override
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native int send(long pointer, ByteBuffer buffer);

    private static native int close(long pointer);

    private static native void destroy(long pointer);

    private native int connect(long pointer, String host, String port);
}
