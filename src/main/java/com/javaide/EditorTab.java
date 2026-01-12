package com.javaide;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * EditorTab represents a single editor tab with syntax highlighting
 */
public class EditorTab {
    public String filepath;
    public boolean isModified;
    public JScrollPane scrollPane;
    private JTextPane textPane;
    private StyledDocument document;
    private UndoManager undoManager;
    
    public EditorTab(String filepath, String contents) {
        this.filepath = filepath;
        this.isModified = false;
        
        // Create text pane with syntax highlighting
        textPane = new JTextPane();
        document = textPane.getStyledDocument();
        
        // Configure editor
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        //textPane.setTabSize(4);
        
        // Set up undo manager
        undoManager = new UndoManager();
        document.addUndoableEditListener(undoManager);
        
        // Add document listener for modification tracking
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified = true;
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified = true;
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                isModified = true;
            }
        });
        
        // Set text
        setText(contents);
        
        // Apply syntax highlighting if filepath is set
        if (filepath != null) {
            applySyntaxHighlighting();
        }
        
        // Create scrolled pane
        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    private void setText(String text) {
        try {
            document.remove(0, document.getLength());
            document.insertString(0, text, null);
            isModified = false;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public String getText() {
        try {
            return document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }
    
    public boolean save() {
        if (filepath == null) {
            return false;
        }
        
        FileOutputStream fos = null;
        try {
            File file = new File(filepath);
            fos = new FileOutputStream(file);
            fos.write(getText().getBytes("UTF-8"));
            isModified = false;
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Error saving file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
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
    
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
    
    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    
    public void cut() {
        textPane.cut();
    }
    
    public void copy() {
        textPane.copy();
    }
    
    public void paste() {
        textPane.paste();
    }
    
    private void applySyntaxHighlighting() {
        if (filepath == null) {
            return;
        }
        
        String ext = filepath.substring(filepath.lastIndexOf('.') + 1).toLowerCase();
        
        // Simple Java syntax highlighting
        if (ext.equals("java")) {
            highlightJava();
        }
    }
    
    private void highlightJava() {
        try {
            String text = getText();
            StyleContext styleContext = StyleContext.getDefaultStyleContext();
            AttributeSet defaultStyle = styleContext.addAttribute(
                styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
            
            // Define styles
            AttributeSet keywordStyle = styleContext.addAttribute(defaultStyle,
                StyleConstants.Foreground, new Color(0, 0, 255)); // Blue
            AttributeSet stringStyle = styleContext.addAttribute(defaultStyle,
                StyleConstants.Foreground, new Color(0, 128, 0)); // Green
            AttributeSet commentStyle = styleContext.addAttribute(defaultStyle,
                StyleConstants.Foreground, new Color(128, 128, 128)); // Gray
            
            // Java keywords
            String[] keywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for",
                "goto", "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super", "switch",
                "synchronized", "this", "throw", "throws", "transient", "try",
                "void", "volatile", "while", "record", "sealed", "permits", "non-sealed"
            };
            
            // Reset all text to default style
            document.setCharacterAttributes(0, text.length(), defaultStyle, true);
            
            // Highlight keywords
            for (String keyword : keywords) {
                int pos = 0;
                while ((pos = text.indexOf(keyword, pos)) >= 0) {
                    // Check if it's a whole word
                    if ((pos == 0 || !Character.isJavaIdentifierPart(text.charAt(pos - 1))) &&
                        (pos + keyword.length() >= text.length() || 
                         !Character.isJavaIdentifierPart(text.charAt(pos + keyword.length())))) {
                        document.setCharacterAttributes(pos, keyword.length(), keywordStyle, false);
                    }
                    pos += keyword.length();
                }
            }
            
            // Highlight strings
            highlightStrings(text, stringStyle);
            
            // Highlight comments
            highlightComments(text, commentStyle);
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void highlightStrings(String text, AttributeSet style) throws BadLocationException {
        boolean inString = false;
        boolean inChar = false;
        char stringChar = '"';
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            if (!inString && !inChar && (c == '"' || c == '\'')) {
                if (c == '"') {
                    inString = true;
                    stringChar = '"';
                } else {
                    inChar = true;
                    stringChar = '\'';
                }
                int start = i;
                i++;
                while (i < text.length()) {
                    if (text.charAt(i) == stringChar && text.charAt(i - 1) != '\\') {
                        document.setCharacterAttributes(start, i - start + 1, style, false);
                        if (inString) inString = false;
                        if (inChar) inChar = false;
                        break;
                    }
                    i++;
                }
            }
        }
    }
    
    private void highlightComments(String text, AttributeSet style) throws BadLocationException {
        // Single-line comments
        int pos = 0;
        while ((pos = text.indexOf("//", pos)) >= 0) {
            int end = text.indexOf("\n", pos);
            if (end < 0) end = text.length();
            document.setCharacterAttributes(pos, end - pos, style, false);
            pos = end;
        }
        
        // Multi-line comments
        pos = 0;
        while ((pos = text.indexOf("/*", pos)) >= 0) {
            int end = text.indexOf("*/", pos);
            if (end < 0) end = text.length();
            else end += 2;
            document.setCharacterAttributes(pos, end - pos, style, false);
            pos = end;
        }
    }
}

