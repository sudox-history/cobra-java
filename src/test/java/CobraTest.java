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

import java.nio.ByteBuffer;

public class CobraTest {

    public static void main(String[] args) throws InterruptedException {
        CobraLoader.init(new CobraTestEnv());

        CobraDiscovery discovery = new CobraDiscovery();
        discovery.setListener(new CobraDiscoveryListener() {
            @Override
            public void onFound(@NotNull CobraDiscovery discovery, @NotNull String host) {
                System.out.println(host);
            }

            @Override
            public void onClose(@NotNull CobraDiscovery discovery, @NotNull CobraDiscoveryError error) {

            }
        });

        discovery.scan();
        Thread.sleep(500000);
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-release\\cobra_java.dll");
    }
}