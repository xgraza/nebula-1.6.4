package ez.nebula.client.api.manager.account;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.util.io.IJSONSerializable;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class Account implements IJSONSerializable
{
    private String username, password;

    public Account(final String username)
    {
        this.username = username;
    }

    public Account()
    {

    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        if (!object.has("username"))
        {
            throw new RuntimeException("Account object did not contain username field");
        }
        final String username = object.get("username").getAsString();
        String password = null;
        if (object.has("password"))
        {
            password = object.get("password").getAsString();
        }
        this.username = username;
        this.password = password;
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("username", username);
        if (password != null && !password.isEmpty())
        {
            object.addProperty("password", password);
        }
        return object;
    }
}
