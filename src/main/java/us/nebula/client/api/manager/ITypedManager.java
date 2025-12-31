package us.nebula.client.api.manager;

import java.util.List;

/**
 * @author xgraza
 * @since 02/14/25
 * @param <T>
 */
public interface ITypedManager<T> extends IManager
{
    default T getReference(final Class<T> type)
    {
        return null;
    }
    List<T> getAll();
}
