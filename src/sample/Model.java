package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class Model {

    public Model(){

    }
    private final String filepath = "C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\config.txt";

    public List<String> checkSyntax(TextFile currentFile){
        String file = currentFile.getFile().getAbsolutePath();
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings((Arrays.asList(file)));

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        javaCompiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();

        List<String> messages = new ArrayList<String>();
        Formatter formatter = new Formatter();
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            messages.add(diagnostic.getKind() + ":\t Line [" + diagnostic.getLineNumber() + "] \t Position [" + diagnostic.getPosition() + "]\t" + diagnostic.getMessage(Locale.ROOT) + "\n");
        }
        formatter.close();
        return messages;
    }

    public void close(File file) {
        String font="";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        alert.setHeaderText("Are you sure you want to exit?");
        alert.getDialogPane().setGraphic(new ImageView("file:C:\\Users\\Abhishek Tiwari\\IdeaProjects\\Project\\src\\sample\\icons\\warning.png"));
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get()==ButtonType.YES){
            if (file.getPath()!=""){
                try {
                    FileReader fr = new FileReader(filepath);
                    BufferedReader br = new BufferedReader(fr);
                    font = br.readLine();
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    FileWriter fw = new FileWriter(filepath);
                    fw.write(font+"\n"+file.getPath()+"\n");
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.exit(0);
        }else{
            alert.close();
        }
    }
    public IOResult<TextFile> open(File file){
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s1="", s2="";
            s1 = bufferedReader.readLine();
            s2 = s2+s1;
            while((s1 = bufferedReader.readLine())!=null){
                s2 = s2 + "\n" +s1;
            }
            return new IOResult<>(true, new TextFile(file, s2));
        } catch (IOException e) {
            e.printStackTrace();
            return new IOResult<>(false, null);
        }
    }

    public void save(File file, String text){
        try{
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Integer> search(String pat, String txt, int q, int d){
        List<Integer> positions = new ArrayList<>(100);
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
                    positions.add(Integer.valueOf(i));
                    positions.add(Integer.valueOf(i+M));
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
        return positions;
    }
    public TextFile replace(TextFile currentTextFile, String find, String repl){
        currentTextFile.setContent(currentTextFile.getContent().replaceAll(find, repl));
        return currentTextFile;
    }
}
