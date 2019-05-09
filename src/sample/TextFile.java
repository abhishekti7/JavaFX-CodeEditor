package sample;


import java.io.File;
import java.util.List;

public class TextFile {

    private File file;

    private String content;
    private List<Integer> indexes;

    public TextFile(File file, String content) {
        this.file = file;
        this.content = content;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file){
        this.file = file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }


}