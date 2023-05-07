package lol.nebula.management;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.listener.events.render.gui.overlay.EventRender2D;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

import java.util.Arrays;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class TickManager {

    /**
     * An array of the times it took between time update packets
     */
    private final double[] tpsMap = new double[20];

    /**
     * The current tick we are counting (% by 20)
     */
    private int tick = 0;

    /**
     * The last time in MS where a S03 was received
     */
    private long lastTime = -1L;

    private final Timer freezeDisplayTimer = new Timer();

    /**
     * The last time in MS the server responded
     */
    private long lastFreeze = -1L;

    private final Animation animation = new Animation(
            Easing.CUBIC_IN_OUT, 250, false);

    public TickManager() {
        Arrays.fill(tpsMap, 0.0);
    }

    @Listener(receiveCanceled = true)
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            if (lastTime == -1L) lastTime = System.currentTimeMillis();

            long diff = System.currentTimeMillis() - lastTime;
            if (diff <= 0) return;

            tpsMap[tick % tpsMap.length] = Math.max(0.0, diff / 50.0);
            ++tick;

            lastTime = System.currentTimeMillis();
        } else if (event.getPacket() instanceof S01PacketJoinGame) {

            // reset tps
            Arrays.fill(tpsMap, 0.0);
            lastFreeze = -1L;
            lastTime = -1L;
            tick = 0;
        }
    }

    @Listener
    public void onRender2D(EventRender2D event) {

        if (lastTime == -1L) {
            Fonts.axiforma.drawCenteredString(
                    "No tick has been received",
                    event.getRes().getScaledWidth_double() / 2.0,
                    4.0 + Fonts.axiforma.FONT_HEIGHT, -1);

            return;
        }

        double factor = animation.getFactor();

        long diff = System.currentTimeMillis() - lastTime;
        boolean frozen = diff > 1500L;
        if (frozen && !animation.getState()) animation.setState(true);

        if (factor > 0.0) {
            double y = 4.0 + (Fonts.axiforma.FONT_HEIGHT * factor);

            if (frozen) {
                if (lastFreeze == -1L) lastFreeze = lastTime;
                if (!animation.getState()) animation.setState(true);

                Fonts.axiforma.drawCenteredString("Froze on tick cycle "
                                + tick
                                + " ("
                                + (tick % tpsMap.length)
                                + ") - No response since "
                                + String.format("%.1f", (double) diff / 1000.0) + " seconds ago",
                        event.getRes().getScaledWidth_double() / 2.0, y, -1);

                freezeDisplayTimer.resetTime();
            } else {
                long freezeTime = lastTime - lastFreeze;

                if (animation.getState()
                        && freezeDisplayTimer.ms(2000L, false)) animation.setState(false);

                Fonts.axiforma.drawCenteredString("Server has been responding for "
                                + String.format("%.1f", (double) freezeTime / 1000.0)
                                + " seconds ("
                                + ((int) freezeTime / 20)
                                + " ticks)",
                        event.getRes().getScaledWidth_double() / 2.0, y, -1);
            }

        } else {
            lastFreeze = -1L;
        }
    }

    /**
     * Gets the average TPS
     * @return the tps of the server
     */
    public double getTps() {
        double sum = 0.0;
        int count = 0;
        for (double value : tpsMap) {
            if (value > 0.0) {
                sum += value;
                ++count;
            }
        }

        if (count == 0) return 0.0;

        return Math.max(0.0, sum / (double) count);
    }

    /**
     * Gets an array of previous tick times
     * @return an array of previous tick times
     */
    public double[] getTpsMap() {
        return tpsMap;
    }
}
