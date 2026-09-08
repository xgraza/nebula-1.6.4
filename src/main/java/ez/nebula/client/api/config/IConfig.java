package ez.nebula.client.api.config;

import java.io.File;

/**
 * @author xgraza
 * @since 02/14/25
 */
public interface IConfig
{
    String write();

    void read(final String data);

    File getLocation();
}
