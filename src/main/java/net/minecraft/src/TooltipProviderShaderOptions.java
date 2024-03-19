package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import shadersmod.client.GuiButtonShaderOption;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderOptionProfile;

public class TooltipProviderShaderOptions extends TooltipProviderOptions
{
    public String[] getTooltipLines(GuiButton btn, int width)
    {
        if (!(btn instanceof GuiButtonShaderOption))
        {
            return null;
        }
        else
        {
            GuiButtonShaderOption btnSo = (GuiButtonShaderOption)btn;
            ShaderOption so = btnSo.getShaderOption();
            String[] lines = this.makeTooltipLines(so, width);
            return lines;
        }
    }

    private String[] makeTooltipLines(ShaderOption so, int width)
    {
        if (so instanceof ShaderOptionProfile)
        {
            return null;
        }
        else
        {
            String name = so.getNameText();
            String desc = Config.normalize(so.getDescriptionText()).trim();
            String[] descs = this.splitDescription(desc);
            GameSettings settings = Config.getGameSettings();
            String id = null;

            if (!name.equals(so.getName()) && settings.advancedItemTooltips)
            {
                id = "\u00a78" + Lang.get("of.general.id") + ": " + so.getName();
            }

            String source = null;

            if (so.getPaths() != null && settings.advancedItemTooltips)
            {
                source = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString((Object[])so.getPaths());
            }

            String def = null;

            if (so.getValueDefault() != null && settings.advancedItemTooltips)
            {
                String list = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
                def = "\u00a78" + Lang.getDefault() + ": " + list;
            }

            ArrayList list1 = new ArrayList();
            list1.add(name);
            list1.addAll(Arrays.asList(descs));

            if (id != null)
            {
                list1.add(id);
            }

            if (source != null)
            {
                list1.add(source);
            }

            if (def != null)
            {
                list1.add(def);
            }

            String[] lines = this.makeTooltipLines(width, list1);
            return lines;
        }
    }

    private String[] splitDescription(String desc)
    {
        if (desc.length() <= 0)
        {
            return new String[0];
        }
        else
        {
            desc = StrUtils.removePrefix(desc, "//");
            String[] descs = desc.split("\\. ");

            for (int i = 0; i < descs.length; ++i)
            {
                descs[i] = "- " + descs[i].trim();
                descs[i] = StrUtils.removeSuffix(descs[i], ".");
            }

            return descs;
        }
    }

    private String[] makeTooltipLines(int width, List<String> args)
    {
        FontRenderer fr = Config.getMinecraft().fontRenderer;
        ArrayList list = new ArrayList();

        for (int lines = 0; lines < args.size(); ++lines)
        {
            String arg = (String)args.get(lines);

            if (arg != null && arg.length() > 0)
            {
                List parts = fr.listFormattedStringToWidth(arg, width);
                Iterator it = parts.iterator();

                while (it.hasNext())
                {
                    String part = (String)it.next();
                    list.add(part);
                }
            }
        }

        String[] var10 = (String[])((String[])list.toArray(new String[list.size()]));
        return var10;
    }
}
