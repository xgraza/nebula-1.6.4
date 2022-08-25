package optifine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils
{
    public static String getHashMd5(String data)
    {
        return getHash(data, "MD5");
    }

    public static String getHashSha1(String data)
    {
        return getHash(data, "SHA-1");
    }

    public static String getHashSha256(String data)
    {
        return getHash(data, "SHA-256");
    }

    public static String getHash(String data, String digest)
    {
        try
        {
            byte[] e = getHash(data.getBytes("UTF-8"), digest);
            return toHexString(e);
        }
        catch (Exception var3)
        {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static String toHexString(byte[] data)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < data.length; ++i)
        {
            sb.append(Integer.toHexString(data[i] & 255 | 256).substring(1, 3));
        }

        return sb.toString();
    }

    public static byte[] getHashMd5(byte[] data) throws NoSuchAlgorithmException
    {
        return getHash(data, "MD5");
    }

    public static byte[] getHashSha1(byte[] data) throws NoSuchAlgorithmException
    {
        return getHash(data, "SHA-1");
    }

    public static byte[] getHashSha256(byte[] data) throws NoSuchAlgorithmException
    {
        return getHash(data, "SHA-256");
    }

    public static byte[] getHash(byte[] data, String digest) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(digest);
        byte[] array = md.digest(data);
        return array;
    }
}
