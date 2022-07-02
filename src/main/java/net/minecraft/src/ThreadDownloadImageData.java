package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ThreadDownloadImageData extends AbstractTexture
{
    private final String imageUrl;
    private final IImageBuffer imageBuffer;
    private BufferedImage bufferedImage;
    private Thread imageThread;
    private SimpleTexture imageLocation;
    private boolean textureUploaded;
    public boolean enabled = true;

    public ThreadDownloadImageData(String par1Str, ResourceLocation par2ResourceLocation, IImageBuffer par3IImageBuffer)
    {
        this.imageUrl = par1Str;
        this.imageBuffer = par3IImageBuffer;
        this.imageLocation = par2ResourceLocation != null ? new SimpleTexture(par2ResourceLocation) : null;
    }

    public int getGlTextureId()
    {
        int var1 = super.getGlTextureId();

        if (!this.textureUploaded && this.bufferedImage != null)
        {
            TextureUtil.uploadTextureImage(var1, this.bufferedImage);
            this.textureUploaded = true;
        }

        return var1;
    }

    public void getBufferedImage(BufferedImage par1BufferedImage)
    {
        this.bufferedImage = par1BufferedImage;
    }

    public void loadTexture(ResourceManager par1ResourceManager) throws IOException
    {
        if (this.bufferedImage == null)
        {
            if (this.imageLocation != null)
            {
                this.imageLocation.loadTexture(par1ResourceManager);
                this.glTextureId = this.imageLocation.getGlTextureId();
            }
        }
        else
        {
            TextureUtil.uploadTextureImage(this.getGlTextureId(), this.bufferedImage);
        }

        if (this.imageThread == null)
        {
            this.imageThread = new ThreadDownloadImageDataINNER1(this);
            this.imageThread.setDaemon(true);
            this.imageThread.setName("Skin downloader: " + this.imageUrl);
            this.imageThread.start();

            try
            {
                URL var2 = new URL(this.imageUrl);
                String var3 = var2.getPath();
                String var4 = "/MinecraftSkins/";
                String var5 = "/MinecraftCloaks/";

                if (var3.startsWith(var5))
                {
                    String var6 = var3.substring(var5.length());
                    String var7 = "http://s.optifine.net/capes/" + var6;
                    ThreadDownloadImage var8 = new ThreadDownloadImage(this, var7, new ImageBufferDownload());
                    var8.setDaemon(true);
                    var8.setName("Cape downloader: " + this.imageUrl);
                    var8.start();
                }
            }
            catch (Exception var9)
            {
                ;
            }
        }
    }

    public boolean isTextureUploaded()
    {
        if (!this.enabled)
        {
            return false;
        }
        else
        {
            this.getGlTextureId();
            return this.textureUploaded;
        }
    }

    static String getImageUrl(ThreadDownloadImageData par0ThreadDownloadImageData)
    {
        return par0ThreadDownloadImageData.imageUrl;
    }

    static IImageBuffer getImageBuffer(ThreadDownloadImageData par0ThreadDownloadImageData)
    {
        return par0ThreadDownloadImageData.imageBuffer;
    }
}
