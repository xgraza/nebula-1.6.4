package lol.nebula.account;

import lol.nebula.config.Config;

import java.io.File;

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

    }

    @Override
    public void load() {

    }
}
