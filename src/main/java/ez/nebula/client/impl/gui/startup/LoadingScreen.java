package ez.nebula.client.impl.gui.startup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.LWJGLException;
import ez.nebula.client.core.ClientConfig;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 3/13/26
 */
public final class LoadingScreen
{
    private static int totalLoadingStages, loadingStage;
    private static String loadingStageText;
    private static Minecraft mc;

    public static void render(final Minecraft mc, final ScaledResolution res, final int factor)
    {
        LoadingScreen.mc = mc;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        RenderUtil.renderRectangle(0, 0, width, height, Color.black.getRGB());

        String text = "Loading Nebula " + ClientConfig.VERSION;
        int textWidth = (int) Fonts.POPPINS_LARGE.getStringWidth(text);
        Fonts.POPPINS_LARGE.drawStringShadow(text, width / 2.0 - (textWidth / 2.0), 50, -1);

        boolean waiting = loadingStageText == null || loadingStageText.isEmpty() || loadingStage == 0;
        text = "Stage " + loadingStage + "/" + totalLoadingStages + " - " + loadingStageText;
        if (waiting)
        {
            text = "Almost there...";
        }
        textWidth = (int) Fonts.POPPINS_LARGE.getStringWidth(text);
        Fonts.POPPINS_LARGE.drawStringShadow(text, width / 2.0 - (textWidth / 2.0), res.getScaledHeight_double() - 60, -1);

        double posY = res.getScaledHeight_double() - 30;

        int progressBarTotalWidth = width - 150;
        RenderUtil.renderRectangle(75, posY, progressBarTotalWidth, 20, Color.lightGray.getRGB());
        double progressPercent = loadingStage / (double) totalLoadingStages;
        if (waiting)
        {
            progressPercent = 1;
        }
        RenderUtil.renderRectangle(77, posY + 2, (progressBarTotalWidth - 4) * progressPercent, 16, Color.green.getRGB());
    }

    public static void setTotalLoadingStages(int totalStages)
    {
        totalLoadingStages = totalStages;
        if (totalStages == 0)
        {
            setStage(0, null);
        }
    }

    public static int getTotalLoadingStages()
    {
        return totalLoadingStages;
    }

    public static void setStage(int stage, String text)
    {
        if (stage > totalLoadingStages)
        {
            stage = 0;
        }
        //System.out.println(stage + "/" + totalLoadingStages + " -> " + text);
        loadingStage = stage;
        loadingStageText = text;
        try
        {
            mc.loadScreen();
        } catch (LWJGLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
