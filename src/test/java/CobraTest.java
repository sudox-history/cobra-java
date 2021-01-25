import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.discovery.finder.CobraDiscoveryInterfaceFinder;
import ru.sudox.cobra.environment.CobraEnvironment;

public class CobraTest {

    public static void main(String[] args) throws InterruptedException {
        CobraLoader.init(new CobraTestEnv());

        CobraDiscoveryInterfaceFinder finder = new CobraDiscoveryInterfaceFinder();
        finder.setListener((finder1, host) -> {
            System.out.println(host);
        });

        finder.find();

        Thread.sleep(50000);
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-debug\\cobra_java.dll");
    }
}