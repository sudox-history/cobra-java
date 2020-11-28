package ru.sudox.cobra.socket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public interface CobraSocketListener {
    void onConnect(@NotNull CobraSocket socket);

    void onData(@NotNull CobraSocket socket, @NotNull ByteBuffer buffer);

    void onClose(@NotNull CobraSocket socket, @Nullable Exception exception);

    void onDrain(@NotNull CobraSocket socket);
}
