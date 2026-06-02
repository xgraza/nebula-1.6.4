package ez.nebula.client.api.config;

import java.io.File;

/**
 * @author xgraza
 * @since 02/14/25
 */
public interface IConfig
{
    String save();

    void load(final String data);

    File getFile();
}
