import org.jetbrains.annotations.NotNull;
import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.discovery.CobraDiscovery;
import ru.sudox.cobra.discovery.CobraDiscoveryError;
import ru.sudox.cobra.discovery.CobraDiscoveryListener;
import ru.sudox.cobra.environment.CobraEnvironment;

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
                System.out.println(error);
            }
        });

        System.out.println(discovery.listen());
        Thread.sleep(5000);
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-release\\cobra_java.dll");
    }
}