package ebookline.notepad.Model;

public class Menu {

    int id;
    String title;
    int icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Menu(int id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }
    public Menu(){}


}
