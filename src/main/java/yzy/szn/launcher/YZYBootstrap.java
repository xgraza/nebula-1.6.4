package yzy.szn.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author graza
 * @since 02/17/24
 */
public final class YZYBootstrap {

    /**
     * If to print ASCII art on startup
     */
    private static final boolean REAL = false;

    private static File WORKING_DIRECTORY;

    public static void init(final File mcDataDir) throws IOException {
        WORKING_DIRECTORY = new File(mcDataDir, "yzy.szn");

        if (!WORKING_DIRECTORY.exists()) {
            final boolean mkdir = WORKING_DIRECTORY.mkdir();
            if (!mkdir) {
                throw new RuntimeException("Failed to create " + WORKING_DIRECTORY);
            }
        }

        if (REAL) {
            try (final InputStream is = YZYBootstrap.class.getResourceAsStream(
                    "/assets/minecraft/yzy/text/1.txt")) {

                if (is == null) {
                    throw new IOException();
                }

                int b;
                while ((b = is.read()) != -1) {
                    final char character = (char) b;
                    System.out.print(character);
                }
            }
        }

        YZY.INSTANCE.init();

        Runtime.getRuntime().addShutdownHook(new Thread(
                YZY.INSTANCE::shutdown, "yzy-szn-app"));
    }

    public static String getClientBrand() {
        return "vanilla_yzy";
    }
}
