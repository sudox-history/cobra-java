package ru.sudox.cobra;

public final class CobraLoader {

    private final static long pointer;

    static {
        loadLibrary();

        // Cache all functions & callbacks ...
        pointer = loadNative();
    }

    public static void loadLibrary() {
        try {
            System.load("C:\\Users\\Kotlinovsky\\Projects\\cobra-java\\src\\main\\jni\\cmake-build-debug\\cobra_java.dll");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getPointer() {
        return pointer;
    }

    private static native long loadNative();
}
