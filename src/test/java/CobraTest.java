import ru.sudox.cobra.CobraLoader;
import ru.sudox.cobra.environment.CobraEnvironment;

import java.io.IOException;

public class CobraTest {

    public static void main(String[] args) {
        CobraLoader.init(new CobraTestEnv());
    }
}

class CobraTestEnv implements CobraEnvironment {

    @Override
    public void load() {
        System.load("C:\\Users\\Kotlinovsky\\cobra-java\\src\\main\\jni\\cmake-build-release\\cobra_java.dll");
    }
}