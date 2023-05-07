package lol.nebula.account;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class AccountManager {

    /**
     * Gets the minecraft game instance
     */
    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * The account cache
     */
    private final List<Account> accountList = new ArrayList<>();

    public AccountManager() {
        new AccountsConfig(this);
    }

    /**
     * Logs into an account
     * @param account the account object
     */
    public void login(Account account) {
        // im not even gonna bother with MSA auth
        mc.setSession(new Session(account.getEmail(), "", ""));
    }

    /**
     * Gets a list of the cached accounts
     * @return the account list
     */
    public List<Account> getAccountList() {
        return accountList;
    }
}
