package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ReflectorForge
{
    public static void FMLClientHandler_trackBrokenTexture(ResourceLocation loc, String message)
    {
        if (!Reflector.FMLClientHandler_trackBrokenTexture.exists())
        {
            Object instance = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);
            Reflector.call(instance, Reflector.FMLClientHandler_trackBrokenTexture, new Object[] {loc, message});
        }
    }

    public static void FMLClientHandler_trackMissingTexture(ResourceLocation loc)
    {
        if (!Reflector.FMLClientHandler_trackMissingTexture.exists())
        {
            Object instance = Reflector.call(Reflector.FMLClientHandler_instance, new Object[0]);
            Reflector.call(instance, Reflector.FMLClientHandler_trackMissingTexture, new Object[] {loc});
        }
    }

    public static void putLaunchBlackboard(String key, Object value)
    {
        Map blackboard = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);

        if (blackboard != null)
        {
            blackboard.put(key, value);
        }
    }

    public static boolean renderFirstPersonHand(RenderGlobal renderGlobal, float partialTicks, int pass)
    {
        return !Reflector.ForgeHooksClient_renderFirstPersonHand.exists() ? false : Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, new Object[] {renderGlobal, Float.valueOf(partialTicks), Integer.valueOf(pass)});
    }

    public static InputStream getOptiFineResourceStream(String path)
    {
        if (!Reflector.OptiFineClassTransformer_instance.exists())
        {
            return null;
        }
        else
        {
            Object instance = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);

            if (instance == null)
            {
                return null;
            }
            else
            {
                if (path.startsWith("/"))
                {
                    path = path.substring(1);
                }

                byte[] bytes = (byte[])((byte[])Reflector.call(instance, Reflector.OptiFineClassTransformer_getOptiFineResource, new Object[] {path}));

                if (bytes == null)
                {
                    return null;
                }
                else
                {
                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    return in;
                }
            }
        }
    }

    public static boolean blockHasTileEntity(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (!Reflector.ForgeBlock_hasTileEntity.exists())
        {
            return block.hasTileEntity();
        }
        else
        {
            int metadata = world.getBlockMetadata(x, y, z);
            return Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, new Object[] {Integer.valueOf(metadata)});
        }
    }

    public static String[] getForgeModIds()
    {
        if (!Reflector.Loader.exists())
        {
            return new String[0];
        }
        else
        {
            Object loader = Reflector.call(Reflector.Loader_instance, new Object[0]);
            List listActiveMods = (List)Reflector.call(loader, Reflector.Loader_getActiveModList, new Object[0]);

            if (listActiveMods == null)
            {
                return new String[0];
            }
            else
            {
                ArrayList listModIds = new ArrayList();
                Iterator modIds = listActiveMods.iterator();

                while (modIds.hasNext())
                {
                    Object modContainer = modIds.next();

                    if (Reflector.ModContainer.isInstance(modContainer))
                    {
                        String modId = Reflector.callString(modContainer, Reflector.ModContainer_getModId, new Object[0]);

                        if (modId != null)
                        {
                            listModIds.add(modId);
                        }
                    }
                }

                String[] modIds1 = (String[])((String[])listModIds.toArray(new String[listModIds.size()]));
                return modIds1;
            }
        }
    }
}
