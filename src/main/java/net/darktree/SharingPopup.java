package net.darktree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Paths;

public class SharingPopup extends JFrame {
    final int windowWidth = 400;
    final int windowHeight = 400;

    final MainWindow mainWindow;

    private JPanel tabContentPanel;
    private final GridBagConstraints mainContraints;

    public enum SharingType {
        JOIN,
        HOST
    }


    public SharingPopup(SharingType sharingType, MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setTitle("Sharing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new GridBagLayout());
        mainContraints = new GridBagConstraints();
        setPreferredSize(new Dimension(windowWidth, windowHeight));

        // Adding tabs
        JPanel tabs = createTabs();
        mainContraints.gridx = 0;
        mainContraints.gridy = 0;
        mainContraints.weighty = 1;
        mainContraints.weightx = 1;
        mainContraints.fill = GridBagConstraints.BOTH;

        add(tabs, mainContraints);

        setTabAndContent(sharingType);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setTabAndContent(SharingType sharingType) {
        if (mainWindow.getStatus() == Status.HOST) {
            sharingType = SharingType.HOST;
        } else if (mainWindow.getStatus() == Status.IN_GROUP) {
            sharingType = SharingType.JOIN;
        }

        if (tabContentPanel != null) {
            remove(tabContentPanel);
        }

        if (sharingType == SharingType.JOIN) {
            tabContentPanel = createJoinTabContent();
        } else {
            tabContentPanel = createHostTabContent();
        }

        mainContraints.gridx = 0;
        mainContraints.gridy = 1;
        mainContraints.weighty = 20;
        mainContraints.weightx = 0.5;
        mainContraints.fill = GridBagConstraints.BOTH;

        add(tabContentPanel, mainContraints);

        revalidate();
        repaint();
    }

    private JPanel createTabs() {
        JPanel tabs = new JPanel();
        tabs.setLayout(new GridLayout(1, 2));

        JButton joinTabButton = new JButton("JOIN");
        joinTabButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTabAndContent(SharingType.JOIN);
            }
        });

        JButton hostTabButton = new JButton("HOST");
        hostTabButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTabAndContent(SharingType.HOST);
            }
        });
        tabs.add(joinTabButton);
        tabs.add(hostTabButton);

        return tabs;
    }

    private JPanel createJoinTabContent() {
        JPanel joinPanel = new JPanel(new GridLayout(2, 1));
        joinPanel.setBorder(BorderFactory.createEmptyBorder(0, windowWidth/8, 0, windowWidth/8));

        JTextField joinCode;
        if (mainWindow.getGroupJoinCode() == null) {
            joinCode = new JTextField("Join Code");
        } else {
            joinCode = new JTextField(mainWindow.getGroupJoinCode());
        }
        JTextField serverAddress;
        if (mainWindow.getServerAddress() == null) {
            serverAddress = new JTextField("Server Address");
        } else {
            serverAddress = new JTextField(mainWindow.getServerAddress());
        }
        JButton joinButton;

        // Join code text field and join button
        JPanel joinCodeAndButtonWrapper = new JPanel(new BorderLayout());
        joinCodeAndButtonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel joinCodeAndButtonGrid = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 3;
        constraints.insets = new Insets(0, 0, 0, windowWidth/20);
        constraints.fill = GridBagConstraints.BOTH;
        joinCodeAndButtonGrid.add(joinCode, constraints);

        if (mainWindow.getStatus() == Status.OFFLINE) {
            joinButton = new JButton("JOIN");
            joinButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainWindow.joinGroup(joinCode.getText(), serverAddress.getText());
                    setTabAndContent(SharingType.JOIN);
                }
            });

            joinButton.setBackground(Color.GREEN);
            joinButton.setForeground(Color.BLACK);
            joinButton.setBorderPainted(false);
            joinButton.setOpaque(true);

        } else {
            joinButton = new JButton("LEAVE");
            joinButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainWindow.leaveGroup();
                    setTabAndContent(SharingType.JOIN);
                }
            });

            joinButton.setBackground(Color.RED);
            joinButton.setForeground(Color.BLACK);
            joinButton.setBorderPainted(false);
            joinButton.setOpaque(true);
        }

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        joinCodeAndButtonGrid.add(joinButton, constraints);

        joinCodeAndButtonWrapper.add(joinCodeAndButtonGrid, BorderLayout.PAGE_END);

        // Server adress text field
        JPanel serverAddressWrapper = new JPanel(new BorderLayout());
        serverAddressWrapper.add(serverAddress, BorderLayout.PAGE_START);

        joinPanel.add(joinCodeAndButtonWrapper);
        joinPanel.add(serverAddressWrapper);

        return joinPanel;
    }

    private JPanel createHostTabContent() {
        JPanel hostPanel = new JPanel(new GridLayout(2, 1));
        hostPanel.setBorder(BorderFactory.createEmptyBorder(0, windowWidth/8, 0, windowWidth/8));

        JTextField joinCode;
        if (mainWindow.getGroupJoinCode() == null) {
            joinCode = new JTextField("");
        } else {
            joinCode = new JTextField(mainWindow.getGroupJoinCode());
        }
        JTextField serverAddress;
        if (mainWindow.getServerAddress() == null) {
            serverAddress = new JTextField("Server Address");
        } else {
            serverAddress = new JTextField(mainWindow.getServerAddress());
        }

        // Join code text field and join button
        JPanel hostTextAndButtonWrapper = new JPanel(new BorderLayout());
        hostTextAndButtonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel hostJoinCodeAndButtonGrid = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        joinCode.setEditable(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 3;
        constraints.insets = new Insets(0, 0, 0, windowWidth/20);
        constraints.fill = GridBagConstraints.BOTH;
        hostJoinCodeAndButtonGrid.add(joinCode, constraints);

        JButton hostButton;
        if (mainWindow.getStatus() == Status.OFFLINE) {
            hostButton = new JButton("HOST");
            hostButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainWindow.hostGroup(serverAddress.getText());
                    setTabAndContent(SharingType.HOST);
                }
            });

            hostButton.setBackground(Color.GREEN);
            hostButton.setForeground(Color.BLACK);
            hostButton.setBorderPainted(false);
            hostButton.setOpaque(true);

        } else {
            hostButton = new JButton("STOP");
            hostButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainWindow.stopGroup();
                    setTabAndContent(SharingType.HOST);
                }
            });

            hostButton.setBackground(Color.RED);
            hostButton.setForeground(Color.BLACK);
            hostButton.setBorderPainted(false);
            hostButton.setOpaque(true);
        }
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        hostJoinCodeAndButtonGrid.add(hostButton, constraints);

        hostTextAndButtonWrapper.add(hostJoinCodeAndButtonGrid, BorderLayout.PAGE_END);

        // Server adress text field
        JPanel serverAddressWrapper = new JPanel(new BorderLayout());
        serverAddressWrapper.add(serverAddress, BorderLayout.PAGE_START);

        hostPanel.add(hostTextAndButtonWrapper);
        hostPanel.add(serverAddressWrapper);

        return hostPanel;
    }
}
