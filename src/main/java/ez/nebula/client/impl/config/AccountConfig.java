package ez.nebula.client.impl.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.account.Account;
import ez.nebula.client.api.manager.account.AccountManager;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountConfig implements IConfig
{
    private final AccountManager manager;

    public AccountConfig(final AccountManager manager)
    {
        this.manager = manager;
    }

    @Override
    public String save()
    {
        final JsonArray array = new JsonArray();
        for (final Account account : manager.getAll())
        {
            array.add(account.toJSON());
        }
        return FileUtil.GSON.toJson(array);
    }

    @Override
    public void load(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        final JsonArray array = FileUtil.JSON_PARSER.parse(data).getAsJsonArray();
        // clear after reading in case of errors
        manager.clear();
        for (final JsonElement element : array)
        {
            if (!element.isJsonObject())
            {
                continue;
            }
            final JsonObject object = element.getAsJsonObject();
            if (object.has("username"))
            {
                final Account account = new Account(object.get("username").getAsString());
                if (object.has("password"))
                {
                    account.setPassword(object.get("password").getAsString());
                }
                manager.add(account);
            }
        }
    }

    @Override
    public File getFile()
    {
        return new File(Nebula.NEBULA_ROOT, "accounts");
    }
}
