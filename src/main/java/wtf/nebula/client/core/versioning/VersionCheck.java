package wtf.nebula.client.core.versioning;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wtf.nebula.client.core.ClientEnvironment;
import wtf.nebula.client.core.Launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionCheck {
    private static final String VERSION_JSON_URL = "https://raw.githubusercontent.com/Sxmurai/nebula-1.6.4/version/version.json";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:104.0) Gecko/20100101 Firefox/104.0";

    public static void check() {
        if (Launcher.VERSION.getEnv().equals(ClientEnvironment.RELEASE)) {
            String res = getResponse();
            if (res != null && !res.isEmpty()) {
                JsonObject object = new JsonParser().parse(res).getAsJsonObject();

                int major = object.get("major").getAsInt();
                int minor = object.get("minor").getAsInt();
                int patch = object.get("patch").getAsInt();
                ClientEnvironment env = ClientEnvironment.valueOf(object.get("type").getAsString());
                int rcId = object.get("rcId").getAsInt();

                Version serverVer = new Version(major, minor, patch, env).setRcId(rcId);
                if (!serverVer.getVersionString().equals(Launcher.VERSION.getVersionString())) {
                    Launcher.LOGGER.info("You are outdated!");
                }
            }
        } else {
            Launcher.LOGGER.info("In a client environment other than release, not checking for latest...");
        }
    }

    private static String getResponse() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_JSON_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", USER_AGENT);

            connection.connect();

            InputStream stream = connection.getInputStream();

            if (stream != null) {
                StringBuilder builder = new StringBuilder();

                int i;
                while ((i = stream.read()) != -1) {
                    builder.append((char) i);
                }

                return builder.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            if (Launcher.VERSION.isDev()) {
                e.printStackTrace();
            } else {
                Launcher.LOGGER.info("An exception occurred while fetching for the latest version");
            }
        }

        return null;
    }
}
