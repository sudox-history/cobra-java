package ru.sudox.cobra.environment.impl;

import org.apache.commons.lang3.SystemUtils;
import ru.sudox.cobra.environment.CobraEnvironment;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class CobraHotspotEnvironment implements CobraEnvironment {

    private String path;

    @Override
    public void load() {
        if (path == null) {
            StringBuilder fileName = new StringBuilder("cobra_");

            if (SystemUtils.IS_OS_WINDOWS) {
                fileName.append("win");
            } else if (SystemUtils.IS_OS_LINUX) {
                fileName.append("linux");
            } else if (SystemUtils.IS_OS_MAC) {
                fileName.append("mac");
            } else if (SystemUtils.IS_OS_FREE_BSD) {
                fileName.append("bsd");
            }

            fileName.append("_");

            if (!SystemUtils.OS_ARCH.contains("arm") && !SystemUtils.OS_ARCH.contains("aarch")) {
                fileName.append(SystemUtils
                        .OS_ARCH
                        .toLowerCase()
                        .replace("amd64", "x86_64"));
            } else if (SystemUtils.OS_ARCH.contains("v8") || SystemUtils.OS_ARCH.contains("64")) {
                fileName.append("arm64");
            } else {
                fileName.append("arm");
            }

            if (SystemUtils.IS_OS_WINDOWS) {
                fileName.append(".dll");
            } else if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_FREE_BSD) {
                fileName.append(".so");
            } else if (SystemUtils.IS_OS_MAC) {
                fileName.append(".dylib");
            }

            String name = fileName.toString();
            URL url = getClass().getClassLoader().getResource(name);

            if (url == null) {
                throw new IllegalArgumentException("Architecture or OS not supported: " + fileName);
            }

            try {
                Path tempFile = Files.createTempFile(name, null);
                tempFile.toFile().deleteOnExit();

                try (InputStream stream = url.openStream()) {
                    Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }

                path = tempFile.toAbsolutePath().toString();
            } catch (IOException e) {
                // Ignore
            }
        }

        if (path != null) {
            System.load(path);
        }
    }
}
