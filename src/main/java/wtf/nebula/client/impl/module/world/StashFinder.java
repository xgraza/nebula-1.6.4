package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StashFinder extends ToggleableModule {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    private static final File STASH_SAVES = new File(FileUtils.CLIENT_DIRECTORY, "stashes");

    private final Property<Boolean> save = new Property<>(false, "Save", "s", "savetofile");
    private final Property<Integer> delay = new Property<>(20, 0, 50, "Delay", "dl");

    private final Property<Boolean> minecarts = new Property<>(true, "Minecarts", "carts");
    private final Property<Integer> minecartCount = new Property<>(4, 2, 10, "Minecart Count", "minecartcount")
            .setVisibility(minecarts::getValue);

    private final List<Integer> minecartEntityIds = new CopyOnWriteArrayList<>();
    private final Map<Vec3, StashInfo> stashInfoMap = new ConcurrentHashMap<>();

    private final Timer timer = new Timer();

    public StashFinder() {
        super("Stash Finder", new String[]{"stashfinder"}, ModuleCategory.WORLD);
        offerProperties(save, delay, minecarts, minecartCount);

        if (!STASH_SAVES.exists()) {
            STASH_SAVES.mkdir();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        stashInfoMap.clear();
        minecartEntityIds.clear();
    }

    @EventListener
    public void onTick(EventTick event) {

        if (timer.hasPassed(delay.getValue() * 50L, true)) {

            if (minecarts.getValue()) {

                List<Entity> loadedEntities = new ArrayList<>(mc.theWorld.loadedEntityList);
                for (Entity entity : loadedEntities) {
                    if (entity instanceof EntityMinecart && !minecartEntityIds.contains(entity.getEntityId())) {

                        Vec3 pos = new Vec3(Vec3.fakePool, entity.posX, entity.posY, entity.posZ);
                        if (stashInfoMap.containsKey(pos)) {
                            continue;
                        }

                        int count = 0;

                        List<Integer> ids = new ArrayList<>();
                        ids.add(entity.getEntityId());

                        for (Entity e : loadedEntities) {

                            if (e instanceof EntityMinecart) {

                                if (entity.boundingBox.intersectsWith(e.boundingBox)) {
                                    ++count;
                                    ids.add(e.getEntityId());
                                }
                            }
                        }

                        if (count >= minecartCount.getValue()) {
                            StashInfo info = new StashInfo(Type.MINECARTS, count, pos);
                            print("Found " + EnumChatFormatting.YELLOW + count + EnumChatFormatting.GRAY + " minecarts put together at X: "
                                    + EnumChatFormatting.RED + String.format("%.2f", pos.xCoord)
                                    + EnumChatFormatting.GRAY + ", Y: "
                                    + EnumChatFormatting.RED + String.format("%.2f", pos.yCoord)
                                    + EnumChatFormatting.GRAY + ", Z: "
                                    + EnumChatFormatting.RED + String.format("%.2f", pos.zCoord)
                                    + EnumChatFormatting.GRAY + ".");

                            stashInfoMap.put(pos, info);
                            minecartEntityIds.addAll(ids);

                            if (save.getValue()) {
                                createSave(info);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createSave(StashInfo info) {
        File file = new File(STASH_SAVES, "stash-" + info.serverIp + "-" + info.getCreatedAt() + ".txt");

        if (file.exists()) {
            return;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String builder = "Type:" + " " + info.type.name() +
                "\n" +
                "Count:" + " " + info.count +
                "\n" +
                "Position:" + " " + info.position.toString() +
                "\n" +
                "Date:" + " " + DATE_FORMAT.format(new Date(info.createdAt));
        FileUtils.write(file, builder);
    }

    public enum Type {
        CONTAINERS, MINECARTS
    }

    public static class StashInfo {
        private final Type type;
        private final int count;
        private final Vec3 position;
        private final long createdAt;
        private final String serverIp;

        public StashInfo(Type type, int count, Vec3 position) {
            this.type = type;
            this.count = count;
            this.position = position;
            this.createdAt = System.currentTimeMillis();

            if (mc.currentServerData != null) {
                serverIp = mc.currentServerData.serverIP;
            } else {
                serverIp = "singleplayer";
            }
        }

        public Type getType() {
            return type;
        }

        public int getCount() {
            return count;
        }

        public Vec3 getPosition() {
            return position;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public String getServerIp() {
            return serverIp;
        }
    }
}
