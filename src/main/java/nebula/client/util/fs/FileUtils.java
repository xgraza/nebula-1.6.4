package nebula.client.util.fs;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Files;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class FileUtils {

  /**
   * The root directory file object
   */
  public static final File ROOT = new File(
    Minecraft.getMinecraft().mcDataDir, "Nebula");

  public static String readFile(File file) throws IOException {
    InputStream is = Files.newInputStream(file.toPath());

    StringBuilder builder = new StringBuilder();
    int b;
    while ((b = is.read()) != -1) {
      builder.append((char) b);
    }

    is.close();
    return builder.toString();
  }

  public static void writeFile(File file, String content) throws IOException {
    OutputStream os = Files.newOutputStream(file.toPath());
    byte[] bytes = content.getBytes();
    os.write(bytes, 0, bytes.length);
    os.close();
  }
}
