/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import ez.nebula.client.Nebula;
import ez.nebula.client.impl.module.exploit.LongChatModule;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ez.nebula.client.api.manager.command.CommandManager;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.impl.module.player.TranslateModule;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.input.Keyboard.*;

public class GuiChat extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private String field_146410_g = "";
    private int chatSize = -1;
    private boolean parsedTabComplete;
    private boolean waitForTabComplete;
    private int tabCompleteIndex;
    private final List<String> tabCompleteCandidateList = new ArrayList<>();
    private URI linkToOpen;
    protected GuiTextField chatTextField;
    private String text = "";

    private final List<String> suggestionList = new ArrayList<>();
    private int suggestionIndex;
    private final CommandManager commandManager;
    private ParseResults<CommandSource> parseResults;

    public GuiChat()
    {
        this("");
    }

    public GuiChat(String text)
    {
        this.text = text;
        commandManager = Nebula.COMMANDS;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.chatSize = this.mc.ingameGUI.getChatGui().getSentMessages().size();
        this.chatTextField = new GuiTextField(this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
        this.chatTextField.setMaxTextLength(LongChatModule.INSTANCE.isToggled() ? Integer.MAX_VALUE : 100);
        this.chatTextField.func_146185_a(false);
        this.chatTextField.setFocused(true);
        this.chatTextField.setText(this.text);
        this.chatTextField.func_146205_d(false);

        suggestionIndex = 0;
        suggestionList.clear();
        parseResults = null;
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGui().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.chatTextField.updateCursorCounter();
        parseCommandResults();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        chatTextField.drawTextBox();

        drawCommandInfo();
        handleHoverEvent(mouseX, mouseY);

        if (chatTextField.getText().startsWith(CommandManager.COMMAND_PREFIX))
        {
            chatTextField.setMaxTextLength(Integer.MAX_VALUE);
        } else
        {
            chatTextField.setMaxTextLength(LongChatModule.INSTANCE.isToggled() ? Integer.MAX_VALUE : 100);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCommandInfo()
    {
        String input = chatTextField.getText();
        if (!input.startsWith(CommandManager.COMMAND_PREFIX))
        {
            return;
        }
        int x = chatTextField.posX;
        int y = chatTextField.posY - fontRenderer.FONT_HEIGHT - 6;

        if (parseResults == null)
        {
            drawString(fontRenderer, EnumChatFormatting.ITALIC
                    + "No command found with that name", x, y, -1);
            return;
        }

        if (!suggestionList.isEmpty())
        {
            final int lengthPerPage = 10;
            final int page = suggestionIndex / lengthPerPage;

            final List<String> paginatedSuggestions = suggestionList.subList(page * lengthPerPage, Math.min(suggestionList.size(), (page + 1) * lengthPerPage));

            int maxWidth = 0;
            for (String suggestion : paginatedSuggestions)
            {
                int textWidth = fontRenderer.getStringWidth(suggestion);
                if (textWidth > maxWidth)
                {
                    maxWidth = textWidth;
                }
            }

            x += fontRenderer.getStringWidth(chatTextField.getText()) + 1;

            int selectedItemOnPage = suggestionIndex % paginatedSuggestions.size();
            for (int i = 0; i < paginatedSuggestions.size(); ++i)
            {
                final String suggestion = paginatedSuggestions.get(i);
                int posY = y - (i * (fontRenderer.FONT_HEIGHT + 2));
                drawRect(x - 2, posY - 2, x + maxWidth + 4, posY + fontRenderer.FONT_HEIGHT, 0xAA000000);
                drawString(fontRenderer, suggestion, x, posY, selectedItemOnPage == i ? 0x00CCCC : -1);
            }
        }
//
//        if (!parseResults.getExceptions().isEmpty())
//        {
//            int offset = 0;
//            for (final CommandNode<CommandSource> node : parseResults.getExceptions().keySet())
//            {
//                final CommandSyntaxException syntaxException = parseResults.getExceptions().get(node);
//                drawString(fontRenderer, EnumChatFormatting.RED.toString()
//                                + EnumChatFormatting.ITALIC
//                                + syntaxException.getMessage(), x,
//                        y - (offset * (fontRenderer.FONT_HEIGHT + 2)), -1);
//                ++offset;
//            }
//            return;
//        }

//        if (!suggestionList.isEmpty())
//        {
//            final int pages = (int) Math.ceil(suggestionList.size() / 10.0);
//            final int page = suggestionIndex / 10;
//            System.out.println(page + " index: " + suggestionIndex);
//            final List<String> paginatedSuggestions = suggestionList.subList(page * 10, Math.min(suggestionList.size(), (page + 1) * 10));
//
//            for (int i = paginatedSuggestions.size() - 1; i >= 0; --i)
//            {
//                final String suggestion = paginatedSuggestions.get(i);
//                drawString(fontRenderer, suggestion, x, y - (i * (fontRenderer.FONT_HEIGHT + 2)), ((page * 5) + suggestionIndex) == i ? 0x00CCCC : -1);
//            }
//        } else
//        {
//            final String usage = commandManager.getSmartUsage(parseResults);
//            if (usage != null && !usage.isEmpty())
//            {
//                int yOffset = fontRenderer.getStringWidth(input.trim() + " ");
//                drawString(fontRenderer, EnumChatFormatting.GRAY.toString()
//                        + EnumChatFormatting.ITALIC
//                        + usage, x + yOffset, y, -1);
//            }
//        }

    }

    private void handleHoverEvent(int mouseX, int mouseY)
    {
        IChatComponent componentAt = this.mc.ingameGUI.getChatGui().getComponentAt(Mouse.getX(), Mouse.getY());
        if (componentAt != null && componentAt.getChatStyle().getChatHoverEvent() != null)
        {
            final HoverEvent hoverEvent = componentAt.getChatStyle().getChatHoverEvent();
            switch (hoverEvent.getAction())
            {
                case SHOW_ITEM:
                {
                    ItemStack itemStack = null;
                    try
                    {
                        final NBTBase nbtBase = JsonToNBT.func_150315_a(hoverEvent.getValue().getUnformattedText());
                        if (nbtBase instanceof NBTTagCompound)
                        {
                            itemStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbtBase);
                        }
                    } catch (final NBTException ignored)
                    {

                    }

                    if (itemStack != null)
                    {
                        this.renderItem(itemStack, mouseX, mouseY);
                    } else
                    {
                        this.renderText(EnumChatFormatting.RED + "Invalid Item!", mouseX, mouseY);
                    }
                    break;
                }
                case SHOW_TEXT:
                {
                    this.renderText(hoverEvent.getValue().getFormattedText(), mouseX, mouseY);
                    break;
                }
                case SHOW_ACHIEVEMENT:
                {
                    StatBase statBase = StatList.func_151177_a(hoverEvent.getValue().getUnformattedText());
                    if (statBase == null)
                    {
                        this.renderText(EnumChatFormatting.RED + "Invalid statistic/achievement!", mouseX, mouseY);
                        break;
                    }

                    final IChatComponent chatComponent = statBase.func_150951_e();
                    final ChatComponentTranslation translationComponent = new ChatComponentTranslation(
                            "stats.tooltip.type."
                                    + (statBase.isAchievement() ? "achievement" : "statistic"));
                    translationComponent.getChatStyle().setItalic(true);
                    final String description = statBase instanceof Achievement
                            ? ((Achievement) statBase).getDescription()
                            : null;
                    ArrayList<String> textList = Lists.newArrayList(chatComponent.getFormattedText(), translationComponent.getFormattedText());

                    if (description != null)
                    {
                        textList.addAll(this.fontRenderer.listFormattedStringToWidth(description, 150));
                    }

                    this.renderTextList(textList, mouseX, mouseY);
                    break;
                }
            }

            GL11.glDisable(GL11.GL_LIGHTING);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        this.waitForTabComplete = false;

        if (chatTextField.getText().startsWith(CommandManager.COMMAND_PREFIX))
        {
            handleNebulaKeyPress(typedChar, keyCode);
            return;
        }

        suggestionList.clear();
        suggestionIndex = 0;
        parseResults = null;

        if (keyCode == KEY_TAB)
        {
            this.offerTabCompleteResults();
        } else
        {
            this.parsedTabComplete = false;
        }

        if (keyCode == KEY_ESCAPE)
        {
            this.mc.displayGuiScreen(null);
        } else if (keyCode != KEY_RETURN && keyCode != KEY_NUMPADENTER)
        {
            if (keyCode == KEY_UP)
            {
                this.getChatHistory(-1);
            } else if (keyCode == KEY_DOWN)
            {
                this.getChatHistory(1);
            } else if (keyCode == KEY_PRIOR)
            {
                this.mc.ingameGUI.getChatGui().scroll(this.mc.ingameGUI.getChatGui().getHeightPerElement() - 1);
            } else if (keyCode == KEY_NEXT)
            {
                this.mc.ingameGUI.getChatGui().scroll(-this.mc.ingameGUI.getChatGui().getHeightPerElement() + 1);
            } else
            {
                this.chatTextField.textboxKeyTyped(typedChar, keyCode);
            }
        } else
        {
            String var3 = this.chatTextField.getText().trim();
            if (!var3.isEmpty())
            {
                this.sendMessage(var3);
            }

            this.mc.displayGuiScreen(null);
        }
    }

    private void handleNebulaKeyPress(char typedChar, int keyCode)
    {
        switch (keyCode)
        {
            case KEY_ESCAPE:
            {
                mc.displayGuiScreen(null);
                break;
            }
            case KEY_UP:
            {
                suggestionIndex++;
                if (suggestionIndex > suggestionList.size() - 1)
                {
                    suggestionIndex = 0;
                }
                break;
            }
            case KEY_DOWN:
            {
                suggestionIndex--;
                if (suggestionIndex < 0)
                {
                    suggestionIndex = suggestionList.size() - 1;
                }

                break;
            }
            case KEY_TAB:
            {
                if (suggestionList.isEmpty() || parseResults == null)
                {
                    break;
                }
                final String suggestion = suggestionList.get(suggestionIndex);
                final CommandNode<CommandSource> lastNode = commandManager.getLastCommandNode(parseResults);

                if (lastNode instanceof RootCommandNode)
                {
                    chatTextField.setText(CommandManager.COMMAND_PREFIX + suggestion + " ");
                    suggestionIndex = 0;
                    suggestionList.clear();
                } else
                {
                    // System.out.println(lastNode);
                }
                break;
            }
            case KEY_RETURN:
            case KEY_NUMPADENTER:
            {
                if (parseResults == null || !parseResults.getExceptions().isEmpty())
                {
                    break;
                }
                commandManager.execute(parseResults);
                mc.displayGuiScreen(null);
                mc.ingameGUI.getChatGui().addToSentMessages(chatTextField.getText().trim());
                break;
            }
            default:
            {
                chatTextField.textboxKeyTyped(typedChar, keyCode);
                break;
            }
        }
    }

    private void parseCommandResults()
    {
        String input = chatTextField.getText();
        if (input == null || input.isEmpty())
        {
            input = CommandManager.COMMAND_PREFIX;
        }
        if (!input.startsWith(CommandManager.COMMAND_PREFIX))
        {
            parseResults = null;
            suggestionList.clear();
            suggestionIndex = 0;
            return;
        }
        parseResults = commandManager.parse(input);
        if (parseResults != null)
        {
            commandManager.addSuggestions(parseResults, suggestionList);
            if (suggestionList.isEmpty())
            {
                suggestionIndex = 0;
                final String usage = commandManager.getSmartUsage(parseResults);
                if (usage != null && !usage.isEmpty())
                {
                    suggestionList.add(usage);
                }
            }
        }
    }

    public void sendMessage(String message)
    {
        this.mc.ingameGUI.getChatGui().addToSentMessages(message);
        this.mc.thePlayer.sendChatMessage(message);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();

        if (scroll != 0)
        {
            if (scroll > 1)
            {
                scroll = 1;
            }

            if (scroll < -1)
            {
                scroll = -1;
            }

            if (!isShiftKeyDown())
            {
                scroll *= 7;
            }

            this.mc.ingameGUI.getChatGui().scroll(scroll);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && this.mc.gameSettings.chatLinks)
        {
            IChatComponent var4 = this.mc.ingameGUI.getChatGui().getComponentAt(Mouse.getX(), Mouse.getY());

            if (var4 != null)
            {
                ClickEvent var5 = var4.getChatStyle().getChatClickEvent();

                if (var5 != null)
                {
                    if (isShiftKeyDown())
                    {
                        this.chatTextField.func_146191_b(var4.getUnformattedTextForChat());
                    } else
                    {
                        URI var6;

                        if (var5.getAction() == ClickEvent.Action.OPEN_URL)
                        {
                            try
                            {
                                var6 = new URI(var5.getValue());

                                if (this.mc.gameSettings.chatLinksPrompt)
                                {
                                    this.linkToOpen = var6;
                                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, var5.getValue(), 0, false));
                                } else
                                {
                                    this.openURL(var6);
                                }
                            } catch (URISyntaxException var7)
                            {
                                LOGGER.error("Can't open url for " + var5, var7);
                            }
                        } else if (var5.getAction() == ClickEvent.Action.OPEN_FILE)
                        {
                            var6 = (new File(var5.getValue())).toURI();
                            this.openURL(var6);
                        } else if (var5.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                        {
                            this.chatTextField.setText(var5.getValue());
                        } else if (var5.getAction() == ClickEvent.Action.RUN_COMMAND)
                        {
                            this.sendMessage(var5.getValue());
                        } else
                        {
                            if (TranslateModule.INSTANCE.isToggled() && var5.getValue().equals("NEBULA_TRANSLATE"))
                            {
                                TranslateModule.INSTANCE.handleTranslate(var4);
                            } else
                            {
                                LOGGER.error("Don't know how to handle " + var5);
                            }
                        }
                    }

                    return;
                }
            }
        }

        this.chatTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void confirmClicked(boolean confirmed, int result)
    {
        if (result == 0)
        {
            if (confirmed)
            {
                this.openURL(this.linkToOpen);
            }

            this.linkToOpen = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openURL(URI uri)
    {
        try
        {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object getDesktopMethod = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null);
            desktopClass.getMethod("browse", URI.class).invoke(getDesktopMethod, uri);
        } catch (final Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public void offerTabCompleteResults()
    {
        String sanitizedText;

        if (this.parsedTabComplete)
        {
            this.chatTextField.func_146175_b(this.chatTextField.func_146197_a(-1, this.chatTextField.func_146198_h(), false) - this.chatTextField.func_146198_h());

            if (this.tabCompleteIndex >= this.tabCompleteCandidateList.size())
            {
                this.tabCompleteIndex = 0;
            }
        } else
        {
            int var1 = this.chatTextField.func_146197_a(-1, this.chatTextField.func_146198_h(), false);
            this.tabCompleteCandidateList.clear();
            this.tabCompleteIndex = 0;
            sanitizedText = this.chatTextField.getText().substring(0, this.chatTextField.func_146198_h());
            this.tabComplete(sanitizedText);

            if (this.tabCompleteCandidateList.isEmpty())
            {
                return;
            }

            this.parsedTabComplete = true;
            this.chatTextField.func_146175_b(var1 - this.chatTextField.func_146198_h());
        }

        if (this.tabCompleteCandidateList.size() > 1)
        {
            StringBuilder var4 = new StringBuilder();

            for (Iterator<String> var5 = this.tabCompleteCandidateList.iterator(); var5.hasNext(); var4.append(sanitizedText))
            {
                sanitizedText = var5.next();

                if (var4.length() > 0)
                {
                    var4.append(", ");
                }
            }

            this.mc.ingameGUI.getChatGui().printChatMessageWithOptionalDeletion(new ChatComponentText(var4.toString()), 1);
        }

        this.chatTextField.func_146191_b(this.tabCompleteCandidateList.get(this.tabCompleteIndex++));
    }

    private void tabComplete(String text)
    {
        if (!text.isEmpty())
        {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(text));
            this.waitForTabComplete = true;
        }
    }

    public void getChatHistory(int position)
    {
        int index = this.chatSize + position;
        int size = this.mc.ingameGUI.getChatGui().getSentMessages().size();

        if (index < 0)
        {
            index = 0;
        }

        if (index > size)
        {
            index = size;
        }

        if (index != this.chatSize)
        {
            if (index == size)
            {
                this.chatSize = size;
                this.chatTextField.setText(this.field_146410_g);
            } else
            {
                if (this.chatSize == size)
                {
                    this.field_146410_g = this.chatTextField.getText();
                }

                this.chatTextField.setText((String) this.mc.ingameGUI.getChatGui().getSentMessages().get(index));
                this.chatSize = index;
            }
        }
    }

    public void handleServerTabComplete(final String[] candidates)
    {
        if (this.waitForTabComplete)
        {
            waitForTabComplete = false;
            this.parsedTabComplete = false;
            this.tabCompleteCandidateList.clear();

            for (String candidate : candidates)
            {
                if (!candidate.isEmpty())
                {
                    this.tabCompleteCandidateList.add(candidate);
                }
            }

            if (!this.tabCompleteCandidateList.isEmpty())
            {
                this.parsedTabComplete = true;
                this.offerTabCompleteResults();
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
