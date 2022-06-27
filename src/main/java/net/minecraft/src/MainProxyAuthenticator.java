package net.minecraft.src;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class MainProxyAuthenticator extends Authenticator
{
    final String field_111237_a;

    final String field_111236_b;

    public MainProxyAuthenticator(String par1Str, String par2Str)
    {
        this.field_111237_a = par1Str;
        this.field_111236_b = par2Str;
    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(this.field_111237_a, this.field_111236_b.toCharArray());
    }
}
