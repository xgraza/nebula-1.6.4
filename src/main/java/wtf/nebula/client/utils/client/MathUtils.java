package wtf.nebula.client.utils.client;

import wtf.nebula.client.core.Nebula;

import java.util.Random;

public class MathUtils {
    public static final Random RNG = new Random();

    public static double interpolate(double start, double end, float partialTicks) {
        return end + partialTicks * (start - end);
    }

    public static int random(int min, int max) {
        return RNG.nextInt((max + 1) - min) + min;
    }

    public static boolean randomBoolean() {
        return RNG.nextBoolean();
    }

    public static <T> T randomElement(T[] arr) {
        int length = arr.length;
        if (length == 0) {
            return null;
        }

        try {
            return arr[(int) (Math.floor(RNG.nextDouble() * length) % length)];
        } catch (Exception e) {
            if (Nebula.VERSION.isDev()) {
                Nebula.LOGGER.error("Ur retarded and cant even do math right lel");
                e.printStackTrace();
            }

            return arr[0];
        }
    }
}
