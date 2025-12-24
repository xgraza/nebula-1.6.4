/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class LauncherGUI extends JFrame implements ActionListener
{
    static LauncherGUI INSTANCE;

    private JComboBox<LaunchVersion> versionSelector;
    private JCheckBox dsaCheckbox;

    private LauncherGUI()
    {
        setTitle("Nebula Launcher");
        setSize(320, 80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponents();
        setAlwaysOnTop(true);
        setEnabled(true);
        setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent event)
    {
        switch (event.getActionCommand())
        {
            case "launch":
            {
                final LaunchVersion version = (LaunchVersion) versionSelector.getSelectedItem();
                if (version != null)
                {
                    Config.set("default_option", version.toString());
                    Launcher.launchVersion(version);
                    setVisible(false);
                } else
                {
                    throw new RuntimeException("LaunchVersion is somehow null?");
                }
                break;
            }
            case "dsa":
            {
                if (dsaCheckbox.isSelected())
                {
                    Config.set("hide_gui", "true");
                } else
                {
                    Config.delete("hide_gui");
                }
                break;
            }
        }
    }

    private void addComponents()
    {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        final JButton launchButton = new JButton("Launch");
        launchButton.setActionCommand("launch");
        launchButton.addActionListener(this);

        dsaCheckbox = new JCheckBox("Don't show GUI again");
        dsaCheckbox.setActionCommand("dsa");
        dsaCheckbox.addActionListener(this);

        panel.add(versionSelector = new JComboBox<>(LaunchVersion.values()));
        panel.add(launchButton);
        panel.add(dsaCheckbox);

        add(panel);
    }

    public static void create()
    {
        if (INSTANCE != null)
        {
            return;
        }
        INSTANCE = new LauncherGUI();
    }

    public static void setLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception ignored)
        {
            // SOL
        }
    }
}
