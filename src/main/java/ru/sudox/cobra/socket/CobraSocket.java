package ru.sudox.cobra.socket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sudox.cobra.CobraLoader;

import java.nio.ByteBuffer;

public final class CobraSocket {

    private final long pointer;
    private CobraSocketListener listener;

    @SuppressWarnings("unused")
    public CobraSocket(int writeQueueSize) {
        this.pointer = create(CobraLoader.getPointer(), writeQueueSize);
    }

    @SuppressWarnings("unused")
    public CobraSocket(long pointer) {
        this.pointer = pointer;
    }

    static {
        CobraLoader.loadInternal();
    }

    public CobraSocketError connect(@NotNull String host, @NotNull String port) {
        return CobraSocketError.values()[connect(pointer, host, port)];
    }

    public CobraSocketError write(@NotNull ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("Supported only direct ByteBuffers.");
        }

        return CobraSocketError.values()[write(pointer, buffer)];
    }

    public CobraSocketError close() {
        return CobraSocketError.values()[close(pointer)];
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
    public void onClose(int reason) {
        if (listener != null) {
            listener.onClose(this, CobraSocketError.values()[reason]);
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

    private static native int write(long pointer, ByteBuffer buffer);

    private static native int close(long pointer);

    private static native void destroy(long pointer);

    private native int connect(long pointer, String host, String port);
}
