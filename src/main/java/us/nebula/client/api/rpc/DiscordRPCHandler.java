package us.nebula.client.api.rpc;

import net.minecraft.client.SplashTextProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.client.ClientSettings;
import us.nebula.client.util.math.Timer;
import us.xgraza.sdrpc.DiscordRPC;
import us.xgraza.sdrpc.DiscordRichPresence;

/**
 * @author xgraza
 * @since 03/07/25
 */
public final class DiscordRPCHandler
{
    private static final Logger LOGGER = LogManager.getLogger("DiscordRPCHandler");

    private static final Timer PRESENCE_UPDATE_TIMER = new Timer();
    private static final DiscordRichPresence RICH_PRESENCE = new DiscordRichPresence();

    private static final String APPLICATION_ID = "1038604916073185380";
    private static final long REFRESH_INTERVAL = 750L;

    private static boolean running;

    public static void start()
    {
        if (running)
        {
            return;
        }
        running = true;

        DiscordRPC.EVENT_HANDLERS.ready = (user) ->
                LOGGER.info("Connected to DiscordRPC -> {}#{} ({})",
                        user.username, user.discriminator, user.userId);
        DiscordRPC.EVENT_HANDLERS.errored = (errorCode, message) ->
                LOGGER.error("DiscordRPC error. code = {}, msg = {}", errorCode, message);
        DiscordRPC.EVENT_HANDLERS.disconnected = (errorCode, message) ->
                LOGGER.error("DiscordRPC disconnected. code = {}, msg = {}", errorCode, message);

        DiscordRPC.init(APPLICATION_ID);
        DiscordRPC.runCallbacks(DiscordRPCHandler::updatePresence, REFRESH_INTERVAL);

        RICH_PRESENCE.startTimestamp = System.currentTimeMillis() / 1000L;
    }

    public static void stop()
    {
        if (!running)
        {
            return;
        }
        DiscordRPC.shutdown();
        running = false;
    }

    private static void updatePresence()
    {
        if (!running)
        {
            return;
        }
        if (!PRESENCE_UPDATE_TIMER.hasElapsed(3000L))
        {
            return;
        }
        PRESENCE_UPDATE_TIMER.resetTime();
        RICH_PRESENCE.largeImageKey = "logo";
        RICH_PRESENCE.largeImageText = ClientSettings.VERSION;
        RICH_PRESENCE.details = "Running game";
        RICH_PRESENCE.state = SplashTextProvider.getRandomSplashText("nebula");
        DiscordRPC.updatePresence(RICH_PRESENCE);
    }
}
