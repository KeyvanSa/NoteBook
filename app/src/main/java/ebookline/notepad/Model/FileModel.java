package ebookline.notepad.Model;

public class FileModel
{
    String path;
    String name;
    String extension;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public FileModel() {}

    public FileModel(String path, String name, String extension) {
        this.path = path;
        this.name = name;
        this.extension = extension;
    }
}
