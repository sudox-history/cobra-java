package ru.sudox.cobra;

public class CobraLoader {

    public static void load() {
        try {
            System.load("C:\\Users\\Kotlinovsky\\Projects\\cobra-java\\src\\main\\jni\\cmake-build-debug\\cobra_java.dll");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
