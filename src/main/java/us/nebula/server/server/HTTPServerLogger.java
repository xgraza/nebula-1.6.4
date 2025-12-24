package us.nebula.server.server;

import io.fusionauth.http.log.Level;
import io.fusionauth.http.log.LoggerFactory;
import org.apache.logging.log4j.Logger;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class HTTPServerLogger implements LoggerFactory
{
    private final io.fusionauth.http.log.Logger adaptedLogger;

    public HTTPServerLogger(final Logger logger)
    {
        adaptedLogger = new io.fusionauth.http.log.Logger()
        {
            @Override
            public void debug(String message)
            {
                logger.debug(message);
            }

            @Override
            public void debug(String message, Object... values)
            {
                logger.debug(message, values);
            }

            @Override
            public void debug(String message, Throwable throwable)
            {
                logger.debug(message, throwable);
            }

            @Override
            public void error(String message, Throwable throwable)
            {
                logger.error(message, throwable);
            }

            @Override
            public void error(String message)
            {
                logger.error(message);
            }

            @Override
            public void info(String message)
            {
                logger.info(message);
            }

            @Override
            public void info(String message, Object... values)
            {
                logger.info(message, values);
            }

            @Override
            public boolean isDebugEnabled()
            {
                return true;
            }

            @Override
            public boolean isErrorEnabled()
            {
                return true;
            }

            @Override
            public boolean isInfoEnabled()
            {
                return true;
            }

            @Override
            public boolean isTraceEnabled()
            {
                return true;
            }

            @Override
            public void setLevel(Level level)
            {

            }

            @Override
            public void trace(String message, Object... values)
            {
                logger.trace(message, values);
            }

            @Override
            public void trace(String message)
            {
                logger.trace(message);
            }
        };
    }

    @Override
    public io.fusionauth.http.log.Logger getLogger(Class<?> klass)
    {
        return adaptedLogger;
    }
}
