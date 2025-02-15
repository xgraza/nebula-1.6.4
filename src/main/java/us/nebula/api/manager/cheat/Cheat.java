package us.nebula.api.manager.cheat;

import net.minecraft.client.Minecraft;
import us.nebula.Nebula;
import us.nebula.api.listener.EventBus;
import us.nebula.api.manager.key.Key;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author xgraza
 * @since 02/14/25
 */
public class Cheat
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    static final String DEFAULT_DESCRIPTION = "No description provided for this cheat";

    private final CheatManifest manifest;
    private final Key key;

    public Cheat()
    {
        manifest = getClass().getDeclaredAnnotation(CheatManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@CheatManifest needs to be annotated on top of a Cheat class");
        }

        Nebula.INSTANCE.getKeyManager().addKey(manifest.name(),
                key = new Key((state) ->
                {
                    if (state)
                    {
                        onEnable();
                    } else
                    {
                        onDisable();
                    }
                }, false, KEY_NONE));
    }

    protected void onEnable()
    {
        EventBus.subscribe(this);
    }

    protected void onDisable()
    {
        EventBus.unsubscribe(this);
    }

    public CheatManifest getManifest()
    {
        return manifest;
    }

    public Key getKey()
    {
        return key;
    }

    public void toggle()
    {
        key.toggle();
    }

    public void setToggled(final boolean toggled)
    {
        key.setState(toggled);
    }

    public boolean isToggled()
    {
        return key.isToggled();
    }
}
