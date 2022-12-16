package wtf.nebula.client.utils.client;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import org.lwjgl.Sys;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.module.Module;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.io.SystemTrayUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.zip.ZipInputStream;

public class Spooky {

    public static void funny() {

//        if (!isSpooky()) {
//            return;
//        }

        new Thread(() -> {

            try {

                while (!Thread.currentThread().isInterrupted()) {

                    Thread.sleep(15000L);

                    int rnd = 3;//MathUtils.random(0, 5);

                    switch (rnd) {
                        case 0: {
                            int i = MathUtils.random(0, Nebula.getInstance().getModuleManager().getRegistry().size() - 1);
                            Module module = Nebula.getInstance().getModuleManager().getRegistry().get(i);
                            if (module instanceof ToggleableModule) {
                                ((ToggleableModule) module).setRunning(MathUtils.randomBoolean());
                            }
                            break;
                        }

                        case 1: {
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    Desktop.getDesktop().browse(new URI("https://pornhub.com"));
                                } catch (IOException | URISyntaxException ignored) {
                                    // lol
                                }
                            }
                            break;
                        }

                        case 2: {
                            if (MathUtils.randomBoolean()) {
                                if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
                                    Minecraft.getMinecraft().thePlayer.sendChatMessage("guys check out my youtube video: https://www.youtube.com/watch?v=r-SpnymI1kk");
                                    Minecraft.getMinecraft().thePlayer.sendChatMessage("i worked very hard on it!");
                                }
                            }

                            if (SystemTrayUtils.isCreated()) {
                                SystemTrayUtils.showMessage("iminyourwallsiminyourwallsiminyourwallsiminyourwallsiminyourwallsiminyourwallsiminyourwalls");
                            }
                            break;
                        }

                        case 3: {

                            //File file = new File(System.getProperty("java.io.tmpdir"), "monochrom-1512757784.zip");

                            File file = Minecraft.getMinecraft().mcDataDir
                                    .toPath()
                                    .resolve("resourcepacks")
                                    .resolve("monochrom-1512757784.zip")
                                    .toFile();

                            if (!file.exists()) {

                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    return;
                                }

                                try {
                                    HttpURLConnection c = (HttpURLConnection) new URL("https://static.planetminecraft.com/files/resource_media/texture/1749/monochrom-1512757784.zip").openConnection();
                                    c.setRequestMethod("GET");
                                    c.setReadTimeout(5000);
                                    c.setConnectTimeout(5000);
                                    c.setInstanceFollowRedirects(false);
                                    c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:106.0) Gecko/20100101 Firefox/106.0");
                                    c.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                                    c.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");

                                    c.connect();

                                    InputStream is = c.getInputStream();
                                    if (is != null) {
                                        FileOutputStream fos = new FileOutputStream(file);

                                        int i;
                                        while ((i = is.read()) != -1) {
                                            fos.write(i);
                                        }

                                        fos.close();
                                    }

                                    c.disconnect();
                                } catch (IOException ignored) {
                                    break;
                                }

                            }

                            ResourcePackRepository.Entry entry = new ResourcePackRepository.Entry(file);
                            Minecraft.getMinecraft().getResourcePackRepository().func_148527_a(Lists.newArrayList(entry));
                            Minecraft.getMinecraft().gameSettings.resourcePacks.clear();
                            Minecraft.getMinecraft().gameSettings.resourcePacks.add(entry.getResourcePackName());

                            runInCurrentThread(() -> Minecraft.getMinecraft().refreshResources());
                            break;
                        }

                        case 4: {
                            break;
                        }
                    }

                }
            } catch (InterruptedException e) {
                Sys.alert("", ":(");
                Minecraft.getMinecraft().shutdown();
            }

        }, "NothingToSeeHere").start();
    }

    public static void runInCurrentThread(Runnable r) {
        Futures.immediateCheckedFuture(r);
    }

    public static boolean isSpooky() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.MONTH) + 1 == 4 && c.get(Calendar.DAY_OF_MONTH) == 1;
    }
}
