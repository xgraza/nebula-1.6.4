package us.nebula.api.manager.toast;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.Fonts;
import us.nebula.util.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/03/25
 */
public final class Toast
{
    private static final double PADDING = 3.0;

    private static final int TOAST_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();

    private final ToastType toastType;
    private String title, details;
    private long deathTimeMS, lifeMS;
    private final int id;

    private final Animation animation = new Animation(
            AnimationEasing.CUBIC_IN_OUT, 300);

    public Toast(final int id,
                 final ToastType toastType,
                 final String title,
                 final String details,
                 final long lifeMS)
    {
        this.id = id;
        this.toastType = toastType;
        this.title = title;
        this.details = details;
        this.lifeMS = lifeMS;
        deathTimeMS = System.currentTimeMillis() + lifeMS + 300;
    }

    public double render(final double posY, final ScaledResolution resolution)
    {
        final double screenWidth = resolution.getScaledWidth_double();
        final double toastWidth = Fonts.POPPINS.getStringWidth(details) + (PADDING * 3);
        final double toastHeight = (Fonts.POPPINS.getFontHeight() + 1.0) * 1.5;
        double posX = screenWidth - (PADDING * 2) - (toastWidth * (animation.getEasedFactor()));

        animation.setState(deathTimeMS - 300 > System.currentTimeMillis());

        RenderUtil.roundedRectangle2D(posX, posY, toastWidth, toastHeight, 3.5f, TOAST_BACKGROUND_COLOR);

        final double progressBar = toastWidth * (((deathTimeMS - System.currentTimeMillis()) / (double)lifeMS));
        RenderUtil.roundedRectangle2D(posX, posY + toastHeight - 1.5, progressBar, 1.5, 2.5f, new Color(112, 82, 143).getRGB());

        Fonts.ICONFACE.drawStringShadow(toastType.getIconChar(), posX + 1, posY + 4, 0xAAAAAA);
        Fonts.POPPINS.drawStringShadow(title, posX + 11, posY + 1, -1);
        Fonts.POPPINS.drawStringShadow(details, posX + PADDING, posY + Fonts.POPPINS.getFontHeight() - 5, -1);
        return toastHeight;
    }

    public int getId()
    {
        return id;
    }

    public ToastType getToastType()
    {
        return toastType;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getDetails()
    {
        return details;
    }

    public void resetTime()
    {
        deathTimeMS = System.currentTimeMillis() + lifeMS + 300;
    }

    public boolean isDead()
    {
        return System.currentTimeMillis() > deathTimeMS;
    }
}
