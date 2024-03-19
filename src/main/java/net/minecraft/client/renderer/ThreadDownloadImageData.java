package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.src.ThreadDownloadImage;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture
{
    private static final Logger logger = LogManager.getLogger();
    private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
    private final String imageUrl;
    private final IImageBuffer imageBuffer;
    private BufferedImage bufferedImage;
    private Thread imageThread;
    private boolean textureUploaded;
    private static final String __OBFID = "CL_00001049";
    public boolean enabled = true;

    public ThreadDownloadImageData(String par1Str, ResourceLocation par2ResourceLocation, IImageBuffer par3IImageBuffer)
    {
        super(par2ResourceLocation);
        this.imageUrl = par1Str;
        this.imageBuffer = par3IImageBuffer;
    }

    private void checkTextureUploaded()
    {
        if (!this.textureUploaded && this.bufferedImage != null)
        {
            this.textureUploaded = true;

            if (this.textureLocation != null)
            {
                this.deleteGlTexture();
            }

            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
        }
    }

    public int getGlTextureId()
    {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage p_147641_1_)
    {
        this.bufferedImage = p_147641_1_;
    }

    public void loadTexture(IResourceManager par1ResourceManager) throws IOException
    {
        if (this.bufferedImage == null && this.textureLocation != null)
        {
            super.loadTexture(par1ResourceManager);
        }

        if (this.imageThread == null)
        {
            this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet())
            {
                private static final String __OBFID = "CL_00001050";
                public void run()
                {
                    HttpURLConnection var1 = null;

                    try
                    {
                        var1 = (HttpURLConnection)(new URL(ThreadDownloadImageData.this.imageUrl)).openConnection(Minecraft.getMinecraft().getProxy());
                        var1.setDoInput(true);
                        var1.setDoOutput(false);
                        var1.connect();

                        if (var1.getResponseCode() / 100 == 2)
                        {
                            BufferedImage var6 = ImageIO.read(var1.getInputStream());

                            if (ThreadDownloadImageData.this.imageBuffer != null)
                            {
                                var6 = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(var6);
                            }

                            ThreadDownloadImageData.this.setBufferedImage(var6);
                            return;
                        }

                        if (var1.getErrorStream() != null)
                        {
                            Config.readAll(var1.getErrorStream());
                        }
                    }
                    catch (Exception var61)
                    {
                        ThreadDownloadImageData.logger.error("Couldn\'t download http texture", var61);
                        return;
                    }
                    finally
                    {
                        if (var1 != null)
                        {
                            var1.disconnect();
                        }
                    }
                }
            };
            this.imageThread.setDaemon(true);
            this.imageThread.setName("Skin downloader: " + this.imageUrl);
            this.imageThread.start();

            try
            {
                URL e = new URL(this.imageUrl);
                String path = e.getPath();
                String prefixSkin = "/MinecraftSkins/";
                String prefixCape = "/MinecraftCloaks/";

                if (path.startsWith(prefixCape))
                {
                    String file = path.substring(prefixCape.length());
                    String ofUrl = "http://s.optifine.net/capes/" + file;
                    ThreadDownloadImage t = new ThreadDownloadImage(this, ofUrl, new ImageBufferDownload());
                    t.setDaemon(true);
                    t.setName("Cape downloader: " + this.imageUrl);
                    t.start();
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
            this.checkTextureUploaded();
            return this.textureUploaded;
        }
    }
}
