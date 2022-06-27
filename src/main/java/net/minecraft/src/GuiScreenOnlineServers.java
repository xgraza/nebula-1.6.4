package net.minecraft.src;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiScreenOnlineServers extends GuiScreen
{
    private static final ResourceLocation field_130039_a = new ResourceLocation("textures/gui/widgets.png");
    private GuiScreen field_96188_a;
    private GuiSlotOnlineServerList field_96186_b;
    private static int field_96187_c;
    private static final Object field_96185_d = new Object();
    private long field_96189_n = -1L;
    private GuiButton field_96190_o;
    private GuiButton field_96198_p;
    private GuiButtonLink field_96197_q;
    private GuiButton field_96196_r;
    private String field_96195_s;
    private static McoServerList field_96194_t = new McoServerList();
    private boolean field_96193_u;
    private List field_96192_v = Lists.newArrayList();
    private volatile int field_96199_x = 0;
    private Long field_102019_y;
    private int field_104044_y;

    public GuiScreenOnlineServers(GuiScreen par1GuiScreen)
    {
        this.field_96188_a = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        field_96194_t.func_130129_a(this.mc.getSession());

        if (!this.field_96193_u)
        {
            this.field_96193_u = true;
            this.field_96186_b = new GuiSlotOnlineServerList(this);
        }
        else
        {
            this.field_96186_b.func_104084_a(this.width, this.height, 32, this.height - 64);
        }

        this.func_96178_g();
    }

    public void func_96178_g()
    {
        this.buttonList.add(this.field_96196_r = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.getString("mco.selectServer.play")));
        this.buttonList.add(this.field_96198_p = new GuiButton(2, this.width / 2 - 48, this.height - 52, 100, 20, I18n.getString("mco.selectServer.create")));
        this.buttonList.add(this.field_96190_o = new GuiButton(3, this.width / 2 + 58, this.height - 52, 100, 20, I18n.getString("mco.selectServer.configure")));
        this.buttonList.add(this.field_96197_q = new GuiButtonLink(4, this.width / 2 - 154, this.height - 28, 154, 20, I18n.getString("mco.selectServer.moreinfo")));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 28, 153, 20, I18n.getString("gui.cancel")));
        McoServer var1 = this.func_140030_b(this.field_96189_n);
        this.field_96196_r.enabled = var1 != null && var1.field_96404_d.equals("OPEN") && !var1.field_98166_h;
        this.field_96198_p.enabled = this.field_96199_x > 0;

        if (var1 != null && !var1.field_96405_e.equals(this.mc.getSession().getUsername()))
        {
            this.field_96190_o.displayString = I18n.getString("mco.selectServer.leave");
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.field_104044_y;

        if (field_96194_t.func_130127_a())
        {
            List var1 = field_96194_t.func_98252_c();
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                McoServer var3 = (McoServer)var2.next();
                Iterator var4 = this.field_96192_v.iterator();

                while (var4.hasNext())
                {
                    McoServer var5 = (McoServer)var4.next();

                    if (var3.field_96408_a == var5.field_96408_a)
                    {
                        var3.func_96401_a(var5);

                        if (this.field_102019_y != null && this.field_102019_y.longValue() == var3.field_96408_a)
                        {
                            this.field_102019_y = null;
                            var3.field_96411_l = false;
                        }

                        break;
                    }
                }
            }

            this.field_96199_x = field_96194_t.func_140056_e();
            this.field_96192_v = var1;
            field_96194_t.func_98250_b();
        }

        this.field_96198_p.enabled = this.field_96199_x > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.func_140032_e(this.field_96189_n);
            }
            else if (par1GuiButton.id == 3)
            {
                this.func_140019_s();
            }
            else if (par1GuiButton.id == 0)
            {
                field_96194_t.func_98248_d();
                this.mc.displayGuiScreen(this.field_96188_a);
            }
            else if (par1GuiButton.id == 2)
            {
                field_96194_t.func_98248_d();
                this.mc.displayGuiScreen(new GuiScreenCreateOnlineWorld(this));
            }
            else if (par1GuiButton.id == 4)
            {
                this.field_96197_q.func_96135_a("http://realms.minecraft.net/");
            }
            else
            {
                this.field_96186_b.actionPerformed(par1GuiButton);
            }
        }
    }

    private void func_140019_s()
    {
        McoServer var1 = this.func_140030_b(this.field_96189_n);

        if (var1 != null)
        {
            if (this.mc.getSession().getUsername().equals(var1.field_96405_e))
            {
                McoServer var2 = this.func_98086_a(var1.field_96408_a);

                if (var2 != null)
                {
                    field_96194_t.func_98248_d();
                    this.mc.displayGuiScreen(new GuiScreenConfigureWorld(this, var2));
                }
            }
            else
            {
                String var4 = I18n.getString("mco.configure.world.leave.question.line1");
                String var3 = I18n.getString("mco.configure.world.leave.question.line2");
                this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Info, var4, var3, 3));
            }
        }
    }

    private McoServer func_140030_b(long par1)
    {
        Iterator var3 = this.field_96192_v.iterator();
        McoServer var4;

        do
        {
            if (!var3.hasNext())
            {
                return null;
            }

            var4 = (McoServer)var3.next();
        }
        while (var4.field_96408_a != par1);

        return var4;
    }

    private int func_140009_c(long par1)
    {
        for (int var3 = 0; var3 < this.field_96192_v.size(); ++var3)
        {
            if (((McoServer)this.field_96192_v.get(var3)).field_96408_a == par1)
            {
                return var3;
            }
        }

        return -1;
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par2 == 3 && par1)
        {
            (new ThreadOnlineScreen(this)).start();
        }

        this.mc.displayGuiScreen(this);
    }

    private void func_140012_t()
    {
        int var1 = this.func_140009_c(this.field_96189_n);

        if (this.field_96192_v.size() - 1 == var1)
        {
            --var1;
        }

        if (this.field_96192_v.size() == 0)
        {
            var1 = -1;
        }

        if (var1 >= 0 && var1 < this.field_96192_v.size())
        {
            this.field_96189_n = ((McoServer)this.field_96192_v.get(var1)).field_96408_a;
        }
    }

    public void func_102018_a(long par1)
    {
        this.field_96189_n = -1L;
        this.field_102019_y = Long.valueOf(par1);
    }

    private McoServer func_98086_a(long par1)
    {
        McoClient var3 = new McoClient(this.mc.getSession());

        try
        {
            return var3.func_98176_a(par1);
        }
        catch (ExceptionMcoService var5)
        {
            this.mc.getLogAgent().logSevere(var5.toString());
        }
        catch (IOException var6)
        {
            this.mc.getLogAgent().logWarning("Realms: could not parse response");
        }

        return null;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 59)
        {
            this.mc.gameSettings.hideServerAddress = !this.mc.gameSettings.hideServerAddress;
            this.mc.gameSettings.saveOptions();
        }
        else
        {
            if (par2 != 28 && par2 != 156)
            {
                super.keyTyped(par1, par2);
            }
            else
            {
                this.actionPerformed((GuiButton)this.buttonList.get(0));
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_96195_s = null;
        this.drawDefaultBackground();
        this.field_96186_b.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);

        if (this.field_96195_s != null)
        {
            this.func_96165_a(this.field_96195_s, par1, par2);
        }

        this.func_130038_b(par1, par2);
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.func_130037_c(par1, par2) && field_96194_t.func_130124_d() != 0)
        {
            GuiScreenPendingInvitation var4 = new GuiScreenPendingInvitation(this);
            this.mc.displayGuiScreen(var4);
        }
    }

    private void func_130038_b(int par1, int par2)
    {
        int var3 = field_96194_t.func_130124_d();
        boolean var4 = this.func_130037_c(par1, par2);
        this.mc.getTextureManager().bindTexture(field_130039_a);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        this.drawTexturedModalRect(this.width / 2 + 58, 15, var4 ? 166 : 182, 22, 16, 16);
        GL11.glPopMatrix();
        int var5;
        int var6;

        if (var3 != 0)
        {
            var5 = 198 + (Math.min(var3, 6) - 1) * 8;
            var6 = (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.field_104044_y) * 0.57F), MathHelper.cos((float)this.field_104044_y * 0.35F))) * -6.0F);
            this.mc.getTextureManager().bindTexture(field_130039_a);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPushMatrix();
            this.drawTexturedModalRect(this.width / 2 + 58 + 4, 19 + var6, var5, 22, 8, 8);
            GL11.glPopMatrix();
        }

        if (var4 && var3 != 0)
        {
            var5 = par1 + 12;
            var6 = par2 - 12;
            String var7 = I18n.getString("mco.invites.pending");
            int var8 = this.fontRenderer.getStringWidth(var7);
            this.drawGradientRect(var5 - 3, var6 - 3, var5 + var8 + 3, var6 + 8 + 3, -1073741824, -1073741824);
            this.fontRenderer.drawStringWithShadow(var7, var5, var6, -1);
        }
    }

    private boolean func_130037_c(int par1, int par2)
    {
        int var3 = this.width / 2 + 56;
        int var4 = this.width / 2 + 78;
        byte var5 = 13;
        byte var6 = 27;
        return var3 <= par1 && par1 <= var4 && var5 <= par2 && par2 <= var6;
    }

    private void func_140032_e(long par1)
    {
        McoServer var3 = this.func_140030_b(par1);

        if (var3 != null)
        {
            field_96194_t.func_98248_d();
            GuiScreenLongRunningTask var4 = new GuiScreenLongRunningTask(this.mc, this, new TaskOnlineConnect(this, var3));
            var4.func_98117_g();
            this.mc.displayGuiScreen(var4);
        }
    }

    private void func_101008_c(int par1, int par2, int par3, int par4)
    {
        this.mc.getTextureManager().bindTexture(field_130039_a);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.drawTexturedModalRect(par1 * 2, par2 * 2, 191, 0, 16, 15);
        GL11.glPopMatrix();

        if (par3 >= par1 && par3 <= par1 + 9 && par4 >= par2 && par4 <= par2 + 9)
        {
            this.field_96195_s = I18n.getString("mco.selectServer.expired");
        }
    }

    private void func_104039_b(int par1, int par2, int par3, int par4, int par5)
    {
        if (this.field_104044_y % 20 < 10)
        {
            this.mc.getTextureManager().bindTexture(field_130039_a);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            this.drawTexturedModalRect(par1 * 2, par2 * 2, 207, 0, 16, 15);
            GL11.glPopMatrix();
        }

        if (par3 >= par1 && par3 <= par1 + 9 && par4 >= par2 && par4 <= par2 + 9)
        {
            if (par5 == 0)
            {
                this.field_96195_s = I18n.getString("mco.selectServer.expires.soon");
            }
            else if (par5 == 1)
            {
                this.field_96195_s = I18n.getString("mco.selectServer.expires.day");
            }
            else
            {
                this.field_96195_s = I18n.getStringParams("mco.selectServer.expires.days", new Object[] {Integer.valueOf(par5)});
            }
        }
    }

    private void func_101006_d(int par1, int par2, int par3, int par4)
    {
        this.mc.getTextureManager().bindTexture(field_130039_a);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.drawTexturedModalRect(par1 * 2, par2 * 2, 207, 0, 16, 15);
        GL11.glPopMatrix();

        if (par3 >= par1 && par3 <= par1 + 9 && par4 >= par2 && par4 <= par2 + 9)
        {
            this.field_96195_s = I18n.getString("mco.selectServer.open");
        }
    }

    private void func_101001_e(int par1, int par2, int par3, int par4)
    {
        this.mc.getTextureManager().bindTexture(field_130039_a);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.drawTexturedModalRect(par1 * 2, par2 * 2, 223, 0, 16, 15);
        GL11.glPopMatrix();

        if (par3 >= par1 && par3 <= par1 + 9 && par4 >= par2 && par4 <= par2 + 9)
        {
            this.field_96195_s = I18n.getString("mco.selectServer.closed");
        }
    }

    protected void func_96165_a(String par1Str, int par2, int par3)
    {
        if (par1Str != null)
        {
            int var4 = par2 + 12;
            int var5 = par3 - 12;
            int var6 = this.fontRenderer.getStringWidth(par1Str);
            this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
            this.fontRenderer.drawStringWithShadow(par1Str, var4, var5, -1);
        }
    }

    private void func_96174_a(McoServer par1McoServer) throws IOException
    {
        if (par1McoServer.field_96414_k.equals(""))
        {
            par1McoServer.field_96414_k = EnumChatFormatting.GRAY + "" + 0;
        }

        par1McoServer.field_96415_h = 78;
        ServerAddress var2 = ServerAddress.func_78860_a(par1McoServer.field_96403_g);
        Socket var3 = null;
        DataInputStream var4 = null;
        DataOutputStream var5 = null;

        try
        {
            var3 = new Socket();
            var3.setSoTimeout(3000);
            var3.setTcpNoDelay(true);
            var3.setTrafficClass(18);
            var3.connect(new InetSocketAddress(var2.getIP(), var2.getPort()), 3000);
            var4 = new DataInputStream(var3.getInputStream());
            var5 = new DataOutputStream(var3.getOutputStream());
            var5.write(254);
            var5.write(1);

            if (var4.read() != 255)
            {
                throw new IOException("Bad message");
            }

            String var6 = Packet.readString(var4, 256);
            char[] var7 = var6.toCharArray();

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                if (var7[var8] != 167 && var7[var8] != 0 && ChatAllowedCharacters.allowedCharacters.indexOf(var7[var8]) < 0)
                {
                    var7[var8] = 63;
                }
            }

            var6 = new String(var7);
            int var9;
            int var10;
            String[] var27;

            if (var6.startsWith("\u00a7") && var6.length() > 1)
            {
                var27 = var6.substring(1).split("\u0000");

                if (MathHelper.parseIntWithDefault(var27[0], 0) == 1)
                {
                    par1McoServer.field_96415_h = MathHelper.parseIntWithDefault(var27[1], par1McoServer.field_96415_h);
                    var9 = MathHelper.parseIntWithDefault(var27[4], 0);
                    var10 = MathHelper.parseIntWithDefault(var27[5], 0);

                    if (var9 >= 0 && var10 >= 0)
                    {
                        par1McoServer.field_96414_k = EnumChatFormatting.GRAY + "" + var9;
                    }
                    else
                    {
                        par1McoServer.field_96414_k = "" + EnumChatFormatting.DARK_GRAY + "???";
                    }
                }
                else
                {
                    par1McoServer.field_96415_h = 79;
                    par1McoServer.field_96414_k = "" + EnumChatFormatting.DARK_GRAY + "???";
                }
            }
            else
            {
                var27 = var6.split("\u00a7");
                var6 = var27[0];
                var9 = -1;
                var10 = -1;

                try
                {
                    var9 = Integer.parseInt(var27[1]);
                    var10 = Integer.parseInt(var27[2]);
                }
                catch (Exception var25)
                {
                    ;
                }

                par1McoServer.field_96407_c = EnumChatFormatting.GRAY + var6;

                if (var9 >= 0 && var10 > 0)
                {
                    par1McoServer.field_96414_k = EnumChatFormatting.GRAY + "" + var9;
                }
                else
                {
                    par1McoServer.field_96414_k = "" + EnumChatFormatting.DARK_GRAY + "???";
                }

                par1McoServer.field_96415_h = 77;
            }
        }
        finally
        {
            try
            {
                if (var4 != null)
                {
                    var4.close();
                }
            }
            catch (Throwable var24)
            {
                ;
            }

            try
            {
                if (var5 != null)
                {
                    var5.close();
                }
            }
            catch (Throwable var23)
            {
                ;
            }

            try
            {
                if (var3 != null)
                {
                    var3.close();
                }
            }
            catch (Throwable var22)
            {
                ;
            }
        }
    }

    static long func_140041_a(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.field_96189_n;
    }

    static McoServer func_140011_a(GuiScreenOnlineServers par0GuiScreenOnlineServers, long par1)
    {
        return par0GuiScreenOnlineServers.func_140030_b(par1);
    }

    static Minecraft func_98075_b(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static McoServerList func_140040_h()
    {
        return field_96194_t;
    }

    static List func_140013_c(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.field_96192_v;
    }

    static void func_140017_d(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        par0GuiScreenOnlineServers.func_140012_t();
    }

    static Minecraft func_98076_f(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static Minecraft func_140037_f(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static long func_140036_b(GuiScreenOnlineServers par0GuiScreenOnlineServers, long par1)
    {
        return par0GuiScreenOnlineServers.field_96189_n = par1;
    }

    static Minecraft func_140015_g(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static GuiButton func_140038_h(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.field_96190_o;
    }

    static GuiButton func_140033_i(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.field_96196_r;
    }

    static void func_140008_c(GuiScreenOnlineServers par0GuiScreenOnlineServers, long par1)
    {
        par0GuiScreenOnlineServers.func_140032_e(par1);
    }

    static int func_140027_d(GuiScreenOnlineServers par0GuiScreenOnlineServers, long par1)
    {
        return par0GuiScreenOnlineServers.func_140009_c(par1);
    }

    static Minecraft func_104032_j(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static FontRenderer func_140023_k(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.fontRenderer;
    }

    static void func_104031_c(GuiScreenOnlineServers par0GuiScreenOnlineServers, int par1, int par2, int par3, int par4)
    {
        par0GuiScreenOnlineServers.func_101008_c(par1, par2, par3, par4);
    }

    static void func_140035_b(GuiScreenOnlineServers par0GuiScreenOnlineServers, int par1, int par2, int par3, int par4)
    {
        par0GuiScreenOnlineServers.func_101001_e(par1, par2, par3, par4);
    }

    static Minecraft func_140014_l(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }

    static void func_140031_a(GuiScreenOnlineServers par0GuiScreenOnlineServers, int par1, int par2, int par3, int par4, int par5)
    {
        par0GuiScreenOnlineServers.func_104039_b(par1, par2, par3, par4, par5);
    }

    static void func_140020_c(GuiScreenOnlineServers par0GuiScreenOnlineServers, int par1, int par2, int par3, int par4)
    {
        par0GuiScreenOnlineServers.func_101006_d(par1, par2, par3, par4);
    }

    static FontRenderer func_140039_m(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.fontRenderer;
    }

    static FontRenderer func_98079_k(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.fontRenderer;
    }

    static Object func_140029_i()
    {
        return field_96185_d;
    }

    static int func_140018_j()
    {
        return field_96187_c;
    }

    static int func_140016_k()
    {
        return field_96187_c++;
    }

    static void func_140024_a(GuiScreenOnlineServers par0GuiScreenOnlineServers, McoServer par1McoServer) throws IOException
    {
        par0GuiScreenOnlineServers.func_96174_a(par1McoServer);
    }

    static int func_140021_r()
    {
        return field_96187_c--;
    }

    static FontRenderer func_110402_q(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.fontRenderer;
    }

    static FontRenderer func_140010_p(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.fontRenderer;
    }

    static Minecraft func_142023_q(GuiScreenOnlineServers par0GuiScreenOnlineServers)
    {
        return par0GuiScreenOnlineServers.mc;
    }
}
