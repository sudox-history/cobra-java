import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.environment.CobraEnvironment;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketError;
import ru.sudox.cobra.socket.CobraSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class CobraTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        CobraLoader.init(new CobraTestEnv());
        CobraSocket socket = new CobraSocket(65535);
        Semaphore semaphore = new Semaphore(0);

        socket.setListener(new CobraSocketListener() {
            @Override
            public void onConnect(@NotNull CobraSocket socket) {
                System.out.println(socket);
            }

            @Override
            public void onData(@NotNull CobraSocket socket, @NotNull ByteBuffer buffer) {
            }

            @Override
            public void onClose(@NotNull CobraSocket socket, @NotNull CobraSocketError error) {
                System.out.println(error);
            }

            @Override
            public void onDrain(@NotNull CobraSocket socket) {
            }
        });

        System.out.println(socket.connect("127.0.0.1", "7899"));
        semaphore.acquire();
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-debug\\cobra_java.dll");
    }
}