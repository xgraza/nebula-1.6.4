package ez.nebula.client.api.manager.account;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.config.IJSONSerializable;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class Account implements IJSONSerializable
{
    private final String username;
    private String password;

    public Account(final String username)
    {
        this.username = username;
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
