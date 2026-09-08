package ez.nebula.client.api.manager.account;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.impl.config.AccountConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountManager implements ITypedManager<Account>
{
    private final List<Account> accountList = new LinkedList<>();

    @Override
    public void init()
    {
        Nebula.CONFIGS.register(new AccountConfig(this));
    }

    public void add(final Account account)
    {
        accountList.add(account);
    }

    public void remove(final Account account)
    {
        accountList.remove(account);
    }

    public void clear()
    {
        accountList.clear();
    }

    @Override
    public List<Account> getAll()
    {
        return accountList;
    }
}
