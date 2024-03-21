package nebula.client.util.render;

import java.awt.*;

/**
 * @author Gavin
 * @since 08/15/23
 */
public class ColorUtils {

  public static int alpha(int color, int alpha) {
    return (color & 0x00ffffff) | (alpha << 24);
  }

  public static Color pulse(Color firstColor, Color secondColor, int index, double speed) {
    long now = (long) (speed * System.currentTimeMillis() + -index * 11L);

    float time = 850.0f;

    float rd = (firstColor.getRed() - secondColor.getRed()) / time;
    float gd = (firstColor.getGreen() - secondColor.getGreen()) / time;
    float bd = (firstColor.getBlue() - secondColor.getBlue()) / time;

    float rd2 = (secondColor.getRed() - firstColor.getRed()) / time;
    float gd2 = (secondColor.getGreen() - firstColor.getGreen()) / time;
    float bd2 = (secondColor.getBlue() - firstColor.getBlue()) / time;

    int re1 = Math.round(secondColor.getRed() + rd * (now % (long) time));
    int ge1 = Math.round(secondColor.getGreen() + gd * (now % (long) time));
    int be1 = Math.round(secondColor.getBlue() + bd * (now % (long) time));
    int re2 = Math.round(firstColor.getRed() + rd2 * (now % (long) time));
    int ge2 = Math.round(firstColor.getGreen() + gd2 * (now % (long) time));
    int be2 = Math.round(firstColor.getBlue() + bd2 * (now % (long) time));

    if (now % ((long) time * 2L) < (long) time) {
      return new Color(re2, ge2, be2);
    } else {
      return new Color(re1, ge1, be1);
    }
  }
}
