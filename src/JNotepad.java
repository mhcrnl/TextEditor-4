import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class JNotepad extends JFrame implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int count = 1;
    JMenuBar bar = new JMenuBar();
    JTextArea jta = new JTextArea();
    JLabel wordCount = new JLabel("");
    Font defFont;
    UndoManager undoRedoMan = new UndoManager();
    boolean saved = false;
    File currentFile;
    JCheckBoxMenuItem wordWrap;
    JFontChooser fontChooser;
    File viewHelp;

    JButton reCloseButton = new JButton();
    JCheckBox remCheckBox = new JCheckBox();
    JTextField refindField = new JTextField();
    boolean resensitive = false;

    //Need to set global
    //used for find functionality
    JDialog jd;
    static JDialog re;
    JTextField findField;
    JTextField replaceField;
    JCheckBox mCheckBox;
    JButton closeButton;
    JButton findNextButton;
    JButton replaceButton;
    JButton findButton;
    boolean sensitive;
    int findIdx;
    //highlights the find word(s)
    Object lastHL;
    Highlighter hl = jta.getHighlighter();
    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY);


    //used for redo and undo functionality
    JMenuItem undo;
    JMenuItem redo;

    JNotepad() {

        setTitle("Notepad" + count);
        setLayout(new BorderLayout());
        add(wordCount, BorderLayout.SOUTH);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Are you sure to close this window?", "Really Closing?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        setIconImage(new ImageIcon("JNotepad.png").getImage());
        setLocationRelativeTo(null);

        /*---------------------------------
            FILE MENU
        ----------------------------------*/
        JMenu file = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem newTab = new JMenuItem("New Tab");
        //createMenuItem("New Tab",KeyEvent.VK_N,fileMenu,KeyEvent.VK_N,this);
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem saveAsFile = new JMenuItem("Save As");
        //JMenuItem pageSetup = new JMenuItem("Page Setup");
        JMenuItem print = new JMenuItem("Print");
        JMenuItem exit = new JMenuItem("Exit");
        
        file.setMnemonic('F');
        newFile.setMnemonic('N');
        // pageSetup.setMnemonic('u');
        exit.setMnemonic('x');

        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        newFile.addActionListener(this);
        newTab.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        saveAsFile.addActionListener(this);
        print.addActionListener(this);
        //pageSetup.addActionListener(this);
        exit.addActionListener(this);


        file.add(newFile);
        file.add(newTab);
        file.add(openFile);
        file.add(saveFile);
        file.add(saveAsFile);
        file.addSeparator();
        //file.add(pageSetup);
        file.add(print);
        file.addSeparator();
        file.add(exit);

        /*---------------------------------
            EDIT MENU
        ----------------------------------*/
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic('E');
        undo = new JMenuItem("Undo");
        redo = new JMenuItem("Redo");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem find = new JMenuItem("Find");
        JMenuItem findNext = new JMenuItem("Find Next");
        JMenuItem replace = new JMenuItem("Replace");
        JMenuItem gotoEdit = new JMenuItem("Go To");
        JMenuItem selectAll = new JMenuItem("Select All");
        JMenuItem timeDate = new JMenuItem("Time/Date");

        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        //delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        gotoEdit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        undo.setEnabled(false);
        redo.setEnabled(false);

        undo.addActionListener(this);
        redo.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        //delete.addActionListener(this);
        find.addActionListener(this);
        findNext.addActionListener(this);
        replace.addActionListener(this);
        gotoEdit.addActionListener(this);
        selectAll.addActionListener(this);
        timeDate.addActionListener(this);

        edit.add(undo);
        edit.add(redo);
        edit.addSeparator();
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.addSeparator();
        edit.add(find);
        edit.add(findNext);
        edit.add(replace);
        edit.add(gotoEdit);
        edit.addSeparator();
        edit.add(selectAll);
        edit.add(timeDate);

        /*---------------------------------
            FORMAT MENU
        ----------------------------------*/

        JMenu format = new JMenu("Format");
        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        JMenuItem font = new JMenuItem("Font");
        JMenuItem foreground = new JMenuItem("Foreground Color");
        JMenuItem background = new JMenuItem("Background Color");
        format.setMnemonic('o');
        wordWrap.setMnemonic('W');
        font.setMnemonic('F');
        wordWrap.addActionListener(this);
        font.addActionListener(this);
        foreground.addActionListener(this);
        background.addActionListener(this);
        format.add(wordWrap);
        format.add(font);
        format.add(foreground);
        format.add(background);

        /*---------------------------------
            VIEW MENU
        ----------------------------------*/
        JMenu view = new JMenu("View");
        JCheckBoxMenuItem LineCountBar = new JCheckBoxMenuItem("Line Count", true);
        view.setMnemonic('V');
        //LineCountBar.setMnemonic('S');
        //LineCountBar.addActionListener(this);
        view.add(LineCountBar);
        LineCountBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ev.getSource();
                wordCount.setVisible(temp.isSelected());
            }
        });

        /*---------------------------------
            HELP MENU
        ----------------------------------*/
        JMenu help = new JMenu("Help");
        JMenuItem viewHelp = new JMenuItem("View Help");
        JMenuItem about = new JMenuItem("About Notepad");
        help.setMnemonic('H');
        viewHelp.setMnemonic('H');
        viewHelp.addActionListener(this);
        about.addActionListener(this);
        view.add(LineCountBar);
        help.add(viewHelp);
        help.add(about);

        /*---------------------------------
            ADD MENUS TO MENU BAR
        ----------------------------------*/

        bar.add(file);
        bar.add(edit);
        bar.add(format);
        bar.add(view);
        bar.add(help);

        /*---------------------------------
            DEFAULT TEXT AREA
        ----------------------------------*/
        fontChooser = new JFontChooser(this);
        jta.setFont(new Font("Courier", Font.PLAIN, 12));
        jta.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(jta);
        jta.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent ce) {
                String str = jta.getText();
                findIdx = jta.getCaretPosition();
                int lineNumber = 0, column = 0, pos = 0;
                try {
                    pos = jta.getCaretPosition();
                    lineNumber = jta.getLineOfOffset(pos);
                    column = pos - jta.getLineStartOffset(lineNumber);
                } catch (Exception excp) {}
                if (jta.getText().length() == 0) {
                    lineNumber = 0;
                    column = 0;
                }
                wordCount.setText("||       Ln " + (lineNumber + 1) + ", Col " + (column + 1));
            }
        });
        JLabel test = new JLabel("test");
        /*---------------------------------
            UNDO MANAGER FOR TEXT AREA
        ----------------------------------*/

        jta.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoRedoMan.addEdit(e.getEdit());
                UndoRedo();
            }
        });
        setJMenuBar(bar);
        this.add(scrollPane);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New":
                newFile();
                break;
            case "New Tab":
                newTab();
                break;
            case "Open":
                openFile();
                break;
            case "Save":
                save();
                break;
            case "Save As":
                saveAs();
                break;
                // case "Page Setup":
                //    print();
                //   break;
            case "Print":
                print();
                break;
            case "Exit":
                newFile();
                //this.dispose();
                break;
            case "Undo":
                undoRedoMan.undo();
                UndoRedo();
                break;
            case "Redo":
                undoRedoMan.redo();
                UndoRedo();
                break;
            case "Cut":
                jta.cut();
                break;
            case "Copy":
                jta.copy();
                break;
            case "Paste":
                jta.paste();
                break;
                //case "Delete":
                //   jta.setText(jta.getText().replace(jta.getSelectedText(),""));
                // break;
            case "Find":
                find();
                break;
            case "Find Next":
                find();
                break;
            case "Go To":
                goTo();
                break;
            case "Replace":
                replace();
                break;
            case "Select All":
                jta.selectAll();
                break;
            case "Time/Date":
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a M/d/yyyy");
                jta.append(dateFormat.format(new Date(e.getWhen())));
                break;
            case "Word Wrap":
                if (wordWrap.getState())
                    jta.setLineWrap(true);
                else
                    jta.setLineWrap(false);
                break;
            case "Font":
                fontChooser.setVisible(true);
                if (fontChooser.isNewFont) {
                    jta.setFont(fontChooser.getNewFont());
                }
                break;
            case "Foreground Color":
                Color c = JColorChooser.showDialog(null, "Choose a color", jta.getForeground());
                //JColorChooser.sh
                //Color c = new Color(255,0,0);
                if (c != null)
                    jta.setForeground(c);
                break;
                // case "Line Count":
                //	showLineCount();
                //   break;
            case "Background Color":
                Color c1 = JColorChooser.showDialog(null, "Choose a color", jta.getForeground());
                if (c1 != null)
                    jta.setBackground(c1);
                break;
            case "View Help":
                viewHelp = new File("/Users/danishs/Desktop/help.txt");
                //this will open the file if it exist
                if (viewHelp.exists()) {
                    try {
                        Desktop.getDesktop().open(viewHelp);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                break;
            case "About Notepad":
                JOptionPane.showMessageDialog(this, "JAVA SWINGS ROCKS!!!!", "About Notepad", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    void showLineCount() {

    }
    //use the text pane print method
    //copy the content of the jtextarea to textpant
    //used by print and page setup
    public void print() {
        JTextPane tempPane = new JTextPane();
        tempPane.setEditorKit(new HTMLEditorKit());
        tempPane.setText(jta.getText());

        try {
            boolean printed = tempPane.print();
            if (printed)
                JOptionPane.showMessageDialog(null, "Finished Printing");
            else
                JOptionPane.showMessageDialog(null, "Cancelled Printing");

        } catch (PrinterException e) {
            e.printStackTrace();
        }

    }
    public void goTo() {
        int lineNumber = 0;
        try {
            lineNumber = jta.getLineOfOffset(jta.getCaretPosition()) + 1;
            String tempStr = JOptionPane.showInputDialog(null, "Enter Line Number:", "" + lineNumber);
            if (tempStr == null) {
                return;
            }
            lineNumber = Integer.parseInt(tempStr);
            jta.setCaretPosition(jta.getLineStartOffset(lineNumber - 1));
        } catch (Exception e) {}
    }
    public void newFile() {
        //when creating a new file, see if user wants to save the current document
        Object[] options = {
            "Save",
            "Discard",
            "Cancel"
        };
        JOptionPane optionsPane = new JOptionPane();
        JDialog j = optionsPane.createDialog(this, "Title");
        int choice = optionsPane.showOptionDialog(null, "Would you like to save changes to the document?", "New",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "Save");
        //go to save method if user select save.

        if (choice == 0) {
            save();
        } else if (choice == 1) {
            jta.setText("");
            setTitle("Notepad" + count);
            saved = false;
            this.dispose();


        }

    }
    public void newTab() {

        /*JFrame frame2 = new JNotepad();
        frame2.setDefaultCloseOperation(HIDE_ON_CLOSE);
        frame2.setLocation(100, 100);
        */
        Thread t = new Thread(new NewTabThread());
        t.start();
        count++;
    }
    public void openFile() {
        JFileChooser jfc = new JFileChooser();
        //set the filter for the jfilechooser
        FileNameExtensionFilter txt = new FileNameExtensionFilter("Text Files", "txt");
        jfc.addChoosableFileFilter(txt);
        if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(this)) {
            File file = jfc.getSelectedFile();
            currentFile = file;
            try {
                //read the file and put it in the jtextarea
                Scanner sc = new Scanner(file);
                jta.setText("");
                while (sc.hasNext()) {
                    String str = sc.nextLine();
                    jta.append(str + "\n");
                    //System.out.println();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        saved = true;
    }
    public void save() {
        //it will run the saveAs function if its first time saving.
        if (!saved) {
            saveAs();
        }
        //otherwise it will update the file.
        else {
            try {
                BufferedWriter outFile = new BufferedWriter(new FileWriter(currentFile));
                jta.write(outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saved = true;
    }
    public void saveAs() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Save As");
        //create filter for java and txt extensions
        FileNameExtensionFilter txt = new FileNameExtensionFilter("Text Files", "txt");
        jFileChooser.addChoosableFileFilter(txt);
        if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = jFileChooser.getSelectedFile();
            //check if the filename specified have a extension
            //if none, add to it
            if (!currentFile.getAbsolutePath().endsWith(".txt"))
                currentFile = new File(currentFile.getAbsolutePath() + ".txt");
            if (currentFile.exists()) {
                //if file already exist
                //this will check if user wants to overwrite the document
                int overwrite = JOptionPane.showConfirmDialog(null, "Do you want to overwrite the existing file?",
                    "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) {
                    saved = false;
                } else {
                    writeToFile();
                    saved = true;
                }
            } else {
                writeToFile();
            }
        }
    }
    public void writeToFile() {
        //its a new document so make the file
        try {
            FileWriter fw = new FileWriter(currentFile.getAbsolutePath());
            BufferedWriter outFile = new BufferedWriter(fw);
            outFile.write(this.jta.getText());
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //make sure to only update from now on
        saved = false;
        //update the title of the frame
        setTitle(currentFile.getName() + " - Notepad" + count);
    }

    //this will update the menu item to have the redo
    //and undo to be visible or not.
    public void UndoRedo() {
        undo.setEnabled(undoRedoMan.canUndo());
        redo.setEnabled(undoRedoMan.canRedo());
    }

    public void replace() {

        re = new JDialog(this, "Replace", false);
        re.setSize(320, 150);
        re.getContentPane().setLayout(new BorderLayout());
        re.setLocationRelativeTo(this);
        //create the buttons and textfield that will be used for find and find next
        reCloseButton = new JButton("Close");
        replaceButton = new JButton("Replace");
        //findNextButton = new JButton("Find Next");
        remCheckBox = new JCheckBox("Match Case");
        refindField = new JTextField(15);
        replaceField = new JTextField(15);

        JPanel findPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        findPanel.setLayout(new FlowLayout());
        buttonPanel.setLayout(new GridLayout(1, 3));

        //add the textfield and checkbox on this panel and 
        //add it to the centered of JDialod
        //refindField.setPreferredSize(new Dimension(400,20));
        findPanel.add(new JLabel("Find :                 "));
        findPanel.add(refindField);
        findPanel.add(new JLabel("Replace :           "));
        findPanel.add(replaceField);
        re.getContentPane().add(findPanel, BorderLayout.CENTER);
        //add the find next and close buttons
        buttonPanel.add(replaceButton);
        buttonPanel.add(reCloseButton);
        buttonPanel.add(remCheckBox);
        re.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        //sensitive = false;
        re.setVisible(true);

        reCloseButton.addActionListener(new ButtonListener());

        remCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (remCheckBox.isSelected())
                    resensitive = true;
                else
                    resensitive = false;
            }
        });

        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //get the text from textarea and textfield
                String str;
                String findStr;
                String replaceStr;
                //see if case sensitive
                if (resensitive) {
                    str = jta.getText();
                    findStr = refindField.getText();
                    replaceStr = replaceField.getText();
                } else {
                    str = jta.getText().toLowerCase();
                    findStr = refindField.getText().toLowerCase();
                    replaceStr = replaceField.getText();
                }
                if (!str.equals("")) {
                    if (str.contains(findStr)) {
                        String newStr = str.replaceAll(findStr, replaceStr);
                        jta.setText(newStr);
                    } else {
                        JOptionPane.showMessageDialog(null, "Not Found");
                    }
                }
            }
        });

    }

    public void find() {
        //creates a JDialog
        jd = new JDialog(this, "Find", false);
        jd.setSize(300, 150);
        jd.getContentPane().setLayout(new BorderLayout());
        jd.setLocationRelativeTo(this);
        //create the buttons and textfield that will be used for find and find next
        closeButton = new JButton("Close");
        findButton = new JButton("Find");
        findNextButton = new JButton("Find Next");
        mCheckBox = new JCheckBox("Match Case");
        findField = new JTextField(15);
        //create a panels needed for the dialog
        JPanel findPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        findPanel.setLayout(new FlowLayout());
        buttonPanel.setLayout(new GridLayout(1, 3));
        //add the textfield and checkbox on this panel and 
        //add it to the centered of JDialod
        findPanel.add(new JLabel("Find: "));
        findPanel.add(findField);
        findPanel.add(mCheckBox);
        jd.getContentPane().add(findPanel, BorderLayout.CENTER);
        //add the find next and close buttons
        buttonPanel.add(findButton);
        buttonPanel.add(findNextButton);
        buttonPanel.add(closeButton);
        jd.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        //add actionlisteners to the checkbox and buttons



        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jd.setVisible(false);
            }
        });
        mCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (mCheckBox.isSelected())
                    sensitive = true;
                else
                    sensitive = false;
            }
        });
        findNextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //get the text from textarea and textfield
                String str = jta.getText().toLowerCase();
                String findStr = findField.getText().toLowerCase();
                //see if case sensitive
                if (sensitive) {
                    str = jta.getText();
                    findStr = findField.getText();
                }
                if (!str.equals("")) {
                    if (lastHL != null)
                        hl.removeHighlight(lastHL);
                    int idx = str.indexOf(findStr, findIdx + 1);
                    if (idx > -1) {
                        highlights(idx, findStr);
                    } else {
                        JOptionPane.showMessageDialog(null, "Not Found");
                    }
                }
            }
        });
        findButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //get the text from textarea and textfield
                String str = jta.getText().toLowerCase();
                String findStr = findField.getText().toLowerCase();
                //see if case sensitive
                if (sensitive) {
                    str = jta.getText();
                    findStr = findField.getText();
                }
                if (!str.equals("")) {
                    if (lastHL != null)
                        hl.removeHighlight(lastHL);
                    int idx = str.indexOf(findStr, 0);
                    if (idx > -1) {
                        highlights(idx, findStr);
                    } else {
                        JOptionPane.showMessageDialog(null, "Not Found");
                    }
                }
            }
        });
        jd.setVisible(true);
    }
    //get the current index then the last word of the found string and highlight it
    public void highlights(int idx, String findStr) {
        jta.setCaretPosition(idx);
        findIdx = idx;
        int startHL = jta.getCaretPosition();
        int endHL = startHL + findStr.length();
        try {
            lastHL = hl.addHighlight(startHL, endHL, painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}