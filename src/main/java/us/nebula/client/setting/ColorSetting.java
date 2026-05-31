package us.nebula.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.cheat.impl.render.HUDCheat;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 1/29/26
 */
public final class ColorSetting extends Setting<Color>
{
    private boolean clientSync, exemptClientSync;
    private boolean allowTransparency;

    public ColorSetting(String name, String description, Predicate<Color> visibility, Consumer<Color> valueChanged, Color value, boolean clientSync, boolean exemptClientSync, boolean allowTransparency)
    {
        super(name, description, visibility, valueChanged, value);
        this.clientSync = clientSync;
        this.exemptClientSync = exemptClientSync;
        this.allowTransparency = allowTransparency;
    }

    @Override
    public Color getValue()
    {
        if (clientSync && !exemptClientSync)
        {
            return HUDCheat.INSTANCE.primaryColorSetting.getValue();
        }
        return super.getValue();
    }

    public int getValueInt()
    {
        return getValue().getRGB();
    }

    public Color getWithTransparency(int alpha)
    {
        final Color color = getValue();
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public void setHSB(float hue, float saturation, float brightness)
    {
        setValue(Color.getHSBColor(hue, saturation, brightness));
    }

    public void setTransparency(int alpha)
    {
        if (!allowTransparency)
        {
            return;
        }
        final Color color = getValue();
        setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
    }

    public boolean isAllowTransparency()
    {
        return allowTransparency;
    }

    public void setClientSync(boolean clientSync)
    {
        this.clientSync = clientSync;
    }

    public boolean isClientSync()
    {
        return clientSync;
    }

    public boolean isExemptClientSync()
    {
        return exemptClientSync;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();

        if (object.has("clientSync"))
        {
            clientSync = object.get("clientSync").getAsBoolean();
        }

        int red = 255, green = 255, blue = 255, alpha = 255;
        if (object.has("r"))
        {
            red = object.get("r").getAsInt();
        }
        if (object.has("g"))
        {
            green = object.get("g").getAsInt();
        }
        if (object.has("b"))
        {
            blue = object.get("b").getAsInt();
        }
        if (object.has("a"))
        {
            alpha = object.get("a").getAsInt();
        }
        setValue(new Color(red, green, blue, alpha));
    }

    @Override
    public JsonElement toJSON()
    {
        final Color color = getValue();
        final JsonObject object = new JsonObject();
        object.addProperty("clientSync", clientSync);
        object.addProperty("r", color.getRed());
        object.addProperty("g", color.getGreen());
        object.addProperty("b", color.getBlue());
        object.addProperty("a", color.getAlpha());
        return object;
    }

    public static final class Builder extends Setting.Builder<Color>
    {
        private int red, green, blue, alpha;
        private boolean clientSync, exemptClientSync, allowTransparency;

        public Builder(String name, Color value)
        {
            super(name, value);
            red = value.getRed();
            green = value.getGreen();
            blue = value.getBlue();
            alpha = value.getAlpha();
            allowTransparency = true;
        }

        public Builder setAllowTransparency(boolean allowTransparency)
        {
            this.allowTransparency = allowTransparency;
            return this;
        }

        public Builder setExemptClientSync(final boolean exemptClientSync)
        {
            this.exemptClientSync = exemptClientSync;
            return this;
        }

        public Builder setClientSync(boolean clientSync)
        {
            this.clientSync = clientSync;
            return this;
        }

        public Builder setRed(int red)
        {
            checkColorBounds("red", red);
            this.red = red;
            return this;
        }

        public Builder setGreen(int green)
        {
            checkColorBounds("green", green);
            this.green = green;
            return this;
        }

        public Builder setBlue(int blue)
        {
            checkColorBounds("blue", blue);
            this.blue = blue;
            return this;
        }

        public Builder setAlpha(int alpha)
        {
            checkColorBounds("alpha", alpha);
            this.alpha = alpha;
            return this;
        }

        private void checkColorBounds(String element, int value)
        {
            if (value > 255 || value < 0)
            {
                throw new RuntimeException(String.format("%s value must be less than 255 and greater than 0", element));
            }
        }

        @Override
        public ColorSetting build()
        {
            if (!allowTransparency)
            {
                alpha = 255;
            }
            return new ColorSetting(name, description, visibility, valueChanged, new Color(red, green, blue, alpha), clientSync, exemptClientSync, allowTransparency);
        }
    }
}