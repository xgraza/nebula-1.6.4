package ez.nebula.client.impl.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.type.JSONConfig;
import ez.nebula.client.api.manager.account.Account;
import ez.nebula.client.api.manager.account.AccountManager;

import java.io.File;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountConfig extends JSONConfig<JsonArray>
{
    private final AccountManager manager;

    public AccountConfig(final AccountManager manager)
    {
        this.manager = manager;
    }

    @Override
    public JsonArray writeJSON()
    {
        final JsonArray array = new JsonArray();
        for (final Account account : manager.getAll())
        {
            array.add(account.toJSON());
        }
        return array;
    }

    @Override
    public void readJSON(final JsonArray json)
    {
        manager.clear();
        for (final JsonElement element : json)
        {
            final Account account = new Account();
            try
            {
                account.fromJSON(element);
                manager.add(account);
            } catch (final Exception ignored)
            {
            }
        }
    }

    @Override
    public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "accounts");
    }
}
