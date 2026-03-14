package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GuiMultiplayer extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private final OldServerPinger field_146797_f = new OldServerPinger();
    private final GuiScreen field_146798_g;
    private ServerSelectionList field_146803_h;
    private ServerList field_146804_i;
    private GuiButton field_146810_r;
    private GuiButton field_146809_s;
    private GuiButton field_146808_t;
    private boolean field_146807_u;
    private boolean field_146806_v;
    private boolean field_146805_w;
    private boolean field_146813_x;
    private String field_146812_y;
    private ServerData serverData;
    private LanServerDetector.LanServerList field_146799_A;
    private LanServerDetector.ThreadLanServerFind field_146800_B;
    private boolean field_146801_C;
    private static final String __OBFID = "CL_00000814";

    public GuiMultiplayer(GuiScreen par1GuiScreen)
    {
        this.field_146798_g = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (!this.field_146801_C)
        {
            this.field_146801_C = true;
            this.field_146804_i = new ServerList(this.mc);
            this.field_146804_i.loadServerList();
            this.field_146799_A = new LanServerDetector.LanServerList();

            try
            {
                this.field_146800_B = new LanServerDetector.ThreadLanServerFind(this.field_146799_A);
                this.field_146800_B.start();
            } catch (Exception var2)
            {
                logger.warn("Unable to start LAN server detection: " + var2.getMessage());
            }

            this.field_146803_h = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
            this.field_146803_h.func_148195_a(this.field_146804_i);
        } else
        {
            this.field_146803_h.func_148122_a(this.width, this.height, 32, this.height - 64);
        }

        this.func_146794_g();
    }

    public void func_146794_g()
    {
        this.buttonList.add(this.field_146810_r = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
        this.buttonList.add(this.field_146808_t = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
        this.buttonList.add(this.field_146809_s = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
        this.func_146790_a(this.field_146803_h.func_148193_k());
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (this.field_146799_A.getWasUpdated())
        {
            List var1 = this.field_146799_A.getLanServers();
            this.field_146799_A.setWasNotUpdated();
            this.field_146803_h.func_148194_a(var1);
        }

        this.field_146797_f.func_147223_a();
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.field_146800_B != null)
        {
            this.field_146800_B.interrupt();
            this.field_146800_B = null;
        }

        this.field_146797_f.func_147226_b();
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            GuiListExtended.IGuiListEntry var2 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

            if (button.id == 2 && var2 instanceof ServerListEntryNormal)
            {
                String serverName = ((ServerListEntryNormal) var2).func_148296_a().serverName;

                if (serverName != null)
                {
                    this.field_146807_u = true;
                    String var4 = I18n.format("selectServer.deleteQuestion");
                    String var5 = "'" + serverName + "' " + I18n.format("selectServer.deleteWarning");
                    String var6 = I18n.format("selectServer.deleteButton");
                    String var7 = I18n.format("gui.cancel");
                    GuiYesNo screen = new GuiYesNo(this, var4, var5, var6, var7, this.field_146803_h.func_148193_k());
                    this.mc.displayGuiScreen(screen);
                }
            } else if (button.id == 1)
            {
                this.func_146796_h();
            } else if (button.id == 4)
            {
                this.field_146813_x = true;
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.serverData = new ServerData(I18n.format("selectServer.defaultName"), "")));
            } else if (button.id == 3)
            {
                this.field_146806_v = true;
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.serverData = new ServerData(I18n.format("selectServer.defaultName"), "")));
            } else if (button.id == 7 && var2 instanceof ServerListEntryNormal)
            {
                this.field_146805_w = true;
                ServerData var3 = ((ServerListEntryNormal) var2).func_148296_a();
                this.serverData = new ServerData(var3.serverName, var3.serverIP);
                this.serverData.setHideAddress(var3.isHidingAddress());
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.serverData));
            } else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.field_146798_g);
            } else if (button.id == 8)
            {
                this.func_146792_q();
            }
        }
    }

    private void func_146792_q()
    {
        this.mc.displayGuiScreen(new GuiMultiplayer(this.field_146798_g));
    }

    public void confirmClicked(boolean par1, int par2)
    {
        GuiListExtended.IGuiListEntry var3 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

        if (this.field_146807_u)
        {
            this.field_146807_u = false;

            if (par1 && var3 instanceof ServerListEntryNormal)
            {
                this.field_146804_i.removeServerData(this.field_146803_h.func_148193_k());
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148192_c(-1);
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.mc.displayGuiScreen(this);
        } else if (this.field_146813_x)
        {
            this.field_146813_x = false;

            if (par1)
            {
                this.connectToServer(this.serverData);
            } else
            {
                this.mc.displayGuiScreen(this);
            }
        } else if (this.field_146806_v)
        {
            this.field_146806_v = false;

            if (par1)
            {
                this.field_146804_i.addServerData(this.serverData);
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148192_c(-1);
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.mc.displayGuiScreen(this);
        } else if (this.field_146805_w)
        {
            this.field_146805_w = false;

            if (par1 && var3 instanceof ServerListEntryNormal)
            {
                ServerData var4 = ((ServerListEntryNormal) var3).func_148296_a();
                var4.serverName = this.serverData.serverName;
                var4.serverIP = this.serverData.serverIP;
                var4.setHideAddress(this.serverData.isHidingAddress());
                this.field_146804_i.saveServerList();
                this.field_146803_h.func_148195_a(this.field_146804_i);
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        int var3 = this.field_146803_h.func_148193_k();
        GuiListExtended.IGuiListEntry var4 = var3 < 0 ? null : this.field_146803_h.func_148180_b(var3);

        if (keyCode == 63)
        {
            this.func_146792_q();
        } else
        {
            if (var3 >= 0)
            {
                if (keyCode == 200)
                {
                    if (isShiftKeyDown())
                    {
                        if (var3 > 0 && var4 instanceof ServerListEntryNormal)
                        {
                            this.field_146804_i.swapServers(var3, var3 - 1);
                            this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                            this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                            this.field_146803_h.func_148195_a(this.field_146804_i);
                        }
                    } else if (var3 > 0)
                    {
                        this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                        this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());

                        if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan)
                        {
                            if (this.field_146803_h.func_148193_k() > 0)
                            {
                                this.func_146790_a(this.field_146803_h.getSize() - 1);
                                this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                            } else
                            {
                                this.func_146790_a(-1);
                            }
                        }
                    } else
                    {
                        this.func_146790_a(-1);
                    }
                } else if (keyCode == 208)
                {
                    if (isShiftKeyDown())
                    {
                        if (var3 < this.field_146804_i.countServers() - 1)
                        {
                            this.field_146804_i.swapServers(var3, var3 + 1);
                            this.func_146790_a(var3 + 1);
                            this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            this.field_146803_h.func_148195_a(this.field_146804_i);
                        }
                    } else if (var3 < this.field_146803_h.getSize())
                    {
                        this.func_146790_a(this.field_146803_h.func_148193_k() + 1);
                        this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());

                        if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan)
                        {
                            if (this.field_146803_h.func_148193_k() < this.field_146803_h.getSize() - 1)
                            {
                                this.func_146790_a(this.field_146803_h.getSize() + 1);
                                this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                            } else
                            {
                                this.func_146790_a(-1);
                            }
                        }
                    } else
                    {
                        this.func_146790_a(-1);
                    }
                } else if (keyCode != 28 && keyCode != 156)
                {
                    super.keyTyped(typedChar, keyCode);
                } else
                {
                    this.actionPerformed(this.buttonList.get(2));
                }
            } else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_146812_y = null;
        this.drawDefaultBackground();
        this.field_146803_h.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);

        if (this.field_146812_y != null)
        {
            this.renderTextList(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), par1, par2);
        }
    }

    public void func_146796_h()
    {
        GuiListExtended.IGuiListEntry var1 = this.field_146803_h.func_148193_k() < 0 ? null : this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());

        if (var1 instanceof ServerListEntryNormal)
        {
            this.connectToServer(((ServerListEntryNormal) var1).func_148296_a());
        } else if (var1 instanceof ServerListEntryLanDetected)
        {
            LanServerDetector.LanServer var2 = ((ServerListEntryLanDetected) var1).func_148289_a();
            this.connectToServer(new ServerData(var2.getServerMotd(), var2.getServerIpPort()));
        }
    }

    private void connectToServer(ServerData data)
    {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, data));
    }

    public void func_146790_a(int p_146790_1_)
    {
        this.field_146803_h.func_148192_c(p_146790_1_);
        GuiListExtended.IGuiListEntry var2 = p_146790_1_ < 0 ? null : this.field_146803_h.func_148180_b(p_146790_1_);
        this.field_146809_s.enabled = false;
        this.field_146810_r.enabled = false;
        this.field_146808_t.enabled = false;

        if (var2 != null && !(var2 instanceof ServerListEntryLanScan))
        {
            this.field_146809_s.enabled = true;

            if (var2 instanceof ServerListEntryNormal)
            {
                this.field_146810_r.enabled = true;
                this.field_146808_t.enabled = true;
            }
        }
    }

    public OldServerPinger func_146789_i()
    {
        return this.field_146797_f;
    }

    public void func_146793_a(String p_146793_1_)
    {
        this.field_146812_y = p_146793_1_;
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field_146803_h.func_148179_a(mouseX, mouseY, mouseButton);
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
        this.field_146803_h.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    public ServerList func_146795_p()
    {
        return this.field_146804_i;
    }
}
