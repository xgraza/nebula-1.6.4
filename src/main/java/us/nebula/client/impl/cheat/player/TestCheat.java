package us.nebula.client.impl.cheat.player;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.input.EventKey;
import us.nebula.client.impl.event.render.EventRender3D;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@CheatManifest(name = "Test", category = CheatCategory.PLAYER)
public final class TestCheat extends Cheat
{

    private final List<Vec3> pathList = new LinkedList<>();
    private Vec3 currentVector;
    private int index;

    @Override
    public void onEnable()
    {
        super.onEnable();
        if (MC.thePlayer == null)
        {
            setToggled(false);
            return;
        }
        index = 0;
        currentVector = null;
        Nebula.INSTANCE.getMovementController().override();

        currentVector = Vec3.createVectorHelper(MC.thePlayer.posX + (Math.random() * 30), MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ  -(Math.random() * 30));
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        Nebula.INSTANCE.getMovementController().restore();
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {


        final Vec3 vector = currentVector;
        final AxisAlignedBB bb = new AxisAlignedBB(vector.xCoord, vector.yCoord, vector.zCoord, vector.xCoord + 1, vector.yCoord + 1, vector.zCoord + 1);
        RenderUtil.outlinedBox3D(bb, 1.5f, Color.red.getRGB());
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final Vec3 vector = Vec3.createVectorHelper(MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ)
                        .addVector(currentVector.xCoord, currentVector.yCoord, currentVector.zCoord);
        float[] movement = Nebula.INSTANCE.getMovementController().getMovementFor(currentVector);
        MC.thePlayer.movementInput.moveForward = movement[0];
        MC.thePlayer.movementInput.moveStrafe = movement[1];
        ChatUtil.send("F: %.1f, S: %.1f", movement[0], movement[1]);

//        final double deltaX = (Math.floor(vector.xCoord) + 0.5) - MC.thePlayer.posX;
//        final double deltaZ = (Math.floor(vector.zCoord) + 0.5) - MC.thePlayer.posZ;
//        final float angle = (float) -(Math.toDegrees(Math.atan2(deltaX, deltaZ)));
//        MC.thePlayer.rotationYaw = angle;
    };

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (event.getKeyCode() == Keyboard.KEY_LSHIFT)
        {
            index++;
            if (index >= pathList.size())
            {
                index = 0;
            }
            //currentVector = pathList.get(index);
        }
    };

    public EventListener<EventRender3D> getRender3DEventListener()
    {
        return render3DEventListener;
    }
}
