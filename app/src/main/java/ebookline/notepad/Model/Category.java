package ebookline.notepad.Model;

public class Category
{
    int id;
    int parent;
    String title;
    String color;

    public Category() {}

    public Category(int id,
                    int parent,
                    String title,
                    String color)
    {
        this.id = id;
        this.parent = parent;
        this.title = title;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
