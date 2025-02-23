package us.nebula.launcher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 02/22/25
 */
public final class HWID
{
    private static final MessageDigest MESSAGE_DIGEST;

    static
    {
        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String get()
    {
        final byte[] sha256Bytes = MESSAGE_DIGEST.digest((System.getenv("OS") + "_"
                + System.getenv("COMPUTERNAME") + "_"
                + System.getenv("USERNAME") + "_"
                + System.getenv("USERDOMAIN") + "_"
                + System.getenv("PROCESSOR_REVISION") + "_"
                + System.getenv("PROCESSOR_LEVEL") + "_"
                + System.getenv("PROCESSOR_ARCHITECTURE") + "_"
                + System.getenv("NUMBER_OF_PROCESSORS") + "_"
                + System.getenv("PROCESSOR_IDENTIFIER"))
                .getBytes(StandardCharsets.UTF_8));
        return Util.bytesToHex(sha256Bytes);
    }
}
