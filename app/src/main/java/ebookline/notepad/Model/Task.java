package ebookline.notepad.Model;

public class Task
{
    int id;
    int check;
    String title;
    String color;

    public Task(int id, int check, String title, String color) {
        this.id = id;
        this.check = check;
        this.title = title;
        this.color = color;
    }

    public Task(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
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
