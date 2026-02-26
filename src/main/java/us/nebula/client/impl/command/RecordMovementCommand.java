package us.nebula.client.impl.command;

import net.minecraft.util.Vec3;
import us.nebula.client.api.listener.EventBus;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.nebula.client.impl.cheat.world.FakePlayerCheat;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.util.player.ChatUtil;
import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.parser.CommandContext;

@CommandManifest(aliases = { "recordmovement", "rcdmv", "fpmove" },
        description = "Records movement for your fake player")
public final class RecordMovementCommand extends Command
{
    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        FakePlayerCheat cheat = FakePlayerCheat.INSTANCE;

        if (MC.thePlayer.isSneaking())
        {
            EventBus.unsubscribe(this);
            ChatUtil.send("Recorded %s moves.", cheat.fakePlayerMovement.size());
            return;
        }

        Vec3 vec = Vec3.createVectorHelper(MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ);
        if (!cheat.fakePlayerMovement.isEmpty())
        {
//            FakePlayerCheat.Movement movement = cheat.fakePlayerMovement.element();
//            if (movement.getPosition().equals(vec))
//            {
//                return;
//            }
        }
        FakePlayerCheat.Movement newMovement = new FakePlayerCheat.Movement(vec, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch);
        cheat.fakePlayerMovement.add(newMovement);
    };

    @Override
    public CommandResult dispatch(CommandContext ctx)
    {
        FakePlayerCheat cheat = FakePlayerCheat.INSTANCE;
        if (cheat == null)
        {
            EventBus.unsubscribe(this);
            return ctx.ok("FakePlayer cheat is null...");
        }
        cheat.fakePlayerMovement.clear();
        EventBus.subscribe(this);
        return ctx.ok("Move around, press shift to finish recording");
    }
}
