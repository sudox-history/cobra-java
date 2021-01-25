package ru.sudox.cobra.discovery.finder;

import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.discovery.CobraDiscoveryError;

public final class CobraDiscoveryInterfaceFinder {

    private final long pointer;
    private CobraDiscoveryInterfaceFinderListener listener;

    public CobraDiscoveryInterfaceFinder() {
        this.pointer = create(CobraLoader.getPointer());
    }

    static {
        CobraLoader.loadInternal();
    }

    public synchronized CobraDiscoveryError find() {
        return CobraDiscoveryError.values()[find(pointer)];
    }

    @SuppressWarnings("unused")
    private void onFound(String host) {
        if (listener != null) {
            listener.onFound(this, host);
        }
    }

    public void setListener(CobraDiscoveryInterfaceFinderListener listener) {
        this.listener = listener;
    }

    @Override
    protected void finalize() throws Throwable {
        destroy(pointer);
    }

    private static native long create(long loaderPointer);

    private static native void destroy(long pointer);

    private native int find(long pointer);
}
