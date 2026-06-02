package ez.nebula.client.util.text.translation;

/**
 * @author xgraza
 * @since 03/24/25
 */
@FunctionalInterface
public interface TranslationResponse
{
    void provideResult(final Language source, final String content);
}
