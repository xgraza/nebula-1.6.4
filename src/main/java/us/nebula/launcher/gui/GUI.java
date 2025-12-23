/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher.gui;

import us.nebula.launcher.LauncherMain;
import us.nebula.launcher.Wrapper;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author xgraza
 * @since 12/20/25
 */
public final class GUI extends JFrame implements ActionListener
{
    public GUI()
    {
        setTitle("Nebula Launcher");
        setSize(500, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addLaunchButtons();
        setAlwaysOnTop(true);
        setEnabled(true);
        setVisible(true);
    }

    private void addLaunchButtons()
    {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        final JButton launchStableButton = new JButton("Launch Stable");
        launchStableButton.addActionListener(this);
        launchStableButton.setActionCommand("stable");

        final JButton launchLatestButton = new JButton("Launch Latest");
        launchLatestButton.setActionCommand("latest");
        launchLatestButton.addActionListener(this);

        final JCheckBox checkBox = new JCheckBox("Don't ask again");

        panel.add(launchStableButton);
        panel.add(launchLatestButton);
        panel.add(checkBox);

        add(panel);
    }

    @Override
    public void actionPerformed(final ActionEvent event)
    {
        final String actionCommand = event.getActionCommand();
        setVisible(false);
        LauncherMain.WRAPPER.launch(actionCommand.equals("stable")
                ? Wrapper.LAUNCH_TYPE_STABLE
                : Wrapper.LAUNCH_TYPE_LATEST);
    }
}
