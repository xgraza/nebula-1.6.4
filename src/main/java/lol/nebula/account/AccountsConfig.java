package lol.nebula.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.nebula.Nebula;
import lol.nebula.config.Config;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;

import static lol.nebula.config.ConfigManager.ROOT;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class AccountsConfig extends Config {
    private final AccountManager accounts;

    public AccountsConfig(AccountManager accounts) {
        super(new File(ROOT, "accounts.json"));
        this.accounts = accounts;
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();
        for (Account account : accounts.getAccountList()) {
            JsonObject accountObject = new JsonObject();
            accountObject.addProperty("email", account.getEmail());
            accountObject.addProperty("password", account.getPassword());

            array.add(accountObject);
        }

        try {
            FileUtils.write(getFile(), FileUtils.getGSON().toJson(array));
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to save accounts");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        // do not try to read a non-existent file
        if (!getFile().exists()) return;

        String content;
        try {
            content = FileUtils.read(getFile());
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to read accounts");
            e.printStackTrace();
            return;
        }

        JsonArray array = FileUtils.getGSON().fromJson(content, JsonArray.class);
        if (array == null) return;

        accounts.getAccountList().clear();

        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            JsonObject accountObject = element.getAsJsonObject();

            accounts.getAccountList().add(new Account(
                    accountObject.get("email").getAsString(),
                    accountObject.get("password").getAsString()));
        }
    }
}
