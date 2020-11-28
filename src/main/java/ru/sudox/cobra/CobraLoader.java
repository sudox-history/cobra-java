package ru.sudox.cobra;

import ru.sudox.cobra.environment.CobraEnvironment;

public final class CobraLoader {

    private static long pointer;
    private static CobraEnvironment environment;

    public static void init(CobraEnvironment env) {
        if (environment == null) {
            environment = env;
            environment.load();

            // Cache all functions & callbacks ...
            pointer = loadNative();
        }
    }

    public static void loadInternal() {
        if (environment == null) {
            throw new IllegalStateException("Environment not initialized.");
        }

        environment.load();
    }

    public static long getPointer() {
        return pointer;
    }

    private static native long loadNative();
}
