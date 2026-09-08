package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.input.Keyboard.*;

public class StringSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting, IComponentDescription
{
    private static final int PADDING = 1;

    private final Setting<String> setting;

    private boolean typing;
    private int cursor, blinkTicks;

    public StringSettingComponent(Setting<String> setting)
    {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (!typing)
        {
            blinkTicks = 0;
            cursor = setting.getValue().length();
        } else
        {
            ++blinkTicks;
            if (blinkTicks > 200)
            {
                blinkTicks = 0;
            }
        }

        if (isMouseInDynamic(mouseX, mouseY))
        {
            drawValue();
        } else
        {
            typing = false;
            drawName();
        }
    }

    protected void drawName()
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        final String value = "...";
        Fonts.POPPINS.drawStringShadow(value, x + width - Fonts.POPPINS.getStringWidth(value) - (PADDING * 4), y + middle, -1);
    }

    protected void drawValue()
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        String value = setting.getValue();

        double textX = x + (PADDING * 2);
        Fonts.POPPINS.drawStringShadow(value, textX, y + middle, -1);

        if (!typing)
        {
            return;
        }
        if (blinkTicks > 100)
        {
            return;
        }
        double cursorLength = 5.0;
        value = cursor >= setting.getValue().length() - 1 ? value : value.substring(0, cursor);
        double textWidth = Fonts.POPPINS.getStringWidth(value);
        double lineY = y + middle + Fonts.POPPINS.getFontHeight() - 1;
        Render2D.line(textX + textWidth, lineY, textX + textWidth + cursorLength, lineY, 2.5f, 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseInDynamic(mouseX, mouseY))
        {
            typing = !typing;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (!typing)
        {
            return;
        }
        switch (keyCode)
        {
            case KEY_RIGHT:
                moveCursor(1);
                break;
            case KEY_LEFT:
                moveCursor(-1);
                break;
            case KEY_DELETE:
            case KEY_BACK:
            {
                removeAtCursor(Keyboard.isKeyDown(KEY_LSHIFT));
                break;
            }
            case KEY_RETURN:
            {
                typing = false;
                break;
            }
            default:
            {
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
                {
                    insertAtCursor(typedChar);
                }
                break;
            }
        }
    }

    private void insertAtCursor(char typedChar)
    {
        String text = setting.getValue();
        if (cursor == 0)
        {
            setting.setValue(typedChar + text);
        } else if (cursor >= text.length() - 1)
        {
            setting.setValue(text + typedChar);
        } else
        {
            final String beforeCursor = text.substring(0, cursor);
            final String afterCursor = text.substring(cursor);
            setting.setValue(beforeCursor + typedChar + afterCursor);
        }
    }

    private void removeAtCursor(boolean shift)
    {
        String text = setting.getValue();
        if (text.isEmpty() || cursor == 0)
        {
            return;
        }
        if (cursor >= text.length() - 1)
        {
            if (shift)
            {
                setting.setValue("");
                return;
            }
            setting.setValue(text.substring(0, text.length() - 1));
        } else
        {
            if (shift)
            {
                setting.setValue(text.substring(0, cursor));
            } else
            {
                final String beforeCursor = text.substring(0, cursor - 1);
                final String afterCursor = text.substring(cursor);
                setting.setValue(beforeCursor + afterCursor);
            }
        }
    }

    private void moveCursor(int offset)
    {
        final String text = setting.getValue();
        if (text.isEmpty())
        {
            cursor = 0;
            return;
        }
        cursor = Math.min(text.length() - 1, Math.max(0, cursor + offset));
    }

    @Override
    public Setting<?> getSetting()
    {
        return setting;
    }

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public String getDescription()
    {
        return setting.getDescription();
    }
}
