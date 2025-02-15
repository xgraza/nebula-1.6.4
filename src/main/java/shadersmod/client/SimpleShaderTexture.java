package shadersmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.src.TextureUtils;
import org.apache.commons.io.IOUtils;
import shadersmod.common.SMCLog;

public class SimpleShaderTexture extends AbstractTexture
{
    private String texturePath;
    private static final IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

    public SimpleShaderTexture(String texturePath)
    {
        this.texturePath = texturePath;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        InputStream inputStream = Shaders.getShaderPackResourceStream(this.texturePath);

        if (inputStream == null)
        {
            throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
        }
        else
        {
            try
            {
                BufferedImage bufferedimage = TextureUtils.readBufferedImage(inputStream);
                TextureMetadataSection tms = this.loadTextureMetadataSection();
                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, tms.getTextureBlur(), tms.getTextureClamp());
            }
            finally
            {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private TextureMetadataSection loadTextureMetadataSection()
    {
        String pathMeta = this.texturePath + ".mcmeta";
        String sectionName = "texture";
        InputStream inMeta = Shaders.getShaderPackResourceStream(pathMeta);

        if (inMeta != null)
        {
            IMetadataSerializer ms = METADATA_SERIALIZER;
            BufferedReader brMeta = new BufferedReader(new InputStreamReader(inMeta));
            TextureMetadataSection var8;

            try
            {
                JsonObject re = (new JsonParser()).parse(brMeta).getAsJsonObject();
                TextureMetadataSection meta = (TextureMetadataSection)ms.parseMetadataSection(sectionName, re);

                if (meta == null)
                {
                    return new TextureMetadataSection(false, false, new ArrayList());
                }

                var8 = meta;
            }
            catch (RuntimeException var12)
            {
                SMCLog.warning("Error reading metadata: " + pathMeta);
                SMCLog.warning("" + var12.getClass().getName() + ": " + var12.getMessage());
                return new TextureMetadataSection(false, false, new ArrayList());
            }
            finally
            {
                IOUtils.closeQuietly(brMeta);
                IOUtils.closeQuietly(inMeta);
            }

            return var8;
        }
        else
        {
            return new TextureMetadataSection(false, false, new ArrayList());
        }
    }

    private static IMetadataSerializer makeMetadataSerializer()
    {
        IMetadataSerializer ms = new IMetadataSerializer();
        ms.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        ms.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        ms.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        ms.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        ms.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        return ms;
    }
}
