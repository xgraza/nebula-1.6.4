package wtf.nebula.client.utils.client;

import wtf.nebula.client.core.Launcher;

import java.util.Random;

public class MathUtils {
    public static final Random RNG = new Random();

    public static int random(int min, int max) {
        return RNG.nextInt((max + 1) - min) + min;
    }

    public static <T> T randomElement(T[] arr) {
        int length = arr.length;
        if (length == 0) {
            return null;
        }

        try {
            return arr[(int) (Math.floor(RNG.nextDouble() * length) % length)];
        } catch (Exception e) {
            if (Launcher.VERSION.isDev()) {
                Launcher.LOGGER.error("Ur retarded and cant even do math right lel");
                e.printStackTrace();
            }

            return arr[0];
        }
    }
}
