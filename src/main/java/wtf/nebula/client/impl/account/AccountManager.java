package wtf.nebula.client.impl.account;

import net.minecraft.util.Session;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.io.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountManager implements Wrapper {
    private final List<Account> accounts = new ArrayList<>();

    public AccountManager() {
        new Config("alt_accounts.txt") {

            @Override
            public void load(String element) {
                if (element == null || element.isEmpty()) {
                    return;
                }

                element = element.trim();

                for (String result : element.split("\n")) {
                    String[] c = result.split(":");

                    String username = c[0];
                    String password = c[1];

                    AccountType accountType = AccountType.CRACKED;
                    if (!password.isEmpty() && !password.equals("n/a")) {
                        accountType = AccountType.PREMIUM;
                    }

                    add(new Account(username, password, accountType));
                }
            }

            @Override
            public void save() {
                StringBuilder builder = new StringBuilder();

                for (Account account : accounts) {
                    builder.append(account.getEmail()).append(":");

                    if (account.getPassword() == null || account.getPassword().isEmpty()) {
                        builder.append("n/a");
                    } else {
                        builder.append(account.getPassword());
                    }

                    builder.append("\n");
                }

                FileUtils.write(getFile(), builder.toString());
            }
        };
    }

    public void add(Account account) {
        accounts.add(account);
    }

    public void remove(Account account) {
        accounts.remove(account);
    }

    public void login(Account account) {
        if (account.getAltType().equals(AccountType.CRACKED)) {
            mc.session = new Session(account.getEmail(), "", "");
        } else {

        }
    }

    public List<Account> getAlts() {
        return accounts;
    }
}
