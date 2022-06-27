package net.minecraft.src;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

class SoundPoolProtocolHandler extends URLStreamHandler
{
    final SoundPool theSoundPool;

    SoundPoolProtocolHandler(SoundPool par1SoundPool)
    {
        this.theSoundPool = par1SoundPool;
    }

    protected URLConnection openConnection(URL par1URL)
    {
        return new SoundPoolURLConnection(this.theSoundPool, par1URL, (SoundPoolProtocolHandler)null);
    }
}
