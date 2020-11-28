package ru.sudox.cobra.socket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @SuppressWarnings("unused")
    private CobraSocket(long pointer) {
        this.pointer = pointer;
    }

    static {
        CobraLoader.loadInternal();
    }

    public void connect(@NotNull String host, @NotNull String port) throws CobraSocketAlreadyConnectedException, CobraSocketUnhandledException {
        int code = connect(pointer, host, port);

        if (code == ALREADY_CONNECTED_ERROR) {
            throw new CobraSocketAlreadyConnectedException();
        } else if (code != OK) {
            throw new CobraSocketUnhandledException(code);
        }
    }

    public void send(@NotNull ByteBuffer buffer) throws CobraSocketNotConnectedException, CobraSocketUnhandledException, CobraSocketQueueOverflowException, CobraSocketWritingException {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("Supported only direct ByteBuffers.");
        }

        int status = send(pointer, buffer);

        if (status != OK) {
            switch (status) {
                case NOT_CONNECTED_ERROR: throw new CobraSocketNotConnectedException();
                case QUEUE_OVERFLOW_ERROR: throw new CobraSocketQueueOverflowException();
                case WRITING_ERROR: throw new CobraSocketWritingException();
                default: throw new CobraSocketUnhandledException(status);
            }
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

    public void setListener(@Nullable CobraSocketListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    private void onConnect() {
        if (listener != null) {
            listener.onConnect(this);
        }
    }

    @SuppressWarnings("unused")
    private void onClose(int reason) {
        if (listener != null) {
            switch (reason) {
                case OK: {
                    listener.onClose(this, null);
                    break;
                }
                case ALREADY_CONNECTED_ERROR: {
                    listener.onClose(this, new CobraSocketAlreadyConnectedException());
                    break;
                }
                case NOT_CONNECTED_ERROR: {
                    listener.onClose(this, new CobraSocketNotConnectedException());
                    break;
                }
                case RESOLVING_ERROR: {
                    listener.onClose(this, new CobraSocketResolvingException());
                    break;
                }
                case CONNECTING_ERROR: {
                    listener.onClose(this, new CobraSocketConnectingException());
                    break;
                }
                case QUEUE_OVERFLOW_ERROR: {
                    listener.onClose(this, new CobraSocketQueueOverflowException());
                    break;
                }
                case WRITING_ERROR: {
                    listener.onClose(this, new CobraSocketWritingException());
                    break;
                }
                default: {
                    listener.onClose(this, new CobraSocketUnhandledException(reason));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void onDrain() {
        if (listener != null) {
            listener.onDrain(this);
        }
    }

    @SuppressWarnings("unused")
    private void onData(ByteBuffer buffer) {
        if (listener != null) {
            listener.onData(this, buffer);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() {
        destroy(pointer);
    }

    private static native long create(long loaderPointer, int writeQueueSize);

    private static native int send(long pointer, ByteBuffer buffer);

    private static native int close(long pointer);

    private static native void destroy(long pointer);

    private native int connect(long pointer, String host, String port);
}
