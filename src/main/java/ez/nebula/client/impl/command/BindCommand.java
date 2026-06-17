package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.input.EventKey;
import ez.nebula.client.api.listener.event.input.EventMouse;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.arg.ModuleArgumentType;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * @author xgraza
 * @since 6/16/26
 */
@CommandManifest(aliases = { "bind", "setbind" }, description = "Sets a module's bind")
public final class BindCommand extends Command
{
    private Module module;

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (module == null || event.getKeyCode() <= Keyboard.KEY_NONE)
        {
            return;
        }
        module.getKey().setMouseBind(false);
        module.getKey().setKeyCode(event.getKeyCode());
        ChatUtil.sendNebula("Set %s's key bind to %s", module.getManifest().name(), module.getKey().toString());
        module = null;
    };

    @Subscribe
    private final EventListener<EventMouse> mouseEventListener = event ->
    {
        if (module == null)
        {
            return;
        }
        module.getKey().setMouseBind(true);
        module.getKey().setKeyCode(event.getMouseButton());
        ChatUtil.sendNebula("Set %s's mouse bind to %s", module.getManifest().name(), module.getKey().toString());
        module = null;
    };

    public BindCommand()
    {
        EventBus.subscribe(this);
    }

    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(literal("unbind")
                .then(argument("module", ModuleArgumentType.module())
                        .executes((ctx) ->
                        {
                            final Module mod = ModuleArgumentType.get(ctx, "module");
                            mod.getKey().setKeyCode(Keyboard.KEY_NONE);
                            mod.getKey().setMouseBind(false);
                            return ctx.getSource().respond("Unbound any bind from %s", mod.getManifest().name());
                        })))
                .then(argument("module", ModuleArgumentType.module())
                        .executes((ctx) ->
                        {
                            module = ModuleArgumentType.get(ctx, "module");
                            return ctx.getSource().respond("Press any key/mouse button to bind %s", module.getManifest().name());
                        }));
    }
}
