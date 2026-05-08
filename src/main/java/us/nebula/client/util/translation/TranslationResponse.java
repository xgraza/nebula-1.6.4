package us.nebula.client.util.translation;

/**
 * @author xgraza
 * @since 03/24/25
 */
@FunctionalInterface
public interface TranslationResponse
{
    void provideResult(final Language source, final String content);
}
