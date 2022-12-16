package wtf.nebula.client.impl.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.multiplayer.ServerData;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.client.Wrapper;

public class DiscordRPCHandler implements Wrapper {

    private static final String ID = "1038604916073185380";

    private static final String[] SPLASHES = {
            "Sending coordinates to Aesthetical",
            "Griefing a players small dirt hut",
            "Carb loading on 2b2t larp",
            "Sending double the packets to do double the damage",
            "Pussying out in burrow",
            "Disabling NCP",
            "Verus Airlines: Next stop - Staff ban",
            "Baiting kids on a block game",
            "Installing yoink rat...",
            "Sending Sjnez a pizza",
            "Throwing a brick at Sjnez's window",
            "Downloading intent.store rat",
            "Crashing epearls $13 VPS",
            "Yelling at strangers on the internet",
            "CFontRenderer extends ClassLoader",
            "Injecting estrogen",
            "Stealing hometea's 32ks",
            "larping on alfheim.pw",
            "Making epearl talk about keyboards",
            "Making iWoodz mad about every little thing",
            "Leaking Nebula beta",
            "Dumping Future Client (587/2098)",
            "Saying \"ez\" to kids with a robotic client",
            "Placing end portal blocks on alfheim.pw",
            "Running from MedMex's AutoTNTMinecart",
            "Begging for Yeezus Private",
            "Stealing Round Table Client from 29",
            "Eating panera bread",
            "Obfuscated with Paramorphism",
            "Sponsored by Memphis and Detroit",
            "smacking giants with a toothpick",
            "Smoking the Opps with an end crystal spawn egg",
            "I just flew 32 meters like a butterfly thanks to DotGod.cc!",
            "doin you're mom",
            "*yo'ure",
            "hint: i'm in your walls",
            "currently eredicating obesity",
            "i like gay black men kissing",
            "hola gordo puta",
            "Sending request to checkip.amazonaws.com",
            "Michelle Obama is a dude",
            "What's obama's last name?",
            "do u are have stupid",
            "why did my dad leave?",
            "Sending epearl a backdoored jar",
            "Switching to creative mode",
            "Stacking Alpha's 32ks"
    };

    private static final DiscordRPC discordRpc = DiscordRPC.INSTANCE;
    private static Thread discordThread;
    private static DiscordRichPresence richPresence;

    public static void start() {
        if (richPresence == null) {
            richPresence = new DiscordRichPresence();
        }

        discordRpc.Discord_Initialize(ID, new DiscordEventHandlers(), true, "");
        richPresence.startTimestamp = System.currentTimeMillis() / 1000L;

        if (discordThread == null) {
            discordThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    richPresence.largeImageKey = "logo";
                    richPresence.largeImageText = "Nebula " + Nebula.VERSION.getVersionString();

                    String details = "No data";

                    if (wtf.nebula.client.impl.module.miscellaneous.DiscordRPC.showIp.getValue()) {

                        ServerData data = mc.currentServerData;
                        if (data == null || data.serverIP == null) {

                            if (mc.currentScreen instanceof GuiMainMenu) {
                                details = "In the main menu";
                            } else if (mc.currentScreen instanceof GuiMultiplayer) {
                                details = "Deciding what server to play";
                            } else if (mc.currentScreen instanceof GuiSelectWorld) {
                                details = "Deciding what world to play";
                            } else {
                                if (mc.isSingleplayer()) {
                                    details = "Playing singleplayer";
                                } else {
                                    details = "Doing god knows what";
                                }
                            }
                        } else {
                            details = "Playing on " + data.serverIP;
                        }

                    } else {
                        details = "I'm a private fuck";
                    }

                    if (!details.equals(richPresence.details)) {
                        richPresence.startTimestamp = System.currentTimeMillis() / 1000L;
                    }

                    richPresence.details = details;
                    richPresence.state = SPLASHES[MathUtils.random(0, SPLASHES.length - 1)];
                    discordRpc.Discord_UpdatePresence(richPresence);

                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }, "DiscordRPC-Update-Thread");
            discordThread.start();
        }

    }

    public static void stop() {
        if (discordThread != null) {
            discordThread.interrupt();
            discordThread = null;
        }

        discordRpc.Discord_Shutdown();
        discordRpc.Discord_ClearPresence();
    }

}
