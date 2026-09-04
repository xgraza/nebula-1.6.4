package ez.nebula.client.api.nws.packet.c2s;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.BuildConfig;
import ez.nebula.client.api.nws.packet.IPacket;

/**
 * @author xgraza
 * @since 9/4/26
 */
public final class C2SIdentify implements IPacket
{
    @Override
    public void read(JsonElement element)
    {
        throw new RuntimeException("C2SPacketIdentify is cannot be read, only sent");
    }

    @Override
    public JsonElement write()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("name", BuildConfig.NAME);
        object.addProperty("version", BuildConfig.VERSION);
        object.addProperty("hash", BuildConfig.HASH);
        object.addProperty("branch", BuildConfig.BRANCH);
        object.addProperty("env", BuildConfig.ENV.toString());
        object.addProperty("id", BuildConfig.BUILD);
        object.addProperty("hwid", hwid());
        return object;
    }

    @Override
    public int getType()
    {
        return 0;
    }

    public static String hwid()
    {
        return "";
    }
}
