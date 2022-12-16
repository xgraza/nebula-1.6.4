package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.EnumSkyBlock;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.*;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class NoRender extends ToggleableModule {
    private final Property<Boolean> negatives = new Property<>(true, "Negatives");

    private final Property<Boolean> hurtCam = new Property<>(true, "Hurt Camera", "hurtcam");
    private final Property<Boolean> fire = new Property<>(true, "Fire", "burning");
    private final Property<Boolean> blocks = new Property<>(true, "Blocks");
    private final Property<Boolean> skylight = new Property<>(false, "Skylight Updates", "skylight");
    private final Property<Boolean> confusion = new Property<>(true, "Confusion", "nausea");

    private final Property<Boolean> voidParticles = new Property<>(true, "Void Particles", "voidparticles", "void");
    private final Property<Boolean> signText = new Property<>(false, "Sign Text", "signtext");

    public NoRender() {
        super("No Render", new String[]{"norender", "antirender", "rendertweaks"}, ModuleCategory.VISUALS);
        offerProperties(negatives, hurtCam, fire, blocks, skylight, confusion, voidParticles, signText);
    }

    @EventListener
    public void onRenderItemText(EventItemText event) {
        if (event.getStack() != null && event.getStack().stackSize != 0 && negatives.getValue()) {

            if (event.getText() == null && event.getStack().stackSize < 0) {
                event.setText(EnumChatFormatting.RED + String.valueOf(event.getStack().stackSize));
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onRenderSignText(EventRenderSignText event) {
        if (signText.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onHasVoidParticles(EventVoidParticles event) {
        if (voidParticles.getValue()) {
            event.has = false;
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRenderOverlay(EventRenderOverlay event) {
        switch (event.getType()) {
            case BLOCK: {
                event.setCancelled(blocks.getValue());
                break;
            }

            case HURTCAM: {
                event.setCancelled(hurtCam.getValue());
                break;
            }

            case FIRE: {
                event.setCancelled(fire.getValue());
                break;
            }

            case CONFUSION: {
                event.setCancelled(confusion.getValue());
                break;
            }
        }
    }

    @EventListener
    public void onUpdateLight(EventUpdateLight event) {
        event.setCancelled(event.getSkyBlock().equals(EnumSkyBlock.Sky) && skylight.getValue());
    }
}
