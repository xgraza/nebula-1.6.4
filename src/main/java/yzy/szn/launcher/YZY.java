package yzy.szn.launcher;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import yzy.szn.api.eventbus.EventBus;
import yzy.szn.api.module.ModuleManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author graza
 * @since 02/17/24
 */
public enum YZY {

    INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger("yzy.szn");
    public static final EventBus BUS = new EventBus(LOGGER::error);

    private ModuleManager moduleManager;

    void init() throws IOException {
        LOGGER.info("yzy szn approchin fuck whatever yall been hearing");

        setIcon();

        moduleManager = new ModuleManager();

        LOGGER.info("south park had them all laughing, now all my - designing and we all swaggin'");

    }

    synchronized void shutdown() {

    }

    private void setIcon() throws IOException {
        if (Util.getOSType() != Util.EnumOS.MACOS) {
            final int image = 1;
            final String[] locations = {"ico" + image + "_16.png", "ico" + image + "_32.png"};

            final ByteBuffer[] bufs = new ByteBuffer[2];
            for (int i = 0; i < bufs.length; ++i) {
                final String loc = locations[i];
                final InputStream is = YZY.class.getResourceAsStream(
                        "/assets/minecraft/yzy/icon/" + loc);
                if (is == null) {
                    throw new RuntimeException("java is ASS!");
                }
                bufs[i] = Minecraft.readImageToBuffer(is);
                is.close();
            }

            Display.setIcon(bufs);
        }
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
