package net.darktree;

import net.darktree.urp.NetUtils;
import net.darktree.urp.R2UMessage;
import net.darktree.urp.URPClient;
import net.darktree.urp.URPClientHelper;
import net.darktree.urp.u2rmessage.U2RJoin;
import net.darktree.urp.u2rmessage.U2RMake;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MainWindow extends JFrame {

    private KeywordStyledDocument currentDocument = null;
    private final Map<String, KeywordStyledDocument> openDocuments = new HashMap<>();

    private final Style defaultStyle;
    private final Style highlightStyle;
    private final Font defaultFont;

    private final JPanel codePanelWrapper;

    private final int windowWidth = 1200;
    private final int windowHeight = 800;

    private Status status = Status.OFFLINE;


    private String serverAddress = "localhost";
    private String groupJoinCode;

    private URPClient client = null;
    private Typewriter typewriter = null;

    private void registerLocalFont(String path) {
        try {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            Objects.requireNonNull(stream);
            environment.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
        } catch(FontFormatException | IOException e){
            e.printStackTrace();
        }
    }

    public MainWindow() {
        // default font
        registerLocalFont("fonts/JetBrainsMono-Regular.ttf");
        defaultFont = new Font("JetBrains Mono Regular", Font.PLAIN, 13);

        // default text style
        StyleContext styleContext = new StyleContext();
        defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setLineSpacing(defaultStyle, 1.2f);

        // style of highlighted words
        highlightStyle = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(highlightStyle, new Color(0x729fcf));
        StyleConstants.setLineSpacing(highlightStyle, 1.2f);

        setTitle("Ratify | Untitled Document");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(windowWidth, windowHeight));

        codePanelWrapper = new JPanel(new BorderLayout());
        add(codePanelWrapper, BorderLayout.CENTER);

        // setting the save(ctrl+s) action
        KeyStroke saveKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKeyStroke, "saveCurrentFile");
        getRootPane().getActionMap().put("saveCurrentFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentFile();
            }
        });

        addMenuBar();

        pack();
        setLocationRelativeTo(null);
    }

    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    public Status getStatus() {
        return status;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getGroupJoinCode() {
        return groupJoinCode;
    }

    public void setGroupJoinCode(String groupJoinCode) {
        this.groupJoinCode = groupJoinCode;
    }

    public void joinGroup(String joinCode, String serverAddress) {
        if (typewriter != null) {
            typewriter.close();
        }
        if (client != null) {
            client.close();
        }
        client = new URPClient(serverAddress);
        client.waitForConnection(5000);

        if (client.isConnected()) {
            client.getTxBuffer().send(new U2RJoin(Integer.parseInt(joinCode), 0), false);
            while (true) {
                R2UMessage message = client.getRxBuffer().receive(true);
                if (message.getType() == R2UMessage.R2U.MADE) {
                    if (message.getData()[4] == URPClientHelper.JOIN_SUCCESS) {
                        this.currentDocument.clear();
                        this.typewriter = new Typewriter(client, (offset, str) -> {
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    currentDocument.remoteInsert(offset, str);
                                } catch (BadLocationException e) {
                                    //e.printStackTrace();
                                }
                                System.out.println("Other: Offset: " + offset + " Text: " + str);
                            });
                        });

                        this.currentDocument.setOnTypedCallback((offset, str) -> {
                            typewriter.write(offset, str);
                            System.out.println("Me: Offset: " + offset + " Text: " + str);
                        });

                        typewriter.listen();

                        setStatus(Status.IN_GROUP);
                        setGroupJoinCode(joinCode);
                        setServerAddress(serverAddress);
                    }
                    break;
                }
            }
        }
    }

    public void leaveGroup() {
        if (typewriter != null) {
            typewriter.close();
            typewriter = null;
        }
        if (client != null) {
            client.close();
            client = null;
        }

        setStatus(Status.OFFLINE);
        groupJoinCode = null;
        serverAddress = null;
    }

    public void hostGroup(String serverAddress) {
        if (typewriter != null) {
            typewriter.close();
        }
        if (client != null) {
            client.close();
        }
        client = new URPClient(serverAddress);
        client.waitForConnection(5000);

        if (client.isConnected()) {
            client.getTxBuffer().send(new U2RMake(), false);
            while (true){
                R2UMessage message = client.getRxBuffer().receive(true);
                if (message.getType() == R2UMessage.R2U.MADE) {
                    if (message.getData()[4] == URPClientHelper.MAKE_SUCCESS) {
                        this.typewriter = new Host(client, (offset, str) -> {
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    currentDocument.remoteInsert(offset, str);
                                } catch (BadLocationException e) {
                                    //e.printStackTrace();
                                }
                                System.out.println("Other: Offset: " + offset + " Text: " + str);
                            });
                        }, () -> {
                            try {
                                return currentDocument.getText(0, currentDocument.getLength());
                            } catch (BadLocationException e) {
                                return "";
                            }
                        });

                        this.currentDocument.setOnTypedCallback((offset, str) -> {
                            typewriter.write(offset, str);
                            System.out.println("Me: Offset: " + offset + " Text: " + str);
                        });

                        typewriter.listen();

                        setGroupJoinCode(String.valueOf(NetUtils.readIntLE(message.getData(), 0)));
                        setServerAddress(serverAddress);
                        setStatus(Status.HOST);
                    }
                    break;
                }
            }
        }
    }

    public void stopGroup() {
        if (typewriter != null) {
            typewriter.close();
            typewriter = null;
        }
        if (client != null) {
            client.close();
            client = null;
        }

        setStatus(Status.OFFLINE);
        groupJoinCode = null;
        serverAddress = null;
    }

    public void openFile(String path) throws IOException {
        if (openDocuments.containsKey(path)) {
            currentDocument = openDocuments.get(path);
        } else {
            currentDocument = new KeywordStyledDocument(defaultFont, defaultStyle, highlightStyle, path);
            openDocuments.put(path, currentDocument);
        }

        codePanelWrapper.removeAll();
        codePanelWrapper.add(currentDocument.getPanel(), BorderLayout.CENTER);

        setTitle("Ratify | " + currentDocument.getFileName());

        codePanelWrapper.revalidate();
        codePanelWrapper.repaint();
    }

    private void saveCurrentFile() {
        try {
            currentDocument.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseAndOpenFile() {
        Optional<String> pathOpt = chooseFileAndGetLocalPath();
        if (pathOpt.isEmpty()) {
            return;
        }

        String path = pathOpt.get();
        try {
            openFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> chooseFileAndGetLocalPath() {
        FileDialog dialog = new FileDialog((Frame)null, "Select file to open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);

        String path = dialog.getDirectory() + dialog.getFile();
        dialog.dispose();

        if (dialog.getFile() == null) {
            return Optional.empty();
        }

        // This line changes global path to a local project path. It is needed because the need for paths being
        // uniform, as they are used as keys for files
        return Optional.of(path.substring(System.getProperty("user.dir").length() + 1));
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new DefaultMenuLayout(menuBar, BoxLayout.X_AXIS));

        JMenu file = new JMenu("File");
        JMenu sharing = new JMenu("Sharing");

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(e -> chooseAndOpenFile());

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(e -> saveCurrentFile());

        JMenuItem hostMenuItem = new JMenuItem("Host");
        MainWindow mainWindow = this;
        hostMenuItem.addActionListener(e -> EventQueue.invokeLater(() -> new SharingPopup(SharingPopup.SharingType.HOST, mainWindow)));

        JMenuItem joinMenuItem = new JMenuItem("Join");
        joinMenuItem.addActionListener(e -> EventQueue.invokeLater(() -> new SharingPopup(SharingPopup.SharingType.JOIN, mainWindow)));

        file.add(openMenuItem);
        file.add(saveMenuItem);
        sharing.add(hostMenuItem);
        sharing.add(joinMenuItem);

        menuBar.add(file);
        menuBar.add(sharing);
        setJMenuBar(menuBar);
    }
}
