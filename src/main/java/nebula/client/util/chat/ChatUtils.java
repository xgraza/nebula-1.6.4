package nebula.client.util.chat;

/**
 * @author Gavin
 * @since 08/11/23
 */
public class ChatUtils {

  /**
   * The § symbol used for the minecraft font renderer to use a color
   */
  private static final String SECTION_SYMBOL = "\u00a7";

  /**
   * Replaces all occurrences of &[...] with §[...]
   * @param s the string
   * @return the formatted string
   */
  public static String replaceFormatting(String s) {
    return s.replaceAll("&", SECTION_SYMBOL);
  }
}
