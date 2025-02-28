package us.nebula.api.manager.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 02/26/25
 */
public abstract class Overlay
{
    protected static final Minecraft MC = Minecraft.getMinecraft();

    private final OverlayManifest manifest;
    private final Setting<Boolean> stateSetting;

    public Overlay()
    {
        manifest = getClass().getDeclaredAnnotation(OverlayManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@OverlayManifest needs to be annotated on top of an InterfaceOverlay class");
        }
        stateSetting = new Setting<>(manifest.value(), false)
                .onValueChange((o, v) ->
                {
                    if (v)
                    {
                        onEnable();
                    } else
                    {
                        onDisable();
                    }
                });
        stateSetting.setValue(manifest.defaultState());
    }

    protected void onEnable()
    {

    }

    protected void onDisable()
    {

    }

    public abstract void render(final ScaledResolution resolution, final float partialTicks);

    public OverlayManifest getManifest()
    {
        return manifest;
    }

    public Setting<Boolean> getStateSetting()
    {
        return stateSetting;
    }
}
