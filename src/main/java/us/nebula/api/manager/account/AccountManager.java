package us.nebula.api.manager.account;

import net.minecraft.client.Minecraft;
import us.nebula.Nebula;
import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.ITypedManager;
import us.nebula.impl.event.network.EventPacket;

import java.util.*;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountManager implements ITypedManager<Account>
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final Map<String, Account> accountNameMap = new HashMap<>();
    private final List<Account> accountList = new LinkedList<>();

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {

    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        Nebula.INSTANCE.getConfigurationManager().addConfiguration(
                new AccountConfig(this));
    }

    public void addAccount(final Account account)
    {
        accountNameMap.put(account.getUsername(), account);
        accountList.add(account);
    }

    public void removeAccount(final Account account)
    {
        accountNameMap.remove(account.getUsername());
        accountList.remove(account);
    }

    public void clear()
    {
        accountNameMap.clear();
        accountList.clear();
    }

    @Override
    public List<Account> getAll()
    {
        return accountList;
    }
}
