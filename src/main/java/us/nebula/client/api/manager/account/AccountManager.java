package us.nebula.client.api.manager.account;

import us.nebula.client.Nebula;
import us.nebula.client.api.manager.ITypedManager;

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
        //EventBus.subscribe(this);
        Nebula.INSTANCE.getConfigurationManager().addConfiguration(
                new AccountConfig(this));
    }

    public void addAccount(final Account account)
    {
        accountList.add(account);
    }

    public void removeAccount(final Account account)
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
