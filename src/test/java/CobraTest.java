import org.jetbrains.annotations.NotNull;
import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.discovery.CobraDiscovery;
import ru.sudox.cobra.discovery.CobraDiscoveryError;
import ru.sudox.cobra.discovery.CobraDiscoveryListener;
import ru.sudox.cobra.environment.CobraEnvironment;
import ru.sudox.cobra.server.CobraServer;
import ru.sudox.cobra.server.CobraServerError;
import ru.sudox.cobra.server.CobraServerListener;
import ru.sudox.cobra.socket.CobraSocket;
import ru.sudox.cobra.socket.CobraSocketError;
import ru.sudox.cobra.socket.CobraSocketListener;

import java.nio.ByteBuffer;

public class CobraTest {

    public static void main(String[] args) throws InterruptedException {
        CobraLoader.init(new CobraTestEnv());

        CobraSocket socket = new CobraSocket(32);
        socket.setListener(new CobraSocketListener() {
            @Override
            public void onConnect(@NotNull CobraSocket socket) {
                socket.write(ByteBuffer.allocateDirect(4).put((byte) 1).put((byte) 2).put((byte) 3).put((byte) 4));
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

        socket.connect("127.0.0.1", "5556");
        Thread.sleep(500000);
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-release\\cobra_java.dll");
    }
}