package wtf.nebula.client.impl.manager;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.MathHelper;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.utils.client.Wrapper;

public class TickManager implements Wrapper {
    private double[] ticks = new double[20];
    public long lastTick = -1L;
    private int index = 0;

    public boolean responding = true;

    public TickManager() {
        Nebula.BUS.subscribe(this);
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {

            if (lastTick != -1L) {
                long timeSinceLastTick = System.currentTimeMillis() - lastTick;

                // if we have not received one of these packets in the last 1.2 seconds
                responding = timeSinceLastTick <= 1250L;

                double tickDiff = (20L / (timeSinceLastTick / 1000.0));
                ticks[index % ticks.length] = MathHelper.clamp_double(tickDiff, 0.0, 20.0);

                ++index;
            }

            lastTick = System.currentTimeMillis();
        }
    }

    public double getTps() {
        int nonNullTicks = 0;
        double sum = 0.0;

        for (double v : ticks) {
            if (v > 0.0) {
                ++nonNullTicks;
                sum += v;
            }
        }

        if (nonNullTicks == 0) {
            return 20.0;
        }

        return MathHelper.clamp_double(sum / nonNullTicks, 0.0, 20.0);
    }
}
