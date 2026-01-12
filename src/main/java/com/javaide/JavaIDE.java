package com.javaide;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaIDE - A Java IDE built with Swing
 * 
 * Features:
 * - Code Editor with syntax highlighting
 * - File Browser
 * - Build and Run support
 * - Project Management
 */
public class JavaIDE extends JFrame {
    private String currentProjectPath;
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTabbedPane editorNotebook;
    private JTextArea outputView;
    private Map<JScrollPane, EditorTab> tabMap;
    
    public JavaIDE() {
        super("JavaIDE - Java Development Environment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        tabMap = new HashMap<>();
        
        createMenuBar();
        createToolbar();
        createMainPanel();
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newFileItem = new JMenuItem("New File", KeyEvent.VK_N);
        newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewFile();
            }
        });
        fileMenu.add(newFileItem);
        
        JMenuItem openFileItem = new JMenuItem("Open File", KeyEvent.VK_O);
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenFile();
            }
        });
        fileMenu.add(openFileItem);
        
        JMenuItem saveFileItem = new JMenuItem("Save File", KeyEvent.VK_S);
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveFile();
            }
        });
        fileMenu.add(saveFileItem);
        
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveAs();
            }
        });
        fileMenu.add(saveAsItem);
        
        fileMenu.addSeparator();
        
        JMenuItem openProjectItem = new JMenuItem("Open Project");
        openProjectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenProject();
            }
        });
        fileMenu.add(openProjectItem);
        
        JMenuItem newProjectItem = new JMenuItem("New Project");
        newProjectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewProject();
            }
        });
        fileMenu.add(newProjectItem);
        
        fileMenu.addSeparator();
        
        JMenuItem quitItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(quitItem);
        
        menuBar.add(fileMenu);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUndo();
            }
        });
        editMenu.add(undoItem);
        
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRedo();
            }
        });
        editMenu.add(redoItem);
        
        editMenu.addSeparator();
        
        JMenuItem cutItem = new JMenuItem("Cut", KeyEvent.VK_X);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCut();
            }
        });
        editMenu.add(cutItem);
        
        JMenuItem copyItem = new JMenuItem("Copy", KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCopy();
            }
        });
        editMenu.add(copyItem);
        
        JMenuItem pasteItem = new JMenuItem("Paste", KeyEvent.VK_V);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPaste();
            }
        });
        editMenu.add(pasteItem);
        
        menuBar.add(editMenu);
        
        // Build menu
        JMenu buildMenu = new JMenu("Build");
        buildMenu.setMnemonic(KeyEvent.VK_B);
        
        JMenuItem buildItem = new JMenuItem("Build Project");
        buildItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBuild();
            }
        });
        buildMenu.add(buildItem);
        
        JMenuItem runItem = new JMenuItem("Run");
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        runItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRun();
            }
        });
        buildMenu.add(runItem);
        
        JMenuItem cleanItem = new JMenuItem("Clean");
        cleanItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClean();
            }
        });
        buildMenu.add(cleanItem);
        
        menuBar.add(buildMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem toggleFileBrowserItem = new JMenuItem("Toggle File Browser");
        toggleFileBrowserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onToggleFileBrowser();
            }
        });
        viewMenu.add(toggleFileBrowserItem);
        
        JMenuItem toggleOutputItem = new JMenuItem("Toggle Output");
        toggleOutputItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onToggleOutput();
            }
        });
        viewMenu.add(toggleOutputItem);
        
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton newBtn = new JButton();
        if (newBtn.getIcon() == null) {
            newBtn.setText("New");
        }
        newBtn.setToolTipText("New File");
        newBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewFile();
            }
        });
        toolbar.add(newBtn);
        
        JButton openBtn = new JButton();
        if (openBtn.getIcon() == null) {
            openBtn.setText("Open");
        }
        openBtn.setToolTipText("Open File");
        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenFile();
            }
        });
        toolbar.add(openBtn);
        
        JButton saveBtn = new JButton();
        if (saveBtn.getIcon() == null) {
            saveBtn.setText("Save");
        }
        saveBtn.setToolTipText("Save File");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveFile();
            }
        });
        toolbar.add(saveBtn);
        
        toolbar.addSeparator();
        
        JButton buildBtn = new JButton();
        if (buildBtn.getIcon() == null) {
            buildBtn.setText("Build");
        }
        buildBtn.setToolTipText("Build Project");
        buildBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBuild();
            }
        });
        toolbar.add(buildBtn);
        
        JButton runBtn = new JButton();
        runBtn.setIcon(createPlayIcon());
        runBtn.setToolTipText("Run");
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRun();
            }
        });
        toolbar.add(runBtn);
        
        add(toolbar, BorderLayout.NORTH);
    }
    
    private void createMainPanel() {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setResizeWeight(0.0);
        
        // File browser
        createFileBrowser();
        JScrollPane fileTreeScroll = new JScrollPane(fileTree);
        fileTreeScroll.setPreferredSize(new Dimension(250, 0));
        mainSplitPane.setLeftComponent(fileTreeScroll);
        
        // Editor and output
        JSplitPane editorSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        editorSplitPane.setDividerLocation(500);
        editorSplitPane.setResizeWeight(1.0);
        
        createEditor();
        editorSplitPane.setTopComponent(editorNotebook);
        
        createOutputPanel();
        editorSplitPane.setBottomComponent(new JScrollPane(outputView));
        
        mainSplitPane.setRightComponent(editorSplitPane);
        
        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void createFileBrowser() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("No Project");
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.setRootVisible(true);
        fileTree.setShowsRootHandles(true);
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.getUserObject() instanceof FileInfo) {
                        FileInfo info = (FileInfo) node.getUserObject();
                        if (info.isFile()) {
                            onFileSelected(info.getPath());
                        }
                    }
                }
            }
        });
    }
    
    private void createEditor() {
        editorNotebook = new JTabbedPane();
        editorNotebook.setTabPlacement(JTabbedPane.TOP);
    }
    
    private void createOutputPanel() {
        outputView = new JTextArea();
        outputView.setEditable(false);
        outputView.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputView.setBackground(Color.BLACK);
        outputView.setForeground(Color.GREEN);
    }
    
    private void loadProjectTree(String path) {
        File projectPath = new File(path);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileInfo(path, false));
        treeModel.setRoot(root);
        loadDirectory(root, projectPath);
        fileTree.expandPath(new TreePath(root.getPath()));
    }
    
    private void loadDirectory(DefaultMutableTreeNode parent, File dirPath) {
        try {
            File[] files = dirPath.listFiles();
            if (files == null) {
                return;
            }
            
            // Sort files
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });
            
            for (File file : files) {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                
                FileInfo info = new FileInfo(file.getAbsolutePath(), file.isFile());
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
                parent.add(node);
                
                if (file.isDirectory()) {
                    loadDirectory(node, file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void onFileSelected(String filepath) {
        File filePath = new File(filepath);
        if (filePath.isFile()) {
            try {
                String contents = readFile(filePath);
                openEditorTab(filepath, contents);
            } catch (IOException e) {
                showError("Error opening file: " + e.getMessage());
            }
        }
    }
    
    private void openEditorTab(String filepath, String contents) {
        EditorTab tab = new EditorTab(filepath, contents);
        
        String label = filepath != null ? new File(filepath).getName() : "Untitled";
        
        JPanel tabPanel = new JPanel(new BorderLayout());
        JLabel tabLabel = new JLabel(label);
        JButton closeBtn = new JButton("×");
        closeBtn.setBorder(new EmptyBorder(0, 5, 0, 0));
        closeBtn.setContentAreaFilled(false);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeTab(tab.scrollPane);
            }
        });
        
        tabPanel.add(tabLabel, BorderLayout.CENTER);
        tabPanel.add(closeBtn, BorderLayout.EAST);
        
        editorNotebook.addTab(label, tab.scrollPane);
        int index = editorNotebook.getTabCount() - 1;
        editorNotebook.setTabComponentAt(index, tabPanel);
        editorNotebook.setSelectedIndex(index);
        
        tabMap.put(tab.scrollPane, tab);
    }
    
    private void closeTab(JScrollPane scrollPane) {
        int index = editorNotebook.indexOfComponent(scrollPane);
        if (index >= 0) {
            editorNotebook.removeTabAt(index);
            tabMap.remove(scrollPane);
        }
    }
    
    private EditorTab getCurrentTab() {
        int index = editorNotebook.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        JScrollPane scrollPane = (JScrollPane) editorNotebook.getComponentAt(index);
        return tabMap.get(scrollPane);
    }
    
    private void appendOutput(String text) {
        outputView.append(text);
        outputView.setCaretPosition(outputView.getDocument().getLength());
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String readFile(File file) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    private void writeFile(File file, String content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes("UTF-8"));
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    private void runCommand(String[] command, String workingDir) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            if (workingDir != null) {
                pb.directory(new File(workingDir));
            }
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                appendOutput(line + "\n");
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                appendOutput("Command exited with status: " + exitCode + "\n");
            }
        } catch (Exception e) {
            appendOutput("Error running command: " + e.getMessage() + "\n");
        }
    }
    
    // Menu actions
    private void onNewFile() {
        openEditorTab(null, "");
    }
    
    private void onOpenFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Source files (*.java, *.c, *.cpp, *.h, *.json, *.xml, *.md)", 
            "java", "c", "cpp", "h", "hpp", "json", "xml", "md");
        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("All files", "*"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String contents = readFile(file);
                openEditorTab(file.getAbsolutePath(), contents);
            } catch (IOException e) {
                showError("Error opening file: " + e.getMessage());
            }
        }
    }
    
    private void onSaveFile() {
        EditorTab tab = getCurrentTab();
        if (tab == null) {
            return;
        }
        if (tab.filepath == null) {
            onSaveAs();
        } else {
            tab.save();
        }
    }
    
    private void onSaveAs() {
        EditorTab tab = getCurrentTab();
        if (tab == null) {
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        if (tab.filepath != null) {
            chooser.setSelectedFile(new File(tab.filepath));
        }
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            tab.filepath = chooser.getSelectedFile().getAbsolutePath();
            tab.save();
            // Update tab title
            int index = editorNotebook.getSelectedIndex();
            if (index >= 0) {
                editorNotebook.setTitleAt(index, new File(tab.filepath).getName());
            }
        }
    }
    
    private void onOpenProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentProjectPath = chooser.getSelectedFile().getAbsolutePath();
            loadProjectTree(currentProjectPath);
        }
    }
    
    private void onNewProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File projectDir = chooser.getSelectedFile();
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }
            
            createNewProject(projectDir.getAbsolutePath());
            currentProjectPath = projectDir.getAbsolutePath();
            loadProjectTree(currentProjectPath);
        }
    }
    
    private void createNewProject(String path) {
        try {
            // Create src/main/java structure
            File srcPath = new File(path, "src/main/java");
            srcPath.mkdirs();
            
            // Create Main.java
            File exampleDir = new File(srcPath, "com/example");
            exampleDir.mkdirs();
            String mainJava = "package com.example;\n\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n";
            writeFile(new File(exampleDir, "Main.java"), mainJava);
            
            // Create build.xml for Ant
            String buildXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project name=\"myproject\" default=\"compile\" basedir=\".\">\n" +
                "    <property name=\"src.dir\" value=\"src/main/java\"/>\n" +
                "    <property name=\"build.dir\" value=\"build\"/>\n" +
                "    <property name=\"classes.dir\" value=\"${build.dir}/classes\"/>\n" +
                "    <property name=\"main-class\" value=\"com.example.Main\"/>\n" +
                "    \n" +
                "    <target name=\"clean\">\n" +
                "        <delete dir=\"${build.dir}\"/>\n" +
                "    </target>\n" +
                "    \n" +
                "    <target name=\"compile\" depends=\"clean\">\n" +
                "        <mkdir dir=\"${classes.dir}\"/>\n" +
                "        <javac srcdir=\"${src.dir}\" destdir=\"${classes.dir}\" \n" +
                "               source=\"1.6\" target=\"1.6\" encoding=\"UTF-8\" includeantruntime=\"false\"/>\n" +
                "    </target>\n" +
                "    \n" +
                "    <target name=\"run\" depends=\"compile\">\n" +
                "        <java classname=\"${main-class}\" classpath=\"${classes.dir}\" fork=\"true\"/>\n" +
                "    </target>\n" +
                "</project>\n";
            writeFile(new File(path, "build.xml"), buildXml);
            
            // Create README.md
            String readme = "# My Java Project\n\n" +
                "A Java application.\n\n" +
                "## Building\n\n" +
                "```bash\n" +
                "ant compile\n" +
                "```\n\n" +
                "## Running\n\n" +
                "```bash\n" +
                "ant run\n" +
                "```\n";
            writeFile(new File(path, "README.md"), readme);
            
            showInfo("Project created successfully.");
        } catch (IOException e) {
            showError("Error creating project: " + e.getMessage());
        }
    }
    
    private void onUndo() {
        EditorTab tab = getCurrentTab();
        if (tab != null) {
            tab.undo();
        }
    }
    
    private void onRedo() {
        EditorTab tab = getCurrentTab();
        if (tab != null) {
            tab.redo();
        }
    }
    
    private void onCut() {
        EditorTab tab = getCurrentTab();
        if (tab != null) {
            tab.cut();
        }
    }
    
    private void onCopy() {
        EditorTab tab = getCurrentTab();
        if (tab != null) {
            tab.copy();
        }
    }
    
    private void onPaste() {
        EditorTab tab = getCurrentTab();
        if (tab != null) {
            tab.paste();
        }
    }
    
    private void onBuild() {
        if (currentProjectPath == null) {
            showError("No project open. Please open a project first.");
            return;
        }
        
        appendOutput("Building project...\n");
        
        // Check for Ant
        if (new File(currentProjectPath, "build.xml").exists()) {
            runCommand(new String[]{"ant", "compile"}, currentProjectPath);
        } else {
            appendOutput("No build system found. Using javac...\n");
            // Try to compile Java files
            runCommand(new String[]{"javac", "-d", "build/classes", 
                "src/main/java/**/*.java"}, currentProjectPath);
        }
    }
    
    private void onRun() {
        if (currentProjectPath == null) {
            showError("No project open. Please open a project first.");
            return;
        }
        
        appendOutput("Running application...\n");
        
        // Check for Ant
        if (new File(currentProjectPath, "build.xml").exists()) {
            runCommand(new String[]{"ant", "run"}, currentProjectPath);
        } else {
            // Try to run Main class
            EditorTab tab = getCurrentTab();
            if (tab != null && tab.filepath != null && tab.filepath.endsWith(".java")) {
                File file = new File(tab.filepath);
                String className = file.getName().replace(".java", "");
                runCommand(new String[]{"java", className}, 
                    file.getParent());
            } else {
                showError("No main class found. Please open a Java file with a main method.");
            }
        }
    }
    
    private void onClean() {
        if (currentProjectPath == null) {
            showError("No project open. Please open a project first.");
            return;
        }
        
        appendOutput("Cleaning build artifacts...\n");
        
        // Remove target directory
        File targetPath = new File(currentProjectPath, "target");
        if (targetPath.exists()) {
            try {
                deleteDirectory(targetPath);
                appendOutput("Removed target/\n");
            } catch (IOException e) {
                appendOutput("Error removing target/: " + e.getMessage() + "\n");
            }
        }
        
        appendOutput("Clean complete.\n");
    }
    
    private void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    private void onToggleFileBrowser() {
        // Implementation for toggling file browser visibility
        // This would require storing a reference to the split pane
    }
    
    private void onToggleOutput() {
        // Implementation for toggling output panel visibility
        // This would require storing a reference to the split pane
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JavaIDE().setVisible(true);
            }
        });
    }
    
    /**
     * Creates a play icon (triangle pointing right) for the run button
     */
    private Icon createPlayIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw play triangle
                int size = getIconHeight();
                int[] xPoints = {x + 2, x + 2, x + size - 2};
                int[] yPoints = {y + 2, y + size - 2, y + size / 2};
                
                g2d.setColor(c.isEnabled() ? Color.BLACK : Color.GRAY);
                g2d.fillPolygon(xPoints, yPoints, 3);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 16;
            }
            
            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }
    
    // Helper class for file tree
    private static class FileInfo {
        private String path;
        private boolean isFile;
        
        public FileInfo(String path, boolean isFile) {
            this.path = path;
            this.isFile = isFile;
        }
        
        public String getPath() {
            return path;
        }
        
        public boolean isFile() {
            return isFile;
        }
        
        @Override
        public String toString() {
            File f = new File(path);
            return isFile ? f.getName() : f.getName() + "/";
        }
    }
}

