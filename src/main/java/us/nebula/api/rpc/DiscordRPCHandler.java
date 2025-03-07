package us.nebula.api.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.ClientSettings;
import us.nebula.util.math.Timer;
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
    private static final String[] SPLASH_TEXTS =
            {
                    "Backdooring alfheim.pw",
                    "Cracking Nebula Beta",
                    "Begging for Yeezus private",
                    "Begging for Round Table client",
                    "Griefing MedMex's stash",
                    "Running AutoCartDupe",
                    "Unstacking 32k armor",
                    "3v1ing commie",
                    "Placing End Crystal spawn eggs",
                    "Server predict AutoBed",
                    "Pissing off iWoodz",
                    "Begging hometea for infinites",
                    "Can anyone give me a kit?",
                    "How to /spawn",
                    "Wheres the /sethome command?",
                    "Eradicating obesity",
                    "Injecting estrogen",
                    "CFontRenderer extends ClassLoader",
                    "Carb loading on alfheim larp",
                    "Throwing a brick at Sjnez's window",
                    "Crashing epearls Dell Optiplex dedicated server",
                    "Double the packets to do double the damage",
                    "Dumping Future client...",
                    "Alfheim Wedding: 06/29/2022 10:30 PST X:0, Z:350",
                    "\"you item ilegal pls\"",
                    "Running with PhysicsCalc",
                    "Strength potting Giants",
                    "Epearl please unpatch step :pray:"
            };

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
        RICH_PRESENCE.state = getRandomSplash();
        DiscordRPC.updatePresence(RICH_PRESENCE);
    }

    private static String getRandomSplash()
    {
        final int size = SPLASH_TEXTS.length;
        return SPLASH_TEXTS[Math.min(size - 1, (int) (Math.random() * (size + 1)))];
    }
}
