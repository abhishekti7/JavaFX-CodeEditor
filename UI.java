package javaeditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.text.*;

import javax.swing.plaf.metal.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class UI implements ActionListener, MouseListener{
    JFrame fr, fr_settings;
    JToolBar tool;
    JTextArea textArea, lines;
    JTextField tf_find, tf_repl;
    JLabel label, filename, find_label, repl_label;
    JButton ok, cancel, bt_find, bt_repl, bt_close, bt_useless, tool_new, tool_open, tool_paste, tool_copy, tool_cut, tool_exit;
    JPanel panel, panel_main, panel_settings;
    JMenuBar menu;
    JComboBox combo;
    String curr_font;

    public UI(){
        try {
            // Set Look and Feel to Nimbus
            UIManager.setLookAndFeel ("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } 
        catch (Exception e){
            System.out.println("Look and Feel not set. Restoring default");
            //Set Cross Playform Look and Feel.
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        

        //Read stored font
        try{
            FileReader fread = new FileReader("config.txt");
            BufferedReader br = new BufferedReader(fread);
            curr_font = br.readLine();
        }catch(Exception e){
            curr_font = "Roboto Mono";
        }
        
        //Image Icons for Toolbar items
        final ImageIcon new_file = new ImageIcon("icons/new.png");
        final ImageIcon open_file = new ImageIcon("icons/open.png");
        final ImageIcon copy = new ImageIcon("icons/copy.png");
        final ImageIcon paste = new ImageIcon("icons/paste.png");
        final ImageIcon cut = new ImageIcon("icons/cut.png");
        final ImageIcon exit = new ImageIcon("icons/close.png");
        //Initiallsing frame
        fr = new JFrame("Text Editor");
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        //Creating toolBar
        tool = new JToolBar();
        tool.setVisible(true);

        //New file
        tool_new = new JButton(new_file);
        tool_new.setToolTipText("New");
        tool_new.addActionListener(this);
        tool_new.setRequestFocusEnabled(false);
        tool.add(tool_new);
        tool.addSeparator();

        //Open File
        tool_open = new JButton(open_file);
        tool_open.setToolTipText("Open");
        tool_open.addActionListener(this);
        tool_open.setRequestFocusEnabled(false);
        tool.add(tool_open);
        tool.addSeparator();

        //Copy
        tool_copy = new JButton(copy);
        tool_copy.setToolTipText("Copy");
        tool_copy.addActionListener(this);
        tool_copy.setRequestFocusEnabled(false);
        tool.add(tool_copy);
        tool.addSeparator();
        
        //Paste
        tool_paste = new JButton(paste);
        tool_paste.setToolTipText("Paste");
        tool_paste.addActionListener(this);
        tool_paste.setRequestFocusEnabled(false);
        tool.add(tool_paste);
        tool.addSeparator();

        //Cut
        tool_cut = new JButton(cut);
        tool_cut.setToolTipText("Cut");
        tool_cut.addActionListener(this);
        tool_cut.setRequestFocusEnabled(false);
        tool.add(tool_cut);
        tool.addSeparator();
        
        //Exit
        tool_exit = new JButton(exit);
        tool_exit.setToolTipText("Exit");
        tool_exit.addActionListener(this);
        tool.add(Box.createHorizontalGlue());
        tool_exit.setRequestFocusEnabled(false);
        tool.add(tool_exit);

        //Side Bar to dislay line numbers
        lines = new JTextArea("1  ");
        lines.setFont(new Font(Font.SANS_SERIF,Font.PLAIN, 14));
		lines.setBackground(Color.LIGHT_GRAY);
        lines.setEditable(false);
        lines.setHighlighter(null);     //disable selection in line number display
        
        //Main text area
        textArea = new JTextArea();
        textArea.setFont(new Font(curr_font.trim(),Font.PLAIN, 14));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setTabSize(4);
        //Document Listsener for text area
        textArea.getDocument().addDocumentListener(new MyDocumentListener());
        textArea.getDocument().putProperty("name", "Text Area");

        //Scroll Pane
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.getViewport().add(textArea);
		jsp.setRowHeaderView(lines);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //To set focus on textArea when GUI initialises
        fr.addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                textArea.requestFocus();
            }
        }); 
        
        //Panel for Find and Replace
        panel_main = new JPanel();
        panel_main.setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        //Find label
        find_label = new JLabel("Find");
        find_label.setHorizontalAlignment(JTextField.CENTER);
        //Text field for find
        tf_find = new JTextField();
        tf_find.setSize(10,10);
        //Button for find 
        bt_find = new JButton("Find");
        bt_find.addMouseListener(this);
        bt_find.addActionListener(this);    //Mouse Listener sets focus on replace textfield on clicked

        repl_label = new JLabel("Replace");
        repl_label.setHorizontalAlignment(JTextField.CENTER);
        tf_repl = new JTextField();
        tf_repl.setSize(10,10);
        
        bt_repl = new JButton("Replace");
        bt_repl.addActionListener(this);
        bt_close = new JButton("close");
        bt_close.addActionListener(this);
        
        //Layout fix hack
        bt_useless = new JButton();
        JButton bt_useless2 = new JButton();
        bt_useless.setVisible(false);
        bt_useless2.setVisible(false);
        //----

        panel.add(find_label);
        panel.add(tf_find);
        panel.add(bt_find);
        panel.add(repl_label);
        panel.add(tf_repl);
        panel.add(bt_repl);
        panel.add(bt_useless);
        panel.add(bt_useless2);
        panel.add(bt_close);
        panel.setVisible(false);

        label = new JLabel("Hello There!");
        label.setFont(new Font("Roboto Mono", Font.PLAIN, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel_main.add(BorderLayout.NORTH ,label);
        panel_main.add(BorderLayout.SOUTH, panel);
        filename = new JLabel("");

        menu = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenuItem item11 = new JMenuItem("New");
        JMenuItem item12 = new JMenuItem("Open");
        JMenuItem item13 = new JMenuItem("Save");
        JMenuItem item14 = new JMenuItem("Close");

        item11.addActionListener(this);
        item12.addActionListener(this);
        item13.addActionListener(this);
        item14.addActionListener(this);

        m1.add(item11);
        m1.add(item12);
        m1.add(item13);
        m1.add(item14);

        JMenu m2 = new JMenu("Edit");
        JMenuItem item21 = new JMenuItem("Cut");
        JMenuItem item22 = new JMenuItem("Copy");
        JMenuItem item23 = new JMenuItem("Paste");
        JMenuItem item24 = new JMenuItem("Find & Replace");

        item21.addActionListener(this);
        item22.addActionListener(this);
        item23.addActionListener(this);
        item24.addActionListener(this);

        m2.add(item21);
        m2.add(item22);
        m2.add(item23);
        m2.add(item24);
        
        JMenu m3 = new JMenu("Tools");
        JMenuItem item31 = new JMenuItem("Settings");
        item31.addActionListener(this);
        m3.add(item31);

        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        fr.add(BorderLayout.PAGE_START, tool);
        fr.getContentPane().add(BorderLayout.CENTER, jsp);
        fr.setJMenuBar(menu);
        fr.getContentPane().add(BorderLayout.SOUTH, panel_main);
        fr.getContentPane().add(BorderLayout.NORTH, filename);
        fr.setSize(400,400);
        fr.setVisible(true);

    }

    class MyDocumentListener implements DocumentListener{
        public String getText(){
            int caretPosition = textArea.getDocument().getLength();
            Element root = textArea.getDocument().getDefaultRootElement();
            String text = "1  " + System.getProperty("line.separator");
            for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
                text += i + System.getProperty("line.separator");
            }
            return text;
        }
        public void insertUpdate(DocumentEvent e) {
            lines.setText(getText());
            initCheck(e);

            List<String> error = new ArrayList<String>();
            error = checkSyntax("/home/zeus/zeus/Class/java/hello.java");
            if(error.isEmpty()==false){
                label.setText(error.get(0));
                System.out.println(error.get(0));
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                textArea.setCaretColor(Color.BLACK);
            }
            else{
                label.setText("");
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.WHITE);
                textArea.setCaretColor(Color.WHITE);
            }
        }

        public void removeUpdate(DocumentEvent e) {
            lines.setText(getText());
            initCheck(e);
            List<String> error = new ArrayList<String>();
            error = checkSyntax("/home/zeus/zeus/Class/java/hello.java");
            if(error.isEmpty()==false){
                label.setText(error.get(0));
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                textArea.setCaretColor(Color.BLACK);
            }
            else{
                label.setText("");
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.WHITE);
                textArea.setCaretColor(Color.WHITE);
            }
        }
        public void changedUpdate(DocumentEvent e) {
            lines.setText(getText());
            //Plain text documnets don't fire these events
        }

        public List<String> check(String file) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromStrings(Arrays.asList(file));
    
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
    
            List<String> messages = new ArrayList<String>();
            Formatter formatter = new Formatter();
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                messages.add(diagnostic.getKind() + ":\t Line [" + diagnostic.getLineNumber() + "] \t Position [" + diagnostic.getPosition() + "]\t" + diagnostic.getMessage(Locale.ROOT) + "\n");
            }
            formatter.close();
            return messages;
        }
        public void initCheck(DocumentEvent e){
            Document source = e.getDocument();
            int length = source.getLength();
            String text;
            try{
                text = source.getText(0, length);
            }catch(Exception ex){
                text="";
                ex.printStackTrace();
            }
            try { 
                // Create a file writer 
                FileWriter wr = new FileWriter("/home/zeus/zeus/Class/java/hello.java", false); 
                // Create buffered writer to write 
                BufferedWriter w = new BufferedWriter(wr); 

                // Write 
                w.write(textArea.getText()); 

                w.flush(); 
                w.close(); 
            } 
            catch (Exception ex1) { 
                ex1.printStackTrace();
            } 
        }
        public List<String> checkSyntax(String filepath){
            List<String> error = new ArrayList<String>();
            List<String> syntaxErrors = new ArrayList<String>();
            error = check(filepath);
            // for(String e : error){
            //     if(e.contains("unclosed string literal")){
            //         syntaxErrors.add(0,e);
            //     }else if(e.contains("not a statement")){
            //         syntaxErrors.add(0,e);
            //     }else if(e.contains("';' expected") || e.contains("')' expected")){
            //         syntaxErrors.add(0,e);
            //     }else if(e.contains("variable might not have been initialized")){
            //         syntaxErrors.add(0,e);
            //     }else if(e.contains("return type")){
            //         syntaxErrors.add(0,e);
            //     }
            // }
            return error;
        }
    }//End of MyDocumentListener

    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();

        if(e.getSource()==bt_close)
            panel.setVisible(false);

        if(s.equals("Cut") || e.getSource()==tool_cut)
            textArea.cut();
        else if(s.equals("Copy") || e.getSource()==tool_copy)
            textArea.copy();	
        else if(s.equals("Paste") || e.getSource()==tool_paste)
            textArea.paste();
        else if (s.equals("Open") || e.getSource()==tool_open){
            JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .java files", "java"); 
            fc.setFileFilter(restrict);
            int i = fc.showOpenDialog(null);
            
            if(i==JFileChooser.APPROVE_OPTION){
                File file = new File(fc.getSelectedFile().getAbsolutePath());
                filename.setText(fc.getSelectedFile().getAbsolutePath());
                try{
                    String s1="", s2="";
                    FileReader fr = new FileReader(file);
                    BufferedReader br=new BufferedReader(fr);
                    s1 = br.readLine();
                    s2 = s2 + s1;
                    while((s1 = br.readLine())!=null){
                        s2 = s2 + "\n" + s1;
                    }
                    br.close();
                    textArea.setText(s2);

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(fr, ex.getMessage());
                }
                
            }
        }
        else if(s.equals("Save")){
            JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            int i = fc.showOpenDialog(null);
            if(i==JFileChooser.APPROVE_OPTION){
                String filename=fc.getSelectedFile().getAbsolutePath();
                File file = new File(filename);
                try{
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw=new BufferedWriter(fw);
                    bw.write(textArea.getText());
                    bw.flush();
                    bw.close();
                    JOptionPane.showMessageDialog(fr,"File Saved Successfully!");
                }catch(Exception ex){
                    ex.printStackTrace();;
                }

            }
        }
        else if (s.equals("Find & Replace")){
            panel.setVisible(true);
            tf_find.grabFocus();
        }
        else if(s.equals("Find")){
            if(textArea.getText().equals(""))
                JOptionPane.showMessageDialog(fr, "No text in textarea");
            else{
                textArea.getHighlighter().removeAllHighlights();
                String find = tf_find.getText();       
                String text = textArea.getText();
                search(find, text, 101, 256);              //Uses Rabin-Karp method to find substring
            }   
        }
        else if (s.equals("Replace")){
            if(textArea.getText().equals(""))
                JOptionPane.showMessageDialog(fr, "No text in textarea");
            else{
                String repl = tf_repl.getText();
                String text = textArea.getText();
                text = text.replaceAll(tf_find.getText(), repl);  //beware of using replaceAll(). it uses regex as first argument
                textArea.setText(text);
                textArea.getHighlighter().removeAllHighlights();
            }
        }
        else if(s.equals("New") || e.getSource()==tool_new)
            textArea.setText("");
        else if(s.equals("Close") || e.getSource()==tool_exit){
            int opt = JOptionPane.showConfirmDialog(fr, "Do you want to exit?");
            if(opt==0){
                System.exit(0);
            }
        }
        else if(s.equals("Settings")){
            fr.setEnabled(false);
            fr_settings = new JFrame("Settings");
            fr_settings.setLayout(new GridLayout(2,1));
            fr_settings.setSize(300,200);
            fr_settings.setVisible(true);

            //in case frame is closed with default closing operation
            fr_settings.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    cancel.doClick();
                }
            });
            panel_settings = new JPanel();
            JLabel set_font = new JLabel("Select the font");
            panel_settings.add(set_font);
            combo = new JComboBox();
            combo.addActionListener(this);
            combo.setMaximumSize(new Dimension(170, 30));
            panel_settings.add(combo);

            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for(int i=0;i<fonts.length; i++){
                combo.addItem(fonts[i]);
            }
            combo.setSelectedItem(textArea.getFont().getFontName());
            JPanel set_panel = new JPanel();
            set_panel.setLayout(new FlowLayout());
            ok = new JButton("OK");
            ok.addActionListener(this);
            set_panel.add(ok);
            cancel = new JButton("Cancel");
            cancel.addActionListener(this);
            set_panel.add(cancel);
            fr_settings.add(panel_settings);
            fr_settings.add(set_panel);
            
        }
        else if (e.getSource()==ok){    
            //Getting the selected fontType value from ComboBox
            String p = combo.getSelectedItem().toString();
            try{
                FileWriter fw=new FileWriter("config.txt");
                fw.write(p+"\n");
                fw.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            //Getting size of the current font or text
            int x = textArea.getFont().getSize();
            textArea.setFont(new Font(p, Font.PLAIN, x));
            fr_settings.setVisible(false);
            fr.setEnabled(true);
        }
        else if(e.getSource()==cancel){
            fr_settings.setVisible(false);
            fr.setEnabled(true);
        }
    }

    public void mousePressed(MouseEvent e){
        if(e.getSource()==bt_find){
            tf_repl.grabFocus();
        }
    }
    public void mouseClicked(MouseEvent e){

    }
    public void mouseEntered(MouseEvent e){

    }
    public void mouseExited(MouseEvent e){

    }
    public void mouseReleased(MouseEvent e){

    }

    public void search(String pat, String txt, int q, int d) 
    { 
        int M = pat.length(); 
        int N = txt.length(); 
        int i, j; 
        int p = 0; // hash value for pattern 
        int t = 0; // hash value for txt 
        int h = 1; 
      
        // The value of h would be "pow(d, M-1)%q" 
        for (i = 0; i < M-1; i++) 
            h = (h*d)%q; 
      
        // Calculate the hash value of pattern and first 
        // window of text 
        for (i = 0; i < M; i++) 
        { 
            p = (d*p + pat.charAt(i))%q; 
            t = (d*t + txt.charAt(i))%q; 
        } 
      
        // Slide the pattern over text one by one 
        for (i = 0; i <= N - M; i++) 
        { 
      
            // Check the hash values of current window of text 
            // and pattern. If the hash values match then only 
            // check for characters on by one 
            if ( p == t ) 
            { 
                /* Check for characters one by one */
                for (j = 0; j < M; j++) 
                { 
                    if (txt.charAt(i+j) != pat.charAt(j)) 
                        break; 
                } 
      
                // if p == t and pat[0...M-1] = txt[i, i+1, ...i+M-1] 
                if (j == M) {
                    Highlighter highlighter = textArea.getHighlighter();
                    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                    try{
                        highlighter.addHighlight(i, i+M, painter);

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            } 
      
            // Calculate hash value for next window of text: Remove 
            // leading digit, add trailing digit 
            if ( i < N-M ) 
            { 
                t = (d*(t - txt.charAt(i)*h) + txt.charAt(i+M))%q; 
      
                // We might get negative value of t, converting it 
                // to positive 
                if (t < 0) 
                t = (t + q); 
            } 
        } 
    } 
}