package lol.nebula.account;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class AccountManager {

    /**
     * The account cache
     */
    private final List<Account> accountList = new ArrayList<>();

    public AccountManager() {
        new AccountsConfig(this);
    }

    /**
     * Gets a list of the cached accounts
     * @return the account list
     */
    public List<Account> getAccountList() {
        return accountList;
    }
}
