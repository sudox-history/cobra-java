package ru.sudox.cobra.discovery;

import org.jetbrains.annotations.Nullable;
import ru.sudox.cobra.CobraLoader;

public final class CobraDiscovery {

    private final long pointer;
    private CobraDiscoveryListener listener;

    public CobraDiscovery() {
        this.pointer = create(CobraLoader.getPointer());
    }

    @SuppressWarnings("unused")
    private void onFound(String host) {
        if (listener != null) {
            listener.onFound(this, host);
        }
    }

    @SuppressWarnings("unused")
    private void onClose(int error) {
        if (listener != null) {
            listener.onClose(this, CobraDiscoveryError.values()[error]);
        }
    }

    public CobraDiscoveryError scan() {
        return CobraDiscoveryError.values()[scan(pointer)];
    }

    public CobraDiscoveryError listen() {
        return CobraDiscoveryError.values()[listen(pointer)];
    }

    public CobraDiscoveryError close() {
        return CobraDiscoveryError.values()[close(pointer)];
    }

    public void setListener(@Nullable CobraDiscoveryListener listener) {
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() {
        destroy(pointer);
    }

    private static native void destroy(long pointer);

    private static native long create(long loaderPointer);

    private static native int close(long pointer);

    private native int listen(long pointer);

    private native int scan(long pointer);
}
