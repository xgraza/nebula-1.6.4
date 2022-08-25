package optifine;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class InstallerFrame extends JFrame
{
    private JLabel ivjLabelOfVersion = null;
    private JLabel ivjLabelMcVersion = null;
    private JPanel ivjPanelCenter = null;
    private JButton ivjButtonInstall = null;
    private JButton ivjButtonClose = null;
    private JPanel ivjPanelBottom = null;
    private JPanel ivjPanelContentPane = null;
    InstallerFrame.IvjEventHandler ivjEventHandler = new InstallerFrame.IvjEventHandler();
    private JTextArea ivjTextArea = null;
    private JButton ivjButtonExtract = null;
    private JLabel ivjLabelFolder = null;
    private JTextField ivjFieldFolder = null;
    private JButton ivjButtonFolder = null;

    public InstallerFrame()
    {
        this.initialize();
    }

    private void customInit()
    {
        try
        {
            this.pack();
            this.setDefaultCloseOperation(3);
            File e = Utils.getWorkingDirectory();
            this.getFieldFolder().setText(e.getPath());
            this.getButtonInstall().setEnabled(false);
            this.getButtonExtract().setEnabled(false);
            String ofVer = Installer.getOptiFineVersion();
            Utils.dbg("OptiFine Version: " + ofVer);
            String[] ofVers = Utils.tokenize(ofVer, "_");
            String mcVer = ofVers[1];
            Utils.dbg("Minecraft Version: " + mcVer);
            String ofEd = Installer.getOptiFineEdition(ofVers);
            Utils.dbg("OptiFine Edition: " + ofEd);
            String ofEdClear = ofEd.replace("_", " ");
            ofEdClear = ofEdClear.replace(" U ", " Ultra ");
            ofEdClear = ofEdClear.replace("L ", "Light ");
            this.getLabelOfVersion().setText("OptiFine " + ofEdClear);
            this.getLabelMcVersion().setText("for Minecraft " + mcVer);
            this.getButtonInstall().setEnabled(true);
            this.getButtonExtract().setEnabled(true);
            this.getButtonInstall().requestFocus();

            if (!Installer.isPatchFile())
            {
                this.getButtonExtract().setVisible(false);
            }
        }
        catch (Exception var7)
        {
            var7.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            InstallerFrame e = new InstallerFrame();
            Utils.centerWindow(e, (Component)null);
            e.show();
        }
        catch (Exception var8)
        {
            String msg = var8.getMessage();

            if (msg != null && msg.equals("QUIET"))
            {
                return;
            }

            var8.printStackTrace();
            String str = Utils.getExceptionStackTrace(var8);
            str = str.replace("\t", "  ");
            JTextArea textArea = new JTextArea(str);
            textArea.setEditable(false);
            Font f = textArea.getFont();
            Font f2 = new Font("Monospaced", f.getStyle(), f.getSize());
            textArea.setFont(f2);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, scrollPane, "Error", 0);
        }
    }

    private void handleException(Throwable e)
    {
        String msg = e.getMessage();

        if (msg == null || !msg.equals("QUIET"))
        {
            e.printStackTrace();
            String str = Utils.getExceptionStackTrace(e);
            str = str.replace("\t", "  ");
            JTextArea textArea = new JTextArea(str);
            textArea.setEditable(false);
            Font f = textArea.getFont();
            Font f2 = new Font("Monospaced", f.getStyle(), f.getSize());
            textArea.setFont(f2);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, scrollPane, "Error", 0);
        }
    }

    private JLabel getLabelOfVersion()
    {
        if (this.ivjLabelOfVersion == null)
        {
            try
            {
                this.ivjLabelOfVersion = new JLabel();
                this.ivjLabelOfVersion.setName("LabelOfVersion");
                this.ivjLabelOfVersion.setBounds(2, 5, 385, 42);
                this.ivjLabelOfVersion.setFont(new Font("Dialog", 1, 18));
                this.ivjLabelOfVersion.setHorizontalAlignment(0);
                this.ivjLabelOfVersion.setPreferredSize(new Dimension(385, 42));
                this.ivjLabelOfVersion.setText("OptiFine ...");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjLabelOfVersion;
    }

    private JLabel getLabelMcVersion()
    {
        if (this.ivjLabelMcVersion == null)
        {
            try
            {
                this.ivjLabelMcVersion = new JLabel();
                this.ivjLabelMcVersion.setName("LabelMcVersion");
                this.ivjLabelMcVersion.setBounds(2, 38, 385, 25);
                this.ivjLabelMcVersion.setFont(new Font("Dialog", 1, 14));
                this.ivjLabelMcVersion.setHorizontalAlignment(0);
                this.ivjLabelMcVersion.setPreferredSize(new Dimension(385, 25));
                this.ivjLabelMcVersion.setText("for Minecraft ...");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjLabelMcVersion;
    }

    private JPanel getPanelCenter()
    {
        if (this.ivjPanelCenter == null)
        {
            try
            {
                this.ivjPanelCenter = new JPanel();
                this.ivjPanelCenter.setName("PanelCenter");
                this.ivjPanelCenter.setLayout((LayoutManager)null);
                this.ivjPanelCenter.add(this.getLabelOfVersion(), this.getLabelOfVersion().getName());
                this.ivjPanelCenter.add(this.getLabelMcVersion(), this.getLabelMcVersion().getName());
                this.ivjPanelCenter.add(this.getTextArea(), this.getTextArea().getName());
                this.ivjPanelCenter.add(this.getLabelFolder(), this.getLabelFolder().getName());
                this.ivjPanelCenter.add(this.getFieldFolder(), this.getFieldFolder().getName());
                this.ivjPanelCenter.add(this.getButtonFolder(), this.getButtonFolder().getName());
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjPanelCenter;
    }

    private JButton getButtonInstall()
    {
        if (this.ivjButtonInstall == null)
        {
            try
            {
                this.ivjButtonInstall = new JButton();
                this.ivjButtonInstall.setName("ButtonInstall");
                this.ivjButtonInstall.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonInstall.setText("Install");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjButtonInstall;
    }

    private JButton getButtonClose()
    {
        if (this.ivjButtonClose == null)
        {
            try
            {
                this.ivjButtonClose = new JButton();
                this.ivjButtonClose.setName("ButtonClose");
                this.ivjButtonClose.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonClose.setText("Cancel");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjButtonClose;
    }

    private JPanel getPanelBottom()
    {
        if (this.ivjPanelBottom == null)
        {
            try
            {
                this.ivjPanelBottom = new JPanel();
                this.ivjPanelBottom.setName("PanelBottom");
                this.ivjPanelBottom.setLayout(new FlowLayout(1, 15, 10));
                this.ivjPanelBottom.setPreferredSize(new Dimension(390, 55));
                this.ivjPanelBottom.add(this.getButtonInstall(), this.getButtonInstall().getName());
                this.ivjPanelBottom.add(this.getButtonExtract(), this.getButtonExtract().getName());
                this.ivjPanelBottom.add(this.getButtonClose(), this.getButtonClose().getName());
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjPanelBottom;
    }

    private JPanel getPanelContentPane()
    {
        if (this.ivjPanelContentPane == null)
        {
            try
            {
                this.ivjPanelContentPane = new JPanel();
                this.ivjPanelContentPane.setName("PanelContentPane");
                this.ivjPanelContentPane.setLayout(new BorderLayout(5, 5));
                this.ivjPanelContentPane.setPreferredSize(new Dimension(394, 203));
                this.ivjPanelContentPane.add(this.getPanelCenter(), "Center");
                this.ivjPanelContentPane.add(this.getPanelBottom(), "South");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjPanelContentPane;
    }

    private void initialize()
    {
        try
        {
            this.setName("InstallerFrame");
            this.setSize(404, 236);
            this.setDefaultCloseOperation(0);
            this.setResizable(false);
            this.setTitle("OptiFine Installer");
            this.setContentPane(this.getPanelContentPane());
            this.initConnections();
        }
        catch (Throwable var2)
        {
            this.handleException(var2);
        }

        this.customInit();
    }

    public void onInstall()
    {
        try
        {
            File e = new File(this.getFieldFolder().getText());

            if (!e.exists())
            {
                Utils.showErrorMessage("Folder not found: " + e.getPath());
                return;
            }

            if (!e.isDirectory())
            {
                Utils.showErrorMessage("Not a folder: " + e.getPath());
                return;
            }

            Installer.doInstall(e);
            Utils.showMessage("OptiFine is successfully installed.");
            this.dispose();
        }
        catch (Exception var2)
        {
            this.handleException(var2);
        }
    }

    public void onExtract()
    {
        try
        {
            File e = new File(this.getFieldFolder().getText());

            if (!e.exists())
            {
                Utils.showErrorMessage("Folder not found: " + e.getPath());
                return;
            }

            if (!e.isDirectory())
            {
                Utils.showErrorMessage("Not a folder: " + e.getPath());
                return;
            }

            boolean ok = Installer.doExtract(e);

            if (ok)
            {
                Utils.showMessage("OptiFine is successfully extracted.");
                this.dispose();
            }
        }
        catch (Exception var3)
        {
            this.handleException(var3);
        }
    }

    public void onClose()
    {
        this.dispose();
    }

    private void connEtoC1(ActionEvent arg1)
    {
        try
        {
            this.onInstall();
        }
        catch (Throwable var3)
        {
            this.handleException(var3);
        }
    }

    private void connEtoC2(ActionEvent arg1)
    {
        try
        {
            this.onClose();
        }
        catch (Throwable var3)
        {
            this.handleException(var3);
        }
    }

    private void initConnections() throws Exception
    {
        this.getButtonFolder().addActionListener(this.ivjEventHandler);
        this.getButtonInstall().addActionListener(this.ivjEventHandler);
        this.getButtonExtract().addActionListener(this.ivjEventHandler);
        this.getButtonClose().addActionListener(this.ivjEventHandler);
    }

    private JTextArea getTextArea()
    {
        if (this.ivjTextArea == null)
        {
            try
            {
                this.ivjTextArea = new JTextArea();
                this.ivjTextArea.setName("TextArea");
                this.ivjTextArea.setBounds(15, 66, 365, 44);
                this.ivjTextArea.setEditable(false);
                this.ivjTextArea.setEnabled(true);
                this.ivjTextArea.setFont(new Font("Dialog", 0, 12));
                this.ivjTextArea.setLineWrap(true);
                this.ivjTextArea.setOpaque(false);
                this.ivjTextArea.setPreferredSize(new Dimension(365, 44));
                this.ivjTextArea.setText("This installer will install OptiFine in the official Minecraft launcher and will create a new profile \"OptiFine\" for it.");
                this.ivjTextArea.setWrapStyleWord(true);
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjTextArea;
    }

    private JButton getButtonExtract()
    {
        if (this.ivjButtonExtract == null)
        {
            try
            {
                this.ivjButtonExtract = new JButton();
                this.ivjButtonExtract.setName("ButtonExtract");
                this.ivjButtonExtract.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonExtract.setText("Extract");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjButtonExtract;
    }

    private void connEtoC3(ActionEvent arg1)
    {
        try
        {
            this.onExtract();
        }
        catch (Throwable var3)
        {
            this.handleException(var3);
        }
    }

    private JLabel getLabelFolder()
    {
        if (this.ivjLabelFolder == null)
        {
            try
            {
                this.ivjLabelFolder = new JLabel();
                this.ivjLabelFolder.setName("LabelFolder");
                this.ivjLabelFolder.setBounds(15, 116, 47, 16);
                this.ivjLabelFolder.setPreferredSize(new Dimension(47, 16));
                this.ivjLabelFolder.setText("Folder");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjLabelFolder;
    }

    private JTextField getFieldFolder()
    {
        if (this.ivjFieldFolder == null)
        {
            try
            {
                this.ivjFieldFolder = new JTextField();
                this.ivjFieldFolder.setName("FieldFolder");
                this.ivjFieldFolder.setBounds(62, 114, 287, 20);
                this.ivjFieldFolder.setEditable(false);
                this.ivjFieldFolder.setPreferredSize(new Dimension(287, 20));
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjFieldFolder;
    }

    private JButton getButtonFolder()
    {
        if (this.ivjButtonFolder == null)
        {
            try
            {
                this.ivjButtonFolder = new JButton();
                this.ivjButtonFolder.setName("ButtonFolder");
                this.ivjButtonFolder.setBounds(350, 114, 25, 20);
                this.ivjButtonFolder.setMargin(new Insets(2, 2, 2, 2));
                this.ivjButtonFolder.setPreferredSize(new Dimension(25, 20));
                this.ivjButtonFolder.setText("...");
            }
            catch (Throwable var2)
            {
                this.handleException(var2);
            }
        }

        return this.ivjButtonFolder;
    }

    public void onFolderSelect()
    {
        File dirMc = new File(this.getFieldFolder().getText());
        JFileChooser jfc = new JFileChooser(dirMc);
        jfc.setFileSelectionMode(1);
        jfc.setAcceptAllFileFilterUsed(false);

        if (jfc.showOpenDialog(this) == 0)
        {
            File dir = jfc.getSelectedFile();
            this.getFieldFolder().setText(dir.getPath());
        }
    }

    private void connEtoC4(ActionEvent arg1)
    {
        try
        {
            this.onFolderSelect();
        }
        catch (Throwable var3)
        {
            this.handleException(var3);
        }
    }

    class IvjEventHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == InstallerFrame.this.getButtonClose())
            {
                InstallerFrame.this.connEtoC2(e);
            }

            if (e.getSource() == InstallerFrame.this.getButtonExtract())
            {
                InstallerFrame.this.connEtoC3(e);
            }

            if (e.getSource() == InstallerFrame.this.getButtonFolder())
            {
                InstallerFrame.this.connEtoC4(e);
            }

            if (e.getSource() == InstallerFrame.this.getButtonInstall())
            {
                InstallerFrame.this.connEtoC1(e);
            }
        }
    }
}
