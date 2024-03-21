package nebula.client.account;

import nebula.client.config.ConfigLoader;
import nebula.client.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class AccountRegistry implements Registry<Account> {

  /**
   * A list of account objects
   */
  private final List<Account> accountList = new ArrayList<>();

  @Override
  public void init() {
    ConfigLoader.add(new AccountConfig());
  }

  @Override
  public void add(Account... elements) {
    Collections.addAll(accountList, elements);
  }

  @Override
  public void remove(Account... elements) {
    for (Account element : elements) {
      accountList.remove(element);
    }
  }

  @Override
  public Collection<Account> values() {
    return accountList;
  }

  public Account get(int index) {
    int size = accountList.size();
    if (index > size || index < 0) return null;
    return accountList.get(index);
  }
}
