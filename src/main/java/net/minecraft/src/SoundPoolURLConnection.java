package net.minecraft.src;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class SoundPoolURLConnection extends URLConnection
{
    private final ResourceLocation field_110659_b;

    final SoundPool theSoundPool;

    private SoundPoolURLConnection(SoundPool par1SoundPool, URL par2URL)
    {
        super(par2URL);
        this.theSoundPool = par1SoundPool;
        this.field_110659_b = new ResourceLocation(par2URL.getPath());
    }

    public void connect() {}

    public InputStream getInputStream()
    {
        try
        {
            return SoundPool.func_110655_a(this.theSoundPool).getResource(this.field_110659_b).getInputStream();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    SoundPoolURLConnection(SoundPool par1SoundPool, URL par2URL, SoundPoolProtocolHandler par3SoundPoolProtocolHandler)
    {
        this(par1SoundPool, par2URL);
    }
}
