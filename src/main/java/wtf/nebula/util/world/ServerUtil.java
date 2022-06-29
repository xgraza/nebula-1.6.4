package wtf.nebula.util.world;

import wtf.nebula.util.Globals;

public class ServerUtil implements Globals {

    public static String getServerName() {
        if (mc.isSingleplayer()) {
            return "singleplayer";
        }

        if (mc.theWorld == null) {
            return null;
        }

        return mc.currentServerData.serverIP;
    }
}
