package com.javaide;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        newFileItem.addActionListener(e -> onNewFile());
        fileMenu.add(newFileItem);
        
        JMenuItem openFileItem = new JMenuItem("Open File", KeyEvent.VK_O);
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openFileItem.addActionListener(e -> onOpenFile());
        fileMenu.add(openFileItem);
        
        JMenuItem saveFileItem = new JMenuItem("Save File", KeyEvent.VK_S);
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveFileItem.addActionListener(e -> onSaveFile());
        fileMenu.add(saveFileItem);
        
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> onSaveAs());
        fileMenu.add(saveAsItem);
        
        fileMenu.addSeparator();
        
        JMenuItem openProjectItem = new JMenuItem("Open Project");
        openProjectItem.addActionListener(e -> onOpenProject());
        fileMenu.add(openProjectItem);
        
        JMenuItem newProjectItem = new JMenuItem("New Project");
        newProjectItem.addActionListener(e -> onNewProject());
        fileMenu.add(newProjectItem);
        
        fileMenu.addSeparator();
        
        JMenuItem quitItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        quitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(quitItem);
        
        menuBar.add(fileMenu);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> onUndo());
        editMenu.add(undoItem);
        
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        redoItem.addActionListener(e -> onRedo());
        editMenu.add(redoItem);
        
        editMenu.addSeparator();
        
        JMenuItem cutItem = new JMenuItem("Cut", KeyEvent.VK_X);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> onCut());
        editMenu.add(cutItem);
        
        JMenuItem copyItem = new JMenuItem("Copy", KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> onCopy());
        editMenu.add(copyItem);
        
        JMenuItem pasteItem = new JMenuItem("Paste", KeyEvent.VK_V);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> onPaste());
        editMenu.add(pasteItem);
        
        menuBar.add(editMenu);
        
        // Build menu
        JMenu buildMenu = new JMenu("Build");
        buildMenu.setMnemonic(KeyEvent.VK_B);
        
        JMenuItem buildItem = new JMenuItem("Build Project");
        buildItem.addActionListener(e -> onBuild());
        buildMenu.add(buildItem);
        
        JMenuItem runItem = new JMenuItem("Run");
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        runItem.addActionListener(e -> onRun());
        buildMenu.add(runItem);
        
        JMenuItem cleanItem = new JMenuItem("Clean");
        cleanItem.addActionListener(e -> onClean());
        buildMenu.add(cleanItem);
        
        menuBar.add(buildMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem toggleFileBrowserItem = new JMenuItem("Toggle File Browser");
        toggleFileBrowserItem.addActionListener(e -> onToggleFileBrowser());
        viewMenu.add(toggleFileBrowserItem);
        
        JMenuItem toggleOutputItem = new JMenuItem("Toggle Output");
        toggleOutputItem.addActionListener(e -> onToggleOutput());
        viewMenu.add(toggleOutputItem);
        
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton newBtn = new JButton(new ImageIcon(getClass().getResource("/icons/new.png")));
        if (newBtn.getIcon() == null) {
            newBtn.setText("New");
        }
        newBtn.setToolTipText("New File");
        newBtn.addActionListener(e -> onNewFile());
        toolbar.add(newBtn);
        
        JButton openBtn = new JButton(new ImageIcon(getClass().getResource("/icons/open.png")));
        if (openBtn.getIcon() == null) {
            openBtn.setText("Open");
        }
        openBtn.setToolTipText("Open File");
        openBtn.addActionListener(e -> onOpenFile());
        toolbar.add(openBtn);
        
        JButton saveBtn = new JButton(new ImageIcon(getClass().getResource("/icons/save.png")));
        if (saveBtn.getIcon() == null) {
            saveBtn.setText("Save");
        }
        saveBtn.setToolTipText("Save File");
        saveBtn.addActionListener(e -> onSaveFile());
        toolbar.add(saveBtn);
        
        toolbar.addSeparator();
        
        JButton buildBtn = new JButton(new ImageIcon(getClass().getResource("/icons/build.png")));
        if (buildBtn.getIcon() == null) {
            buildBtn.setText("Build");
        }
        buildBtn.setToolTipText("Build Project");
        buildBtn.addActionListener(e -> onBuild());
        toolbar.add(buildBtn);
        
        JButton runBtn = new JButton(new ImageIcon(getClass().getResource("/icons/run.png")));
        if (runBtn.getIcon() == null) {
            runBtn.setText("Run");
        }
        runBtn.setToolTipText("Run");
        runBtn.addActionListener(e -> onRun());
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
        Path projectPath = Paths.get(path);
        String projectName = projectPath.getFileName().toString();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileInfo(path, false));
        treeModel.setRoot(root);
        loadDirectory(root, projectPath);
        fileTree.expandPath(new TreePath(root.getPath()));
    }
    
    private void loadDirectory(DefaultMutableTreeNode parent, Path dirPath) {
        try {
            Files.list(dirPath).sorted().forEach(filePath -> {
                if (filePath.getFileName().toString().startsWith(".")) {
                    return;
                }
                
                FileInfo info = new FileInfo(filePath.toString(), Files.isRegularFile(filePath));
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
                parent.add(node);
                
                if (Files.isDirectory(filePath)) {
                    loadDirectory(node, filePath);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void onFileSelected(String filepath) {
        Path filePath = Paths.get(filepath);
        if (Files.isRegularFile(filePath)) {
            try {
                String contents = new String(Files.readAllBytes(filePath));
                openEditorTab(filepath, contents);
            } catch (IOException e) {
                showError("Error opening file: " + e.getMessage());
            }
        }
    }
    
    private void openEditorTab(String filepath, String contents) {
        EditorTab tab = new EditorTab(filepath, contents);
        
        String label = filepath != null ? Paths.get(filepath).getFileName().toString() : "Untitled";
        
        JPanel tabPanel = new JPanel(new BorderLayout());
        JLabel tabLabel = new JLabel(label);
        JButton closeBtn = new JButton("×");
        closeBtn.setBorder(new EmptyBorder(0, 5, 0, 0));
        closeBtn.setContentAreaFilled(false);
        closeBtn.addActionListener(e -> closeTab(tab.scrollPane));
        
        tabPanel.add(tabLabel, BorderLayout.CENTER);
        tabPanel.add(closeBtn, BorderLayout.EAST);
        
        int index = editorNotebook.addTab(label, tab.scrollPane);
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
                String contents = new String(Files.readAllBytes(file.toPath()));
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
                editorNotebook.setTitleAt(index, Paths.get(tab.filepath).getFileName().toString());
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
            Path srcPath = Paths.get(path, "src", "main", "java");
            Files.createDirectories(srcPath);
            
            // Create Main.java
            Files.createDirectories(srcPath.resolve("com/example"));
            String mainJava = "package com.example;\n\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n";
            Files.write(srcPath.resolve("com/example/Main.java"), mainJava.getBytes());
            
            // Create pom.xml for Maven
            String pomXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
                "         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>myproject</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>21</maven.compiler.source>\n" +
                "        <maven.compiler.target>21</maven.compiler.target>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "    </properties>\n" +
                "</project>\n";
            Files.write(Paths.get(path, "pom.xml"), pomXml.getBytes());
            
            // Create README.md
            String readme = "# My Java Project\n\n" +
                "A Java application.\n\n" +
                "## Building\n\n" +
                "```bash\n" +
                "mvn compile\n" +
                "```\n\n" +
                "## Running\n\n" +
                "```bash\n" +
                "mvn exec:java -Dexec.mainClass=\"com.example.Main\"\n" +
                "```\n";
            Files.write(Paths.get(path, "README.md"), readme.getBytes());
            
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
        
        // Check for Maven
        if (Files.exists(Paths.get(currentProjectPath, "pom.xml"))) {
            runCommand(new String[]{"mvn", "compile"}, currentProjectPath);
        } else {
            appendOutput("No build system found. Using javac...\n");
            // Try to compile Java files
            runCommand(new String[]{"javac", "-d", "target/classes", 
                "src/main/java/**/*.java"}, currentProjectPath);
        }
    }
    
    private void onRun() {
        if (currentProjectPath == null) {
            showError("No project open. Please open a project first.");
            return;
        }
        
        appendOutput("Running application...\n");
        
        // Check for Maven
        if (Files.exists(Paths.get(currentProjectPath, "pom.xml"))) {
            runCommand(new String[]{"mvn", "exec:java", 
                "-Dexec.mainClass=com.example.Main"}, currentProjectPath);
        } else {
            // Try to run Main class
            EditorTab tab = getCurrentTab();
            if (tab != null && tab.filepath != null && tab.filepath.endsWith(".java")) {
                String className = Paths.get(tab.filepath).getFileName().toString()
                    .replace(".java", "");
                runCommand(new String[]{"java", className}, 
                    Paths.get(tab.filepath).getParent().toString());
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
        Path targetPath = Paths.get(currentProjectPath, "target");
        if (Files.exists(targetPath)) {
            try {
                deleteDirectory(targetPath.toFile());
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
        SwingUtilities.invokeLater(() -> {
            new JavaIDE().setVisible(true);
        });
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
            Path p = Paths.get(path);
            return isFile ? p.getFileName().toString() : p.getFileName().toString() + "/";
        }
    }
}

