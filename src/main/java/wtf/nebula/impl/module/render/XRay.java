package wtf.nebula.impl.module.render;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import wtf.nebula.Nebula;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.FileUtil;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class XRay extends Module {
    public static final Set<Integer> blocks = new HashSet<Integer>() {{
//        add(Blocks.oreRedstone.blockID);
//        add(Block.oreCoal.blockID);
//        add(Block.oreDiamond.blockID);
//        add(Block.oreIron.blockID);
//        add(Block.oreEmerald.blockID);
//        add(Block.oreLapis.blockID);
//        add(Block.oreGold.blockID);
//        add(Block.oreNetherQuartz.blockID);
    }};

    public XRay() {
        super("XRay", ModuleCategory.RENDER);

        if (!Files.exists(FileUtil.XRAY_BLOCKS)) {
            save();
        }

        else {

            String content = FileUtil.read(FileUtil.XRAY_BLOCKS);
            if (content != null && !content.isEmpty()) {
                try {
                    int[] blockIds = new Gson().fromJson(FileUtil.read(FileUtil.XRAY_BLOCKS), int[].class);

                    if (blockIds.length > 0) {
                        blocks.clear();

                        for (int blockId : blockIds) {
                            blocks.add(blockId);
                        }

                        Nebula.log.info("Loaded " + blockIds.length + " xray blocks.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Nebula.log.warn("An exception occurred trying to read Xray blocks, fallback to default...");
                }
            }
        }
    }

    public final Value<Integer> opacity = new Value<>("Opacity", 120, 0, 255);

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }

        mc.renderGlobal.loadRenderers();
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        mc.renderGlobal.loadRenderers();
    }

    public static void save() {
        JsonArray array = new JsonArray();
        blocks.forEach((id) -> array.add(new JsonPrimitive(id)));

        FileUtil.write(FileUtil.XRAY_BLOCKS, array.toString());
    }
}
