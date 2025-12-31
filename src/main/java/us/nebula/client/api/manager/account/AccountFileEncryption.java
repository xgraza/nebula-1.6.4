package us.nebula.client.api.manager.account;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountFileEncryption
{
    private static final Cipher AES_CIPHER;

    static
    {
        try
        {
            AES_CIPHER = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
