package us.nebula.client.util;

import java.util.List;

/**
 * @param <T>
 * @author xgraza
 * @since 02/14/25
 */
public interface ITypedManager<T> extends IManager
{
    default T getReference(final Class<T> type)
    {
        return null;
    }

    List<T> getAll();
}
